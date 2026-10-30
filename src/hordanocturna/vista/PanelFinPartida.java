package hordanocturna.vista;

import hordanocturna.modelo.EstadisticasPartida;

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
import java.awt.event.ActionListener;

/** Pantalla de victoria o derrota con las estadisticas de la partida. */
public class PanelFinPartida extends JPanel {

    private final JLabel titulo = new JLabel();
    private final JLabel detalle = new JLabel();
    private final JButton botonReintentar = new JButton("Jugar de nuevo");
    private final JButton botonMenu = new JButton("Volver al menu");

    public PanelFinPartida() {
        setPreferredSize(new Dimension(PanelJuego.ANCHO, PanelJuego.ALTO));
        setBackground(new Color(12, 10, 20));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(140, 0, 40, 0));

        titulo.setFont(new Font(Font.SERIF, Font.BOLD, 56));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        detalle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        detalle.setForeground(new Color(220, 220, 230));
        detalle.setAlignmentX(Component.CENTER_ALIGNMENT);
        detalle.setHorizontalAlignment(JLabel.CENTER);
        detalle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        for (JButton b : new JButton[]{botonReintentar, botonMenu}) {
            b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(260, 46));
            b.setFocusPainted(false);
        }

        add(titulo);
        add(Box.createVerticalStrut(30));
        add(detalle);
        add(Box.createVerticalStrut(40));
        add(botonReintentar);
        add(Box.createVerticalStrut(12));
        add(botonMenu);
    }

    public void mostrar(EstadisticasPartida e) {
        if (e.isVictoria()) {
            titulo.setText("HA LLEGADO EL AMANECER");
            titulo.setForeground(new Color(255, 210, 110));
        } else {
            titulo.setText("LA NOCHE TE HA CONSUMIDO");
            titulo.setForeground(new Color(200, 50, 60));
        }
        detalle.setText("<html><center>"
                + "Tiempo sobrevivido: <b>" + PanelJuego.formatearTiempo(e.getTiempoSobrevivido()) + "</b><br>"
                + "Enemigos derrotados: <b>" + e.getEnemigosDerrotados() + "</b><br>"
                + "Nivel alcanzado: <b>" + e.getNivelAlcanzado() + "</b><br><br>"
                + "Armas: " + String.join(", ", e.getArmas())
                + "</center></html>");
    }

    public void addReintentarListener(ActionListener l) {
        botonReintentar.addActionListener(l);
    }

    public void addMenuListener(ActionListener l) {
        botonMenu.addActionListener(l);
    }
}
