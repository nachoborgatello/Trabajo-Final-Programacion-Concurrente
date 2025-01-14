import java.util.Arrays;

public class PetriNet {

    private final int NUM_PLAZAS = 21;
    private final int NUM_TRANSICIONES = 17;

    private final double[][] matrizIncidencia; // Matriz de incidencia de la red de Petri
    private double[] marcado;                 // Vector de marcado de la red
    private double[] vector;

    /**
     * Constructor. Inicializa la red de Petri con su matriz de incidencia y el marcado inicial.
     *
     */
    public PetriNet() {
        matrizIncidencia = new double[][] {
                // T0  T1   T2   T3   T4   T5   T6   T7   T8   T9  T10  T11  T12  T13  T14  T15  T16
                { 1, -1,  -1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, // P0
                { 0, -1,   0,   1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, // P1
                { 0,  1,   0,  -1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, // P2
                { 0, -1,  -1,   1,   1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,  -1,   1}, // P3
                { 0,  0,   1,   0,  -1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, // P4
                { 0,  0,  -1,   0,   1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, // P5
                { 0,  0,   0,   1,   1,  -1,  -1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, // P6
                { 0,  0,   0,   0,   0,  -1,   0,   0,   0,   1,   0,   0,   0,   0,   0,   0,   0}, // P7
                { 0,  0,   0,   0,   0,   1,   0,  -1,   0,   0,   0,   0,   0,   0,   0,   0,   0}, // P8
                { 0,  0,   0,   0,   0,  -1,  -1,   0,   0,   1,   1,   0,   0,   0,   0,   0,   0}, // P9
                { 0,  0,   0,   0,   0,   0,   1,   0,  -1,   0,   0,   0,   0,   0,   0,   0,   0}, // P10
                { 0,  0,   0,   0,   0,   0,  -1,   0,   0,   0,   1,   0,   0,   0,   0,   0,   0}, // P11
                { 0,  0,   0,   0,   0,   0,   0,   1,   0,  -1,   0,   0,   0,   0,   0,   0,   0}, // P12
                { 0,  0,   0,   0,   0,   0,   0,   0,   1,   0,  -1,   0,   0,   0,   0,   0,   0}, // P13
                { 0,  0,   0,   0,   0,   0,   0,   0,   0,   1,   1,  -1,  -1,   0,   0,   0,   0}, // P14
                { 0,  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,  -1,  -1,   1,   1,   0,   0}, // P15
                { 0,  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,  -1,   0,   0,   0}, // P16
                { 0,  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   0,  -1,   0,   0}, // P17
                { 0,  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,   1,  -1,   0}, // P18
                { 0,  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1,  -1}, // P19
                { 0,  0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,  -1,   1}  // P20
        };
        // Marcado inicial
        marcado = new double[] {0, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        // Inicializo el vector de disparo de transicion
        vector = new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    /**
     * Dispara una transición si es habilitable.
     *
     * @param transicion Índice de la transición a disparar.
     * @return true si el disparo fue exitoso, false en caso contrario.
     */
    public boolean disparar(int transicion) {
        // Verificar que la transición es válida
        if (transicion < 0 || transicion >= NUM_TRANSICIONES) {
            throw new IllegalArgumentException("Índice de transición inválido");
        }

        crearVectorTransicion(transicion);

        // Verificar que la transición sea habilitable
        double[] result = new double[NUM_PLAZAS];
        for (int i = 0; i < NUM_PLAZAS; i++) {
            result[i] = 0;
            for (int j = 0; j < NUM_TRANSICIONES; j++) {
                result[i] += matrizIncidencia[i][j] * vector[j];
            }
        }

        double[] finalResult = new double[NUM_PLAZAS];
        for (int i = 0; i < NUM_PLAZAS; i++) {
            finalResult[i] = marcado[i] + result[i];
        }

        for (int i = 0; i < NUM_PLAZAS; i++) {
            if (finalResult[i]< 0) {
                return false;   //  No habilitable
            }
        }

        marcado = finalResult;  //  Actualizo el marcado
        return true;
    }

    private void crearVectorTransicion(int i) {
        for(int j=0;j<NUM_TRANSICIONES;j++){
            vector[j]=0;
        }
        vector[i] = 1;
    }

    public int getNUM_TRANSICIONES() {
        return NUM_TRANSICIONES;
    }

    public int getNUM_PLAZAS() {
        return NUM_PLAZAS;
    }
}
