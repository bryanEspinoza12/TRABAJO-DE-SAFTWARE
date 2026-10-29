-- =====================================================================
-- SISTEMA SA-GIT - SCRIPT DE DATOS INICIALES DML (SQL SERVER)
-- =====================================================================

USE sagit_db;
GO

-- Inserción de usuario Administrador (Password: 'admin123' encriptada con BCrypt)
IF NOT EXISTS (SELECT 1 FROM [dbo].[usuarios] WHERE [username] = 'admin')
BEGIN
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

-- Inserción de usuario Analista de ejemplo (Password: 'admin123')
IF NOT EXISTS (SELECT 1 FROM [dbo].[usuarios] WHERE [username] = 'analista')
BEGIN
    INSERT INTO [dbo].[usuarios] ([username], [password], [nombre_completo], [rol], [activo])
    VALUES (
        'analista',
        '$2a$10$tPJQWXeJiauPG1SQWwod3.12B1qse/.ACRyXrTqlR0eq34qN.oBUC',
        'Analista de Incidentes TI',
        'ANALISTA',
        1
    );
END
GO

-- Inserción de incidentes de prueba según el wireframe
IF NOT EXISTS (SELECT 1 FROM [dbo].[incidentes] WHERE [servicio_afectado] = 'Servidor de Base de Datos')
BEGIN
    INSERT INTO [dbo].[incidentes] ([fuente_monitoreo], [servicio_afectado], [descripcion_falla], [estado], [prioridad], [usuario_registro], [fecha_creacion])
    VALUES (
        'Prometheus',
        'Servidor de Base de Datos',
        'Caída de servicio de caja por sobrecarga en pool de conexiones',
        'ABIERTO',
        'ALTA',
        'admin',
        SYSUTCDATETIME()
    );
END
GO
