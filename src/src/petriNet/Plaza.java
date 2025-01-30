package src.petriNet;

public class Plaza {

    private final String nombre;
    private int tokens;

    public Plaza(int numero, int tokens) {
        // P-numero
        this.nombre = "P" + numero;
        this.tokens = tokens;
    }

    // Setters
    public void setTokens(int tokens) {
        if (tokens < 0) {
            throw new IllegalArgumentException("Tokens no pueden ser negativos.");
        }
        this.tokens = tokens;
    }

    // Getters
    public int getTokens() {
        return tokens;
    }

    public String getName() {
        return nombre;
    }
}