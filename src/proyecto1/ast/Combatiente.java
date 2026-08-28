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
    public int saludMaxima;
    public int recurso;
    public int recursoMaximo;
    
    // Estadísticas base (PDF)
    public int ataqueFisico;
    public int ataqueMagico;
    public int armadura;
    public int resistenciaMagica;
    public int velocidad;
    
    // Variables de estado
    public int puntuacion;
    public int buffProximoAtaque; // Para el WAR_CRY
    public List<Accion> historial;

    public Combatiente(String nombre, TipoJugador tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.puntuacion = 0;
        this.buffProximoAtaque = 0;
        this.historial = new ArrayList<>();

        if (tipo == TipoJugador.WARRIOR) {
            this.saludMaxima = 140; this.salud = 140;
            this.recursoMaximo = 100; this.recurso = 100;
            this.ataqueFisico = 22; this.ataqueMagico = 0;
            this.armadura = 20; this.resistenciaMagica = 8;
            this.velocidad = 10;
        } else { // MAGE
            this.saludMaxima = 100; this.salud = 100;
            this.recursoMaximo = 120; this.recurso = 120; // Maná
            this.ataqueFisico = 5; this.ataqueMagico = 25;
            this.armadura = 8; this.resistenciaMagica = 18;
            this.velocidad = 14;
        }
    }

    public void registrarAccion(Accion accion) {
        this.historial.add(accion);
    }
}