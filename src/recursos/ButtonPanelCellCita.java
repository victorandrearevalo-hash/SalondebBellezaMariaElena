package recursos;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import controlador.citaController;
import clases.Cita;
import clases.SesionUsuario;
import clases.Usuario;
import vistas.frmNuevaCita;

/**
 *
 * @author Administrador
 */
public class ButtonPanelCellCita extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private final JPanel panel;
    private final JButton btnEditar;
    private final JButton btnEliminar;

    private final JTable table;
    private final Usuario usuarioActual;
    private String rol;

    private final citaController controller = new citaController();

    public ButtonPanelCellCita(JTable table) {
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

        // 🔐 Aplicar permisos directamente
        aplicarPermisos();      // ⬅️ APLICAMOS LOS PERMISOS AQUÍ
        configurarEventos();    // ⬅️ Listener separados para mejor orden
    }

    /**
     * 🔐 Aplica permisos al momento de construir el panel
     */
    private void aplicarPermisos() {

        if (usuarioActual == null) {
            btnEditar.setEnabled(false);
            btnEliminar.setEnabled(false);
            return;
        }

        var permisos = usuarioActual.getPermisos();

        // 🔐 Editar Cita
        if (!permisos.contains("Editar_Cita")) {
            btnEditar.setEnabled(false);
        }

        // 🔐 Cancelar/Eliminar Cita
        if (!permisos.contains("Cancelar_Cita")) {
            btnEliminar.setEnabled(false);
        }
    }

    private void configurarEventos() {

        // Acción Editar
        btnEditar.addActionListener(e -> {
            // 🔐 VALIDAR PERMISO: Editar Cita
            Usuario usr = SesionUsuario.getUsuarioActual();
            if (usr == null || !usr.getPermisos().contains("Editar_Cita")) {
                JOptionPane.showMessageDialog(null,
                        "🚫 No tienes permiso para Editar Citas.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                fireEditingStopped();
                return;
            }

            int fila = table.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(null, "Seleccione una fila para editar.");
                return;
            }

            try {
                Cita cita = new Cita();

                // 🟦 ID de cita
                cita.setIdCita((int) table.getValueAt(fila, 0));

                // 🟩 Fecha
                Object fechaObj = table.getValueAt(fila, 1);
                if (fechaObj instanceof java.sql.Date) {
                    cita.setFechaCita((java.sql.Date) fechaObj);
                } else {
                    cita.setFechaCita(java.sql.Date.valueOf(fechaObj.toString()));
                }

                // 🕒 Hora (convertir String "HH:mm" a java.sql.Time)
                Object horaObj = table.getValueAt(fila, 2);
                if (horaObj != null) {
                    if (horaObj instanceof java.sql.Time) {
                        cita.setHoraCita((java.sql.Time) horaObj);
                    } else {
                        // Si guardaste como String formateado HH:mm
                        cita.setHoraCita(java.sql.Time.valueOf(horaObj.toString() + ":00"));
                    }
                }

                // 👤 Cliente
                cita.setNombreCliente(table.getValueAt(fila, 3) != null
                        ? table.getValueAt(fila, 3).toString()
                        : "");

                // 👨‍🔧 Trabajador
                cita.setNombreTrabajador(table.getValueAt(fila, 4) != null
                        ? table.getValueAt(fila, 4).toString()
                        : "");
                // 💾 IdArea (oculto)
                Object idAreaObj = table.getValueAt(fila, 5);
                if (idAreaObj != null) {
                    cita.setIdArea(Integer.parseInt(idAreaObj.toString()));
                }

                // 🏠 Área (oculta)
                Object areaObj = table.getValueAt(fila, 6);
                cita.setNombreArea(areaObj != null ? areaObj.toString() : "");

                // 💾 IdServicio (oculto)
                Object idServicioObj = table.getValueAt(fila, 7);
                if (idServicioObj != null) {
                    cita.setIdServicio(Integer.parseInt(idServicioObj.toString()));
                }

                // 💇‍♀️ Servicio
                Object servicioObj = table.getValueAt(fila, 8);
                cita.setNombreServicio(servicioObj != null ? servicioObj.toString() : "");

                // 📋 Estado
                Object estadoObj = table.getValueAt(fila, 9);
                cita.setEstadoCita(estadoObj != null ? estadoObj.toString() : "");

                // 👤 Usuario (oculto)
                Object usuarioObj = table.getValueAt(fila, 10);
                cita.setNombreUsuario(usuarioObj != null ? usuarioObj.toString() : "");

                // 🗒 Observación (String)
                Object obsObj = table.getValueAt(fila, 11);
                cita.setObservacionCita(obsObj != null ? obsObj.toString() : "");

                // Abrir el formulario de edición
                frmNuevaCita form = new frmNuevaCita();
                form.cargarDatosParaEditar(cita);

                // Si está dentro de un JDesktopPane
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
                        "❌ Error al obtener datos de la cita: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            fireEditingStopped();
        });

        // Acción Eliminar
        btnEliminar.addActionListener(e -> {
            // 🔐 VALIDAR PERMISO Cancela/Eliminar
            Usuario usr = SesionUsuario.getUsuarioActual();
            if (usr == null || !usr.getPermisos().contains("Cancelar_Cita")) {
                JOptionPane.showMessageDialog(null,
                        "🚫 No tienes permiso para ELIMINAR/Cancelar citas.",
                        "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int fila = table.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
                return;
            }

            int idCita = (int) table.getValueAt(fila, 0);

            Cita cita = new Cita();
            cita.setIdCita(idCita);

            citaController controller = new citaController();

            // Si el usuario ES Administrador Completo puede escoger
            if (usr.getRol().equalsIgnoreCase("Administrador Completo")) {
                // Preguntar tipo de eliminación
                String[] opciones = {"Eliminación lógica", "Eliminación física", "Cancelar"};
                int opcion = JOptionPane.showOptionDialog(
                        null,
                        "Seleccione el tipo de eliminación para la cita con ID " + idCita,
                        "Eliminar Cita",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );

                if (opcion == 0) { // Lógica
                    if (controller.eliminarCita(cita)) {
                        ((DefaultTableModel) table.getModel()).removeRow(fila);
                        JOptionPane.showMessageDialog(null, "✅ Cita cancelada (eliminación lógica).");
                    }
                } else if (opcion == 1) { // Física
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "⚠️ Esta acción eliminará la cita permanentemente.\n¿Desea continuar?",
                            "Confirmar eliminación física", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                    if (confirm == JOptionPane.YES_OPTION) {
                        if (controller.eliminarFisico(cita, rol)) { // 👈 se pasa el rol al método
                            ((DefaultTableModel) table.getModel()).removeRow(fila);
                            JOptionPane.showMessageDialog(null, "✅ Cita eliminada definitivamente.");
                        } else {
                            JOptionPane.showMessageDialog(null, "❌ No se pudo eliminar físicamente la cita.");
                        }
                    }
                }

            } else {
                // Demás roles (Administrador limitado y Recepcionista)
                int confirm = JOptionPane.showConfirmDialog(null,
                        "¿Cancelar la cita con ID " + idCita + "?",
                        "Confirmar cancelación", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    if (controller.eliminarCita(cita)) {
                        ((DefaultTableModel) table.getModel()).removeRow(fila);
                        JOptionPane.showMessageDialog(null, "✅ Cita cancelada correctamente (eliminación lógica).");
                    } else {
                        JOptionPane.showMessageDialog(null, "⚠️ No se pudo cancelar la cita.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    }
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
        panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return panel;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return panel;
    }

}
