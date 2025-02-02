import re

path = "log/log__BALANCEADA_NINGUNO_0.0_SIN_TIEMPOS.txt"

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
reemplazar_numeros_por_letras(path, path)

# Abrir el archivo log.txt y leer la cadena
with open(path, "r") as archivo_log:
    log = archivo_log.read() # Leemos y eliminamos posibles saltos de línea

regex = r'(T0)(.*?)((T1)(.*?)(T3)|(T2)(.*?)(T4))(.*?)((T5)(.*?)(T7)(.*?)(T9)|(T6)(.*?)(T8)(.*?)(TA))(.*?)((TB)(.*?)(TD)|(TC)(.*?)(TE))(.*?)(TF)(.*?)(TG)'

grupos = r'\g<2>\g<5>\g<8>\g<10>\g<13>\g<15>\g<18>\g<20>\g<22>\g<25>\g<28>\g<30>\g<32>'

resultado = ""
encontrados = 0
total = 0

# Bucle para aplicar la expresión regular y hacer los reemplazos
while True:
    resultado, encontrados = re.subn(regex, grupos, log)
    if encontrados == 0:
        break
    total += encontrados
    log = resultado

# Mostrar resultadoados
print("Resultado:", resultado)
print("Número de reemplazos realizados:", total)

# Mostrar resultados
if not resultado:
    print("Éxito")
else:
    print("Falló")