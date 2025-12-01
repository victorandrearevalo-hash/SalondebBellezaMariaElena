package aplicacion;

import vistas.frmLogin;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Administrador
 */
public class MainApp {

    public static void main(String[] args) {

        // Activar Nimbus si está disponible
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no está, usa el LAF por defecto
            System.err.println("Error al aplicar Nimbus: " + e.getMessage());
        }

        // Iniciar el login en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                frmLogin login = new frmLogin();
                login.setLocationRelativeTo(null);
                login.setVisible(true);
            } catch (Exception ex) {
                // Captura cualquier error al abrir la vista
                ex.printStackTrace();
            }
        });
    }
}
