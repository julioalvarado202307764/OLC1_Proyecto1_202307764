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
    private JTable tablaTokens, tablaErrores;
    private javax.swing.table.DefaultTableModel modeloTokens, modeloErrores;
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
        // Tabla de Tokens
        String[] columnasTokens = {"#", "Lexema", "Tipo", "Línea", "Columna"};
        modeloTokens = new javax.swing.table.DefaultTableModel(columnasTokens, 0);
        tablaTokens = new JTable(modeloTokens);
        // Tabla de Errores
        String[] columnasErrores = {"#", "Tipo", "Descripción", "Línea", "Columna"};
        modeloErrores = new javax.swing.table.DefaultTableModel(columnasErrores, 0);
        tablaErrores = new JTable(modeloErrores);
        // Panel de Pestañas
        JTabbedPane panelReportes = new JTabbedPane();
        panelReportes.addTab("Tokens", new JScrollPane(tablaTokens));
        panelReportes.addTab("Errores", new JScrollPane(tablaErrores));
        panelReportes.setBorder(BorderFactory.createTitledBorder("Reportes del Sistema"));

        // --- 3. DISTRIBUCIÓN (Layout) ---
        JPanel panelSuperior = new JPanel(new GridLayout(1, 2, 10, 10));
        panelSuperior.add(scrollEntrada);
        panelSuperior.add(panelReportes);

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
        modeloTokens.setRowCount(0);
        modeloErrores.setRowCount(0);// Limpia las filas de la tabla
        String codigo = txtEntrada.getText();

        // Limpiamos el área de salida antes de cada nueva ejecución
        txtSalida.setText("");

        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El área de entrada está vacía.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

       try {
            txtSalida.append("🔥 Iniciando análisis del código fuente...\n");
            
            // 1. Limpiamos las tablas
            modeloTokens.setRowCount(0);
            modeloErrores.setRowCount(0);
            
            // 2. Preparamos los analizadores
            java.io.StringReader reader = new java.io.StringReader(codigo);
            proyecto1.analizadores.Lexer lexer = new proyecto1.analizadores.Lexer(reader);
            proyecto1.analizadores.Parser parser = new proyecto1.analizadores.Parser(lexer);
            
            // 3. AISLAMOS EL PARSER: Si entra en pánico, atrapamos el error aquí mismo
            // para que el programa siga su camino y llene las tablas.
            try {
                parser.parse();
            } catch (Exception fatalError) {
                // No hacemos nada, simplemente evitamos que el programa salte al catch principal
            }
            
            // 4. Llenamos la tabla de Tokens (¡Ahora este código SIEMPRE se ejecutará!)
            int contTokens = 1;
            for (proyecto1.analizadores.TokenInfo t : lexer.listaTokens) {
                modeloTokens.addRow(new Object[]{contTokens++, t.lexema, t.tipo, t.linea, t.columna});
            }
            
            // 5. Llenamos la tabla de Errores
            int contErrores = 1;
            for (proyecto1.analizadores.ErrorInfo e : lexer.listaErrores) {
                modeloErrores.addRow(new Object[]{contErrores++, e.tipo, e.descripcion, e.linea, e.columna});
            }
            for (proyecto1.analizadores.ErrorInfo e : parser.listaErroresSintacticos) {
                modeloErrores.addRow(new Object[]{contErrores++, e.tipo, e.descripcion, e.linea, e.columna});
            }
            
            // 6. Veredicto Final en la consola de Salida
            if (lexer.listaErrores.isEmpty() && parser.listaErroresSintacticos.isEmpty()) {
                txtSalida.append("\n✅ ¡Análisis completado exitosamente!\n");
                txtSalida.append("El código es sintácticamente correcto y no contiene errores.\n");
            } else {
                txtSalida.append("\n⚠️ Se encontraron errores en el código.\n");
                txtSalida.append("Se registraron " + (contErrores - 1) + " error(es).\n");
                txtSalida.append("Revisa la pestaña de 'Errores' para corregirlos.\n");
            }
            
        } catch (Exception ex) {
            txtSalida.append("\n❌ Ocurrió un error general en el sistema.\n");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}
