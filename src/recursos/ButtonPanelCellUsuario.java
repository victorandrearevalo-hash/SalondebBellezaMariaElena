package recursos;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import controlador.usuarioController;
import clases.Usuario;
import vistas.frmNuevoUsuario;

/**
 *
 * @author Administrador
 */
public class ButtonPanelCellUsuario extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private final JPanel panel;
    private final JButton btnEditar;
    private final JButton btnEliminar;
    private JTable table;

    private final usuarioController controller = new usuarioController();

    public ButtonPanelCellUsuario(JTable table) {
        this.table = table;

        // 🔹 Usamos GridBagLayout para centrar perfectamente los botones
        panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);

        // 🔹 Pequeño margen interior para no pegar los botones al borde derecho
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6)); // margen derecho de 6px

        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");

        // 🔹 Estilos (opcional)
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
            int fila = table.getEditingRow();
            if (fila < 0) {
                fila = table.getSelectedRow(); // si no está editando, tomar fila seleccionada
            }
            if (fila < 0) {
                return;
            }

            try {
                Usuario usuario = extraerUsuarioDeTabla(fila);
                if (usuario == null) {
                    return;
                }

                frmNuevoUsuario form = new frmNuevoUsuario();
                form.cargarDatosParaEditar(usuario);
                form.setTablaUsuarios(table);

                JDesktopPane desktop = (JDesktopPane) SwingUtilities.getAncestorOfClass(JDesktopPane.class, table);
                if (desktop != null) {
                    desktop.add(form);
                    form.setVisible(true);
                }

                fireEditingStopped();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        // Acción Eliminar
        btnEliminar.addActionListener(e -> {
            int fila = table.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
                return;
            }

            int id = (int) table.getValueAt(fila, 0); // columna 0 = Id_Usuario
            String estado = (String) table.getValueAt(fila, 4); // columna 4 = Estado

            // Solo permitir eliminar si está inactivo
            if (estado.equals("Activo")) {
                JOptionPane.showMessageDialog(null, "⚠️ No se puede eliminar un usuario activo. Primero desactívelo.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "¿Eliminar usuario con ID " + id + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                Usuario u = new Usuario();
                u.setId(id); // ✅ asignar el ID antes de eliminar

                if (controller.eliminarUsuario(u)) {
                    ((DefaultTableModel) table.getModel()).removeRow(fila);
                    JOptionPane.showMessageDialog(null, "✅ Usuario eliminado correctamente.", "Correcto ", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "⚠️ Error al eliminar usuario.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    // ---------------------------
    // Método auxiliar para extraer usuario de la tabla
    // ---------------------------
    private Usuario extraerUsuarioDeTabla(int fila) {
        if (table == null || fila < 0 || fila >= table.getRowCount()) {
            return null;
        }

        try {
            Usuario u = new Usuario();
            u.setId((Integer) table.getValueAt(fila, 0));
            u.setNombre(table.getValueAt(fila, 1).toString());
            u.setRol(table.getValueAt(fila, 2).toString());
            u.setCorreo(table.getValueAt(fila, 3).toString());

            // Estado
            Object valorEstado = table.getValueAt(fila, 4);
            if (valorEstado instanceof Boolean) {
                u.setEstado((Boolean) valorEstado);
            } else if (valorEstado instanceof Number) {
                u.setEstado(((Number) valorEstado).intValue() == 1);
            } else {
                u.setEstado("Activo".equalsIgnoreCase(valorEstado.toString()));
            }

            // Contraseña como marcador
            u.setContrasena("**********");

            // IdRol oculto
            u.setIdRol((Integer) table.getValueAt(fila, 6));

            return u;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return panel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return panel;
    }
}
