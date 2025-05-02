package view;

import gestordeperfil.GestorDePerfil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProfileView extends JFrame {
    private final GestorDeView gestorDeView;
    private final GestorDePerfil gestorDePerfil;
    private static final int WIDTH = 300;
    private static final int HEIGHT = 600;

    public ProfileView(GestorDeView gestorDeView, GestorDePerfil gestorDePerfil) {
        super("Gestión de perfil");
        this.gestorDeView = gestorDeView;
        this.gestorDePerfil = gestorDePerfil;
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));;
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        addButton(panel, "Crear un nuevo perfil", this::nuevoPerfil);
        addButton(panel, "Eliminar perfil", this::eliminarPerfil);
        addButton(panel, "Cambiar password", this::cambiarPassword);
        addButton(panel, "Restablecer password", this::restablecerPassword);
        addButton(panel, "Cambiar username", this::cambiarUsername);
        addButton(panel, "Atrás", e -> gestorDeView.mostrarMain());

        add(panel);
    }

    private void addButton(JPanel panel, String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(button);
    }

    private void nuevoPerfil(ActionEvent e)
    {
        // Implement GUI form for profile creation
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField passwordField2 = new JPasswordField();
        JTextField fraseRecuperacionField = new JTextField();

        Object[] message = {
                "Username:", usernameField,
                "Password (mínimo 8 carácteres, 1 mayúscula y 1 número):", passwordField,
                "Password otra vez:", passwordField2,
                "Cuál es tu color favorito? (Frase de recuperación):", fraseRecuperacionField,
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Crear nuevo perfil", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String password2 = new String(passwordField2.getPassword());
            String fraseRecuperacion = fraseRecuperacionField.getText();

            if (!gestorDePerfil.existeJugador(username)) {
                if (gestorDePerfil.esPasswordSegura(password)) {
                    if (password.equals(password2))
                    {
                        gestorDePerfil.crearPerfil(username, password, fraseRecuperacion);
                        JOptionPane.showMessageDialog(this, "Perfil creado correctamente");
                    } else JOptionPane.showMessageDialog(this, "Las passwords no coinciden");
                } else JOptionPane.showMessageDialog(this, "La password no cumple los requisitos mínimos de seguridad (mínimo 8 carácteres, 1 mayúscula y 1 número)");
            } else JOptionPane.showMessageDialog(this, "Este username ya está en uso");
        }
    }

    private void eliminarPerfil(ActionEvent e)
    {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField,
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Eliminar perfil", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION)
        {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (gestorDePerfil.existeJugador(username))
            {
                if (gestorDePerfil.esPasswordCorrecta(username, password))
                {
                    Object[] message2 = {"Seguro que quieres eliminar tu perfil?"};
                    int option2 = JOptionPane.showConfirmDialog(this, message2,"", JOptionPane.OK_CANCEL_OPTION);
                    if (option2 == JOptionPane.OK_OPTION)
                    {
                        gestorDePerfil.eliminarPerfil(username);
                        JOptionPane.showMessageDialog(this, "Perfil eliminado correctamente");
                    }
                } else JOptionPane.showMessageDialog(this, "Password incorrecta");

            } else JOptionPane.showMessageDialog(this, "No existe ningún perfil con este username");
        }
    }

    private void cambiarPassword(ActionEvent e)
    {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField newPasswordField2 = new JPasswordField();

        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField,
                "Nueva password (mínimo 8 carácteres, 1 mayúscula y 1 número): ", newPasswordField,
                "Nueva password otra vez:", newPasswordField2,
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Cambiar password", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION)
        {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String newPassword2 = new String(newPasswordField2.getPassword());

            if (gestorDePerfil.existeJugador(username))
            {
                if (gestorDePerfil.esPasswordCorrecta(username, password))
                {
                    if (gestorDePerfil.esPasswordSegura(newPassword))
                    {
                        if (!password.equals(newPassword)) {
                            if (newPassword.equals(newPassword2))
                            {
                                gestorDePerfil.cambiarPassword(username, newPassword);
                                JOptionPane.showMessageDialog(this, "Password cambiada correctamente");
                            } else JOptionPane.showMessageDialog(this, "Las passwords no coinciden");
                        } else JOptionPane.showMessageDialog(this, "La password antigua y la nueva son iguales");
                    } else JOptionPane.showMessageDialog(this, "La nueva password no cumple los requisitos mínimos de seguridad (mínimo 8 carácteres, 1 mayúscula y 1 número)");
                } else JOptionPane.showMessageDialog(this, "Password incorrecta");
            } else JOptionPane.showMessageDialog(this, "No existe ningún perfil con este username");
        }
    }

    private void restablecerPassword(ActionEvent e)
    {
        JTextField usernameField = new JTextField();
        JTextField fraseRecuperacionField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField newPasswordField2 = new JPasswordField();

        Object[] message = {
                "Username:", usernameField,
                "Cuál es tu color favorito? (Frase de recuperación):", fraseRecuperacionField,
                "Nueva password (mínimo 8 carácteres, 1 mayúscula y 1 número): ", newPasswordField,
                "Nueva password otra vez:", newPasswordField2,
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Restablecer password", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION)
        {
            String username = usernameField.getText();
            String fraseRecuperacion = fraseRecuperacionField.getText();
            String newPassword = new String(newPasswordField.getPassword());
            String newPassword2 = new String(newPasswordField2.getPassword());

            if (gestorDePerfil.existeJugador(username))
            {
                if (gestorDePerfil.esFraseRecuperacionCorrecta(username, fraseRecuperacion))
                {
                    if (gestorDePerfil.esPasswordSegura(newPassword))
                    {
                        if (newPassword.equals(newPassword2))
                        {
                            gestorDePerfil.cambiarPassword(username, newPassword);
                            JOptionPane.showMessageDialog(this, "Password restablecida correctamente");
                        } else JOptionPane.showMessageDialog(this, "Las nuevas passwords no coinciden");
                    } else JOptionPane.showMessageDialog(this, "La nueva password no cumple los requisitos mínimos de seguridad (mínimo 8 carácteres, 1 mayúscula y 1 número)");
                } else JOptionPane.showMessageDialog(this, "Frase de recuperación incorrecta");
            } else JOptionPane.showMessageDialog(this, "No existe ningún perfil con este username");
        }
    }

    private void cambiarUsername(ActionEvent e)
    {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField newUsernameField = new JTextField();
        JTextField newUsername2Field = new JTextField();

        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField,
                "Nuevo username: ", newUsernameField,
                "Nuevo username otra vez", newUsername2Field,
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Cambiar username", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION)
        {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String newUsername = newUsernameField.getText();
            String newUsername2 = newUsername2Field.getText();

            if (gestorDePerfil.existeJugador(username))
            {
                if (gestorDePerfil.esPasswordCorrecta(username, password))
                {
                    if (!gestorDePerfil.existeJugador(newUsername))
                    {
                        if (!newUsername.equals(username))
                        {
                            if (newUsername.equals(newUsername2))
                            {
                                gestorDePerfil.cambiarUsername(username, newUsername);
                                JOptionPane.showMessageDialog(this, "Username cambiado correctamente");
                            } else JOptionPane.showMessageDialog(this, "Los nuevos usernames no coinciden");
                        } else JOptionPane.showMessageDialog(this, "El username antiguo y el nuevo son iguales");
                    } else JOptionPane.showMessageDialog(this, "El nuevo username ya está en uso");
                } else JOptionPane.showMessageDialog(this, "Password incorrecta");
            } else JOptionPane.showMessageDialog(this, "No existe ningún perfil con este username");
        }
    }
}