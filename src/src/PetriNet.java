package src;

import src.exception.PInvariantesException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PetriNet {

    private final int[][] matrizIncidencia;     // Matriz de incidencia que define los pesos de los arcos entre plazas y transiciones.
    private final int cantidadPlazas;           // Número de plazas en la red.
    private final int cantidadTransiciones;     // Número de transiciones en la red.
    private int[] marcado;                      // Estado actual de la red, definido por los tokens en cada plaza.
    private final int[] vector;                 // Vector de disparo que indica qué transición se va a disparar.
    private final int[] sensibilizadas;         // Indica qué transiciones están habilitadas para disparar por tokens (1 = habilitada, 0 = no habilitada).
    private final long[] timeStamp;             // Vector que guarda el tiempo en que se habilito una transicion temporal.
    private final long[][] tiempos;             // Matriz con los valores de Alfa (columna 0) y Beta (columna 1) para todas las transiciones
    private final int[] ventanaTiempos;         // Vector utilizado para el calculo de la ventana de tiempo:

    // Invariantes de plaza
    private final List<int[]> PInvariantes = Arrays.asList(
            new int[]{1, 2, 1}, // P1 + P2 = 1
            new int[]{4, 5, 1}, // P4 + P5 = 1
            new int[]{2, 3, 4, 19, 3}, // P2 + P3 + P4 + P19 = 3
            new int[]{7, 8, 12, 1}, // P7 + P8 + P12 = 1
            new int[]{10, 11, 13, 1}, // P10 + P11 + P13 = 1
            new int[]{8, 9, 10, 12, 13, 2}, // P8 + P9 + P10 + P12 + P13 = 2
            new int[]{15, 16, 17, 1}, // P15 + P16 + P17 = 1
            new int[]{19, 20, 1}  // P19 + P20 = 1
    );

    /**
     * Constructor de la clase src.PetriNet.
     * Define la matriz de incidencia, el marcado inicial, el vector de disparo y las transiciones habilitadas.
     */
    public PetriNet(Red tipo) {
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

        cantidadPlazas = matrizIncidencia.length;
        cantidadTransiciones = matrizIncidencia[0].length;
        marcado = new int[]{0, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        vector = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        sensibilizadas = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        timeStamp = new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        ventanaTiempos = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        Red tipo1 = Objects.requireNonNullElse(tipo, Red.SIN_TIEMPOS);

        tiempos = new long[cantidadTransiciones][2];
        for (int i = 0; i < tiempos.length; i++) {
            tiempos[i][0] = -1L;
            tiempos[i][1] = Long.MAX_VALUE;
        }

        if(Objects.equals(tipo1, Red.TEMPORAL)){
            // i indica la transicion temporal a considerar
            // [i][0] indica alfa
            // [i][1] indica beta
            tiempos[0][0] = 100L;
            tiempos[0][1] = 300L;
            tiempos[3][0] = 100L;
            tiempos[3][1] = 300L;
            tiempos[4][0] = 100L;
            tiempos[4][1] = 300L;
            tiempos[7][0] = 200L;
            tiempos[7][1] = 400L;
            tiempos[8][0] = 200L;
            tiempos[8][1] = 400L;
            tiempos[9][0] = 200L;
            tiempos[9][1] = 400L;
            tiempos[10][0] = 200L;
            tiempos[10][1] = 400L;
            tiempos[13][0] = 100L;
            tiempos[13][1] = 300L;
            tiempos[14][0] = 100L;
            tiempos[14][1] = 300L;
            tiempos[16][0] = 1L;
            tiempos[16][1] = 200L;
        }

        // Inicializa las transiciones habilitadas.
        // En caso de estar temporizadas, T0 puede considerarse temporal desde el primer disparo.
        actualizarHabilitadas(marcado);
    }

    /**
     * Dispara una transición en la red de Petri.
     * Verifica las condiciones necesarias (habilitación por tokens y ventanas de tiempo) antes de proceder con el disparo.
     *
     * @param transicion Índice de la transición a disparar.
     * @return `true` si el disparo se realizó correctamente, `false` si no fue posible.
     */
    public boolean disparar(int transicion) {

        // Verifica si la transición es válida.
        if (transicion < 0 || transicion >= cantidadTransiciones) {
            throw new IllegalArgumentException("Índice de transición inválido");
        }

        // Verifica si la transición está habilitada por cantidad de tokens en las plazas.
        if (estaHabilitada(transicion)) {

            // Verifica si la transición está dentro de la ventana de tiempos.
            long tiempo = System.currentTimeMillis();
            if (validarTiempos(transicion,tiempo)) {

                int[] nuevoMarcado = operar(transicion);

                if (Arrays.stream(nuevoMarcado).anyMatch(valor -> valor < 0)) {
                    System.out.println("Los valores del marcado no pueden ser negativos.");
                    return false;
                }

                // Verifica que no se hayan corrompido los invariantes de plaza.
                if (!verificarInvariantesPlaza(nuevoMarcado)) {
                    // Actualiza el marcado y las transiciones habilitadas según los tokens disponibles.
                    actualizarHabilitadas(nuevoMarcado);
                    return true;
                } else {
                    throw new PInvariantesException();
                }
            }
        }
        return false;
    }

    /**
     * Calcula el nuevo marcado de la red tras el disparo de una transición.
     * Aplica la ecuación fundamental de estado: m' = m + C * v.
     *
     * @param transicion Índice de la transición a disparar.
     * @return Nuevo vector de marcado resultante del disparo.
     */
    private int[] operar(int transicion){
        //Crea un vector de disparo con 1 en la posición de la transición seleccionada.
        for (int j = 0; j < cantidadTransiciones; j++) {
            vector[j] = 0;          //Pone todas las transiciones en 0.
        }
        vector[transicion] = 1;     //Marca la transición a disparar en 1.

        // Arrays auxiliares para el cálculo del nuevo marcado.
        int[] temp = new int[cantidadPlazas];
        int[] nuevoMarcado = new int[cantidadPlazas];

        // Calcula el nuevo marcado usando la ecuación fundamental de estado: m' = m + C * v.
        // temp = C * v
        for (int i = 0; i < cantidadPlazas; i++) {
            temp[i] = 0;
            for (int j = 0; j < cantidadTransiciones; j++) {
                temp[i] += matrizIncidencia[i][j] * vector[j];
            }
        }

        // Calcula el marcado resultante sumando el marcado actual con el resultado.
        // nuevoMarcado = m + temp
        for (int i = 0; i < cantidadPlazas; i++) {
            nuevoMarcado[i] = marcado[i] + temp[i];
        }
        return nuevoMarcado;
    }

    /**
     * Comprueba si una transición específica está habilitada para ser disparada.
     *
     * @param transicion Índice de la transición a verificar.
     * @return `true` si la transición está habilitada, `false` si no lo está.
     */
    public boolean estaHabilitada(int transicion) {
        return sensibilizadas[transicion] == 1;
    }

    /**
     * Verifica que los invariantes de plaza definidos en la red se mantengan intactos.
     * Los invariantes aseguran propiedades fundamentales como la conservación de tokens.
     *
     * @param marcado Vector de marcado resultante tras el disparo de una transición.
     * @return `true` si algún invariante no se cumple, `false` si todos los invariantes son válidos.
     */
    public boolean verificarInvariantesPlaza(int[] marcado) {
        int suma;
        for (int[] invariante : PInvariantes) {
            suma = 0;
            for (int i = 0; i < invariante.length - 1; i++) {
                suma += marcado[invariante[i]];
            }
            if (suma != invariante[invariante.length - 1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Actualiza el conjunto de transiciones habilitadas basándose en el marcado actual de la red.
     * También actualiza los tiempos de habilitación para transiciones temporizadas.
     *
     * @param marcado Marcado actual de la red.
     */
    public void actualizarHabilitadas(int[] marcado) {
        this.marcado = marcado; // Actualiza el marcado.

        int[] estabanSensibilizadas = new int[cantidadTransiciones];

        for (int i = 0; i < cantidadTransiciones; i++) {

            estabanSensibilizadas[i] = sensibilizadas[i]; // Registra el valor anterior de las transiciones habilitadas.

            // Recalcula las transiciones habilitadas por tokens.
            sensibilizadas[i] = 1; // Habilita inicialmente.
            for (int j = 0; j < cantidadPlazas; j++) {
                if (matrizIncidencia[j][i] == -1 && marcado[j] == 0) {
                    sensibilizadas[i] = 0; // Deshabilita si no hay tokens suficientes.
                    break;
                }
            }

            if (tiempos[i][0]!=-1) {
                if (estabanSensibilizadas[i] == 0 && sensibilizadas[i] == 1) {
                    // Transición habilitada por primera vez
                    timeStamp[i] = System.currentTimeMillis();
                } else if (estabanSensibilizadas[i] == 1 && sensibilizadas[i] == 0) {
                    // Transición deshabilitada
                    timeStamp[i] = 0L;
                } else if (i == 0) {
                    // Caso particular de T0
                    timeStamp[i] = System.currentTimeMillis();
                }
            }
        }
    }

    /**
     * Valida que una transición temporizada sea disparada dentro de su ventana de tiempo especificada (alfa, beta).
     *
     * @param transicion Índice de la transición a validar.
     * @param tiempo Tiempo actual en milisegundos.
     * @return `true` si el disparo es válido según la ventana de tiempo, `false` en caso contrario.
     */
    public boolean validarTiempos(int transicion,long tiempo){
        if(tiempo>(timeStamp[transicion]+tiempos[transicion][0])){
            if((tiempo<(timeStamp[transicion]+tiempos[transicion][1]))){
                // 0:Indica que la transicion esta dentro de la ventana
                ventanaTiempos[transicion]=0;
                return true;
            }
            // 2:Indica que se disparo luego de beta.
            ventanaTiempos[transicion]=2;
            return false;
        }
        // 1:Indica que se intento disparar antes de alfa.
        ventanaTiempos[transicion]=1;
        return false;
    }

    public int[] getTransicionesHabilitadas() {
        return sensibilizadas;
    }

    public int getCantidadTransiciones() {
        return cantidadTransiciones;
    }

    public long[] getTimeStamp() {
        return timeStamp;
    }

    public long[][] getTiempos() {
        return tiempos;
    }

    public int[] getVentanaTiempos() {
        return ventanaTiempos;
    }

    public int[] getMarcado() {
        return marcado;
    }
}
