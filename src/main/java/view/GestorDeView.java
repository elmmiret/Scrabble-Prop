package view;

import gestordepartida.GestorDePartida;
import gestordepartida.Partida;
import gestordeperfil.GestorDePerfil;
import ranking.Ranking;

/**
 * Clase encargada de gestionar la navegación entre las diferentes vistas de la aplicación.
 * Actúa como intermediario entre las interfaces de usuario (vistas).
 * Controla la visibilidad de las ventanas y coordina las interacciones entre las vistas y los gestores.
 *
 * @author Marc Ribas Acon
 */

public class GestorDeView {
    /** Vista principal de la aplicación */
    private MainView mainView;
    /** Vista para la gestión de perfiles */
    private GestionPerfilView gestionPerfilView;
    /** Vista que muestra el ranking de jugadores */
    private RankingView rankingView;
    /** Vista para la gestión de partidas */
    private GestionPartidaView gestionPartidaView;
    /** Controlador de dominio relacionado con perfiles de usuario */
    private GestorDePerfil gestorPerfil;
    /** Gestor de datos y operaciones del ranking */
    private Ranking ranking;
    /** Controlador de dominio relacionado con partidas */
    private GestorDePartida gestorDePartida;

    /**
     * Constructor que inicializa el gestor de vistas y las dependencias necesarias.
     *
     * @param gestorPerfil   Gestor de perfiles inyectado
     * @param ranking        Ranking inyectado
     * @param gestorDePartida Gestor de partidas inyectado
     */
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

    /**
     * Navega a la vista de gestión de perfiles.
     * Oculta todas las demás vistas.
     */
    public void mostrarGestionPerfil() {
        mainView.setVisible(false);
        rankingView.setVisible(false);
        gestionPartidaView.setVisible(false);
        gestionPerfilView.setVisible(true);
    }

    /**
     * Vuelve a la vista principal.
     * Oculta todas las demás vistas.
     */
    public void mostrarMain()
    {
        gestionPerfilView.setVisible(false);
        rankingView.setVisible(false);
        gestionPartidaView.setVisible(false);
        mainView.setVisible(true);
    }

    /**
     * Muestra la vista de ranking.
     * Oculta todas las demás vistas.
     */
    public void mostrarRanking()
    {
        mainView.setVisible(false);
        gestionPerfilView.setVisible(false);
        gestionPartidaView.setVisible(false);
        rankingView.setVisible(true);
    }

    /**
     * Muestra la vista de gestión de partidas.
     * Oculta todas las demás vistas.
     */
    public void mostrarGestionPartida()
    {
        mainView.setVisible(false);
        gestionPerfilView.setVisible(false);
        rankingView.setVisible(false);
        gestionPartidaView.setVisible(true);
    }

    /**
     * Vuelve a la vista de gestión de partidas desde una partida en curso.
     *
     * @param jugarPartidaView Vista de partida en curso que se ocultará
     */
    public void volverMenuGestionPartida(JugarPartidaView jugarPartidaView) {
        jugarPartidaView.setVisible(false);
        mostrarGestionPartida();
    }

    /**
     * Vuelve a la vista de gestión de partidas desde una partida en curso.
     *
     * @param repeticionPartidaView Vista de la repetición en curso que se ocultará
     */
    public void volverMenuGestionPartida(RepeticionPartidaView repeticionPartidaView) {
        repeticionPartidaView.setVisible(false);
        mostrarGestionPartida();
    }

    /**
     * Muestra la vista para jugar una partida específica.
     *
     * @param partida Partida que se va a jugar
     */
    public void mostrarPartida(Partida partida)
    {
        JugarPartidaView jugarPartidaView = new JugarPartidaView(this, partida, gestorDePartida, gestorPerfil);
        mainView.setVisible(false);
        gestionPerfilView.setVisible(false);
        gestionPartidaView.setVisible(false);
        rankingView.setVisible(false);
        jugarPartidaView.setVisible(true);
    }

    /**
     * Muestra la vista de repetición de una partida finalizada.
     *
     * @param partida Partida cuya repetición se visualizará
     */
    public void mostrarRepeticion(Partida partida) {
        mainView.setVisible(false);
        gestionPerfilView.setVisible(false);
        rankingView.setVisible(false);
        gestionPartidaView.setVisible(false);

        RepeticionPartidaView repeticionView = new RepeticionPartidaView(partida, gestorDePartida, this);
        repeticionView.setVisible(true);
    }
}
