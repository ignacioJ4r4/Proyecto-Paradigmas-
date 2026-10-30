package hordanocturna;

import hordanocturna.controlador.ControladorAplicacion;
import hordanocturna.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

/** Punto de entrada: crea la ventana y el controlador en el hilo de Swing. */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            new ControladorAplicacion(ventana).iniciar();
        });
    }
}
