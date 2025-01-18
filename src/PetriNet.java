public class PetriNet {

    private final int[][] matrizIncidencia;     // Matriz de incidencia que define los pesos de los arcos entre plazas y transiciones.
    private final int cantPlazas;               // Número de plazas en la red.
    private final int cantTransiciones;         // Número de transiciones en la red.
    private int[] marcado;                      // Estado actual de la red, definido por los tokens en cada plaza.
    private final int[] vector;                 // Vector de disparo que indica qué transición se va a disparar.
    private final int[] transicionesHabilitadas;// Indica qué transiciones están habilitadas para disparar (1 = habilitada, 0 = no habilitada).

    /**
     * Constructor de la clase PetriNet.
     * Define la matriz de incidencia, el marcado inicial, el vector de disparo y las transiciones habilitadas.
     */
    public PetriNet() {
        // Define la matriz de incidencia.
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

        // Define la cantidad de plazas y transiciones de la red
        this.cantPlazas = this.matrizIncidencia.length;
        this.cantTransiciones = this.matrizIncidencia[0].length;

        // Define el marcado inicial de la red.
        marcado = new int[]{0, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};

        // Inicializa el vector de disparo (sin transiciones disparadas inicialmente).
        vector = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Inicializa las transiciones habilitadas. Solo T0 comienza habilitada.
        transicionesHabilitadas = new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    /**
     * Metodo que intenta disparar una transición en la red.
     *
     * @param transicion Índice de la transición a disparar.
     * @return `true` si el disparo fue exitoso, `false` si no se pudo disparar.
     */
    public boolean disparar(int transicion) {
        // Verifica si la transición es válida.
        if (transicion < 0 || transicion >= cantTransiciones) {
            throw new IllegalArgumentException("Índice de transición inválido");
        }

        // Verifica si la transición está habilitada.
        if (estaHabilitada(transicion)) {
            // Crea el vector de disparo para la transición solicitada.
            crearVectorTransicion(transicion);

            // Arrays auxiliares para el cálculo del nuevo marcado.
            int[] temp = new int[cantPlazas];
            int[] nuevoMarcado = new int[cantPlazas];

            // Calcula el nuevo marcado usando la ecuación fundamental de estado: m' = m + C * v.
            // temp = C * v
            for (int i = 0; i < cantPlazas; i++) {
                temp[i] = 0;
                for (int j = 0; j < cantTransiciones; j++) {
                    temp[i] += matrizIncidencia[i][j] * vector[j];
                }
            }

            // Calcula el marcado resultante sumando el marcado actual con el resultado.
            // nuevoMarcado = m + temp
            for (int i = 0; i < cantPlazas; i++) {
                nuevoMarcado[i] = marcado[i] + temp[i];
            }

            // Verifica que el marcado resultante no tenga valores negativos (inviables).
            for (int i = 0; i < cantPlazas; i++) {
                if (nuevoMarcado[i] < 0) {
                    return false;
                }
            }

            // Verifica los invariantes de plaza.
            if (verificarInvariantesPlaza(nuevoMarcado)) {
                System.out.println("Se corrompieron los invariantes de plaza.");
                return false;
            }

            // Actualiza el marcado y las transiciones habilitadas según los tokens disponibles.
            actualizarHabilitadas(nuevoMarcado);
            return true;
        }
        return false;
    }

    /**
     * Crea un vector de disparo con 1 en la posición de la transición seleccionada.
     *
     * @param transicion Índice de la transición a disparar.
     */
    private void crearVectorTransicion(int transicion) {
        for (int j = 0; j < cantTransiciones; j++) {
            vector[j] = 0; // Reinicia el vector.
        }
        vector[transicion] = 1; // Marca la transición a disparar.
    }

    /**
     * Verifica si una transición está habilitada para disparar.
     *
     * @param transicion Índice de la transición.
     * @return `true` si está habilitada, `false` si no.
     */
    public boolean estaHabilitada(int transicion) {
        return transicionesHabilitadas[transicion] == 1;
    }

    /**
     * Verifica los invariantes de plaza en el marcado resultante.
     *
     * @param marcado Marcado resultante tras el disparo.
     * @return `true` si los invariantes no se cumplen, `false` si son válidos.
     */
    public boolean verificarInvariantesPlaza(int[] marcado) {
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
        if (marcado[1] + marcado[2] != 1) {
            return true;
        } else if (marcado[4] + marcado[5] != 1) {
            return true;
        } else if (marcado[2] + marcado[3] + marcado[4] + marcado[19] != 3) {
            return true;
        } else if (marcado[7] + marcado[8] + marcado[12] != 1) {
            return true;
        } else if (marcado[10] + marcado[11] + marcado[13] != 1) {
            return true;
        } else if (marcado[8] + marcado[9] + marcado[10] + marcado[12] + marcado[13] != 2) {
            return true;
        } else if (marcado[15] + marcado[16] + marcado[17] != 1) {
            return true;
        } else return marcado[19] + marcado[20] != 1;
    }

    /**
     * Actualiza las transiciones habilitadas según los tokens disponibles en las plazas.
     *
     * @param marcado Marcado actual de la red.
     */
    public void actualizarHabilitadas(int[] marcado) {
        this.marcado = marcado; // Actualiza el marcado.

        // Recalcula las transiciones habilitadas.
        for (int i = 0; i < cantTransiciones; i++) {
            transicionesHabilitadas[i] = 1; // Habilita inicialmente.
            for (int j = 0; j < cantPlazas; j++) {
                if (matrizIncidencia[j][i] == -1 && marcado[j] == 0) {
                    transicionesHabilitadas[i] = 0; // Deshabilita si no hay tokens suficientes.
                    break;
                }
            }
        }
    }

    public int[] getTransicionesHabilitadas() {
        return transicionesHabilitadas;
    }

    public int getCantTransiciones() {
        return cantTransiciones;
    }
}
