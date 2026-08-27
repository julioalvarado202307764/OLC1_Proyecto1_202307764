/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ast;

/**
 *
 * @author daish
 */
public class Regla {
    public Condicion condicion;
    public Accion accionThen;

    public Regla(Condicion condicion, Accion accionThen) {
        this.condicion = condicion;
        this.accionThen = accionThen;
    }
}