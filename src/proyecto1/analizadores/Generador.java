/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.analizadores;

public class Generador {

    public static void main(String[] args) {
        // Ajusta esta ruta según dónde esté tu carpeta 'src'
        String rutaBase = "src/proyecto1/analizadores/";
        
        try {
            System.out.println(" Iniciando generacion de analizadores...");
            
            // 1. Generar Lexer con JFlex
            String[] rutaJFlex = { rutaBase + "Lexer.jflex" };
            System.out.println("\n--- Compilando JFlex ---");
            jflex.Main.generate(rutaJFlex);
            
            // 2. Generar Parser y sym con CUP
            String[] rutaCUP = { 
                "-destdir", rutaBase, 
                "-parser", "Parser", 
                rutaBase + "Parser.cup" 
            };
            System.out.println("\n--- Compilando CUP ---");
            java_cup.Main.main(rutaCUP);
            
            System.out.println("\n Generacion exitosa! Actualiza tu proyecto en el IDE.");
            
        } catch (Exception e) {
            System.out.println("\n Ocurrio un error al generar los analizadores:");
            e.printStackTrace();
        }
    }
}