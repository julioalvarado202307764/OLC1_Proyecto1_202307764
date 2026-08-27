/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto1.ast;
import java.util.List;

/**
 *
 * @author daish
 */
// Interfaz base para el AST lógico (Ej: self_health < 50)
public interface Condicion {
    boolean evaluar(ContextoEjecucion ctx); // El contexto tendrá la info de la partida actual
}


