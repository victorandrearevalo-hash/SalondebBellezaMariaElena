package clasesDAO;

import clases.Cliente;
import conexionSql.Conexion;
import interfaces.ICRUD;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author Administrador
 */

    public int getUltimoTipo() {
        return ultimoTipo;
    }

    @Override
    public boolean insertar(Cliente c) {
        Connection cn = Conexion.getConexion();
        boolean exito = false;

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_registrarCliente(?, ?, ?, ?, ?, ?, ?)}");
            cs.setString(1, c.getNombre());
            cs.setString(2, c.getApellido());
            cs.setString(3, c.getCorreo());
            cs.setString(4, c.getTelefono());
            cs.setString(5, c.getDistrito()); // ✅ CHAR(6) → setString
            cs.setBoolean(6, c.isEstado()); // Estado_Cliente
            cs.registerOutParameter(7, Types.INTEGER); // Resultado (ID generado)

            cs.execute();
            int resultado = cs.getInt(7);

            switch (resultado) {
                case -1:
                    ultimoMensaje = "❌ Error al registrar el cliente.";
                    ultimoTipo = 0;
                    break;
                case -2:
                    ultimoMensaje = "⚠️ El teléfono debe tener exactamente 9 dígitos.";
                    ultimoTipo = 1;
                    break;
                case -3:
                    ultimoMensaje = "⚠️ El teléfono no debe comenzar con 0.";
                    ultimoTipo = 1;
                    break;
                case -4:
                    ultimoMensaje = "⚠️ Cliente ya registrado.";
                    ultimoTipo = 1;
                    break;
                default:
                    if (resultado > 0) {
                        c.setId(resultado);
                        ultimoMensaje = "✅ Cliente registrado correctamente.";
                        ultimoTipo = 2;
                        exito = true;
                    } else {
                        ultimoMensaje = "⚠️ No se pudo registrar el cliente.";
                        ultimoTipo = 1;
                    }
                    break;
            }
        } catch (SQLException e) {
            ultimoMensaje = "❌ Error al insertar cliente: " + e.getMessage();
            ultimoTipo = 0;
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
            }
        }
        return exito;
    }

    @Override
    public boolean eliminar(Cliente c) {
        String sql = "{call sp_eliminarCliente(?, ?)}";
        try (Connection con = Conexion.getConexion(); CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, c.getId());
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();

            int resultado = cs.getInt(2);
            switch (resultado) {
                case 1:
                    System.out.println("🗑️ Cliente eliminado correctamente");
                    break;
                case 0:
                    System.err.println("❌ Error al eliminar cliente");
                    break;
                case -1:
                    System.out.println("⚠️ No se puede eliminar: cliente activo o no existe");
                    break;
            }

            return resultado == 1;

        } catch (SQLException e) {
            System.err.println("❌ Error en eliminar cliente: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizar(Cliente c) {
        Connection cn = Conexion.getConexion();
        boolean exito = false;

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_actualizarCliente(?, ?, ?, ?, ?, ?, ?, ?)}");
            cs.setInt(1, c.getId());
            cs.setString(2, c.getNombre());
            cs.setString(3, c.getApellido());
            cs.setString(4, c.getCorreo());
            cs.setString(5, c.getTelefono());
            cs.setString(6, c.getDistrito()); // ✅ CHAR(6) → setString
            cs.setBoolean(7, c.isEstado()); // Estado_Cliente
            cs.registerOutParameter(8, Types.INTEGER);

            cs.execute();
            int resultado = cs.getInt(8);
            //exito = (resultado == 1);

            switch (resultado) {
                case -3:
                    ultimoMensaje = "⚠️ El teléfono no debe comenzar con 0.";
                    ultimoTipo = 1;
                    break;
                case -2:
                    ultimoMensaje = "⚠️ El teléfono debe tener exactamente 9 dígitos.";
                    ultimoTipo = 1;
                    break;
                case -1:
                    ultimoMensaje = "❌ Error inesperado al actualizar cliente.";
                    ultimoTipo = 0;
                    break;
                case 0:
                    ultimoMensaje = "⚠️ Cliente no encontrado para actualizar.";
                    ultimoTipo = 1;
                    break;
                case 1:
                    ultimoMensaje = "✅ Cliente actualizado correctamente.";
                    ultimoTipo = 2;
                    exito = true;
                    break;
                default:
                    ultimoMensaje = "❌ Resultado desconocido al actualizar.";
                    ultimoTipo = 0;
                    break;
            }
        } catch (SQLException e) {
            ultimoMensaje = "❌ Error al actualizar cliente: " + e.getMessage();
            ultimoTipo = 0;

        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
            }
        }
        return exito;
    }

    @Override
    public ArrayList<Cliente> buscar(String criterio) {
        ArrayList<Cliente> lista = new ArrayList<>();
        Connection cn = Conexion.getConexion();

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_buscarCliente(?)}");
            cs.setString(1, criterio);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente(0, "----Seleccione Cliente----");
                c.setId(rs.getInt("Id_Cliente"));
                c.setNombre(rs.getString("Nombre_Cliente"));
                c.setApellido(rs.getString("Apellido_Cliente"));
                c.setCorreo(rs.getString("Email_Cliente"));
                c.setTelefono(rs.getString("Telefono_Cliente"));
                c.setDistrito(rs.getString("Distrito"));
                c.setEstado(rs.getBoolean("Estado_Cliente"));
                lista.add(c);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar cliente: " + e.getMessage());
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                /* Ignorar */ }
        }

        return lista;
    }

    @Override
    public ArrayList<Cliente> listar() {
        ArrayList<Cliente> lista = new ArrayList<>();
        Connection cn = Conexion.getConexion();

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_listarClientes}");
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Cliente c = new Cliente(0, "----Seleccione Cliente----");
                c.setId(rs.getInt("Id_Cliente"));
                c.setNombre(rs.getString("Nombre_Cliente"));
                c.setApellido(rs.getString("Apellido_Cliente"));
                c.setCorreo(rs.getString("Email_Cliente"));
                c.setTelefono(rs.getString("Telefono_Cliente"));
                c.setDistrito(rs.getString("Distrito"));
                c.setEstado(rs.getBoolean("Estado_Cliente"));
                lista.add(c);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar clientes: " + e.getMessage());
        }

        return lista;
    }
}
