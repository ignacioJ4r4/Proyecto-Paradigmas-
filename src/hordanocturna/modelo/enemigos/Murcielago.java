package hordanocturna.modelo.enemigos;

/** Rapido y debil. Avanza en zigzag hacia el jugador. */
public class Murcielago extends Enemigo {

    public Murcielago(double x, double y, double multiplicador) {
        super(TipoEnemigo.MURCIELAGO, x, y, 10, 8 * multiplicador, 5 * multiplicador, 115, 1, 0.05);
    }

    @Override
    protected double[] calcularVelocidad(double haciaX, double haciaY, double dt) {
        // componente perpendicular que oscila con el tiempo -> zigzag
        double oscilacion = Math.sin(tiempoVivo * 6) * 0.8;
        double vx = (haciaX - haciaY * oscilacion) * velocidad;
        double vy = (haciaY + haciaX * oscilacion) * velocidad;
        return new double[]{vx, vy};
    }
}
