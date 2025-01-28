package src;

import java.util.Objects;

public class Politicas {

    private final int cantidadTransiciones;     // Número total de transiciones en el sistema.
    private final Politica tipo;            // Politica implementada durante la ejecucion del programa.
    private final Segmento segmento;        // Segmento de la Etapa 3 que se prioriza.
    private final double prioridad;         // Prioridad del segmento de la Etapa 3.

    /**
     * Constructor de la clase src.Politicas.
     *
     * @param cantTransiciones Número total de transiciones en el sistema.
     */
    public Politicas(int cantTransiciones, Politica tipo, Segmento segmento, double prioridad) {
        if (cantTransiciones <= 0) {
            throw new IllegalArgumentException("El número de transiciones debe ser mayor a 0.");
        }
        this.cantidadTransiciones = cantTransiciones;
        this.tipo = Objects.requireNonNullElse(tipo, Politica.BALANCEADA);
        this.segmento = Objects.requireNonNullElse(segmento, Segmento.NINGUNO);
        this.prioridad = Math.max(0, Math.min(1, prioridad)); // Asegura que prioridad esté en [0, 1].
    }

    /**
     * Selecciona una transición para disparar en función del tipo de política.
     * BALANCEADA: Prioriza la transición habilitada con menos disparos realizados.
     * PRIORITARIA: Prioriza un segmento en la etapa 3 si cumple la relación definida por la prioridad. Si no es posible, utiliza la política BALANCEADA.
     *
     * @param transicionesHabilitadas Arreglo binario (1 = habilitada, 0 = no habilitada).
     * @param disparos                Arreglo que lleva el conteo de los disparos realizados por cada transición.
     * @return El índice de la transición seleccionada, o -1 si ninguna transición está habilitada.
     * @throws IllegalArgumentException Si los arreglos tienen tamaños inválidos.
     */
    public int seleccionarTransicion(int[] transicionesHabilitadas, double[] disparos) {

        validarArreglos(transicionesHabilitadas, disparos);

        int transicionSeleccionada = -1;   // Inicializa la transición seleccionada como no válida (-1).
        double minValue = disparos[0];     // Inicializa el valor mínimo con el número de disparos de la primera transición.

        switch (tipo){
            case BALANCEADA:
                // Recorre todas las transiciones para encontrar la habilitada con menor cantidad de disparos.
                for (int i = 1; i < cantidadTransiciones; i++) {
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
            case PRIORITARIA:
                /*
                    Política de procesamiento que prioriza un segmento en la etapa 3.
                    Este segmento incluye las transiciones T11, T12, T13 y T14.
                */
                int j=0;    // Usado en el bucle siguiente para omitir la eleccion de las transiciones prioritarias.

                if(Objects.equals(segmento, Segmento.IZQUIERDA)){
                    j=12;
                    if(transicionesHabilitadas[j]==1 && disparos[j-1]!=0){
                        double relacion = disparos[j]/disparos[j-1];
                        if (relacion<(1-prioridad)){
                            return 12;
                        }
                    }
                } else if (Objects.equals(segmento, Segmento.DERECHA)){
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
                for (int i = 1; i < cantidadTransiciones; i++) {
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

    private void validarArreglos(int[] transicionesHabilitadas, double[] disparos) {
        if (transicionesHabilitadas.length != cantidadTransiciones || disparos.length != cantidadTransiciones) {
            throw new IllegalArgumentException("Los arreglos deben tener el tamaño igual a la cantidad de transiciones.");
        }
    }
}