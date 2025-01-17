public class Politicas {

    private final int cantTransiciones; // Número total de transiciones en el sistema.

    /**
     * Constructor de la clase Politicas.
     *
     * @param cantTransiciones Número total de transiciones en el sistema.
     */
    Politicas(int cantTransiciones) {
        this.cantTransiciones = cantTransiciones;
    }

    /**
     * Selecciona una transición para disparar en función de las transiciones habilitadas y
     * el número de veces que cada transición ha sido disparada.
     *
     * @param transicionesHabilitadas Arreglo que indica qué transiciones están habilitadas
     *                                (1 = habilitada, 0 = no habilitada).
     * @param disparos                Arreglo que lleva el conteo de cuántas veces se ha disparado cada transición.
     * @return El índice de la transición seleccionada. Retorna -1 si ninguna transición está habilitada.
     */
    public int seleccionarTransicion(int[] transicionesHabilitadas, int[] disparos) {
        int transicionSeleccionada = -1;   // Inicializa la transición seleccionada como no válida (-1).
        int minValue = disparos[0];        // Inicializa el valor mínimo con el número de disparos de la primera transición.

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
        return transicionSeleccionada;
    }
}
