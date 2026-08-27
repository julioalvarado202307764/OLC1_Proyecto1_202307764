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
        // 1. Calculamos el daño crudo que cada uno lanza
        int dmgA_P1 = calcularDaño(a2); // Daño que recibe P1
        int dmgA_P2 = calcularDaño(a1); // Daño que recibe P2

        // 2. Evaluamos Defensas (Reducen el daño a la mitad y otorgan puntos de defensa)
        if (dmgA_P1 > 0 && (a1 == Accion.SHIELD_BLOCK || a1 == Accion.MAGIC_BARRIER)) {
            dmgA_P1 /= 2;
            p1.puntuacion += partida.defPoint;
            bitacora.append("   🛡️ ").append(p1.nombre).append(" bloqueó parcialmente el ataque.\n");
        }
        if (dmgA_P2 > 0 && (a2 == Accion.SHIELD_BLOCK || a2 == Accion.MAGIC_BARRIER)) {
            dmgA_P2 /= 2;
            p2.puntuacion += partida.defPoint;
            bitacora.append("   🛡️ ").append(p2.nombre).append(" bloqueó parcialmente el ataque.\n");
        }

        // 3. Aplicamos el daño final y damos puntos por ataque exitoso
        if (dmgA_P1 > 0) {
            p1.salud -= dmgA_P1;
            p2.puntuacion += partida.dmgPoint;
        }
        if (dmgA_P2 > 0) {
            p2.salud -= dmgA_P2;
            p1.puntuacion += partida.dmgPoint;
        }

        // 4. Aplicamos Curaciones
        aplicarCuracion(p1, a1, partida);
        aplicarCuracion(p2, a2, partida);

        // 5. Verificamos la ejecución de Combos
        if (verificarCombo(p1, partida)) {
            p1.puntuacion += (p1.tipo == TipoJugador.MAGE) ? partida.mageComboPoints : partida.warriorComboPoints;
            bitacora.append("   🔥 ¡").append(p1.nombre).append(" conectó un COMBO brutal!\n");
        }
        if (verificarCombo(p2, partida)) {
            p2.puntuacion += (p2.tipo == TipoJugador.MAGE) ? partida.mageComboPoints : partida.warriorComboPoints;
            bitacora.append("   🔥 ¡").append(p2.nombre).append(" conectó un COMBO brutal!\n");
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private int calcularDaño(Accion a) {
        // NOTA: Ajusta estos valores numéricos si el manual oficial indica un daño exacto.
        switch (a) {
            case SLASH: return 10;
            case ARCANE_BOLT: return 10;
            case HEAVY_STRIKE: return 20;
            case FIREBALL: return 20;
            default: return 0; // Las curaciones o defensas no hacen daño
        }
    }

    private void aplicarCuracion(Combatiente p, Accion a, Partida partida) {
        if (a == Accion.HEALING_RUNE || a == Accion.REST) {
            p.salud += 15; // Ajusta este valor si el manual especifica otra cantidad
            if (p.salud > 100) p.salud = 100; // Tope máximo de vida
            p.puntuacion += partida.healPoint;
        }
    }

    private boolean verificarCombo(Combatiente p, Partida partida) {
        List<Accion> comboRequerido = (p.tipo == TipoJugador.MAGE) ? partida.mageCombo : partida.warriorCombo;
        
        // Si no hay combo definido o el historial es muy corto, ignoramos
        if (comboRequerido == null || comboRequerido.isEmpty() || p.historial.size() < comboRequerido.size()) {
            return false;
        }

        // Revisamos si los últimos movimientos coinciden con la secuencia del combo
        int inicio = p.historial.size() - comboRequerido.size();
        for (int i = 0; i < comboRequerido.size(); i++) {
            if (p.historial.get(inicio + i) != comboRequerido.get(i)) {
                return false;
            }
        }
        return true;
    }

    private void aplicarBonificacionesFinales(Combatiente p1, Combatiente p2, Partida partida) {
        // Bonificación de Victoria (Quien tenga más salud)
        if (p1.salud > p2.salud) p1.puntuacion += partida.victoryBonus;
        else if (p2.salud > p1.salud) p2.puntuacion += partida.victoryBonus;

        // Bonificación Low Health Victory (Ganar con menos del 20% de salud)
        if (p1.salud > 0 && p1.salud <= 20 && p1.salud > p2.salud) p1.puntuacion += partida.lowHealthVictory;
        if (p2.salud > 0 && p2.salud <= 20 && p2.salud > p1.salud) p2.puntuacion += partida.lowHealthVictory;
    }


    private Partida buscarPartida(String id) {
        return partidasAST.stream().filter(p -> p.id.equals(id)).findFirst().orElse(null);
    }

    private Estrategia buscarEstrategia(String id) {
        return estrategiasAST.stream().filter(e -> e.id.equals(id)).findFirst().orElse(null);
    }
}