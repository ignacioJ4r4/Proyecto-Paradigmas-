package hordanocturna.modelo;

/**
 * Clase base de todo objeto que existe dentro del mapa del juego.
 * Define posicion, tamano (radio de colision) y si sigue activa.
 * Es abstracta porque no existe una "entidad generica" en el juego:
 * siempre es un jugador, un enemigo, un proyectil, etc.
 */
public abstract class Entidad {

    protected double x;
    protected double y;
    protected final double radio;
    private boolean activa = true;

    protected Entidad(double x, double y, double radio) {
        this.x = x;
        this.y = y;
        this.radio = radio;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getRadio() {
        return radio;
    }

    public boolean isActiva() {
        return activa;
    }

    /** Marca la entidad para que la partida la elimine al final del ciclo. */
    public void desactivar() {
        activa = false;
    }

    public double distanciaA(Entidad otra) {
        return distanciaA(otra.x, otra.y);
    }

    public double distanciaA(double px, double py) {
        double dx = px - x;
        double dy = py - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** Colision circular: dos circulos se tocan si la distancia es menor que la suma de radios. */
    public boolean colisionaCon(Entidad otra) {
        return distanciaA(otra) < radio + otra.radio;
    }
}
