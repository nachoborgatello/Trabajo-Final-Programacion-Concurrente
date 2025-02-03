import re

def parse_marked_lines(file_path):
    with open(file_path, 'r') as file:
        lines = file.readlines()

    resultados = []
    for line in lines:
        match = re.search(r'"(S\.[^"]*)"', line)    # -> "S.`p0` S.`p1` S.`p11` S.`p15` S.`p20` S.`p3`*3 S.`p5` S.`p7` S.`p9`*2"
        if match:
            marcado_texto = match.group(1) #  Devuelve los grupos capturados por los paréntesis en la expresión regular.
            marcado = [0] * 21 # Crea una lista marcado con 21 elementos, todos inicializados en 0

            for p, mult in re.findall(r'`p(\d+)`(?:\*(\d+))?', marcado_texto):
                index = int(p)
                multiplicador = int(mult) if mult else 1
                if 0 <= index < len(marcado):
                    marcado[index] += multiplicador

            suma_especificas = sum(marcado[i] for i in [2, 4, 8, 10, 12, 13, 16, 17, 19])
            resultados.append((marcado, suma_especificas))

    return resultados

file_path = 'graph\kts\m10.kts'
parsed_results = parse_marked_lines(file_path)

max_sum = max(parsed_results, key=lambda x: x[1])

for i, (marcado, suma) in enumerate(parsed_results):
    print(f"Línea {i + 1}: {marcado} | Suma: {suma}")

print("\nLínea con la mayor suma:")
print(f"Marcado: {max_sum[0]} | Suma: {max_sum[1]}")
