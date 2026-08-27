/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ast;

import java.util.List;

/**
 *
 * @author daish
 */
public class Estrategia {
    public String id;
    public TipoJugador tipo;
    public Accion accionInicial;
    public List<Regla> reglas;
    public Accion accionElse;

    public Estrategia(String id, TipoJugador tipo, Accion accionInicial, List<Regla> reglas, Accion accionElse) {
        this.id = id;
        this.tipo = tipo;
        this.accionInicial = accionInicial;
        this.reglas = reglas;
        this.accionElse = accionElse;
    }
}