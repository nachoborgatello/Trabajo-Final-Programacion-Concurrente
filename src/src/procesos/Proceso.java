package src.procesos;

import src.Monitor;

import java.util.ArrayList;

public abstract class Proceso implements Runnable {

    private final String nombre;            // Nombre del proceso.
    protected final long tiempo;            // Tiempo que el proceso debe esperar al realizar la tarea (en milisegundos).
    protected final int[] transiciones;     // Arreglo de transiciones que el proceso debe manejar.
    protected int index;                    // Índice que indica la transición actual que el proceso disparará.
    private final int[] cuenta;             // Cuenta de disparos de cada transicion.
    protected final Monitor monitor;        // Objeto Monitor que sincroniza las transiciones.

    protected final ArrayList<Integer> timeStamp;    // Implementado para medir los tiempos medios de duracion al entrar al monitor

    /**
     * Constructor de la clase src.procesos.Proceso.
     *
     * @param nombre       Nombre del proceso.
     * @param transiciones Arreglo de transiciones que el proceso debe manejar.
     * @param tiempo       Tiempo de espera entre tareas (en milisegundos).
     * @param monitor      Objeto Monitor para coordinar las transiciones.
     */
    public Proceso(String nombre, int[] transiciones, long tiempo, Monitor monitor) {
        this.nombre = nombre;
        this.transiciones = transiciones;
        index = 0;
        this.tiempo = tiempo;
        cuenta = new int[transiciones.length];
        this.monitor = monitor;
        timeStamp = new ArrayList<Integer>();
    }

    public String getNombre() {
        return nombre;
    }

    public int[] getCuenta() {
        return cuenta;
    }

    public void setCuenta(int transicion) {
        cuenta[transicion]++;
    }

    public void printStats() {
        StringBuilder sb = new StringBuilder();
        sb.append(getNombre()).append("\n");
        for (int i = 0; i < getCuenta().length; i++) {
            sb.append(String.format("Transicion: %d Disparos: %d%n", transiciones[i], getCuenta()[i]));
        }
        System.out.print(sb);
    }

    public void setTimeStamp(Long antes, Long despues){
        timeStamp.add((int) (despues-antes));
    }

    public ArrayList<Integer> getTimeStamp(){
        return timeStamp;
    }
}
