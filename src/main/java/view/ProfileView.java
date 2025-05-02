package view;

import gestordeperfil.GestorDePerfil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProfileView extends JFrame {
    private final GestorDePerfil gestorDePerfil;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public ProfileView(GestorDePerfil gestorDePerfil) {
        super("Gestión de perfil");
        this.gestorDePerfil = gestorDePerfil;
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));;
        panel.setBorder(BorderFactory.createEmptyBorder(20, 300, 20, 300));

        addButton(panel, "Crear un nuevo perfil", this::nuevoPerfil);
        addButton(panel, "Eliminar perfil", this::eliminarPerfil);
        /*
        addButton(panel, "Change Password", this::changePassword);
        addButton(panel, "Reset Password", this::resetPassword);

         */
        addButton(panel, "Back", e -> dispose());

        add(panel);
    }

    private void addButton(JPanel panel, String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
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
}