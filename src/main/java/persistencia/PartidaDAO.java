package persistencia;

import gestordepartida.*;
import gestordeperfil.*;
import exceptions.CasillaOcupadaException;
import exceptions.CoordenadaFueraDeRangoException;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PartidaDAO {
    private  GestorDePerfil gestorDePerfil;

    // Constructor que recibe el GestorDePerfil
    public PartidaDAO(GestorDePerfil gestorDePerfil) {
        this.gestorDePerfil = gestorDePerfil;
    }

    private static final String RUTA_PARTIDAS = "src/main/resources/datos/partidasbd.txt";

    public  Map<Integer, Partida> cargar() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/gestordepartida/partidasbd.txt"))) {
            Map<Integer, Partida> partidas = new HashMap<>();
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
                    if (!username.equals("null")) {
                        oponente = gestorDePerfil.getPerfil(username);
                    } else {
                        oponente = null;
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

                    // Reiniciamos las variables para la siguiente partida
                    partidaActual = null;
                    enSeccionTablero = false;
                    enSeccionBolsa = false;
                    enSeccionTurnos = false;
                    id = null;
                    nombre = null;
                    idioma = null;
                    modoPartida = null;
                    creador = null;
                    oponente = null;
                    dificultad = null;
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
                        String username = datos[1];
                        Perfil jugador;
                        if ("IA".equals(username)) {
                            jugador = null; // ya que IA no tiene perfil
                        } else {
                            jugador = gestorDePerfil.getPerfil(username);
                        }
                        turnoActual = new Turno(partidaActual, jugador, 0, 0, true);
                        Turno.TipoJugada tipo = Turno.TipoJugada.valueOf(datos[2]);
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

                    } else if (linea.startsWith("TABLERO_TURNO|")) {
                        String estadoTablero = linea.split("\\|")[1];
                        if (turnoActual.getTableroTurno() == null) {
                            turnoActual.setTableroTurno(new Tablero(partidaActual.getIdioma()));
                        }
                        cargarTableroTurno(turnoActual.getTableroTurno(), estadoTablero, partidaActual.getMapaLetras());
                    } else if (linea.startsWith("ATRIL_J1|")) {
                        String data = linea.split("\\|")[1];
                        turnoActual.setAtrilJ1(cargarAtril(data, partidaActual.getMapaLetras()));


                    } else if (linea.startsWith("ATRIL_J2|")) {
                        String data = linea.split("\\|")[1];
                        turnoActual.setAtrilJ2(cargarAtril(data, partidaActual.getMapaLetras()));
                    }
                }
            }
            return partidas;

        } catch (IOException | NumberFormatException | CoordenadaFueraDeRangoException | CasillaOcupadaException e) {
            System.err.println("Error al cargar partidas: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public  void guardar(Map<Integer, Partida> partidas) {
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
                StringBuilder bolsaStr = new StringBuilder();
                for (Ficha f : partida.getBolsa()) {
                    bolsaStr.append(f.getLetra()).append(",");
                }
                writer.println(bolsaStr.toString());

                // y finalmente guardamos todos los turnos de esa partida
                writer.println("=== TURNOS ===");
                for (Turno turno : partida.getRondas()) {
                    if (turno.getTipoJugada() == null) turno.setTipoJugada(Turno.TipoJugada.pasar); // con esto evitamos errores y en el turnoActual podemos seguir con el jugador qeu deberia
                    String username = (partida.getModoPartida() == Partida.Modo.PvP) ? turno.getJugador().getUsername() : (turno.getJugador() == null ? "IA" : turno.getJugador().getUsername());
                    writer.println("TURNO|" + username + "|" + turno.getTipoJugada());
                    writer.println("TABLERO_TURNO|" + guardarTableroTurno(turno.getTableroTurno()));
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
            e.printStackTrace();
        }
    }

    private  Map<Ficha, Integer> cargarAtril(String data, Map<String, Ficha> mapaLetras) {
        Map<Ficha, Integer> atril = new HashMap<>();
        if (data.isEmpty()) return atril;

        for (String entry : data.split(",")) {
            String[] partes = entry.split(":");
            if (partes.length == 2) {
                int cantidad = Integer.parseInt(partes[1]);
                Ficha ficha = mapaLetras.get(partes[0]);
                if (ficha != null) {
                    atril.put(ficha, cantidad);
                }
            }
        }
        return atril;
    }

    private  void cargarTableroTurno(Tablero tablero, String estado, Map<String, Ficha> mapaLetras) {
        String[] celdas = estado.split(",");
        int index = 0;
        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                if (index < celdas.length) {
                    String letra = celdas[index++];
                    if (!letra.equals("-")) {
                        Ficha f = mapaLetras.get(letra);
                        try {
                            tablero.setFicha(f, i, j);
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
    }

    private  String guardarTableroTurno(Tablero tablero) {
        if (tablero == null) return "-".repeat(Tablero.FILAS * Tablero.COLUMNAS - 1);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Tablero.FILAS; i++) {
            for (int j = 0; j < Tablero.COLUMNAS; j++) {
                try {
                    Ficha f = tablero.getFicha(i, j);
                    sb.append(f != null ? f.getLetra() : "-");
                } catch (CoordenadaFueraDeRangoException e) {
                    sb.append("-");
                }
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private  String guardarAtril(Map<Ficha, Integer> atril) {
        if (atril == null || atril.isEmpty()) return "";
        return atril.entrySet().stream().map(e -> e.getKey().getLetra() + ":" + e.getValue()).collect(Collectors.joining(","));
    }

}