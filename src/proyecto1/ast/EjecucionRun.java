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
public class EjecucionRun {
    public List<String> partidas;
    public int seed;

    public EjecucionRun(List<String> partidas, int seed) {
        this.partidas = partidas;
        this.seed = seed;
    }
}