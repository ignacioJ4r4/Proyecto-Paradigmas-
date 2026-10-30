package hordanocturna.modelo.enemigos;

/** Lento pero resistente. Camina en linea recta (usa el movimiento por defecto). */
public class Esqueleto extends Enemigo {

    public Esqueleto(double x, double y, double multiplicador) {
        super(TipoEnemigo.ESQUELETO, x, y, 13, 22 * multiplicador, 6 * multiplicador, 70, 2, 0.10);
    }
}
