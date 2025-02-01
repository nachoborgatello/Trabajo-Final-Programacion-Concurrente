package src.petriNet;

import src.exception.PInvariantesException;
import src.utils.Red;

import java.util.Objects;

public class PetriNet {

    private final int[][] matrizIncidencia;     // Matriz de incidencia que define los pesos de los arcos entre plazas y transiciones.
    private final int cantidadPlazas;           // Número de plazas en la red.
    private final int cantidadTransiciones;     // Número de transiciones en la red.
    private final Plaza[] plazas;               // Arreglo de plazas de la red de Petri.
    private final Transicion[] transiciones;    // Arreglo de transiciones de la red de Petri.
    private int[] marcado;                      // Estado actual de la red, definido por los tokens en cada plaza.
    private final int[] vector;                 // Vector de disparo que indica qué transición se va a disparar.
    private final long[] timeStamp;             // Vector que guarda el tiempo en que se habilito una transicion temporal.
    private final int[] ventanaTiempos;         // Vector utilizado para el calculo de la ventana de tiempo:

    /**
     * Constructor de la clase src.petriNet.PetriNet.
     * Define la matriz de incidencia, el marcado inicial, el vector de disparo y las transiciones habilitadas.
     */
    public PetriNet(Red tipo) {
        // Define la matriz de incidencia para el calculo del nuevo marcado
        matrizIncidencia = new int[][]{
                {1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, -1, -1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1},
                {0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, -1, -1, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, -1, -1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 1, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, -1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, -1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1}
        };

        cantidadPlazas = matrizIncidencia.length;
        cantidadTransiciones = matrizIncidencia[0].length;
        marcado = new int[]{0, 1, 0, 3, 0, 1, 0, 1, 0, 2, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};
        vector = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        timeStamp = new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        ventanaTiempos = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        Red tipo1 = Objects.requireNonNullElse(tipo, Red.SIN_TIEMPOS);

        long[][] tiempos = new long[cantidadTransiciones][2];
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

        transiciones = new Transicion[cantidadTransiciones];
        for(int i=0;i<cantidadTransiciones;i++){
            transiciones[i] = new Transicion(i,tiempos[i][0],tiempos[i][1]);
        }

        plazas = new Plaza[cantidadPlazas];
        for(int i=0;i<cantidadPlazas;i++){
            plazas[i] = new Plaza(i,marcado[i]);
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
        return transiciones[transicion].estaHabilitada() == 1;
    }

    /**
     * Deshabilita una transición, indicando que no puede ser disparada.
     *
     * @param transicion Índice de la transición a deshabilitar.
     */
    private void deshabilitar(int transicion) {
        transiciones[transicion].deshabilitar();
    }

    /**
     * Verifica los invariantes de plaza en el marcado resultante.
     *
     * @return `true` si los invariantes no se cumplen, `false` si son válidos.
     */
    public boolean verificarInvariantesPlaza(int[] nuevoMarcado) {
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
        if (nuevoMarcado[1] + nuevoMarcado[2] != 1) {
            return true;
        } else if (nuevoMarcado[4] + nuevoMarcado[5] != 1) {
            return true;
        } else if (nuevoMarcado[2] + nuevoMarcado[3] + nuevoMarcado[4] + nuevoMarcado[19] != 3) {
            return true;
        } else if (nuevoMarcado[7] + nuevoMarcado[8] + nuevoMarcado[12] != 1) {
            return true;
        } else if (nuevoMarcado[10] + nuevoMarcado[11] + nuevoMarcado[13] != 1) {
            return true;
        } else if (nuevoMarcado[8] + nuevoMarcado[9] + nuevoMarcado[10] + nuevoMarcado[12] + nuevoMarcado[13] != 2) {
            return true;
        } else if (nuevoMarcado[15] + nuevoMarcado[16] + nuevoMarcado[17] != 1) {
            return true;
        } else return nuevoMarcado[19] + nuevoMarcado[20] != 1;
    }

    /**
     * Actualiza los tokens disponibles en las plazas.
     * Actualiza el conjunto de transiciones habilitadas basándose en el marcado actual de la red.
     * También actualiza los tiempos de habilitación para transiciones temporizadas.
     *
     * @param marcado Marcado actual de la red.
     */
    private void actualizarHabilitadas(int[] marcado) {
        this.marcado = marcado; // Actualiza el marcado.

        for (int i = 0; i < cantidadPlazas; i++) {
            // Actualizamos la cantidad de tokens en las distintas plazas.
            plazas[i].setTokens(marcado[i]);
        }

            int[] estabanSensibilizadas = new int[cantidadTransiciones];

        for (int i = 0; i < cantidadTransiciones; i++) {

            estabanSensibilizadas[i] = transiciones[i].estaHabilitada(); // Registra el valor anterior de las transiciones habilitadas.

            // Recalcula las transiciones habilitadas por tokens.
            transiciones[i].habilitar(); // Habilita inicialmente.
            for (int j = 0; j < cantidadPlazas; j++) {
                if (matrizIncidencia[j][i] == -1 && marcado[j] == 0) {
                    transiciones[i].deshabilitar(); // Deshabilita si no hay tokens suficientes.
                    break;
                }
            }

            if (transiciones[i].getAlfa()!=-1) {
                if (estabanSensibilizadas[i] == 0 && transiciones[i].estaHabilitada() == 1) {
                    // Transición habilitada por primera vez
                    timeStamp[i] = System.currentTimeMillis();
                } else if (estabanSensibilizadas[i] == 1 && transiciones[i].estaHabilitada() == 0) {
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
    private boolean validarTiempos(int transicion,long tiempo){
        if(tiempo>(timeStamp[transicion]+transiciones[transicion].getAlfa())){
            if((tiempo<(timeStamp[transicion]+transiciones[transicion].getBeta()))){
                // 0:Indica que la transicion esta dentro de la ventana
                ventanaTiempos[transicion]=0;
                return true;
            }
            // 2:Indica que se disparo luego de beta.
            // Deshabilita la transicion y avisa al hilo que se intento disparar fuera de la ventana de tiempos
            deshabilitar(transicion);
            ventanaTiempos[transicion]=2;
            return false;
        }
        // 1:Indica que se intento disparar antes de alfa.
        ventanaTiempos[transicion]=1;
        return false;
    }

    // Métodos getter para acceder a las variables privadas de la clase.

    public int[] getSensibilizadas() {
        int[] sensibilizadas = new int[cantidadTransiciones];
        for (int i=0;i<cantidadTransiciones;i++){
            if (transiciones[i].estaHabilitada()==1){
                sensibilizadas[i]=1;
            }else {
                sensibilizadas[i]=0;
            }
        }
        return sensibilizadas;
    }

    public int getCantidadTransiciones() {
        return cantidadTransiciones;
    }

    public long[] getTimeStamp() {
        return timeStamp;
    }

    public int[] getVentanaTiempos() {
        return ventanaTiempos;
    }

    public int[] getMarcado() {
        return marcado;
    }

    public Transicion[] getTransiciones() {
        return transiciones;
    }
}
