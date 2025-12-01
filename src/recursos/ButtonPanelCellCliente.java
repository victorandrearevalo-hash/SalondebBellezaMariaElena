package recursos;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import controlador.clienteController;
import clases.Cliente;
import clases.SesionUsuario;
import clases.Usuario;
import vistas.frmNuevoCliente;

/**
 *
 * @author Administrador
 */
public class ButtonPanelCellCliente extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private final JPanel panel;
    private final JButton btnEditar;
    private final JButton btnEliminar;

    private JTable table;
    private final Usuario usuarioActual;
    private String rol;

    private final clienteController controller = new clienteController();

    public ButtonPanelCellCliente(JTable table) {
        this.table = table;

        // Usuario logueado
        this.usuarioActual = SesionUsuario.getUsuarioActual();
        this.rol = (usuarioActual != null) ? usuarioActual.getRol() : "";

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

        aplicarPermisos();      // Aplica permisos al crear
        configurarEventos();    // Configura listeners

    }

    /**
     * Aplica permisos según rol/permisos del usuario
     */
    private void aplicarPermisos() {
        if (usuarioActual == null) {
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
            return;
        }

        // Administrador Completo tiene acceso total
        if ("Administrador Completo".equalsIgnoreCase(rol)) {
            btnEditar.setEnabled(true);
            btnEliminar.setEnabled(true);
            return;
        }

        // Administrador Limitado no puede registrar, editar ni eliminar
        if ("Administrador Limitado".equalsIgnoreCase(rol)) {
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
            return;
        }

        // Otros roles (Recepcionista, etc.) usan permisos individuales
        var permisos = usuarioActual.getPermisos();
        btnEditar.setEnabled(permisos.contains("Editar_Cliente"));
        btnEliminar.setEnabled(permisos.contains("Eliminar_Cliente"));
    }

    private void configurarEventos() {
        // Acción Editar
        btnEditar.addActionListener(e -> {
            Usuario usr = SesionUsuario.getUsuarioActual();
            if (usr == null || !usr.getPermisos().contains("Editar_Cliente")) {
                JOptionPane.showMessageDialog(null,
                        "🚫 No tienes permiso para EDITAR clientes.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                fireEditingStopped();
                return;
            }

            int fila = table.getSelectedRow(); // ✅ Usamos getSelectedRow() (más seguro)
            if (fila < 0) {
                JOptionPane.showMessageDialog(null, "Seleccione una fila para editar.");
                return;
            }

            try {
                Cliente c = new Cliente(0, "----Seleccione Cliente----");
                c.setId((int) table.getValueAt(fila, 0));
                c.setNombre(table.getValueAt(fila, 1).toString());
                c.setApellido(table.getValueAt(fila, 2).toString());
                c.setCorreo(table.getValueAt(fila, 3) != null ? table.getValueAt(fila, 3).toString() : "");
                c.setTelefono(table.getValueAt(fila, 4) != null ? table.getValueAt(fila, 4).toString() : "");
                c.setDistrito(table.getValueAt(fila, 5) != null ? table.getValueAt(fila, 5).toString() : "");

                // ✅ Leer correctamente el estado (puede ser texto o booleano)
                Object estadoValor = table.getValueAt(fila, 6);
                boolean estadoActivo = false;

                if (estadoValor instanceof Boolean) {
                    estadoActivo = (Boolean) estadoValor;
                } else if (estadoValor instanceof String) {
                    estadoActivo = ((String) estadoValor).equalsIgnoreCase("Activo");
                }

                c.setEstado(estadoActivo);

                // ✅ Abrir formulario de edición
                frmNuevoCliente form = new frmNuevoCliente();
                form.cargarDatosParaEditar(c);

                // Si es un JInternalFrame dentro de escritorio
                JDesktopPane desktop = (JDesktopPane) SwingUtilities.getAncestorOfClass(JDesktopPane.class, table);
                if (desktop != null) {
                    desktop.add(form);
                    form.setVisible(true);
                    form.toFront();
                } else {
                    form.setVisible(true);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "❌ Error al obtener datos de la fila: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            fireEditingStopped();
        }
        );

        // Acción Eliminar
        btnEliminar.addActionListener(e -> {
            Usuario usr = SesionUsuario.getUsuarioActual();
            if (usr == null || !usr.getPermisos().contains("Eliminar_Cliente")) {
                JOptionPane.showMessageDialog(null,
                        "🚫 No tienes permiso para ELIMINAR clientes.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int fila = table.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
                return;
            }
            int id = (int) table.getValueAt(fila, 0); // Columna 0 = Id_Cliente
            String estado = (String) table.getValueAt(fila, 6); // Columna 6 = Estado

            // Solo permitir eliminar si está inactivo
            if (estado.equals("Activo")) {
                JOptionPane.showMessageDialog(
                        null,
                        "⚠️ No se puede eliminar un cliente activo. Primero desactívelo.",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "¿Eliminar cliente con ID " + id + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                clienteController controller = new clienteController();
                Cliente c = new Cliente(0, "----Seleccione Cliente----");
                c.setId(id);

                if (controller.eliminarCliente(c)) {
                    ((DefaultTableModel) table.getModel()).removeRow(fila);
                    JOptionPane.showMessageDialog(null,
                            "✅ Cliente eliminado correctamente.",
                            "Correcto",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "⚠️ Error al eliminar cliente.",
                            "Error",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        );
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
