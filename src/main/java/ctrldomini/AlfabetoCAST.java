package ctrldomini;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.io.*;

import java.lang.String;
import java.nio.file.*;

public class AlfabetoCAST {
    private Map<Ficha,Integer> mapaFichas;

    public AlfabetoCAST() {
        mapaFichas = cargarArchivo("src/main/java/archivos/letrasCAST.txt");
    }

    private Map<Ficha,Integer> cargarArchivo(String ruta) {
        Map<Ficha,Integer> mapaFichasAux = new HashMap<>();

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
                        mapaFichasAux.put(new Ficha(letra,puntuacion), frequencia);
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


    public Map<Ficha,Integer> getMapaFichas() {
        return mapaFichas;
    }

    public int getFrequenciaLetra(Ficha ficha) {
        return mapaFichas.get(ficha);
    }

    /*public int getPuntuacionLetra(String letra) {
        return mapaLetras
    }*/
}
