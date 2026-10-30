package hordanocturna.modelo.armas;

import hordanocturna.modelo.Jugador;
import hordanocturna.modelo.Partida;
import hordanocturna.modelo.enemigos.Enemigo;
import hordanocturna.modelo.objetos.Proyectil;

/**
 * Arma inicial. Lanza dagas hacia el enemigo mas cercano
 * (o hacia donde mira el jugador si no hay enemigos cerca).
 */
public class Daga extends Arma {

    private static final double VELOCIDAD = 520;
    private static final double ALCANCE_BUSQUEDA = 600;
    private static final double APERTURA = 0.12;   // separacion entre dagas (radianes)

    public Daga() {
        super(TipoArma.DAGA);
    }

    @Override
    protected boolean atacar(Partida partida) {
        Jugador jugador = partida.getJugador();
        Enemigo objetivo = partida.getEnemigoMasCercano(jugador.getX(), jugador.getY(), ALCANCE_BUSQUEDA);

        double angulo;
        if (objetivo != null) {
            angulo = Math.atan2(objetivo.getY() - jugador.getY(), objetivo.getX() - jugador.getX());
        } else {
            angulo = Math.atan2(jugador.getMiraY(), jugador.getMiraX());
        }

        int cantidad = 1 + (nivel - 1) / 2;
        double inicio = angulo - APERTURA * (cantidad - 1) / 2.0;

        for (int i = 0; i < cantidad; i++) {
            double a = inicio + APERTURA * i;
            partida.agregarProyectil(new Proyectil(
                    jugador.getX(), jugador.getY(),
                    Math.cos(a) * VELOCIDAD, Math.sin(a) * VELOCIDAD,
                    10 + 4 * nivel, 1.4));
        }
        return true;
    }

    @Override
    protected double getTiempoRecarga() {
        return 1.0 - 0.08 * nivel;
    }
}
