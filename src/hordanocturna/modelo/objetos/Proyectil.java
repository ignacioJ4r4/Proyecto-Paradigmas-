package hordanocturna.modelo.objetos;

import hordanocturna.modelo.Entidad;
import hordanocturna.modelo.enemigos.Enemigo;

/**
 * Proyectil lanzado por un arma. Se mueve en linea recta con velocidad constante
 * hasta que se agota su duracion o golpea a un enemigo.
 */
public class Proyectil extends Entidad {

    private final double vx;
    private final double vy;
    private final double dano;
    private double tiempoRestante;

    public Proyectil(double x, double y, double vx, double vy, double dano, double duracion) {
        super(x, y, 7);
        this.vx = vx;
        this.vy = vy;
        this.dano = dano;
        this.tiempoRestante = duracion;
    }

    public void actualizar(double dt) {
        x += vx * dt;
        y += vy * dt;
        tiempoRestante -= dt;
        if (tiempoRestante <= 0) {
            desactivar();
        }
    }

    public boolean puedeGolpear(Enemigo enemigo) {
        return isActiva() && colisionaCon(enemigo);
    }

    /** Al golpear un enemigo el proyectil desaparece. */
    public void registrarImpacto() {
        desactivar();
    }

    public double getDano() {
        return dano;
    }

    public double getAngulo() {
        return Math.atan2(vy, vx);
    }
}
