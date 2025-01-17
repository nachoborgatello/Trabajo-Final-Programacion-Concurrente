import java.io.IOException;
import java.util.concurrent.Semaphore;

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
        //this.politicas = new Politicas(this.petriNet.getCantTransiciones(),"BALANCEADA",0);
        this.politicas = new Politicas(this.petriNet.getCantTransiciones(),"PRIORITARIA","IZQUIERDA" ,0.8);
        //this.politicas = new Politicas(this.petriNet.getCantTransiciones(),"PRIORITARIA","DERECHA" ,0.7);
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
                        return;
                    }
                    this.colas.release(transicionADisparar); // Libera un hilo de la cola correspondiente.
                    return;
                }
                // Si no hay hilos esperando en transiciones habilitadas, termina el bucle.
                k = false;
            } else {
                // Si la transición no está habilitada, el hilo se bloquea en la cola correspondiente.
                mutex.release();
                this.colas.acquire(transicion); // El hilo entra en la cola de la transición.
                k = true; // Retoma la ejecución para intentar disparar la transición nuevamente.
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
    private int[] getTransicionesBloqueadasHabilitadas(int[] transicionesHabilitadas, int[] listaBloqueadas) {
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
}
