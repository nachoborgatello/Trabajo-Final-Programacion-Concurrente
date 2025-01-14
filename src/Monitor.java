import java.util.concurrent.Semaphore;

public class Monitor {

    private final Semaphore mutex;
    private final PetriNet petriNet;
    private final int[] disparos;

    /**
     * Constructor. Privado para garantizar singleton.
     *
     */
    public Monitor() {
        this.mutex = new Semaphore(1);
        this.petriNet = new PetriNet();
        this.disparos = new int[this.petriNet.getNUM_TRANSICIONES()];   //Disparos de las transiciones
    }

    /**
     * Toma la decision sobre que transición disparar y que hilo debe realizar su tarea.
     * Implementa una politica Signal and Continue.
     * @param transicion Transicion que un hilo solicita disparar.
     */
    public void dispararTransicion(int transicion) {
        // Adquiere el mutex del monitor.
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            System.err.println(e);
            System.exit(1);
        }

        boolean k = true;

        while (k) {
            // Intenta disparar una transición.
            k = petriNet.disparar(transicion);
            if (k){
                System.out.println("Se pudo disparar la transicion " + transicion);
                //Aumentamos la cuenta de disparos de la transicion
                setDisparo(transicion);
                k=false;
            }
            else {
                System.out.println("No se pudo disparar la transicion " + transicion);
            }
        }
        mutex.release();
    }

    private void setDisparo(int transicion){
        disparos[transicion] += 1;
    }

    public int[] getDisparos(){
        return this.disparos;
    }
}

