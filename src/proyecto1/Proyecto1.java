/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto1;
import java.io.StringReader;
import proyecto1.analizadores.Lexer;
import proyecto1.analizadores.Parser;
/**
 *
 * @author daish
 */
public class Proyecto1 {

    public static void main(String[] args) {
        
        // Un fragmento de prueba válido según la estructura de BattleScript
        String entradaPrueba = 
            "mage Merlin {\n" +
            "  initial: ARCANE_BOLT\n" +
            "  rules: [\n" +
            "    if self_resource <= 20 then MEDITATE,\n" +
            "    else ARCANE_BOLT\n" +
            "  ]\n" +
            "}\n" +
            "main {\n" +
            "  run [DueloFinal] with {\n" +
            "    seed: 42\n" +
            "  }\n" +
            "}";

        try {
            System.out.println(" Iniciando analisis del codigo fuente...");
            
            // 1. Enviamos el texto al Analizador Léxico
            Lexer lexer = new Lexer(new StringReader(entradaPrueba));
            
            // 2. Pasamos los tokens al Analizador Sintáctico
            Parser parser = new Parser(lexer);
            
            // 3. Ejecutamos el análisis
            parser.parse();
            
            System.out.println(" Analisis completado! El codigo es sintacticamente correcto.");
            
        } catch (Exception e) {
            System.out.println(" Ocurrio un error durante el análisis:");
            e.printStackTrace();
        }
    }
}