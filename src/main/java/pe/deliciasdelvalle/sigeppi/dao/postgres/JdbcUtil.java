package pe.deliciasdelvalle.sigeppi.dao.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.deliciasdelvalle.sigeppi.util.ConexionBD;

/** Ayuda para consultas de solo lectura que devuelven filas como mapas (las usan el panel y los reportes). */
final class JdbcUtil {

    private JdbcUtil() {
    }

    static List<Map<String, Object>> lista(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> filas = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                while (rs.next()) {
                    Map<String, Object> fila = new LinkedHashMap<>();
                    for (int c = 1; c <= md.getColumnCount(); c++) {
                        fila.put(md.getColumnLabel(c), rs.getObject(c));
                    }
                    filas.add(fila);
                }
            }
        }
        return filas;
    }

    static Map<String, Object> uno(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> filas = lista(sql, params);
        return filas.isEmpty() ? new LinkedHashMap<>() : filas.get(0);
    }

    static int ejecutar(String sql, Object... params) throws SQLException {
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeUpdate();
        }
    }
}
