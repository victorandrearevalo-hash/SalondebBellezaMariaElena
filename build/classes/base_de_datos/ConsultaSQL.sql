--------------------------------------------------------------------------------------------
select * from Area
select * from Turno
--------------------------------------------------------------------------------------------
SELECT 
    c.Id_Cita,
    cli.Nombre_Cliente + ' ' + cli.Apellido_Cliente AS Cliente,
    s.Nombre_Servicio AS Servicio,
    t.Nombre_Trabajador + ' ' + t.Apellido_Trabajador AS Trabajador,
    c.Fecha_Cita,
    c.Hora_Cita,
    c.Estado_Cita,
    c.Observacion_Cita
FROM Cita c
INNER JOIN Cliente cli ON c.Id_Cliente = cli.Id_Cliente
INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
INNER JOIN Trabajador t ON c.Id_Trabajador = t.Id_Trabajador
WHERE cli.Id_Cliente = 1; 
--------------------------------------------------------------------------------------------
SELECT 
        T.Id_Trabajador,
        T.Nombre_Trabajador,
        T.Apellido_Trabajador,
        T.Especialidad_Trabajador,
        A.Nombre_Area AS Area,
        Tu.Nombre_Turno AS Turno
    FROM Trabajador T
    RIGHT JOIN Area A ON T.Id_Area = A.Id_Area
    RIGHT JOIN Turno Tu ON T.Id_Turno = Tu.Id_Turno
    ORDER BY T.Apellido_Trabajador;
--------------------------------------------------------------------------------------------
	SELECT 
        T.Id_Trabajador,
        T.Nombre_Trabajador,
        T.Apellido_Trabajador,
        T.Especialidad_Trabajador,
        A.Nombre_Area AS Area,
        Tu.Nombre_Turno AS Turno
    FROM Trabajador T
    LEFT JOIN Area A ON T.Id_Area = A.Id_Area
    LEFT JOIN Turno Tu ON T.Id_Turno = Tu.Id_Turno
    ORDER BY T.Apellido_Trabajador;
--------------------------------------------------------------------------------------------
select * from Cita
select * from Cliente
select * from Trabajador
select * from Distrito

select * from Area
select * from Servicio


delete from Area where Id_Area=9
delete from Servicio where Id_Servicio=25
-------------------Resetear identity
DBCC CHECKIDENT ('Usuario', RESEED, 3);


-- INSERT DE TRABAJADORES
INSERT INTO Trabajador (Nombre_Trabajador, Apellido_Trabajador, Especialidad_Trabajador, Id_Turno, Id_Area)
VALUES 
('Ana', 'Gómez', 'Estética', 1, 1),
('Luis', 'Pérez', 'Corte y Peinado', 2, 2),
('María', 'López', 'Manicure y Pedicure', 1, 3),
('Carlos', 'Ramírez', 'Masajes y Spa', 3, 4),
('Sofía', 'Torres', 'Depilación', 2, 5),
('Valeria', 'Morales', 'Tratamientos Faciales', 1, 6),
('Jorge', 'Sánchez', 'Coloración y Tintura', 2, 7),
('Laura', 'Vega', 'Maquillaje Profesional', 1, 8);

-- INSERT DE CLIENTES
INSERT INTO Cliente (Nombre_Cliente, Apellido_Cliente, Email_Cliente, Telefono_Cliente, Id_Distrito)
VALUES
('Ana', 'Gomez', 'ana.gomez@email.com', '987654321', 'D001'),
('Luis', 'Perez', 'luis.perez@email.com', '912345678', 'D002'),
('María', 'Lopez', 'maria.lopez@email.com', '923456789', 'D003'),
('Carlos', 'Ramirez', 'cramirez@email.com', '934567890', 'D004'),
('Sofia', 'Torres', 'sofia.torres@email.com', '945678901', 'D005');



DECLARE @Resultado INT;
EXEC sp_validarLogin
    @Nombre_Usuario = 'Administrador',  
    @Password_Usuario = '513e0eadd1797d65bb277b9b4de782124731935e83e6e3948fd970371761e900', 
    @Resultado = @Resultado OUTPUT;

SELECT @Resultado AS Resultado;
select * from Usuario




SELECT 
    t.Id_Trabajador,
    t.Nombre_Trabajador,
    c.Id_Cita,
    c.Fecha_Cita,
    c.Hora_Cita AS Hora_Inicio,

    -- Hora fin calculada
    DATEADD(MINUTE, s.Duracion_Minutos, c.Hora_Cita) AS Hora_Fin,

    s.Nombre_Servicio,
    s.Duracion_Minutos,
    cl.Nombre_Cliente
FROM Cita c
INNER JOIN Trabajador t ON t.Id_Trabajador = c.Id_Trabajador
INNER JOIN Servicio s ON s.Id_Servicio = c.Id_Servicio
INNER JOIN Cliente cl ON cl.Id_Cliente = c.Id_Cliente
ORDER BY 
    t.Nombre_Trabajador,
    c.Fecha_Cita,
    c.Hora_Cita;


SELECT 
    c.Id_Cita,
    c.Fecha_Cita,
    c.Hora_Cita AS Hora_Inicio,
    DATEADD(MINUTE, s.Duracion_Minutos, c.Hora_Cita) AS Hora_Fin,
    s.Nombre_Servicio,
    cl.Nombre_Cliente
FROM Cita c
INNER JOIN Servicio s ON s.Id_Servicio = c.Id_Servicio
INNER JOIN Cliente cl ON cl.Id_Cliente = c.Id_Cliente
WHERE c.Id_Trabajador = 8
ORDER BY c.Fecha_Cita, c.Hora_Cita;


GO


INSERT INTO Rol_Permiso (Id_Rol, Id_Permiso)
SELECT 1, Id_Permiso FROM Permiso;


INSERT INTO Rol_Permiso VALUES
(2, 1), -- Registrar cliente
(2, 2), -- Editar cliente
(2, 10), -- Ver reportes
(2, 6), -- Registrar trabajador
(2, 7), -- Editar trabajador
(2, 8), -- Registrar servicios
(2, 9); -- Editar servicios



select * from Usuario
select * from rol
select * from Permiso
select * from Rol_Permiso


SELECT r.Nombre_Rol, p.Nombre_Permiso
FROM Rol_Permiso rp
JOIN Rol r ON rp.Id_Rol = r.Id_Rol
JOIN Permiso p ON rp.Id_Permiso = p.Id_Permiso
WHERE r.Nombre_Rol LIKE '%Administrador%';


-- Agregar nuevos permisos
INSERT INTO Permiso (Nombre_Permiso) VALUES ('Eliminar_Servicio');

-- Asignar permisos 
INSERT INTO Rol_Permiso (Id_Rol, Id_Permiso) VALUES (3, 12);


SELECT 
    u.Id_Usuario,
    u.Nombre_Usuario,
    r.Nombre_Rol,
    p.Nombre_Permiso
FROM Usuario u
JOIN Rol r ON u.Id_Rol = r.Id_Rol
LEFT JOIN Rol_Permiso rp ON r.Id_Rol = rp.Id_Rol
LEFT JOIN Permiso p ON rp.Id_Permiso = p.Id_Permiso
ORDER BY u.Id_Usuario;



DECLARE @r INT;
EXEC sp_registrarCita 
    @Fecha_Cita = '2025-11-24',
    @Hora_Cita = '19:00',
    @Observacion_Cita = 'Prueba directa SQL',
    @Id_Cliente = 1,
    @Id_Trabajador = 4,
    @Id_Usuario = 2,
    @Id_Servicio = 5,
    @Resultado = @r OUTPUT;

SELECT @r AS Resultado;

------Verificar si esta bien 
SELECT 
    c.Id_Cita,
    c.Fecha_Cita,
    c.Hora_Cita AS Inicio_Cita,
    DATEADD(MINUTE, s.Duracion_Minutos, c.Hora_Cita) AS Fin_Cita,
    s.Duracion_Minutos,
    c.Id_Trabajador
FROM Cita c
INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
WHERE 
    c.Id_Trabajador = 4           -- <-- cambia por el trabajador que estás probando
    AND c.Fecha_Cita = '2025-11-24'  -- <-- tu fecha de prueba
ORDER BY c.Hora_Cita;



select * from Usuario