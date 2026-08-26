/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto1;
import java.io.StringReader;
import proyecto1.analizadores.Lexer;
import proyecto1.analizadores.Parser;
import proyecto1.interfaz.VentanaPrincipal;
import javax.swing.SwingUtilities;
/**
 *
 * @author daish
 */
public class Proyecto1 {

    public static void main(String[] args) {
        
        // Es una buena práctica de Java arrancar las interfaces gráficas (Swing) 
        // dentro de su propio hilo de ejecución para evitar cuelgues.
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
        
    }
}