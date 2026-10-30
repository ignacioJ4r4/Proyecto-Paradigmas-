package hordanocturna.vista;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;

/** Menu principal. Solo muestra botones; las acciones las asigna el controlador. */
public class PanelMenu extends JPanel {

    private final JButton botonJugar = crearBoton("Nueva partida");
    private final JButton botonSalir = crearBoton("Salir");

    public PanelMenu() {
        setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(110, 0, 40, 0));

        JLabel titulo = new JLabel("HORDA NOCTURNA");
        titulo.setFont(new Font(Font.SERIF, Font.BOLD, 64));
        titulo.setForeground(new Color(255, 200, 110));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Sobrevive hasta el amanecer");
        subtitulo.setFont(new Font(Font.SERIF, Font.ITALIC, 24));
        subtitulo.setForeground(new Color(200, 200, 220));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] lineasAyuda = {
                "WASD para moverte - las armas atacan solas",
                "Recoge gemas para subir de nivel y aceite para mantener encendido tu farol",
                "Version beta - Avance 1"
        };

        add(titulo);
        add(Box.createVerticalStrut(8));
        add(subtitulo);
        add(Box.createVerticalStrut(60));
        add(botonJugar);
        add(Box.createVerticalStrut(14));
        add(botonSalir);
        add(Box.createVerticalGlue());
        for (String linea : lineasAyuda) {
            JLabel ayuda = new JLabel(linea);
            ayuda.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            ayuda.setForeground(new Color(170, 170, 190));
            ayuda.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(ayuda);
            add(Box.createVerticalStrut(4));
        }
    }

    private static JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(300, 48));
        boton.setFocusPainted(false);
        return boton;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, new Color(10, 10, 30), 0, getHeight(), new Color(30, 15, 25)));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(240, 240, 220));
        g2.fillOval(getWidth() - 140, 30, 80, 80);   // luna
        g2.setColor(new Color(10, 10, 30));
        g2.fillOval(getWidth() - 122, 20, 80, 80);
    }

    public void addJugarListener(ActionListener l) {
        botonJugar.addActionListener(l);
    }

    public void addSalirListener(ActionListener l) {
        botonSalir.addActionListener(l);
    }
}
