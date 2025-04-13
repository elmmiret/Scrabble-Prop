package ctrldomini;

public class Main {
  public static void main(String[] args) {
    Perfil j1 = new Perfil("Pollito", "a", "a");
    Perfil j2 = new Perfil("Gian", "b", "b");
    Partida.Modo m = Partida.Modo.PvP;
    Partida partida = new Partida(j1, j2, "partida-de-prueba", m);

    // Coloca fichas de prueba
    Ficha fichaA = new Ficha("A", 1);
    Ficha fichaB = new Ficha("B", 3);

    partida.getTablero().setFicha(fichaA, 7, 7); // Centro del tablero
    partida.getTablero().setFicha(fichaB, 7, 8);

    // Imprimir tablero
    System.out.println("Estado del tablero:");
    partida.getTablero().imprimirTablero();
  }
}
