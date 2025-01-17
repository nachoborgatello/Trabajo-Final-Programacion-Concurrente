import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        // Crear la instancia del Monitor que gestiona las transiciones.
        Monitor monitor = new Monitor();

        // Definición de las transiciones asociadas a cada proceso.
        int[][] transiciones = {
                {0},        // Importador
                {1, 3},     // Cargador 1
                {2, 4},     // Cargador 2
                {5, 7, 9},  // Filtro 1
                {6, 8, 10}, // Filtro 2
                {11, 13},   // Redimensionador 1
                {12, 14},   // Redimensionador 2
                {15, 16}    // Exportador
        };

        // Tiempos de espera (simulados) para cada proceso.
        int[] tiempos = {
                1,    // Importador
                1,    // Cargador 1
                1,    // Cargador 2
                1,    // Filtro 1
                1,    // Filtro 2
                1,    // Redimensionador 1
                1,    // Redimensionador 2
                1     // Exportador
        };

        // Parámetro adicional para el Importador.
        int capacidadImportador = 10000;

        // Creación de los procesos.
        Proceso[] procesos = {
                new Importador("Importador 1", transiciones[0], tiempos[0], monitor, capacidadImportador),
                new Cargador("Cargador 1", transiciones[1], tiempos[1], monitor),
                new Cargador("Cargador 2", transiciones[2], tiempos[2], monitor),
                new Filtro("Filtro 1", transiciones[3], tiempos[3], monitor),
                new Filtro("Filtro 2", transiciones[4], tiempos[4], monitor),
                new Redimensionador("Redimensionador 1", transiciones[5], tiempos[5], monitor),
                new Redimensionador("Redimensionador 2", transiciones[6], tiempos[6], monitor),
                new Exportador("Exportador 1", transiciones[7], tiempos[7], monitor)
        };

        // Crear y ejecutar un hilo para cada proceso.
        Thread[] hilos = new Thread[procesos.length];
        for (int i = 0; i < procesos.length; i++) {
            hilos[i] = new Thread(procesos[i]);
            hilos[i].start();
        }

        // Ejecutar los procesos durante un tiempo determinado (60 segundos).
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Obtener y mostrar la cantidad de disparos por transición.
        int[] disparos = monitor.getDisparos();
        System.out.println("\nResultados:");
        for (int i = 0; i < disparos.length; i++) {
            System.out.printf("La transición %d se disparó %d veces.%n", i, disparos[i]);
        }
    }
}
