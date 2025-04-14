package ctrldomini;
import exceptions.*;

public class Main {
  public static void main(String[] args) throws CoordenadaFueraDeRangoException, CasillaOcupadaException {
    Perfil j1 = new Perfil("Pollito", "a", "a");
    Perfil j2 = new Perfil("Gian", "b", "b");
    Partida.Modo m = Partida.Modo.PvP;
    Partida partida = new Partida(j1, j2, "partida-de-prueba", m);

    Ficha fichaA = new Ficha("A", 1);
    Ficha fichaB = new Ficha("B", 3);
    Ficha fichaC = new Ficha("CH", 8);
    Ficha fichaD = new Ficha("L·L", 10);

    partida.getTablero().setFicha(fichaA, 'H', 7); // Centro del tablero
    partida.getTablero().setFicha(fichaB, 'H', 8);
    partida.getTablero().setFicha(fichaC, 'I', 7);
    partida.getTablero().setFicha(fichaD, 'H', 9);

    System.out.println("Estado del tablero:");
    partida.getTablero().imprimirTablero();
  }
}
