package src;

import src.procesos.*;
import src.utils.Politica;
import src.utils.Red;
import src.utils.Segmento;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int MAX_INVARIANTES = 200;              // Cantidad máxima de invariantes
    private static final Politica politica = Politica.PRIORITARIA; // Politica implementada durante la ejecucion de la red
    private static final Segmento segmento = Segmento.IZQUIERDA;     // Segmento a priorizar de la etapa seleccionada
    private static final double prioridad = 0.8;                   // Prioridad dada al segmento
    private static final Red red = Red.TEMPORAL;                // Tipo de Red de Petri a considerar durante la ejecucion

    public static void main(String[] args) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

        logMessage("Se inició la ejecución de la red: " + formatter.format(new Date()));

        try {
            // Ruta del archivo de log
            String LOG_PATH = "log/log_" + "_" + politica + "_" + segmento + "_" + prioridad + "_" + red + ".txt";
            Log log = new Log(LOG_PATH);
            Monitor monitor = new Monitor(log,politica,segmento,prioridad,red);
            Proceso[] procesos = inicializarProcesos(monitor);

            Thread[] threads = startProcesos(procesos);
            esperarProcesos(monitor);

            pararProcesos(monitor,threads);
            printStats(monitor);
            printtimeStamps(procesos);

            log.closeFile();
        } catch (IOException e) {
            System.err.println("Error al inicializar el log: " + e.getMessage());
        }

        logMessage("Se finalizó la ejecución de la red: " + formatter.format(new Date()));
    }

    /*
        Metodo que imprime los tiempos que tardo en entrar y salir del monitor cada proceso.
     */
    private static void printtimeStamps(Proceso[] procesos) throws IOException {
        for (Proceso proceso : procesos) {
            Log log;
            log = new Log("stats/log_" + proceso.getNombre() + ".txt");
            for (int i=0;i< proceso.getTimeStamp().size();i++){
                log.add(proceso.getTimeStamp().get(i));
            }
            log.closeFile();
        }
    }

    /**
     * Metodo que inicializa los procesos que interactuarán con el monitor.
     *
     * @param monitor Instancia del monitor que gestiona la red de Petri.
     * @return Array con las instancias de los procesos inicializados.
     */
    private static Proceso[] inicializarProcesos(Monitor monitor) {
        String[] nombres = {"Importador-1", "Cargador-1", "Cargador-2", "Mejorador-1", "Mejorador-2", "Cortador-1", "Cortador-2", "Exportador-1"};

        int[][] transiciones = { {0}, {1, 3}, {2, 4}, {5, 7, 9}, {6, 8, 10}, {11, 13}, {12, 14}, {15, 16} };

        int[] tiempos = {100, 100, 100, 80, 80, 100, 100, 50};

        return new Proceso[]{
                new Importador(nombres[0], transiciones[0], tiempos[0], monitor, MAX_INVARIANTES),
                new Cargador(nombres[1], transiciones[1], tiempos[1], monitor),
                new Cargador(nombres[2], transiciones[2], tiempos[2], monitor),
                new Mejorador(nombres[3], transiciones[3], tiempos[3], monitor),
                new Mejorador(nombres[4], transiciones[4], tiempos[4], monitor),
                new Cortador(nombres[5], transiciones[5], tiempos[5], monitor),
                new Cortador(nombres[6], transiciones[6], tiempos[6], monitor),
                new Exportador(nombres[7], transiciones[7], tiempos[7], monitor)
        };
    }

    /**
     * Metodo para lanzar los hilos.
     *
     * @param procesos Array con las instancias de los procesos.
     * @return Array con los hilos inicializados y en ejecución.
     */
    private static Thread[] startProcesos(Proceso[] procesos) {
        Thread[] threads = new Thread[procesos.length];
        for (int i = 0; i < procesos.length; i++) {
            threads[i] = new Thread(procesos[i]);
            threads[i].start();
        }
        return threads;
    }

    /**
     * Metodo para esperar la finalización de los procesos al alcanzar el máximo de invariantes.
     *
     * @param monitor Instancia del monitor para verificar el progreso.
     */
    private static void esperarProcesos(Monitor monitor) {
        while (monitor.getDisparos()[16] != MAX_INVARIANTES) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("La espera fue interrumpida", e);
            }
        }
    }

    /**
     * Metodo para detener todos los procesos en ejecución.
     *
     * @param threads Array con los hilos en ejecución.
     */
    private static void pararProcesos(Monitor monitor, Thread[] threads) {
        // Notifica a los procesos que deben detenerse
        monitor.detenerHilos();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Error al esperar un hilo", e);
            }
        }
    }

    /**
     * Méetodo para imprimir las estadísticas finales de la ejecución de la red de Petri.
     *
     * @param monitor Instancia del monitor que contiene los datos de las transiciones.
     */
    private static void printStats(Monitor monitor) {
        System.out.println("\nResultados:");
        System.out.printf("Cantidad de invariantes: %d%n", MAX_INVARIANTES);

        double[] disparos = monitor.getDisparos();
        for (int i = 0; i < disparos.length; i++) {
            System.out.printf("La transición %d se disparó %d veces.%n", i, (int) disparos[i]);
        }
    }

    private static void logMessage(String message) {
        System.out.println(message);
    }
}
