package gestordepartida;

import java.util.*;

import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;
import gestordeperfil.*;

/**
 * Clase controladora para la gestión interactiva de partidas de Scrabble.
 *
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Menú de gestión de partidas (crear/cargar/eliminar/consultar)</li>
 *   <li>Autenticación de jugadores y recuperación de contraseñas</li>
 *   <li>Flujo completo de juego para modos PvP y PvIA</li>
 *   <li>Interacción con gestores de partidas y perfiles</li>
 * </ul>
 *
 * @author Albert Aulet Niubó
 * @author Paula Pérez Chia
 */
public class DriverGestionPartida {

    /** Gestor principal de partidas */
    private static GestorDePartida gestor;

    /** Gestor de perfiles de jugadores */
    private static GestorDePerfil gestorPerfiles;

    /** Lector de entrada de consola */
    private static Scanner scanner;

    private static HerramientasConsola consola;

    /**
     * Construye un controlador de partidas con dependencias inyectadas.
     *
     * @param gdp Gestor de partidas principal
     * @param gestorp Gestor de perfiles de jugadores
     * @param scanner Entrada de datos configurada
     */
    public DriverGestionPartida(GestorDePartida gdp, GestorDePerfil gestorp, Scanner scanner) {
        gestor = gdp;
        gestorPerfiles = gestorp;
        this.scanner = scanner;
        consola = new HerramientasConsola(scanner);
    }

    /**
     * Gestiona el menú principal de operaciones con partidas.
     *
     * @throws CasillaOcupadaException Si se intenta colocar en casilla ocupada
     * @throws CoordenadaFueraDeRangoException Si se usan coordenadas inválidas
     */
    public void partidaManagement() throws CasillaOcupadaException, CoordenadaFueraDeRangoException{
        if (gestorPerfiles.hayJugadores()){
            Scanner scanner = new Scanner(System.in);
            boolean salir = false;
            while (!salir) {
                int opcion = consola.leerEntero("Selección:\n1- Crear una nueva partida\n2- Eliminar partida\n3- Consultar partidas\n4- Atrás\n\n");
                switch (opcion) {
                    case 1 -> crearNuevaPartida();
                    case 2 -> eliminarPartida();
                    case 3 -> consultarPartidas();
                    case 4 -> salir = true;
                    default -> System.out.println("Opción inválida");
                }
            }
        }
        else System.out.println("\nNo hay ningún perfil en el sistema para jugar!");
    }

    /**
     * Crea una nueva partida con configuración interactiva.
     *
     * @throws CasillaOcupadaException Error en colocación inicial
     * @throws CoordenadaFueraDeRangoException Coordenadas fuera de rango
     */
    private void crearNuevaPartida() throws CasillaOcupadaException, CoordenadaFueraDeRangoException{
        System.out.println("\n=== CREAR PARTIDA ===");

        int id = consola.leerEntero("ID de partida: ");
        if (gestor.obtenerPartida(id) != null) {
            System.out.println("¡Ya existe una partida con este ID!\n");
            return;
        }

        String nombre = consola.leerCadena("Nombre de la partida: ");
        int idioma = consola.leerEntero("Idioma (1-CAT 2-CAST 3-ENG): ");
        Partida.Idioma idiom = switch (idioma) {
            case 1 -> Partida.Idioma.CAT;
            case 2 -> Partida.Idioma.CAST;
            case 3 -> Partida.Idioma.ENG;
            default -> throw new IllegalArgumentException("Idioma inválido");
        };

        System.out.println("\n=== AUTENTIFICACIÓN JUGADOR 1 ===");
        Perfil jugador = autenticarUsuario();
        if (jugador == null) {
            return;
        }

        int modo = consola.leerEntero("Modo (1-PvP 2-PvIA): ");
        Partida partida;
        if (modo == 1) {
            System.out.println("\n=== AUTENTIFICACIÓN JUGADOR 2 ===");
            Perfil oponente = autenticarUsuario();
            if (oponente == null) {
                return;
            }
            else if (oponente == jugador) {
                System.out.println("\nNo puedes jugar contra ti mismo!\n");
                return;
            }
            partida = gestor.crearPartida(id, nombre, idiom, jugador, Partida.Modo.PvP, oponente, 0);
        } else {
            int dificultad = consola.leerEntero("Dificultad IA (1-3): ");
            partida = gestor.crearPartida(id, nombre, idiom, jugador, Partida.Modo.PvIA, null, dificultad);
            System.out.println("Partida creada correctamente!");
        }

        System.out.print("\n");
    }

    /**
     * Autentica un usuario mediante credenciales.
     *
     * @return Perfil autenticado o null si se cancela
     */
    private Perfil autenticarUsuario() {
        while (true) {
            String username = consola.leerCadena("Username: ");
            if (!gestorPerfiles.existeJugador(username)) {
                System.out.println("No existe ningun usuario con este username: " + username);
                continue;
            }
            String contraseña = consola.leerCadena("Password del usuario: ");
            if (gestorPerfiles.esPasswordCorrecta(username, contraseña)) {
                return gestorPerfiles.getPerfil(username);
            }

            int opcion = consola.leerEntero("Password incorrecta\n1- Reintentar\n2- Restablecer Password\n3- Salir\n");
            switch (opcion) {
                case 1 -> {
                    continue;
                }
                case 2 -> {
                    if (manejarRecuperacionContraseña(username)) continue;
                    else return null;
                }
                case 3 -> {
                    return null;
                }
            }
        }
    }

    /**
     * Gestiona el proceso de recuperación de contraseña de un usuario.
     *
     * @param username Nombre de usuario que solicita la recuperación
     * @return true si la recuperación fue exitosa, false si se superaron los intentos
     */
    private boolean manejarRecuperacionContraseña (String username) {
        String frase = consola.leerCadena("\nIntroduce la frase de recuperación: ");
        int intentos = 3;
        while (!gestor.verificarFraseRecuperacion(username, frase)) {
            System.out.println("Frase incorrecta. Intentos restantes: " + --intentos);
            if (intentos == 0) return false;

            frase = consola.leerCadena("\nIntroduce la frase de recuperación: ");
        }

        String nuevaContraseña = consola.leerCadena("Introduce la nueva password: ");
        while (!gestorPerfiles.esPasswordSegura(nuevaContraseña)) {
            System.out.println("La password no cumple los requisitos de seguridad. Los requisitos son: \n- Mínimo 8 caracteres\n- Al menos 1 mayúscula\n- Al menos 1 número\n");
            nuevaContraseña = consola.leerCadena("Introduce la nueva password: ");
        }
        gestorPerfiles.cambiarPassword(username, nuevaContraseña);
        System.out.println("¡Password actualizada correctamente!");
        return true;
    }

    /**
     * Muestra el listado completo de partidas asociadas a un jugador.
     */
    private void consultarPartidas() {
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;

        List<Partida> partidas = gestor.obtenerPartidasJugador(jugador);
        System.out.println("\n=== TUS PARTIDAS ===");
        partidas.forEach(p -> {
            System.out.println("ID: " + p.getId() + "  Nombre: " + p.getNombre() + "  Modo: " + p.getModoPartida() + (p.getModoPartida().equals(Partida.Modo.PvP) ? "  Oponente: " + p.getOponente().getUsername() : "  Dificultad: " + p.getDificultad()));

        });
    }

    /**
     * Elimina permanentemente una partida del sistema.
     *
     * @return void Muestra mensajes de éxito/error por consola
     */
    private void eliminarPartida() {
        Perfil jugador = autenticarUsuario();
        if (jugador == null) return;
        int id = consola.leerEntero("ID de partida a eliminar: ");
        if (gestor.eliminarPartida(id)) {
            System.out.println("Partida eliminada");
        } else {
            System.out.println("No se encontró la partida");
        }
    }




}