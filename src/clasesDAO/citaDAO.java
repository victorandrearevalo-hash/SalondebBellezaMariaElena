package clasesDAO;

import clases.Cita;
import conexionSql.Conexion;
import interfaces.ICRUD;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author v1ct0
 */
public class citaDAO implements ICRUD<Cita> {

    private String ultimoMensaje;
    private int ultimoTipo;  // 0=ERROR, 1=ADVERTENCIA, 2=EXITO

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public int getUltimoTipo() {
        return ultimoTipo;
    }

    public int validarSolapamiento(Date fecha, Time hora, int idTrabajador, int idServicio) {
        int resultado = 1;

        String sql = "{CALL sp_validarSolapamiento(?,?,?,?,?)}";

        try (Connection cn = Conexion.getConexion(); CallableStatement cs = cn.prepareCall(sql)) {

            cs.setDate(1, fecha);
            cs.setTime(2, hora);
            cs.setInt(3, idTrabajador);
            cs.setInt(4, idServicio);

            cs.registerOutParameter(5, Types.INTEGER);

            cs.execute();
            resultado = cs.getInt(5);

        } catch (Exception e) {
            resultado = 0;
        }

        return resultado;
    }

    @Override
    public boolean insertar(Cita c) {
        Connection cn = Conexion.getConexion();
        boolean exito = false;

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_registrarCita(?, ?, ?, ?, ?, ?, ?, ?)}");
            cs.setDate(1, c.getFechaCita());
            cs.setTime(2, c.getHoraCita());
            cs.setString(3, c.getObservacionCita());
            cs.setInt(4, c.getIdCliente());
            cs.setInt(5, c.getIdTrabajador());
            cs.setInt(6, c.getIdUsuario());
            cs.setInt(7, c.getIdServicio());

            cs.registerOutParameter(8, Types.INTEGER); // Id generado o resultado

            cs.execute();

            int resultado = cs.getInt(8);
            // Interpretar códigos del SP
            switch (resultado) {
                case -1:
                    ultimoMensaje = "❌ Error general en el procedimiento almacenado.";
                    ultimoTipo = 0;
                    break;

                case -2:
                    ultimoMensaje = "⚠️ La fecha de la cita es anterior al día de hoy.";
                    ultimoTipo = 1;
                    break;

                case -3:
                    ultimoMensaje = "⚠️ Ya existe una cita para este trabajador en la misma fecha y hora.";
                    ultimoTipo = 1;
                    break;

                case -4:
                    ultimoMensaje = "⚠️ No puedes registrar una hora pasada cuando la fecha es hoy.";
                    ultimoTipo = 1;
                    break;

                case -5:
                    ultimoMensaje = "⚠️ El cliente ya tiene una cita registrada a esa misma hora.";
                    ultimoTipo = 1;
                    break;

                case -6:
                    ultimoMensaje = "⚠️ El trabajador ya tiene una cita agendada que se superpone con el horario de esta solicitud, según la duración del servicio.";
                    ultimoTipo = 1;
                    break;

                case -7:
                    ultimoMensaje = "⚠️ El servicio seleccionado no existe o no tiene duración registrada.";
                    ultimoTipo = 1;
                    break;

                case -8:
                    ultimoMensaje = "⚠️ El trabajador no tiene asignado un turno.";
                    ultimoTipo = 1;
                    break;

                case -9:
                    ultimoMensaje = "⚠️ La hora seleccionada NO está dentro del turno laboral del trabajador.";
                    ultimoTipo = 1;
                    break;

                case -10:
                    ultimoMensaje = "⚠️ La duración del servicio excede el horario del turno del trabajador.";
                    ultimoTipo = 1;
                    break;

                default:
                    if (resultado > 0) {
                        c.setIdCita(resultado);
                        ultimoMensaje = "✔️ Cita registrada correctamente.";
                        ultimoTipo = 2;
                        exito = true;
                    } else {
                        ultimoMensaje = "⚠️ No se pudo registrar la cita.";
                        ultimoTipo = 1;
                    }
                    break;
            }
        } catch (SQLException e) {
            ultimoMensaje = "❌ Error al insertar cita: " + e.getMessage();
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
    public boolean eliminar(Cita c) {
        Connection cn = Conexion.getConexion();
        boolean exito = false;

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_eliminarCita(?, ?)}");
            cs.setInt(1, c.getIdCita());
            cs.registerOutParameter(2, Types.INTEGER);
            cs.execute();

            int resultado = cs.getInt(2);
            exito = (resultado == 1);

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar cita: " + e.getMessage());
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return exito;
    }

    public boolean eliminarFisico(Cita c, String rolUsuario) {
        Connection cn = Conexion.getConexion();
        boolean exito = false;

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_eliminarCitaFisica(?, ?)}");
            cs.setInt(1, c.getIdCita());
            cs.setString(2, rolUsuario); // Rol del usuario actual ("Administrador")
            cs.registerOutParameter(3, Types.INTEGER);
            cs.execute();

            int resultado = cs.getInt(3); // Obtener el OUT parameter
            if (resultado == 1) {
                exito = true;
            } else if (resultado == -1) {
                System.err.println("⛔ No tienes permisos para eliminar físicamente la cita.");
            } else if (resultado == 0) {
                System.err.println("⚠️ La cita no existe.");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al eliminar físicamente la cita: " + e.getMessage());
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return exito;
    }

    public Cita obtenerCitaPorId(int idCita) {
        Connection cn = Conexion.getConexion();
        Cita cita = null;

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_obtenerCitaPorId(?)}");
            cs.setInt(1, idCita);

            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                cita = new Cita();
                cita.setIdCita(rs.getInt("Id_Cita"));
                cita.setFechaCita(rs.getDate("Fecha_Cita"));
                cita.setHoraCita(rs.getTime("Hora_Cita"));
                cita.setEstadoCita(rs.getString("Estado_Cita"));
                cita.setObservacionCita(rs.getString("Observacion_Cita"));
                cita.setIdCliente(rs.getInt("Id_Cliente"));
                cita.setIdTrabajador(rs.getInt("Id_Trabajador"));
                cita.setIdUsuario(rs.getInt("Id_Usuario"));
                cita.setIdServicio(rs.getInt("Id_Servicio"));
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al obtener cita por ID: " + e.getMessage());
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return cita; // Si no existe, retorna null
    }

    @Override
    public boolean actualizar(Cita c) {
        Connection cn = Conexion.getConexion();
        boolean exito = false;

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_actualizarCita(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
            cs.setInt(1, c.getIdCita());
            cs.setDate(2, c.getFechaCita());
            cs.setTime(3, c.getHoraCita());
            cs.setString(4, c.getEstadoCita());
            cs.setString(5, c.getObservacionCita());
            cs.setInt(6, c.getIdCliente());
            cs.setInt(7, c.getIdTrabajador());
            cs.setInt(8, c.getIdUsuario());
            cs.setInt(9, c.getIdServicio());

            cs.registerOutParameter(10, Types.INTEGER);

            cs.execute();

            int resultado = cs.getInt(10);

            switch (resultado) {
                case 1:
                    exito = true;
                    ultimoMensaje = "✔️ Cita actualizada correctamente.";
                    ultimoTipo = 2;
                    break;

                case 0:
                    ultimoMensaje = "⚠️ La cita no existe.";
                    ultimoTipo = 1;
                    break;

                case -1:
                    ultimoMensaje = "❌ Error general en la actualización.";
                    ultimoTipo = 0;
                    break;

                case -2:
                    ultimoMensaje = "⚠️ La fecha no puede ser anterior al día actual.";
                    ultimoTipo = 1;
                    break;

                case -4:
                    ultimoMensaje = "⚠️ No puedes asignar una hora pasada cuando la fecha es hoy.";
                    ultimoTipo = 1;
                    break;

                case -6:
                    ultimoMensaje = "⚠️ El trabajador tiene otra cita que se solapa con esta según la duración del servicio.";
                    ultimoTipo = 1;
                    break;

                case -7:
                    ultimoMensaje = "❌ El servicio seleccionado no tiene duración válida.";
                    ultimoTipo = 1;
                    break;

                case -8:
                    ultimoMensaje = "⚠️ No puedes cambiar el trabajador porque tiene una cita pendiente en esa fecha y hora.";
                    ultimoTipo = 1;
                    break;

                default:
                    ultimoMensaje = "⚠️ Error desconocido. Código: " + resultado;
                    ultimoTipo = 1;
                    break;
            }

        } catch (SQLException e) {
            ultimoMensaje = "❌ Error al actualizar cita: " + e.getMessage();
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
    public ArrayList<Cita> buscar(String criterio) {
        ArrayList<Cita> lista = new ArrayList<>();
        Connection cn = Conexion.getConexion();

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_buscarCita(?)}");
            cs.setString(1, criterio);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Cita c = new Cita();
                c.setIdCita(rs.getInt("Id_Cita"));
                c.setFechaCita(rs.getDate("Fecha_Cita"));
                c.setHoraCita(rs.getTime("Hora_Cita"));
                c.setEstadoCita(rs.getString("Estado_Cita"));
                c.setObservacionCita(rs.getString("Observacion_Cita"));

                // ✅ Asignar nombres completos para mostrar en tabla
                c.setNombreCliente(rs.getString("Cliente"));
                c.setNombreTrabajador(rs.getString("Trabajador"));
                c.setNombreServicio(rs.getString("Servicio"));
                c.setNombreUsuario(rs.getString("Usuario"));
                c.setIdArea(rs.getInt("Id_Area"));
                c.setNombreArea(rs.getString("Area"));
                c.setIdServicio(rs.getInt("Id_Servicio"));
                lista.add(c);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al buscar cita: " + e.getMessage());
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Error al cerrar conexión: " + e.getMessage());
            }
        }
        return lista;
    }

    @Override
    public ArrayList<Cita> listar() {
        ArrayList<Cita> lista = new ArrayList<>();
        Connection cn = Conexion.getConexion();

        try {
            CallableStatement cs = cn.prepareCall("{CALL sp_listarCitas}");
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                Cita c = new Cita();
                c.setIdCita(rs.getInt("Id_Cita"));
                c.setFechaCita(rs.getDate("Fecha_Cita"));
                c.setHoraCita(rs.getTime("Hora_Cita"));
                c.setEstadoCita(rs.getString("Estado_Cita"));
                c.setObservacionCita(rs.getString("Observacion_Cita"));
                c.setNombreCliente(rs.getString("Cliente"));
                c.setNombreTrabajador(rs.getString("Trabajador"));
                c.setNombreServicio(rs.getString("Servicio"));
                c.setNombreUsuario(rs.getString("Usuario"));
                c.setIdArea(rs.getInt("Id_Area"));
                c.setNombreArea(rs.getString("Area"));
                c.setIdServicio(rs.getInt("Id_Servicio"));
                lista.add(c);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error al listar citas: " + e.getMessage());
        } finally {
            try {
                cn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ Error al cerrar conexión: " + e.getMessage());
            }
        }

        return lista;
    }

}
