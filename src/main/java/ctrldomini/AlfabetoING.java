package ctrldomini;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.io.*;

import java.lang.String;
import java.nio.file.*;

/**
 * La clase AlfabetoING gestiona el conjunto de fichas disponibles para el juego,
 * cargando desde un archivo la frecuencia y puntuación de cada letra del alfabeto inglés.
 * Proporciona métodos para acceder a la información de las fichas.
 *
 * @author Arnau Miret Barrull
 */
public class AlfabetoING {

    /**
     * Mapa que almacena las fichas como clave y su frecuencia como valor.
     * La frecuencia indica cuántas veces aparece cada ficha en el conjunto total.
     */
    private Map<Ficha,Integer> mapaFichas;

    /**
     * Constructor que inicializa el mapa de fichas cargando los datos desde
     * el archivo ubicado en la ruta especificada.
     */
    public AlfabetoING() {
        mapaFichas = cargarArchivo("src/main/java/archivos/letrasENG.txt");
    }

    /**
     * Carga los datos de fichas desde un archivo de texto.
     *
     * @param ruta Ruta del archivo a cargar
     * @return Mapa de fichas con sus frecuencias
     * @throws IOException Si ocurre un error durante la lectura del archivo
     * @throws NumberFormatException Si hay errores en el formato numérico del archivo
     */
    private Map<Ficha,Integer> cargarArchivo(String ruta) {
        Map<Ficha,Integer> mapaFichasAux = new HashMap<>();

        List<String> lineas;

        try{
            lineas = Files.readAllLines(Paths.get(ruta));
            for(String linea : lineas) {
                String[] partes = linea.split(" ");
                if(partes.length == 3) {
                    String letra = partes[0];
                    if (letra.equals("#")) {
                        continue;
                    }
                    try {
                        int frequencia = Integer.parseInt(partes[1]);
                        int puntuacion = Integer.parseInt(partes[2]);
                        mapaFichasAux.put(new Ficha(letra, puntuacion), frequencia);
                    }
                    catch (NumberFormatException e) {
                        System.err.println("Error al convertir números en la línea: " + linea);
                    }
                }
            }
        }
        catch (IOException e){
            System.err.println("Error de lectura del file");
        }
        return mapaFichasAux;
    }

    /**
     * Devuelve el mapa completo de fichas con sus frecuencias.
     *
     * @return Mapa donde la clave es la ficha y el valor es su frecuencia
     */
    public Map<Ficha,Integer> getMapaFichas() {
        return mapaFichas;
    }

}
