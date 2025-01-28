package src;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Monitor {

    private volatile boolean detener = false;

    private final Semaphore mutex;          // Semáforo binario para garantizar la exclusión mutua.
    private final PetriNet petriNet;        // Instancia de la red de Petri que se gestionará.
    private final Colas colas;              // Estructura para gestionar las colas de espera de los hilos.
    private final Politicas politicas;      // Define las políticas para seleccionar qué transición disparar.
    private final double[] cantDisparos;    // Contador para registrar cuántas veces se dispara cada transición.
    private final Log log;

    /**
     * Constructor del Monitor.
     * Inicializa las estructuras necesarias para gestionar la concurrencia y la sincronización
     * en la red de Petri, incluyendo semáforos, políticas, contadores y registros.
     *
     * @param log Registro de transiciones y otros eventos en la red.
     * @param politica Política de selección para disparos en caso de múltiples transiciones habilitadas.
     * @param segmento Segmento específico para evaluar prioridades entre transiciones.
     * @param prioridad Valor que establece el nivel de prioridad.
     * @param tipo Tipo de red de Petri a gestionar.
     */
    public Monitor(Log log, Politica politica, Segmento segmento, double prioridad, Red tipo) {
        mutex = new Semaphore(1);
        petriNet = new PetriNet(tipo);
        colas = new Colas(petriNet.getCantidadTransiciones());
        politicas = new Politicas(petriNet.getCantidadTransiciones(),politica,segmento,prioridad);
        cantDisparos = new double[petriNet.getCantidadTransiciones()];
        this.log = log;
    }

    /**
     * Dispara una transición en la red de Petri de forma concurrente.
     * Gestiona las colas, sincronización mediante semáforos y verificaciones de condiciones para el disparo.
     *
     * @param transicion Índice de la transición a disparar.
     */
    public void dispararTransicion(int transicion) {

        // Intenta adquirir el semáforo para entrar al monitor.
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        boolean k = true;

        while (k) {
            /*
                Inicio de logica para detener el programa
             */
            if (debeDetener()){
                int index = -1;
                for (int i=0;i<colas.getColas().size();i++){
                    if(colas.getColas().get(i).hasQueuedThreads()){
                        index=i;
                        break;
                    }
                }
                if (index!=-1){
                    // Hay hilos esperando en las colas de condicion
                    colas.liberar(index);
                } else {
                    mutex.release();
                }
                return;
            }
            /*
                Fin de logica para detener el programa
            */

            k = petriNet.disparar(transicion);
            if (k) {

                // Incrementa el contador de disparos para la transición actual.
                contarDisparo(transicion);

                // Registra el disparos de la transición actual.
                try {
                    log.addTransicion(transicion);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.printf("Se disparo la transicion %d\n",transicion);

                // Obtiene las transiciones habilitadas después del disparo.
                int[] sensibilizadas = petriNet.getTransicionesHabilitadas();

                // Obtiene la lista de transiciones bloqueadas (colas con hilos esperando).
                int[] listaBloqueadas = colas.getListaBloqueadas();

                // ¿Quienes estan?
                int[] listaDeEspera = quienesEstan(sensibilizadas, listaBloqueadas);

                if (!Arrays.stream(listaDeEspera).allMatch(valor -> valor == 0)) {
                    // Selecciona alguna de las transiciones habilitadas con hilos esperando y la despierta.
                    int transicionADisparar = politicas.seleccionarTransicion(listaDeEspera, cantDisparos);
                    if (transicionADisparar == -1) {
                        /*
                            Si no hay ninguna transición seleccionada, libera el mutex y finaliza.
                            En el caso PRIORITARIA, lo libera si la relacion (prioridad,1-prioridad) no se cumple y el unico en la cola es la transicion involucrada en la relacion
                         */
                        mutex.release();
                    } else {
                        colas.liberar(transicionADisparar); // Libera un hilo de la cola correspondiente.
                    }
                    return;
                } else {
                    // Si no hay hilos esperando en transiciones habilitadas, termina el bucle.
                    k = false;
                }
            } else {
                /*
                    Si la transición no fue disparada, puede deberse a las siguientes razones:
                        1. La transición no cuenta con los tokens necesarios.
                        2. El tiempo alfa aún no se ha cumplido, por lo que el hilo debe esperar un tiempo igual a alfa - ahora antes de volver a intentar disparar.
                        3. El tiempo beta ha sido superado, lo que impide volver a disparar la transición.
                 */
                if(!petriNet.estaHabilitada(transicion)){
                    // 1. La transición no cuenta con los tokens necesarios. El hilo se bloquea en la cola correspondiente.
                    mutex.release();
                    colas.esperar(transicion); // El hilo entra en la cola de la transición.
                    k = true;
                } else if (petriNet.getVentanaTiempos()[transicion]==1) {
                    // 2. El tiempo alfa aún no se ha cumplido, por lo que el hilo debe esperar un tiempo igual a alfa - ahora antes de volver a intentar disparar.
                    long ahora = System.currentTimeMillis();
                    long tiempo = (petriNet.getTimeStamp()[transicion]+petriNet.getTiempos()[transicion][0])-ahora;
                    mutex.release();
                    try {
                        TimeUnit.MILLISECONDS.sleep(tiempo);
                        mutex.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    k=true;
                } else if (petriNet.getVentanaTiempos()[transicion]==2) {
                    // 3. El tiempo beta ha sido superado, lo que impide volver a disparar la transición.
                    System.out.printf("Se intento disparar la transicion %d\n",transicion);
                    long ahora = System.currentTimeMillis();
                    System.out.println(ahora-petriNet.getTimeStamp()[transicion]);
                    throw new RuntimeException("Fuera de la ventana de tiempos");
                }
            }
        }
        mutex.release(); // Libera el semáforo al salir del monitor.
    }

    /**
     * Devuelve una lista con las transiciones habilitadas y bloqueadas por hilos esperando.
     *
     * @param sensibilizadas Array de transiciones habilitadas.
     * @param listaBloqueadas Array de transiciones con hilos bloqueados.
     * @return Array de transiciones habilitadas con hilos en espera.
     */
    public int[] quienesEstan(int[] sensibilizadas, int[] listaBloqueadas) {
        int[] temp = new int[sensibilizadas.length];
        for (int i = 0; i < sensibilizadas.length; i++) {
            temp[i] = (sensibilizadas[i] == 1 && listaBloqueadas[i] == 1) ? 1 : 0;
        }
        return temp;
    }

    /**
     * Incrementa el contador de disparos para una transición específica.
     *
     * @param transicion Índice de la transición cuyo contador se incrementará.
     */
    private void contarDisparo(int transicion) {
        cantDisparos[transicion] += 1;
    }

    /**
     * Devuelve el número acumulado de disparos para todas las transiciones.
     *
     * @return Array con el contador de disparos de cada transición.
     */
    public double[] getDisparos() {
        return cantDisparos;
    }

    public boolean debeDetener() {
        return detener;
    }

    public void detenerHilos() {
        detener = true;
    }

    public Semaphore getMutex() {
        return mutex;
    }

    public PetriNet getPetriNet() {
        return petriNet;
    }
}
