package hordanocturna.controlador;

import hordanocturna.modelo.EstadoPartida;
import hordanocturna.modelo.Partida;
import hordanocturna.vista.PanelJuego;

import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * CONTROLADOR de la partida.
 * - Traduce el teclado en llamadas al modelo (Partida).
 * - Ejecuta el ciclo del juego con un javax.swing.Timer (~60 cuadros por segundo):
 *   en cada tick actualiza el modelo y pide a la vista que se redibuje.
 */
public class ControladorJuego implements KeyListener {

    private static final int MILISEGUNDOS_POR_CUADRO = 16;
    private static final double DT_MAXIMO = 0.05;   // evita saltos grandes si el PC se pega

    private final Partida partida;
    private final PanelJuego vista;
    private final ControladorAplicacion aplicacion;
    private final Timer temporizador;

    private boolean arriba, abajo, izquierda, derecha;
    private long ultimoTick;

    public ControladorJuego(Partida partida, PanelJuego vista, ControladorAplicacion aplicacion) {
        this.partida = partida;
        this.vista = vista;
        this.aplicacion = aplicacion;
        this.temporizador = new Timer(MILISEGUNDOS_POR_CUADRO, e -> tick());
    }

    public void iniciar() {
        vista.setPartida(partida);
        vista.addKeyListener(this);
        ultimoTick = System.nanoTime();
        temporizador.start();
    }

    public void detener() {
        temporizador.stop();
        vista.removeKeyListener(this);
    }

    /** Un cuadro del ciclo principal del juego. */
    private void tick() {
        long ahora = System.nanoTime();
        double dt = Math.min(DT_MAXIMO, (ahora - ultimoTick) / 1_000_000_000.0);
        ultimoTick = ahora;

        partida.actualizar(dt);
        vista.repaint();

        if (partida.getEstado().esFinal()) {
            detener();
            aplicacion.mostrarFinDePartida(partida.getEstadisticas());
        }
    }

    private void actualizarDireccion() {
        double dx = (derecha ? 1 : 0) - (izquierda ? 1 : 0);
        double dy = (abajo ? 1 : 0) - (arriba ? 1 : 0);
        partida.setDireccionJugador(dx, dy);
    }

    // ------------------------------------------------------------ teclado

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                arriba = true;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                abajo = true;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                izquierda = true;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                derecha = true;
                break;
            case KeyEvent.VK_ESCAPE:
                partida.alternarPausa();
                break;
            case KeyEvent.VK_Q:
                if (partida.getEstado() == EstadoPartida.PAUSADA) {
                    detener();
                    aplicacion.volverAlMenu();
                }
                break;
            default:
                // otras teclas se ignoran
        }
        actualizarDireccion();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
            case KeyEvent.VK_UP:
                arriba = false;
                break;
            case KeyEvent.VK_S:
            case KeyEvent.VK_DOWN:
                abajo = false;
                break;
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                izquierda = false;
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                derecha = false;
                break;
            default:
        }
        actualizarDireccion();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // no se usa
    }
}
