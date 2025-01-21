import re

def reemplazar_numeros_por_letras(archivo_entrada, archivo_salida):
    # Definir los reemplazos
    reemplazos = {
        "10": "A",
        "11": "B",
        "12": "C",
        "13": "D",
        "14": "E",
        "15": "F",
        "16": "G"
    }

    # Abrir el archivo de entrada y leer el contenido
    with open(archivo_entrada, "r") as archivo:
        contenido = archivo.read()

    # Realizar los reemplazos en el contenido
    for numero, letra in reemplazos.items():
        contenido = contenido.replace(numero, letra)

    # Escribir el contenido modificado en el archivo de salida
    with open(archivo_salida, "w") as archivo:
        archivo.write(contenido)

# Llamada a la función (asegurate de cambiar los nombres de los archivos según corresponda)
reemplazar_numeros_por_letras("log/log.txt", "log/log.txt")

# Abrir el archivo log.txt y leer la cadena
with open("log/log.txt", "r") as archivo_log:
    log = archivo_log.read().strip()  # Leemos y eliminamos posibles saltos de línea

# Definir la expresión regular y la cadena de reemplazo
regex = r'(T0)(.*?)((T1)(.*?)(T3)(.*?)|(T2)(.*?)(T4)(.*?))((T5)(.*?)(T7)(.*?)(T9)(.*?)|(T6)(.*?)(T8)(.*?)(TA)(.*?))((TB)(.*?)(TD)(.*?)|(TC)(.*?)(TE)(.*?))(TF)(.*?)(TG)'

# Usamos una cadena cruda para evitar problemas con las secuencias de escape
grupos = r'\g<2>\g<5>\g<7>\g<9>\g<11>\g<14>\g<16>\g<18>\g<20>\g<22>\g<24>\g<27>\g<29>\g<31>\g<33>\g<35>'

result = ""
found = 0
total = 0

# Bucle para aplicar la expresión regular y hacer los reemplazos
while True:
    result, found = re.subn(regex, grupos, log, count=0)
    if found == 0:
        break
    total += found
    log = result

# Mostrar resultados
print("Resultado:", result)
print("Número de reemplazos realizados:", total)

# Mostrar resultados
if not result:  # Si el resultado está vacío
    print("Éxito")
else:
    print("Falló")
