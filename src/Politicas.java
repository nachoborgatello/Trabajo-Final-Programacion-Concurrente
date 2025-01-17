public class Politicas {

    private final int cantTransiciones; // Número total de transiciones en el sistema.
    private final String tipo;          // Politica implementada durante la ejecucion del programa.
                                        // BALANCEADA o PRIORITARIA
    private final String segmento;      // DERECHA O IZQUIERDA
    private final double prioridad;

    /**
     * Constructor de la clase Politicas.
     *
     * @param cantTransiciones Número total de transiciones en el sistema.
     */
    Politicas(int cantTransiciones, String tipo, String segmento, double prioridad) {
        this.cantTransiciones = cantTransiciones;
        this.tipo = tipo;
        this.segmento = segmento;
        this.prioridad = prioridad;
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

        if(this.tipo=="BALANCEADA"){
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
        } else if (this.tipo=="PRIORITARIA"){
            /*
                Política de procesamiento que prioriza el segmento izquierdo en la etapa 3.
                Este segmento incluye las transiciones T11 y T13
             */
            int j=0;
            if(this.segmento == "IZQUIERDA"){
                j=12;
                if(transicionesHabilitadas[12]==1 && disparos[11]!=0){
                    double relacion = disparos[12]/disparos[11];
                    if (relacion<(1-prioridad)){
                        return 12;
                    }
                }
            } else if (this.segmento == "DERECHA"){
                j=11;
                if(transicionesHabilitadas[11]==1 && disparos[12]!=0){
                    double relacion = disparos[11]/disparos[12];
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
        }
        return transicionSeleccionada;
    }
}
