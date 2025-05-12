package gestordeperfil;

import java.io.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import ranking.Ranking;

/**
 * Gestiona la creación, modificación y eliminación de perfiles de usuario,
 * integrando su información con el sistema de rankings. Proporciona métodos para:
 * <ul>
 *   <li>Verificación de credenciales y frases de recuperación</li>
 *   <li>Validación de seguridad de contraseñas</li>
 *   <li>Operaciones CRUD (Crear, Leer, Actualizar, Eliminar) de perfiles</li>
 *   <li>Sincronización automática con el sistema de rankings</li>
 * </ul>
 *
 * <p>Utiliza una estructura Map para almacenamiento eficiente y mantiene
 * consistencia con el sistema de rankings asociado.
 *
 * @author Marc Ribas Acon
 */
public class GestorDePerfil {

    /**
     * Mapa que almacena todos los perfiles registrados utilizando el nombre de usuario
     * como clave única para búsquedas eficientes.
     */
    private Map<String, Perfil> jugadores;

    /**
     * Sistema de rankings asociado donde se registran y actualizan automáticamente
     * las puntuaciones de los jugadores.
     */
    private Ranking ranking;

    /**
     * Scanner utilizado para la interacción con el usuario mediante entrada/salida
     * estándar en operaciones que requieren input interactivo.
     */
    private Scanner lector;

    /**
     * Construye un nuevo gestor de perfiles asociado a un sistema de rankings.
     * Inicializa las estructuras de datos para almacenamiento en memoria
     * Carga automáticamente los perfiles desde el archivo de persistencia
     * Registra un hook de cierre para guardar los perfiles automáticamente al finalizar la aplicación
     *
     * @param rkg Sistema de rankings que se actualizará con los perfiles cargados
     */
    public GestorDePerfil(Ranking rkg) {
        jugadores = new HashMap<>();
        ranking = rkg;
        cargarPerfiles();
        // Register shutdown hook to save profiles on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            guardarPerfiles();
            System.out.println("Perfiles guardados exitosamente.");
        }));
    }

    /**
     * Carga los perfiles desde el archivo de persistencia
     * Los perfiles cargados se añaden al mapa interno y se registran en el sistema de rankings.
     *
     * @throws FileNotFoundException Si el archivo no existe (se inicia con lista vacía)
     * @throws IOException Si ocurre un error de lectura del archivo
     * @throws NumberFormatException Si los valores numéricos en el archivo no son válidos
     */
    public void cargarPerfiles() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/gestordeperfil/perfilesbd.txt"))) {
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
                ranking.addToRankings(perfil);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de perfiles no encontrado. Iniciando con lista vacía.");
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar perfiles: " + e.getMessage());
        }
    }

    /**
     * Guarda todos los perfiles en el archivo de persistencia
     * Este método se ejecuta automáticamente al cerrar la aplicación mediante un shutdown hook.
     *
     * @throws IOException Si ocurre un error de escritura en el archivo
     */
    public void guardarPerfiles() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("src/main/java/gestordeperfil/perfilesbd.txt"))) {
            for (Perfil perfil : jugadores.values()) {
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

    /**
     * Verifica si la contraseña coincide con la del perfil especificado.
     *
     * @param username Nombre de usuario a verificar (debe existir)
     * @param password Contraseña a validar
     * @return true si la contraseña es correcta, false en caso contrario
     */
    public boolean esPasswordCorrecta(String username, String password) {
        return jugadores.get(username).getPassword().equals(password);
    }

    /**
     * Valida una frase de recuperación comparándola con la almacenada (case-insensitive).
     *
     * @param username Nombre de usuario asociado al perfil
     * @param fraseRecuperacion Frase a comprobar
     * @return true si la frase coincide, false en caso contrario
     */
    public boolean esFraseRecuperacionCorrecta(String username, String fraseRecuperacion) {
        return jugadores.get(username).getFraseRecuperacion().equalsIgnoreCase(fraseRecuperacion);
    }

    /**
     * Evalúa si una contraseña cumple los requisitos mínimos de seguridad:
     * <ul>
     *   <li>Longitud mínima de 8 caracteres</li>
     *   <li>Al menos una letra mayúscula</li>
     *   <li>Al menos un dígito numérico</li>
     * </ul>
     *
     * @param password Contraseña a evaluar
     * @return true si cumple los requisitos, false en caso contrario
     */
    public boolean esPasswordSegura(String password) {
        if (password == null || password.length() < 8) return false;
        boolean tieneMayuscula = false;
        boolean tieneDigito = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) tieneMayuscula = true;
            else if (Character.isDigit(c)) tieneDigito = true;
            if (tieneMayuscula && tieneDigito) return true;
        }
        return false;
    }

    /**
     * Crea un nuevo perfil y lo registra en el sistema de rankings.
     *
     * @param username Identificador único para el nuevo perfil
     * @param password Contraseña del perfil (debe ser segura)
     * @param fraseRecuperacion Frase para recuperación de cuenta
     */
    public void crearPerfil(String username, String password, String fraseRecuperacion) {
        jugadores.put(username, new Perfil(username, password, fraseRecuperacion));
        ranking.addToRankings(jugadores.get(username));
    }

    /**
     * Actualiza el nombre de usuario manteniendo la consistencia en:
     * <ul>
     *   <li>Mapa de perfiles (actualiza la clave)</li>
     *   <li>Sistema de rankings (re-registro con nuevo nombre)</li>
     * </ul>
     *
     * @param username Nombre actual del perfil (debe existir)
     * @param newUsername Nuevo nombre a establecer (debe ser único)
     */
    public void cambiarUsername(String username, String newUsername) {
        ranking.deleteFromRankings(jugadores.get(username));

        Perfil perfil = jugadores.get(username);
        perfil.cambiarUsername(newUsername);
        jugadores.remove(username);
        jugadores.put(newUsername, perfil);

        ranking.addToRankings(jugadores.get(newUsername));
    }

    /**
     * Modifica la contraseña de un perfil existente y actualiza su registro en rankings.
     *
     * @param username Nombre del perfil a modificar
     * @param newPassword Nueva contraseña (debe cumplir políticas de seguridad)
     */
    public void cambiarPassword(String username, String newPassword) {
        ranking.deleteFromRankings(jugadores.get(username));

        jugadores.get(username).cambiarPassword(newPassword);

        ranking.addToRankings(jugadores.get(username));
    }

    /**
     * Elimina permanentemente un perfil del sistema, incluyendo:
     * <ul>
     *   <li>Remoción del mapa de jugadores</li>
     *   <li>Eliminación de todos los rankings asociados</li>
     * </ul>
     *
     * @param username Nombre del perfil a eliminar
     */
    public void eliminarPerfil(String username)
    {
        ranking.deleteFromRankings(jugadores.get(username));
        jugadores.remove(username);
    }

    /**
     * Obtiene todos los perfiles registrados en el sistema.
     *
     * @return Mapa inmutable con la estructura [Username → Perfil]
     */
    public Map<String, Perfil> getJugadores() { return jugadores; }

    /**
     * Recupera un perfil específico por su nombre de usuario.
     *
     * @param username Nombre del perfil a obtener
     * @return Perfil correspondiente o null si no existe
     */
    public Perfil getPerfil(String username)
    {
        return jugadores.get(username);
    }

    /**
     * Verifica la existencia de un perfil en el sistema.
     *
     * @param username Nombre a buscar
     * @return true si existe un perfil con ese nombre, false en caso contrario
     */
    public boolean existeJugador(String username) { return jugadores.containsKey(username); }

    /**
     * Determina si el sistema contiene perfiles registrados.
     *
     * @return true si hay al menos un perfil registrado, false si está vacío
     */
    public boolean hayJugadores() { return !jugadores.isEmpty(); }

    /**
     * Incrementa los puntos de un perfil del sistema
     *
     * @param username Nombre del perfil al que sumarle los puntos
     * @param puntos Puntos a sumar
     */
    public void incrementarPuntosJugador(String username, int puntos)
    {
        ranking.deleteFromRankings(jugadores.get(username));

        Perfil perfil = jugadores.get(username);
        perfil.incrementarPuntos(puntos);
        jugadores.remove(username);
        jugadores.put(username, perfil);

        ranking.addToRankings(jugadores.get(username));
    }

    /**
     * Incrementa en 1 las partidas jugadas del usuario obtenido como parámetro
     *
     * @param username Nombre del perfil al que sumarle las partidas jugadas
     */
    public void incrementarPartidasJugadas(String username)
    {
        ranking.deleteFromRankings(jugadores.get(username));

        Perfil perfil = jugadores.get(username);
        perfil.incrementarPartidasJugadas(1);
        jugadores.remove(username);
        jugadores.put(username, perfil);

        ranking.addToRankings(jugadores.get(username));
    }

    /**
     * Incrementa en 1 las partidas ganadas del usuario obtenido como parámetro
     *
     * @param username Nombre del perfil al que sumarle las partidas ganadas
     */
    public void incrementarPartidasGanadas(String username)
    {
        ranking.deleteFromRankings(jugadores.get(username));

        Perfil perfil = jugadores.get(username);
        perfil.incrementarPartidasGanadas(1);
        jugadores.remove(username);
        jugadores.put(username, perfil);

        ranking.addToRankings(jugadores.get(username));
    }

    /**
     * Incrementa en 1 las partidas perdidas del usuario obtenido como parámetro
     *
     * @param username Nombre del perfil al que sumarle las partidas perdidas
     */
    public void incrementarPartidasPerdidas(String username)
    {
        ranking.deleteFromRankings(jugadores.get(username));

        Perfil perfil = jugadores.get(username);
        perfil.incrementarPartidasPerdidas(1);
        jugadores.remove(username);
        jugadores.put(username, perfil);

        ranking.addToRankings(jugadores.get(username));
    }
}