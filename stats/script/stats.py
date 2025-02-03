# Nombre del archivo
nombre_archivo = "stats\log_Cargador-1.txt"

# Función para leer números de un archivo y calcular el promedio
def calcular_promedio_desde_archivo(nombre_archivo):
    with open(nombre_archivo, "r") as archivo:
        # Leer números desde el archivo, convirtiéndolos a enteros
        numeros = [int(linea.strip()) for linea in archivo if linea.strip().isdigit()]
    # Calcular el promedio
    if numeros:
        return sum(numeros) / len(numeros)
    else:
        print("Error: El archivo está vacío.")
        return None

# Ejecución del script
if __name__ == "__main__":
    resultado = calcular_promedio_desde_archivo(nombre_archivo)
    if resultado is not None:
        print(f"El promedio de los tiempos es: {resultado}")
