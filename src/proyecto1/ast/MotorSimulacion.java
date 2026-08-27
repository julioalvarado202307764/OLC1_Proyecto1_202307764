/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ast;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 *
 * @author daish
 */
public class MotorSimulacion {
    private List<Estrategia> estrategiasAST;
    private List<Partida> partidasAST;
    private List<String> partidasAEjecutar;
    private Random random;
    private StringBuilder bitacora;

    public MotorSimulacion(List<Estrategia> estrategiasAST, List<Partida> partidasAST, 
                           List<String> partidasAEjecutar, int semilla) {
        this.estrategiasAST = estrategiasAST;
        this.partidasAST = partidasAST;
        this.partidasAEjecutar = partidasAEjecutar;
        this.random = new Random(semilla);
        this.bitacora = new StringBuilder();
    }

    public String ejecutarSimulacion() {
        bitacora.append("⚔️ INICIANDO SIMULACIÓN DE BATALLAS ⚔️\n\n");

        for (String idM : partidasAEjecutar) {
            Partida partida = buscarPartida(idM);
            if (partida == null) {
                bitacora.append("❌ Error: No se encontró la partida '").append(idM).append("'\n");
                continue;
            }

            Estrategia est1 = buscarEstrategia(partida.player1Id);
            Estrategia est2 = buscarEstrategia(partida.player2Id);

            if (est1 == null || est2 == null) {
                bitacora.append("❌ Error: Faltan estrategias para la partida '").append(idM).append("'\n");
                continue;
            }

            simularPartida(partida, est1, est2);
        }

        return bitacora.toString();
    }

    private void simularPartida(Partida partida, Estrategia est1, Estrategia est2) {
        bitacora.append("=========================================\n");
        bitacora.append("🏆 PARTIDA: ").append(partida.id).append("\n");
        bitacora.append("=========================================\n");

        Combatiente p1 = new Combatiente(est1.id, est1.tipo);
        Combatiente p2 = new Combatiente(est2.id, est2.tipo);

        for (int ronda = 1; ronda <= partida.rounds; ronda++) {
            // 1. Crear el contexto para esta ronda
            ContextoEjecucion ctx1 = new ContextoEjecucion(p1, p2, ronda, partida.rounds, random);
            ContextoEjecucion ctx2 = new ContextoEjecucion(p2, p1, ronda, partida.rounds, random);

            // 2. Cada jugador decide su acción
            Accion accionP1 = decidirAccion(est1, ctx1);
            Accion accionP2 = decidirAccion(est2, ctx2);

            p1.registrarAccion(accionP1);
            p2.registrarAccion(accionP2);

            // 3. Resolver la ronda (Cálculo de daño, recursos y puntos)
            resolverInteraccion(p1, accionP1, p2, accionP2, partida);

            // 4. Reporte de la ronda
            bitacora.append("Ronda ").append(ronda).append(": ")
                    .append(p1.nombre).append(" (").append(accionP1).append(") vs ")
                    .append(p2.nombre).append(" (").append(accionP2).append(")\n");
            
            // Si alguno muere antes de llegar al límite de rondas, termina la partida
            if (p1.salud <= 0 || p2.salud <= 0) break;
        }

        // 5. Asignar Bonificaciones Finales (Combos y Low Health)
        aplicarBonificacionesFinales(p1, p2, partida);

        // 6. Declarar Ganador
        bitacora.append("\n--- RESULTADO FINAL ---\n");
        bitacora.append(p1.nombre).append(" -> Vida: ").append(p1.salud).append(" | Puntos: ").append(p1.puntuacion).append("\n");
        bitacora.append(p2.nombre).append(" -> Vida: ").append(p2.salud).append(" | Puntos: ").append(p2.puntuacion).append("\n");
        
        if (p1.puntuacion > p2.puntuacion) {
            bitacora.append("👑 GANADOR: ").append(p1.nombre).append("!\n\n");
        } else if (p2.puntuacion > p1.puntuacion) {
            bitacora.append("👑 GANADOR: ").append(p2.nombre).append("!\n\n");
        } else {
            bitacora.append("🤝 EMPATE TÉCNICO!\n\n");
        }
    }

    private Accion decidirAccion(Estrategia est, ContextoEjecucion ctx) {
        // En la ronda 1 siempre se ejecuta initial
        if (ctx.roundActual == 1) return est.accionInicial;

        // Evaluamos el bloque rules en orden
        for (Regla regla : est.reglas) {
            if (regla.condicion.evaluar(ctx)) {
                return regla.accionThen;
            }
        }
        // Si ninguna regla se cumple, aplicamos el else
        return est.accionElse;
    }

    private void resolverInteraccion(Combatiente p1, Accion a1, Combatiente p2, Accion a2, Partida partida) {
        // LÓGICA BASE: Aquí se aplican las reglas específicas del enunciado para daño, curación y puntuación.
        // Ejemplo simplificado (Debes ajustarlo según las tablas de daño exactas del manual):
        
        boolean p1Ataca = esAtaque(a1);
        boolean p2Ataca = esAtaque(a2);
        
        // Puntuación por ataque exitoso (si ataca y el otro no defiende adecuadamente)
        if (p1Ataca) {
            p2.salud -= 15; // Daño base hipotético
            p1.puntuacion += partida.dmgPoint; 
        }
        if (p2Ataca) {
            p1.salud -= 15;
            p2.puntuacion += partida.dmgPoint;
        }

        // Puntuación por curación
        if (a1 == Accion.HEALING_RUNE || a1 == Accion.REST) {
            p1.salud += 20;
            p1.puntuacion += partida.healPoint;
        }
        if (a2 == Accion.HEALING_RUNE || a2 == Accion.REST) {
            p2.salud += 20;
            p2.puntuacion += partida.healPoint;
        }
        
        // Prevención de vida infinita
        if(p1.salud > 100) p1.salud = 100;
        if(p2.salud > 100) p2.salud = 100;
    }

    private void aplicarBonificacionesFinales(Combatiente p1, Combatiente p2, Partida partida) {
        // Bonificación de Victoria (Quien tenga más salud)
        if (p1.salud > p2.salud) p1.puntuacion += partida.victoryBonus;
        else if (p2.salud > p1.salud) p2.puntuacion += partida.victoryBonus;

        // Bonificación Low Health Victory (Ganar con menos del 20% de salud)
        if (p1.salud > 0 && p1.salud <= 20 && p1.salud > p2.salud) p1.puntuacion += partida.lowHealthVictory;
        if (p2.salud > 0 && p2.salud <= 20 && p2.salud > p1.salud) p2.puntuacion += partida.lowHealthVictory;
    }

    private boolean esAtaque(Accion a) {
        return a == Accion.ARCANE_BOLT || a == Accion.FIREBALL || a == Accion.SLASH || a == Accion.HEAVY_STRIKE;
    }

    private Partida buscarPartida(String id) {
        return partidasAST.stream().filter(p -> p.id.equals(id)).findFirst().orElse(null);
    }

    private Estrategia buscarEstrategia(String id) {
        return estrategiasAST.stream().filter(e -> e.id.equals(id)).findFirst().orElse(null);
    }
}