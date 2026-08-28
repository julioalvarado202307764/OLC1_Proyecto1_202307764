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
    private int semilla;
    
    public MotorSimulacion(List<Estrategia> estrategiasAST, List<Partida> partidasAST, 
                           List<String> partidasAEjecutar, int semilla) {
        this.estrategiasAST = estrategiasAST;
        this.partidasAST = partidasAST;
        this.partidasAEjecutar = partidasAEjecutar;
        this.semilla = semilla;
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
        
        Random randP1 = new Random(this.semilla); 
        Random randP2 = new Random(this.semilla + 1);
        
        for (int ronda = 1; ronda <= partida.rounds; ronda++) {
        // Generamos EXACTAMENTE UN VALOR aleatorio (0-100) por jugador en esta ronda            
            int randomP1 = randP1.nextInt(101);
            int randomP2 = randP2.nextInt(101);
            
            //1. Crear el contexto con su número aleatorio fijo
            ContextoEjecucion ctx1 = new ContextoEjecucion(p1, p2, ronda, partida.rounds, randomP1); 
            ContextoEjecucion ctx2 = new ContextoEjecucion(p2, p1, ronda, partida.rounds, randomP2); 

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
        // 1. Cobrar Recursos (Si no tienen suficiente, fallan la acción)
        boolean exitoP1 = consumirRecurso(p1, a1, partida);
        boolean exitoP2 = consumirRecurso(p2, a2, partida);

        // 2. Aplicar Mejoras (Prioridad 6)
        if (exitoP1 && a1 == Accion.WAR_CRY) p1.buffProximoAtaque += 10;
        if (exitoP2 && a2 == Accion.WAR_CRY) p2.buffProximoAtaque += 10;

        // 3. Aplicar Recuperaciones (Prioridad 1)
        if (exitoP1 && (a1 == Accion.REST || a1 == Accion.MEDITATE)) recuperarRecurso(p1, 25);
        if (exitoP2 && (a2 == Accion.REST || a2 == Accion.MEDITATE)) recuperarRecurso(p2, 25);

        // 4. Aplicar Curaciones (Prioridad 5)
        if (exitoP1 && a1 == Accion.HEALING_RUNE) aplicarCuracion(p1, 25, partida);
        if (exitoP2 && a2 == Accion.HEALING_RUNE) aplicarCuracion(p2, 25, partida);

        // 5. Calcular Daño Crudo (Prioridad 2 y 4)
        int dmgA_P1 = exitoP2 ? calcularDañoReal(p2, a2, p1) : 0;
        int dmgA_P2 = exitoP1 ? calcularDañoReal(p1, a1, p2) : 0;

        // 6. Aplicar Defensas (Prioridad 7)
        if (dmgA_P1 > 0 && exitoP1 && (a1 == Accion.SHIELD_BLOCK || a1 == Accion.MAGIC_BARRIER)) {
            dmgA_P1 /= 2;
            p1.puntuacion += partida.defPoint;
            bitacora.append("   🛡️ ").append(p1.nombre).append(" bloqueó el 50% del daño.\n");
        }
        if (dmgA_P2 > 0 && exitoP2 && (a2 == Accion.SHIELD_BLOCK || a2 == Accion.MAGIC_BARRIER)) {
            dmgA_P2 /= 2;
            p2.puntuacion += partida.defPoint;
            bitacora.append("   🛡️ ").append(p2.nombre).append(" bloqueó el 50% del daño.\n");
        }

        // 7. Aplicar Daño Final a la Vida
        if (dmgA_P1 > 0) {
            p1.salud -= dmgA_P1;
            p2.puntuacion += partida.dmgPoint;
        }
        if (dmgA_P2 > 0) {
            p2.salud -= dmgA_P2;
            p1.puntuacion += partida.dmgPoint;
        }

        // 8. Verificar Combos
        verificarYAplicarCombo(p1, partida, exitoP1);
        verificarYAplicarCombo(p2, partida, exitoP2);
    }

    // --- MÉTODOS AUXILIARES ---

    private boolean consumirRecurso(Combatiente c, Accion a, Partida partida) {
        int costo = 0;
        switch (a) {
            case SLASH: case ARCANE_BOLT: costo = 10; break;
            case SHIELD_BLOCK: costo = 15; break;
            case WAR_CRY: case MAGIC_BARRIER: costo = 20; break;
            case HEAVY_STRIKE: case FIREBALL: costo = 25; break;
            case HEALING_RUNE: costo = 30; break;
            case REST: case MEDITATE: costo = 0; break;
        }
        
        if (c.recurso >= costo) {
            c.recurso -= costo;
            return true;
        } else {
            c.puntuacion -= partida.penalty; // Penalización por acción fallida
            bitacora.append("   ⚠️ ").append(c.nombre).append(" no tiene energía/maná para usar ").append(a).append("!\n");
            return false;
        }
    }

    private void recuperarRecurso(Combatiente c, int cantidad) {
        c.recurso += cantidad;
        if (c.recurso > c.recursoMaximo) c.recurso = c.recursoMaximo;
    }

    private void aplicarCuracion(Combatiente c, int cantidad, Partida partida) {
        c.salud += cantidad;
        if (c.salud > c.saludMaxima) c.salud = c.saludMaxima;
        c.puntuacion += partida.healPoint;
    }

    private int calcularDañoReal(Combatiente atacante, Accion accion, Combatiente defensor) {
        int poder = 0;
        int danioCalculado = 0;

        if (accion == Accion.SLASH) poder = 12;
        else if (accion == Accion.HEAVY_STRIKE) poder = 25;
        else if (accion == Accion.ARCANE_BOLT) poder = 12;
        else if (accion == Accion.FIREBALL) poder = 25;

        if (poder == 0) return 0; // Si no es ataque, retorna 0

        if (accion == Accion.SLASH || accion == Accion.HEAVY_STRIKE) {
            danioCalculado = poder + atacante.ataqueFisico - defensor.armadura;
        } else { // ARCANE_BOLT o FIREBALL
            danioCalculado = poder + atacante.ataqueMagico - defensor.resistenciaMagica;
        }

        // Aplicamos el buff del WAR_CRY si lo tiene y lo reiniciamos
        if (danioCalculado > 0) {
            danioCalculado += atacante.buffProximoAtaque;
            atacante.buffProximoAtaque = 0; 
        }

        return Math.max(0, danioCalculado); // El daño mínimo es 0
    }

    private void verificarYAplicarCombo(Combatiente p, Partida partida, boolean accionExitosa) {
        if (!accionExitosa) return; // Si la última acción falló, no hay combo

        List<Accion> comboRequerido = (p.tipo == TipoJugador.MAGE) ? partida.mageCombo : partida.warriorCombo;
        if (comboRequerido == null || comboRequerido.isEmpty() || p.historial.size() < comboRequerido.size()) {
            return;
        }

        int inicio = p.historial.size() - comboRequerido.size();
        for (int i = 0; i < comboRequerido.size(); i++) {
            if (p.historial.get(inicio + i) != comboRequerido.get(i)) return;
        }

        // Si llegamos aquí, completó el combo
        p.puntuacion += (p.tipo == TipoJugador.MAGE) ? partida.mageComboPoints : partida.warriorComboPoints;
        bitacora.append("   🔥 ¡").append(p.nombre).append(" conectó su COMBO!\n");
    }

    private void aplicarBonificacionesFinales(Combatiente p1, Combatiente p2, Partida partida) {
        if (p1.salud > p2.salud) p1.puntuacion += partida.victoryBonus;
        else if (p2.salud > p1.salud) p2.puntuacion += partida.victoryBonus;

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