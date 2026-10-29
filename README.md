# SA-GIT: Sistema de Gestión de Incidentes TI

Proyecto completo desarrollado con **Java 17**, **Spring Boot 3.2.5** y **SQL Server**, implementado siguiendo rigurosamente los principios de **Arquitectura Hexagonal (Puertos y Adaptadores)**.

---

## 🏛️ Estructura del Proyecto (Arquitectura Hexagonal)

```text
com.sagit.incidentes
├── domain                                   # NÚCLEO DE NEGOCIO (Agnóstico a frameworks)
│   ├── model
│   │   ├── Usuario.java
│   │   ├── Incidente.java
│   │   ├── Rol.java                         # Enum: ADMIN, ANALISTA
│   │   ├── EstadoIncidente.java             # Enum: ABIERTO, EN_PROCESO, RESUELTO
│   │   └── AuthResult.java
│   ├── port
│   │   ├── in                               # Puertos de Entrada (Casos de Uso)
│   │   │   ├── AuthUseCase.java
│   │   │   ├── CrearIncidenteUseCase.java
│   │   │   └── ConsultarIncidentesUseCase.java
│   │   └── out                              # Puertos de Salida (Persistencia / Servicios)
│   │       ├── UsuarioRepositoryPort.java
│   │       └── IncidenteRepositoryPort.java
│   └── exception
│       ├── IncidenteNotFoundException.java
│       ├── UsuarioNotFoundException.java
│       ├── InvalidCredentialsException.java
│       └── BusinessRuleException.java
│
├── application                              # CAPA DE APLICACIÓN
│   └── service
│       ├── AuthService.java                 # Implementa AuthUseCase
│       └── IncidenteService.java            # Implementa CrearIncidenteUseCase, ConsultarIncidentesUseCase
│
└── infrastructure                           # CAPA DE INFRAESTRUCTURA
    ├── adapter
    │   ├── in
    │   │   └── rest                         # Adaptador Primario (Controladores y DTOs)
    │   │       ├── AuthController.java
    │   │       ├── IncidenteController.java
    │   │       ├── GlobalExceptionHandler.java
    │   │       └── dto
    │   │           ├── LoginRequest.java
    │   │           ├── JwtAuthResponse.java
    │   │           ├── CrearIncidenteRequest.java
    │   │           ├── IncidenteResponse.java
    │   │           └── ErrorResponse.java
    │   └── out
    │       └── persistence                  # Adaptador Secundario (Spring Data JPA)
    │           ├── entity
    │           │   ├── UsuarioEntity.java
    │           │   └── IncidenteEntity.java
    │           ├── repository
    │           │   ├── SpringDataUsuarioRepository.java
    │           │   └── SpringDataIncidenteRepository.java
    │           ├── mapper
    │           │   ├── UsuarioMapper.java
    │           │   └── IncidenteMapper.java
    │           ├── UsuarioPersistenceAdapter.java
    │           └── IncidentePersistenceAdapter.java
    └── config
        └── security                         # Seguridad Spring Security 6 & JWT
            ├── JwtTokenProvider.java
            ├── JwtAuthenticationFilter.java
            └── SecurityConfig.java
```

---

## 🚀 Requisitos Previos

- **Java Development Kit (JDK):** Versión 17 o superior.
- **Apache Maven:** Versión 3.8+.
- **Microsoft SQL Server:** Instancia local o remota en el puerto `1433`.

---

## 🗄️ Base de Datos SQL Server

1. Ejecuta el script `sagit_db_setup.sql` ubicado en la raíz del proyecto (o los archivos en `src/main/resources/db/`):
   - Crea la base de datos `sagit_db`.
   - Crea las tablas `usuarios` e `incidentes`.
   - Inserta usuarios iniciales con contraseña encriptada en BCrypt:
     * **Usuario:** `admin` | **Contraseña:** `admin123` (Rol: `ADMIN`)
     * **Usuario:** `analista` | **Contraseña:** `admin123` (Rol: `ANALISTA`)

---

## ⚙️ Configuración (`application.properties`)

El archivo `src/main/resources/application.properties` viene listo para producción local:

```properties
server.port=8080

spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=sagit_db;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=TuPasswordSeguro123!
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.show-sql=true

jwt.secret=TuClaveSecretaSuperSeguraParaFirmarTokensJWT1234567890!
jwt.expiration=86400000
```

---

## 🖥️ Interfaz Gráfica de Usuario (Web UI)

El proyecto incluye una **interfaz gráfica moderna y responsiva** basada en el wireframe, servida de manera integrada por Spring Boot en:

👉 **URL de acceso local:** `http://localhost:8080/`

### Características de la Interfaz:
1. **Pantalla de Inicio de Sesión (Login):**
   - Interfaz con validación, iconos, spinner de carga y botones de acceso rápido con las credenciales demo (`admin` / `admin123` y `analista` / `admin123`).
   - Autenticación asíncrona contra `/api/v1/auth/login` y almacenamiento seguro del token JWT en `localStorage`.
2. **Dashboard de Incidentes TI (Según Wireframe):**
   - **Tarjetas de métricas en tiempo real:** Total de incidentes, abiertos, en proceso y resueltos.
   - **Formulario Wireframe "Registro de Incidentes TI":**
     - Selector de herramientas de monitoreo (Prometheus, Grafana, Zabbix, Dynatrace, Datadog, AWS CloudWatch, Manual).
     - Input de Servicio Afectado con lista de sugerencias.
     - Selector de Prioridad (ALTA, MEDIA, BAJA) con chips interactivos.
     - Área de texto para la descripción detallada de la falla.
     - Asignación automática en backend del usuario autenticado vía JWT y fecha de creación con estado `ABIERTO`.
   - **Tabla Interactiva de Consulta:**
     - Ordenada automáticamente por fecha descendente.
     - Badges cromáticos distintivos para estado y prioridad.
     - Barra de búsqueda en vivo (por servicio, falla, fuente o usuario) y filtro por estado.
     - Botón "Ver Detalle" que abre una **ficha técnica modal** consultando `/api/v1/incidentes/{id}`.
     - Cierre de sesión y badge con avatar del usuario activo.

---

## 📡 Guía de Consumo de la API REST

### 1. Iniciar Sesión (Autenticación Pública)

**Endpoint:** `POST /api/v1/auth/login`

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbCI6IkFETUlOIiwiZXhwIjoxNzI5OTM3Mzg5fQ...",
  "type": "Bearer",
  "username": "admin",
  "rol": "ADMIN"
}
```

---

### 2. Registrar Incidente TI (Protegido con JWT)

> **Nota:** Se debe enviar el token en la cabecera `Authorization: Bearer <TOKEN>`. El campo `usuarioRegistro` es extraído automáticamente de las credenciales del token JWT.

**Endpoint:** `POST /api/v1/incidentes`

**Headers:**
```text
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

**Request Body:**
```json
{
  "fuenteMonitoreo": "Prometheus",
  "servicioAfectado": "Servidor de Base de Datos",
  "descripcionFalla": "Caída de servicio de caja por sobrecarga en pool de conexiones",
  "prioridad": "ALTA"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "fuenteMonitoreo": "Prometheus",
  "servicioAfectado": "Servidor de Base de Datos",
  "descripcionFalla": "Caída de servicio de caja por sobrecarga en pool de conexiones",
  "estado": "ABIERTO",
  "prioridad": "ALTA",
  "usuarioRegistro": "admin",
  "fechaCreacion": "2026-10-24T17:15:30.123"
}
```

---

### 3. Listar Todos los Incidentes (Protegido con JWT)

Devuelve los incidentes ordenados por `fechaCreacion` descendente.

**Endpoint:** `GET /api/v1/incidentes`

**Headers:**
```text
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "fuenteMonitoreo": "Prometheus",
    "servicioAfectado": "Servidor de Base de Datos",
    "descripcionFalla": "Caída de servicio de caja por sobrecarga en pool de conexiones",
    "estado": "ABIERTO",
    "prioridad": "ALTA",
    "usuarioRegistro": "admin",
    "fechaCreacion": "2026-10-24T17:15:30.123"
  }
]
```

---

### 4. Consultar Incidente por ID (Protegido con JWT)

**Endpoint:** `GET /api/v1/incidentes/1`

**Headers:**
```text
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response (200 OK):**
```json
{
  "id": 1,
  "fuenteMonitoreo": "Prometheus",
  "servicioAfectado": "Servidor de Base de Datos",
  "descripcionFalla": "Caída de servicio de caja por sobrecarga en pool de conexiones",
  "estado": "ABIERTO",
  "prioridad": "ALTA",
  "usuarioRegistro": "admin",
  "fechaCreacion": "2026-10-24T17:15:30.123"
}
```

**Response si no existe (404 Not Found):**
```json
{
  "timestamp": "2026-10-24T17:16:00.000",
  "status": 404,
  "error": "Not Found",
  "message": "Incidente no encontrado con ID: 99",
  "path": "/api/v1/incidentes/99"
}
```
#   T R A B A J O - D E - S A F T W A R E  
 