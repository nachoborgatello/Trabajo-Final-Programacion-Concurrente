import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Monitor monitor = new Monitor();

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

        int[] tiempos = {
                    100,    // Importador
                    100,    // Cargador 1
                    100,    // Cargador 2
                    100,    // Filtro 1
                    100,    // Filtro 2
                    100,    // Redimensionador 1
                    100,    // Redimensionador 2
                    100     // Exportador
        };

        Proceso[] procesos = {
                new Importador("Importador 1", transiciones[0], tiempos[0], monitor),
                new Cargador("Cargador 1", transiciones[1], tiempos[1], monitor),
                new Cargador("Cargador 2", transiciones[2], tiempos[2], monitor),
                new Filtro("Filtro 1", transiciones[3], tiempos[3], monitor),
                new Filtro("Filtro 2", transiciones[4], tiempos[4], monitor),
                new Redimensionador("Redimensionador 1", transiciones[5], tiempos[5], monitor),
                new Redimensionador("Redimensionador 2", transiciones[6], tiempos[6], monitor),
                new Exportador("Exportador 1", transiciones[7], tiempos[7], monitor)
        };

        Thread[] hilos = new Thread[procesos.length];
        for (int i = 0; i < procesos.length; i++) {
            hilos[i] = new Thread(procesos[i]);
            hilos[i].start();
        }

        try {
            TimeUnit.SECONDS.sleep(120);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int[] disparos = monitor.getDisparos();
        for (int i = 0; i < disparos.length; i++) {
            System.out.println("La transicion " + i + " se disparo " + disparos[i]);
        }
    }
}
