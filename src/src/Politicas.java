package src;

import src.utils.Politica;
import src.utils.Segmento;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Politicas {

    private final int cantidadTransiciones;     // Número total de transiciones en el sistema.
    private final Politica tipo;            // Politica implementada durante la ejecucion del programa.
    private final Segmento segmento;        // Segmento de la Etapa 3 que se prioriza.
    private final double prioridad;         // Prioridad del segmento de la Etapa 3.
    private final int transicion;

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
        int transicion1 = 0;
        if(Objects.equals(tipo, Politica.PRIORITARIA)){
            if (Objects.equals(segmento, Segmento.IZQUIERDA)){
                transicion1 =12;
            }else {
                transicion1 =11;
            }
        }
        transicion = transicion1;
    }

    /**
     * Selecciona una transición para disparar en función del tipo de política.
     * ALEATORIA: Devuelve una transicion al azar.
     * BALANCEADA: Prioriza la transición habilitada con menos disparos realizados.
     * PRIORITARIA: Prioriza un segmento en la etapa 3 si cumple la relación definida por la prioridad. Si no es posible, utiliza la política BALANCEADA.
     *
     * @param transicionesHabilitadas Arreglo binario (1 = habilitada, 0 = no habilitada).
     * @param disparos                Arreglo que lleva el conteo de los disparos realizados por cada transición.
     * @return El índice de la transición seleccionada, o -1 si ninguna transición está habilitada.
     * @throws IllegalArgumentException Si los arreglos tienen tamaños inválidos.
     */
    public int seleccionarTransicion(int[] transicionesHabilitadas, double[] disparos) {

        int transicionSeleccionada = -1;    // Inicializa la transición seleccionada como no válida (-1).
        double minValue = disparos[0];     // Inicializa el valor mínimo con el número de disparos de la primera transición.

        switch (tipo){
            case ALEATORIA:
                // Devuelve una transicion al azar
                ArrayList<Integer> indices = new ArrayList<>();
                // Recorrer el arreglo y guardar los índices donde hay 1s
                for (int i = 0; i < transicionesHabilitadas.length; i++) {
                    if (transicionesHabilitadas[i] == 1) {
                        indices.add(i);
                    }
                }

                // Si no hay ningún 1 en el arreglo, retornamos -1
                if (indices.isEmpty()) {
                    return -1;
                }

                // Elegimos aleatoriamente uno de los índices guardados
                Random rand = new Random();
                transicionSeleccionada=indices.get(rand.nextInt(indices.size()));
                break;
            case BALANCEADA:
                /*
                    Balance en la etapa de Cargar
                 */
                if(transicionesHabilitadas[2]==1 && disparos[1]!=0){
                    double relacion = disparos[2]/disparos[1];
                    if (relacion<1){
                        return 2;
                    }
                }
                /*
                    Balance en la etapa de Mejorar
                 */
                if(transicionesHabilitadas[6]==1 && disparos[5]!=0){
                    double relacion = disparos[6]/disparos[5];
                    if (relacion<1){
                        return 6;
                    }
                }
                // Recorre todas las transiciones para encontrar la habilitada con menor cantidad de disparos.
                int i = 1;
                while (i < cantidadTransiciones) {
                    if (transicionesHabilitadas[i] == 1 && disparos[i] < minValue && i!=2 && i!=6) {
                        minValue = disparos[i];      // Actualiza el valor mínimo con el número de disparos de la transición actual.
                        transicionSeleccionada = i;  // Guarda el índice de la transición con menos disparos.
                    }
                    i++;  // Avanza al siguiente índice en la lista de transiciones.
                }
                break;
            case PRIORITARIA:
                /*
                    Política de procesamiento que prioriza un segmento en la etapa seleccionada.
                */
                if(Objects.equals(segmento, Segmento.IZQUIERDA)){
                    if(transicionesHabilitadas[transicion]==1 && disparos[transicion-1]!=0){
                        double relacion = disparos[transicion]/disparos[transicion-1];
                        if (relacion<(1-prioridad)){
                            return transicion;
                        }
                    }
                } else if (Objects.equals(segmento, Segmento.DERECHA)){
                    if(transicionesHabilitadas[transicion]==1 && disparos[transicion+1]!=0){
                        double relacion = disparos[transicion]/disparos[transicion+1];
                        if (relacion<(1-prioridad)){
                            return transicion;
                        }
                    }
                }
                // Recorre todas las transiciones para encontrar la habilitada con menor cantidad de disparos.
                // Esta etapa es igual a la Politica BALANCEADA.
                /*
                    Balance en la etapa de Cargar
                 */
                if(transicionesHabilitadas[2]==1 && disparos[1]!=0){
                    double relacion = disparos[2]/disparos[1];
                    if (relacion<1){
                        return 2;
                    }
                }
                /*
                    Balance en la etapa de Mejorar
                 */
                if(transicionesHabilitadas[6]==1 && disparos[5]!=0){
                    double relacion = disparos[6]/disparos[5];
                    if (relacion<1){
                        return 6;
                    }
                }
                int j = 1;
                while (j < cantidadTransiciones) {  // Recorre todas las transiciones hasta cantidadTransiciones - 1.
                    // Verifica si la transición está habilitada y no es la misma que la transición priorizada.
                    if (transicionesHabilitadas[j] == 1 && j != transicion && j!=2 && j!=6) {
                        if (disparos[j] < minValue) {
                            minValue = disparos[j];      // Actualiza el valor mínimo con el número de disparos de la transición actual.
                            transicionSeleccionada = j;  // Guarda el índice de la transición con menos disparos.
                        }
                    }
                    j++;  // Avanza al siguiente índice en la lista de transiciones.
                }
                break;
            default:
                System.out.println("Política inválida.");
        }
        return transicionSeleccionada;
    }
}