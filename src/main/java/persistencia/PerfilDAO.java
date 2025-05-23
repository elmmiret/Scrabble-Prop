package persistencia;

import gestordeperfil.Perfil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object para operaciones de persistencia de perfiles de jugadores.
 * Maneja la carga y almacenamiento de perfiles en formato de texto plano,
 * manteniendo consistencia entre las instancias en memoria y el almacenamiento persistente.
 *
 * <p>Formato del archivo de persistencia:</p>
 * <ul>
 *   <li>Una línea por perfil con campos separados por pipes (|)</li>
 *   <li>Estructura: username|password|fraseRecuperacion|partidasJugadas|partidasGanadas|partidasPerdidas|puntos</li>
 *   <li>Codificación UTF-8</li>
 * </ul>
 *
 * @author Marc Ribas Acon
 */
public class PerfilDAO {

    private static final String RUTA_ARCHIVO = "src/main/java/gestordeperfil/perfilesbd.txt";

    /**
     * Carga todos los perfiles desde el archivo de persistencia.
     *
     * @return Mapa de perfiles cargados donde la clave es el nombre de usuario
     * @throws FileNotFoundException Si el archivo no existe (se ignora creando mapa vacío)
     * @throws IOException Si ocurren errores de lectura del archivo
     * @throws NumberFormatException Si hay valores numéricos corruptos en los datos
     */
    public Map<String, Perfil> cargar() {
        Map<String, Perfil> jugadores = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length != 7) {
                    System.err.println("Línea inválida: " + line);
                    continue;
                }
                String username = parts[0];
                String password = parts[1];
                String frase = parts[2];
                int partidasJugadas = Integer.parseInt(parts[3]);
                int partidasGanadas = Integer.parseInt(parts[4]);
                int partidasPerdidas = Integer.parseInt(parts[5]);
                int puntos = Integer.parseInt(parts[6]);

                Perfil perfil = new Perfil(username, password, frase);
                perfil.setPartidasJugadas(partidasJugadas);
                perfil.setPartidasGanadas(partidasGanadas);
                perfil.setPartidasPerdidas(partidasPerdidas);
                perfil.setPuntos(puntos);

                jugadores.put(username, perfil);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de perfiles no encontrado. Iniciando con lista vacía.");
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar perfiles: " + e.getMessage());
        }
        return jugadores;
    }

    /**
     * Persiste todos los perfiles en el archivo de almacenamiento.
     *
     * @param perfiles Mapa de perfiles a guardar (username => Perfil)
     * @throws IOException Si ocurren errores de escritura del archivo
     * @throws SecurityException Si no hay permisos de escritura en el archivo
     */
    public void guardar(Map<String, Perfil> perfiles) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(RUTA_ARCHIVO))) {
            for (Perfil perfil : perfiles.values()) {
                String line = String.join("|",
                        perfil.getUsername(),
                        perfil.getPassword(),
                        perfil.getFraseRecuperacion(),
                        String.valueOf(perfil.getPartidasJugadas()),
                        String.valueOf(perfil.getPartidasGanadas()),
                        String.valueOf(perfil.getPartidasPerdidas()),
                        String.valueOf(perfil.getPuntos()));
                writer.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error al guardar perfiles: " + e.getMessage());
        }
    }
}