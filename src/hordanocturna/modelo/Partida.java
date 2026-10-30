package hordanocturna.modelo;

import hordanocturna.modelo.armas.Arma;
import hordanocturna.modelo.armas.TipoArma;
import hordanocturna.modelo.enemigos.Enemigo;
import hordanocturna.modelo.objetos.FrascoAceite;
import hordanocturna.modelo.objetos.GemaExperiencia;
import hordanocturna.modelo.objetos.Proyectil;
import hordanocturna.modelo.objetos.Recolectable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Nucleo del MODELO: contiene todo el estado de una partida y sus reglas.
 * No sabe nada de Swing; la vista solo lee su estado y el controlador
 * solo llama a sus metodos publicos (setDireccionJugador, actualizar, alternarPausa).
 */
public class Partida {

    public static final double DURACION_NOCHE = 300;        // beta: 5 minutos
    public static final double ANCHO_MAPA = 4000;
    public static final double ALTO_MAPA = 4000;
    private static final double DURACION_MENSAJE = 3.0;

    private final Random aleatorio;
    private final Jugador jugador;
    private final List<Enemigo> enemigos = new ArrayList<>();
    private final List<Proyectil> proyectiles = new ArrayList<>();
    private final List<Recolectable> recolectables = new ArrayList<>();
    private final GeneradorOleadas generadorOleadas;

    private EstadoPartida estado = EstadoPartida.JUGANDO;
    private double tiempo = 0;
    private int enemigosDerrotados = 0;

    private String mensaje;
    private double tiempoMensaje;

    public Partida() {
        this(new Random());
    }

    /** Constructor con semilla, util para pruebas reproducibles. */
    public Partida(Random aleatorio) {
        this.aleatorio = aleatorio;
        this.jugador = new Jugador(ANCHO_MAPA / 2, ALTO_MAPA / 2);
        this.jugador.agregarArma(TipoArma.DAGA.crear());
        this.generadorOleadas = new GeneradorOleadas(aleatorio);
        mostrarMensaje("Sobrevive hasta el amanecer...");
    }

    // ===================== Ciclo principal =====================

    /** Avanza la simulacion dt segundos. Solo hace algo si el estado es JUGANDO. */
    public void actualizar(double dt) {
        if (estado != EstadoPartida.JUGANDO) {
            return;
        }
        tiempo += dt;
        if (tiempoMensaje > 0) {
            tiempoMensaje -= dt;
        }

        generadorOleadas.actualizar(dt, this);
        jugador.actualizar(dt, ANCHO_MAPA, ALTO_MAPA);
        for (Arma arma : jugador.getArmas()) {
            arma.actualizar(dt, this);
        }
        actualizarEnemigos(dt);
        actualizarProyectiles(dt);
        actualizarRecolectables(dt);
        eliminarInactivos();
        verificarFinDePartida();
    }

    private void actualizarEnemigos(double dt) {
        for (Enemigo enemigo : enemigos) {
            enemigo.actualizar(dt, jugador);
            if (enemigo.colisionaCon(jugador)) {
                jugador.recibirDano(enemigo.getDano());
            }
        }
    }

    private void actualizarProyectiles(double dt) {
        for (Proyectil proyectil : proyectiles) {
            proyectil.actualizar(dt);
            for (Enemigo enemigo : enemigos) {
                if (enemigo.isActiva() && proyectil.puedeGolpear(enemigo)) {
                    proyectil.registrarImpacto();
                    danarEnemigo(enemigo, proyectil.getDano());
                    break;   // el proyectil desaparece al primer impacto
                }
            }
        }
    }

    private void actualizarRecolectables(double dt) {
        // se recorre por indice porque aplicar() puede agregar elementos indirectamente
        for (int i = 0; i < recolectables.size(); i++) {
            Recolectable r = recolectables.get(i);
            r.actualizar(dt, jugador);
            if (r.isActiva() && r.colisionaCon(jugador)) {
                r.aplicar(this);
                r.desactivar();
            }
        }
    }

    private void eliminarInactivos() {
        enemigos.removeIf(e -> !e.isActiva());
        proyectiles.removeIf(p -> !p.isActiva());
        recolectables.removeIf(r -> !r.isActiva());
    }

    private void verificarFinDePartida() {
        if (!jugador.estaVivo()) {
            estado = EstadoPartida.DERROTA;
        } else if (tiempo >= DURACION_NOCHE) {
            estado = EstadoPartida.VICTORIA;
        }
    }

    // ===================== Combate (usado por las armas) =====================

    /** Unico punto donde se aplica dano a un enemigo. */
    public void danarEnemigo(Enemigo enemigo, double dano) {
        boolean murio = enemigo.recibirDano(dano, jugador);
        if (murio) {
            enemigosDerrotados++;
            recolectables.add(new GemaExperiencia(enemigo.getX(), enemigo.getY(), enemigo.getExperiencia()));
            if (aleatorio.nextDouble() < enemigo.getProbabilidadAceite()) {
                recolectables.add(new FrascoAceite(enemigo.getX() + 12, enemigo.getY() + 6));
            }
        }
    }

    public Enemigo getEnemigoMasCercano(double x, double y, double distanciaMaxima) {
        Enemigo masCercano = null;
        double mejor = distanciaMaxima;
        for (Enemigo enemigo : enemigos) {
            double d = enemigo.distanciaA(x, y);
            if (enemigo.isActiva() && d < mejor) {
                mejor = d;
                masCercano = enemigo;
            }
        }
        return masCercano;
    }

    /** Devuelve una lista nueva (modificable) con los enemigos activos dentro del radio. */
    public List<Enemigo> getEnemigosEnRadio(double x, double y, double radio) {
        List<Enemigo> resultado = new ArrayList<>();
        for (Enemigo enemigo : enemigos) {
            if (enemigo.isActiva() && enemigo.distanciaA(x, y) <= radio + enemigo.getRadio()) {
                resultado.add(enemigo);
            }
        }
        return resultado;
    }

    public void agregarProyectil(Proyectil proyectil) {
        proyectiles.add(proyectil);
    }

    void agregarEnemigo(Enemigo enemigo) {
        enemigos.add(enemigo);
    }

    // ===================== Experiencia =====================

    /**
     * Suma experiencia al jugador y avisa si subio de nivel.
     * (Beta: subir de nivel aun no entrega mejoras; eso llega en el siguiente avance.)
     */
    public void otorgarExperiencia(int cantidad) {
        int nivelesSubidos = jugador.ganarExperiencia(cantidad);
        if (nivelesSubidos > 0) {
            mostrarMensaje("Subiste a nivel " + jugador.getNivel() + "!");
        }
    }

    // ===================== Pausa =====================

    public void alternarPausa() {
        if (estado == EstadoPartida.JUGANDO) {
            estado = EstadoPartida.PAUSADA;
        } else if (estado == EstadoPartida.PAUSADA) {
            estado = EstadoPartida.JUGANDO;
        }
    }

    // ===================== Consultas para la vista =====================

    public void setDireccionJugador(double dx, double dy) {
        jugador.setDireccion(dx, dy);
    }

    public EstadisticasPartida getEstadisticas() {
        List<String> nombresArmas = new ArrayList<>();
        for (Arma arma : jugador.getArmas()) {
            nombresArmas.add(arma.getNombre() + " (Nv " + arma.getNivel() + ")");
        }
        return new EstadisticasPartida(estado == EstadoPartida.VICTORIA, tiempo, enemigosDerrotados,
                jugador.getNivel(), nombresArmas);
    }

    private void mostrarMensaje(String texto) {
        mensaje = texto;
        tiempoMensaje = DURACION_MENSAJE;
    }

    public String getMensajeActivo() {
        return tiempoMensaje > 0 ? mensaje : null;
    }

    public int getMinutoActual() {
        return (int) (tiempo / 60);
    }

    public double getTiempoRestante() {
        return Math.max(0, DURACION_NOCHE - tiempo);
    }

    public double getTiempo() {
        return tiempo;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public List<Enemigo> getEnemigos() {
        return Collections.unmodifiableList(enemigos);
    }

    public List<Proyectil> getProyectiles() {
        return Collections.unmodifiableList(proyectiles);
    }

    public List<Recolectable> getRecolectables() {
        return Collections.unmodifiableList(recolectables);
    }

    public int getEnemigosDerrotados() {
        return enemigosDerrotados;
    }

    public Random getAleatorio() {
        return aleatorio;
    }
}
