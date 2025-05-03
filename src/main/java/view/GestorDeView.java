package view;

import gestordeperfil.GestorDePerfil;
import ranking.Ranking;

public class GestorDeView {
    private MainView mainView;
    private ProfileView profileView;
    private RankingView rankingView;
    private final GestorDePerfil gestorPerfil;
    private final Ranking ranking;

    public GestorDeView(GestorDePerfil gestorPerfil, Ranking ranking) {
        this.gestorPerfil = gestorPerfil;
        this.ranking = ranking;

        mainView = new MainView(this);
        mainView.setVisible(true);
        profileView = new ProfileView(this, gestorPerfil);
        profileView.setVisible(false);
        rankingView = new RankingView(this, ranking);
        rankingView.setVisible(false);
    }

    // Navigation methods
    public void mostrarGestionPerfil() {
        mainView.setVisible(false);
        rankingView.setVisible(false);
        profileView.setVisible(true);
    }

    public void mostrarMain()
    {
        profileView.setVisible(false);
        rankingView.setVisible(false);
        mainView.setVisible(true);
    }

    public void mostrarRanking()
    {
        mainView.setVisible(false);
        profileView.setVisible(false);
        rankingView.setVisible(true);
    }
}
