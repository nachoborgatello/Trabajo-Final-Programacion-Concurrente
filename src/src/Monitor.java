package src;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Monitor {

    private final Semaphore mutex;          // Semáforo binario para garantizar la exclusión mutua.
    private final PetriNet petriNet;        // Instancia de la red de Petri que se gestionará.
    private final Colas colas;              // Estructura para gestionar las colas de espera de los hilos.
    private final Politicas politicas;      // Define las políticas para seleccionar qué transición disparar.
    private final double[] cantDisparos;    // Contador para registrar cuántas veces se dispara cada transición.
    private final Log log;

    /**
     * Constructor del Monitor.
     * Inicializa los componentes necesarios para gestionar la red de Petri.
     */
    public Monitor(Log log) {
        this.mutex = new Semaphore(1);
        this.petriNet = new PetriNet();
        this.colas = new Colas(this.petriNet.getCantTransiciones());
        this.politicas = new Politicas(this.petriNet.getCantTransiciones(),"BALANCEADA","",0);
        //this.politicas = new src.Politicas(this.petriNet.getCantTransiciones(),"PRIORITARIA","DERECHA",0.8);
        this.cantDisparos = new double[this.petriNet.getCantTransiciones()];
        this.log = log;
    }

    /**
     * Metodo para disparar una transición en la red de Petri.
     * Gestiona la sincronización, verifica las transiciones habilitadas,
     * y despierta hilos bloqueados según sea necesario.
     *
     * @param transicion Índice de la transición que se desea disparar.
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
            k = petriNet.disparar(transicion);
            if (k) {

                // Incrementa el contador de disparos para la transición actual.
                setDisparo(transicion);

                // Registra el disparos de la transición actual.
                try {
                    log.addTransicion(transicion);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.out.printf("Se disparo la transicion %d\n",transicion);

                // Obtiene las transiciones habilitadas después del disparo.
                int[] transicionesHabilitadas = petriNet.getTransicionesHabilitadas();

                // Obtiene la lista de transiciones bloqueadas (colas con hilos esperando).
                int[] listaBloqueadas = colas.getListaBloqueadas();

                // Verifica cuáles transiciones habilitadas tienen hilos esperando.
                int[] transicionesBloqueadasHabilitadas = getTransicionesBloqueadasHabilitadas(transicionesHabilitadas, listaBloqueadas);

                // Comprueba si hay hilos esperando en transiciones habilitadas.
                boolean flag = false;
                for (int transiciones_bloqueadas_habilitadas : transicionesBloqueadasHabilitadas) {
                    if (transiciones_bloqueadas_habilitadas == 1) {
                        flag = true; // Hay al menos una transición habilitada con hilos esperando.
                        break;
                    }
                }

                if (flag) {
                    // Selecciona una transición habilitada con hilos esperando y la despierta.
                    int transicionADisparar = politicas.seleccionarTransicion(transicionesBloqueadasHabilitadas, this.cantDisparos);
                    if (transicionADisparar == -1) {
                        // Si no hay ninguna transición seleccionada, libera el mutex y finaliza.
                        // En el caso PRIORITARIO
                        // Lo libera si la relacion 80 20 no se cumple y el unico en la cola es el T12
                        mutex.release();
                    } else {
                        this.colas.release(transicionADisparar); // Libera un hilo de la cola correspondiente.
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
                if(!this.petriNet.estaHabilitada(transicion)){
                    // 1. La transición no cuenta con los tokens necesarios. El hilo se bloquea en la cola correspondiente.
                    mutex.release();
                    this.colas.acquire(transicion); // El hilo entra en la cola de la transición.
                    k = true;
                } else if (this.petriNet.getFlagsVentanaTiempo()[transicion]==1) {
                    // 2. El tiempo alfa aún no se ha cumplido, por lo que el hilo debe esperar un tiempo igual a alfa - ahora antes de volver a intentar disparar.
                    long ahora = System.currentTimeMillis();
                    long tiempo = (this.petriNet.getVectorTiempos()[transicion]+this.petriNet.getMatrizTiempos()[transicion][0])-ahora;
                    mutex.release();
                    try {
                        TimeUnit.MILLISECONDS.sleep(tiempo);
                        mutex.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    k=true;
                } else if (this.petriNet.getFlagsVentanaTiempo()[transicion]==2) {
                    // 3. El tiempo beta ha sido superado, lo que impide volver a disparar la transición.
                    System.out.printf("Se intento disparar la transicion %d\n",transicion);
                    long ahora = System.currentTimeMillis();
                    System.out.println(ahora-this.petriNet.getVectorTiempos()[transicion]);
                    throw new RuntimeException("Fuera de la ventana de tiempos");
                }
            }
        }
        mutex.release(); // Libera el semáforo al salir del monitor.
    }

    /**
     * Metodo que obtiene las transiciones habilitadas que tienen hilos bloqueados esperando.
     *
     * @param transicionesHabilitadas Lista de transiciones habilitadas en la red.
     * @param listaBloqueadas Lista de transiciones con hilos bloqueados.
     * @return Array con las transiciones habilitadas que tienen hilos esperando.
     */
    public int[] getTransicionesBloqueadasHabilitadas(int[] transicionesHabilitadas, int[] listaBloqueadas) {
        int[] temp = new int[transicionesHabilitadas.length];
        for (int i = 0; i < transicionesHabilitadas.length; i++) {
            temp[i] = (transicionesHabilitadas[i] == 1 && listaBloqueadas[i] == 1) ? 1 : 0;
        }
        return temp;
    }

    /**
     * Incrementa el contador de disparos para una transición específica.
     *
     * @param transicion Índice de la transición.
     */
    private void setDisparo(int transicion) {
        cantDisparos[transicion] += 1;
    }

    /**
     * Devuelve el número de veces que se ha disparado cada transición.
     *
     * @return Array con los contadores de disparos.
     */
    public double[] getDisparos() {
        return this.cantDisparos;
    }

    public Semaphore getMutex() {
        return mutex;
    }

    public PetriNet getPetriNet() {
        return petriNet;
    }

    public Colas getColas() {
        return colas;
    }

    public Politicas getPoliticas() {
        return politicas;
    }
}
