package util;

import java.io.InputStream;
import java.util.Map;
import conexionSql.Conexion;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperExportManager;

/**
 *
 * @author Administrador
 */
public class Reportes {

    public static void mostrar(String nombreReporte, Map<String, Object> parametros) {

        try {
            // Ruta interna dentro del JAR
            String path = "/reportes/" + nombreReporte;

            InputStream reporteStream = Reportes.class.getResourceAsStream(path);

            if (reporteStream == null) {
                System.err.println("❌ No se encontró el reporte: " + path);
                return;
            }

            JasperPrint jp = JasperFillManager.fillReport(reporteStream, parametros, Conexion.getConexion());

            JasperExportManager.exportReportToPdfFile(jp, "ReporteGenerado.pdf");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
