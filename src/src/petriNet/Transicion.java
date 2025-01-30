package src.petriNet;

public class Transicion {
    private final String nombre;
    private final long alfa;
    private final long beta;
    private final boolean esInmediata;
    private int habilitada;

    public Transicion(int numero, long alfa, long beta) {
        // T-numero
        this.nombre = "T" + numero;
        this.alfa = alfa;
        this.beta = beta;
        this.esInmediata = alfa == 0;
        this.habilitada = 0;
    }

    public boolean esInmediata() {
        return esInmediata;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public long getAlfa() {
        return alfa;
    }

    public long getBeta() {
        return beta;
    }

    public int estaHabilitada() {
        return habilitada;
    }

    public void habilitar() {
        this.habilitada=1;
    }

    public void deshabilitar() {
        this.habilitada=0;
    }
}
