package gestordeperfil;

public class Perfil {
    private String username;
    private String password;
    private String recoveryPhrase;
    private int playedGames;
    private int wonGames;
    private int lostGames;
    private int points;

    //Constructor: initializes object
    public Perfil(String name, String pass, String recovery){
        this.username = name;
        this.password = pass;
        this.recoveryPhrase = recovery;
        this.playedGames = 0;
        this.wonGames = 0;
        this.lostGames = 0;
        this.points = 0;
    }

    //Sets

    public void changeUsername(String name){
        this.username = name;
    }

    public void changePassword(String pass){
        this.password = pass;
    }

    public void increasePlayedGames() {
        this.playedGames++;
    }

    public void increaseWonGames() {
        this.wonGames++;
    }

    public void increaseLostGames() {
        this.lostGames++;
    }

    public void increasePoints(int p) {
        this.points += p;
    }

    //Gets

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRecoveryPhrase() {
        return this.recoveryPhrase;
    }

    public int getPlayedGames() {
        return this.playedGames;
    }

    public int getWonGames() {
        return this.wonGames;
    }

    public int getLostGames() {
        return this.lostGames;
    }

    public int getPoints() {
        return this.points;
    }

}