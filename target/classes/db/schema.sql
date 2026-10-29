-- =====================================================================
-- SISTEMA SA-GIT - SCRIPT DE ESTRUCTURA DDL (SQL SERVER)
-- =====================================================================

-- 1. Creación de la base de datos si no existe
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'sagit_db')
BEGIN
    CREATE DATABASE sagit_db;
END
GO

USE sagit_db;
GO

-- 2. Creación de la tabla 'usuarios'
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usuarios]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[usuarios] (
        [id] BIGINT IDENTITY(1,1) NOT NULL,
        [username] NVARCHAR(50) NOT NULL,
        [password] NVARCHAR(100) NOT NULL,
        [nombre_completo] NVARCHAR(150) NOT NULL,
        [rol] NVARCHAR(20) NOT NULL,
        [activo] BIT NOT NULL CONSTRAINT [DF_usuarios_activo] DEFAULT (1),
        CONSTRAINT [PK_usuarios] PRIMARY KEY CLUSTERED ([id] ASC),
        CONSTRAINT [UQ_usuarios_username] UNIQUE NONCLUSTERED ([username] ASC)
    );
END
GO

-- 3. Creación de la tabla 'incidentes'
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[incidentes]') AND type in (N'U'))
BEGIN
    CREATE TABLE [dbo].[incidentes] (
        [id] BIGINT IDENTITY(1,1) NOT NULL,
        [fuente_monitoreo] NVARCHAR(100) NOT NULL,
        [servicio_afectado] NVARCHAR(150) NOT NULL,
        [descripcion_falla] NVARCHAR(1000) NOT NULL,
        [estado] NVARCHAR(20) NOT NULL CONSTRAINT [DF_incidentes_estado] DEFAULT ('ABIERTO'),
        [prioridad] NVARCHAR(20) NULL,
        [usuario_registro] NVARCHAR(50) NOT NULL,
        [fecha_creacion] DATETIME2 NOT NULL CONSTRAINT [DF_incidentes_fecha_creacion] DEFAULT (SYSUTCDATETIME()),
        CONSTRAINT [PK_incidentes] PRIMARY KEY CLUSTERED ([id] ASC)
    );

    CREATE NONCLUSTERED INDEX [IX_incidentes_fecha_creacion] ON [dbo].[incidentes] ([fecha_creacion] DESC);
END
GO
