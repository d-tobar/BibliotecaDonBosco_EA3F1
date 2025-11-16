package sv.edu.udb.biblioteca;

import conexion.Conexion;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    private static final BigDecimal TARIFA_DIARIA = new BigDecimal("0.25");

    public boolean usuarioTienePrestamoActivo(int usuarioId, int materialId) throws SQLException {
        Conexion c = new Conexion();
        Connection conn = c.obtenerConexion();

        String sql = "SELECT COUNT(*) FROM prestamos WHERE usuario_id = ? AND material_id = ? AND estado = 'Activo'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, usuarioId);
        ps.setInt(2, materialId);

        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public boolean registrarPrestamo(int userId, int materialId, LocalDate fechaPrestamo, LocalDate fechaLimite) throws SQLException {
        Conexion c = new Conexion();
        Connection conn = c.obtenerConexion();
        conn.setAutoCommit(false);

        try {
            String sqlPrestamo = "INSERT INTO prestamos (usuario_id, material_id, fecha_prestamo, fecha_limite, estado) VALUES (?, ?, ?, ?, 'Activo')";
            PreparedStatement ps = conn.prepareStatement(sqlPrestamo);
            ps.setInt(1, userId);
            ps.setInt(2, materialId);
            ps.setDate(3, java.sql.Date.valueOf(fechaPrestamo));
            ps.setDate(4, java.sql.Date.valueOf(fechaLimite));
            ps.executeUpdate();

            String sqlUpdate = "UPDATE materiales SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ?";
            PreparedStatement ps2 = conn.prepareStatement(sqlUpdate);
            ps2.setInt(1, materialId);
            ps2.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;

        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Prestamo> listarPrestamosActivos() throws SQLException {
        Conexion c = new Conexion();
        Connection conn = c.obtenerConexion();

        List<Prestamo> lista = new ArrayList<>();

        String sql = "SELECT p.id, u.usuario, m.titulo, p.fecha_prestamo, p.fecha_limite FROM prestamos p INNER JOIN usuarios u ON p.usuario_id = u.id INNER JOIN materiales m ON p.material_id = m.id WHERE p.estado = 'Activo'";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new Prestamo(
                rs.getInt("id"),
                rs.getString("usuario"),
                rs.getString("titulo"),
                rs.getDate("fecha_prestamo").toLocalDate(),
                rs.getDate("fecha_limite").toLocalDate()
            ));
        }

        return lista;
    }

    public boolean registrarDevolucion(int prestamoId, LocalDate fechaDevolucion) throws SQLException {
        Conexion c = new Conexion();
        Connection conn = c.obtenerConexion();
        conn.setAutoCommit(false);

        try {
            String sqlGet = "SELECT * FROM prestamos WHERE id = ?";
            PreparedStatement psGet = conn.prepareStatement(sqlGet);
            psGet.setInt(1, prestamoId);

            ResultSet rs = psGet.executeQuery();
            if (!rs.next()) {
                conn.rollback();
                throw new SQLException("Préstamo no encontrado.");
            }

            int materialId = rs.getInt("material_id");
            LocalDate fechaLimite = rs.getDate("fecha_limite").toLocalDate();

            long diasAtraso = ChronoUnit.DAYS.between(fechaLimite, fechaDevolucion);
            BigDecimal mora = diasAtraso > 0
                    ? TARIFA_DIARIA.multiply(BigDecimal.valueOf(diasAtraso))
                    : BigDecimal.ZERO;

            String sqlUp = "UPDATE prestamos SET fecha_devolucion = ?, mora = ?, estado = 'Devuelto' WHERE id = ?";
            PreparedStatement psUp = conn.prepareStatement(sqlUp);
            psUp.setDate(1, java.sql.Date.valueOf(fechaDevolucion));
            psUp.setBigDecimal(2, mora);
            psUp.setInt(3, prestamoId);
            psUp.executeUpdate();

            String sqlMaterial = "UPDATE materiales SET cantidad_disponible = cantidad_disponible + 1 WHERE id = ?";
            PreparedStatement psMat = conn.prepareStatement(sqlMaterial);
            psMat.setInt(1, materialId);
            psMat.executeUpdate();

            conn.commit();
            return true;

        } catch (SQLException e) {
            conn.rollback();
            throw e;

        } finally {
            conn.setAutoCommit(true);
        }
    }

    public List<Usuario> listarUsuarios() throws SQLException {
        Conexion c = new Conexion();
        Connection conn = c.obtenerConexion();

        List<Usuario> lista = new ArrayList<>();

        String sql = "SELECT * FROM usuarios";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new Usuario(
                rs.getInt("id"),
                rs.getString("usuario")
            ));
        }

        return lista;
    }

    public List<Material> listarMateriales() throws SQLException {
        Conexion c = new Conexion();
        Connection conn = c.obtenerConexion();

        List<Material> lista = new ArrayList<>();

        String sql = "SELECT * FROM materiales";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new Material(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getInt("cantidad_disponible")
            ));
        }

        return lista;
    }
}
