package recursos;

import clases.SesionUsuario;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import controlador.trabajadorController;
import clases.Trabajador;
import clases.Usuario;
import vistas.frmNuevoTrabajador;

/**
 *
 * @author Administrador
 */
public class ButtonPanelCellTrabajador extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private final JPanel panel;
    private final JButton btnEditar;
    private final JButton btnEliminar;
    private final trabajadorController controller = new trabajadorController();
    private JTable table;

    public ButtonPanelCellTrabajador(JTable table) {
        this.table = table;

        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);

        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        // 🔹 Estilos visuales
        btnEditar.setBackground(new Color(52, 152, 219));
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        btnEditar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEditar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnEliminar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // 🔹 Configurar layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 0); // espacio entre botones
        gbc.anchor = GridBagConstraints.EAST; // 🔸 alinear contenido a la derecha

        // 🔹 Añadir botones al panel alineados a la derecha
        gbc.gridx = 0;
        panel.add(btnEditar, gbc);
        gbc.gridx = 1;
        panel.add(btnEliminar, gbc);

        // 🔹 Ajuste de tamaño
        panel.setPreferredSize(new Dimension(150, 25));
        panel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Acción Editar
        btnEditar.addActionListener(e -> {
            int fila = table.getSelectedRow(); // Usamos getSelectedRow() (más seguro)
            if (fila < 0) {
                JOptionPane.showMessageDialog(null, "Seleccione una fila para editar.");
                return;
            }

            try {
                Trabajador t = new Trabajador(0, "----Seleccione Trabajador----");
                t.setId((int) table.getValueAt(fila, 0));
                t.setNombres(table.getValueAt(fila, 1).toString());
                t.setApellidos(table.getValueAt(fila, 2).toString());
                t.setEspecialidad(table.getValueAt(fila, 3).toString());
                t.setHorario(table.getValueAt(fila, 4).toString());  // Turno (texto)
                t.setCodArea(table.getValueAt(fila, 5).toString()); // Área (texto)

                // ✅ Leer correctamente el valor del estado
                Object estadoValor = table.getValueAt(fila, 6);
                boolean estadoActivo = false;

                if (estadoValor instanceof Boolean) {
                    estadoActivo = (Boolean) estadoValor;
                } else if (estadoValor instanceof String) {
                    estadoActivo = ((String) estadoValor).equalsIgnoreCase("Activo");
                }
                t.setEstado(estadoActivo);

                // Abrir formulario de edición
                frmNuevoTrabajador form = new frmNuevoTrabajador();
                form.cargarDatosParaEditar(t);

                // Si el formulario está dentro de un JDesktopPane:
                JDesktopPane desktop = (JDesktopPane) SwingUtilities.getAncestorOfClass(JDesktopPane.class, table);
                if (desktop != null) {
                    desktop.add(form);
                    form.setVisible(true);
                    form.toFront();
                } else {
                    // Si no hay desktop, abrir como ventana normal
                    form.setVisible(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error al obtener datos de la fila: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            fireEditingStopped();
        });

        // Acción Eliminar
        btnEliminar.addActionListener(e -> {
            Usuario usr = SesionUsuario.getUsuarioActual();

            // 🔹 Verificar permisos
            if (usr == null || !usr.getPermisos().contains("Eliminar_Trabajadores")) {
                JOptionPane.showMessageDialog(null,
                        "🚫 No tienes permiso para Eliminar trabajadores.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int fila = table.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
                return;
            }

            int id = (int) table.getValueAt(fila, 0);// Columna 0 = Id_Trabajador
            String estado = (String) table.getValueAt(fila, 6); // Columna 6 = Estado

            // Solo permitir eliminar si está inactivo
            if (estado.equals("Activo")) {
                JOptionPane.showMessageDialog(
                        null,
                        "⚠️ No se puede eliminar un trabajador activo. Primero desactívelo.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "¿Eliminar trabajador con ID " + id + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                trabajadorController controller = new trabajadorController();
                Trabajador t = new Trabajador(0, "----Seleccione Trabajador----");
                t.setId(id);

                if (controller.eliminarTrabajador(t)) {
                    ((DefaultTableModel) table.getModel()).removeRow(fila);
                    JOptionPane.showMessageDialog(null,
                            "✅ Trabajador eliminado correctamente.",
                            "Correcto",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "⚠️ Error al eliminar trabajador.",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return panel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return panel;
    }

}
