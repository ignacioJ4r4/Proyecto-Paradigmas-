package hordanocturna.controlador;

import hordanocturna.modelo.EstadisticasPartida;
import hordanocturna.modelo.Partida;
import hordanocturna.vista.VentanaPrincipal;

/**
 * Controlador de navegacion: conecta los botones de los menus
 * y decide que pantalla se muestra en cada momento.
 */
public class ControladorAplicacion {

    private final VentanaPrincipal ventana;
    private ControladorJuego controladorJuego;

    public ControladorAplicacion(VentanaPrincipal ventana) {
        this.ventana = ventana;

        ventana.getPanelMenu().addJugarListener(e -> iniciarPartida());
        ventana.getPanelMenu().addSalirListener(e -> System.exit(0));
        ventana.getPanelFin().addReintentarListener(e -> iniciarPartida());
        ventana.getPanelFin().addMenuListener(e -> volverAlMenu());
    }

    public void iniciar() {
        ventana.mostrar(VentanaPrincipal.MENU);
        ventana.setVisible(true);
    }

    public void iniciarPartida() {
        if (controladorJuego != null) {
            controladorJuego.detener();
        }
        controladorJuego = new ControladorJuego(new Partida(), ventana.getPanelJuego(), this);
        ventana.mostrar(VentanaPrincipal.JUEGO);
        controladorJuego.iniciar();
    }

    public void mostrarFinDePartida(EstadisticasPartida estadisticas) {
        ventana.getPanelFin().mostrar(estadisticas);
        ventana.mostrar(VentanaPrincipal.FIN);
        // TODO etapa 4: guardar records y monedas con la capa de persistencia
    }

    public void volverAlMenu() {
        ventana.mostrar(VentanaPrincipal.MENU);
    }
}
