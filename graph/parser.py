import re

def parsear(file_path):

    # Abrimos el archivo
    with open(file_path, 'r') as file:
        # Leemos todas las líneas del archivo
        lines = file.readlines()
    
    resultados = []
    
    # Iteramos sobre cada línea del archivo
    for line in lines:
        # Utilizamos una expresión regular para extraer la parte de la línea que contiene las etiquetas "S.p0"
        match = re.search(r'"(S\.[^"]*)"', line)
       
        if match:
            # Si encontramos una coincidencia, extraemos el texto de las etiquetas S.p0, S.p1, etc.
            marcado_texto = match.group(1)
            
            # Inicializamos una lista con 21 elementos (correspondientes a p0, p1,..., p20), todos con valor 0
            marcado = [0] * 21
            
            # Ahora buscamos todos los valores "p0", "p1" o "p3*3", con o sin multiplicador (p3*3)
            # La expresión regular `(?:\*(\d+))?` permite detectar el multiplicador
            tokens = re.findall(r'`p(\d+)`(?:\*(\d+))?', marcado_texto)
            
            # Iteramos sobre los tokens encontrados (pares p y multiplicador)
            for p, mult in tokens:
                index = int(p)  # Convertimos el número de p a entero (índice de la lista)
                multiplicador = int(mult) if mult else 1  # Si hay multiplicador, lo usamos, si no, ponemos 1
                
                # Verificamos que el índice esté dentro del rango permitido (0-20)
                if 0 <= index < len(marcado):
                    # Sumamos el valor del multiplicador en la posición correspondiente
                    marcado[index] += multiplicador
            
            # Calculamos la suma de las posiciones que nos interesan (2, 4, 8, 10, 12, 13, 16, 17, 19)
            suma_especificas = sum(marcado[i] for i in [2, 4, 8, 10, 12, 13, 16, 17, 19])
            
            # Almacenamos el marcado (lista de p0 a p20) y la suma calculada como un tupla
            resultados.append((marcado, suma_especificas))
    
    # Retornamos la lista de resultados (marcado y suma)
    return resultados

# Ruta del archivo
file_path = 'graph\kts\m1.kts'

# Pasamos el archivo al parser para que lo procese
parser_resultados = parsear(file_path)

# Encontramos la línea con el mayor valor de suma usando la función max y una expresión lambda
max_sum = max(parser_resultados, key=lambda x: x[1])  # La clave de la comparación es la suma (x[1])

# Mostrar los resultados de todas las líneas con su marcado y suma
for i, (marcado, suma) in enumerate(parser_resultados):
    print(f"Línea {i + 1}: {marcado} | Suma: {suma}")

# Imprimimos la línea con la mayor suma
print("\nLínea con la mayor suma:")
print(f"Marcado: {max_sum[0]} | Suma total: {max_sum[1]}")
