package hordanocturna.modelo.armas;

/**
 * Tipos de arma disponibles. Funciona como Fabrica (patron Factory):
 * cada constante sabe crear su propia instancia de Arma.
 * (Version beta: solo la Daga. El resto de las armas llega en el siguiente avance.)
 */
public enum TipoArma {
    DAGA("Daga");

    private final String nombre;

    TipoArma(String nombre) {
        this.nombre = nombre;
    }

    public Arma crear() {
        switch (this) {
            case DAGA:
                return new Daga();
            default:
                throw new IllegalStateException("Arma no implementada: " + this);
        }
    }

    public String getNombre() {
        return nombre;
    }
}
