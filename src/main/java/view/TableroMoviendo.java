package view;

import javax.swing.*;
import java.awt.*;

/**
 * Clase que representa un panel gráfico con un efecto visual de tablero en movimiento.
 * Dibuja una cuadrícula animada con líneas que se desplazan continuamente.
 * Utilizado como fondo dinámico en diversas vistas de la aplicación.
 *
 * @author Marc Ribas Acon
 */

public class TableroMoviendo extends JPanel {
    /** Color semitransparente para las líneas del tablero */
    private static final Color BORDE_TABLERO_COLOR = new Color(255, 80, 80, 60);
    /** Espaciado entre las líneas de la cuadrícula */
    private static final int ESPACIO_TABLERO = 40;
    /** Desplazamiento horizontal actual de las líneas */
    private int offsetX = 0;
    /** Desplazamiento vertical actual de las líneas */
    private int offsetY = 0;

    /**
     * Dibuja la cuadrícula animada en el panel.
     * Las líneas se actualizan según los desplazamientos almacenados en offsetX y offsetY.
     *
     * @param g Contexto gráfico para el renderizado
     */
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

    /**
     * Inicia la animación del tablero.
     * Utiliza un temporizador para actualizar periódicamente la posición de las líneas,
     * creando un efecto de movimiento continuo.
     */
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