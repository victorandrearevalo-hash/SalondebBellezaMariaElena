package recursos;

import clases.Area;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import controlador.servicioController;
import clases.Servicio;
import clases.SesionUsuario;
import clases.Usuario;
import vistas.frmNuevoServicio;

/**
 *
 * @author Administrador
 */
public class ButtonPanelCellAreaServicio extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private final JPanel panel;
    private final JButton btnEditar;
    private final JButton btnEliminar;

    private final JTable table;
    private final Usuario usuarioActual;
    private final String rol;

    private final servicioController controller = new servicioController();

    public ButtonPanelCellAreaServicio(JTable table) {
        this.table = table;

        // Usuario logueado
        usuarioActual = SesionUsuario.getUsuarioActual();
        rol = (usuarioActual != null) ? usuarioActual.getRol() : "";

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

    private void aplicarPermisos() {
        if (usuarioActual == null) {
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
            return;
        }

        if ("Administrador Completo".equalsIgnoreCase(rol)) {
            return; // todo habilitado
        }

        if ("Administrador Limitado".equalsIgnoreCase(rol)) {
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
            return;
        }

        var permisos = usuarioActual.getPermisos();
        btnEditar.setEnabled(permisos.contains("Editar_Servicio"));
        btnEliminar.setEnabled(permisos.contains("Eliminar_Servicio"));
    }

    private void configurarEventos() {

        // Acción Editar
        btnEditar.addActionListener(e -> {

            int fila = table.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(null, "Seleccione un servicio para editar.");
                return;
            }

            try {
                Servicio s = new Servicio();
                s.setIdServicio((int) table.getValueAt(fila, 2));   // ID Servicio
                s.setNombre(table.getValueAt(fila, 3).toString());
                s.setPrecio((double) table.getValueAt(fila, 4));
                s.setDuracion((int) table.getValueAt(fila, 5));

                // Crear y asignar objeto Area
                Area a = new Area();
                a.setIdArea((int) table.getValueAt(fila, 0));       // ID Área
                a.setNombreArea(table.getValueAt(fila, 1).toString()); // Nombre Área
                s.setArea(a);

                // Abrir formulario
                frmNuevoServicio form = new frmNuevoServicio();
                form.cargarDatosParaEditar(s);

                JDesktopPane desktop = (JDesktopPane) SwingUtilities.getAncestorOfClass(JDesktopPane.class, table);
                if (desktop != null) {
                    desktop.add(form);
                    form.setVisible(true);
                    form.toFront();
                } else {
                    form.setVisible(true);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "❌ Error al obtener datos del servicio: " + ex.getMessage());
            }

            fireEditingStopped();
        });

        // Acción Eliminar
        btnEliminar.addActionListener(e -> {
            // ↪ Validar usuario logueado y permisos
            Usuario usr = SesionUsuario.getUsuarioActual();
            if (usr == null || !usr.getPermisos().contains("Eliminar_Servicio")) {
                JOptionPane.showMessageDialog(
                        null,
                        "🚫 No tienes permiso para ELIMINAR servicios.",
                        "Acceso denegado",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // ↪ Verificar que se haya seleccionado una fila   
            int fila = table.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(null, "Seleccione un servicio para eliminar.");
                return;
            }

            int idServicio = (int) table.getValueAt(fila, 2);

            int confirm = JOptionPane.showConfirmDialog(null,
                    "¿Eliminar servicio con ID " + idServicio + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                Servicio s = new Servicio();
                s.setIdServicio(idServicio);

                if (controller.eliminar(s)) {
                    ((DefaultTableModel) table.getModel()).removeRow(fila);
                    JOptionPane.showMessageDialog(null, "✅ Servicio eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(null, "⚠️ Error al eliminar servicio.");
                }
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return panel;
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        return panel;
    }

}
