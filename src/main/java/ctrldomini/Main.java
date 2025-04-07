package ctrldomini;

import com.google.gson.Gson;
import algorisme.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

      //vector de palabras
      /*String[] palabras = {
              "apple", "app", "cocacol·la", "banana", "ban", "band", "bat", "bar", "bark",
              "car", "cat", "carpet", "dog", "door", "dolphin", "elephant",
              "eagle", "fish", "frog", "goat", "giraffe", "horse", "house",
              "ice", "igloo", "jungle", "jump", "kite", "kitten", "lion",
              "lamp", "monkey", "mountain", "nest", "nose", "orange", "owl",
              "penguin", "parrot", "queen", "quail", "rabbit", "river",
              "snake", "sun", "tiger", "tree", "umbrella", "unicorn", "violet",
              "violin", "whale", "window", "xylophone", "yak", "zebra", "zoo"
      };

      //insertar todas las palabras del vector en el DAWG
      for(String palabra : palabras) {
        dawg.insertar(palabra);
      }*/

      Dawg dawgcatala = new Dawg();
      dawgcatala.insertarDiccionarioCatalan(dawgcatala);

      //buscar algunas palabras
      System.out.println("Buscar 'cocacola mal escrito': " + dawgcatala.buscar("cocacol·la")); // true
      System.out.println("Buscar 'GOLADES': " + dawgcatala.buscar("GOLADES"));     // true
      System.out.println("Buscar 'GOLAFRE': " + dawgcatala.buscar("GOLAFRE"));      // true
      System.out.println("Buscar 'EMBORRASCAR': " + dawgcatala.buscar("EMBORRASCAR"));      // true
      System.out.println("Buscar 'EMOL·LIENTS': " + dawgcatala.buscar("EMOL·LIENTS"));    // false

      //verificar prefijos
      System.out.println("Prefijo 'GO': " + dawgcatala.empiezaCon("ba"));   // true
      System.out.println("Prefijo 'EMB': " + dawgcatala.empiezaCon("do"));    // true
      System.out.println("Prefijo 'xyz': " + dawgcatala.empiezaCon("xyz"));  // false

      AlfabetoCAT alfcat = new AlfabetoCAT();
      Map<String, AbstractMap.SimpleEntry<Integer, Integer>> mapaLetrasCat = alfcat.getMapaLetras();

      //Verificar que el alfabeto funciona
      System.out.println("La linea con L·L del alfabeto catalan tiene frequencia y puntuacion de " + alfcat.getFrequenciaLetra("L·L") + " y " + alfcat.getPuntuacionLetra("L·L"));


      /*try (BufferedReader entrada = new BufferedReader (new FileReader("src/main/java/archivos/castellano.txt"))) {
        String linea;
        while((linea = entrada.readLine()) != null) {
          System.out.println(linea); //Procesa cada linea
        }
      } catch (IOException e) {
        System.err.println("Error al leer el archivo: " + e.getMessage());
      }*/
    }
}