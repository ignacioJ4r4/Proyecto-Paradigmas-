package hordanocturna.modelo.enemigos;

import hordanocturna.modelo.Entidad;
import hordanocturna.modelo.Jugador;

/**
 * Clase base de las criaturas de la noche.
 * Todas persiguen al jugador, pero cada subclase define su forma de moverse
 * sobrescribiendo calcularVelocidad() (polimorfismo).
 */
public abstract class Enemigo extends Entidad {

    private static final double DURACION_PARPADEO = 0.1;
    private static final double EMPUJE = 6;

    private final TipoEnemigo tipo;
    private double vida;
    private final double vidaMaxima;
    private final double dano;
    protected final double velocidad;
    private final int experiencia;
    private final double probabilidadAceite;

    protected double tiempoVivo = 0;
    private double parpadeo = 0;

    protected Enemigo(TipoEnemigo tipo, double x, double y, double radio, double vida, double dano,
                      double velocidad, int experiencia, double probabilidadAceite) {
        super(x, y, radio);
        this.tipo = tipo;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.dano = dano;
        this.velocidad = velocidad;
        this.experiencia = experiencia;
        this.probabilidadAceite = probabilidadAceite;
    }

    public void actualizar(double dt, Jugador jugador) {
        tiempoVivo += dt;
        if (parpadeo > 0) {
            parpadeo -= dt;
        }
        double dx = jugador.getX() - x;
        double dy = jugador.getY() - y;
        double distancia = Math.sqrt(dx * dx + dy * dy);
        if (distancia < 1) {
            return;
        }
        double[] v = calcularVelocidad(dx / distancia, dy / distancia, dt);
        x += v[0] * dt;
        y += v[1] * dt;
    }

    /**
     * Devuelve la velocidad {vx, vy} dada la direccion unitaria hacia el jugador.
     * Por defecto va en linea recta; las subclases pueden cambiarlo.
     */
    protected double[] calcularVelocidad(double haciaX, double haciaY, double dt) {
        return new double[]{haciaX * velocidad, haciaY * velocidad};
    }

    /**
     * Aplica dano y un pequeno empuje alejandolo del jugador.
     * @return true si el enemigo murio con este golpe
     */
    public boolean recibirDano(double cantidad, Jugador jugador) {
        if (!isActiva()) {
            return false;
        }
        vida -= cantidad;
        parpadeo = DURACION_PARPADEO;
        double d = distanciaA(jugador);
        if (d > 0) {
            x += (x - jugador.getX()) / d * EMPUJE;
            y += (y - jugador.getY()) / d * EMPUJE;
        }
        if (vida <= 0) {
            desactivar();
            return true;
        }
        return false;
    }

    public TipoEnemigo getTipo() {
        return tipo;
    }

    public double getVida() {
        return vida;
    }

    public double getVidaMaxima() {
        return vidaMaxima;
    }

    public double getDano() {
        return dano;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public double getProbabilidadAceite() {
        return probabilidadAceite;
    }

    public boolean estaParpadeando() {
        return parpadeo > 0;
    }

    public double getTiempoVivo() {
        return tiempoVivo;
    }
}
