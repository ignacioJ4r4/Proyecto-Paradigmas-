package hordanocturna.modelo;

/** Estados posibles de una partida. La partida solo avanza en JUGANDO. */
public enum EstadoPartida {
    JUGANDO,
    PAUSADA,
    VICTORIA,
    DERROTA;

    public boolean esFinal() {
        return this == VICTORIA || this == DERROTA;
    }
}
