package src;

public class PetriNet {

    private final int[][] matrizIncidencia;                 // Matriz de incidencia que define los pesos de los arcos entre plazas y transiciones.
    private final int cantPlazas;                           // Número de plazas en la red.
    private final int cantTransiciones;                     // Número de transiciones en la red.
    private int[] marcado;                                  // Estado actual de la red, definido por los tokens en cada plaza.
    private final int[] vector;                             // Vector de disparo que indica qué transición se va a disparar.
    private final int[] transicionesHabilitadas;            // Indica qué transiciones están habilitadas para disparar por tokens (1 = habilitada, 0 = no habilitada).
    private final int[] transicionesHabilitadasAnteriores;  // Indica qué transiciones estaban habilitadas anteriormente. Auxiliar en el calculo del timestamp.
    private final long[] vectorTiempos;                     // Vector que guarda el tiempo en que se habilito una transicion temporal.
    private final long[][] matrizTiempos;                   // Matriz con los valores de Alfa (columna 0) y Beta (columna 1) para todas las transiciones
    private final int[] flagsVentanaTiempo;                 // Vector utilizado para el calculo de la ventana de tiempo:
                                                            // 0: Indica que la transicion esta dentro de la ventana, 1: Indica que se intento disparar antes de Alfa, 2: Indica que se disparo luego de Beta.

    /**
     * Constructor de la clase src.PetriNet.
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

        // Inicializa las transiciones habilitadas.
        transicionesHabilitadas = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        transicionesHabilitadasAnteriores = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        //Las transiciones {T0, T3, T4, T7, T8, T9, T10, T13, T14, T16} son transiciones temporales.

/*        matrizTiempos = new long[][] {
                // alfa, beta
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},*/

        matrizTiempos = new long[][] {
                // alfa, beta
                {100L,300L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {100L,300L},
                {100L,300L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {100L,300L},
                {100L,300L},
                {100L,300L},
                {100L,300L},
                {-1,9223372036854775807L},
                {-1,9223372036854775807L},
                {100L,300L},
                {100L,300L},
                {-1,9223372036854775807L},
                {100L,300L},
        };

        vectorTiempos = new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        flagsVentanaTiempo = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        // Inicializa las transiciones habilitadas.
        // De esta manera T0 puede considerarse temporal en el primer disparo.
        actualizarHabilitadas(this.marcado);
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

        // Verifica si la transición está habilitada por cantidad de tokens en las plazas.
        if (estaHabilitada(transicion)) {

            // Verifica si la transición está dentro de la ventana de tiempos.
            long tiempo = System.currentTimeMillis();
            if (validarTiempos(transicion,tiempo)) {

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
                    throw new RuntimeException("Se corrompieron los invariantes de plaza.");
                }

                // Actualiza el marcado y las transiciones habilitadas según los tokens disponibles.
                actualizarHabilitadas(nuevoMarcado);

                return true;
            }
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

        for (int i = 0; i < cantTransiciones; i++) {

            // Registra el valor anterior de las transiciones habilitadas.
            transicionesHabilitadasAnteriores[i] = transicionesHabilitadas[i];

            // Recalcula las transiciones habilitadas por tokens.
            transicionesHabilitadas[i] = 1; // Habilita inicialmente.
            for (int j = 0; j < cantPlazas; j++) {
                if (matrizIncidencia[j][i] == -1 && marcado[j] == 0) {
                    transicionesHabilitadas[i] = 0; // Deshabilita si no hay tokens suficientes.
                    break;
                }
            }

            if (matrizTiempos[i][0]!=-1) {
                if (transicionesHabilitadasAnteriores[i] == 0 && transicionesHabilitadas[i] == 1) {
                    // Transición habilitada por primera vez
                    vectorTiempos[i] = System.currentTimeMillis();
                } else if (transicionesHabilitadasAnteriores[i] == 1 && transicionesHabilitadas[i] == 0) {
                    // Transición deshabilitada
                    vectorTiempos[i] = 0L;
                } else if (i == 0) {
                    // Caso particular de T0
                    vectorTiempos[i] = System.currentTimeMillis();
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

    public long[] getVectorTiempos() {
        return vectorTiempos;
    }

    public long[][] getMatrizTiempos() {
        return matrizTiempos;
    }

    public boolean validarTiempos(int transicion,long tiempo){
        if(tiempo>(this.vectorTiempos[transicion]+this.matrizTiempos[transicion][0])){
            if((tiempo<(this.vectorTiempos[transicion]+this.matrizTiempos[transicion][1]))){
                flagsVentanaTiempo[transicion]=0;
                return true;
            }
            flagsVentanaTiempo[transicion]=2;
            return false;
        }
        flagsVentanaTiempo[transicion]=1;
        return false;
    }

    public int[] getFlagsVentanaTiempo() {
        return flagsVentanaTiempo;
    }
}
