package clasesDAO;

import clases.Administrador;
import clases.Recepcionista;
import clases.Usuario;
import conexionSql.Conexion;
import interfaces.IAuth;
import interfaces.ICRUD;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Administrador
 */
public class usuarioDAO implements ICRUD<Usuario>, IAuth {

    private String ultimoMensaje;
    private int ultimoTipo; // 0=ERROR, 1=ADVERTENCIA, 2=ÉXITO
    private Usuario usuarioActual; // <-- guardamos el usuario logueado

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public int getUltimoTipo() {
        return ultimoTipo;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    // 🧩 MÉTODOS DE LA INTERFAZ GENÉRICA IAuth
    @Override
    public Usuario validarLogin(String nombreUsuario, String password) {
        Connection con = Conexion.getConexion();
        Usuario user = null;

        if (con == null) {
            ultimoMensaje = "⚠️ No hay conexión con la base de datos.";
            ultimoTipo = 0;
            return null;
        }

        try {
            String sql = "{CALL sp_validarLogin(?, ?, ?)}";
            CallableStatement cs = con.prepareCall(sql);

            cs.setString(1, nombreUsuario);
            cs.setString(2, Conexion.encriptarSHA256(password));
            cs.registerOutParameter(3, Types.INTEGER);

            // Ejecutar SP
            boolean hasResult = cs.execute();

            int resultSetIndex = 0;

            // Consumir todos los ResultSet (IMPORTANTE para que OUT parameter funcione)
            while (true) {
                if (hasResult) {
                    ResultSet rs = cs.getResultSet();

                    if (resultSetIndex == 0) {
                        // ---------- RS #1: Datos del usuario ----------
                        if (rs.next()) {

                            String rol = rs.getString("Nombre_Rol");

                            // --- MARCADORES (Marker Classes) ---
                            if (rol.toLowerCase().contains("administrador")) {
                                // Crear Administrador y mapear tipo
                                Administrador admin = new Administrador();
                                admin.setTipo(Administrador.tipoDesdeString(rol)); // COMPLETO o LIMITADO
                                user = admin;
                            } else if (rol.equalsIgnoreCase("Recepcionista")) {
                                user = new Recepcionista();
                            } else {
                                user = new Usuario(); // Rol desconocido
                            }
                            // Setear datos comunes
                            user.setId(rs.getInt("Id_Usuario"));
                            user.setNombre(rs.getString("Nombre_Usuario"));
                            user.setCorreo(rs.getString("Correo_Usuario"));
                            user.setEstado(rs.getBoolean("Estado_Usuario"));
                            user.setRol(rol);
                            user.setIdRol(rs.getInt("Id_Rol"));
                        }
                    } // ---------- RS #2: Lista de permisos ----------
                    else if (resultSetIndex == 1 && user != null) {
                        while (rs.next()) {
                            user.getPermisos().add(rs.getString("Nombre_Permiso"));
                        }
                    }

                    rs.close();
                    resultSetIndex++;
                }

                if (!cs.getMoreResults()) {
                    break;
                }
                hasResult = true;
            }

            // Leer el OUT parameter DESPUÉS de consumir todos los ResultSet
            int resultado = cs.getInt(3);

            if (resultado == -1) {
                ultimoMensaje = "❌ Usuario no existe.";
                ultimoTipo = 1;
            } else if (resultado == 0) {
                ultimoMensaje = "❌ Contraseña incorrecta o usuario inactivo.";
                ultimoTipo = 1;
            } else if (resultado == 1) {
                if (user != null) {
                    ultimoMensaje = "✅ Bienvenido (Usuario):  " + user.getNombre();
                    ultimoTipo = 2;
                } else {
                    ultimoMensaje = "❌ Error: No se obtuvieron datos del usuario.";
                    ultimoTipo = 0;
                }
            } else {
                ultimoMensaje = "❌ Error desconocido.";
                ultimoTipo = 0;
            }

            cs.close();
        } catch (SQLException e) {
            ultimoMensaje = "❌ Error al validar login: " + e.getMessage();
            ultimoTipo = 0;
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }

        return user;
    }

    @Override
    public boolean insertar(Usuario u
    ) {
        String sql = "{CALL sp_registrarUsuario(?, ?,?, ?, ?, ?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            // Encriptar la contraseña antes de enviar al SP
            String passwordEncriptado = Conexion.encriptarSHA256(u.getContrasena());

            // Obtener Id_Rol según el nombre del rol
            int idRol;
            switch (u.getRol()) {
                case "Administrador Completo":
                    idRol = 1;
                    break;
                case "Administrador Limitado":
                    idRol = 2;
                    break;
                case "Recepcionista":
                    idRol = 3;
                    break;
                default: {
                    System.out.println("❌ Rol inválido.");
                    return false;
                }
            }
            // Setear parámetros del SP
            cs.setString(1, u.getNombre());
            cs.setString(2, u.getRol()); // nombre del rol
            cs.setInt(3, idRol);
            cs.setString(4, u.getCorreo());
            cs.setString(5, passwordEncriptado);
            cs.registerOutParameter(6, java.sql.Types.INTEGER);

            cs.execute();

            int resultado = cs.getInt(6);

            switch (resultado) {
                case 1:
                    ultimoMensaje = "✅ Usuario registrado correctamente.";
                    ultimoTipo = 2; // ÉXITO
                    return true;
                case -1:
                    ultimoMensaje = "⚠️ El nombre de usuario ya existe.";
                    ultimoTipo = 1; // ADVERTENCIA
                    return false;
                case -2:
                    ultimoMensaje = "❌ Rol inválido.";
                    ultimoTipo = 0; // ERROR
                    return false;
                case -3:
                    ultimoMensaje = "❌ Ya existe un Administrador Completo activo.";
                    ultimoTipo = 1; // ADVERTENCIA
                    return false;
                default:
                    ultimoMensaje = "❌ Error al registrar el usuario (resultado: " + resultado + ").";
                    ultimoTipo = 0; // ERROR
                    return false;
            }

        } catch (SQLException e) {
            ultimoMensaje = "❌ Error al ejecutar procedimiento almacenado: " + e.getMessage();
            ultimoTipo = 0;
            return false;
        }
    }

    @Override
    public boolean eliminar(Usuario u
    ) {
        String sql = "{CALL sp_eliminarUsuario(?, ?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, u.getId());
            cs.registerOutParameter(2, java.sql.Types.INTEGER);

            cs.execute();

            int resultado = cs.getInt(2);
            switch (resultado) {
                case 1:
                    System.out.println("🗑️ Usuario eliminado correctamente");
                    break;
                case 0:
                    System.err.println("❌ Error al eliminar usuario");
                    break;
                case -1:
                    System.out.println("⚠️ No se puede eliminar: usuario activo o no existe");
                    break;
            }
            return resultado == 1;

        } catch (SQLException e) {
            System.err.println("❌ Error en eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Usuario u
    ) {
        String sql = "{CALL sp_actualizarUsuario(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            // Si la contraseña está vacía o es el marcador, no se envía nueva contraseña
            String password = u.getContrasena();
            String passwordEncriptado = null;

            if (password != null && !password.isBlank() && !password.equals("**********")) {
                passwordEncriptado = Conexion.encriptarSHA256(password);
            }

            cs.setInt(1, u.getId());
            cs.setString(2, u.getNombre());
            cs.setInt(3, u.getIdRol());
            cs.setString(4, u.getRol()); // 🔹 nuevo parámetro Rol_Usuario
            cs.setString(5, u.getCorreo());
            if (passwordEncriptado != null) {
                cs.setString(6, passwordEncriptado);
            } else {
                cs.setNull(6, java.sql.Types.VARCHAR); // 🔹 envía NULL si no se cambia
            }
            cs.setBoolean(7, u.isEstado());
            cs.registerOutParameter(8, java.sql.Types.INTEGER);

            cs.execute();

            int resultado = cs.getInt(8);
            switch (resultado) {
                case 1: {
                    ultimoMensaje = "✏️ Usuario actualizado correctamente.";
                    ultimoTipo = 2;
                    return true;
                }
                case 0: {
                    ultimoMensaje = "❌ Error al actualizar usuario.";
                    ultimoTipo = 0;
                    return false;
                }
                case -1: {
                    ultimoMensaje = "⚠️ Usuario no encontrado.";
                    ultimoTipo = 1;
                    return false;
                }
                case -2: {
                    ultimoMensaje = "❌ Rol inválido.";
                    ultimoTipo = 0;
                    return false;
                }
                case -3: {
                    ultimoMensaje = "❌ Ya existe un Administrador Completo activo.";
                    ultimoTipo = 1;
                    return false;
                }
                default:
                    ultimoMensaje = "❌ Resultado desconocido: " + resultado;
                    ultimoTipo = 0;
                    return false;
            }
        } catch (SQLException e) {
            ultimoMensaje = "❌ Error en actualizar usuario: " + e.getMessage();
            ultimoTipo = 0;
            return false;
        }

    }

    @Override
    public ArrayList<Usuario> buscar(String criterio
    ) {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "{CALL sp_buscarUsuario(?)}";

        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setString(1, criterio);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("Id_Usuario"));
                u.setNombre(rs.getString("Nombre_Usuario"));
                u.setRol(rs.getString("Rol_Usuario"));
                u.setCorreo(rs.getString("Correo_Usuario"));
                u.setEstado(rs.getBoolean("Estado_Usuario"));
                lista.add(u);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error en buscar: " + e.getMessage());
        }

        return lista;
    }

    @Override
    public ArrayList<Usuario> listar() {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "{call sp_listarUsuarios()}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("Id_Usuario"));
                u.setNombre(rs.getString("Nombre_Usuario"));
                u.setRol(rs.getString("Rol_Usuario"));
                u.setCorreo(rs.getString("Correo_Usuario"));
                u.setEstado(rs.getBoolean("Estado_Usuario"));
                u.setContrasena(rs.getString("Password_Usuario"));
                u.setIdRol(rs.getInt("Id_Rol"));
                lista.add(u);
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println("❌ Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }
}
