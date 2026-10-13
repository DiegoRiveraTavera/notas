# Notas en la nube

Aplicación Android básica con Jetpack Compose que permite capturar notas en español y consultar las notas guardadas en una tabla de Supabase.

## Configuración

1. En Supabase, ejecuta el SQL de creación de `public.notas` y las políticas RLS indicadas para esta práctica.
2. En el archivo `local.properties` de la raíz del proyecto, conserva `sdk.dir` y agrega las propiedades del proyecto:

   ```properties
   supabase.url=https://TU-PROYECTO.supabase.co
   supabase.anonKey=TU_CLAVE_PUBLISHABLE
   ```

3. Sincroniza Gradle y ejecuta la app desde Android Studio.

La URL y la clave se inyectan en `BuildConfig`; `local.properties` está excluido del control de versiones. La clave publishable/anon se incluye en el cliente y no es un secreto: RLS debe proteger la tabla. No uses la `service_role` key en una aplicación móvil. Las políticas abiertas del SQL de práctica permiten acceso público; para una app real, configura Supabase Auth y limita las políticas por usuario.

## Arquitectura cliente-servidor

- **Cliente:** la app Android presenta el formulario, valida los datos y muestra las notas.
- **Capa de acceso a datos:** `SupabaseNotasRepository` realiza solicitudes HTTPS REST a la API de Supabase (`GET` para consultar y `POST` para guardar).
- **Servidor cloud:** Supabase recibe la solicitud, aplica autenticación por API key y políticas RLS, y opera sobre PostgreSQL.
- **Datos:** la tabla `public.notas` valida longitudes y genera el identificador y la fecha de creación.

La app maneja fallos de red y respuestas HTTP, limita el título a 3–80 caracteres y la descripción a 300, y no guarda la clave en los archivos versionados. HTTPS protege los datos en tránsito.

## Ventajas de la infraestructura cloud

Supabase hospeda y mantiene la base de datos y la API, por lo que no hace falta desplegar un servidor propio para esta práctica. La API permite acceder a los datos desde distintos dispositivos, mientras que PostgreSQL, las copias de seguridad y la capacidad de escalar se administran en la nube. RLS agrega control de acceso en el servidor independientemente de la interfaz móvil.
