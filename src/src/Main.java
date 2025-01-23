package src;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        // Formato de fecha
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

        // Hora de inicio
        Date start = new Date();
        System.out.println("Se inició la ejecución de la red: " + formatter.format(start));

        Log log;
        String path = "log/log.txt";
        try {
            log = new Log(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Cantidad de invariantes de transicion a cumplir durante la ejecucion del programa.
        int cantMaxima = 1000;

        // Crear la instancia del src.Monitor que gestiona las transiciones.
        Monitor monitor = new Monitor(log);

        // Definición de las transiciones asociadas a cada proceso.
        Proceso[] procesos = getProcesos(cantMaxima,monitor);

        // Crear y ejecutar un hilo para cada proceso.
        Thread[] hilos = new Thread[procesos.length];
        for (int i = 0; i < procesos.length; i++) {
            hilos[i] = new Thread(procesos[i]);
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
        System.out.printf("\nCantidad de invariantes: %d\n",cantMaxima);
        for (int i = 0; i < disparos.length; i++) {
            System.out.printf("La transición %d se disparó %d veces.%n", i, (int) disparos[i]);
        }

        try {
            log.closeFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Hora de finalización
        Date end = new Date();
        System.out.println("Se finalizó la ejecución de la red: " + formatter.format(end));
    }

    private static Proceso[] getProcesos(int cantMaxima, Monitor monitor) {
        String[] nombres = {
                "src.Importador-1",
                "src.Cargador-1",
                "src.Cargador-2",
                "src.Filtro-1",
                "src.Filtro-2",
                "src.Redimensionador-1",
                "src.Redimensionador-2",
                "src.Exportador-1"
        };

        int[][] transiciones = {
                {0},        // src.Importador
                {1, 3},     // src.Cargador 1
                {2, 4},     // src.Cargador 2
                {5, 7, 9},  // src.Filtro 1
                {6, 8, 10}, // src.Filtro 2
                {11, 13},   // src.Redimensionador 1
                {12, 14},   // src.Redimensionador 2
                {15, 16}    // src.Exportador
        };

        // Tiempos de espera (simulados) para cada proceso (en milisegundos).
        int[] tiempos = {
                100,    // src.Importador
                100,    // src.Cargador 1
                100,    // src.Cargador 2
                100,    // src.Filtro 1
                100,    // src.Filtro 2
                100,    // src.Redimensionador 1
                100,    // src.Redimensionador 2
                100     // src.Exportador
        };

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
