package hordanocturna.modelo.armas;

import hordanocturna.modelo.Partida;

/**
 * Clase abstracta para todas las armas.
 * Implementa el "esqueleto" comun (patron Template Method): cada arma espera su
 * tiempo de recarga y luego ataca. COMO ataca lo define cada subclase (polimorfismo).
 */
public abstract class Arma {

    public static final int NIVEL_MAXIMO = 5;

    private final TipoArma tipo;
    protected int nivel = 1;
    private double recarga = 0;

    protected Arma(TipoArma tipo) {
        this.tipo = tipo;
    }

    /** Metodo plantilla: igual para todas las armas. */
    public final void actualizar(double dt, Partida partida) {
        recarga -= dt;
        if (recarga <= 0) {
            boolean ataco = atacar(partida);
            // si no habia objetivo, se reintenta pronto en vez de esperar la recarga completa
            recarga = ataco ? getTiempoRecarga() : 0.1;
        }
    }

    /** Ejecuta el ataque. Devuelve true si efectivamente ataco. */
    protected abstract boolean atacar(Partida partida);

    protected abstract double getTiempoRecarga();

    public String getNombre() {
        return tipo.getNombre();
    }

    public TipoArma getTipo() {
        return tipo;
    }

    public int getNivel() {
        return nivel;
    }
}
