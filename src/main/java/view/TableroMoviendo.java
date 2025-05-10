package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TableroMoviendo extends JPanel {
    private static final Color BORDE_TABLERO_COLOR = new Color(255, 80, 80, 60);
    private static final int ESPACIO_TABLERO = 40;
    private int offsetX = 0;
    private int offsetY = 0;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(BORDE_TABLERO_COLOR);
        for (int x = offsetX; x < getWidth(); x += ESPACIO_TABLERO) {
            g2d.drawLine(x, 0, x, getHeight());
        }
        for (int y = offsetY; y < getHeight(); y += ESPACIO_TABLERO) {
            g2d.drawLine(0, y, getWidth(), y);
        }
        g2d.dispose();
    }

    public void iniciarMovimiento() {
        Timer timer = new Timer(50, e -> {
            // Mover posicion del tablero
            offsetX = (offsetX + 1) % ESPACIO_TABLERO;
            offsetY = (offsetY + 1) % ESPACIO_TABLERO;
            repaint();
        });
        timer.start();
    }
}