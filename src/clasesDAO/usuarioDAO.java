package clasesDAO;

import clases.Usuario;
import conexionSql.Conexion;
import interfaces.interfaz;
import java.sql.*;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Administrador
 */
public class usuarioDAO implements interfaz<Usuario> {

    // ✅ MÉTODO DE LOGIN USANDO PROCEDIMIENTO ALMACENADO
    public Usuario validarLogin(String nombreUsuario, String password) {
        Usuario user = null;
        Connection con = Conexion.getConexion();

        if (con == null) {
            System.out.println("⚠️ No hay conexión con la base de datos.");
            return null;
        }

        try {
            String sql = "{CALL sp_validarLogin(?, ?)}";

            CallableStatement cs = con.prepareCall(sql);
            String passwordEncriptado = Conexion.encriptarSHA256(password);
            cs.setString(1, nombreUsuario);
            cs.setString(2, passwordEncriptado);

            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                user = new Usuario();
                user.setIdUsuario(rs.getInt("Id_Usuario"));
                user.setNombreUsuario(rs.getString("Nombre_Usuario"));
                user.setRolUsuario(rs.getString("Rol_Usuario"));
                user.setCorreoUsuario(rs.getString("Correo_Usuario"));
                user.setEstadoUsuario(rs.getBoolean("Estado_Usuario"));
                System.out.println("✅ Usuario autenticado correctamente");

            } else {
                System.out.println("❌ Usuario o contraseña incorrectos");
            }
            rs.close();
            cs.close();

        } catch (SQLException e) {
            System.err.println("❌ Error al validar el login: " + e.getMessage());
        }
        return user;
    }

    // 🧩 MÉTODOS DE LA INTERFAZ GENÉRICA
    @Override
    public boolean insertar(Usuario u) {
        String sql = "{CALL sp_registrarUsuario(?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            // Encriptar la contraseña antes de enviar al SP
            String passwordEncriptado = Conexion.encriptarSHA256(u.getPasswordUsuario());

            cs.setString(1, u.getNombreUsuario());
            cs.setString(2, u.getRolUsuario());
            cs.setString(3, u.getCorreoUsuario());
            cs.setString(4, passwordEncriptado);
            cs.registerOutParameter(5, java.sql.Types.INTEGER);

            cs.execute();

            int resultado = cs.getInt(5);

            switch (resultado) {
                case 1:
                    System.out.println("✅ Usuario registrado correctamente.");
                    return true;
                case -1:
                    System.out.println("⚠️ El nombre de usuario ya existe. No se puede registrar.");
                    return false;
                default:
                    System.out.println("❌ Error al registrar el usuario (resultado: " + resultado + ").");
                    return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al ejecutar procedimiento almacenado: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(Usuario u) {
        String sql = "{call sp_eliminarUsuario(?, ?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, u.getIdUsuario());
            cs.registerOutParameter(2, java.sql.Types.INTEGER);

            cs.execute();

            int resultado = cs.getInt(2);
            switch (resultado) {
                case 1:
                    System.out.println("🗑️ Usuario eliminado correctamente");
                case 0:
                    System.err.println("❌ Error al eliminar usuario");
                case -1:
                    System.out.println("⚠️ No se puede eliminar: usuario activo o no existe");
            }
            return resultado == 1;

        } catch (SQLException e) {
            System.err.println("❌ Error en eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Usuario u) {
        String sql = "{call sp_actualizarUsuario(?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, u.getIdUsuario());
            cs.setString(2, u.getNombreUsuario());
            cs.setString(3, u.getRolUsuario());
            cs.setString(4, u.getCorreoUsuario());
            cs.setString(5, u.getPasswordUsuario()); // si guardas contraseña aquí
            cs.registerOutParameter(6, java.sql.Types.INTEGER);

            cs.execute();

            int resultado = cs.getInt(6);
            switch (resultado) {
                case 1:
                    System.out.println("✏️ Usuario actualizado correctamente");
                case 0:
                    System.err.println("❌ Error al actualizar usuario");
                case -1:
                    System.out.println("⚠️ Usuario no encontrado");
            }
            return resultado == 1;

        } catch (SQLException e) {
            System.err.println("❌ Error en actualizar usuario: " + e.getMessage());
            return false;
        }

    }

    @Override
    public Usuario buscar(int id) {
        Usuario u = null;
        String sql = "{call sp_buscarUsuarioPorId(?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                u = new Usuario();
                u.setIdUsuario(rs.getInt("Id_Usuario"));
                u.setNombreUsuario(rs.getString("Nombre_Usuario"));
                u.setRolUsuario(rs.getString("Rol_Usuario"));
                u.setCorreoUsuario(rs.getString("Correo_Usuario"));
                u.setEstadoUsuario(rs.getBoolean("Estado_Usuario"));
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar usuario por ID: " + e.getMessage());
        }
        return u;
    }

    @Override
    public ArrayList<Usuario> listar() {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "{call sp_listarUsuarios()}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("Id_Usuario"));
                u.setNombreUsuario(rs.getString("Nombre_Usuario"));
                u.setRolUsuario(rs.getString("Rol_Usuario"));
                u.setCorreoUsuario(rs.getString("Correo_Usuario"));
                u.setEstadoUsuario(rs.getBoolean("Estado_Usuario"));
                lista.add(u);
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("❌ Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }
}
