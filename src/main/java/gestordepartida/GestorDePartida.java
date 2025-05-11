package gestordepartida;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import gestordeperfil.*;
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

    /**
     * Construye un gestor de partidas vinculado a un gestor de perfiles.
     *
     * @param gdp Gestor de perfiles para autenticación y recuperación de datos de jugadores
     */
    public GestorDePartida(GestorDePerfil gdp) {
        partidas = new HashMap<>();
        gestorDePerfil = gdp;

        cargarPartidas();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            guardarPartidas();
            System.out.println("Partidas guardadas existosamente.");
        }));
    }

    public void cargarPartidas() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/gestordepartida/partidasbd.txt"))) {
            String linea;
            Partida partidaActual = null;
            Turno turnoActual = null;
            boolean enSeccionTablero = false;
            boolean enSeccionBolsa = false;
            boolean enSeccionTurnos = false;

            Integer id = null;
            String nombre = null;
            Partida.Idioma idioma = null;
            Partida.Modo modoPartida = null;
            Perfil creador = null;
            Perfil oponente = null;
            Integer dificultad = null;

            while ((linea = reader.readLine()) != null) {
                if (linea.startsWith("ID|")) {
                    id = Integer.parseInt(linea.split("\\|")[1]);
                } else if (linea.startsWith("NOMBRE|")) {
                    nombre = linea.split("\\|")[1];
                } else if (linea.startsWith("IDIOMA|")) {
                    idioma = Partida.Idioma.valueOf(linea.split("\\|")[1]);
                } else if (linea.startsWith("MODO|")) {
                    modoPartida = Partida.Modo.valueOf(linea.split("\\|")[1]);
                } else if (linea.startsWith("CREADOR|")) {
                    String username = linea.split("\\|")[1];
                    creador = gestorDePerfil.getPerfil(username);
                } else if (linea.startsWith("OPONENTE|")) {
                    String username = linea.split("\\|")[1];
                    if (username != null) {
                        oponente = gestorDePerfil.getPerfil(username);
                    }
                } else if (linea.startsWith("DIFICULTAD|")) {
                    dificultad = Integer.parseInt(linea.split("\\|")[1]);
                    if (modoPartida == Partida.Modo.PvP) {
                        partidaActual = new Partida(creador, oponente, id, nombre, modoPartida, idioma);
                    } else {
                        partidaActual = new Partida(creador, id, nombre, modoPartida, idioma, dificultad);
                    }
                } else if (linea.startsWith("=== TABLERO ===")) {
                    enSeccionTablero = true;
                    enSeccionBolsa = false;
                    enSeccionTurnos = false;
                } else if (linea.startsWith("=== BOLSA ===")) {
                    enSeccionTablero = false;
                    enSeccionBolsa = true;
                    enSeccionTurnos = false;
                } else if (linea.startsWith("=== TURNOS ===")) {
                    enSeccionTablero = false;
                    enSeccionBolsa = false;
                    enSeccionTurnos = true;
                    partidaActual.getRondas().clear();
                } else if (linea.startsWith("=== FIN ===")) {
                    // almacenamos la partida en el map de partidas
                    partidas.put(partidaActual.getId(), partidaActual);
                    partidaActual = null;
                } else if (enSeccionTablero && linea.startsWith("CELDA|")) {
                    String[] datos = linea.split("\\|");
                    int x = Integer.parseInt(datos[1]);
                    int y = Integer.parseInt(datos[2]);
                    Ficha ficha = partidaActual.getMapaLetras().get(datos[3]);
                    partidaActual.getTablero().setFicha(ficha, x, y);
                } else if (enSeccionBolsa) {
                    String[] letras = linea.split(",");
                    Queue<Ficha> bolsa = new LinkedList<>();
                    for (String letra : letras) {
                        if (!letra.isEmpty()) {
                            Ficha f = partidaActual.getMapaLetras().get(letra);
                            bolsa.add(f);
                        }
                    }
                    partidaActual.setBolsa(bolsa);
                } else if (enSeccionTurnos) {
                    if (linea.startsWith("TURNO|")) {
                        String[] datos = linea.split("\\|");
                        Perfil jugador = gestorDePerfil.getPerfil(datos[1]);
                        Turno.TipoJugada tipo = Turno.TipoJugada.valueOf(datos[2]);
                        turnoActual = new Turno(partidaActual, jugador, 0, 0);
                        turnoActual.setTipoJugada(tipo);
                        partidaActual.getRondas().add(turnoActual);

                    } else if (linea.startsWith("COLOCAR|")) {
                        String[] datos = linea.split("\\|");
                        turnoActual.setPalabra(datos[1]);
                        turnoActual.setX(Integer.parseInt(datos[2]));
                        turnoActual.setY(Integer.parseInt(datos[3]));
                        turnoActual.setHorizontal(datos[4].equals("horizontal") ? true : false);

                    } else if (linea.startsWith("PUNTOS|")) {
                        String[] puntos = linea.split("\\|");
                        turnoActual.setPuntosJ1(Integer.parseInt(puntos[1]));
                        turnoActual.setPuntosJ2(Integer.parseInt(puntos[2]));

                    } else if (linea.startsWith("ATRIL_J1|")) {
                        String data = linea.split("\\|")[1];
                        turnoActual.setAtrilJ1(cargarAtril(data, partidaActual.getMapaLetras()));

                    } else if (linea.startsWith("ATRIL_J2|")) {
                        String data = linea.split("\\|")[1];
                        turnoActual.setAtrilJ2(cargarAtril(data, partidaActual.getMapaLetras()));
                    }
                }
            }
        } catch (IOException | NumberFormatException | CoordenadaFueraDeRangoException | CasillaOcupadaException e) {
            System.err.println("Error al cargar partidas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<Ficha, Integer> cargarAtril(String data, Map<String, Ficha> mapaLetras) {
        Map<Ficha, Integer> atril = new HashMap<>();
        if (data.isEmpty()) return atril;

        for (String entry : data.split(",")) {
            String[] partes = entry.split(":");
            String letra = partes[0];
            int cantidad = Integer.parseInt(partes[1]);
            Ficha ficha = mapaLetras.get(letra);
            if (ficha != null) {
                atril.put(ficha, cantidad);
            }
        }
        return atril;
    }


    public void guardarPartidas() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("src/main/java/gestordepartida/partidasbd.txt"))) {
            for (Partida partida : partidas.values()) {
                // guardamos los datos básicos de la partida
                writer.println("=== PARTIDA ===");
                writer.println("ID|" + partida.getId());
                writer.println("NOMBRE|" + partida.getNombre());
                writer.println("IDIOMA|" + partida.getIdioma());
                writer.println("MODO|" + partida.getModoPartida());
                writer.println("CREADOR|" + partida.getCreador().getUsername());
                writer.println("OPONENTE|" + (partida.getOponente() != null ? partida.getOponente().getUsername() : "null"));
                writer.println("DIFICULTAD|" + partida.getDificultad());

                // después guardamos la disposición del tablero
                writer.println("=== TABLERO ===");
                for (int i = 0; i < Tablero.FILAS; i++) {
                    for (int j = 0; j < Tablero.COLUMNAS; j++) {
                        Ficha f = partida.getTablero().getFicha(i, j);
                        if (f != null) {
                            writer.println("CELDA|" + i + "|" + j + "|" + f.getLetra());
                        }
                    }
                }

                // guardamos las fichas que había en la bolsa
                writer.println("=== BOLSA ===");
                partida.getBolsa().forEach(f -> writer.print(f.getLetra() + ","));
                writer.println();

                // y finalmente guardamos todos los turnos de esa partida
                writer.println("=== TURNOS ===");
                for (Turno turno : partida.getRondas()) {
                    if (turno.getTipoJugada() == null) turno.setTipoJugada(Turno.TipoJugada.pasar); // con esto evitamos errores y en el turnoActual podemos seguir con el jugador qeu deberia
                    writer.println("TURNO|" + turno.getJugador().getUsername() + "|" + turno.getTipoJugada());
                    if (turno.getTipoJugada() == Turno.TipoJugada.colocar) {
                        writer.println("COLOCAR|" + turno.getPalabra() + "|" + turno.getX() + "|" + turno.getY() + "|" + (turno.getHorizontal() ? "horizontal" : "vertical"));
                    } else if (turno.getTipoJugada() == Turno.TipoJugada.cambiar) {
                        writer.println("CAMBIAR|" + guardarAtril(turno.getFichasCambiadas()));
                    }
                    writer.println("PUNTOS|" + turno.getPuntuacionJ1() + "|" + turno.getPuntuacionJ2());
                    writer.println("ATRIL_J1|" + guardarAtril(turno.getAtrilJ1()));
                    writer.println("ATRIL_J2|" + guardarAtril(turno.getAtrilJ2()));
                }
                writer.println("=== FIN ===");
            }
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    private String guardarAtril(Map<Ficha, Integer> atril) {
        return atril.entrySet().stream().map(e -> e.getKey().getLetra() + ":" + e.getValue()).collect(Collectors.joining(","));
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
    public Partida crearPartida(int id, String nombre, Partida.Idioma idioma,
                                Perfil jugadorPrincipal, Partida.Modo modo,
                                Perfil oponente, int dificultad) {
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

}