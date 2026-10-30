package hordanocturna.modelo;

import hordanocturna.modelo.armas.Arma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * El cazador controlado por el usuario.
 * Encapsula su vida, experiencia, nivel, armas y su farol.
 */
public class Jugador extends Entidad {

    private static final double VELOCIDAD = 170.0;
    private static final double RADIO_IMAN = 70.0;
    private static final double TIEMPO_INVULNERABLE = 0.5;

    private double vida;
    private final double vidaMaxima = 100;
    private int nivel = 1;
    private int experiencia = 0;

    // Direccion pedida por el controlador (teclado) y ultima direccion valida
    private double dirX;
    private double dirY;
    private double miraX = 1;
    private double miraY = 0;

    private double invulnerable;

    private final Farol farol = new Farol();
    private final List<Arma> armas = new ArrayList<>();

    public Jugador(double x, double y) {
        super(x, y, 14);
        this.vida = vidaMaxima;
    }

    /** Define hacia donde se quiere mover el jugador (-1, 0 o 1 en cada eje). */
    public void setDireccion(double dx, double dy) {
        double largo = Math.sqrt(dx * dx + dy * dy);
        if (largo > 0) {
            dirX = dx / largo;   // normalizamos para que en diagonal no vaya mas rapido
            dirY = dy / largo;
            miraX = dirX;
            miraY = dirY;
        } else {
            dirX = 0;
            dirY = 0;
        }
    }

    public void actualizar(double dt, double anchoMapa, double altoMapa) {
        x += dirX * VELOCIDAD * dt;
        y += dirY * VELOCIDAD * dt;
        x = Math.max(radio, Math.min(anchoMapa - radio, x));
        y = Math.max(radio, Math.min(altoMapa - radio, y));

        if (invulnerable > 0) {
            invulnerable -= dt;
        }
        farol.consumir(dt);
    }

    public void recibirDano(double dano) {
        if (invulnerable > 0 || dano <= 0) {
            return;
        }
        vida = Math.max(0, vida - dano);
        invulnerable = TIEMPO_INVULNERABLE;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    /**
     * Suma experiencia y devuelve cuantos niveles se subieron
     * (puede ser mas de uno si se recoge mucha experiencia de golpe).
     */
    public int ganarExperiencia(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La experiencia no puede ser negativa");
        }
        experiencia += cantidad;
        int nivelesSubidos = 0;
        while (experiencia >= getExperienciaSiguienteNivel()) {
            experiencia -= getExperienciaSiguienteNivel();
            nivel++;
            nivelesSubidos++;
        }
        return nivelesSubidos;
    }

    public int getExperienciaSiguienteNivel() {
        return 5 + nivel * 5;
    }

    public void agregarArma(Arma arma) {
        for (Arma a : armas) {
            if (a.getTipo() == arma.getTipo()) {
                throw new IllegalStateException("El jugador ya tiene el arma " + arma.getNombre());
            }
        }
        armas.add(arma);
    }

    /** Lista de solo lectura: nadie de afuera puede agregar o quitar armas sin pasar por las reglas. */
    public List<Arma> getArmas() {
        return Collections.unmodifiableList(armas);
    }

    public double getRadioIman() {
        return RADIO_IMAN;
    }

    public double getVida() {
        return vida;
    }

    public double getVidaMaxima() {
        return vidaMaxima;
    }

    public int getNivel() {
        return nivel;
    }

    public int getExperiencia() {
        return experiencia;
    }

    public double getMiraX() {
        return miraX;
    }

    public double getMiraY() {
        return miraY;
    }

    public boolean estaInvulnerable() {
        return invulnerable > 0;
    }

    public Farol getFarol() {
        return farol;
    }
}
