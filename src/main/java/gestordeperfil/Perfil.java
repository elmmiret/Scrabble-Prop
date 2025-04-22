package gestordeperfil;

/**
 * Representa un perfil de usuario que contiene información de la cuenta y estadísticas de juego.
 * Esta clase gestiona el nombre de usuario, password, frase de recuperación y realiza un seguimiento
 * del número de partidas jugadas, ganadas, perdidas y los puntos acumulados para cada perfil en el sistema.
 *
 * @author Marc Ribas Acon
 */
public class Perfil {

    /**
     * Nombre único que identifica al usuario en el sistema. Se utiliza como clave principal
     * para todas las operaciones relacionadas con el perfil.
     */
    private String username;

    /**
     * Contraseña de acceso asociada al perfil. Almacenada de forma privada para garantizar
     * la seguridad de la autenticación.
     */
    private String password;

    /**
     * Frase secreta utilizada para procesos de recuperación de cuenta. La verificación
     * se realiza de forma case-insensitive.
     */
    private String fraseRecuperacion;

    /**
     * Contador acumulativo del total de partidas jugadas por el usuario en todas las
     * modalidades de juego disponibles.
     */
    private int partidasJugadas;

    /**
     * Registro de partidas donde el usuario ha obtenido la victoria según las reglas
     * establecidas en cada modalidad de juego.
     */
    private int partidasGanadas;

    /**
     * Contador de partidas donde el usuario no ha cumplido los objetivos requeridos
     * para la victoria en la correspondiente modalidad de juego.
     */
    private int partidasPerdidas;

    /**
     * Puntuación global acumulada a través de diferentes logros y rendimiento en
     * partidas. Utilizada para posicionamiento en rankings.
     */
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
     * Incrementa el contador de partidas jugadas según el parámetro especificado.
     *
     * @param nuevasPartidas número de partidas nuevas jugadas a sumar
     */
    public void incrementarPartidasJugadas(int nuevasPartidas) {
        partidasJugadas += nuevasPartidas;
    }

    /**
     * Incrementa el contador de partidas ganadas según el parámetro especificado.
     *
     * @param nuevasVictorias número de victorias nuevas a sumar
     */
    public void incrementarPartidasGanadas(int nuevasVictorias) {
        partidasGanadas += nuevasVictorias;
    }

    /**
     * Incrementa el contador de partidas perdidas según el parámetro especificado.
     *
     * @param nuevasDerrotas número de derrotas nuevas a sumar
     */
    public void incrementarPartidasPerdidas(int nuevasDerrotas) {
        this.partidasPerdidas += nuevasDerrotas;
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