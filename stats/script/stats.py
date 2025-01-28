# Nombre del archivo
nombre_archivo = "log_Importador-1.txt"

# Función para leer números de un archivo y calcular el promedio
def calcular_promedio_desde_archivo(nombre_archivo):
    try:
        with open(nombre_archivo, "r") as archivo:
            # Leer números desde el archivo, convirtiéndolos a enteros
            numeros = [int(linea.strip()) for linea in archivo if linea.strip().isdigit()]
        # Calcular el promedio
        if numeros:
            return sum(numeros) / len(numeros)
        else:
            print("Error: El archivo está vacío.")
            return None
    except FileNotFoundError:
        print(f"Error: El archivo '{nombre_archivo}' no existe.")
        return None
    except ValueError:
        print("Error: El archivo contiene datos no válidos.")
        return None

# Ejecución del script
if __name__ == "__main__":
    resultado = calcular_promedio_desde_archivo(nombre_archivo)
    if resultado is not None:
        print(f"El promedio de los números es: {resultado}")
