package gestordeperfil;

/**
 * Representa un perfil de usuario que contiene información de la cuenta y estadísticas de juego.
 * Esta clase gestiona el nombre de usuario, password, frase de recuperación y realiza un seguimiento
 * del número de partidas jugadas, ganadas, perdidas y los puntos acumulados para cada perfil en el sistema.
 *
 * @author Marc Ribas Acon
 */
public class Perfil {
    private String username;
    private String password;
    private String fraseRecuperacion;
    private int partidasJugadas;
    private int partidasGanadas;
    private int partidasPerdidas;
    private int puntos;

    /**
     * Construye un nuevo Perfil con el nombre de usuario, password y frase de recuperación especificados.
     * Inicializa las estadísticas de juego (partidas jugadas, ganadas, perdidas y puntos) a cero.
     *
     * @param username         el nombre de usuario para el perfil
     * @param password        la password para el perfil
     * @param fraseRecuperacion la frase de recuperación para recuperar la cuenta
     */
    public Perfil(String username, String password, String fraseRecuperacion) {
        this.username = username;
        this.password = password;
        this.fraseRecuperacion = fraseRecuperacion;
        this.partidasJugadas = 0;
        this.partidasGanadas = 0;
        this.partidasPerdidas = 0;
        this.puntos = 0;
    }

    /**
     * Establece el nombre de usuario del perfil al valor especificado.
     *
     * @param username el nuevo nombre de usuario a establecer
     */
    public void cambiarUsername(String username) {
        this.username = username;
    }

    /**
     * Establece la password del perfil al valor especificado.
     *
     * @param password la nueva password a establecer
     */
    public void cambiarPassword(String password) {
        this.password = password;
    }

    /**
     * Incrementa el contador de partidas jugadas en uno.
     */
    public void incrementarPartidasJugadas() {
        this.partidasJugadas++;
    }

    /**
     * Incrementa el contador de partidas ganadas en uno.
     */
    public void incrementarPartidasGanadas() {
        this.partidasGanadas++;
    }

    /**
     * Incrementa el contador de partidas perdidas en uno.
     */
    public void incrementarPartidasPerdidas() {
        this.partidasPerdidas++;
    }

    /**
     * Añade el número especificado de puntos al total actual.
     *
     * @param puntos los puntos a añadir
     */
    public void incrementarPuntos(int puntos) {
        this.puntos += puntos;
    }

    /**
     * Devuelve el nombre de usuario del perfil.
     *
     * @return el nombre de usuario actual
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Devuelve la password del perfil.
     *
     * @return la password actual
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Devuelve la frase de recuperación del perfil.
     *
     * @return la frase de recuperación
     */
    public String getFraseRecuperacion() {
        return this.fraseRecuperacion;
    }

    /**
     * Devuelve el número de partidas jugadas.
     *
     * @return el contador de partidas jugadas
     */
    public int getPartidasJugadas() {
        return this.partidasJugadas;
    }

    /**
     * Devuelve el número de partidas ganadas.
     *
     * @return el contador de partidas ganadas
     */
    public int getPartidasGanadas() {
        return this.partidasGanadas;
    }

    /**
     * Devuelve el número de partidas perdidas.
     *
     * @return el contador de partidas perdidas
     */
    public int getPartidasPerdidas() {
        return this.partidasPerdidas;
    }

    /**
     * Devuelve el total de puntos acumulados.
     *
     * @return los puntos actuales
     */
    public int getPuntos() {
        return this.puntos;
    }
}