package hordanocturna.modelo.objetos;

import hordanocturna.modelo.Entidad;
import hordanocturna.modelo.Jugador;
import hordanocturna.modelo.Partida;

/**
 * Objeto que el jugador recoge al tocarlo (gemas, aceite...).
 * Si el jugador esta dentro de su radio de iman, el objeto vuela hacia el.
 */
public abstract class Recolectable extends Entidad {

    private static final double VELOCIDAD_ATRACCION = 380;

    protected Recolectable(double x, double y, double radio) {
        super(x, y, radio);
    }

    public void actualizar(double dt, Jugador jugador) {
        double distancia = distanciaA(jugador);
        if (distancia < jugador.getRadioIman() && distancia > 0) {
            x += (jugador.getX() - x) / distancia * VELOCIDAD_ATRACCION * dt;
            y += (jugador.getY() - y) / distancia * VELOCIDAD_ATRACCION * dt;
        }
    }

    /** Efecto que produce al ser recogido. Cada subclase define el suyo. */
    public abstract void aplicar(Partida partida);
}
