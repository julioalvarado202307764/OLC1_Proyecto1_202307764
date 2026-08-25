/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.interfaz;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author daish
 */
public class VentanaPrincipal extends JFrame {

    // Componentes principales
    private JTextArea txtEntrada, txtSalida;
    private JTable tablaReportes;
    private javax.swing.table.DefaultTableModel modeloTabla;
    private JMenuItem menuNuevo, menuAbrir, menuGuardar, menuReportes, menuEjecutar;

    public VentanaPrincipal() {
        setTitle("BattleScript - IDE");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        // --- 1. BARRA DE MENÚ ---
        JMenuBar barraMenu = new JMenuBar();
        
        JMenu menuArchivo = new JMenu("Archivo");
        menuNuevo = new JMenuItem("Nuevo");
        menuAbrir = new JMenuItem("Abrir");
        menuGuardar = new JMenuItem("Guardar Archivo");
        menuNuevo.addActionListener(e -> txtEntrada.setText(""));
        menuAbrir.addActionListener(e -> abrirArchivo());
        menuGuardar.addActionListener(e -> guardarArchivo());
        menuArchivo.add(menuNuevo);
        menuArchivo.add(menuAbrir);
        menuArchivo.add(menuGuardar);
        
        
        JMenu menuOpcionesReportes = new JMenu("Reportes");
        menuReportes = new JMenuItem("Ver Reportes");
        menuOpcionesReportes.add(menuReportes);

        JMenu menuOpcionesEjecutar = new JMenu("Ejecutar");
        menuEjecutar = new JMenuItem("Analizar Código");
        menuEjecutar.addActionListener(e -> ejecutarAnalisis());
        menuOpcionesEjecutar.add(menuEjecutar);

        barraMenu.add(menuArchivo);
        barraMenu.add(menuOpcionesReportes);
        barraMenu.add(menuOpcionesEjecutar);
        setJMenuBar(barraMenu);

        // --- 2. ÁREAS DE TEXTO ---
        txtEntrada = new JTextArea();
        txtSalida = new JTextArea();

        // Paneles con Scroll
        JScrollPane scrollEntrada = new JScrollPane(txtEntrada);
        scrollEntrada.setBorder(BorderFactory.createTitledBorder("Entrada"));
               
        JScrollPane scrollSalida = new JScrollPane(txtSalida);
        scrollSalida.setBorder(BorderFactory.createTitledBorder("Salida"));
        
        // --- CONFIGURACIÓN DE LA TABLA DE REPORTES ---
        String[] columnas = {"#", "Lexema", "Tipo", "Línea", "Columna"};
        modeloTabla = new javax.swing.table.DefaultTableModel(columnas, 0);
        tablaReportes = new JTable(modeloTabla);
        
        JScrollPane scrollReporte = new JScrollPane(tablaReportes);
        scrollReporte.setBorder(BorderFactory.createTitledBorder("Reporte de Tokens"));

        // --- 3. DISTRIBUCIÓN (Layout) ---
        JPanel panelSuperior = new JPanel(new GridLayout(1, 2, 10, 10));
        panelSuperior.add(scrollEntrada);
        panelSuperior.add(scrollReporte);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.add(panelSuperior, BorderLayout.CENTER);
        
        scrollSalida.setPreferredSize(new Dimension(800, 200));
        panelPrincipal.add(scrollSalida, BorderLayout.SOUTH);

        add(panelPrincipal);
    }
    //BOTONES PARA ABRIR/GUARDAR/LIMPIAR
    private void abrirArchivo() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos BattleScript (*.btl)", "btl"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
                txtEntrada.read(br, null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarArchivo() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Aseguramos que se guarde con la extensión correcta
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            if (!ruta.endsWith(".btl")) {
                ruta += ".btl";
            }
            
            try (FileWriter fw = new FileWriter(ruta)) {
                txtEntrada.write(fw);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    //BOTON PARA ANALIZAR CODIGO
    private void ejecutarAnalisis() {
        // Limpiamos áreas y tablas
        txtSalida.setText("");
        modeloTabla.setRowCount(0); // Limpia las filas de la tabla
        String codigo = txtEntrada.getText();
        
        // Limpiamos el área de salida antes de cada nueva ejecución
        txtSalida.setText("");
        
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El área de entrada está vacía.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            txtSalida.append(" Iniciando análisis del código fuente...\n");
            
            // 1. Enviamos el texto al Analizador Léxico
            java.io.StringReader reader = new java.io.StringReader(codigo);
            proyecto1.analizadores.Lexer lexer = new proyecto1.analizadores.Lexer(reader);
            
            // 2. Pasamos los tokens al Analizador Sintáctico
            proyecto1.analizadores.Parser parser = new proyecto1.analizadores.Parser(lexer);
            
            // 3. Ejecutamos el análisis
            parser.parse();
            // 4. Llenamos la tabla de Reporte de Tokens
            int contador = 1;
            for (proyecto1.analizadores.TokenInfo t : lexer.listaTokens) {
                modeloTabla.addRow(new Object[]{
                    contador, 
                    t.lexema, 
                    t.tipo, 
                    t.linea, 
                    t.columna
                });
                contador++;
            }
            txtSalida.append("\n ¡Análisis completado exitosamente!\n");
            txtSalida.append("El código es sintácticamente correcto y cumple con la gramática de BattleScript.\n");
            
        } catch (Exception ex) {
            txtSalida.append("\n OcurriÓ un error durante el análisis.\n");
            txtSalida.append("Revisa la sintaxis de tu código o la consola para más detalles.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}