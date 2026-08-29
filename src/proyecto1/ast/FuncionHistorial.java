/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author daish
 */
public class FuncionHistorial implements Expresion {

    public enum TipoFuncion {
        GET_MOVE, LAST_MOVE, GET_MOVES_COUNT, GET_LAST_N_MOVES
    }

    public enum Target {
        SELF, OPPONENT
    }

    private TipoFuncion tipo;
    private Target target;
    private Expresion argEntero;
    private Accion argAccion;
    private FuncionHistorial fuenteAnidada;

    // Constructor para LAST_MOVE
    public FuncionHistorial(TipoFuncion tipo, Target target) {
        this.tipo = tipo;
        this.target = target;
    }

    // Constructor para GET_MOVE y GET_LAST_N_MOVES
    public FuncionHistorial(TipoFuncion tipo, Target target, Expresion argEntero) {
        this.tipo = tipo;
        this.target = target;
        this.argEntero = argEntero;
    }

    // Constructor para GET_MOVES_COUNT
    public FuncionHistorial(TipoFuncion tipo, Target target, Accion argAccion) {
        this.tipo = tipo;
        this.target = target;
        this.argAccion = argAccion;
    }

    // Constructor para GET_MOVES_COUNT anidado: get_moves_count(get_last_n_moves(...), ACCION)
    public FuncionHistorial(FuncionHistorial fuenteAnidada, Accion argAccion) {
        this.tipo = TipoFuncion.GET_MOVES_COUNT;
        this.fuenteAnidada = fuenteAnidada;
        this.argAccion = argAccion;
    }

    @Override
    public Object evaluar(ContextoEjecucion ctx) {
        // Obtenemos la lista del historial que nos interesa
        List<Accion> history = (target == Target.SELF) ? ctx.self.historial : ctx.opponent.historial;

        switch (tipo) {
            case LAST_MOVE:
                if (history.isEmpty()) {
                    return null; // Aún no hay movimientos
                }
                return history.get(history.size() - 1); // Devuelve el último de la lista

            case GET_MOVE:
                // PDF sección 6.5: los índices comienzan en 0.
                int indice = (int) argEntero.evaluar(ctx);
                if (indice < 0 || indice >= history.size()) {
                    throw new RuntimeException("Error fatal: get_move fuera de rango (n=" + indice
                            + ", tamaño del historial=" + history.size() + ").");
                }
                return history.get(indice);

            case GET_MOVES_COUNT: {
                // Si viene de una función anidada (ej. get_last_n_moves), contamos sobre esa sublista.
                // Si no, usamos el historial completo de self/opponent como antes.
                @SuppressWarnings("unchecked")
                List<Accion> fuente = (fuenteAnidada != null)
                        ? (List<Accion>) fuenteAnidada.evaluar(ctx)
                        : history;

                int contador = 0;
                for (Accion a : fuente) {
                    if (a == argAccion) {
                        contador++;
                    }
                }
                return contador;
            }

            case GET_LAST_N_MOVES:
                int n = (int) argEntero.evaluar(ctx);
                List<Accion> subList = new ArrayList<>();
                int start = Math.max(0, history.size() - n);
                for (int i = start; i < history.size(); i++) {
                    subList.add(history.get(i));
                }
                return subList;
        }
        return null;
    }
}
