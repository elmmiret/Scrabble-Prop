package view;

import gestordeperfil.GestorDePerfil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;

public class GestionPerfilView extends JFrame {
    private static final int ANCHO = 400;
    private static final int ALTO = 700;
    private final Color COLOR_AZUL = new Color(40, 100, 240);
    private final Color COLOR_ROJO = new Color(220, 50, 40);
    private final Color COLOR_NARANJA = new Color(240, 73, 48);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    private final GestorDeView gestorDeView;
    private final GestorDePerfil gestorDePerfil;

    public GestionPerfilView(GestorDeView gestorDeView, GestorDePerfil gestorDePerfil) {
        super("Gestión de perfil");
        this.gestorDeView = gestorDeView;
        this.gestorDePerfil = gestorDePerfil;
        init();
    }

    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setLocationRelativeTo(null);
        setResizable(false);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(ANCHO, ALTO));

        // Añadir tablero con animacion
        TableroMoviendo tableroMoviendo = new TableroMoviendo();
        tableroMoviendo.setBounds(0, 0, ANCHO, ALTO);
        layeredPane.add(tableroMoviendo, JLayeredPane.DEFAULT_LAYER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50));

        JLabel titleLabel = new JLabel("GESTIÓN DE PERFIL");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(COLOR_NARANJA);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 25));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        buttonPanel.setOpaque(false);

        addPerfilButton(buttonPanel, "Crear nuevo perfil", COLOR_AZUL, this::nuevoPerfil);
        addPerfilButton(buttonPanel, "Eliminar perfil", COLOR_AZUL, this::eliminarPerfil);
        addPerfilButton(buttonPanel, "Cambiar password", COLOR_AZUL, this::cambiarPassword);
        addPerfilButton(buttonPanel, "Restablecer password", COLOR_AZUL, this::restablecerPassword);
        addPerfilButton(buttonPanel, "Cambiar username", COLOR_AZUL, this::cambiarUsername);
        addPerfilButton(buttonPanel, "Atrás", COLOR_ROJO, e -> gestorDeView.mostrarMain());

        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.setBounds(0, 0, ANCHO, ALTO);
        layeredPane.add(panel, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);
        tableroMoviendo.iniciarMovimiento();
    }

    private void addPerfilButton(JPanel panel, String text, Color color, java.awt.event.ActionListener action) {
        JButton button = new JButton(text) {
            private boolean isHovering = false;
            private final int radioEsquina = 35;
            private final BasicStroke grosorBorde = new BasicStroke(3f); // Thicker border
            private final Color colorBordeNormal = color.brighter();
            private final Color colorBordeHover = color.darker().darker();

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background
                Color finalColor = isHovering ? color.darker() : color;
                g2d.setColor(finalColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radioEsquina, radioEsquina);

                // Border
                g2d.setStroke(grosorBorde);
                g2d.setColor(isHovering ? colorBordeHover : colorBordeNormal);
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, radioEsquina, radioEsquina);

                // Text
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2d);
                g2d.drawString(getText(),
                        (int)((getWidth() - r.getWidth()) / 2),
                        (int)((getHeight() - r.getHeight()) / 2 + fm.getAscent())
                );
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
        panel.add(button);
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