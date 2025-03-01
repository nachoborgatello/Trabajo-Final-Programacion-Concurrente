package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// Clase Log para manejar un archivo de registro
public class Log {
    private BufferedWriter writer;  // Escritor de archivos para agregar datos al archivo
    private final String filePath;  // Ruta del archivo de registro

    // Constructor para inicializar el archivo
    public Log(String filePath) throws IOException {
        this.filePath = filePath;
        openFile(); // Abre el archivo en modo de escritura
    }

    // Metodo para abrir el archivo en modo de escritura (append)
    private void openFile() throws IOException {
        writer = new BufferedWriter(new FileWriter(filePath, true)); // true indica que se agrega contenido sin sobrescribir
    }

    // Metodo para cerrar el archivo
    public void closeFile() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }

    // Metodo para agregar una transición (un identificador como "Tn") al archivo
    public void addTransicion(String nombre) throws IOException {
        if (writer == null) {
            throw new IOException("El archivo no está abierto");
        }
        writer.write(nombre);  // Escribe la transición en el archivo (ejemplo: "T1", "T2")
    }

    public void add(Integer n) throws IOException {
        if (writer == null) {
            throw new IOException("El archivo no está abierto");
        }
        writer.write(n.toString()); // Convierte a texto antes de escribir
        writer.newLine(); // Añade un salto de línea si cada número debe estar en una nueva línea
    }
}
