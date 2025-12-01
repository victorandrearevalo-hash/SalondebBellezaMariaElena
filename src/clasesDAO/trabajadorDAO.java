package clasesDAO;

import clases.Trabajador;
import conexionSql.Conexion;
import interfaces.ICRUD;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Administrador
 */
public class trabajadorDAO implements ICRUD<Trabajador> {

    @Override
    public boolean insertar(Trabajador t) {
        Connection cn = Conexion.getConexion();
        boolean exito = false;

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_registrarTrabajador(?, ?, ?, ?, ?, ?, ?)}");
            cs.setString(1, t.getNombres());
            cs.setString(2, t.getApellidos());
            cs.setString(3, t.getEspecialidad());
            cs.setInt(4, Integer.parseInt(t.getHorario())); // Id_Turno
            cs.setInt(5, Integer.parseInt(t.getCodArea()));  // Id_Area
            cs.setBoolean(6, t.isEstado());                 // Estado
            cs.registerOutParameter(7, Types.INTEGER);       // Resultado (ID generado)

            cs.execute();
            int idGenerado = cs.getInt(7);
            if (idGenerado > 0) {
                t.setId(idGenerado); // guardar en el objeto
                exito = true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar trabajador: " + e.getMessage());
        }

        return exito;
    }

    @Override
    public boolean eliminar(Trabajador t) {
        String sql = "{call sp_eliminarTrabajador(?, ?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, t.getId());
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();

            int resultado = cs.getInt(2);
            switch (resultado) {
                case 1 :
                    System.out.println("🗑️ Trabajador eliminado correctamente");
                    break;
                case 0 :
                    System.err.println("❌ Error al eliminar trabajador");
                    break;
                case -1 :
                    System.out.println("⚠️ No se puede eliminar: trabajador activo o no existe");
                    break;
            }

            return resultado == 1;

        } catch (SQLException e) {
            System.err.println("❌ Error en eliminar trabajador: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Trabajador t) {
        Connection cn = Conexion.getConexion();
        boolean exito = false;

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_actualizarTrabajador(?, ?, ?, ?, ?, ?, ?, ?)}");
            cs.setInt(1, t.getId());
            cs.setString(2, t.getNombres());
            cs.setString(3, t.getApellidos());
            cs.setString(4, t.getEspecialidad());
            cs.setInt(5, Integer.parseInt(t.getHorario())); // convierte String a int
            cs.setInt(6, Integer.parseInt(t.getCodArea()));
            cs.setBoolean(7, t.isEstado());                 // Estado
            cs.registerOutParameter(8, Types.INTEGER);

            cs.execute();
            int resultado = cs.getInt(8);
            exito = (resultado == 1);
        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar trabajador: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Error de formato numérico: " + e.getMessage());
        }

        return exito;
    }

    @Override
    public ArrayList<Trabajador> buscar(String criterio) {
        ArrayList<Trabajador> lista = new ArrayList<>();
        Connection cn = Conexion.getConexion();

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_buscarTrabajador(?)}");
            cs.setString(1, criterio);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Trabajador t = new Trabajador(0, "----Seleccione Trabajador----");
                t.setId(rs.getInt("Id_Trabajador"));
                t.setNombres(rs.getString("Nombre_Trabajador"));
                t.setApellidos(rs.getString("Apellido_Trabajador"));
                t.setEspecialidad(rs.getString("Especialidad_Trabajador"));
                t.setHorario(rs.getString("Turno")); // ✅ importante: el procedimiento devuelve el nombre del turno
                t.setCodArea(rs.getString("Area"));   // ✅ lo mismo con el nombre del área
                t.setEstado(rs.getBoolean("Estado_Trabajador"));
                lista.add(t);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar trabajadores: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public ArrayList<Trabajador> listar() {
        ArrayList<Trabajador> lista = new ArrayList<>();
        Connection cn = Conexion.getConexion();

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_listarTrabajadores}");
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Trabajador t = new Trabajador(0, "----Seleccione Trabajador----");
                t.setId(rs.getInt("Id_Trabajador"));
                t.setNombres(rs.getString("Nombre_Trabajador"));
                t.setApellidos(rs.getString("Apellido_Trabajador"));
                t.setEspecialidad(rs.getString("Especialidad_Trabajador"));
                t.setHorario(rs.getString("Turno"));
                t.setCodArea(rs.getString("Area"));
                t.setEstado(rs.getBoolean("Estado_Trabajador"));
                lista.add(t);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar trabajadores: " + e.getMessage());
        }

        return lista;
    }
}
