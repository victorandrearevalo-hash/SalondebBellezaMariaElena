package clasesDAO;

import clases.Area;
import clases.Servicio;
import conexionSql.Conexion;
import interfaces.ICRUD;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Administrador
 */
public class servicioDAO implements ICRUD<Servicio> {

    private String ultimoMensaje;
    private int ultimoTipo;  // 0=ERROR, 1=ADVERTENCIA, 2=EXITO

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public int getUltimoTipo() {
        return ultimoTipo;
    }

    public ArrayList<Servicio> listarPorArea(int idArea) {
        ArrayList<Servicio> lista = new ArrayList<>();
        String sql = "{CALL sp_listar_servicioArea(?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idArea);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Servicio s = new Servicio(0, "----Seleccione un servicio----");
                s.setIdServicio(rs.getInt("Id_Servicio"));
                s.setNombre(rs.getString("Nombre_Servicio"));
                s.setPrecio(rs.getDouble("Precio_Servicio"));
                s.setDuracion(rs.getInt("Duracion_Minutos"));
                lista.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar servicios por área: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean insertar(Servicio s) {
        Connection cn = Conexion.getConexion();
        boolean exito = false;

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_registrarServicio(?, ?, ?, ?, ?)}");
            cs.setInt(1, s.getArea().getIdArea()); // IdArea
            cs.setString(2, s.getNombre());          // Nombre del servicio
            cs.setDouble(3, s.getPrecio());          // Precio
            cs.setInt(4, s.getDuracion());           // Duración
            cs.registerOutParameter(5, Types.INTEGER); // @Resultado

            cs.execute();

            int resultado = cs.getInt(5);

            switch (resultado) {
                case -1:
                    ultimoMensaje = "⚠️ El área ya existe.";
                    ultimoTipo = 1;
                    break;
                case -2:
                    ultimoMensaje = "⚠️ El servicio ya existe en esta área.";
                    ultimoTipo = 1;
                    break;
                case -99:
                    ultimoMensaje = "❌ Error general en el procedimiento almacenado.";
                    ultimoTipo = 0;
                    break;
                case 1:
                    ultimoMensaje = "✔️ Área y servicio registrados correctamente.";
                    ultimoTipo = 2;
                    exito = true;
                    break;
                default:
                    ultimoMensaje = "⚠️ Resultado desconocido del SP: " + resultado;
                    ultimoTipo = 1;
                    break;
            }

        } catch (SQLException e) {
            ultimoMensaje = "❌ Error al insertar servicio: " + e.getMessage();
            ultimoTipo = 0;
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return exito;
    }

    @Override
    public boolean eliminar(Servicio s) {
        String sql = "{call sp_eliminarServicio(?, ?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, s.getIdServicio());
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();
            return cs.getInt(2) == 1;
        } catch (SQLException e) {
            System.err.println("Error al eliminar servicio: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Servicio s) {
        String sql = "{CALL sp_actualizarServicio(?, ?, ?, ?, ?, ?)}"; // SP actualizado con IdArea
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, s.getIdServicio());             // Id del servicio
            cs.setInt(2, s.getArea().getIdArea());       // Id del área asociada
            cs.setString(3, s.getNombre());              // Nombre del servicio
            cs.setDouble(4, s.getPrecio());              // Precio
            cs.setInt(5, s.getDuracion());               // Duración
            cs.registerOutParameter(6, Types.INTEGER);   // @Resultado

            cs.execute();

            int resultado = cs.getInt(6);

            switch (resultado) {
                case -2:
                    ultimoMensaje = "⚠️ El servicio ya existe en esta área.";
                    ultimoTipo = 1;
                    return false;
                case -99:
                    ultimoMensaje = "❌ Error general en el procedimiento almacenado.";
                    ultimoTipo = 0;
                    return false;
                case 1:
                    ultimoMensaje = "✔️ Servicio actualizado correctamente.";
                    ultimoTipo = 2;
                    return true;
                default:
                    ultimoMensaje = "⚠️ Resultado desconocido del SP: " + resultado;
                    ultimoTipo = 1;
                    return false;
            }

        } catch (SQLException e) {
            ultimoMensaje = "❌ Error al actualizar servicio: " + e.getMessage();
            ultimoTipo = 0;
            return false;
        }
    }

    @Override
    public ArrayList<Servicio> buscar(String criterio) {
        ArrayList<Servicio> lista = new ArrayList<>();
        String sql = "{CALL sp_buscarAreaServicio(?)}";

        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, criterio); // Enviar criterio al SP
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                // Crear objeto Área
                Area a = new Area();
                a.setIdArea(rs.getInt("Id_Area"));
                a.setNombreArea(rs.getString("Nombre_Area"));

                // Crear objeto Servicio
                Servicio s = new Servicio();
                s.setIdServicio(rs.getInt("Id_Servicio"));
                s.setNombre(rs.getString("Nombre_Servicio"));
                s.setPrecio(rs.getDouble("Precio_Servicio"));
                s.setDuracion(rs.getInt("Duracion_Minutos"));
                s.setArea(a); // Asociar área al servicio

                lista.add(s);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar áreas y servicios: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public ArrayList<Servicio> listar() {
        ArrayList<Servicio> lista = new ArrayList<>();
        String sql = "{CALL sp_listarServicios()}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Servicio s = new Servicio(0, "----Seleccione un servicio----");
                s.setIdServicio(rs.getInt("Id_Servicio"));
                s.setNombre(rs.getString("Nombre"));
                s.setPrecio(rs.getDouble("Precio"));
                s.setDuracion(rs.getInt("Duracion"));
                lista.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar servicios: " + e.getMessage());
        }
        return lista;
    }
}
