package gestordeperfil;

//this class manages the creation, elimination and logs into the database of the profiles
public class GestorDePerfil {

    public Perfil createPerfil(String name, String pass, String recovery){
        return new Perfil(name, pass, recovery);
    }

    private boolean correctCredentials(Perfil perfil, String name, String pass)
    {
        String perfName = perfil.getUsername();
        if (!perfName.equals(name)) return false;
        String perfPass = perfil.getPassword();
        if (!perfPass.equals(pass)) return false;
        return true;
    }

    //return true if oldName and the actual name of the profile are the same and the password is correct, meaning that
    // the process turned ok
    //return false if oldName and the actual name of the profile dont match, if the password is incorrect or the old and
    //new usernames are the same
    public boolean changeUsername(Perfil perfil, String oldName, String newName, String password) {
        if (!correctCredentials(perfil, oldName, password)) return false;
        if (oldName.equals(newName)) return false;
        perfil.changeUsername(newName);
        return true;
    }

    //return true if the password was changed without any problems
    //return false if the username and the name dont match, if the oldPassword does not match wit the actual password
    //or if the new and old passwords are the same
    public boolean changePassword(Perfil perfil, String name, String oldPassword, String newPassword) {
        if (!correctCredentials(perfil, name, oldPassword)) return false;
        if (oldPassword.equals(newPassword)) return false;
        perfil.changePassword(newPassword);
        return true;
    }

    public boolean reestablishPassword(Perfil perfil, String recovery, String password) {
        String perfilRecovery = perfil.getRecoveryPhrase();
        if (!perfilRecovery.equalsIgnoreCase(recovery)) return false;
        perfil.changePassword(password);
        return true;
    }
}