/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ast;

/**
 *
 * @author daish
 */
public class Literal implements Expresion {
    private Object valor;

    public Literal(Object valor) {
        this.valor = valor;
    }

    @Override
    public Object evaluar(ContextoEjecucion ctx) {
        return valor; 
    }
}