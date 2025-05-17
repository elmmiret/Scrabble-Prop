package gestordepartida;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import algorisme.Algorithm;
import algorisme.Movimiento;
import gestordeperfil.*;
import persistencia.*;
import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;

/**
 * Controla la gestión de partidas de Scrabble, actuando como intermediario entre la interfaz de usuario
 * y la lógica del juego. Mantiene un registro de todas las partidas activas. Maneja operaciones relacionadas con:
 * <ul>
 *   <li>Creación y eliminación de partidas</li>
 *   <li>Manipulación del estado del juego durante las partidas</li>
 *   <li>Interacción con el gestor de perfiles</li>
 *   <li>Validación de operaciones de juego</li>
 * </ul>
 *
 * @author Albert Aulet Niubó
 */
public class GestorDePartida {

    /** Almacenamiento de partidas activas indexadas por ID */
    private Map<Integer, Partida> partidas;

    /** Gestor de perfiles para operaciones relacionadas con jugadores */
    private GestorDePerfil gestorDePerfil;

    private final ControladorPersistencia persistencia;

    /**
     * Construye un gestor de partidas vinculado a un gestor de perfiles.
     *
     * @param gdp Gestor de perfiles para autenticación y recuperación de datos de jugadores
     */
    public GestorDePartida(GestorDePerfil gdp) {
        partidas = new HashMap<>();
        gestorDePerfil = gdp;
        persistencia = new ControladorPersistencia(gestorDePerfil);

        cargarPartidas();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            guardarPartidas();
            System.out.println("Partidas guardadas existosamente.");
        }));
    }

    public void cargarPartidas() {
        partidas = persistencia.cargarPartidas();
    }



    public void guardarPartidas() {
        persistencia.guardarPartidas(partidas);
    }


    /**
     * Recupera todas las partidas registradas en el sistema.
     *
     * @return Mapa no modificable con todas las partidas (ID => Partida)
     */
    public Map<Integer, Partida> getPartidas() {
        return partidas;
    }

    /**
     * Valida una frase de recuperación para un usuario.
     *
     * @param username Nombre de usuario a verificar
     * @param frase Frase de recuperación proporcionada
     * @return true si la frase coincide con el registro del usuario
     */
    public boolean verificarFraseRecuperacion(String username, String frase) {
        return gestorDePerfil.esFraseRecuperacionCorrecta(username, frase);
    }

    /**
     * Genera una representación visual del tablero actual.
     *
     * @param tablero Tablero a imprimir
     * @throws CoordenadaFueraDeRangoException Si hay errores en las dimensiones del tablero
     */
    public void obtenerRepresentacionTablero(Tablero tablero) throws CoordenadaFueraDeRangoException {
        tablero.imprimirTablero();
    }

    /**
     * Intenta colocar una palabra en el tablero durante un turno.
     *
     * @param turno Turno actual del juego
     * @param palabra Palabra a colocar
     * @param x Coordenada X inicial
     * @param y Coordenada Y inicial
     * @param orientacion Orientación de colocación (H/V)
     * @return true si la colocación fue exitosa
     * @throws CasillaOcupadaException Si se intenta sobreescribir fichas existentes
     * @throws CoordenadaFueraDeRangoException Si las coordenadas son inválidas
     */
    public boolean colocarPalabra(Turno turno, String palabra, int x, int y, String orientacion)
            throws CasillaOcupadaException, CoordenadaFueraDeRangoException {
        return turno.colocarPalabra(palabra, x, y, orientacion);
    }

    /**
     * Realiza el intercambio de fichas durante un turno.
     *
     * @param turno Turno actual del juego
     * @param atril Fichas disponibles del jugador
     * @param letras Lista de letras a intercambiar
     * @return true si el intercambio fue exitoso
     */
    public boolean cambiarFichas(Turno turno, Map<Ficha, Integer> atril, List<String> letras) {
        Map<Ficha, Integer> cambio = new HashMap<>();
        for (String letra : letras) {
            Ficha fichaReal = null;
            for (Ficha fAtril : atril.keySet()) {
                if (fAtril.getLetra().equalsIgnoreCase(letra)) {
                    fichaReal = fAtril;
                    break;
                }
            }
            if (fichaReal == null) {
                return false;
            }
            cambio.put(fichaReal, cambio.getOrDefault(fichaReal, 0) + 1);
        }
        // Verificar cantidad suficiente
        for (Map.Entry<Ficha, Integer> entry : cambio.entrySet()) {
            Ficha f = entry.getKey();
            int needed = entry.getValue();
            if (!atril.containsKey(f) || atril.get(f) < needed) {
                return false;
            }
        }
        turno.cambiarFichas(atril, cambio);
        return true;
    }

    /**
     * Crea una nueva partida según los parámetros especificados.
     *
     * @param id Identificador único de la partida
     * @param nombre Nombre descriptivo de la partida
     * @param idioma Idioma base para fichas y diccionario
     * @param jugadorPrincipal Perfil del jugador creador
     * @param modo Modalidad de juego (PvP/PvIA)
     * @param oponente Perfil del oponente (requerido en PvP)
     * @param dificultad Nivel de dificultad IA (0-10)
     * @return Instancia de la partida recién creada
     */
    public Partida crearPartida(int id, String nombre, Partida.Idioma idioma, Perfil jugadorPrincipal, Partida.Modo modo, Perfil oponente, int dificultad) {
        Partida nuevaPartida;
        if (modo == Partida.Modo.PvP) {
            nuevaPartida = new Partida(jugadorPrincipal, oponente, id, nombre, modo, idioma);
        } else {
            nuevaPartida = new Partida(jugadorPrincipal, id, nombre, modo, idioma, dificultad);
        }
        partidas.put(id, nuevaPartida);
        guardarPartidas();
        return nuevaPartida;
    }

    /**
     * Recupera una partida específica por su ID.
     *
     * @param idPartida Identificador de la partida
     * @return Instancia de Partida o null si no existe
     */
    public Partida obtenerPartida(int idPartida) {
        return partidas.get(idPartida);
    }

    /**
     * Verifica si un jugador participa en una partida específica.
     *
     * @param jugador Perfil a verificar
     * @param idpartida Identificador de la partida
     * @return true si el jugador es creador u oponente en la partida
     */
    public boolean existePartidaJugador(Perfil jugador, int idpartida) {
        Partida p = partidas.get(idpartida);
        return p != null && (p.getCreador().equals(jugador) || (p.getOponente() != null && p.getOponente().equals(jugador)));
    }

    /**
     * Recupera todas las partidas asociadas a un jugador.
     *
     * @param jugador Perfil del jugador
     * @return Lista de partidas donde participa el jugador
     */
    public List<Partida> obtenerPartidasJugador(Perfil jugador) {
        List<Partida> partidasJugador = new ArrayList<>();
        for (Partida partida : partidas.values()) {
            if (partida.getCreador().equals(jugador) || (partida.getOponente() != null && partida.getOponente().equals(jugador))) {
                partidasJugador.add(partida);
            }
        }
        return partidasJugador;
    }

    /**
     * Elimina una partida del registro.
     *
     * @param idPartida Identificador de la partida a eliminar
     * @return true si la partida existía y fue eliminada, false en caso contrario
     */
    public boolean eliminarPartida(int idPartida) {
        boolean eliminada = partidas.remove(idPartida) != null;
        if (eliminada) {
            guardarPartidas();
        }
        return eliminada;
    }

    /**
     * Obtiene el estado actual del atril de un jugador.
     *
     * @param partida Partida de referencia
     * @param jugador Jugador solicitante
     * @return Mapa de fichas disponibles en el atril
     */
    public Map<Ficha, Integer> obtenerAtrilJugador(Partida partida, Perfil jugador) {
        Turno turno = partida.getRondas().get(partida.getRondas().size() - 1);
        if (jugador == null) return turno.getAtrilJ2();
        else return jugador.equals(partida.getCreador()) ? turno.getAtrilJ1() : turno.getAtrilJ2();
    }

    public Movimiento pedirPista(Partida partida, Perfil jugador) {
        Turno turno = partida.getRondas().get(partida.getRondas().size() - 1);
        return turno.pedirPista(jugador);
    }


    public int getMaxTurnos(Partida partida) {
        return partida.getRondas().size()-1;
    }

    public boolean isTurnoValido(Partida partida, int numTurno) {
        int max = getMaxTurnos(partida);
        return numTurno >= 1 && numTurno <= max;
    }

    public Turno getTurno(Partida partida, int index) {
        if (index < 0 || index >= partida.getRondas().size()) {
            throw new IllegalArgumentException("Índex de torn invàlid");
        }
        return partida.getRondas().get(index);
    }

    public String getOponenteUsername(Partida partida) {
        if (partida.getModoPartida() == Partida.Modo.PvP) {
            return partida.getOponente().getUsername();
        } else {
            return "IA";
        }
    }

    public Map<Ficha, Integer>[] getAtrilesTurno(Turno turno) {
        Partida partida = turno.getPartida();
        Perfil jugadorActivo = turno.getJugador();
        Map<Ficha, Integer> atrilJugador, atrilOponente;

        if (jugadorActivo != null && jugadorActivo.equals(partida.getCreador())) {
            atrilJugador = turno.getAtrilJ1();
            atrilOponente = turno.getAtrilJ2();
        } else {
            atrilJugador = turno.getAtrilJ2();
            atrilOponente = turno.getAtrilJ1();
        }

        return new Map[] {atrilJugador, atrilOponente};
    }

}