# Registro de cambios - SIGEPPI

## 2026-10-24
### Seguridad
- Se agregó `.gitignore`: `src/main/resources/db.properties` (credenciales de la BD), `target/` y archivos privados de NetBeans ya no se suben a GitHub.
- Se agregó `src/main/resources/db.properties.example` como plantilla sin contraseñas.

### Despliegue
- Proyecto migrado a **Tomcat 10.1** (Jakarta EE 10, Java 17).
- Se agregó `Dockerfile` (Maven + Tomcat 10.1) y `.dockerignore`. La conexión a la BD se pasa con las variables de entorno `SIGEPPI_DB_URL`, `SIGEPPI_DB_USER` y `SIGEPPI_DB_PASS`.

## Cómo configurar el proyecto después de clonarlo
1. Copiar `src/main/resources/db.properties.example` como `db.properties` (misma carpeta).
2. Completar URL, usuario y contraseña de PostgreSQL.
3. En NetBeans: Clean and Build → Run (servidor Tomcat 10.1).
