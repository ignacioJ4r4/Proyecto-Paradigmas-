package hordanocturna.vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

/**
 * Ventana unica del juego. Usa CardLayout para cambiar entre
 * el menu, la partida y la pantalla final.
 */
public class VentanaPrincipal extends JFrame {

    public static final String MENU = "menu";
    public static final String JUEGO = "juego";
    public static final String FIN = "fin";

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);

    private final PanelMenu panelMenu = new PanelMenu();
    private final PanelJuego panelJuego = new PanelJuego();
    private final PanelFinPartida panelFin = new PanelFinPartida();

    public VentanaPrincipal() {
        super("Horda Nocturna: Sobrevive hasta el Amanecer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        contenedor.add(panelMenu, MENU);
        contenedor.add(panelJuego, JUEGO);
        contenedor.add(panelFin, FIN);
        setContentPane(contenedor);

        pack();
        setLocationRelativeTo(null);
    }

    public void mostrar(String pantalla) {
        cartas.show(contenedor, pantalla);
        if (JUEGO.equals(pantalla)) {
            panelJuego.requestFocusInWindow();   // para que reciba el teclado
        }
    }

    public PanelMenu getPanelMenu() {
        return panelMenu;
    }

    public PanelJuego getPanelJuego() {
        return panelJuego;
    }

    public PanelFinPartida getPanelFin() {
        return panelFin;
    }
}
