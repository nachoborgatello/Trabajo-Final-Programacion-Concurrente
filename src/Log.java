import java.io.*; // Importación de clases para manejo de entrada y salida (I/O)

// Clase Log para manejar un archivo de registro
public class Log {
    private BufferedWriter writer; // Escritor de archivos para agregar datos al archivo
    private String filePath; // Ruta del archivo de registro

    // Constructor para inicializar el archivo
    public Log(String filePath) throws IOException {
        this.filePath = filePath;

        // Crear carpeta "log" si no existe
        File logDir = new File("log");
        if (!logDir.exists()) { // Verifica si el directorio no existe
            logDir.mkdirs(); // Crea el directorio "log"
        }

        openFile(); // Abre el archivo en modo de escritura
    }

    // Metodo para abrir el archivo en modo de escritura (append)
    private void openFile() throws IOException {
        writer = new BufferedWriter(new FileWriter(filePath, true)); // true indica que se agrega contenido sin sobrescribir
    }

    // Metodo para cerrar el archivo
    public void closeFile() throws IOException {
        if (writer != null) { // Verifica si el archivo está abierto
            writer.close(); // Cierra el archivo
        }
    }

    // Metodo para agregar una transición (un identificador como "Tn") al archivo
    public void addTransicion(int n) throws IOException {
        if (writer == null) { // Si el archivo no está abierto, lanza una excepción
            throw new IOException("El archivo no está abierto");
        }
        writer.write("T" + n); // Escribe la transición en el archivo (ejemplo: "T1", "T2")
        //writer.newLine(); // Agrega un salto de línea
    }

    // Metodo para verificar si el archivo está abierto
    public boolean isFileOpen() {
        return writer != null; // Devuelve true si el escritor no es nulo (archivo abierto)
    }
}
