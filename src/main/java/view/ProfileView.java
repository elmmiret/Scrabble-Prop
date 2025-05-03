package view;

import gestordeperfil.GestorDePerfil;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionEvent;

public class ProfileView extends JFrame {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 700;
    private final Color COLOR_FONDO = new Color(36, 36, 36);
    private final Color COLOR_AZUL = new Color(15, 100, 150);
    private final Color COLOR_ROJO = new Color(150, 30, 20);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    private final GestorDeView gestorDeView;
    private final GestorDePerfil gestorDePerfil;

    public ProfileView(GestorDeView gestorDeView, GestorDePerfil gestorDePerfil) {
        super("Gestión de perfil");
        this.gestorDeView = gestorDeView;
        this.gestorDePerfil = gestorDePerfil;
        init();
        setTheme();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50));
        mainPanel.setBackground(COLOR_FONDO);

        JLabel titleLabel = new JLabel("Gestión de Perfil");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 25));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        buttonPanel.setBackground(COLOR_FONDO);

        addButton(buttonPanel, "Crear nuevo perfil", COLOR_AZUL, this::nuevoPerfil);
        addButton(buttonPanel, "Eliminar perfil", COLOR_AZUL, this::eliminarPerfil);
        addButton(buttonPanel, "Cambiar password", COLOR_AZUL, this::cambiarPassword);
        addButton(buttonPanel, "Restablecer password", COLOR_AZUL, this::restablecerPassword);
        addButton(buttonPanel, "Cambiar username", COLOR_AZUL, this::cambiarUsername);
        addButton(buttonPanel, "Atrás", COLOR_ROJO, e -> gestorDeView.mostrarMain());

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void addButton(JPanel panel, String text, Color color, java.awt.event.ActionListener action) {
        JButton button = new JButton(text) {
            private boolean isHovering = false;
            private final int radioEsquina = 35;
            private final BasicStroke grosorBorde = new BasicStroke(2f);
            private final Color colorBorde = color.brighter().brighter();

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                Color finalColor = isHovering ? color.darker() : color;
                g2d.setColor(finalColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquina, radioEsquina);

                if (isHovering) {
                    g2d.setStroke(grosorBorde);
                    g2d.setColor(colorBorde);
                    g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, radioEsquina, radioEsquina);
                }

                g2d.setColor(getForeground());
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(this.getText(), g2d);
                int x = (this.getWidth() - (int) r.getWidth()) / 2;
                int y = (this.getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(this.getText(), x, y);

                g2d.dispose();
            }

            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        isHovering = true;
                        repaint();
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        isHovering = false;
                        repaint();
                    }
                });
            }
        };

        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        button.setFocusPainted(false);
        button.setFocusable(false);

        panel.add(button);
    }

    private void setTheme() {
        try {
            UIManager.put("OptionPane.background", COLOR_FONDO);
            UIManager.put("Panel.background", COLOR_FONDO);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Button.background", COLOR_AZUL);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Label.foreground", Color.WHITE);
        } catch (Exception ignored) {}
    }

    private void nuevoPerfil(ActionEvent e)
    {
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