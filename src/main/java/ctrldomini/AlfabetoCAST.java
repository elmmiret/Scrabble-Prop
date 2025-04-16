package ctrldomini;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.io.*;

import java.lang.String;
import java.nio.file.*;

public class AlfabetoCAST {
    private Map<String, SimpleEntry<Integer, Integer>> mapaLetras;

    public AlfabetoCAST() {
        mapaLetras = cargarArchivo("src/main/java/archivos/letrasCAST.txt");
    }

    private Map<String, SimpleEntry<Integer, Integer>> cargarArchivo(String ruta) {
        Map<String, SimpleEntry<Integer, Integer>> mapaLetrasAux = new HashMap<>();

        List<String> lineas;

        try{
            lineas = Files.readAllLines(Paths.get(ruta));
            for(String linea : lineas) {
                String[] partes = linea.split(" ");
                if(partes.length == 3) {
                    String letra = partes[0];
                    try {
                        int frequencia = Integer.parseInt(partes[1]);
                        int puntuacion = Integer.parseInt(partes[2]);
                        mapaLetrasAux.put(letra, new SimpleEntry<>(frequencia, puntuacion));
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
        return mapaLetrasAux;
    }


    public Map<String, SimpleEntry<Integer, Integer>> getMapaLetras() {
        return mapaLetras;
    }

    public Integer getFrequenciaLetra(String letra) {
        return mapaLetras.get(letra).getKey();
    }

    public Integer getPuntuacionLetra(String letra) {
        return mapaLetras.get(letra).getValue();
    }
}
