/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ast;

/**
 *
 * @author daish
 */
public class Variable implements Expresion {
    public enum TipoVar {
        ROUND_NUMBER, TOTAL_ROUNDS, SELF_HEALTH, OPPONENT_HEALTH,
        SELF_RESOURCE, OPPONENT_RESOURCE, SELF_SCORE, OPPONENT_SCORE, RANDOM
    }

    private TipoVar tipo;

    public Variable(TipoVar tipo) {
        this.tipo = tipo;
    }

    @Override
    public Object evaluar(ContextoEjecucion ctx) {
        switch (tipo) {
            case ROUND_NUMBER: return ctx.roundActual;
            case TOTAL_ROUNDS: return ctx.totalRounds;
            case SELF_HEALTH: return ctx.self.salud;
            case OPPONENT_HEALTH: return ctx.opponent.salud;
            case SELF_RESOURCE: return ctx.self.recurso;
            case OPPONENT_RESOURCE: return ctx.opponent.recurso;
            case SELF_SCORE: return ctx.self.puntuacion;
            case OPPONENT_SCORE: return ctx.opponent.puntuacion;
            case RANDOM: return ctx.valorRandomActual;// Retorna de 0.0 1.0
            default: return 0;
        }
    }
}