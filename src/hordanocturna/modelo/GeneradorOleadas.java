package hordanocturna.modelo;

import hordanocturna.modelo.enemigos.TipoEnemigo;

import java.util.Random;

/**
 * Decide cuando, cuantos y que enemigos aparecen.
 * Cada minuto la frecuencia, la cantidad y la resistencia aumentan.
 * Desde el minuto 1 aparecen esqueletos ademas de murcielagos.
 */
public class GeneradorOleadas {

    private static final double DISTANCIA_APARICION = 650;   // fuera de la pantalla
    private static final int MAXIMO_ENEMIGOS = 350;

    private final Random aleatorio;
    private double temporizador = 0;

    public GeneradorOleadas(Random aleatorio) {
        this.aleatorio = aleatorio;
    }

    public void actualizar(double dt, Partida partida) {
        temporizador -= dt;
        if (temporizador > 0) {
            return;
        }
        int minuto = partida.getMinutoActual();
        temporizador = getIntervalo(minuto);

        int cantidad = 1 + minuto / 3;
        for (int i = 0; i < cantidad && partida.getEnemigos().size() < MAXIMO_ENEMIGOS; i++) {
            generarEnemigo(partida, elegirTipo(minuto), getMultiplicador(minuto));
        }
    }

    public void generarEnemigo(Partida partida, TipoEnemigo tipo, double multiplicador) {
        Jugador jugador = partida.getJugador();
        double angulo = aleatorio.nextDouble() * Math.PI * 2;
        double x = jugador.getX() + Math.cos(angulo) * DISTANCIA_APARICION;
        double y = jugador.getY() + Math.sin(angulo) * DISTANCIA_APARICION;
        x = Math.max(0, Math.min(Partida.ANCHO_MAPA, x));
        y = Math.max(0, Math.min(Partida.ALTO_MAPA, y));
        partida.agregarEnemigo(tipo.crear(x, y, multiplicador));
    }

    double getIntervalo(int minuto) {
        return Math.max(0.3, 1.0 - minuto * 0.07);
    }

    double getMultiplicador(int minuto) {
        return 1 + minuto * 0.2;
    }

    TipoEnemigo elegirTipo(int minuto) {
        double r = aleatorio.nextDouble();
        if (minuto >= 1 && r < 0.50) {
            return TipoEnemigo.ESQUELETO;
        }
        return TipoEnemigo.MURCIELAGO;
    }
}
