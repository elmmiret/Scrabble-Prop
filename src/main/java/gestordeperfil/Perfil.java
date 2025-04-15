package gestordeperfil;

/**
 * Represents a user profile containing account information and game statistics.
 * This class manages the username, password, recovery phrase, and tracks
 * the number of games played, won, lost, and accumulated points for each profile in the system.
 *
 * @author Marc Ribas Acon
 */
public class Perfil {
    private String username;
    private String password;
    private String recoveryPhrase;
    private int playedGames;
    private int wonGames;
    private int lostGames;
    private int points;

    /**
     * Constructs a new Perfil with the specified username, password, and recovery phrase.
     * Initializes the game statistics (played, won, lost games, and points) to zero.
     *
     * @param name     the username for the profile
     * @param pass     the password for the profile
     * @param recovery the recovery phrase for account recovery
     */
    public Perfil(String name, String pass, String recovery) {
        this.username = name;
        this.password = pass;
        this.recoveryPhrase = recovery;
        this.playedGames = 0;
        this.wonGames = 0;
        this.lostGames = 0;
        this.points = 0;
    }

    /**
     * Sets the username of the profile to the specified value.
     *
     * @param name the new username to set
     */
    public void changeUsername(String name) {
        this.username = name;
    }

    /**
     * Sets the password of the profile to the specified value.
     *
     * @param pass the new password to set
     */
    public void changePassword(String pass) {
        this.password = pass;
    }

    /**
     * Increments the count of played games by one.
     */
    public void increasePlayedGames() {
        this.playedGames++;
    }

    /**
     * Increments the count of won games by one.
     */
    public void increaseWonGames() {
        this.wonGames++;
    }

    /**
     * Increments the count of lost games by one.
     */
    public void increaseLostGames() {
        this.lostGames++;
    }

    /**
     * Adds the specified number of points to the current total.
     *
     * @param p the points to add
     */
    public void increasePoints(int p) {
        this.points += p;
    }

    /**
     * Returns the username of the profile.
     *
     * @return the current username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the password of the profile.
     *
     * @return the current password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the recovery phrase of the profile.
     *
     * @return the recovery phrase
     */
    public String getRecoveryPhrase() {
        return this.recoveryPhrase;
    }

    /**
     * Returns the number of games played.
     *
     * @return the count of played games
     */
    public int getPlayedGames() {
        return this.playedGames;
    }

    /**
     * Returns the number of games won.
     *
     * @return the count of won games
     */
    public int getWonGames() {
        return this.wonGames;
    }

    /**
     * Returns the number of games lost.
     *
     * @return the count of lost games
     */
    public int getLostGames() {
        return this.lostGames;
    }

    /**
     * Returns the total points accumulated.
     *
     * @return the current points
     */
    public int getPoints() {
        return this.points;
    }
}