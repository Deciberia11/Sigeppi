package pe.deliciasdelvalle.sigeppi.util;

import org.mindrot.jbcrypt.BCrypt;

/** Encripta y verifica contraseñas con BCrypt (RNF-01: 0 % de contraseñas en texto plano). */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String encriptar(String texto) {
        return BCrypt.hashpw(texto, BCrypt.gensalt(10));
    }

    public static boolean verificar(String texto, String hash) {
        if (texto == null || hash == null) {
            return false;
        }
        return BCrypt.checkpw(texto, hash);
    }
}
