/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.analizadores;

/**
 *
 * @author daish
 */
public class TokenInfo {
    public String lexema;
    public String tipo;
    public int linea;
    public int columna;

    public TokenInfo(String lexema, String tipo, int linea, int columna) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
    }
}