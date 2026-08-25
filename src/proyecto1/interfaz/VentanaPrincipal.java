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
    private JTextArea txtEntrada, txtSalida, txtReporte;
    private JMenuItem menuNuevo, menuAbrir, menuGuardar, menuReportes, menuEjecutar;

    public VentanaPrincipal() {
        setTitle("BattleScript - IDE");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

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
        menuOpcionesEjecutar.add(menuEjecutar);

        barraMenu.add(menuArchivo);
        barraMenu.add(menuOpcionesReportes);
        barraMenu.add(menuOpcionesEjecutar);
        setJMenuBar(barraMenu);

        // --- 2. ÁREAS DE TEXTO ---
        txtEntrada = new JTextArea();
        txtReporte = new JTextArea();
        txtSalida = new JTextArea();

        // Paneles con Scroll
        JScrollPane scrollEntrada = new JScrollPane(txtEntrada);
        scrollEntrada.setBorder(BorderFactory.createTitledBorder("Entrada"));
        
        JScrollPane scrollReporte = new JScrollPane(txtReporte);
        scrollReporte.setBorder(BorderFactory.createTitledBorder("Reporte"));
        
        JScrollPane scrollSalida = new JScrollPane(txtSalida);
        scrollSalida.setBorder(BorderFactory.createTitledBorder("Salida"));

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}