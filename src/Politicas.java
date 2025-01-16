import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Politicas {

    private final int NUM_TRANSICIONES;

    Politicas(int NUM_TRANSICIONES) {
        this.NUM_TRANSICIONES = NUM_TRANSICIONES;
    }

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
