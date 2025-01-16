import re
from collections import defaultdict

def analizar_transiciones(archivo):
    # Expresión regular principal
    expresion = r"(T0)(.*?)((T1)(.*?)(T3)(.*?)|(T2)(.*?)(T4)(.*?))((T5)(.*?)(T7)(.*?)(T9)(.*?)|(T6)(.*?)(T8)(.*?)(TA)( .*?))((TB)(.*?)(TD)(.*?)|(TC)(.*?)(TE)(.*?))(TF)(.*?)(TG)"

    # Lista de invariantes de transición (en este caso, un ejemplo simplificado)
    invariantes = [
        {"T0", "T1", "T3", "T5", "T7", "T9", "TB", "TD", "TF", "TG"},
        {"T0", "T2", "T4", "T6", "T8", "TA", "TC", "TE", "TF", "TG"},
    ]

    transiciones_fuera_invariante = []
    invariantes_completados = 0

    try:
        with open(archivo, 'r') as file:
            contenido = file.read()

            # Buscar todas las coincidencias de la expresión regular
            coincidencias = re.findall(expresion, contenido)

            # Crear un conjunto con las transiciones encontradas
            transiciones_encontradas = set(re.findall(r"T[0-9A-F]", contenido))

            # Verificar transiciones contra los invariantes
            for invariante in invariantes:
                if invariante.issubset(transiciones_encontradas):
                    invariantes_completados += 1

            # Identificar las transiciones que no pertenecen a ningún invariante
            for transicion in transiciones_encontradas:
                if not any(transicion in invariante for invariante in invariantes):
                    transiciones_fuera_invariante.append(transicion)

            # Resultados
            print(f"Cantidad de invariantes completados: {invariantes_completados}")
            if transiciones_fuera_invariante:
                print("Transiciones fuera de los invariantes:", transiciones_fuera_invariante)
            else:
                print("Todas las transiciones pertenecen a algún invariante.")

    except FileNotFoundError:
        print(f"El archivo '{archivo}' no fue encontrado.")
    except Exception as e:
        print(f"Ocurrió un error: {e}")

# Ejemplo de uso
archivo = "transiciones.txt"  # Nombre del archivo a analizar
analizar_transiciones(archivo)
