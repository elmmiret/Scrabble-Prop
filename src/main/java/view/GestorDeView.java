package view;

import gestordepartida.GestorDePartida;
import gestordepartida.Partida;
import gestordeperfil.GestorDePerfil;
import ranking.Ranking;

public class GestorDeView {
    private MainView mainView;
    private ProfileView profileView;
    private RankingView rankingView;
    private GestionPartidaView gestionPartidaView;
    private GestorDePerfil gestorPerfil;
    private Ranking ranking;
    private GestorDePartida gestorDePartida;

    public GestorDeView(GestorDePerfil gestorPerfil, Ranking ranking, GestorDePartida gestorDePartida) {
        this.gestorPerfil = gestorPerfil;
        this.ranking = ranking;

        mainView = new MainView(this);
        mainView.setVisible(true);
        profileView = new ProfileView(this, gestorPerfil);
        profileView.setVisible(false);
        rankingView = new RankingView(this, ranking);
        rankingView.setVisible(false);
        gestionPartidaView = new GestionPartidaView(this, gestorDePartida, gestorPerfil);
        gestionPartidaView.setVisible(false);
    }

    // Navigation methods
    public void mostrarGestionPerfil() {
        mainView.setVisible(false);
        rankingView.setVisible(false);
        gestionPartidaView.setVisible(false);
        profileView.setVisible(true);
    }

    public void mostrarMain()
    {
        profileView.setVisible(false);
        rankingView.setVisible(false);
        gestionPartidaView.setVisible(false);
        mainView.setVisible(true);
    }

    public void mostrarRanking()
    {
        mainView.setVisible(false);
        profileView.setVisible(false);
        gestionPartidaView.setVisible(false);
        rankingView.setVisible(true);
    }

    public void mostrarGestionPartida()
    {
        mainView.setVisible(false);
        profileView.setVisible(false);
        rankingView.setVisible(false);
        gestionPartidaView.setVisible(true);
    }

    public void mostrarPartida(Partida partida) {
    }
}
