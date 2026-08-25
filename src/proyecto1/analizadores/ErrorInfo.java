/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.analizadores;

/**
 *
 * @author daish
 */
public class ErrorInfo {
    public String tipo;
    public String descripcion;
    public int linea;
    public int columna;

    public ErrorInfo(String tipo, String descripcion, int linea, int columna) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.linea = linea;
        this.columna = columna;
    }
}