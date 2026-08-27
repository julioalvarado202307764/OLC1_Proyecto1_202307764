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
public class Combatiente {
    public String nombre;
    public TipoJugador tipo;
    public int salud;
    public int recurso;
    public int puntuacion;
    public List<Accion> historial;

    public Combatiente(String nombre, TipoJugador tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
        // Valores iniciales típicos para el inicio de una simulación
        this.salud = 100; 
        this.recurso = 100;
        this.puntuacion = 0;
        this.historial = new ArrayList<>();
    }

    public void registrarAccion(Accion accion) {
        this.historial.add(accion);
    }
}