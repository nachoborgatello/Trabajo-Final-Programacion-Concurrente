package src;

import src.exception.TemporalException;
import src.petriNet.PetriNet;
import src.utils.Politica;
import src.utils.Red;
import src.utils.Segmento;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Monitor {

    private boolean detener = false;

    private final Semaphore mutex;          // Semáforo binario para garantizar la exclusión mutua.
    private final PetriNet petriNet;        // Instancia de la red de Petri que se gestionará.
    private final Colas colas;              // Estructura para gestionar las colas de espera de los hilos.
    private final Politicas politicas;      // Define las políticas para seleccionar qué transición disparar.
    private final double[] cantDisparos;    // Contador para registrar cuántas veces se dispara cada transición.
    private final int MAX_INVARIANTES;
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
    public Monitor(Log log , Politica politica, Segmento segmento, double prioridad, Red tipo, int MAX_INVARIANTES) {
        mutex = new Semaphore(1);
        petriNet = new PetriNet(tipo);
        colas = new Colas(petriNet.getCantidadTransiciones());
        politicas = new Politicas(petriNet.getCantidadTransiciones(),politica,segmento,prioridad);
        cantDisparos = new double[petriNet.getCantidadTransiciones()];
        this.log = log;
        this.MAX_INVARIANTES = MAX_INVARIANTES;
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

                // Registra el disparos de la transición actual.
                try {
                    log.addTransicion(petriNet.getTransiciones()[transicion].getNombre());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Incrementa el contador de disparos para la transición actual.
                contarDisparo(transicion);

                // Cuando se dispara por ultima vez el exportador, levanta el flag que comienza a detener el programa
                if(cantDisparos[16]==MAX_INVARIANTES){
                    detenerHilos();
                }

                System.out.println(Thread.currentThread().getName() + " disparo la transicion " + transicion);

                // Obtiene las transiciones habilitadas después del disparo.
                int[] sensibilizadas = petriNet.getSensibilizadas();

                // ¿Quienes estan?
                int[] listaDeEspera = quienesEstan(sensibilizadas);

                if (!Arrays.stream(listaDeEspera).allMatch(valor -> valor == 0)) {
                    // Selecciona alguna de las transiciones habilitadas con hilos esperando y la despierta.
                    int transicionADisparar = politicas.seleccionarTransicion(listaDeEspera, cantDisparos);
                    if (transicionADisparar == -1) {
                        /*
                            Si no hay ninguna transición distinta de 0 para dispararse, libera el mutex y finaliza.
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
                } if (!petriNet.getTransiciones()[transicion].esInmediata()){
                    if (petriNet.getVentanaTiempos()[transicion]==1) {
                        // 2. El tiempo alfa aún no se ha cumplido, por lo que el hilo debe esperar un tiempo igual a alfa - ahora antes de volver a intentar disparar.
                        long ahora = System.currentTimeMillis();
                        long tiempo = (petriNet.getTimeStamp()[transicion]+petriNet.getTransiciones()[transicion].getAlfa())-ahora;
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
                        // ¿Puedo liberar el mutex y continuar?
                        mutex.release(); // Libera el semáforo al salir del monitor.
                        throw new TemporalException();
                    }
                }
            }
        }
        mutex.release(); // Libera el semáforo al salir del monitor.
    }

    /**
     * Devuelve una lista con las transiciones habilitadas y bloqueadas por hilos esperando.
     *
     * @param sensibilizadas Array de transiciones habilitadas.
     * @return Array de transiciones habilitadas con hilos en espera.
     */
    public int[] quienesEstan(int[] sensibilizadas) {
        int[] temp = new int[sensibilizadas.length];
        for (int i = 0; i < sensibilizadas.length; i++) {
            temp[i] = (sensibilizadas[i] == 1 && colas.getColas().get(i).getQueueLength()!=0) ? 1 : 0;
        }
        return temp;
    }

    /**
     * Incrementa el contador de disparos de una transición específica.
     */
    private void contarDisparo(int transicion) {
        cantDisparos[transicion] += 1;
    }

    /**
     * Retorna el número total de disparos de cada transición.
     */
    public double[] getDisparos() {
        return cantDisparos;
    }

    /**
     * Indica si el monitor debe detenerse.
     */
    public boolean debeDetener() {
        return detener;
    }

    /**
     * Detiene la ejecución de los hilos en el monitor.
     */
    private void detenerHilos() {
        detener = true;
    }

    /**
     * Retorna el semáforo utilizado para exclusión mutua.
     */
    public Semaphore getMutex() {
        return mutex;
    }

    /**
     * Retorna la estructura de colas utilizada para gestionar los hilos en espera.
     */
    public Colas getColas() {
        return colas;
    }

    /**
     * Retorna la instancia de la red de Petri gestionada por el monitor.
     */
    public PetriNet getPetriNet() {
        return petriNet;
    }
}
