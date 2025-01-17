import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        Log log;
        try {
            log = new Log("log/log.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Crear la instancia del Monitor que gestiona las transiciones.
        Monitor monitor = new Monitor(log);

        // Definición de las transiciones asociadas a cada proceso.
        Proceso[] procesos = getProcesos(monitor);

        // Crear y ejecutar un hilo para cada proceso.
        Thread[] hilos = new Thread[procesos.length];
        for (int i = 0; i < procesos.length; i++) {
            hilos[i] = new Thread(procesos[i]);
            hilos[i].start();
        }

        // Ejecutar los procesos durante un tiempo determinado (60 segundos).
        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Interrumpimos los procesos.
        for (int i = 0; i < procesos.length; i++) {
            hilos[i].interrupt();
        }

        // Esperamos a todos los procesos.
        for (int i = 0; i < procesos.length; i++) {
            try {
                hilos[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Obtener y mostrar la cantidad de disparos por transición.
        double[] disparos = monitor.getDisparos();
        System.out.println("\nResultados:");
        for (int i = 0; i < disparos.length; i++) {
            System.out.printf("La transición %d se disparó %d veces.%n", i, (int) disparos[i]);
        }

        try {
            log.closeFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Proceso[] getProcesos(Monitor monitor) {
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
                3,    // Importador
                5,    // Cargador 1
                5,    // Cargador 2
                2,    // Filtro 1
                2,    // Filtro 2
                4,    // Redimensionador 1
                4,    // Redimensionador 2
                10     // Exportador
        };

        // Parámetro adicional para el Importador.
        int capacidadImportador = 1000;

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
        return procesos;
    }
}
