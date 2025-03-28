package ctrldomini;

import com.google.gson.Gson;
import algorisme.*;

public class Main {
    public static void main(String[] args) {
      Dawg dawg = new Dawg();

      //vector de palabras
      String[] palabras = {
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
      }

      //buscar algunas palabras
      System.out.println("Buscar 'cocacola mal escrito': " + dawg.buscar("cocacol·la")); // true
      System.out.println("Buscar 'app': " + dawg.buscar("app"));     // true
      System.out.println("Buscar 'ban': " + dawg.buscar("ban"));      // true
      System.out.println("Buscar 'bat': " + dawg.buscar("bat"));      // true
      System.out.println("Buscar 'ball': " + dawg.buscar("ball"));    // false

      //verificar prefijos
      System.out.println("Prefijo 'ba': " + dawg.empiezaCon("ba"));   // true
      System.out.println("Prefijo 'do': " + dawg.empiezaCon("do"));    // true
      System.out.println("Prefijo 'xyz': " + dawg.empiezaCon("xyz"));  // false

    }
}