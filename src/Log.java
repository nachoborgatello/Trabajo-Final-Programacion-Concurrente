import java.io.*;

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
    public void addTransicion(int n) throws IOException {
        if (writer == null) {
            throw new IOException("El archivo no está abierto");
        }
        writer.write("T" + n);  // Escribe la transición en el archivo (ejemplo: "T1", "T2")
    }
}
