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
    
    public enum TipoFuncion { GET_MOVE, LAST_MOVE, GET_MOVES_COUNT, GET_LAST_N_MOVES }
    public enum Target { SELF, OPPONENT }

    private TipoFuncion tipo;
    private Target target;
    private Expresion argEntero;
    private Accion argAccion;

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

    @Override
    public Object evaluar(ContextoEjecucion ctx) {
        // Obtenemos la lista del historial que nos interesa
        List<Accion> history = (target == Target.SELF) ? ctx.self.historial : ctx.opponent.historial;

        switch (tipo) {
            case LAST_MOVE:
                if (history.isEmpty()) return null; // Aún no hay movimientos
                return history.get(history.size() - 1); // Devuelve el último de la lista

            case GET_MOVE:
                // argEntero es el número de la ronda (empezando en 1)
                int ronda = (int) argEntero.evaluar(ctx);
                if (ronda > 0 && ronda <= history.size()) {
                    return history.get(ronda - 1);
                }
                return null;

            case GET_MOVES_COUNT:
                int contador = 0;
                for (Accion a : history) {
                    if (a == argAccion) contador++;
                }
                return contador;

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