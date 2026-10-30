package hordanocturna.modelo.objetos;

import hordanocturna.modelo.Partida;

/** Frasco de aceite que recarga el farol. Lo sueltan algunos enemigos. */
public class FrascoAceite extends Recolectable {

    public static final double CANTIDAD = 30;

    public FrascoAceite(double x, double y) {
        super(x, y, 8);
    }

    @Override
    public void aplicar(Partida partida) {
        partida.getJugador().getFarol().recargar(CANTIDAD);
    }
}
