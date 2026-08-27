/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ast;

/**
 *
 * @author daish
 */
public class OperacionLogica implements Condicion {
    private Condicion izq;
    private Condicion der;
    private String operador;

    // Constructor para AND / OR
    public OperacionLogica(Condicion izq, String operador, Condicion der) {
        this.izq = izq;
        this.operador = operador;
        this.der = der;
    }

    // Constructor para NOT
    public OperacionLogica(Condicion unica, String operador) {
        this.izq = unica;
        this.operador = operador;
        this.der = null;
    }

    @Override
    public boolean evaluar(ContextoEjecucion ctx) {
        if (operador.equals("NOT")) {
            return !izq.evaluar(ctx);
        }
        
        boolean resIzq = izq.evaluar(ctx);
        if (operador.equals("AND")) {
            return resIzq && der.evaluar(ctx);
        } else if (operador.equals("OR")) {
            return resIzq || der.evaluar(ctx);
        }
        
        return false;
    }
}