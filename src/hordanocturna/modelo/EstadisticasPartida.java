package hordanocturna.modelo;

import java.util.List;

/**
 * Resumen inmutable de una partida terminada.
 * Es lo que se muestra en la pantalla final y lo que se guardara como record.
 */
public final class EstadisticasPartida {

    private final boolean victoria;
    private final double tiempoSobrevivido;
    private final int enemigosDerrotados;
    private final int nivelAlcanzado;
    private final List<String> armas;

    public EstadisticasPartida(boolean victoria, double tiempoSobrevivido, int enemigosDerrotados,
                               int nivelAlcanzado, List<String> armas) {
        this.victoria = victoria;
        this.tiempoSobrevivido = tiempoSobrevivido;
        this.enemigosDerrotados = enemigosDerrotados;
        this.nivelAlcanzado = nivelAlcanzado;
        this.armas = List.copyOf(armas);
    }

    public boolean isVictoria() {
        return victoria;
    }

    public double getTiempoSobrevivido() {
        return tiempoSobrevivido;
    }

    public int getEnemigosDerrotados() {
        return enemigosDerrotados;
    }

    public int getNivelAlcanzado() {
        return nivelAlcanzado;
    }

    public List<String> getArmas() {
        return armas;
    }
}
