-- =============================================
-- BASE DE DATOS: SalonMariaElena
-- Sistema de Reserva y Control de Citas
-- Autor: [Cesar]
-- Fecha: [05/10/25]
-- =============================================
IF DB_ID('BDSpaMariaElena') IS NOT NULL
    DROP DATABASE BDSpaMariaElena;
GO

CREATE DATABASE BDSpaMariaElena;
GO
USE BDSpaMariaElena;
GO
-- =============================================
-- LIMPIEZA PREVIA (opcional, ejecuta solo si ya existen)
-- =============================================
IF OBJECT_ID('Cita', 'U') IS NOT NULL DROP TABLE Cita;
IF OBJECT_ID('Usuario', 'U') IS NOT NULL DROP TABLE Usuario;
IF OBJECT_ID('Trabajador', 'U') IS NOT NULL DROP TABLE Trabajador;
IF OBJECT_ID('Servicio', 'U') IS NOT NULL DROP TABLE Servicio;
IF OBJECT_ID('Cliente', 'U') IS NOT NULL DROP TABLE Cliente;
IF OBJECT_ID('Area', 'U') IS NOT NULL DROP TABLE Area;
IF OBJECT_ID('Distrito', 'U') IS NOT NULL DROP TABLE Distrito;
GO

-- =============================================
-- 1. TABLA: Distrito
-- =============================================
CREATE TABLE Distrito (
    Id_Distrito CHAR(6) PRIMARY KEY,   -- Código de ubigeo
    Nombre_Distrito VARCHAR(50) NOT NULL,
    Codigo_Postal VARCHAR(10)
);
GO

-- =============================================
-- 2. TABLA: Area
-- =============================================
CREATE TABLE Area (
    Id_Area INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Area VARCHAR(50) NOT NULL
);
GO

-- =============================================
-- 3. TABLA: Cliente
-- =============================================
CREATE TABLE Cliente (
    Id_Cliente INT  PRIMARY KEY,
    Nombre_Cliente VARCHAR(50) NOT NULL,
    Apellido_Cliente VARCHAR(50) NOT NULL,
    Email_Cliente VARCHAR(100),
    Telefono_Cliente VARCHAR(15),
    Id_Distrito CHAR(6) NOT NULL,
    FOREIGN KEY (Id_Distrito) REFERENCES Distrito(Id_Distrito)
        ON UPDATE CASCADE
        ON DELETE NO ACTION
);
GO

-- =============================================
-- 4. TABLA: Servicio
-- =============================================
CREATE TABLE Servicio (
    Id_Servicio INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Servicio VARCHAR(50) NOT NULL,
    Precio_Servicio DECIMAL(10,2) NOT NULL,
    Duracion_Minutos INT,
    Id_Area INT NOT NULL,
    FOREIGN KEY (Id_Area) REFERENCES Area(Id_Area)
        ON UPDATE CASCADE
        ON DELETE NO ACTION
);
GO

-- =============================================
-- 5. TABLA: Trabajador
-- =============================================
CREATE TABLE Trabajador (
    Id_Trabajador INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Trabajador VARCHAR(50) NOT NULL,
    Apellido_Trabajador VARCHAR(50) NOT NULL,
    Especialidad_Trabajador VARCHAR(50),
    Horario_Trabajo TIME,
    Id_Area INT NOT NULL,
    FOREIGN KEY (Id_Area) REFERENCES Area(Id_Area)
        ON UPDATE CASCADE
        ON DELETE NO ACTION
);
GO

-- =============================================
-- 6. TABLA: Usuario
-- =============================================
CREATE TABLE Usuario (
    Id_Usuario INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Usuario VARCHAR(50) NOT NULL,
    Rol_Usuario VARCHAR(30) NOT NULL,
    Correo_Usuario VARCHAR(100),
    Password_Usuario VARCHAR(255) NOT NULL,
    Estado_Usuario BIT DEFAULT 1
);
GO

-- =============================================
-- 7. TABLA: Cita
-- =============================================
CREATE TABLE Cita (
    Id_Cita INT IDENTITY(1,1) PRIMARY KEY,
    Fecha_Cita DATE NOT NULL,
    Hora_Cita TIME NOT NULL,
    Estado_Cita VARCHAR(20) DEFAULT 'Pendiente',
    Observacion_Cita VARCHAR(200),
    Id_Cliente INT NOT NULL,
    Id_Trabajador INT NOT NULL,
    Id_Usuario INT NOT NULL,
    Id_Servicio INT NOT NULL,
    FOREIGN KEY (Id_Cliente) REFERENCES Cliente(Id_Cliente)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    FOREIGN KEY (Id_Trabajador) REFERENCES Trabajador(Id_Trabajador)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    FOREIGN KEY (Id_Usuario) REFERENCES Usuario(Id_Usuario)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    FOREIGN KEY (Id_Servicio) REFERENCES Servicio(Id_Servicio)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
GO


-----APARTE SE EJECUTA
-- =============================================
----PROCEDIMIENTO ALMACENADO
-- =============================================
CREATE PROCEDURE sp_registrarUsuario
    @Nombre_Usuario NVARCHAR(50),
    @Rol_Usuario NVARCHAR(50),
    @Correo_Usuario NVARCHAR(100),
    @Password_Usuario NVARCHAR(255),
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    -- Verificar si ya existe el nombre de usuario
    IF EXISTS (SELECT 1 FROM Usuario WHERE Nombre_Usuario = @Nombre_Usuario)
    BEGIN
        SET @Resultado = -1;  -- Usuario ya existente
        RETURN;
    END

    BEGIN TRY
        INSERT INTO Usuario (Nombre_Usuario, Rol_Usuario, Correo_Usuario, Password_Usuario, Estado_Usuario)
        VALUES (@Nombre_Usuario, @Rol_Usuario, @Correo_Usuario, @Password_Usuario, 1);

        SET @Resultado = 1; -- Registro exitoso
    END TRY
    BEGIN CATCH
        SET @Resultado = 0; -- Error general
    END CATCH
END;
GO
-----------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_validarLogin
    @Nombre_Usuario NVARCHAR(50),
    @Password_Usuario NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    -- Retorna los datos del usuario si coinciden nombre y contraseña
    SELECT 
        Id_Usuario,
        Nombre_Usuario,
        Rol_Usuario,
        Correo_Usuario,
        Estado_Usuario
    FROM Usuario
    WHERE Nombre_Usuario = @Nombre_Usuario
      AND Password_Usuario = @Password_Usuario
      AND Estado_Usuario = 1; -- Opcional: solo usuarios activos
END;
GO
-----------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_eliminarUsuario
    @Id_Usuario INT,
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM Usuario WHERE Id_Usuario = @Id_Usuario AND Estado_Usuario = 0)
    BEGIN
        BEGIN TRY
            DELETE FROM Usuario WHERE Id_Usuario = @Id_Usuario;
            SET @Resultado = 1; -- Eliminado correctamente
        END TRY
        BEGIN CATCH
            SET @Resultado = 0; -- Error general
        END CATCH
    END
    ELSE
    BEGIN
        SET @Resultado = -1; -- No se puede eliminar: usuario activo o no existe
    END
END;
GO
--------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_actualizarUsuario
    @Id_Usuario INT,
    @Nombre_Usuario NVARCHAR(50),
    @Rol_Usuario NVARCHAR(50),
    @Correo_Usuario NVARCHAR(100),
    @Password_Usuario NVARCHAR(255),
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM Usuario WHERE Id_Usuario = @Id_Usuario)
    BEGIN
        BEGIN TRY
            UPDATE Usuario
            SET Nombre_Usuario = @Nombre_Usuario,
                Rol_Usuario = @Rol_Usuario,
                Correo_Usuario = @Correo_Usuario,
                Password_Usuario = @Password_Usuario
            WHERE Id_Usuario = @Id_Usuario;

            SET @Resultado = 1; -- Actualización exitosa
        END TRY
        BEGIN CATCH
            SET @Resultado = 0; -- Error general
        END CATCH
    END
    ELSE
    BEGIN
        SET @Resultado = -1; -- Usuario no encontrado
    END
END;
GO
--------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_buscarUsuarioPorId
    @Id_Usuario INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        Id_Usuario,
        Nombre_Usuario,
        Rol_Usuario,
        Correo_Usuario,
        Estado_Usuario
    FROM Usuario
    WHERE Id_Usuario = @Id_Usuario;
END;
GO
-------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_listarUsuarios
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        Id_Usuario,
        Nombre_Usuario,
        Rol_Usuario,
        Correo_Usuario,
        Estado_Usuario
    FROM Usuario
    ORDER BY Nombre_Usuario;
END;
GO

