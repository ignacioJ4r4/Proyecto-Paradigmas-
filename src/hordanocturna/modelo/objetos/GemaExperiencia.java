package hordanocturna.modelo.objetos;

import hordanocturna.modelo.Partida;

/** Gema que sueltan los enemigos al morir. Da experiencia al recogerla. */
public class GemaExperiencia extends Recolectable {

    private final int valor;

    public GemaExperiencia(double x, double y, int valor) {
        super(x, y, 6);
        this.valor = valor;
    }

    @Override
    public void aplicar(Partida partida) {
        partida.otorgarExperiencia(valor);
    }

    public int getValor() {
        return valor;
    }
}
