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

        boolean terminoPorMuerte = false;

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

            // 3. Resolver la ronda (Cálculo de daño, recursos y puntos)
            resolverInteraccion(p1, accionP1, p2, accionP2, partida);

            // 4. Reporte de la ronda
            bitacora.append("Ronda ").append(ronda).append(": ")
                    .append(p1.nombre).append(" (").append(accionP1).append(") vs ")
                    .append(p2.nombre).append(" (").append(accionP2).append(")\n");

            // Si alguno muere antes de llegar al límite de rondas, termina la partida
            if (p1.salud <= 0 || p2.salud <= 0) {
                terminoPorMuerte = true;
                break;
            }
        }

        // 5. Asignar Bonificaciones Finales (Combos y Low Health)
        Combatiente ganador = determinarGanadorYAplicarBonos(p1, p2, partida, terminoPorMuerte);

        // 6. Reportar resultado
        bitacora.append("\n--- RESULTADO FINAL ---\n");
        bitacora.append(p1.nombre).append(" -> Vida: ").append(p1.salud)
                .append(" | Recurso: ").append(p1.recurso)
                .append(" | Puntos: ").append(p1.puntuacion).append("\n");
        bitacora.append(p2.nombre).append(" -> Vida: ").append(p2.salud)
                .append(" | Recurso: ").append(p2.recurso)
                .append(" | Puntos: ").append(p2.puntuacion).append("\n");

        if (ganador != null) {
            bitacora.append("👑 GANADOR: ").append(ganador.nombre).append("!\n\n");
        } else {
            bitacora.append("🤝 EMPATE TÉCNICO!\n\n");
        }
    }

    private Accion decidirAccion(Estrategia est, ContextoEjecucion ctx) {
        // En la ronda 1 siempre se ejecuta initial
        if (ctx.roundActual == 1) {
            return est.accionInicial;
        }

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
        // 1. Cobrar recursos y registrar historial (no depende del orden de turno)
        boolean exitoP1 = consumirRecurso(p1, a1, partida);
        boolean exitoP2 = consumirRecurso(p2, a2, partida);

        if (exitoP1) {
            p1.registrarAccion(a1);
        }
        if (exitoP2) {
            p2.registrarAccion(a2);
        }

        // 2. Determinar quién actúa primero (PDF sección 7.4)
        boolean primeroEsP1 = p1VaPrimero(a1, a2, p1, p2);

        if (primeroEsP1) {
            ejecutarAccionIndividual(p1, a1, exitoP1, p2, a2, exitoP2, partida);
            if (p2.salud > 0) {
                ejecutarAccionIndividual(p2, a2, exitoP2, p1, a1, exitoP1, partida);
            }
        } else {
            ejecutarAccionIndividual(p2, a2, exitoP2, p1, a1, exitoP1, partida);
            if (p1.salud > 0) {
                ejecutarAccionIndividual(p1, a1, exitoP1, p2, a2, exitoP2, partida);
            }
        }

        // 3. Verificar combos (con los historiales ya actualizados)
        verificarYAplicarCombo(p1, partida, exitoP1);
        verificarYAplicarCombo(p2, partida, exitoP2);
    }

// Resuelve TODOS los efectos de la acción de un solo combatiente
    private void ejecutarAccionIndividual(Combatiente actor, Accion accionActor, boolean exitoActor,
            Combatiente rival, Accion accionRival, boolean exitoRival,
            Partida partida) {
        if (!exitoActor) {
            return; // la acción falló por falta de recurso, no produce ningún efecto
        }
        switch (accionActor) {
            case WAR_CRY:
                actor.buffProximoAtaque += 10;
                break;

            case REST:
            case MEDITATE:
                recuperarRecurso(actor, 25);
                break;

            case HEALING_RUNE:
                aplicarCuracion(actor, 25, partida);
                break;

            case SHIELD_BLOCK:
            case MAGIC_BARRIER:
                // No hace nada por sí sola aquí; se consulta cuando el RIVAL calcula su daño.
                break;

            case SLASH:
            case HEAVY_STRIKE:
            case ARCANE_BOLT:
            case FIREBALL:
                int danio = calcularDañoReal(actor, accionActor, rival);

                boolean rivalSeDefiende = exitoRival
                        && (accionRival == Accion.SHIELD_BLOCK || accionRival == Accion.MAGIC_BARRIER);

                if (danio > 0 && rivalSeDefiende) {
                    danio /= 2;
                    rival.puntuacion += partida.defPoint;
                    bitacora.append("   🛡️ ").append(rival.nombre).append(" bloqueó el 50% del daño.\n");
                }

                if (danio > 0) {
                    rival.salud -= danio;
                    actor.puntuacion += danio * partida.dmgPoint;
                }
                break;
        }
    }

// Tabla de prioridad exacta del PDF (sección 7.4)
    private int obtenerPrioridad(Accion a) {
        switch (a) {
            case MAGIC_BARRIER:
            case SHIELD_BLOCK:
                return 7;
            case WAR_CRY:
                return 6;
            case HEALING_RUNE:
                return 5;
            case ARCANE_BOLT:
            case SLASH:
                return 4;
            case FIREBALL:
            case HEAVY_STRIKE:
                return 2;
            case MEDITATE:
            case REST:
                return 1;
            default:
                return 0;
        }
    }

// Decide quién actúa primero: prioridad -> velocidad -> posición en players
    private boolean p1VaPrimero(Accion a1, Accion a2, Combatiente p1, Combatiente p2) {
        int prio1 = obtenerPrioridad(a1);
        int prio2 = obtenerPrioridad(a2);
        if (prio1 != prio2) {
            return prio1 > prio2;
        }

        if (p1.velocidad != p2.velocidad) {
            return p1.velocidad > p2.velocidad;
        }

        return true; // empate total -> gana p1, que es el primero en "players: [...]"
    }
    // --- MÉTODOS AUXILIARES ---

    private boolean consumirRecurso(Combatiente c, Accion a, Partida partida) {
        int costo = 0;
        switch (a) {
            case SLASH:
            case ARCANE_BOLT:
                costo = 10;
                break;
            case SHIELD_BLOCK:
                costo = 15;
                break;
            case WAR_CRY:
            case MAGIC_BARRIER:
                costo = 20;
                break;
            case HEAVY_STRIKE:
                costo = 25;
                break;
            case HEALING_RUNE:
                costo = 30;
                break;
            case REST:
            case MEDITATE:
                costo = 0;
                break;
            case FIREBALL:
                costo = 30;
                break;
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
        if (c.recurso > c.recursoMaximo) {
            c.recurso = c.recursoMaximo;
        }
    }

    private void aplicarCuracion(Combatiente c, int cantidad, Partida partida) {
        int saludAntes = c.salud;
        c.salud += cantidad;
        if (c.salud > c.saludMaxima) {
            c.salud = c.saludMaxima;
        }

        int recuperadoReal = c.salud - saludAntes; // lo que realmente subió, ya con el tope aplicado
        c.puntuacion += recuperadoReal * partida.healPoint;
    }

    private int calcularDañoReal(Combatiente atacante, Accion accion, Combatiente defensor) {
        int poder = 0;
        int danioCalculado = 0;

        if (accion == Accion.SLASH) {
            poder = 12;
        } else if (accion == Accion.HEAVY_STRIKE) {
            poder = 25;
        } else if (accion == Accion.ARCANE_BOLT) {
            poder = 12;
        } else if (accion == Accion.FIREBALL) {
            poder = 25;
        }

        if (poder == 0) {
            return 0; // Si no es ataque, retorna 0
        }
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

        return Math.max(1, danioCalculado); // El daño mínimo es 0
    }

    private void verificarYAplicarCombo(Combatiente p, Partida partida, boolean accionExitosa) {
        if (!accionExitosa) {
            return; // Si la última acción falló, no hay combo
        }
        List<Accion> comboRequerido = (p.tipo == TipoJugador.MAGE) ? partida.mageCombo : partida.warriorCombo;
        if (comboRequerido == null || comboRequerido.isEmpty() || p.historial.size() < comboRequerido.size()) {
            return;
        }

        int inicio = p.historial.size() - comboRequerido.size();
        for (int i = 0; i < comboRequerido.size(); i++) {
            if (p.historial.get(inicio + i) != comboRequerido.get(i)) {
                return;
            }
        }

        // Si llegamos aquí, completó el combo
        p.puntuacion += (p.tipo == TipoJugador.MAGE) ? partida.mageComboPoints : partida.warriorComboPoints;
        bitacora.append("   🔥 ¡").append(p.nombre).append(" conectó su COMBO!\n");
    }

    private Combatiente determinarGanadorYAplicarBonos(Combatiente p1, Combatiente p2, Partida partida, boolean terminoPorMuerte) {
        Combatiente ganador;

        if (terminoPorMuerte) {
            // Derrota directa (PDF 7.5): gana quien sobrevive, sin importar el puntaje.
            if (p1.salud <= 0 && p2.salud > 0) {
                ganador = p2;
            } else if (p2.salud <= 0 && p1.salud > 0) {
                ganador = p1;
            } else {
                ganador = null; // caso extremo: doble KO el mismo turno -> empate
            }
        } else {
            // Límite de rondas con ambos vivos (PDF 7.5): puntaje -> vida -> recurso -> empate.
            if (p1.puntuacion != p2.puntuacion) {
                ganador = (p1.puntuacion > p2.puntuacion) ? p1 : p2;
            } else if (p1.salud != p2.salud) {
                ganador = (p1.salud > p2.salud) ? p1 : p2;
            } else if (p1.recurso != p2.recurso) {
                ganador = (p1.recurso > p2.recurso) ? p1 : p2;
            } else {
                ganador = null; // empate total
            }
        }

        if (ganador != null) {
            ganador.puntuacion += partida.victoryBonus;

            // De paso, arreglo el umbral de low_health_victory (25% de SU vida máxima, no un 20 fijo)
            int umbral = (int) (ganador.saludMaxima * 0.25);
            if (ganador.salud > 0 && ganador.salud <= umbral) {
                ganador.puntuacion += partida.lowHealthVictory;
            }
        }

        return ganador;
    }

    private Partida buscarPartida(String id) {
        return partidasAST.stream().filter(p -> p.id.equals(id)).findFirst().orElse(null);
    }

    private Estrategia buscarEstrategia(String id) {
        return estrategiasAST.stream().filter(e -> e.id.equals(id)).findFirst().orElse(null);
    }
}
