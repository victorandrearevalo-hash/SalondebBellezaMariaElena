package clasesDAO;

import clases.DashboardCitasPorDia;
import clases.DashboardMetricas;
import conexionSql.Conexion;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.InputStream;
import javax.swing.JFileChooser;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Administrador
 */
public class dashboardDAO {

    public DashboardMetricas obtenerMetricas() {
        DashboardMetricas d = null;
        Connection cn = null;
        CallableStatement cs = null;
        ResultSet rs = null;

        try {
            cn = Conexion.getConexion();
            cs = cn.prepareCall("{CALL sp_dashboardMetricas}");
            rs = cs.executeQuery();

            if (rs.next()) {
                d = new DashboardMetricas();
                d.setTotalCitas(rs.getInt("Total_Citas"));
                d.setCitasHoy(rs.getInt("Citas_Hoy"));
                d.setCitasAtendidas(rs.getInt("Citas_Atendidas"));
                d.setCitasPendientes(rs.getInt("Citas_Pendientes"));
                d.setCitasCanceladas(rs.getInt("Citas_Canceladas"));
                d.setClientesActivos(rs.getInt("Clientes_Activos"));
                d.setTrabajadoresActivos(rs.getInt("Trabajadores_Activos"));
                d.setServicioMasSolicitado(rs.getString("Servicio_Mas_Solicitado"));
                d.setAreaMasDemandada(rs.getString("Area_Mas_Demandada"));
                d.setPromedioProductividad(rs.getDouble("Promedio_Citas_Por_Trabajador"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener métricas del dashboard: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (cs != null) {
                    cs.close();
                }
                if (cn != null) {
                    cn.close();
                }
            } catch (SQLException ex) {
                System.err.println("Error al cerrar recursos: " + ex.getMessage());
            }
        }

        return d;
    }

    public List<DashboardCitasPorDia> listarCitasPorDiaSemana(LocalDate fechaInicio, LocalDate fechaFin, Integer idServicio, Integer idTrabajador) {
        List<DashboardCitasPorDia> lista = new ArrayList<>();

        String sql = "{CALL sp_dashboardCitasPorDiaSemana(?, ?, ?, ?)}";

        try (Connection cn = Conexion.getConexion(); CallableStatement cs = cn.prepareCall(sql)) {

            cs.setDate(1, Date.valueOf(fechaInicio));
            cs.setDate(2, Date.valueOf(fechaFin));

            if (idServicio != null) {
                cs.setInt(3, idServicio);
            } else {
                cs.setNull(3, Types.INTEGER);
            }

            if (idTrabajador != null) {
                cs.setInt(4, idTrabajador);
            } else {
                cs.setNull(4, Types.INTEGER);
            }

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    DashboardCitasPorDia d = new DashboardCitasPorDia();
                    d.setDiaSemana(rs.getString("DiaSemana"));
                    d.setFecha(rs.getDate("Fecha").toLocalDate());
                    d.setTotalCitas(rs.getInt("TotalCitas"));
                    d.setCitasCanceladas(rs.getInt("CitasCanceladas"));
                    d.setCitasCompletadas(rs.getInt("CitasCompletadas"));
                    lista.add(d);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar citas por día de la semana: " + e.getMessage());
        }

        return lista;
    }

    // 🔹 Nuevo método para generar el reporte Jasper
    public void generarReporteDashboard(boolean exportarPDF) {
        Connection cn = null;

        try {
            // 🔇 Silenciar warnings de duplicidad de extensiones JasperReports
            java.util.logging.Logger.getLogger("net.sf.jasperreports")
                    .setLevel(java.util.logging.Level.SEVERE);

            // 🔹 Cargar el reporte desde el JAR (NO desde src/)
            InputStream reporteStream = getClass().getResourceAsStream("/reportes/Metricas.jasper");

            if (reporteStream == null) {
                throw new RuntimeException("❌ No se encontró el archivo Metricas.jasper dentro del JAR");
            }

            JasperReport reporte = (JasperReport) JRLoader.loadObject(reporteStream);

            cn = Conexion.getConexion();

            JasperPrint jp = JasperFillManager.fillReport(reporte, null, cn);

            if (exportarPDF) {

                // Elegir dónde guardar el PDF
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Guardar reporte en PDF");
                chooser.setSelectedFile(new File("Reporte_Metricas.pdf"));

                int result = chooser.showSaveDialog(null);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();

                    // 🔹 Exportar PDF usando iText (100% compatible con 6.19.1)
                    JasperExportManager.exportReportToPdfFile(jp, file.getAbsolutePath());

                    System.out.println("✅ Reporte exportado correctamente en: " + file.getAbsolutePath());
                }

            } else {

                // Mostrar en visor
                JasperViewer viewer = new JasperViewer(jp, false);
                viewer.setTitle("📊 Reporte de Métricas del Dashboard");
                viewer.setVisible(true);
            }

        } catch (JRException e) {
            System.err.println("❌ Error al generar el reporte: " + e.getMessage());
            e.printStackTrace();

        } finally {
            if (cn != null) {
                try {
                    cn.close();
                } catch (Exception ex) {
                    System.err.println("⚠️ Error al cerrar conexión: " + ex.getMessage());
                }
            }
        }
    }
}
