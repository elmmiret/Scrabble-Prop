package ctrldomini;

import java.util.HashMap;
import java.util.Scanner;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import gestordeperfil.GestorDePerfil;

/**
 * Gestiona la creación, eliminación, consulta y ejecución de partidas de Scrabble.
 * Proporciona funcionalidades para interactuar con múltiples partidas y usuarios, como:
 *
 * @author Albert Aulet Niubó
 */
public class GestorDePartida {
    private Map<Integer, Partida> partidas;
    private GestorDePerfil gestorDePerfil;
    Scanner scanner;

    /**
     * Constructor que inicializa el gestor con un scanner para entrada de usuario
     * y un mapa vacío para almacenar partidas.
     *
     * @author Albert Aulet Niubó
     */
    public GestorDePartida(GestorDePerfil gDP) {
        scanner = new Scanner(System.in);
        partidas = new HashMap<>();
        gestorDePerfil = gDP;
    }

    /**
     * Crea una nueva partida interactuando con el usuario para recoger los parámetros necesarios.
     * Valida identificadores únicos, credenciales de jugadores y configura el modo de juego.
     *
     * @return Partida creada o null si hubo errores en el proceso.
     * @author Albert Aulet Niubó
     * @author Arnau Miret Barrull
     */
    // TODO: acabar esta clase
    public Partida crearPartida() {
        Partida nuevaPartida = null;
        System.out.print("Identificador de la partida: ");
        int idpartida = scanner.nextInt();
        if (!partidas.containsKey(idpartida)) {
            System.out.print("Nombre de la partida: ");
            String nombrepartida = scanner.nextLine();
            System.out.println("Escoge un diccionario: \n1- Català\n2- Castellano\n3- English");
            // funcion para cargar el diccionario en la partida (llenar la bolsa)
            int diccionario = scanner.nextInt();

            if (diccionario == 1 || diccionario == 2 || diccionario == 3) {
                Partida.Idioma idioma;
                switch (diccionario) {
                    case 1 -> idioma = Partida.Idioma.CAT;
                    case 2 -> idioma = Partida.Idioma.CAST;
                    case 3 -> idioma = Partida.Idioma.ENG;
                }
                System.out.println("\n\"Modo de juego: \\n1- PvP\\n2- PvIA\n");
                int mododejuego = scanner.nextInt();
                if (mododejuego == 1) {
                    // PvP
                    // logear jugador principal y secundario (2 funciones)
                    System.out.println("Logguear jugador principal:\n");
                    System.out.println("Introduce el nombre de usuario principal: ");
                    String nombreprincipal = scanner.nextLine();
                    if (gestorDePerfil.existeJugador(nombreprincipal)) {
                        System.out.println("Introduce la contraseña: ");
                        String contraseñaprincipal = scanner.nextLine();
                        if (gestorDePerfil.esPasswordCorrecta(nombreprincipal, contraseñaprincipal)) {
                            System.out.println("Introduce el nombre de usuario secundario: ");
                            String nombresecundario = scanner.nextLine();
                            if (gestorDePerfil.existeJugador(nombresecundario)) {
                                System.out.println("Introduce la contraseña: ");
                                String contraseñasecundario = scanner.nextLine();
                                if (gestorDePerfil.esPasswordCorrecta(nombresecundario, contraseñasecundario)) {
                                    nuevaPartida = new Partida(gestorDePerfil.getPerfil(nombreprincipal), gestorDePerfil.getPerfil(nombresecundario), idpartida, nombrepartida, Partida.Modo.PvP, idioma);
                                    partidas.put(idpartida, nuevaPartida);
                                    System.out.println("\nPartida creada exitosamente!\n");
                                } else {
                                    System.out.println("\nLa contraseña no es correcta\n");
                                    System.out.println("1- Volver a intentar\n2- Cambiar contraseña\n3- Salir");
                                    int opcion = scanner.nextInt();
                                    if (opcion == 1) {
                                        boolean salir = false;
                                        while (!salir) {
                                            System.out.println("Introduce la contraseña");
                                            contraseñaprincipal = scanner.nextLine();
                                            if (gestorDePerfil.esPasswordCorrecta(nombresecundario, contraseñaprincipal)) {
                                                crearPartida();
                                            } else {
                                                System.out.println("1- Volver a intentar\n2- Cambiar contraseña\n3- Salir");
                                                int num = scanner.nextInt();
                                                scanner.nextLine();
                                                if (num == 2) {
                                                    opcion = 2;
                                                    salir = true;
                                                } else if (num == 3) {
                                                    return null;
                                                }
                                            }
                                        }
                                    }
                                    if (opcion == 2) {
                                        System.out.println("Introduce la frase de recuperación: ");
                                        String frase = scanner.nextLine();
                                        while (!gestorDePerfil.esFraseRecuperacionCorrecta(nombreprincipal, frase))
                                        {
                                            System.out.println("La frase de recuperación no es correcta");
                                            System.out.println("1- Volver a intentar\n 2- Salir\n");
                                            int num = scanner.nextInt();
                                            scanner.nextLine();
                                            if (num == 1) {
                                                System.out.println("Introduce la frase de recuperación: ");
                                                frase = scanner.nextLine();
                                            } else {
                                                return null;
                                            }
                                        }

                                        System.out.println("Introduce la nueva contraseña\n");
                                        String nuevaContraseña = scanner.nextLine();
                                        while (!gestorDePerfil.esPasswordSegura(nuevaContraseña)) {
                                            System.out.println("La contraseña no cumple los requisitos de seguridad\n");
                                            System.out.println("1- Volver a intentar\n 2- Salir\n");
                                            int num = scanner.nextInt();
                                            scanner.nextLine();
                                            if (num == 1) {
                                                nuevaContraseña = scanner.nextLine();
                                            } else {
                                                return null;
                                            }

                                        }
                                        gestorDePerfil.cambiarPassword(nombreprincipal, nuevaContraseña);
                                        System.out.println("Contraseña cambiada correctamente!");
                                        crearPartida();

                                    }
                                    if (opcion == 3) {
                                        return null;
                                    }

                                }
                            } else System.out.println("\nEl nombre de perfil indicado no existe\n");
                        } else {
                            System.out.println("\nLa contraseña no es correcta\n");
                            System.out.println("1- Volver a intentar\n2- Cambiar contraseña\n3- Salir");
                            int opcion = scanner.nextInt();
                            if (opcion == 1) {
                                boolean salir = false;
                                while (!salir) {
                                    System.out.println("Introduce la contraseña");
                                    contraseñaprincipal = scanner.nextLine();
                                    if (gestorDePerfil.esPasswordCorrecta(nombreprincipal, contraseñaprincipal)) {
                                        System.out.println("Contraseña correcta!");
                                        crearPartida();
                                    } else {
                                        System.out.println("1- Volver a intentarlo\n2- Cambiar contraseña\n3- Salir") {
                                            int num = scanner.nextInt();
                                            scanner.nextLine();
                                            if (num == 2) {
                                                opcion = 2;
                                                salir = true;
                                            } else if (opcion == 3) {
                                                return null;
                                            }
                                        }
                                    }
                                }
                            }
                            if (opcion == 2) {
                                System.out.println("Introduce la frase de recuperación: ");
                                String frase = scanner.nextLine();
                                while (!gestorDePerfil.esFraseRecuperacionCorrecta(nombreprincipal, frase))
                                {
                                    System.out.println("La frase de recuperación no es correcta");
                                    System.out.println("1- Volver a intentar\n 2- Salir\n");
                                    int num = scanner.nextInt();
                                    scanner.nextLine();
                                    if (num == 1) {
                                        System.out.println("Introduce la frase de recuperación: ");
                                        frase = scanner.nextLine();
                                    } else {
                                        return null;
                                    }
                                }

                                System.out.println("Introduce la nueva contraseña\n");
                                String nuevaContraseña = scanner.nextLine();
                                while (!gestorDePerfil.esPasswordSegura(nuevaContraseña)) {
                                    System.out.println("La contraseña no cumple los requisitos de seguridad\n");
                                    System.out.println("1- Volver a intentar\n 2- Salir\n");
                                    int num = scanner.nextInt();
                                    scanner.nextLine();
                                    if (num == 1) {
                                        nuevaContraseña = scanner.nextLine();
                                    } else {
                                        return null;
                                    }

                                }
                                gestorDePerfil.cambiarPassword(nombreprincipal, nuevaContraseña);
                                System.out.println("Contraseña cambiada correctamente!");
                                crearPartida();



                            }
                            if (opcion == 3) {
                                return null;
                            }

                        }
                    } else System.out.println("\nEl nombre de perfil indicado no existe\n");

                } else if (mododejuego == 2) {
                    //PvIA
                    System.out.println("Logguear jugador principal:\n");
                    System.out.println("Introduce el nombre de usuario principal: ");
                    String nombreprincipal = scanner.nextLine();
                    if (gestorDePerfil.existeJugador(nombreprincipal)) {
                        System.out.println("Introduce la contraseña: ");
                        String contraseñaprincipal = scanner.nextLine();
                        if (gestorDePerfil.esPasswordCorrecta(nombreprincipal, contraseñaprincipal)) {
                            nuevaPartida = new Partida(gestorDePerfil.getPerfil(nombreprincipal), idpartida, nombrepartida, Partida.Modo.PvIA, idioma, 1);
                            partidas.put(idpartida, nuevaPartida);
                        } else {
                            System.out.println("\nLa contraseña no es correcta\n");
                            System.out.println("1- Volver a intentar\n2- Cambiar contraseña\n3- Salir\n");
                            int opcion = scanner.nextInt();
                            scanner.nextLine();
                            if (opcion == 1) {
                                boolean salir = false;
                                while (!salir) {
                                    System.out.println("Introduce la contraseña:");
                                    contraseñaprincipal = scanner.nextLine();
                                    if (gestorDePerfil.esPasswordCorrecta(nombreprincipal, contraseñaprincipal)) {
                                        System.out.println("Contraseña correcta!");
                                        crearPartida();
                                    } else {
                                        System.out.println("Contraseña incorrecta.\n1- Volver a intentar\n2- Cambiar contraseña\n3- Salir");
                                        int num = scanner.nextInt();
                                        scanner.nextLine();
                                        if (num == 2) {
                                            opcion = 2;
                                            salir = true;
                                        } else if (num == 3) {
                                            return null;
                                        }
                                    }
                                }
                            }
                            if (opcion == 2) {
                                System.out.println("Introduce la frase de recuperación: ");
                                String frase = scanner.nextLine();
                                while (!gestorDePerfil.esFraseRecuperacionCorrecta(nombreprincipal, frase)) {
                                    System.out.println("La frase de recuperación no es correcta\n1- Volver a intentar\n3- Salir\n");
                                    int num = scanner.nextInt();
                                    scanner.nextLine();
                                    if (num == 1) {
                                        System.out.println("Introduce la frase de recuperación: ");
                                        frase = scanner.nextLine();
                                    } else {
                                        return null;
                                    }

                                }
                                System.out.println("Introduce la nueva contraseña\n");
                                String nuevaContraseña = scanner.nextLine();
                                while (!gestorDePerfil.esPasswordSegura(nuevaContraseña)) {
                                    System.out.println("La contraseña no cumple con los requisitos de seguridad\n1- Volver a intentar\n2- Salir\n");
                                    int num = scanner.nextInt();
                                    if (num == 1) {
                                        System.out.println("Introduce la nueva contraseña\n");
                                        nuevaContraseña = scanner.nextLine();
                                    } else {
                                        return null;
                                    }
                                }
                                gestorDePerfil.cambiarPassword(nombreprincipal, nuevaContraseña);
                                System.out.println("Contraseña cambiada correctamente!");

                            }
                            if (opcion == 3) {
                                return null;
                            }
                        }
                    } else System.out.println("\nEl nombre de perfil indicado no existe\n");

                } else System.out.println("\nEl modo de juego no es correcto\n");

            } else System.out.println("\nEl diccionario seleccionado no existe\n");

        } else System.out.println("\nEste identificador ya está en uso\n");

        return nuevaPartida;
    }

    /**
     * Muestra todas las partidas asociadas a un jugador (como creador u oponente).
     * Incluye detalles como ID, nombre, fecha de creación y modo de juego.
     *
     * @param jugador Perfil del jugador cuyas partidas se quieren consultar.
     * @author Albert Aulet Niubó
     */
    public void consultarPartidasJugador(Perfil jugador) {
        if (jugador == null) {
            System.out.println("\nEl jugador no es válido.\n");
            return;
        }

        List<Partida> partidasJugador = new ArrayList<>();

        for (Map.Entry<Integer, Partida> entry : partidas.entrySet()) {
            Partida partida = entry.getValue();
            if (partida.getCreador().equals(jugador) || (partida.getOponente() != null && partida.getOponente().equals(jugador))) {
                partidasJugador.add(partida);
            }
        }

        if (partidasJugador.isEmpty()) {
            System.out.println("\nEl jugador " + jugador.getUsername() + " no participa en ninguna partida.\n");
        } else {
            System.out.println("\nPartidas de " + jugador.getUsername() + ":");
            System.out.println("-------------------------------------------------");
            for (Partida p : partidasJugador) {


                System.out.println("ID: " + p.getId());
                System.out.println("Nombre: " + p.getNombre());
                System.out.println("Data de creación: " + p.getFechaHoraCreacion());
                System.out.println("Modo: " + p.getModoPartida());
                if (p.getModoPartida() == Partida.Modo.PvP) {
                    System.out.println("Oponente: " + p.getOponente());
                } else {
                    System.out.println("Dificultad: " + p.getDificultad());
                }
                System.out.println("-------------------------------------------------");
            }
            System.out.println();
        }
    }

    /**
     * Verifica si un jugador participa en una partida específica.
     *
     * @param jugador   Perfil del jugador a verificar.
     * @param idpartida Identificador de la partida.
     * @return true si el jugador es creador u oponente de la partida, false en caso contrario.
     * @author Albert Aulet Niubó
     */
    public boolean existePartidaJugador(Perfil jugador, int idpartida) {
        Partida p = partidas.get(idpartida);
        return p != null && (p.getCreador().equals(jugador) || (p.getOponente() != null && p.getOponente().equals(jugador)));
    }


    /**
     * Inicia la ejecución de una partida existente.
     *
     * @param idpartida Identificador de la partida a jugar.
     * @author Albert Aulet Niubó
     */
    public void jugar(int idpartida) {
        if (!partidas.containsKey(idpartida)) {
            System.out.println("No existe ninguna partida con identificador " + idpartida + ".\n");
            return;
        }
        Partida partida = partidas.get(idpartida);
        boolean partidaTerminada = false;
        if (partida.getModoPartida() == Partida.Modo.PvP) {
            System.out.println("Logguear jugador principal:\n");
            System.out.println("Introduce el nombre de usuario principal: ");
            String nombreprincipal = scanner.nextLine();
            if (gestorDePerfil.existeJugador(nombreprincipal)) {
                System.out.println("Introduce la contraseña: ");
                String contraseñaPrincipal = scanner.nextLine();
                if (gestorDePerfil.esPasswordCorrecta(nombreprincipal, contraseñaPrincipal)) {
                    System.out.println("Introduce el nombre del usuario secundario: ");
                    String nombresecundario = scanner.nextLine();
                    if (gestorDePerfil.existeJugador(nombresecundario)) {
                        String contraseñasecundaria = scanner.nextLine();
                        if (gestorDePerfil.esPasswordCorrecta(nombresecundario, contraseñasecundaria)) {
                            System.out.println("---- PARTIDA INICIADA ----");
                            while (!partidaTerminada) {
                                Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
                                Perfil jugadorActual = turnoActual.getJugador();
                                System.out.println("\n--- TURNO DE " + jugadorActual.getUsername() + "---");
                                System.out.println("Tablero actual: ");
                                partida.getTablero().imprimirTablero();
                                System.out.println("\nTus fichas actuales son:");
                                Map<Ficha, Integer> atril;
                                if (jugadorActual == partida.getCreador()) {
                                    System.out.println("Tu puntuación: " + turnoActual.getPuntuaciónJ1() + "\n Puntuación de tu rival: " + turnoActual.getPuntuaciónJ2());
                                    atril = turnoActual.getAtrilJ1();

                                } else {
                                    System.out.println("Tu puntuación: " + turnoActual.getPuntuaciónJ2() + "\n Puntuación de tu rival: " + turnoActual.getPuntuaciónJ1());
                                    atril = turnoActual.getAtrilJ2();
                                }
                                if (atril == null) {
                                    partidaTerminada = true;
                                    //indicar que la partida ha finalizado
                                    System.out.println("Partida finalizada: Un jugador no tiene fichas en su atril y la bolsa está vacía");
                                    System.out.println("Puntuación J1: " + getPuntuaciónJ1() + "\nPuntuación J2: " + getPuntuaciónJ2());
                                }
                                atril.forEach((ficha, cantidad) -> System.out.print(ficha.getLetra() + " (" + cantidad + ") "));
                                System.out.println("\nOpciones a jugar:\n 1.Colocar palabra \n 2.Cambiar fichas \n 3.Pasar Turno \n 4.Salir de la partida\n\n Selecciona una opción:");
                                int opcion = scanner.nextInt();
                                scanner.nextLine();
                                switch (opcion) {
                                    case 1:
                                        System.out.println("Indica la palabra que quieres colocar: ");
                                        String palabra = scanner.nextLine();
                                        System.out.println("Indica la posición inicial de la palabra (ejemplo: 5 3): ");
                                        String[] posicion = scanner.nextLine().split(" ");
                                        int x = Integer.parseInt(posicion[0]);
                                        int y = Integer.parseInt(posicion[1]);
                                        System.out.println("Indica la orientación de la palabra: vertical o horizontal");
                                        String orientacion = scanner.nextLine();
                                        if (!turnoActual.colocarPalabra(palabra, x, y, orientacion)) {
                                            opcion = 1;
                                        }
                                        break;

                                    case 2:
                                        System.out.println("Introduce las fichas a cambiar: ");
                                        String fichasInp = scanner.nextLine().toUpperCase();
                                        String[] letras = fichasInp.split(" ");
                                        Map<Ficha, Integer> fichasParaCambiar = new HashMap();
                                        for (String letra : letras) {
                                            if (letra.isEmpty()) continue;
                                            Ficha f = new Ficha(letra, 0);
                                            fichasParaCambiar.put(f, fichasParaCambiar.getOrDefault(f, 0) + 1);
                                        }
                                        boolean fichasValidas = true;
                                        for (Ficha f : fichasParaCambiar.keySet()) {
                                            if (!atril.containsKey(f) || atril.get(f) < fichasParaCambiar.get(f)) {
                                                System.out.println("Error: No tienes suficientes fichas de " + f.getLetra());
                                                fichasValidas = false;
                                                opcion = 2;
                                            }
                                        }
                                        if (fichasValidas) {
                                            turnoActual.cambiarFichas(atril, fichasParaCambiar);
                                            System.out.println("Fichas cambiadas exitosamente!");

                                        }
                                        break;
                                    case 3:
                                        turnoActual.pasarTurno();
                                        if (partida.getRondas().get(partidas.getRondas().size()) > 1) {
                                            Turno turnoAnterior = partida.getRondas().get(partidas.getRondas().size() - 2);
                                            if (turnoAnterior.getTipoJugada() == Turno.TipoJugada.pasar) {
                                                partidaTerminada = true;
                                                //indicar que la partida esta finalizada
                                                System.out.println("Partida finalizada: Ningún jugador puede formar más palabras");
                                                System.out.println("Puntuación J1: " + getPuntuaciónJ1() + "\nPuntuación J2: " + getPuntuaciónJ2());
                                            }
                                        }
                                        break;
                                    case 4:
                                        partidaTerminada = true;
                                        break;


                                }
                            }
                        }
                    }
                } else {
                    System.out.println("\nLa contraseña no es correcta\n");
                    System.out.println("1- Volver a intentar\n2- Cambiar contraseña\n");
                    int opcion = scanner.nextInt();
                    scanner.nextLine();
                    if (opcion == 2) {

                    }
                }
            }
        } else {
            System.out.println("Logguear jugador principal:\n");
            System.out.println("Introduce el nombre de usuario principal: ");
            String nombreprincipal = scanner.nextLine();
            if (gestorDePerfil.existeJugador(nombreprincipal)) {
                System.out.println("Introduce la contraseña: ");
                String contraseñaPrincipal = scanner.nextLine();
                if (gestorDePerfil.esPasswordCorrecta(nombreprincipal, contraseñaPrincipal)) {
                    System.out.println("---- PARTIDA INICIADA ----");
                    while (!partidaTerminada) {
                        Turno turnoActual = partida.getRondas().get(partida.getRondas().size() - 1);
                        Perfil jugadorActual = turnoActual.getJugador();
                        System.out.println("\n--- TURNO DE " + jugadorActual.getUsername() + "---");
                        System.out.println("Tablero actual: ");
                        partida.getTablero().imprimirTablero();
                        System.out.println("\nTus fichas actuales son:");
                        Map<Ficha, Integer> atril;
                        if (jugadorActual == partida.getCreador()) {
                            atril = turnoActual.getAtrilJ1();

                            atril.forEach((ficha, cantidad) -> System.out.print(ficha.getLetra() + " (" + cantidad + ") "));
                            System.out.println("\nOpciones a jugar:\n 1.Colocar palabra \n 2.Cambiar fichas \n 3.Pasar Turno \n 4.Salir de la partida\n\n Selecciona una opción:");
                            int opcion = scanner.nextInt();
                            scanner.nextLine();
                            switch (opcion) {
                                case 1:
                                    System.out.println("Indica la palabra que quieres colocar: ");
                                    String palabra = scanner.nextLine();
                                    System.out.println("Indica la posición inicial de la palabra (ejemplo: 5 3): ");
                                    String[] posicion = scanner.nextLine().split(" ");
                                    int x = Integer.parseInt(posicion[0]);
                                    int y = Integer.parseInt(posicion[1]);
                                    System.out.println("Indica la orientación de la palabra: vertical o horizontal");
                                    String orientacion = scanner.nextLine();
                                    turnoActual.colocarPalabra(palabra, x, y, orientacion);
                                    break;

                                case 2:
                                    System.out.println("Introduce las fichas a cambiar: ");
                                    String fichasInp = scanner.nextLine().toUpperCase();
                                    String[] letras = fichasInp.split(" ");
                                    Map<Ficha, Integer> fichasParaCambiar = new HashMap();
                                    for (String letra : letras) {
                                        if (letra.isEmpty()) continue;
                                        Ficha f = new Ficha(letra, 0);
                                        fichasParaCambiar.put(f, fichasParaCambiar.getOrDefault(f, 0) + 1);
                                    }
                                    boolean fichasValidas = true;
                                    for (Ficha f : fichasParaCambiar.keySet()) {
                                        if (!atril.containsKey(f) || atril.get(f) < fichasParaCambiar.get(f)) {
                                            System.out.println("Error: No tienes suficientes fichas de " + f.getLetra());
                                            fichasValidas = false;
                                            break;
                                        }
                                    }
                                    if (fichasValidas) {

                                        turnoActual.cambiarFichas(atril, fichasParaCambiar);
                                        System.out.println("Fichas cambiadas exitosamente!");

                                    }
                                case 3:
                                    turnoActual.pasarTurno();
                                    break;
                                case 4:
                                    partidaTerminada = true;
                                    break;
                            }

                        }
                        else {
                            turnoActual.jugarIA();
                        }
                    }
                }
            }
        }
    }

    private void

    /**
     * Elimina una partida del gestor.
     *
     * @param idpartida Identificador de la partida a borrar.
     * @author Albert Aulet Niubó
     */
    public void borrar(int idpartida) {
        if (partidas.containsKey(idpartida)) {
            partidas.remove(idpartida);
            System.out.println("La partida con identificador " + idpartida + " ha sido borrada correctamente.\n");
        }
        else {
            System.out.println("No existe ninguna partida con identificador " + idpartida + ".\n");
        }
    }


}
