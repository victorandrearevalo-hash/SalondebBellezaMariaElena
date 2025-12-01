package clasesDAO;

import conexionSql.Conexion;
import java.io.InputStream;
import java.sql.*;
import java.util.Map;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Administrador
 */
public class reporteDAO {

    public void generarReporte(String nombreReporte, Map<String, Object> parametros) {

        Connection cn = null;

        try {
            // Silenciar warnings Jasper
            java.util.logging.Logger jrLogger = java.util.logging.Logger.getLogger("net.sf.jasperreports");
            jrLogger.setUseParentHandlers(false);
            jrLogger.setLevel(java.util.logging.Level.OFF);

            // Ruta del archivo jasper
            //String ruta = "src/reportes/" + nombreReporte + ".jasper";
            //JasperReport reporte = (JasperReport) JRLoader.loadObjectFromFile(ruta);
            // Cargar reporte desde dentro del .jar
            String ruta = "/reportes/" + nombreReporte + ".jasper";
            InputStream stream = getClass().getResourceAsStream(ruta);

            JasperReport reporte = (JasperReport) JRLoader.loadObject(stream);

            if (stream == null) {
                throw new RuntimeException("No se encontró el reporte: " + ruta);
            }

            cn = Conexion.getConexion();

            JasperPrint jp = JasperFillManager.fillReport(reporte, parametros, cn);

            // 🔥 SIEMPRE abrir el visor, sin exportar automáticamente
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Reporte: " + nombreReporte);
            viewer.setVisible(true);

        } catch (Exception e) {
            System.err.println("Error al generar reporte:\n" + e.getMessage());
            e.printStackTrace();

        } finally {
            try {
                if (cn != null) {
                    cn.close();
                }
            } catch (Exception ex) {
            }
        }
    }

}
