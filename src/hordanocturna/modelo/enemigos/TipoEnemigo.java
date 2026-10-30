package hordanocturna.modelo.enemigos;

/** Identifica el tipo de criatura (lo usa la vista para dibujarla y el generador de oleadas). */
public enum TipoEnemigo {
    MURCIELAGO,
    ESQUELETO;

    /**
     * Fabrica de enemigos (patron Factory).
     * @param multiplicador escala la vida y el dano segun el minuto de la partida
     */
    public Enemigo crear(double x, double y, double multiplicador) {
        switch (this) {
            case MURCIELAGO:
                return new Murcielago(x, y, multiplicador);
            case ESQUELETO:
                return new Esqueleto(x, y, multiplicador);
            default:
                throw new IllegalStateException("Enemigo no implementado: " + this);
        }
    }
}
