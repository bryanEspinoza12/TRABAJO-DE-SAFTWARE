-- =========================================================================================
-- SISTEMA SA-GIT: SISTEMA DE GESTIÓN DE INCIDENTES TI
-- SCRIPT DE INICIALIZACIÓN COMPLETA PARA MICROSOFT SQL SERVER
-- =========================================================================================

-- 1. CREACIÓN DE LA BASE DE DATOS
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'sagit_db')
BEGIN
    PRINT 'Creando la base de datos sagit_db...';
    CREATE DATABASE sagit_db;
END
ELSE
BEGIN
    PRINT 'La base de datos sagit_db ya existe.';
END
GO

USE sagit_db;
GO

-- 2. CREACIÓN DE LA TABLA 'usuarios'
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usuarios]') AND type in (N'U'))
BEGIN
    PRINT 'Creando tabla usuarios...';
    CREATE TABLE [dbo].[usuarios] (
        [id] BIGINT IDENTITY(1,1) NOT NULL,
        [username] NVARCHAR(50) NOT NULL,
        [password] NVARCHAR(100) NOT NULL,
        [nombre_completo] NVARCHAR(150) NOT NULL,
        [rol] NVARCHAR(20) NOT NULL, -- ADMIN, ANALISTA
        [activo] BIT NOT NULL CONSTRAINT [DF_usuarios_activo] DEFAULT (1),
        CONSTRAINT [PK_usuarios] PRIMARY KEY CLUSTERED ([id] ASC),
        CONSTRAINT [UQ_usuarios_username] UNIQUE NONCLUSTERED ([username] ASC)
    );
END
GO

-- 3. CREACIÓN DE LA TABLA 'incidentes' (SEGÚN WIREFRAME REGISTRO DE INCIDENTES TI)
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[incidentes]') AND type in (N'U'))
BEGIN
    PRINT 'Creando tabla incidentes...';
    CREATE TABLE [dbo].[incidentes] (
        [id] BIGINT IDENTITY(1,1) NOT NULL,
        [fuente_monitoreo] NVARCHAR(100) NOT NULL,
        [servicio_afectado] NVARCHAR(150) NOT NULL,
        [descripcion_falla] NVARCHAR(1000) NOT NULL,
        [estado] NVARCHAR(20) NOT NULL CONSTRAINT [DF_incidentes_estado] DEFAULT ('ABIERTO'), -- ABIERTO, EN_PROCESO, RESUELTO
        [prioridad] NVARCHAR(20) NULL, -- ALTA, MEDIA, BAJA
        [usuario_registro] NVARCHAR(50) NOT NULL,
        [fecha_creacion] DATETIME2 NOT NULL CONSTRAINT [DF_incidentes_fecha_creacion] DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT [PK_incidentes] PRIMARY KEY CLUSTERED ([id] ASC)
    );

    CREATE NONCLUSTERED INDEX [IX_incidentes_fecha_creacion] ON [dbo].[incidentes] ([fecha_creacion] DESC);
END
GO

-- 4. POBLADO DE USUARIOS INICIALES
-- Nota: La contraseña para ambos es 'admin123' encriptada con BCrypt (Costo: 10)
-- Hash BCrypt válido: $2a$10$tPJQWXeJiauPG1SQWwod3.12B1qse/.ACRyXrTqlR0eq34qN.oBUC

IF NOT EXISTS (SELECT 1 FROM [dbo].[usuarios] WHERE [username] = 'admin')
BEGIN
    PRINT 'Insertando usuario administrador inicial...';
    INSERT INTO [dbo].[usuarios] ([username], [password], [nombre_completo], [rol], [activo])
    VALUES (
        'admin',
        '$2a$10$tPJQWXeJiauPG1SQWwod3.12B1qse/.ACRyXrTqlR0eq34qN.oBUC',
        'Administrador del Sistema SA-GIT',
        'ADMIN',
        1
    );
END
GO

IF NOT EXISTS (SELECT 1 FROM [dbo].[usuarios] WHERE [username] = 'analista')
BEGIN
    PRINT 'Insertando usuario analista de prueba...';
    INSERT INTO [dbo].[usuarios] ([username], [password], [nombre_completo], [rol], [activo])
    VALUES (
        'analista',
        '$2a$10$tPJQWXeJiauPG1SQWwod3.12B1qse/.ACRyXrTqlR0eq34qN.oBUC',
        'Analista TI de Turno',
        'ANALISTA',
        1
    );
END
GO

-- 5. POBLADO DE DATOS DE EJEMPLO DE INCIDENTES
IF NOT EXISTS (SELECT 1 FROM [dbo].[incidentes] WHERE [servicio_afectado] = 'Servidor de Base de Datos')
BEGIN
    PRINT 'Insertando incidentes de ejemplo...';
    INSERT INTO [dbo].[incidentes] ([fuente_monitoreo], [servicio_afectado], [descripcion_falla], [estado], [prioridad], [usuario_registro], [fecha_creacion])
    VALUES 
    (
        'Prometheus',
        'Servidor de Base de Datos',
        'Caída de servicio de caja por saturación de conexiones en el pool transaccional',
        'ABIERTO',
        'ALTA',
        'admin',
        SYSUTCDATETIME()
    ),
    (
        'Grafana',
        'API Gateway Pagos',
        'Latencia anormal > 4500ms en endpoint de autorización de tarjetas',
        'EN_PROCESO',
        'MEDIA',
        'analista',
        DATEADD(MINUTE, -30, SYSUTCDATETIME())
    ),
    (
        'Manual',
        'Servicio de Facturación Electrónica',
        'Falla de sincronización con servidor de la administración tributaria',
        'RESUELTO',
        'BAJA',
        'admin',
        DATEADD(HOUR, -2, SYSUTCDATETIME())
    );
END
GO

PRINT '=======================================================';
PRINT 'Base de datos sagit_db inicializada con éxito.';
PRINT 'Usuario disponible: admin / Password: admin123';
PRINT '=======================================================';
GO
