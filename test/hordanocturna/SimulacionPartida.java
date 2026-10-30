package hordanocturna;

import hordanocturna.modelo.Partida;
import hordanocturna.modelo.armas.Arma;
import hordanocturna.vista.PanelJuego;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Prueba automatica: un "bot" juega una partida completa moviendose en circulos
 * Verifica que el ciclo principal
 * no lance excepciones y guarda capturas en distintos momentos.
 */
public class SimulacionPartida {

    public static void main(String[] args) throws Exception {
        long semilla = args.length > 0 ? Long.parseLong(args[0]) : 42;
        String carpetaCapturas = args.length > 1 ? args[1] : null;
        Partida partida = new Partida(new Random(semilla));
        PanelJuego panel = new PanelJuego();
        panel.setSize(PanelJuego.ANCHO, PanelJuego.ALTO);
        panel.setPartida(partida);

        double dt = 1.0 / 60;
        int ultimoMinuto = -1;
        int pasos = 0;
        while (!partida.getEstado().esFinal() && pasos < 60 * 60 * 7) {
            moverBot(partida);
            partida.actualizar(dt);
            int minuto = partida.getMinutoActual();
            if (minuto != ultimoMinuto) {
                ultimoMinuto = minuto;
                StringBuilder armas = new StringBuilder();
                for (Arma a : partida.getJugador().getArmas()) {
                    armas.append(a.getNombre()).append(" ").append(a.getNivel()).append(", ");
                }
                System.out.printf("min %d | vida %.0f/%.0f | nv %d | enemigos %d | muertes %d | luz %.0f | %s %n",
                        minuto, partida.getJugador().getVida(), partida.getJugador().getVidaMaxima(),
                        partida.getJugador().getNivel(), partida.getEnemigos().size(),
                        partida.getEnemigosDerrotados(), partida.getJugador().getFarol().getLuz(), armas);
                if (carpetaCapturas != null && (minuto == 1 || minuto == 3)) {
                    capturar(panel, carpetaCapturas + "/minuto" + minuto + ".png");
                }
            }
            pasos++;
        }
        System.out.println("FIN: " + partida.getEstado() + " en " + PanelJuego.formatearTiempo(partida.getTiempo())
                + " muertes=" + partida.getEnemigosDerrotados());
    }

    /** Bot simple: huye de los enemigos cercanos y se acerca a gemas/aceite. */
    private static void moverBot(Partida p) {
        hordanocturna.modelo.Jugador j = p.getJugador();
        double dx = 0, dy = 0;
        for (hordanocturna.modelo.enemigos.Enemigo e : p.getEnemigos()) {
            double d = e.distanciaA(j);
            if (d < 220 && d > 0) {
                dx += (j.getX() - e.getX()) / (d * d);
                dy += (j.getY() - e.getY()) / (d * d);
            }
        }
        double mejor = 400;
        hordanocturna.modelo.objetos.Recolectable objetivo = null;
        for (hordanocturna.modelo.objetos.Recolectable r : p.getRecolectables()) {
            double d = r.distanciaA(j);
            if (d < mejor) {
                mejor = d;
                objetivo = r;
            }
        }
        if (objetivo != null) {
            dx += (objetivo.getX() - j.getX()) / mejor * 0.004;
            dy += (objetivo.getY() - j.getY()) / mejor * 0.004;
        }
        // tendencia a volver al centro del mapa para no quedar atrapado en un borde
        dx += (Partida.ANCHO_MAPA / 2 - j.getX()) * 0.0000015;
        dy += (Partida.ALTO_MAPA / 2 - j.getY()) * 0.0000015;
        p.setDireccionJugador(dx, dy);
    }

    private static void capturar(PanelJuego panel, String ruta) throws Exception {
        BufferedImage img = new BufferedImage(PanelJuego.ANCHO, PanelJuego.ALTO, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        panel.paint(g);
        g.dispose();
        ImageIO.write(img, "png", new File(ruta));
    }
}
