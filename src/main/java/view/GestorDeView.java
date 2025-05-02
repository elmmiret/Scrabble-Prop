package view;

import gestordeperfil.GestorDePerfil;
import ranking.Ranking;

public class GestorDeView {
    private MainView mainView;
    private ProfileView profileView;
    private final GestorDePerfil gestorPerfil;
    private final Ranking ranking;

    public GestorDeView(GestorDePerfil gestorPerfil, Ranking ranking) {
        this.gestorPerfil = gestorPerfil;
        this.ranking = ranking;

        mainView = new MainView(this);
        mainView.setVisible(true);
    }

    // Navigation methods
    public void mostrarGestionPerfil() {
        profileView = new ProfileView(this, gestorPerfil);
        mainView.setVisible(false);
        profileView.setVisible(true);
    }

    public void mostrarMain()
    {
        if (profileView != null) profileView.setVisible(false);
        mainView.setVisible(true);
    }
}
