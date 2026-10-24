package pe.deliciasdelvalle.sigeppi.servicio;

import pe.deliciasdelvalle.sigeppi.modelo.Rol;
import java.util.List;
import java.sql.SQLException;
import pe.deliciasdelvalle.sigeppi.dao.AuditoriaDAO;
import pe.deliciasdelvalle.sigeppi.dao.DAOFactory;
import pe.deliciasdelvalle.sigeppi.dao.UsuarioDAO;
import pe.deliciasdelvalle.sigeppi.modelo.NegocioException;
import pe.deliciasdelvalle.sigeppi.modelo.Usuario;
import pe.deliciasdelvalle.sigeppi.util.PasswordUtil;

/** CUS01 - Iniciar Sesión. */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;
    private final AuditoriaDAO auditoriaDAO;

    public UsuarioService() {
        DAOFactory fabrica = DAOFactory.getDAOFactory(DAOFactory.POSTGRES);
        this.usuarioDAO = fabrica.getUsuarioDAO();
        this.auditoriaDAO = fabrica.getAuditoriaDAO();
    }

    public Usuario autenticar(String nombreUsuario, String contrasena, String ip) throws NegocioException, SQLException {
        // REQ-2: campos obligatorios (también se valida en el servlet)
        if (nombreUsuario == null || nombreUsuario.isBlank() || contrasena == null || contrasena.isBlank()) {
            throw new NegocioException("Complete los campos obligatorios: usuario y contraseña.");
        }
        Usuario usuario = usuarioDAO.buscarPorUsuario(nombreUsuario.trim());

        // REQ-3: mismo mensaje si no existe o si la clave no coincide (no damos pistas)
        if (usuario == null || !usuario.isEstado() || !PasswordUtil.verificar(contrasena, usuario.getContrasena())) {
            auditoriaDAO.registrar(usuario == null ? null : usuario.getIdUsuario(), ip,
                    "SEGURIDAD", "LOGIN_FALLIDO", "usuario=" + nombreUsuario);
            throw new NegocioException("Usuario o contraseña incorrectos.");
        }
        usuarioDAO.actualizarUltimoAcceso(usuario.getIdUsuario());
        auditoriaDAO.registrar(usuario.getIdUsuario(), ip, "SEGURIDAD", "LOGIN_OK", null);
        usuario.setContrasena(null); // el hash no viaja a la sesión
        return usuario;
    }

    // ------------------------------------------------------------------ cuenta propia
    /** Caso de uso "Cambiar contraseña" (<<extend>> de Login en el diagrama de la ES). */
    public void cambiarContrasena(Usuario usuario, String actual, String nueva, String confirmar, String ip)
            throws NegocioException, SQLException {
        Usuario bd = usuarioDAO.buscarPorId(usuario.getIdUsuario());
        if (bd == null || !PasswordUtil.verificar(actual, bd.getContrasena())) {
            throw new NegocioException("La contraseña actual no es correcta.");
        }
        validarClave(nueva, confirmar);
        usuarioDAO.actualizarContrasena(usuario.getIdUsuario(), PasswordUtil.encriptar(nueva));
        auditoriaDAO.registrar(usuario.getIdUsuario(), ip, "SEGURIDAD", "CAMBIO_CONTRASENA", null);
    }

    private void validarClave(String nueva, String confirmar) throws NegocioException {
        if (nueva == null || nueva.length() < 8 || !nueva.matches(".*[A-Za-z].*") || !nueva.matches(".*\\d.*")) {
            throw new NegocioException("La nueva contraseña debe tener al menos 8 caracteres, con letras y números.");
        }
        if (confirmar != null && !nueva.equals(confirmar)) {
            throw new NegocioException("La confirmación no coincide con la nueva contraseña.");
        }
    }

    // ------------------------------------------------------------------ administración de usuarios (RNF-10, RNF-16)
    public List<Usuario> listar() throws SQLException {
        return usuarioDAO.listar();
    }

    public Usuario buscar(int idUsuario) throws SQLException {
        Usuario u = usuarioDAO.buscarPorId(idUsuario);
        if (u != null) {
            u.setContrasena(null);
        }
        return u;
    }

    public void guardar(Usuario u, String claveInicial, Usuario admin, String ip) throws NegocioException, SQLException {
        if (u.getNombreUsuario() == null || !u.getNombreUsuario().matches("[A-Za-z0-9._]{4,30}")) {
            throw new NegocioException("El usuario debe tener entre 4 y 30 caracteres (letras, números, punto o guion bajo).");
        }
        if (u.getNombres() == null || u.getNombres().isBlank() || u.getApellidos() == null || u.getApellidos().isBlank()) {
            throw new NegocioException("Ingrese los nombres y apellidos del trabajador.");
        }
        if (usuarioDAO.existeNombreUsuario(u.getNombreUsuario(), u.getIdUsuario())) {
            throw new NegocioException("Ya existe otro usuario con ese nombre de usuario.");
        }
        if (Rol.VENDEDOR.equals(u.getRol().getNombre()) && (u.getTienda() == null || u.getTienda().getIdTienda() == 0)) {
            throw new NegocioException("Un vendedor debe tener una tienda asignada.");
        }
        if (!Rol.VENDEDOR.equals(u.getRol().getNombre())) {
            u.setTienda(null);
        }
        if (u.getIdUsuario() == 0) {
            validarClave(claveInicial, null);
            u.setContrasena(PasswordUtil.encriptar(claveInicial));
            u.setEstado(true);
            int id = usuarioDAO.insertar(u);
            auditoriaDAO.registrar(admin.getIdUsuario(), ip, "SEGURIDAD", "USUARIO_CREADO", "usu_id=" + id);
        } else {
            usuarioDAO.actualizar(u);
            auditoriaDAO.registrar(admin.getIdUsuario(), ip, "SEGURIDAD", "USUARIO_EDITADO", "usu_id=" + u.getIdUsuario());
        }
    }

    public void cambiarEstado(int idUsuario, boolean activo, Usuario admin, String ip) throws NegocioException, SQLException {
        if (idUsuario == admin.getIdUsuario() && !activo) {
            throw new NegocioException("No puede desactivar su propio usuario.");
        }
        usuarioDAO.cambiarEstado(idUsuario, activo);
        auditoriaDAO.registrar(admin.getIdUsuario(), ip, "SEGURIDAD", activo ? "USUARIO_ACTIVADO" : "USUARIO_DESACTIVADO",
                "usu_id=" + idUsuario);
    }

    public void resetearContrasena(int idUsuario, String nueva, Usuario admin, String ip) throws NegocioException, SQLException {
        validarClave(nueva, null);
        usuarioDAO.actualizarContrasena(idUsuario, PasswordUtil.encriptar(nueva));
        auditoriaDAO.registrar(admin.getIdUsuario(), ip, "SEGURIDAD", "CONTRASENA_RESETEADA", "usu_id=" + idUsuario);
    }
}
