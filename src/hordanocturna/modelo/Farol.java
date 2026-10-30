package hordanocturna.modelo;

/**
 * Mecanica distintiva: el farol del cazador.
 * Ilumina un radio alrededor del jugador que se reduce a medida que se consume el aceite.
 */
public class Farol {

    public static final double LUZ_MAXIMA = 100.0;

    private static final double RADIO_MINIMO = 70.0;
    private static final double RADIO_MAXIMO = 260.0;
    private static final double CONSUMO_POR_SEGUNDO = 1.6;

    private double luz = LUZ_MAXIMA;

    public void consumir(double dt) {
        luz = Math.max(0, luz - CONSUMO_POR_SEGUNDO * dt);
    }

    public void recargar(double cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La recarga de aceite no puede ser negativa");
        }
        luz = Math.min(LUZ_MAXIMA, luz + cantidad);
    }

    /** El radio iluminado depende de cuanto aceite queda. */
    public double getRadioActual() {
        return RADIO_MINIMO + (RADIO_MAXIMO - RADIO_MINIMO) * (luz / LUZ_MAXIMA);
    }

    public double getLuz() {
        return luz;
    }

    public double getPorcentaje() {
        return luz / LUZ_MAXIMA;
    }
}
