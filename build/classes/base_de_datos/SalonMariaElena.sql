-- =============================================
-- BASE DE DATOS: BDSpaMariaElena
-- Sistema de Reserva y Control de Citas
-- Autor: Cesar
-- Fecha: 2025-10-05
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
-- 1️⃣ TABLA: Distrito
-- =============================================
CREATE TABLE Distrito (
    Id_Distrito CHAR(6) PRIMARY KEY,
    Nombre_Distrito VARCHAR(50) NOT NULL,
    Codigo_Postal VARCHAR(10)
);
GO
--  DISTRITOS
INSERT INTO Distrito (Id_Distrito, Nombre_Distrito, Codigo_Postal) VALUES
('150101','Cercado de Lima','15001'),
('150102','Ancón','15021'),
('150103','Ate','15017'),
('150104','Barranco','15063'),
('150105','Breña','15004'),
('150106','Carabayllo','15101'),
('150107','Chaclacayo','15032'),
('150108','Chorrillos','15067'),
('150109','Cieneguilla','15822'),
('150110','Comas','15313'),
('150111','El Agustino','15026'),
('150112','Independencia','15311'),
('150113','Jesús Maria','15072'),
('150114','La Molina','15024'),
('150115','La Victoria','15023'),
('150116','Lince','15046'),
('150117','Los Olivos','15320'),
('150118','Lurigancho','15401'),
('150119','Lurín','15820'),
('150120','Magdalena del Mar','15076'),
('150121','Pueblo Libre','15084'),
('150122','Miraflores','15074'),
('150123','Pachacámac','15817'),
('150124','Pucusana','15821'),
('150125','Puente Piedra','15212'),
('150126','Punta Hermosa','15838'),
('150127','Punta Negra','15839'),
('150128','Rímac','15312'),
('150129','San Bartolo','15830'),
('150130','San Borja','15036'),
('150131','San Isidro','15073'),
('150132','San Juan de Lurigancho','15412'),
('150133','San Juan de Miraflores','15413'),
('150134','San Luis','15021'),
('150135','San Martín de Porres','15101'),
('150136','San Miguel','15088'),
('150137','Santa Anita','15033'),
('150138','Santa Maria del Mar','15074'),
('150139','Santa Rosa','15073'),
('150140','Santiago de Surco','15023'),
('150141','Surquillo','15047'),
('150142','Villa El Salvador','15412'),
('150143','Villa María del Triunfo','15413');
GO
-- =============================================
-- 2️⃣ TABLA: Area
-- =============================================
CREATE TABLE Area (
    Id_Area INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Area VARCHAR(50) NOT NULL
);
GO
-------AREAS
INSERT INTO Area (Nombre_Area)
VALUES
('Estetica'),
('Corte y Peinado'),
('Manicure y Pedicure'),
('Masajes y Spa'),
('Depilación'),
('Tratamientos Faciales'),
('Coloración y Tintura'),
('Maquillaje Profesional');
GO
-- =============================================
-- 3️⃣ TABLA: Turno
-- =============================================
CREATE TABLE Turno (
    Id_Turno INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Turno VARCHAR(20) NOT NULL,       -- Mañana, Tarde, Noche
    Hora_Inicio TIME NOT NULL,
    Hora_Fin TIME NOT NULL
);
GO

INSERT INTO Turno (Nombre_Turno, Hora_Inicio, Hora_Fin) VALUES
('Mañana', '08:00', '11:59'),
('Tarde',  '12:00', '18:59'),
('Noche',  '19:00', '23:00');
GO

-- =============================================
-- 4️⃣ TABLA: Cliente
-- =============================================
CREATE TABLE Cliente (
    Id_Cliente INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Cliente VARCHAR(60) NOT NULL,
    Apellido_Cliente VARCHAR(80) NOT NULL,
    Email_Cliente VARCHAR(250),
    Telefono_Cliente VARCHAR(9),
    Id_Distrito CHAR(6) NOT NULL,
	Estado_Cliente BIT DEFAULT 1,
	Fecha_Registro DATETIME DEFAULT GETDATE()
    FOREIGN KEY (Id_Distrito) REFERENCES Distrito(Id_Distrito)
        ON UPDATE CASCADE
        ON DELETE NO ACTION
);
GO

-- =============================================
-- 5️⃣ TABLA: Servicio
-- =============================================
CREATE TABLE Servicio (
    Id_Servicio INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Servicio VARCHAR(50) NOT NULL,
    Precio_Servicio DECIMAL(10,2) NOT NULL,
    Duracion_Minutos INT,
    Id_Area INT NOT NULL,
    CONSTRAINT FK_Servicio_Area FOREIGN KEY (Id_Area)
        REFERENCES Area(Id_Area)
        ON UPDATE CASCADE
        ON DELETE CASCADE  -- borra automáticamente servicios si se borra el área
);
GO
INSERT INTO Servicio (Nombre_Servicio, Precio_Servicio, Duracion_Minutos, Id_Area)
VALUES
-- 1️⃣ Estética
('Limpieza facial básica', 50.00, 45, 1),
('Exfoliación corporal', 80.00, 60, 1),
('Tratamiento reafirmante', 90.00, 75, 1),

-- 2️⃣ Corte y Peinado
('Corte de cabello dama', 35.00, 30, 2),
('Corte de cabello caballero', 25.00, 25, 2),
('Peinado de fiesta', 60.00, 40, 2),

-- 3️⃣ Manicure y Pedicure
('Manicure clásico', 25.00, 30, 3),
('Pedicure spa', 40.00, 45, 3),
('Manicure con gel', 35.00, 40, 3),

-- 4️⃣ Masajes y Spa
('Masaje relajante', 70.00, 60, 4),
('Masaje descontracturante', 85.00, 75, 4),
('Terapia con piedras calientes', 100.00, 90, 4),

-- 5️⃣ Depilación
('Depilación facial', 25.00, 20, 5),
('Depilación de piernas completas', 55.00, 45, 5),
('Depilación de cejas', 15.00, 15, 5),

-- 6️⃣ Tratamientos Faciales
('Tratamiento hidratante', 65.00, 50, 6),
('Tratamiento antiacné', 80.00, 60, 6),
('Rejuvenecimiento facial', 100.00, 75, 6),

-- 7️⃣ Coloración y Tintura
('Tinte completo', 90.00, 90, 7),
('Mechas balayage', 150.00, 120, 7),
('Baño de color', 60.00, 60, 7),

-- 8️⃣ Maquillaje Profesional
('Maquillaje social', 70.00, 45, 8),
('Maquillaje para novia', 150.00, 90, 8),
('Maquillaje artístico', 120.00, 75, 8);

GO
-- =============================================
-- 6️⃣ TABLA: Trabajador
-- =============================================
CREATE TABLE Trabajador (
    Id_Trabajador INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Trabajador VARCHAR(50) NOT NULL,
    Apellido_Trabajador VARCHAR(50) NOT NULL,
    Especialidad_Trabajador VARCHAR(30),
    Id_Turno INT NOT NULL,
    Id_Area INT NOT NULL,
	Estado_Trabajador BIT DEFAULT 1,
	Fecha_Registro DATETIME DEFAULT GETDATE()
    FOREIGN KEY (Id_Turno) REFERENCES Turno(Id_Turno)
        ON UPDATE CASCADE
        ON DELETE NO ACTION,
    FOREIGN KEY (Id_Area) REFERENCES Area(Id_Area)
        ON UPDATE CASCADE
        ON DELETE NO ACTION
);
GO

-- =============================================
--  TABLA: Usuario
-- =============================================
CREATE TABLE Usuario (
    Id_Usuario INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Usuario VARCHAR(50) NOT NULL,
    Correo_Usuario VARCHAR(100),
	Rol_Usuario VARCHAR(30),
    Password_Usuario VARCHAR(255) NOT NULL,
    Estado_Usuario BIT DEFAULT 1,
	Fecha_Registro DATETIME DEFAULT GETDATE(),
	Id_Rol INT NOT NULL
    FOREIGN KEY (Id_Rol) REFERENCES Rol(Id_Rol)
);
GO
-- =============================================
-- TABLA: Rol
-- =============================================
CREATE TABLE Rol (
    Id_Rol INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Rol VARCHAR(50) NOT NULL,
    Descripcion_Rol VARCHAR(200)
);
-- =============================================
-- TABLA: Permiso
-- =============================================
CREATE TABLE Permiso (
    Id_Permiso INT IDENTITY(1,1) PRIMARY KEY,
    Nombre_Permiso VARCHAR(100) NOT NULL
);
-- =============================================
-- TABLA: Rol_Permiso
-- =============================================
CREATE TABLE Rol_Permiso (
    Id_Rol INT,
    Id_Permiso INT,
    PRIMARY KEY (Id_Rol, Id_Permiso),
    FOREIGN KEY (Id_Rol) REFERENCES Rol(Id_Rol),
    FOREIGN KEY (Id_Permiso) REFERENCES Permiso(Id_Permiso)
);
-- =============================================
--  TABLA: Cita
-- =============================================
CREATE TABLE Cita (
    Id_Cita INT IDENTITY(1,1) PRIMARY KEY,
    Fecha_Cita DATE NOT NULL,
    Hora_Cita TIME NOT NULL,
    Estado_Cita VARCHAR(20) DEFAULT 'Pendiente',
    Observacion_Cita VARCHAR(200) NULL,
    Id_Cliente INT NOT NULL,
    Id_Trabajador INT NOT NULL,
    Id_Usuario INT NOT NULL,
    Id_Servicio INT NOT NULL,
	Fecha_Registro DATETIME DEFAULT GETDATE()
    FOREIGN KEY (Id_Cliente) REFERENCES Cliente(Id_Cliente),
    FOREIGN KEY (Id_Trabajador) REFERENCES Trabajador(Id_Trabajador),
    FOREIGN KEY (Id_Usuario) REFERENCES Usuario(Id_Usuario),
    FOREIGN KEY (Id_Servicio) REFERENCES Servicio(Id_Servicio)
);
GO
