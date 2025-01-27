package src;

import java.util.Objects;

public class Politicas {

    private final int cantTransiciones; // Número total de transiciones en el sistema.
    private final String tipo;          // Politica implementada durante la ejecucion del programa.
                                        // BALANCEADA o PRIORITARIA
    private final String segmento;      // Segmento de la Etapa 3 que se prioriza.
                                        // DERECHA o IZQUIERDA
    private final double prioridad;

    /**
     * Constructor de la clase src.Politicas.
     *
     * @param cantTransiciones Número total de transiciones en el sistema.
     */
    public Politicas(int cantTransiciones, String tipo, String segmento, double prioridad) {
        this.cantTransiciones = cantTransiciones;
        if (!Objects.equals(tipo, "BALANCEADA") && !Objects.equals(tipo, "PRIORITARIA")){
            this.tipo = "BALANCEADA";
        } else {
            this.tipo = tipo;
        }
        if (!Objects.equals(segmento, "DERECHA") && !Objects.equals(segmento, "IZQUIERDA")){
            this.segmento = "";
        } else {
            this.segmento = segmento;
        }
        if (prioridad<0 || prioridad>1){
            this.prioridad = 0;
        }else {
            this.prioridad = prioridad;
        }
    }

    /**
     * Selecciona una transición para disparar en función del tipo, las transiciones habilitadas y
     * el número de veces que cada transición ha sido disparada.
     *
     * @param transicionesHabilitadas Arreglo que indica qué transiciones están habilitadas
     *                                (1 = habilitada, 0 = no habilitada).
     * @param disparos                Arreglo que lleva el conteo de cuántas veces se ha disparado cada transición.
     * @return El índice de la transición seleccionada. Retorna -1 si ninguna transición está habilitada.
     */
    public int seleccionarTransicion(int[] transicionesHabilitadas, double[] disparos) {
        int transicionSeleccionada = -1;   // Inicializa la transición seleccionada como no válida (-1).
        double minValue = disparos[0];     // Inicializa el valor mínimo con el número de disparos de la primera transición.

        switch (this.tipo){
            case "BALANCEADA":
                // Recorre todas las transiciones para encontrar la habilitada con menor cantidad de disparos.
                for (int i = 1; i < this.cantTransiciones; i++) {
                    // Verifica si la transición está habilitada.
                    if (transicionesHabilitadas[i] == 1) {
                        // Si la transición tiene menos disparos que el valor mínimo actual, la selecciona.
                        if (disparos[i] < minValue) {
                            minValue = disparos[i];      // Actualiza el valor mínimo.
                            transicionSeleccionada = i;  // Actualiza la transición seleccionada.
                        }
                    }
                }
                break;
            case "PRIORITARIA":
                /*
                    Política de procesamiento que prioriza un segmento en la etapa 3.
                    Este segmento incluye las transiciones T11, T12, T13 y T14.
                */
                int j=0;    // Usado en el bucle siguiente para omitir la eleccion de las transiciones prioritarias.

                if(Objects.equals(this.segmento, "IZQUIERDA")){
                    j=12;
                    if(transicionesHabilitadas[j]==1 && disparos[j-1]!=0){
                        double relacion = disparos[j]/disparos[j-1];
                        if (relacion<(1-prioridad)){
                            return 12;
                        }
                    }
                } else if (Objects.equals(this.segmento, "DERECHA")){
                    j=11;
                    if(transicionesHabilitadas[j]==1 && disparos[j+1]!=0){
                        double relacion = disparos[j]/disparos[j+1];
                        if (relacion<(1-prioridad)){
                            return 11;
                        }
                    }
                }


                // Recorre todas las transiciones para encontrar la habilitada con menor cantidad de disparos.
                // Esta etapa es igual a la Politica BALANCEADA.
                for (int i = 1; i < this.cantTransiciones; i++) {
                    // Verifica si la transición está habilitada.
                    if (transicionesHabilitadas[i] == 1 && i!=j) {
                        // Si la transición tiene menos disparos que el valor mínimo actual, la selecciona.
                        if (disparos[i] < minValue) {
                            minValue = disparos[i];      // Actualiza el valor mínimo.
                            transicionSeleccionada = i;  // Actualiza la transición seleccionada.
                        }
                    }
                }
                break;
            default:
                System.out.println("Política inválida.");
        }
        return transicionSeleccionada;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPrioridad() {
        return prioridad;
    }

    public String getSegmento() {
        return segmento;
    }
}