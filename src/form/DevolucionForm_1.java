package sv.edu.udb.biblioteca;

import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class DevolucionForm extends javax.swing.JFrame {

    private PrestamoDAO prestamoDAO = new PrestamoDAO();

    public DevolucionForm() {
        initComponents();
        cargarPrestamosActivos();
    }

    private void btnRegistrarDevolucionActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int fila = tablaPrestamos.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un préstamo.");
                return;
            }

            int prestamoId = Integer.parseInt(tablaPrestamos.getValueAt(fila, 0).toString());

            boolean exito = prestamoDAO.registrarDevolucion(prestamoId, LocalDate.now());

            if (exito) {
                JOptionPane.showMessageDialog(this, "Devolución registrada.");
                cargarPrestamosActivos();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error:\n" + ex.getMessage());
        }
    }

    private void cargarPrestamosActivos() {
        try {
            var lista = prestamoDAO.listarPrestamosActivos();

            if (lista == null || lista.isEmpty()) {
                tablaPrestamos.setModel(new DefaultTableModel());
                return;
            }

            String[] columnas = {"ID", "Usuario", "Material", "Fecha Préstamo", "Fecha Límite"};
            Object[][] datos = new Object[lista.size()][5];

            for (int i = 0; i < lista.size(); i++) {
                Prestamo p = lista.get(i);
                datos[i][0] = p.getId();
                datos[i][1] = p.getUsuarioNombre();
                datos[i][2] = p.getMaterialTitulo();
                datos[i][3] = p.getFechaPrestamo();
                datos[i][4] = p.getFechaLimite();
            }

            tablaPrestamos.setModel(new DefaultTableModel(datos, columnas));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error:\n" + ex.getMessage());
        }
    }
}
