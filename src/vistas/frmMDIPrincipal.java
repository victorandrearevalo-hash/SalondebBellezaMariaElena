package vistas;

import clases.Administrador;
import clases.Recepcionista;
import clases.SesionUsuario;
import clases.Usuario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Administrador
 */
public class frmMDIPrincipal extends JFrame {

    /**
     * Creates new form frmMDIPrincipal
     */
    private Usuario user; // Usuario logueado

    public frmMDIPrincipal(Usuario user) {

        initComponents();
        this.user = user;
        // Maximizar la ventana al abrir
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        configurarAccesos();
        configurarBarraEstado(); // 🔹 Método externo para crear la barra inferior
    }

    private void configurarAccesos() {

        // 🔹 Ocultar todos los menús por defecto
        dashboardMenu.setVisible(false);
        clienteMenu.setVisible(false);
        citaMenu.setVisible(false);
        jMenuServicio.setVisible(false);
        mantenimientoMenu.setVisible(false);
        trabajadorMenu.setVisible(false);
        sistemaMenu.setVisible(false);
        reporteMenu.setVisible(false);

        // 🔹 ACCESOS POR ROL BASE
        if (user instanceof Administrador) {
            Administrador admin = (Administrador) user;

            // Menús básicos visibles para todos los administradores
            dashboardMenu.setVisible(true);
            clienteMenu.setVisible(true);
            sistemaMenu.setVisible(true);
            citaMenu.setVisible(true);
            trabajadorMenu.setVisible(true);
            reporteMenu.setVisible(true);

            // Menú solo para administradores completos
            if (admin.getTipo() == Administrador.Tipo.Completo) {
                jMenuServicio.setVisible(true);
                mantenimientoMenu.setVisible(true);
                System.out.println("Administrador Completo: todo visible");
            } else {
                jMenuServicio.setVisible(false);
                mantenimientoMenu.setVisible(false);
                System.out.println("Administrador Limitado: mantenimiento oculto");
            }

        } else if (user instanceof Recepcionista) {
            // Menús básicos visibles para recepcionista
            dashboardMenu.setVisible(true);
            clienteMenu.setVisible(true);
            citaMenu.setVisible(true);
            sistemaMenu.setVisible(true);
            System.out.println("Recepcionista: accesos base");
        }
    }

    private void configurarBarraEstado() {
        // 🟦 1. Configurar panel inferior (status bar)
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusPanel.setBackground(new Color(44, 62, 80)); // Azul grisáceo elegante

        // Limpiamos componentes previos
        statusPanel.removeAll();

        // 🟨 2. Crear etiqueta central (texto principal)
        JLabel lblEstado = new JLabel("Sistema de Reserva - Control de citas - Salón de belleza María Elena");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 13)); // 🔸 Negrita
        lblEstado.setHorizontalAlignment(SwingConstants.LEFT);
        lblEstado.setForeground(Color.WHITE); // 🔸 Texto blanco

        // 🟩 3. Panel derecho con info del usuario
        JPanel panelDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 3));
        panelDerecho.setOpaque(false);

        panelDerecho.add(lblCodigo);
        panelDerecho.add(lblUsuario);
        panelDerecho.add(lblRol);
        panelDerecho.add(lblMayus);
        panelDerecho.add(lblFecha);
        panelDerecho.add(lblHora);

        // 🟢 4. Mostrar datos iniciales
        lblCodigo.setForeground(Color.WHITE);
        lblUsuario.setForeground(Color.WHITE);
        lblRol.setForeground(Color.WHITE);
        lblMayus.setForeground(Color.WHITE);
        lblFecha.setForeground(Color.WHITE);
        lblHora.setForeground(Color.WHITE);

        lblCodigo.setText("CODIGO: " + user.getId());
        lblUsuario.setText("USUARIO: " + user.getNombre());
        lblRol.setText("ROL: " + user.getRol());

        // 🕒 5. Timer dinámico (fecha, hora, mayúsculas)
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter dateFmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
            java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");

            lblFecha.setText("FECHA: " + now.format(dateFmt));
            lblHora.setText("HORA: " + now.format(timeFmt));

            try {
                boolean caps = java.awt.Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
                lblMayus.setText("MAYUS: " + (caps ? "ON" : "OFF"));
            } catch (UnsupportedOperationException ex) {
                lblMayus.setText("MAYUS: N/A");
            }
        });
        timer.start();

        // 🧩 6. Agregar componentes
        statusPanel.add(lblEstado, BorderLayout.CENTER);
        statusPanel.add(panelDerecho, BorderLayout.EAST);

        // 🪟 7. Reajustar layout del JFrame
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(desktopPane, BorderLayout.CENTER);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // Método para abrir formularios hijos y traerlos al frente si ya están abiertos
    private void abrirFormulario(JInternalFrame form, Class<? extends JInternalFrame> claseForm) {
        boolean existe = false;
        for (JInternalFrame f : desktopPane.getAllFrames()) {
            if (f.getClass() == form.getClass()) {
                try {
                    f.setSelected(true);
                    f.toFront();
                    existe = true;
                } catch (PropertyVetoException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        if (!existe) {
            // ✅ Aseguramos que se pueda cerrar correctamente
            form.setClosable(true);
            form.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

            desktopPane.add(form);
            form.setVisible(true);
            // 🔹 Escucha el evento de apertura y cierre
            form.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
                @Override
                public void internalFrameOpened(javax.swing.event.InternalFrameEvent e) {
                    actualizarMenuVentanas();
                }

                @Override
                public void internalFrameClosed(javax.swing.event.InternalFrameEvent e) {
                    actualizarMenuVentanas();
                }
            });

            actualizarMenuVentanas();

        }
    }
    // 🔹 Actualiza el menú "Ventana" con las ventanas abiertas

    private void actualizarMenuVentanas() {
        ventanaMenu.removeAll(); // Limpia el menú de ventanas abiertas

        JInternalFrame[] ventanas = desktopPane.getAllFrames();

        if (ventanas.length == 0) {
            JMenuItem vacio = new JMenuItem("(No hay ventanas abiertas)");
            vacio.setEnabled(false);
            ventanaMenu.add(vacio);
        } else {
            for (JInternalFrame ventana : ventanas) {
                JMenuItem item = new JMenuItem(ventana.getTitle());
                item.addActionListener(e -> {
                    try {
                        ventana.setSelected(true);
                        ventana.toFront();
                    } catch (java.beans.PropertyVetoException ex) {
                        ex.printStackTrace();
                    }
                });
                ventanaMenu.add(item);
            }
        }

        ventanaMenu.revalidate();
        ventanaMenu.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktopPane = new javax.swing.JDesktopPane();
        statusPanel = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        lblUsuario = new javax.swing.JLabel();
        lblRol = new javax.swing.JLabel();
        lblMayus = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        lblHora = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        dashboardMenu = new javax.swing.JMenu();
        dashboardItem = new javax.swing.JMenuItem();
        clienteMenu = new javax.swing.JMenu();
        registroClienteItem = new javax.swing.JMenuItem();
        listarClienteItem = new javax.swing.JMenuItem();
        citaMenu = new javax.swing.JMenu();
        registarCitasItem = new javax.swing.JMenuItem();
        listarCitasItem = new javax.swing.JMenuItem();
        trabajadorMenu = new javax.swing.JMenu();
        registrarTrabajadorItem = new javax.swing.JMenuItem();
        listarTrabajadorItem = new javax.swing.JMenuItem();
        jMenuServicio = new javax.swing.JMenu();
        jRegistrarServicioItem = new javax.swing.JMenuItem();
        jListarServicioItem = new javax.swing.JMenuItem();
        reporteMenu = new javax.swing.JMenu();
        mostrarReporteItem = new javax.swing.JMenuItem();
        mantenimientoMenu = new javax.swing.JMenu();
        registrarUsuarioItem = new javax.swing.JMenuItem();
        listarUsuarioItem = new javax.swing.JMenuItem();
        sistemaMenu = new javax.swing.JMenu();
        cerrarSesionItem = new javax.swing.JMenuItem();
        salirSistemaItem = new javax.swing.JMenuItem();
        ventanaMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        statusPanel.setBackground(new java.awt.Color(102, 102, 102));
        statusPanel.setForeground(new java.awt.Color(153, 153, 153));

        lblCodigo.setText("jLabel7");

        lblUsuario.setText("jLabel8");

        lblRol.setText("jLabel7");

        lblMayus.setText("jLabel8");

        lblFecha.setText("jLabel9");

        lblHora.setText("jLabel10");

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(lblCodigo)
                .addGap(165, 165, 165)
                .addComponent(lblUsuario)
                .addGap(141, 141, 141)
                .addComponent(lblRol)
                .addGap(166, 166, 166)
                .addComponent(lblMayus)
                .addGap(166, 166, 166)
                .addComponent(lblFecha)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblHora)
                .addGap(54, 54, 54))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigo)
                    .addComponent(lblUsuario)
                    .addComponent(lblRol)
                    .addComponent(lblMayus)
                    .addComponent(lblFecha)
                    .addComponent(lblHora))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        dashboardMenu.setMnemonic('f');
        dashboardMenu.setText("Dashboard");

        dashboardItem.setMnemonic('o');
        dashboardItem.setText("Mostrar");
        dashboardItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dashboardItemActionPerformed(evt);
            }
        });
        dashboardMenu.add(dashboardItem);

        menuBar.add(dashboardMenu);

        clienteMenu.setMnemonic('e');
        clienteMenu.setText("Clientes");

        registroClienteItem.setMnemonic('t');
        registroClienteItem.setText("Registrar");
        registroClienteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registroClienteItemActionPerformed(evt);
            }
        });
        clienteMenu.add(registroClienteItem);

        listarClienteItem.setMnemonic('p');
        listarClienteItem.setText("Listar");
        listarClienteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listarClienteItemActionPerformed(evt);
            }
        });
        clienteMenu.add(listarClienteItem);

        menuBar.add(clienteMenu);

        citaMenu.setMnemonic('h');
        citaMenu.setText("Citas");

        registarCitasItem.setMnemonic('c');
        registarCitasItem.setText("Registrar");
        registarCitasItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registarCitasItemActionPerformed(evt);
            }
        });
        citaMenu.add(registarCitasItem);

        listarCitasItem.setText("Listar");
        listarCitasItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listarCitasItemActionPerformed(evt);
            }
        });
        citaMenu.add(listarCitasItem);

        menuBar.add(citaMenu);

        trabajadorMenu.setText("Trabajador");

        registrarTrabajadorItem.setText("Registrar");
        registrarTrabajadorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registrarTrabajadorItemActionPerformed(evt);
            }
        });
        trabajadorMenu.add(registrarTrabajadorItem);

        listarTrabajadorItem.setText("Listar");
        listarTrabajadorItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listarTrabajadorItemActionPerformed(evt);
            }
        });
        trabajadorMenu.add(listarTrabajadorItem);

        menuBar.add(trabajadorMenu);

        jMenuServicio.setText("Servicio");

        jRegistrarServicioItem.setText("Registrar");
        jRegistrarServicioItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRegistrarServicioItemActionPerformed(evt);
            }
        });
        jMenuServicio.add(jRegistrarServicioItem);

        jListarServicioItem.setText("Listar");
        jListarServicioItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jListarServicioItemActionPerformed(evt);
            }
        });
        jMenuServicio.add(jListarServicioItem);

        menuBar.add(jMenuServicio);

        reporteMenu.setText("Reportes");

        mostrarReporteItem.setText("Mostrar");
        mostrarReporteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrarReporteItemActionPerformed(evt);
            }
        });
        reporteMenu.add(mostrarReporteItem);

        menuBar.add(reporteMenu);

        mantenimientoMenu.setText("Mantenimiento");

        registrarUsuarioItem.setText("Registrar");
        registrarUsuarioItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registrarUsuarioItemActionPerformed(evt);
            }
        });
        mantenimientoMenu.add(registrarUsuarioItem);

        listarUsuarioItem.setText("Listar");
        listarUsuarioItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listarUsuarioItemActionPerformed(evt);
            }
        });
        mantenimientoMenu.add(listarUsuarioItem);

        menuBar.add(mantenimientoMenu);

        sistemaMenu.setText("Sistema");

        cerrarSesionItem.setText("Cerrar Sesion");
        cerrarSesionItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarSesionItemActionPerformed(evt);
            }
        });
        sistemaMenu.add(cerrarSesionItem);

        salirSistemaItem.setText("Salir");
        salirSistemaItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salirSistemaItemActionPerformed(evt);
            }
        });
        sistemaMenu.add(salirSistemaItem);

        menuBar.add(sistemaMenu);

        ventanaMenu.setText("Ventana");
        menuBar.add(ventanaMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1144, Short.MAX_VALUE)
            .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(desktopPane, javax.swing.GroupLayout.PREFERRED_SIZE, 663, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void listarClienteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listarClienteItemActionPerformed
        abrirFormulario(new frmCliente(), frmCliente.class
        );
    }//GEN-LAST:event_listarClienteItemActionPerformed

    private void registrarUsuarioItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registrarUsuarioItemActionPerformed
        abrirFormulario(new frmNuevoUsuario(), frmNuevoUsuario.class
        );
    }//GEN-LAST:event_registrarUsuarioItemActionPerformed

    private void cerrarSesionItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarSesionItemActionPerformed
        // Confirmar acción
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            // Ocultar o cerrar el frmPrincipal
            this.dispose(); // cierra la ventana principal

            // Mostrar el formulario de login
            frmLogin loginForm = new frmLogin();
            loginForm.setVisible(true);

        }

    }//GEN-LAST:event_cerrarSesionItemActionPerformed

    private void listarUsuarioItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listarUsuarioItemActionPerformed
        abrirFormulario(new frmUsuario(), frmUsuario.class
        );
    }//GEN-LAST:event_listarUsuarioItemActionPerformed

    private void dashboardItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dashboardItemActionPerformed
        abrirFormulario(new frmDashboard(), frmDashboard.class
        );
    }//GEN-LAST:event_dashboardItemActionPerformed

    private void registroClienteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registroClienteItemActionPerformed
        Usuario usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(null, "No hay usuario logueado.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar rol: Administrador Limitado no puede registrar clientes
        String rol = usuarioActual.getRol();
        if (rol.equalsIgnoreCase("Administrador Limitado")) {
            JOptionPane.showMessageDialog(null, "🚫 No tienes permiso para registrar clientes.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return; // ❌ Sale del método y no abre el formulario
        }
        abrirFormulario(new frmNuevoCliente(), frmNuevoCliente.class
        );
    }//GEN-LAST:event_registroClienteItemActionPerformed

    private void registarCitasItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registarCitasItemActionPerformed
        Usuario usuarioActual = SesionUsuario.getUsuarioActual();

        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(null, "No hay usuario logueado.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar rol
        String rol = usuarioActual.getRol();
        if (rol.equalsIgnoreCase("Administrador Limitado")) {
            JOptionPane.showMessageDialog(null, "🚫 No tienes permiso para registrar citas.", "Acceso denegado", JOptionPane.WARNING_MESSAGE);
            return; // ❌ Sale del método y no abre el formulario
        }

        // Abrir formulario solo si tiene permiso
        abrirFormulario(new frmNuevaCita(), frmNuevaCita.class
        );
    }//GEN-LAST:event_registarCitasItemActionPerformed

    private void listarCitasItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listarCitasItemActionPerformed
        abrirFormulario(new frmCita(), frmCita.class
        );
    }//GEN-LAST:event_listarCitasItemActionPerformed

    private void mostrarReporteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostrarReporteItemActionPerformed
        abrirFormulario(new frmReportes(), frmReportes.class
        );
    }//GEN-LAST:event_mostrarReporteItemActionPerformed

    private void registrarTrabajadorItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registrarTrabajadorItemActionPerformed
        abrirFormulario(new frmNuevoTrabajador(), frmNuevoTrabajador.class
        );
    }//GEN-LAST:event_registrarTrabajadorItemActionPerformed

    private void listarTrabajadorItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listarTrabajadorItemActionPerformed
        abrirFormulario(new frmTrabajadores(), frmTrabajadores.class
        );
    }//GEN-LAST:event_listarTrabajadorItemActionPerformed

    private void salirSistemaItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salirSistemaItemActionPerformed
        // TODO add your handling code here:
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea salir del sistema?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            // Cierra todo el programa
            System.exit(0);
        }

    }//GEN-LAST:event_salirSistemaItemActionPerformed

    private void jListarServicioItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jListarServicioItemActionPerformed
        abrirFormulario(new frmServicio(), frmServicio.class
        );
    }//GEN-LAST:event_jListarServicioItemActionPerformed

    private void jRegistrarServicioItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRegistrarServicioItemActionPerformed
        abrirFormulario(new frmNuevoServicio(), frmNuevoServicio.class
        ); 
    }//GEN-LAST:event_jRegistrarServicioItemActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            Usuario u = new Usuario(); // crear un usuario válido
            u.setRol("Administrador"); // o "Empleado" según prueba
            new frmMDIPrincipal(u).setVisible(true);
        });
    }

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem cerrarSesionItem;
    private javax.swing.JMenu citaMenu;
    private javax.swing.JMenu clienteMenu;
    private javax.swing.JMenuItem dashboardItem;
    private javax.swing.JMenu dashboardMenu;
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenuItem jListarServicioItem;
    private javax.swing.JMenu jMenuServicio;
    private javax.swing.JMenuItem jRegistrarServicioItem;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblHora;
    private javax.swing.JLabel lblMayus;
    private javax.swing.JLabel lblRol;
    private javax.swing.JLabel lblUsuario;
    private javax.swing.JMenuItem listarCitasItem;
    private javax.swing.JMenuItem listarClienteItem;
    private javax.swing.JMenuItem listarTrabajadorItem;
    private javax.swing.JMenuItem listarUsuarioItem;
    private javax.swing.JMenu mantenimientoMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mostrarReporteItem;
    private javax.swing.JMenuItem registarCitasItem;
    private javax.swing.JMenuItem registrarTrabajadorItem;
    private javax.swing.JMenuItem registrarUsuarioItem;
    private javax.swing.JMenuItem registroClienteItem;
    private javax.swing.JMenu reporteMenu;
    private javax.swing.JMenuItem salirSistemaItem;
    private javax.swing.JMenu sistemaMenu;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JMenu trabajadorMenu;
    private javax.swing.JMenu ventanaMenu;
    // End of variables declaration//GEN-END:variables

}
