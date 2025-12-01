package conexionSql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.security.MessageDigest;

/**
 *
 * @author Administrador
 *
 */
public class Conexion {

    private static final String URL = "jdbc:sqlserver://localhost:1433;instanceName=MSSQLSERVER;databaseName=BDSpaMariaElena;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASSWORD = "sql2011*";
    // 🔹 Variable para mantener una conexión activa
    private static Connection conexion = null;

    // Método para obtener la conexión
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Error: No se encontró el driver JDBC de SQL Server.");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos: " + e.getMessage());
        }
        return conexion;
    }

    // ✅ Método para cerrar sesión (cerrar conexión)
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                conexion = null; // Se libera la referencia
                System.out.println("🔒 Conexión cerrada correctamente (sesión terminada)");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Error al cerrar la conexión: " + e.getMessage());
        }
    }

    // Método para encriptar contraseñas con SHA-256
    public static String encriptarSHA256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            System.err.println("⚠️ Error al encriptar la contraseña: " + e.getMessage());
            return null;
        }
    }
}
