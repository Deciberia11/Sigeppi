package pe.deliciasdelvalle.sigeppi.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Patrón Singleton: una sola instancia administra el pool de conexiones (HikariCP)
 * para soportar 50 a 100 usuarios concurrentes (RNF-05).
 * Los datos de conexión se leen del archivo db.properties (src/main/resources) y,
 * si existen, las variables de entorno SIGEPPI_DB_* tienen prioridad (RNF-18).
 */
public final class ConexionBD {

    private static ConexionBD instancia;
    private static final Properties CONFIG = cargarConfig();
    private final HikariDataSource dataSource;

    private ConexionBD() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl(env("SIGEPPI_DB_URL", "jdbc:postgresql://localhost:5432/sigeppi_db"));
        config.setUsername(env("SIGEPPI_DB_USER", "sigeppi_app"));
        config.setPassword(env("SIGEPPI_DB_PASS", ""));
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(5000); // 5 s como máximo esperando una conexión
        dataSource = new HikariDataSource(config);
    }

    public static synchronized ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() throws SQLException {
        return dataSource.getConnection();
    }

    private static String env(String clave, String porDefecto) {
        String valor = System.getenv(clave);
        if (valor == null || valor.isBlank()) {
            valor = CONFIG.getProperty(clave);
        }
        return (valor == null || valor.isBlank()) ? porDefecto : valor.trim();
    }

    private static Properties cargarConfig() {
        Properties p = new Properties();
        try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (Exception e) {
            // si no hay archivo se usan los valores por defecto
        }
        return p;
    }
}
