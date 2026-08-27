/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ast;

/**
 *
 * @author daish
 */
public class OperacionRelacional implements Condicion {
    private Expresion izq;
    private Expresion der;
    private String operador;

    public OperacionRelacional(Expresion izq, String operador, Expresion der) {
        this.izq = izq;
        this.operador = operador;
        this.der = der;
    }

    @Override
    public boolean evaluar(ContextoEjecucion ctx) {
        Object valIzq = izq.evaluar(ctx);
        Object valDer = der.evaluar(ctx);

        // Si son números, los comparamos como doubles
        if (valIzq instanceof Number && valDer instanceof Number) {
            double numIzq = ((Number) valIzq).doubleValue();
            double numDer = ((Number) valDer).doubleValue();

            switch (operador) {
                case "==": return numIzq == numDer;
                case "!=": return numIzq != numDer;
                case "<":  return numIzq < numDer;
                case ">":  return numIzq > numDer;
                case "<=": return numIzq <= numDer;
                case ">=": return numIzq >= numDer;
            }
        } else {
            // Para comparar Enum (Acciones) o Strings (si aplican)
            switch (operador) {
                case "==": return valIzq.equals(valDer);
                case "!=": return !valIzq.equals(valDer);
            }
        }
        return false;
    }
}