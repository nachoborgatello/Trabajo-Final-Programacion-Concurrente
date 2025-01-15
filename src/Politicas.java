import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Politicas {

    private final int NUM_TRANSICIONES;

    /**
     * Constructor.
     * @param NUM_TRANSICIONES numero de transiciones de la red de Petri
     */
    Politicas(int NUM_TRANSICIONES) {
        this.NUM_TRANSICIONES = NUM_TRANSICIONES;
    }

    /**
     * Selecciona una transicion para ser disparada
     * @param transicionesHabilitadas array de transiciones habilitadas
     * @param disparos lista con la cuenta de disparos por transicion
     * @return transicion seleccionada para disparar
     */
    public int seleccionarTransicion(int[] transicionesHabilitadas, int[] disparos) {

        int transicionSeleccionada = -1;
        int minValue = disparos[0];

        for (int i = 1; i < this.NUM_TRANSICIONES; i++) {
            if (transicionesHabilitadas[i] == 1) {
                if (disparos[i] < minValue) {
                    minValue = disparos[i];
                    transicionSeleccionada = i;
                }
            }
        }
        return transicionSeleccionada;
    }
}
