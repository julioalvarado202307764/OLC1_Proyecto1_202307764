/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ast;
import java.util.Random;
/**
 *
 * @author daish
 */
public class ContextoEjecucion {
    public Combatiente self;       // El jugador que está evaluando su estrategia
    public Combatiente opponent;   // El rival
    public int roundActual;
    public int totalRounds;
    public int valorRandomActual; // Para evaluar la variable 'random'

    public ContextoEjecucion(Combatiente self, Combatiente opponent, int roundActual, int totalRounds, Random generadorRandom) {
        this.self = self;
        this.opponent = opponent;
        this.roundActual = roundActual;
        this.totalRounds = totalRounds;
        this.valorRandomActual = valorRandomActual;
    }
}