public class PetriNet {

    private final int NUM_PLAZAS = 21;
    private final int NUM_TRANSICIONES = 17;
    private final int[][] matrizIncidencia; // Matriz de incidencia de la red de Petri
    private int[] marcado;                  // Vector de marcado de la red
    private final int[] vector;             // Vector utilizado para el calculo de la ecuacion de estado
    private final int[] transicionesHabilitadas;

    public PetriNet() {
        matrizIncidencia = new int[][]{
                // T0  T1   T2   T3   T4   T5   T6   T7   T8   T9  T10  T11  T12  T13  T14  T15  T16
                {1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // P0
                {0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // P1
                {0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // P2
                {0, -1, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1}, // P3
                {0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // P4
                {0, 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // P5
                {0, 0, 0, 1, 1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // P6
                {0, 0, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, // P7
                {0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, // P8
                {0, 0, 0, 0, 0, -1, -1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0}, // P9
                {0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0}, // P10
                {0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, // P11
                {0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0}, // P12
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0}, // P13
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, -1, -1, 0, 0, 0, 0}, // P14
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 1, 1, 0, 0}, // P15
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0}, // P16
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0}, // P17
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, -1, 0}, // P18
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1}, // P19
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1}  // P20
        };

        // Marcado inicial
        marcado = new int[]{0, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};

        // Inicializo el vector de disparo de transicion
        vector = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Inicializas las transiciones habilitadas
        // T0 es la unica que comienza estando habilitada debido a que representa un evento externo, la llegada de una imagen en este caso
        transicionesHabilitadas = new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    /*
        Arrays utilizados para el calculo del nuevo marcado
     */
    int[] result = new int[NUM_PLAZAS];
    int[] finalResult = new int[NUM_PLAZAS];

    public boolean disparar(int transicion) {

        // Verifica que la transición es válida
        if (transicion < 0 || transicion >= NUM_TRANSICIONES) {
            throw new IllegalArgumentException("Índice de transición inválido");
        }

        if(estaHabilitada(transicion)){

            // Crea el vector de disparo, pone 1 en la transicion a disparar y 0 en todas las otras
            crearVectorTransicion(transicion);

            // Resolvemos el marcado siguiente con la ecuacion fundamental de estado
            // mi+1 = mi + M * v
            for (int i = 0; i < NUM_PLAZAS; i++) {
                result[i] = 0;
                for (int j = 0; j < NUM_TRANSICIONES; j++) {
                    result[i] += matrizIncidencia[i][j] * vector[j];
                }
            }

            for (int i = 0; i < NUM_PLAZAS; i++) {
                finalResult[i] = marcado[i] + result[i];
            }

            // Si el marcado alcanzado presenta valores negativos, retornamos false ya que ese marcado no es posible
            for (int i = 0; i < NUM_PLAZAS; i++) {
                if (finalResult[i] < 0) {
                    return false;
                }
            }

            // Verificamos tras el disparo que se cumplan los invariantes de plaza
            if(verificarInvariantesPlaza(finalResult)) {
                return false;
            }

            // Actualizamos el marcado de la red y las transiciones habilitadas por tokens
            actualizarHabilitadas(finalResult);
            return true;
        }
        return false;
    }

    private void crearVectorTransicion(int i) {
        for (int j = 0; j < NUM_TRANSICIONES; j++) {
            vector[j] = 0;
        }
        vector[i] = 1;
    }

    public boolean estaHabilitada(int transicion){
        return transicionesHabilitadas[transicion] == 1;
    }

    public boolean verificarInvariantesPlaza(int[] finalResult){
    /*
        P1 + P2 = 1
        P4 + P5 = 1
        P2 + P3 + P4 + P19 = 3
        P7 + P8 + P12 = 1
        P10 + P11 + P13 = 1
        P8 + P9 + P10 + P12 + P13 = 2
        P15 + P16 + P17 = 1
        P19 + P20 = 1
    */
        if (finalResult[1] + finalResult[2] != 1){
            return true;
        } else if (finalResult[4] + finalResult[5] != 1) {
            return true;
        } else if (finalResult[2] + finalResult[3] + finalResult[4] + finalResult[19] != 3) {
            return true;
        } else if (finalResult[7] + finalResult[8] + finalResult[12] != 1) {
            return true;
        } else if (finalResult[10] + finalResult[11] + finalResult[13] != 1) {
            return true;
        } else if (finalResult[8] + finalResult[9] + finalResult[10] + finalResult[12] + finalResult[13] != 2) {
            return true;
        } else if (finalResult[15] + finalResult[16] + finalResult[17] != 1) {
            return true;
        } else if (finalResult[19] + finalResult[20] != 1) {
            return true;
        }
        return false;
    }

    public void actualizarHabilitadas(int[] finalResult){
        //  Actualizo el marcado
        this.marcado = finalResult;

        // Actualizo las transiciones habilitadas por tokens
        for (int i = 0; i < NUM_TRANSICIONES; i++) {
            for (int j = 0; j < NUM_PLAZAS; j++) {
                // A priori habilitas la transicion
                transicionesHabilitadas[i] = 1;
                // Despues revisas si los arcos de entrada a esa transicion tienen tokens disponibles en las plazas
                if (matrizIncidencia [j][i] == -1 && marcado[j] == 0){
                    transicionesHabilitadas[i] = 0;
                    break;
                }
            }
        }
    }

    public int[] getTransicionesHabilitadas() {
        return transicionesHabilitadas;
    }

    public int getNUM_TRANSICIONES() {
        return NUM_TRANSICIONES;
    }

    public int getNUM_PLAZAS() {
        return NUM_PLAZAS;
    }
}
