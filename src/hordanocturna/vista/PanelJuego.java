package hordanocturna.vista;

import hordanocturna.modelo.EstadoPartida;
import hordanocturna.modelo.Farol;
import hordanocturna.modelo.Jugador;
import hordanocturna.modelo.Partida;
import hordanocturna.modelo.armas.Arma;
import hordanocturna.modelo.enemigos.Enemigo;
import hordanocturna.modelo.objetos.FrascoAceite;
import hordanocturna.modelo.objetos.GemaExperiencia;
import hordanocturna.modelo.objetos.Proyectil;
import hordanocturna.modelo.objetos.Recolectable;

import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * VISTA de la partida: dibuja el estado del modelo en cada cuadro.
 * No modifica el modelo; solo lo consulta.
 */
public class PanelJuego extends JPanel {

    public static final int ANCHO = 1024;
    public static final int ALTO = 720;
    private static final int TAMANO_BALDOSA = 64;

    private static final Color COLOR_SUELO_1 = new Color(28, 42, 30);
    private static final Color COLOR_SUELO_2 = new Color(32, 47, 33);
    private static final Color COLOR_ARBOL = new Color(16, 28, 18);
    private static final Color COLOR_VIDA = new Color(200, 40, 50);
    private static final Color COLOR_EXP = new Color(70, 150, 255);
    private static final Color COLOR_LUZ = new Color(255, 190, 70);

    private final Font fuenteHud = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    private final Font fuenteGrande = new Font(Font.SANS_SERIF, Font.BOLD, 30);
    private final Font fuenteTarjeta = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

    private Partida partida;
    private double camaraX;
    private double camaraY;

    public PanelJuego() {
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setBackground(Color.BLACK);
        setFocusable(true);
        setDoubleBuffered(true);
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (partida == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        actualizarCamara();
        dibujarSuelo(g2);
        dibujarEnemigos(g2);
        dibujarProyectiles(g2);
        dibujarJugador(g2);
        dibujarOscuridad(g2);
        dibujarRecolectables(g2);
        dibujarHud(g2);

        if (partida.getEstado() == EstadoPartida.PAUSADA) {
            dibujarPausa(g2);
        }
        g2.dispose();
    }

    // ------------------------------------------------------------------ camara

    /** La camara sigue al jugador sin salirse de los bordes del mapa. */
    private void actualizarCamara() {
        Jugador j = partida.getJugador();
        camaraX = j.getX() - getWidth() / 2.0;
        camaraY = j.getY() - getHeight() / 2.0;
        camaraX = Math.max(0, Math.min(Partida.ANCHO_MAPA - getWidth(), camaraX));
        camaraY = Math.max(0, Math.min(Partida.ALTO_MAPA - getHeight(), camaraY));
    }

    private int pantallaX(double mundoX) {
        return (int) Math.round(mundoX - camaraX);
    }

    private int pantallaY(double mundoY) {
        return (int) Math.round(mundoY - camaraY);
    }

    private boolean estaEnPantalla(double x, double y, double margen) {
        return x > camaraX - margen && x < camaraX + getWidth() + margen
                && y > camaraY - margen && y < camaraY + getHeight() + margen;
    }

    // ------------------------------------------------------------------ mundo

    private void dibujarSuelo(Graphics2D g) {
        int primeraCol = (int) (camaraX / TAMANO_BALDOSA);
        int primeraFila = (int) (camaraY / TAMANO_BALDOSA);
        int columnas = getWidth() / TAMANO_BALDOSA + 2;
        int filas = getHeight() / TAMANO_BALDOSA + 2;

        for (int f = primeraFila; f < primeraFila + filas; f++) {
            for (int c = primeraCol; c < primeraCol + columnas; c++) {
                int px = pantallaX(c * TAMANO_BALDOSA);
                int py = pantallaY(f * TAMANO_BALDOSA);
                int hash = Math.abs((c * 73856093) ^ (f * 19349663));
                g.setColor(hash % 2 == 0 ? COLOR_SUELO_1 : COLOR_SUELO_2);
                g.fillRect(px, py, TAMANO_BALDOSA, TAMANO_BALDOSA);
                if (hash % 11 == 0) {   // algunos arboles decorativos, siempre en el mismo lugar
                    g.setColor(COLOR_ARBOL);
                    g.fillOval(px + 14, py + 10, 36, 36);
                    g.fillRect(px + 29, py + 40, 6, 14);
                }
            }
        }
        // borde del mapa
        g.setColor(new Color(90, 20, 20));
        g.setStroke(new BasicStroke(4));
        g.drawRect(pantallaX(0), pantallaY(0), (int) Partida.ANCHO_MAPA, (int) Partida.ALTO_MAPA);
    }

    private void dibujarEnemigos(Graphics2D g) {
        for (Enemigo e : partida.getEnemigos()) {
            if (!estaEnPantalla(e.getX(), e.getY(), e.getRadio() + 10)) {
                continue;
            }
            int x = pantallaX(e.getX());
            int y = pantallaY(e.getY());
            int r = (int) e.getRadio();
            g.setColor(e.estaParpadeando() ? Color.WHITE : colorEnemigo(e));

            switch (e.getTipo()) {
                case MURCIELAGO:
                    // cuerpo + alas que aletean
                    int aleteo = (int) (Math.sin(e.getTiempoVivo() * 20) * 4);
                    g.fillOval(x - r / 2, y - r / 2, r, r);
                    g.fillPolygon(new int[]{x - 2, x - r - 6, x - r}, new int[]{y, y - 4 + aleteo, y + 4}, 3);
                    g.fillPolygon(new int[]{x + 2, x + r + 6, x + r}, new int[]{y, y - 4 + aleteo, y + 4}, 3);
                    break;
                case ESQUELETO:
                    g.fillOval(x - r, y - r, r * 2, r * 2);
                    g.setColor(new Color(60, 60, 60));
                    g.fillOval(x - 5, y - 4, 4, 4);
                    g.fillOval(x + 1, y - 4, 4, 4);
                    break;
                default:
                    g.fillOval(x - r, y - r, r * 2, r * 2);
            }
        }
    }

    private Color colorEnemigo(Enemigo e) {
        switch (e.getTipo()) {
            case MURCIELAGO:
                return new Color(120, 70, 140);
            case ESQUELETO:
                return new Color(215, 210, 190);
            default:
                return Color.GRAY;
        }
    }

    private void dibujarProyectiles(Graphics2D g) {
        for (Proyectil p : partida.getProyectiles()) {
            if (!estaEnPantalla(p.getX(), p.getY(), 20)) {
                continue;
            }
            AffineTransform original = g.getTransform();
            g.translate(pantallaX(p.getX()), pantallaY(p.getY()));
            g.rotate(p.getAngulo());
            g.setColor(new Color(220, 225, 235));
            g.fillPolygon(new int[]{-8, 10, -8}, new int[]{-3, 0, 3}, 3);
            g.setColor(new Color(130, 90, 50));
            g.fillRect(-12, -2, 5, 4);
            g.setTransform(original);
        }
    }

    private void dibujarJugador(Graphics2D g) {
        Jugador j = partida.getJugador();
        int x = pantallaX(j.getX());
        int y = pantallaY(j.getY());
        int r = (int) j.getRadio();
        // parpadea mientras es invulnerable tras recibir un golpe
        if (j.estaInvulnerable() && (System.currentTimeMillis() / 80) % 2 == 0) {
            return;
        }
        g.setColor(new Color(60, 90, 160));
        g.fillOval(x - r, y - r, r * 2, r * 2);
        g.setColor(new Color(90, 60, 40));              // sombrero
        g.fillOval(x - r - 3, y - r - 2, r * 2 + 6, 10);
        g.fillRect(x - r / 2, y - r - 8, r, 9);
        g.setColor(COLOR_LUZ);                           // farol
        int fx = x + (int) (j.getMiraX() * (r + 4));
        int fy = y + (int) (j.getMiraY() * (r + 4));
        g.fillOval(fx - 4, fy - 4, 8, 8);
    }

    /**
     * Mecanica distintiva: se cubre la pantalla de oscuridad excepto un circulo
     * alrededor del jugador cuyo tamano depende del aceite del farol.
     */
    private void dibujarOscuridad(Graphics2D g) {
        Jugador j = partida.getJugador();
        float radio = (float) Math.max(10, j.getFarol().getRadioActual());
        Point2D centro = new Point2D.Float(pantallaX(j.getX()), pantallaY(j.getY()));
        float[] fracciones = {0f, 0.65f, 1f};
        Color[] colores = {
                new Color(0, 0, 12, 0),
                new Color(0, 0, 12, 70),
                new Color(0, 0, 12, 242)
        };
        g.setPaint(new RadialGradientPaint(centro, radio, fracciones, colores));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    private void dibujarRecolectables(Graphics2D g) {
        for (Recolectable r : partida.getRecolectables()) {
            if (!estaEnPantalla(r.getX(), r.getY(), 10)) {
                continue;
            }
            int x = pantallaX(r.getX());
            int y = pantallaY(r.getY());
            if (r instanceof GemaExperiencia) {
                int valor = ((GemaExperiencia) r).getValor();
                g.setColor(valor >= 10 ? new Color(255, 80, 120) : valor >= 3 ? new Color(90, 230, 120) : COLOR_EXP);
                g.fillPolygon(new int[]{x, x + 5, x, x - 5}, new int[]{y - 7, y, y + 7, y}, 4);
            } else if (r instanceof FrascoAceite) {
                g.setColor(COLOR_LUZ);
                g.fillRoundRect(x - 5, y - 6, 10, 12, 4, 4);
                g.setColor(new Color(120, 80, 40));
                g.fillRect(x - 2, y - 10, 4, 4);
            }
        }
    }

    // ------------------------------------------------------------------ HUD

    private void dibujarHud(Graphics2D g) {
        Jugador j = partida.getJugador();
        int ancho = getWidth();

        // barra de experiencia (arriba, todo el ancho)
        g.setColor(new Color(20, 20, 30, 200));
        g.fillRect(0, 0, ancho, 14);
        g.setColor(COLOR_EXP);
        g.fillRect(0, 0, (int) (ancho * (double) j.getExperiencia() / j.getExperienciaSiguienteNivel()), 14);

        // tiempo restante
        g.setFont(fuenteGrande);
        g.setColor(Color.WHITE);
        String reloj = formatearTiempo(partida.getTiempoRestante());
        FontMetrics fm = g.getFontMetrics();
        g.drawString(reloj, (ancho - fm.stringWidth(reloj)) / 2, 50);

        // vida y luz del farol
        g.setFont(fuenteHud);
        dibujarBarra(g, 16, 26, 200, 16, j.getVida() / j.getVidaMaxima(), COLOR_VIDA,
                "Vida " + (int) j.getVida() + "/" + (int) j.getVidaMaxima());
        Farol farol = j.getFarol();
        dibujarBarra(g, 16, 48, 200, 16, farol.getPorcentaje(), COLOR_LUZ, "Farol " + (int) farol.getLuz() + "%");

        g.setColor(Color.WHITE);
        g.drawString("Nivel " + j.getNivel() + "     Enemigos derrotados: " + partida.getEnemigosDerrotados(), 16, 84);

        // armas equipadas (abajo a la izquierda)
        int y = getHeight() - 16;
        List<Arma> armas = new ArrayList<>(j.getArmas());
        for (int i = armas.size() - 1; i >= 0; i--) {
            Arma a = armas.get(i);
            g.setColor(Color.WHITE);
            g.drawString(a.getNombre() + "  Nv " + a.getNivel(), 16, y);
            y -= 18;
        }
        g.setColor(new Color(200, 200, 200));
        g.drawString("Armas", 16, y);

        // mensaje temporal
        String mensaje = partida.getMensajeActivo();
        if (mensaje != null) {
            g.setFont(fuenteHud.deriveFont(18f));
            fm = g.getFontMetrics();
            int mx = (ancho - fm.stringWidth(mensaje)) / 2;
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRoundRect(mx - 12, 96, fm.stringWidth(mensaje) + 24, 32, 12, 12);
            g.setColor(new Color(255, 220, 140));
            g.drawString(mensaje, mx, 118);
        }

        g.setFont(fuenteHud.deriveFont(Font.PLAIN, 12f));
        g.setColor(new Color(200, 200, 200, 160));
        g.drawString("WASD: mover   ESC: pausa", ancho - 170, 34);
    }

    private void dibujarBarra(Graphics2D g, int x, int y, int ancho, int alto, double porcentaje,
                              Color color, String texto) {
        porcentaje = Math.max(0, Math.min(1, porcentaje));
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(x, y, ancho, alto);
        g.setColor(color);
        g.fillRect(x, y, (int) (ancho * porcentaje), alto);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(1));
        g.drawRect(x, y, ancho, alto);
        g.drawString(texto, x + 6, y + alto - 3);
    }

    // ------------------------------------------------------------------ overlays

    private void dibujarPausa(Graphics2D g) {
        oscurecerPantalla(g, 0.6f);
        g.setFont(fuenteGrande.deriveFont(44f));
        g.setColor(Color.WHITE);
        String texto = "PAUSA";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2, getHeight() / 2);
        g.setFont(fuenteHud);
        String ayuda = "ESC para continuar  -  Q para abandonar la partida";
        g.drawString(ayuda, (getWidth() - g.getFontMetrics().stringWidth(ayuda)) / 2, getHeight() / 2 + 40);
    }

    private void oscurecerPantalla(Graphics2D g, float opacidad) {
        Composite original = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setComposite(original);
    }

    public static String formatearTiempo(double segundos) {
        int total = (int) Math.ceil(segundos);
        return String.format("%02d:%02d", total / 60, total % 60);
    }
}
