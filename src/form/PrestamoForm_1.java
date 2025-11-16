package sv.edu.udb.biblioteca;

import java.sql.SQLException;
import java.time.LocalDate;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class PrestamoForm extends javax.swing.JFrame {

    private PrestamoDAO prestamoDAO = new PrestamoDAO();

    public PrestamoForm() {
        initComponents();
        cargarUsuarios();
        cargarMateriales();
    }

    private void btnRegistrarPrestamoActionPerformed(java.awt.event.ActionEvent evt) {

        try {
            Usuario usuario = (Usuario) cbUsuarios.getSelectedItem();
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario.");
                return;
            }

            Material material = (Material) cbMateriales.getSelectedItem();
            if (material == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un material.");
                return;
            }

            if (material.getCantidadDisponible() <= 0) {
                JOptionPane.showMessageDialog(this, "Este material no tiene unidades disponibles.");
                return;
            }

            if (prestamoDAO.usuarioTienePrestamoActivo(usuario.getId(), material.getId())) {
                JOptionPane.showMessageDialog(this, "Este usuario ya tiene un préstamo activo del mismo material.");
                return;
            }

            int dias = (int) spDias.getValue();
            if (dias < 1 || dias > 30) {
                JOptionPane.showMessageDialog(this, "Los días deben estar entre 1 y 30.");
                return;
            }

            LocalDate fechaPrestamo = LocalDate.now();
            LocalDate fechaLimite = fechaPrestamo.plusDays(dias);

            boolean exito = prestamoDAO.registrarPrestamo(
                    usuario.getId(),
                    material.getId(),
                    fechaPrestamo,
                    fechaLimite
            );

            if (exito) {
                JOptionPane.showMessageDialog(this, "Préstamo registrado.");
                cargarMateriales();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo registrar.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error:\n" + ex.getMessage());
        }
    }

    private void cargarUsuarios() {
        try {
            DefaultComboBoxModel<Usuario> model = new DefaultComboBoxModel<>();
            for (Usuario u : prestamoDAO.listarUsuarios()) {
                model.addElement(u);
            }
            cbUsuarios.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando usuarios.");
        }
    }

    private void cargarMateriales() {
        try {
            DefaultComboBoxModel<Material> model = new DefaultComboBoxModel<>();
            for (Material m : prestamoDAO.listarMateriales()) {
                model.addElement(m);
            }
            cbMateriales.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando materiales.");
        }
    }
}
