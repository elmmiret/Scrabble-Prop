package view;

import gestordepartida.GestorDePartida;
import gestordepartida.Partida;
import gestordeperfil.GestorDePerfil;
import ranking.Ranking;

public class GestorDeView {
    private MainView mainView;
    private GestionPerfilView gestionPerfilView;
    private RankingView rankingView;
    private GestionPartidaView gestionPartidaView;
    private GestorDePerfil gestorPerfil;
    private Ranking ranking;
    private GestorDePartida gestorDePartida;

    public GestorDeView(GestorDePerfil gestorPerfil, Ranking ranking, GestorDePartida gestorDePartida) {
        this.gestorPerfil = gestorPerfil;
        this.ranking = ranking;
        this.gestorDePartida = gestorDePartida;
        this.ranking = ranking;


        mainView = new MainView(this);
        mainView.setVisible(true);
        gestionPerfilView = new GestionPerfilView(this, gestorPerfil);
        gestionPerfilView.setVisible(false);
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
        gestionPerfilView.setVisible(true);
    }

    public void mostrarMain()
    {
        gestionPerfilView.setVisible(false);
        rankingView.setVisible(false);
        gestionPartidaView.setVisible(false);
        mainView.setVisible(true);
    }

    public void mostrarRanking()
    {
        mainView.setVisible(false);
        gestionPerfilView.setVisible(false);
        gestionPartidaView.setVisible(false);
        rankingView.setVisible(true);
    }

    public void mostrarGestionPartida()
    {
        mainView.setVisible(false);
        gestionPerfilView.setVisible(false);
        rankingView.setVisible(false);
        gestionPartidaView.setVisible(true);
    }

    public void volverMenuGestionPartida(JugarPartidaView jugarPartidaView) {
        jugarPartidaView.setVisible(false);
        mostrarGestionPartida();
    }

    public void mostrarPartida(Partida partida)
    {
        JugarPartidaView jugarPartidaView = new JugarPartidaView(this, partida, gestorDePartida, gestorPerfil);
        mainView.setVisible(false);
        gestionPerfilView.setVisible(false);
        gestionPartidaView.setVisible(false);
        rankingView.setVisible(false);
        jugarPartidaView.setVisible(true);
    }

    public void mostrarRepeticion(Partida partida) {
        mainView.setVisible(false);
        gestionPerfilView.setVisible(false);
        rankingView.setVisible(false);
        gestionPartidaView.setVisible(false);

        RepeticionPartidaView repeticionView = new RepeticionPartidaView(partida, gestorDePartida, this);
        repeticionView.setVisible(true);
    }
}
