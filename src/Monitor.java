import java.util.concurrent.Semaphore;

public class Monitor {

    private final Semaphore mutex;
    private final PetriNet petriNet;
    private final Colas colas;
    private final Politicas politicas;
    private final int[] disparos;

    public Monitor() {
        this.mutex = new Semaphore(1);
        this.petriNet = new PetriNet();
        this.colas = new Colas(this.petriNet.getNUM_TRANSICIONES());
        this.politicas = new Politicas(this.petriNet.getNUM_TRANSICIONES());
        this.disparos = new int[this.petriNet.getNUM_TRANSICIONES()];
    }

    public void dispararTransicion(int transicion) {

        // Adquiere el mutex del monitor.
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean k = true;

        while (k) {
            k = petriNet.disparar(transicion);
            if (k){

                setDisparo(transicion); //Aumenta la cuenta del disparo de esa transicion
                //System.out.print("T"+transicion);

                /*
                    Verifica las transiciones habilitadas posterior al disparo
                    Comprueba la lista de hilos que estan esperando en las colas
                 */
                int[] transicionesHabilitadas = petriNet.getTransicionesHabilitadas();
                int[] listaBloqueadas = colas.getListaBloqueadas();

                // Obtiene un listado de transiciones habilitadas y que corresponden con colas con hilos esperando
                int[] transicionesBloqueadasHabilitadas;
                transicionesBloqueadasHabilitadas = getTransicionesBloqueadasHabilitadas(transicionesHabilitadas, listaBloqueadas);

                // Puede pasar que en las transiciones habilitadas no haya hilos esperando
                boolean flag = false;
                for (int transiciones_bloqueadas_habilitadas : transicionesBloqueadasHabilitadas) {
                    if (transiciones_bloqueadas_habilitadas == 1) {
                        // Sale del loop con que al menos exista una transicion habilitada con hilos esperando
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    // La politica despierta un hilo para que intente disparar la transicion
                    int transicionADisparar = politicas.seleccionarTransicion(transicionesBloqueadasHabilitadas, this.disparos);
                    if (transicionADisparar == -1) {
                        mutex.release();
                        return;
                    }
                    this.colas.release(transicionADisparar);
                    return;
                }
                // No hay hilos esperando en transiciones habilitadas entonces sale del monitor
                k=false;
            }
            else {
                // La transicion no esta habilitada. El hilo entra a una cola
                mutex.release();
                this.colas.acquire(transicion);
                // Cuando un hilo es despertado por otro, intenta disparar la transicion nuevamente
                // k=true indica que retoma la ejecucion desde este punto y por ende no sale del loop, sino que ejecuta el disparo nuevamente
                k = true;
            }
        }
        mutex.release();
    }

    private int[] getTransicionesBloqueadasHabilitadas(int[] transicionesHabilitadas, int[] listaBloqueadas){
        // Creamos el nuevo array para guardar los resultados
        int[] resultArray = new int[transicionesHabilitadas.length];

        // Comparar ambos arrays y llenar el nuevo array
        for (int i = 0; i < transicionesHabilitadas.length; i++) {
            // Si en ambos arrays es 1, colocamos un 1 en el nuevo array; de lo contrario, un 0
            resultArray[i] = (transicionesHabilitadas[i] == 1 && listaBloqueadas[i] == 1) ? 1 : 0;
        }
        return resultArray;
    }

    private void setDisparo(int transicion){
        disparos[transicion] += 1;
    }

    public int[] getDisparos(){
        return this.disparos;
    }
}