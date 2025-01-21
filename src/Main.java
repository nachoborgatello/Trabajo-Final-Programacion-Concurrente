import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        Log log;
        String path = "log/log.txt";
        try {
            log = new Log(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Cantidad de invariantes de transicion a cumplir durante la ejecucion del programa.
        int cantMaxima = 200;

        // Crear la instancia del Monitor que gestiona las transiciones.
        Monitor monitor = new Monitor(log);

        // Definición de las transiciones asociadas a cada proceso.
        Proceso[] procesos = getProcesos(cantMaxima,monitor);

        // Crear y ejecutar un hilo para cada proceso.
        Thread[] hilos = new Thread[procesos.length];
        ExceptionHandler handler = new ExceptionHandler();
        for (int i = 0; i < procesos.length; i++) {
            hilos[i] = new Thread(procesos[i]);
            hilos[i].setUncaughtExceptionHandler(handler);
            hilos[i].start();
        }

        // Verificamos que se cumplan todos los invariantes de transicion.
        while (monitor.getDisparos()[16]!=cantMaxima){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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

    private static Proceso[] getProcesos(int cantMaxima, Monitor monitor) {
        String[] nombres = {
                "Importador-1",
                "Cargador-1",
                "Cargador-2",
                "Filtro-1",
                "Filtro-2",
                "Redimensionador-1",
                "Redimensionador-2",
                "Exportador-1"
        };

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

        // Tiempos de espera (simulados) para cada proceso (en milisegundos).
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

        // Creación de los procesos.
        return new Proceso[]{
                new Importador(nombres[0], transiciones[0], tiempos[0], monitor, cantMaxima),
                new Cargador(nombres[1], transiciones[1], tiempos[1], monitor),
                new Cargador(nombres[2], transiciones[2], tiempos[2], monitor),
                new Filtro(nombres[3], transiciones[3], tiempos[3], monitor),
                new Filtro(nombres[4], transiciones[4], tiempos[4], monitor),
                new Redimensionador(nombres[5], transiciones[5], tiempos[5], monitor),
                new Redimensionador(nombres[6], transiciones[6], tiempos[6], monitor),
                new Exportador(nombres[7], transiciones[7], tiempos[7], monitor)
        };
    }
}
