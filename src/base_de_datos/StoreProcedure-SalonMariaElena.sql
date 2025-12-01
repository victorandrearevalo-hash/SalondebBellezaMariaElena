-----APARTE SE EJECUTA
-- =============================================
----PROCEDIMIENTO ALMACENADO
-- =============================================
CREATE PROCEDURE sp_validarLogin
     @Nombre_Usuario NVARCHAR(50), 
    @Password_Usuario NVARCHAR(255), 
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    -- Usuario no existe
    IF NOT EXISTS (SELECT 1 FROM Usuario WHERE Nombre_Usuario = @Nombre_Usuario)
    BEGIN
        SET @Resultado = -1;
        RETURN;
    END

    -- Contraseña incorrecta o usuario inactivo
    IF NOT EXISTS (SELECT 1 FROM Usuario 
                   WHERE Nombre_Usuario = @Nombre_Usuario
                         AND Password_Usuario = @Password_Usuario
                         AND Estado_Usuario = 1)
    BEGIN
        SET @Resultado = 0;
        RETURN;
    END
	-- Login correcto: primero setear OUTPUT
    SET @Resultado = 1;

    -- Luego devolver los datos del usuario
    SELECT Id_Usuario, Nombre_Usuario, Rol_Usuario, Correo_Usuario, Estado_Usuario
    FROM Usuario
    WHERE Nombre_Usuario = @Nombre_Usuario;
END
GO

-----------------------------------------------------------------------------------
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
        INSERT INTO Usuario (Nombre_Usuario, Rol_Usuario, Correo_Usuario, Password_Usuario, Estado_Usuario,Fecha_Registro)
        VALUES (@Nombre_Usuario, @Rol_Usuario, @Correo_Usuario, @Password_Usuario, 1,GETDATE());

        SET @Resultado = 1; -- Registro exitoso
    END TRY
    BEGIN CATCH
        SET @Resultado = 0; -- Error general
    END CATCH
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
    @Password_Usuario NVARCHAR(255)= NULL, -- puede venir nulo
	@Estado_Usuario BIT, 
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM Usuario WHERE Id_Usuario = @Id_Usuario)
    BEGIN
        BEGIN TRY
            UPDATE Usuario
            SET 
				Nombre_Usuario = @Nombre_Usuario,
                Rol_Usuario = @Rol_Usuario,
                Correo_Usuario = @Correo_Usuario,
                Password_Usuario = COALESCE(@Password_Usuario, Password_Usuario), 
				Estado_Usuario = @Estado_Usuario  
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
CREATE PROCEDURE sp_buscarUsuario
    @Criterio NVARCHAR(50)
AS
BEGIN
	-- Intentar convertir a INT
    DECLARE @Id INT = TRY_CAST(@Criterio AS INT);

    SET NOCOUNT ON;

    SELECT 
        Id_Usuario,
        Nombre_Usuario,
        Rol_Usuario,
        Correo_Usuario,
        Estado_Usuario
    FROM Usuario
    WHERE 
        -- Si el parámetro puede convertirse a entero, busca por ID
        (@Id IS NOT NULL AND Id_Usuario = @Id)
        -- Si no es número, busca por nombre (LIKE)
        OR (@Id IS NULL AND Nombre_Usuario LIKE '%' + @Criterio + '%');
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
        Estado_Usuario,
		Password_Usuario
    FROM Usuario
END;
GO
-------------------------------------------------------------------------------------------------------------
-- =============================================
-- 1️⃣ INSERTAR TRABAJADOR
-- =============================================
CREATE PROCEDURE sp_registrarTrabajador
	@Nombre_Trabajador VARCHAR(50),
    @Apellido_Trabajador VARCHAR(50),
    @Especialidad_Trabajador VARCHAR(30),
    @Id_Turno INT,
    @Id_Area INT,
	@Estado BIT,
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        INSERT INTO Trabajador (Nombre_Trabajador, Apellido_Trabajador, Especialidad_Trabajador, Id_Turno, Id_Area,Estado_Trabajador,Fecha_Registro)
        VALUES (@Nombre_Trabajador, @Apellido_Trabajador, @Especialidad_Trabajador, @Id_Turno, @Id_Area,@Estado,GETDATE());

        SET @Resultado = SCOPE_IDENTITY(); -- Devuelve el ID insertado
    END TRY
    BEGIN CATCH
        SET @Resultado = -1; -- Error genérico
    END CATCH
END;

GO
-- =============================================
-- 2️⃣ ACTUALIZAR TRABAJADOR
-- =============================================
CREATE PROCEDURE sp_actualizarTrabajador
    @Id_Trabajador INT,
    @Nombre_Trabajador VARCHAR(50),
    @Apellido_Trabajador VARCHAR(50),
    @Especialidad_Trabajador VARCHAR(30),
    @Id_Turno INT,
    @Id_Area INT,
	@Estado BIT,
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM Trabajador WHERE Id_Trabajador = @Id_Trabajador)
    BEGIN
        UPDATE Trabajador
        SET Nombre_Trabajador = @Nombre_Trabajador,
            Apellido_Trabajador = @Apellido_Trabajador,
            Especialidad_Trabajador = @Especialidad_Trabajador,
            Id_Turno = @Id_Turno,
            Id_Area = @Id_Area,
			Estado_Trabajador=@Estado
        WHERE Id_Trabajador = @Id_Trabajador;

        SET @Resultado = 1; -- Éxito
    END
    ELSE
        SET @Resultado = 0; -- No existe el registro
END;

/*DECLARE @Resultado INT;
EXEC sp_actualizarTrabajador
	1,
    'Luis',
    'Torres',
    'Podologo',
     1,
     1,
	@Resultado OUTPUT;
	SELECT @Resultado AS IdInsertado;

select * from Trabajador
*/
GO
-- =============================================
-- 3️⃣ ELIMINAR TRABAJADOR
-- =============================================
CREATE PROCEDURE sp_eliminarTrabajador
    @Id_Trabajador INT,
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM Trabajador WHERE Id_Trabajador = @Id_Trabajador)
    BEGIN
        DELETE FROM Trabajador WHERE Id_Trabajador = @Id_Trabajador;
        SET @Resultado = 1;
    END
    ELSE
        SET @Resultado = 0;
END;
GO
-- =============================================
-- 4️⃣ BUSCAR TRABAJADOR (por nombre, apellido o especialidad)
-- =============================================
CREATE PROCEDURE sp_buscarTrabajador
       @Criterio NVARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        T.Id_Trabajador,
        T.Nombre_Trabajador,
        T.Apellido_Trabajador,
        T.Especialidad_Trabajador,
        U.Nombre_Turno AS Turno,      
        A.Nombre_Area AS Area,        
        T.Estado_Trabajador
    FROM Trabajador T
    INNER JOIN Area A ON T.Id_Area = A.Id_Area
    INNER JOIN Turno U ON T.Id_Turno = U.Id_Turno
    WHERE 
        T.Nombre_Trabajador LIKE '%' + @Criterio + '%' OR
        T.Apellido_Trabajador LIKE '%' + @Criterio + '%' OR
        T.Especialidad_Trabajador LIKE '%' + @Criterio + '%'
    ORDER BY T.Apellido_Trabajador;
END;
GO
-- =============================================
-- 5️⃣ LISTAR TODOS LOS TRABAJADORES
-- =============================================
CREATE PROCEDURE sp_listarTrabajadores
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        T.Id_Trabajador,
        T.Nombre_Trabajador,
        T.Apellido_Trabajador,
        T.Especialidad_Trabajador,
        U.Nombre_Turno AS Turno,      
        A.Nombre_Area AS Area,
		T.Estado_Trabajador
    FROM Trabajador T
    INNER JOIN Area A ON T.Id_Area = A.Id_Area
    INNER JOIN Turno U ON T.Id_Turno = U.Id_Turno;
END;
GO





----------------------------------------------------------------------------------------
-- Insertar Cliente
CREATE PROCEDURE sp_registrarCliente
    @Nombre_Cliente     VARCHAR(60),
    @Apellido_Cliente   VARCHAR(80),
    @Email_Cliente      VARCHAR(250),
    @Telefono_Cliente   VARCHAR(9),
    @Id_Distrito        CHAR(6),
	@Estado					BIT,
    @Resultado          INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        -- Inserta el cliente
        INSERT INTO Cliente (
            Nombre_Cliente,
            Apellido_Cliente,
            Email_Cliente,
            Telefono_Cliente,
            Id_Distrito,
            Estado_Cliente,
			Fecha_Registro
        )
        VALUES (
            @Nombre_Cliente,
            @Apellido_Cliente,
            @Email_Cliente,
            @Telefono_Cliente,
            @Id_Distrito,
            @Estado,
			GETDATE()
        );

        -- Devuelve el ID del cliente insertado
        SET @Resultado = SCOPE_IDENTITY();
    END TRY
    BEGIN CATCH
        SET @Resultado = -1;
    END CATCH
END;
----DBCC CHECKIDENT('Cliente' , RESEED,0)
/*DECLARE @Resultado INT;
EXEC sp_registrarTrabajador
    'Luis',
    'Torres',
    'Podologo',
     1,
     1,
	 1,
	@Resultado OUTPUT;
	SELECT @Resultado AS IdInsertado;
*/
GO
---------------------------------------------------------------------------------------------------------------------------
-- Actualizar Cliente
CREATE PROCEDURE sp_actualizarCliente
     @Id_Cliente         INT,
    @Nombre_Cliente     VARCHAR(60),
    @Apellido_Cliente   VARCHAR(80),
    @Email_Cliente      VARCHAR(250),
    @Telefono_Cliente   VARCHAR(9),
    @Id_Distrito        CHAR(6),
    @Estado             BIT,
    @Resultado          INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        -- Verificar si el cliente existe
        IF EXISTS (SELECT 1 FROM Cliente WHERE Id_Cliente = @Id_Cliente)
        BEGIN
            UPDATE Cliente
            SET 
                Nombre_Cliente = @Nombre_Cliente,
                Apellido_Cliente = @Apellido_Cliente,
                Email_Cliente = @Email_Cliente,
                Telefono_Cliente = @Telefono_Cliente,
                Id_Distrito = @Id_Distrito,
                Estado_Cliente = @Estado
            WHERE Id_Cliente = @Id_Cliente;

            SET @Resultado = 1;  -- Actualización exitosa
        END
        ELSE
        BEGIN
            SET @Resultado = 0;  -- Cliente no encontrado
        END
    END TRY
    BEGIN CATCH
        SET @Resultado = -1;  -- Error en la ejecución
    END CATCH
END;
GO
---------------------------------------------------------------------------------------------------------------------------
-- Eliminar Cliente
CREATE PROCEDURE sp_eliminarCliente
    @Id_Cliente INT,
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM Cliente WHERE Id_Cliente = @Id_Cliente AND Estado_Cliente = 0)
    BEGIN
		BEGIN TRY
			DELETE FROM Cliente WHERE Id_Cliente = @Id_Cliente;
			SET @Resultado = 1;
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
-----select * from Cliente
GO
---------------------------------------------------------------------------------------------------------------------------
-- Buscar Cliente (por nombre o ID)
CREATE PROCEDURE sp_buscarCliente
   @Criterio NVARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Id_Cliente INT = TRY_CAST(@Criterio AS INT);

    SELECT 
        c.Id_Cliente,
        c.Nombre_Cliente,
        c.Apellido_Cliente,
        c.Email_Cliente,
        c.Telefono_Cliente,
        d.Nombre_Distrito AS Distrito,
        c.Estado_Cliente
    FROM Cliente c
    INNER JOIN Distrito d ON c.Id_Distrito = d.Id_Distrito
    WHERE 
        c.Nombre_Cliente LIKE '%' + @Criterio + '%'
        OR c.Apellido_Cliente LIKE '%' + @Criterio + '%'
        OR d.Nombre_Distrito LIKE '%' + @Criterio + '%'
    ORDER BY c.Nombre_Cliente;
END;
--exec sp_buscarCliente 'Los Olivos'
--exec sp_buscarCliente 'Torres';      
--exec sp_buscarCliente 'Luis';        
  
GO
---------------------------------------------------------------------------------------------------------------------------
-- Listar Clientes
CREATE PROCEDURE sp_listarClientes
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        c.Id_Cliente,
        c.Nombre_Cliente,
        c.Apellido_Cliente,
        c.Email_Cliente,
        c.Telefono_Cliente,
        d.Nombre_Distrito AS Distrito,
        c.Estado_Cliente
    FROM Cliente c
    INNER JOIN Distrito d ON c.Id_Distrito = d.Id_Distrito
    ORDER BY c.Nombre_Cliente;
END;
GO
--------------------------------------------------------------------------------------------------
----==============-CARGAR COMBOBOX=============================
-- PROCEDIMIENTO PARA LISTAR ÁREAS
CREATE PROCEDURE sp_listar_areas
AS
BEGIN
    SELECT Id_Area, Nombre_Area FROM Area ORDER BY Nombre_Area;
END;
GO

-- PROCEDIMIENTO PARA LISTAR TURNOS
CREATE PROCEDURE sp_listar_turnos
AS
BEGIN
    SELECT Id_Turno, Nombre_Turno, Hora_Inicio, Hora_Fin FROM Turno ORDER BY Id_Turno;
END;
GO

-- PROCEDIMIENTO PARA LISTAR DISTRITO
CREATE PROCEDURE sp_listar_distrito
AS
BEGIN
    SELECT Id_Distrito,Nombre_Distrito,Codigo_Postal from Distrito ORDER BY Id_Distrito;
END;
GO
/*
=====================================================================================================
*****************************PROCEDIMIENTO ALMACENADO DE CITAS  *************************************
=====================================================================================================
*/
CREATE PROCEDURE sp_registrarCita
    @Fecha_Cita       DATE,
    @Hora_Cita        TIME,
    @Estado_Cita      VARCHAR(20),
    @Observacion_Cita VARCHAR(200),
    @Id_Cliente       INT,
    @Id_Trabajador    INT,
    @Id_Usuario       INT,
    @Id_Servicio      INT,
    @Resultado        INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        INSERT INTO Cita (
            Fecha_Cita,
            Hora_Cita,
            Estado_Cita,
            Observacion_Cita,
            Id_Cliente,
            Id_Trabajador,
            Id_Usuario,
            Id_Servicio,
			Fecha_Registro
        )
        VALUES (
            @Fecha_Cita,
            @Hora_Cita,
            @Estado_Cita,
            @Observacion_Cita,
            @Id_Cliente,
            @Id_Trabajador,
            @Id_Usuario,
            @Id_Servicio,
			GETDATE()
        );

        SET @Resultado = SCOPE_IDENTITY(); -- Devuelve el ID generado
    END TRY
    BEGIN CATCH
        SET @Resultado = -1; -- Error general
    END CATCH
END;
GO
----------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_validarSolapamiento
(
    @Fecha_Cita DATE,
    @Hora_Cita TIME,
    @Id_Trabajador INT,
    @Id_Servicio INT,
    @Resultado INT OUTPUT
)
AS
BEGIN
    SET NOCOUNT ON;

    -- Obtener duración
    DECLARE @Duracion INT;
    SELECT @Duracion = Duracion_Minutos 
    FROM Servicio 
    WHERE Id_Servicio = @Id_Servicio;

    IF @Duracion IS NULL
    BEGIN
        SET @Resultado = -7;
        RETURN;
    END

    -- Calcular hora fin
    DECLARE @HoraFin TIME;
    SET @HoraFin = DATEADD(MINUTE, @Duracion, @Hora_Cita);

    -- Validar solapamiento usando JOIN
    IF EXISTS (
        SELECT 1
        FROM Cita AS C
        INNER JOIN Servicio AS S ON C.Id_Servicio = S.Id_Servicio
        WHERE C.Id_Trabajador = @Id_Trabajador
          AND C.Fecha_Cita = @Fecha_Cita
          AND (
                (C.Hora_Cita <= @Hora_Cita 
                 AND DATEADD(MINUTE, S.Duracion_Minutos, C.Hora_Cita) > @Hora_Cita)
                OR
                (C.Hora_Cita < @HoraFin 
                 AND DATEADD(MINUTE, S.Duracion_Minutos, C.Hora_Cita) >= @HoraFin)
              )
    )
    BEGIN
        SET @Resultado = -6;
        RETURN;
    END

    SET @Resultado = 1;
END
GO
----------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_actualizarCita
    @Id_Cita          INT,
    @Fecha_Cita       DATE,
    @Hora_Cita        TIME,
    @Estado_Cita      VARCHAR(20),
    @Observacion_Cita VARCHAR(200),
    @Id_Cliente       INT,
    @Id_Trabajador    INT,
    @Id_Usuario       INT,
    @Id_Servicio      INT,
    @Resultado        INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        IF EXISTS (SELECT 1 FROM Cita WHERE Id_Cita = @Id_Cita)
        BEGIN
            UPDATE Cita
            SET 
                Fecha_Cita = @Fecha_Cita,
                Hora_Cita = @Hora_Cita,
                Estado_Cita = @Estado_Cita,
                Observacion_Cita = @Observacion_Cita,
                Id_Cliente = @Id_Cliente,
                Id_Trabajador = @Id_Trabajador,
                Id_Usuario = @Id_Usuario,
                Id_Servicio = @Id_Servicio
            WHERE Id_Cita = @Id_Cita;

            SET @Resultado = 1; -- Actualización exitosa
        END
        ELSE
        BEGIN
            SET @Resultado = 0; -- Cita no encontrada
        END
    END TRY
    BEGIN CATCH
        SET @Resultado = -1; -- Error en ejecución
    END CATCH
END;
GO
-----------------------------------------------------------------------------------------------------------
-- =============================================
-- 🔹 Eliminación lógica (actualiza Estado_Cita)
-- =============================================
CREATE PROCEDURE sp_eliminarCita
    @Id_Cita   INT,
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
	  IF EXISTS (SELECT 1 FROM Cita WHERE Id_Cita = @Id_Cita)
    BEGIN
        UPDATE Cita
        SET Estado_Cita = 'Cancelada'
        WHERE Id_Cita = @Id_Cita;

        SET @Resultado = 1; -- OK
    END
    ELSE
    BEGIN
        SET @Resultado = 0; -- No existe
    END
END
GO

/*
-------------------Resetear identity
DBCC CHECKIDENT ('Cita', RESEED, 0);
----delete from Cita 
-----select * from Cita*/
-- ===========================================================
-- 🔹 Eliminación física (DELETE real)---Uso Administrador
-- ===========================================================
CREATE PROCEDURE sp_eliminarCitaFisica
    @Id_Cita INT,
    @RolUsuario VARCHAR(20),
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    IF @RolUsuario <> 'Administrador'
    BEGIN
        SET @Resultado = -1; -- No autorizado
        RETURN;
    END

    IF EXISTS (SELECT 1 FROM Cita WHERE Id_Cita = @Id_Cita)
    BEGIN
        DELETE FROM Cita WHERE Id_Cita = @Id_Cita;
        SET @Resultado = 1;
    END
    ELSE
    BEGIN
        SET @Resultado = 0;
    END
END
GO
---------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_buscarCita
    @Criterio NVARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @Fecha DATE = TRY_CAST(@Criterio AS DATE);

   SELECT 
        c.Id_Cita,
        c.Fecha_Cita,
        c.Hora_Cita,
        c.Estado_Cita,
        c.Observacion_Cita,
        cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente AS Cliente,
        t.Nombre_Trabajador + ' ' + t.Apellido_Trabajador AS Trabajador,
        s.Nombre_Servicio AS Servicio,
        u.Nombre_Usuario AS Usuario,
        a.Id_Area,  
        a.Nombre_Area AS Area,
        c.Id_Servicio
   FROM Cita c
        INNER JOIN Cliente cl ON c.Id_Cliente = cl.Id_Cliente
        INNER JOIN Trabajador t ON c.Id_Trabajador = t.Id_Trabajador
        INNER JOIN Usuario u ON c.Id_Usuario = u.Id_Usuario
        INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
        INNER JOIN Area a ON s.Id_Area = a.Id_Area
    WHERE
        -- Buscar por cliente (nombre o apellido concatenado)
        (cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente LIKE '%' + @Criterio + '%')
        OR (t.Nombre_Trabajador + ' ' + t.Apellido_Trabajador LIKE '%' + @Criterio + '%')
        OR (c.Fecha_Cita = @Fecha)
    ORDER BY c.Fecha_Cita, c.Hora_Cita;
END;
/*
EXEC sp_buscarCita @Criterio = 'Carlos Ramos';
EXEC sp_buscarCita @Criterio = 'Alberto Gongora';
EXEC sp_buscarCita @Criterio = '2025-11-07';
*/
GO
-----------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_listarCitas
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        c.Id_Cita,
        c.Fecha_Cita,
        c.Hora_Cita,
        c.Estado_Cita,
        c.Observacion_Cita,
        cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente AS Cliente,
        t.Nombre_Trabajador +' '+ t.Apellido_Trabajador AS Trabajador,
		s.Nombre_Servicio AS Servicio,
        u.Nombre_Usuario AS Usuario,
		a.Id_Area,  
		a.Nombre_Area AS Area,
		c.Id_Servicio
    FROM Cita c
    INNER JOIN Cliente cl ON c.Id_Cliente = cl.Id_Cliente
    INNER JOIN Trabajador t ON c.Id_Trabajador = t.Id_Trabajador
    INNER JOIN Usuario u ON c.Id_Usuario = u.Id_Usuario
    INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
	INNER JOIN Area a ON s.Id_Area = a.Id_Area 
	--WHERE c.Estado_Cita = 'Pendiente'    
    ORDER BY c.Fecha_Cita, c.Hora_Cita;
END;
GO
-----------------------------------------------------------------------------------------------------------------------------------------------------
/*==============Registrar Area=================*/
CREATE PROCEDURE sp_registrarArea
    @NombreArea VARCHAR(50),
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRY
        IF EXISTS (SELECT 1 FROM Area WHERE Nombre_Area = @NombreArea)
        BEGIN
            SET @Resultado = -1; -- Área ya existe
            RETURN;
        END

        INSERT INTO Area (Nombre_Area)
        VALUES (@NombreArea);

        SET @Resultado = 1; -- Éxito
    END TRY
    BEGIN CATCH
        SET @Resultado = -99; -- Error general
    END CATCH
END
GO
-----------------------------------------------------------------------------------------------------------------------------------------------------
/*==============Actializar Area=================*/
CREATE PROCEDURE sp_actualizarArea
    @IdArea INT,
    @NombreArea VARCHAR(50),
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- Validar duplicados
        IF EXISTS (
            SELECT 1 FROM Area
            WHERE Nombre_Area = @NombreArea
              AND Id_Area <> @IdArea
        )
        BEGIN
            SET @Resultado = -1; -- Área duplicada
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- Actualizar área
        UPDATE Area
        SET Nombre_Area = @NombreArea
        WHERE Id_Area = @IdArea;

        COMMIT TRANSACTION;
        SET @Resultado = 1; -- Éxito

    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SET @Resultado = -99; -- Error genérico
    END CATCH
END
GO
-----------------------------------------------------------------------------------------------------------------------------------------------------
/*==============Registrar Servicios=================*/
CREATE PROCEDURE sp_registrarServicio
    @IdArea INT,
    @NombreServicio VARCHAR(50),
    @PrecioServicio DECIMAL(10,2),
    @Duracion INT,
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- Verificar si el servicio ya existe en esta área
        IF EXISTS (
            SELECT 1
            FROM Servicio
            WHERE Nombre_Servicio = @NombreServicio
              AND Id_Area = @IdArea
        )
        BEGIN
            SET @Resultado = -2; -- Servicio duplicado
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- Insertar servicio
        INSERT INTO Servicio (Nombre_Servicio, Precio_Servicio, Duracion_Minutos, Id_Area)
        VALUES (@NombreServicio, @PrecioServicio, @Duracion, @IdArea);

        COMMIT TRANSACTION;
        SET @Resultado = 1; -- Éxito

    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SET @Resultado = -99; -- Error genérico
    END CATCH
END
GO
-----------------------------------------------------------------------------------------------------------------------------------------------------
/*==============Actializar Area=================*/
CREATE PROCEDURE sp_actualizarServicio
    @IdServicio INT,
    @IdArea INT,
    @NombreServicio VARCHAR(50),
    @PrecioServicio DECIMAL(10,2),
    @Duracion INT,
    @Resultado INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- 1️⃣ Verificar que no exista otro servicio con el mismo nombre en la misma área
        IF EXISTS (
            SELECT 1
            FROM Servicio
            WHERE Nombre_Servicio = @NombreServicio
              AND Id_Area = @IdArea
              AND Id_Servicio <> @IdServicio
        )
        BEGIN
            SET @Resultado = -2; -- Servicio duplicado
            ROLLBACK TRANSACTION;
            RETURN;
        END

        -- 2️⃣ Actualizar servicio
        UPDATE Servicio
        SET Nombre_Servicio = @NombreServicio,
            Precio_Servicio = @PrecioServicio,
            Duracion_Minutos = @Duracion,
            Id_Area = @IdArea
        WHERE Id_Servicio = @IdServicio;

        COMMIT TRANSACTION;
        SET @Resultado = 1; -- Éxito

    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SET @Resultado = -99; -- Error genérico
    END CATCH
END
GO
-----------------------------------------------------------------------------------------------------------------------------------------------------
/*==============BuscarAreaServicio=================*/
CREATE PROCEDURE sp_buscarAreaServicio
    @Criterio NVARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;

    -- Intentar convertir el criterio a entero para buscar por ID
    DECLARE @Id INT = TRY_CAST(@Criterio AS INT);

    SELECT
        a.Id_Area,
        a.Nombre_Area,
        s.Id_Servicio,
        s.Nombre_Servicio,
        s.Precio_Servicio,
        s.Duracion_Minutos
    FROM Area a
    LEFT JOIN Servicio s ON a.Id_Area = s.Id_Area
    WHERE
        (@Id IS NOT NULL AND (a.Id_Area = @Id OR s.Id_Servicio = @Id))
        OR a.Nombre_Area LIKE '%' + @Criterio + '%'
        OR s.Nombre_Servicio LIKE '%' + @Criterio + '%'
    ORDER BY a.Nombre_Area, s.Nombre_Servicio;
END
GO
-- Por nombre
EXEC sp_buscarAreaServicio @Criterio = 'Corte y Peinado';

-- Por ID
EXEC sp_buscarAreaServicio @Criterio = '5';

GO
-----------------------------------------------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_listar_roles
AS
BEGIN
    SELECT Id_Rol, Nombre_Rol, Descripcion_Rol
    FROM Rol
    ORDER BY Id_Rol;
END;
GO

-----------------------------------------------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_listarAreasServicios
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        a.Id_Area,
        a.Nombre_Area,
        s.Id_Servicio,
        s.Nombre_Servicio,
        s.Precio_Servicio,
        s.Duracion_Minutos
    FROM Area a
    INNER JOIN Servicio s
        ON a.Id_Area = s.Id_Area
    ORDER BY a.Nombre_Area, s.Nombre_Servicio;
END
GO
-----------------------------------------------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_listar_servicio
AS
BEGIN
    SELECT Id_Servicio, Nombre_Servicio,Id_Area FROM Servicio ORDER BY Id_Area;
END;
GO
-----------------------------------------------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_listar_servicioArea
    @Id_Area INT
AS
BEGIN
    SELECT Id_Servicio, Nombre_Servicio, Precio_Servicio, Duracion_Minutos
    FROM Servicio
    WHERE Id_Area = @Id_Area
    ORDER BY Id_Servicio;
END;
--exec sp_listar_servicioArea 1
GO
---------------------------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_dashboardMetricas
AS
BEGIN
    SET NOCOUNT ON;

    -- 1️⃣ Total de citas
    DECLARE @TotalCitas INT = (SELECT COUNT(*) FROM Cita);

    -- 2️⃣ Total de citas por estado
    DECLARE @CitasAtendidas INT = (SELECT COUNT(*) FROM Cita WHERE Estado_Cita = 'Atendida');
    DECLARE @CitasPendientes INT = (SELECT COUNT(*) FROM Cita WHERE Estado_Cita = 'Pendiente');
    DECLARE @CitasCanceladas INT = (SELECT COUNT(*) FROM Cita WHERE Estado_Cita = 'Cancelada');

     -- 3️⃣ Citas del día (solo fecha actual, sin importar la hora)
    DECLARE @CitasHoy INT = (
        SELECT COUNT(*)
        FROM Cita
        WHERE CONVERT(date, Fecha_Cita) = CONVERT(date, GETDATE())
    );
	
	-- 3️⃣ Total de clientes activos
    DECLARE @ClientesActivos INT = (SELECT COUNT(*) FROM Cliente WHERE Estado_Cliente = 1);

    -- 4️⃣ Total de trabajadores activos
    DECLARE @TrabajadoresActivos INT = (SELECT COUNT(*) FROM Trabajador WHERE Estado_Trabajador = 1);

    -- 5️⃣ Servicio más solicitado
    DECLARE @ServicioMasSolicitado VARCHAR(100);
    SELECT TOP 1 @ServicioMasSolicitado = s.Nombre_Servicio
    FROM Cita c
    INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
    GROUP BY s.Nombre_Servicio
    ORDER BY COUNT(*) DESC;

    -- 6️⃣ Área más demandada
    DECLARE @AreaMasDemandada VARCHAR(100);
    SELECT TOP 1 @AreaMasDemandada = a.Nombre_Area
    FROM Cita c
    INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
    INNER JOIN Area a ON s.Id_Area = a.Id_Area
    GROUP BY a.Nombre_Area
    ORDER BY COUNT(*) DESC;

    -- 7️⃣ Productividad promedio por trabajador
    DECLARE @PromedioProductividad DECIMAL(10,2);
    SELECT @PromedioProductividad = AVG(TotalCitas)
    FROM (
        SELECT t.Id_Trabajador, COUNT(c.Id_Cita) AS TotalCitas
        FROM Trabajador t
        LEFT JOIN Cita c ON t.Id_Trabajador = c.Id_Trabajador
        GROUP BY t.Id_Trabajador
    ) AS Sub;

    -- 🔹 Resultado consolidado
    SELECT
        @TotalCitas AS Total_Citas,
		@CitasHoy AS Citas_Hoy,  -- 👈 nuevo campo
        @CitasAtendidas AS Citas_Atendidas,
        @CitasPendientes AS Citas_Pendientes,
        @CitasCanceladas AS Citas_Canceladas,
        @ClientesActivos AS Clientes_Activos,
        @TrabajadoresActivos AS Trabajadores_Activos,
        @ServicioMasSolicitado AS Servicio_Mas_Solicitado,
        @AreaMasDemandada AS Area_Mas_Demandada,
        @PromedioProductividad AS Promedio_Citas_Por_Trabajador;
END;
GO
-----------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_dashboardCitasPorDiaSemana
    @FechaInicio DATE,
    @FechaFin DATE,
    @Id_Servicio INT = NULL,
    @Id_Trabajador INT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        DATENAME(WEEKDAY, c.Fecha_Cita) AS DiaSemana,
        CONVERT(date, c.Fecha_Cita) AS Fecha,
        COUNT(*) AS TotalCitas,
		SUM(CASE WHEN c.Estado_Cita = 'Cancelada' THEN 1 ELSE 0 END) AS CitasCanceladas,
        SUM(CASE WHEN c.Estado_Cita = 'Completada' THEN 1 ELSE 0 END) AS CitasCompletadas 
    FROM Cita c
    INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
    INNER JOIN Trabajador t ON c.Id_Trabajador = t.Id_Trabajador
    WHERE 
        c.Fecha_Cita BETWEEN @FechaInicio AND @FechaFin
        AND (@Id_Servicio IS NULL OR c.Id_Servicio = @Id_Servicio)
        AND (@Id_Trabajador IS NULL OR c.Id_Trabajador = @Id_Trabajador)
    GROUP BY DATENAME(WEEKDAY, c.Fecha_Cita), DATEPART(WEEKDAY, c.Fecha_Cita), CONVERT(date, c.Fecha_Cita)
    ORDER BY DATEPART(WEEKDAY, c.Fecha_Cita);
END;
GO


EXEC sp_dashboardCitasPorDiaSemana 
    @FechaInicio = '2025-11-01',
    @FechaFin = '2025-11-11',
    @Id_Servicio = NULL,
    @Id_Trabajador = NULL;

GO
----------------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_horasDisponiblesTrabajador
(
    @Id_Trabajador INT,
    @Fecha DATE,
    @IntervaloMin INT = 30   -- intervalos en minutos (por defecto 30)
)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @HoraInicio TIME, @HoraFin TIME;

    -- Obtener turno del trabajador
    SELECT 
        @HoraInicio = t.Hora_Inicio,
        @HoraFin   = t.Hora_Fin
    FROM Trabajador tr
    INNER JOIN Turno t ON tr.Id_Turno = t.Id_Turno
    WHERE tr.Id_Trabajador = @Id_Trabajador;

    IF @HoraInicio IS NULL
    BEGIN
        RAISERROR('No se encontró turno para el trabajador.', 16, 1);
        RETURN;
    END

    -- Tabla temporal para guardar intervalos
    DECLARE @Slots TABLE (Hora TIME PRIMARY KEY);

    DECLARE @Current TIME = @HoraInicio;
    DECLARE @LastStart TIME;

    -- Calcular último inicio válido (evitar empezar en la hora exacta de fin)
    SET @LastStart = CONVERT(TIME, DATEADD(MINUTE, -@IntervaloMin, @HoraFin));

    -- Si LastStart < HoraInicio, entonces permitimos al menos el inicio
    IF @LastStart < @HoraInicio
        SET @LastStart = @HoraInicio;

    -- Llenar tabla de slots
    WHILE @Current <= @LastStart
    BEGIN
        INSERT INTO @Slots(Hora) VALUES (@Current);
        SET @Current = CONVERT(TIME, DATEADD(MINUTE, @IntervaloMin, @Current));
    END

    -- Obtener citas del día para el trabajador con sus horas de inicio y fin
    DECLARE @Citas TABLE (Inicio TIME, Fin TIME);

    INSERT INTO @Citas (Inicio, Fin)
    SELECT 
        c.Hora_Cita AS Inicio,
        CONVERT(TIME, DATEADD(MINUTE, s.Duracion_Minutos, c.Hora_Cita)) AS Fin
    FROM Cita c
    INNER JOIN Servicio s ON s.Id_Servicio = c.Id_Servicio
    WHERE c.Id_Trabajador = @Id_Trabajador
      AND c.Fecha_Cita = @Fecha;

    -- Resultado: cada slot y si está libre u ocupado
    SELECT 
        FORMAT(s.Hora, 'HH:mm') AS Hora,
        CASE 
            WHEN EXISTS (
                SELECT 1
                FROM @Citas c
                WHERE s.Hora >= c.Inicio
                  AND s.Hora < c.Fin
            ) THEN 'Ocupado'
            ELSE 'Libre'
        END AS Estado
    FROM @Slots s
    ORDER BY s.Hora;
END;
GO
-----------------------------------------------------------------------------------------------------------------------

/*
REPORTE EN BASE A LO SOLICITADO  
*/
-----------------------------------------------------------------------------------------------------------------------
---A) Reporte de citas registradas por turno
CREATE PROCEDURE sp_rptCitasPorTurno
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        c.Id_Cita,
        c.Fecha_Cita,
        c.Hora_Cita,
        c.Estado_Cita,
        cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente AS Cliente,
        t.Nombre_Trabajador + ' ' + t.Apellido_Trabajador AS Trabajador,
        s.Nombre_Servicio AS Servicio,
        u.Nombre_Usuario AS Usuario,
        CASE
            WHEN c.Hora_Cita >= '08:00:00' AND c.Hora_Cita <= '11:59:59' THEN 'Mañana'
            WHEN c.Hora_Cita >= '12:00:00' AND c.Hora_Cita <= '18:59:59' THEN 'Tarde'
            WHEN c.Hora_Cita >= '19:00:00' AND c.Hora_Cita <= '23:00:00' THEN 'Noche'
            ELSE 'Fuera de Turno'
        END AS Turno
    FROM Cita AS c
         INNER JOIN Cliente AS cl ON c.Id_Cliente = cl.Id_Cliente
         INNER JOIN Trabajador AS t ON c.Id_Trabajador = t.Id_Trabajador
         INNER JOIN Usuario AS u ON c.Id_Usuario = u.Id_Usuario
         INNER JOIN Servicio AS s ON c.Id_Servicio = s.Id_Servicio
    ORDER BY c.Fecha_Cita, Turno, c.Hora_Cita;
END
GO
-----------------------------------------------------------------------------------------------------------------------

CREATE PROCEDURE sp_rptCitasPorTurnoConConteo
AS
SELECT 
    CASE
        WHEN Hora_Cita >= '08:00:00' AND Hora_Cita <= '11:59:59' THEN 'Mañana'
        WHEN Hora_Cita >= '12:00:00' AND Hora_Cita <= '18:59:59' THEN 'Tarde'
        WHEN Hora_Cita >= '19:00:00' AND Hora_Cita <= '23:00:00' THEN 'Noche'
        ELSE 'Fuera de Turno'
    END AS Turno,
    COUNT(*) AS CantidadCitas
FROM Cita
GROUP BY 
    CASE
        WHEN Hora_Cita >= '08:00:00' AND Hora_Cita <= '11:59:59' THEN 'Mañana'
        WHEN Hora_Cita >= '12:00:00' AND Hora_Cita <= '18:59:59' THEN 'Tarde'
        WHEN Hora_Cita >= '19:00:00' AND Hora_Cita <= '23:00:00' THEN 'Noche'
        ELSE 'Fuera de Turno'
    END
ORDER BY Turno;
GO
-----------------------------------------------------------------------------------------------------------------------


CREATE PROCEDURE sp_rptCitasPorTurnoDetallado
AS
BEGIN
    SET NOCOUNT ON;

    -- CTE para clasificar por turno
    WITH CitasConTurno AS (
        SELECT 
            c.Id_Cita,
            c.Fecha_Cita,
            c.Hora_Cita,
            c.Estado_Cita,
            cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente AS Cliente,
            t.Nombre_Trabajador + ' ' + t.Apellido_Trabajador AS Trabajador,
            s.Nombre_Servicio AS Servicio,
            u.Nombre_Usuario AS Usuario,
            CASE 
                WHEN c.Hora_Cita BETWEEN '08:00:00' AND '11:59:59' THEN 'Mañana'
                WHEN c.Hora_Cita BETWEEN '12:00:00' AND '18:59:59' THEN 'Tarde'
                WHEN c.Hora_Cita BETWEEN '19:00:00' AND '23:00:00' THEN 'Noche'
                ELSE 'Fuera de Turno'
            END AS Turno
        FROM Cita AS c
        INNER JOIN Cliente AS cl ON c.Id_Cliente = cl.Id_Cliente
        INNER JOIN Trabajador AS t ON c.Id_Trabajador = t.Id_Trabajador
        INNER JOIN Usuario AS u ON c.Id_Usuario = u.Id_Usuario
        INNER JOIN Servicio AS s ON c.Id_Servicio = s.Id_Servicio
    )

    -- Resumen por turno con detalle
    SELECT 
        Turno,
        COUNT(*) AS CantidadCitas,
        STRING_AGG(Cliente + ' (' + Servicio + ')', ', ') AS ClientesYServicios
    FROM CitasConTurno
    GROUP BY Turno
    ORDER BY 
        CASE Turno
            WHEN 'Mañana' THEN 1
            WHEN 'Tarde' THEN 2
            WHEN 'Noche' THEN 3
            ELSE 4
        END;
END
GO
-----------------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_rptServicios
(
    @FechaInicio DATE,
    @FechaFin DATE
)
AS
BEGIN
    SELECT 
        c.Id_Cita,
        c.Fecha_Cita,
        c.Hora_Cita,
        s.Nombre_Servicio,
        s.Precio_Servicio,
        cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente AS Cliente,
        tr.Nombre_Trabajador + ' ' + tr.Apellido_Trabajador AS Trabajador,
        a.Nombre_Area AS Area
    FROM Cita c
    INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
    INNER JOIN Cliente cl ON c.Id_Cliente = cl.Id_Cliente
    INNER JOIN Trabajador tr ON c.Id_Trabajador = tr.Id_Trabajador
    INNER JOIN Area a ON a.Id_Area = s.Id_Area
    WHERE c.Fecha_Cita BETWEEN @FechaInicio AND @FechaFin
    ORDER BY c.Fecha_Cita, c.Hora_Cita;
END
GO
exec sp_rptServicios '2025-11-01','2025-11-17'
select * from Cita
GO

-----------------------------------------------------------------------------------------------------------------------
----B)Total de ingresos por rango
CREATE PROCEDURE sp_rptIngresos
(
    @FechaInicio DATE,
    @FechaFin DATE
)
AS
BEGIN
      -- DETALLE
    SELECT 
        c.Id_Cita,
        c.Fecha_Cita,
        c.Hora_Cita,
        cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente AS Cliente,
        s.Nombre_Servicio AS Servicio,
        s.Precio_Servicio AS Precio
    FROM Cita c
    INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
    INNER JOIN Cliente cl ON c.Id_Cliente = cl.Id_Cliente
    WHERE c.Estado_Cita = 'Atendida'
      AND c.Fecha_Cita BETWEEN @FechaInicio AND @FechaFin;
END
GO
exec sp_rptIngresos '2025-11-01','2025-11-17'
GO
-----------------------------------------------------------------------------------------------------------------------
----C)Indicadores completos
CREATE PROCEDURE sp_rptIndicadores
AS
BEGIN
    SELECT
        (SELECT COUNT(*) FROM Cita) AS TotalCitas,
        (SELECT COUNT(*) FROM Cita WHERE Estado_Cita = 'Atendida') AS CitasAtendidas,
        (SELECT COUNT(*) FROM Cita WHERE Estado_Cita = 'Pendiente') AS CitasPendientes,
        (SELECT COUNT(*) FROM Cita WHERE Estado_Cita = 'Cancelada') AS CitasCanceladas,
        (SELECT COUNT(*) FROM Trabajador WHERE Estado_Trabajador = 1) AS TrabajadoresActivos,
        (SELECT COUNT(*) FROM Cliente WHERE Estado_Cliente = 1) AS ClientesActivos;
END
GO
-----------------------------------------------------------------------------------------------------------------------
----D)Máximo / Mínimo / Promedio de precios
CREATE PROCEDURE sp_rptPrecioServicioStats
AS
BEGIN
    SELECT 
        MAX(Precio_Servicio) AS PrecioMaximo,
        MIN(Precio_Servicio) AS PrecioMinimo,
        AVG(Precio_Servicio) AS PrecioPromedio
    FROM Servicio;
END
GO
-----------------------------------------------------------------------------------------------------------------------
----F1.1)Citas Canceladas (Eliminadas logica)
CREATE PROCEDURE sp_rptCitasCanceladas
AS
BEGIN
    SET NOCOUNT ON;
    SELECT   c.Id_Cita,
             c.Fecha_Cita,
             c.Hora_Cita,
             c.Estado_Cita,
             c.Observacion_Cita,
             cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente AS Cliente,
             t.Nombre_Trabajador + ' ' + t.Apellido_Trabajador AS Trabajador,
             s.Nombre_Servicio AS Servicio,
             u.Nombre_Usuario AS Usuario
    FROM     Cita AS c
             INNER JOIN
             Cliente AS cl
             ON c.Id_Cliente = cl.Id_Cliente
             INNER JOIN
             Trabajador AS t
             ON c.Id_Trabajador = t.Id_Trabajador
             INNER JOIN
             Usuario AS u
             ON c.Id_Usuario = u.Id_Usuario
             INNER JOIN
             Servicio AS s
             ON c.Id_Servicio = s.Id_Servicio
             INNER JOIN
             Area AS a
             ON s.Id_Area = a.Id_Area
    WHERE    c.Estado_Cita = 'Cancelado'
    ORDER BY c.Fecha_Cita, c.Hora_Cita;
END
GO
-----------------------------------------------------------------------------------------------------------------------
----H)Ingresos por trabajador
CREATE PROCEDURE sp_ingresosPorTrabajador
(
    @FechaInicio DATE,
    @FechaFin DATE
)
AS
BEGIN
    SELECT 
        tr.Nombre_Trabajador + ' ' + tr.Apellido_Trabajador AS Trabajador,
        SUM(s.Precio_Servicio) AS TotalIngresos
    FROM Cita c
    INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
    INNER JOIN Trabajador tr ON tr.Id_Trabajador = c.Id_Trabajador
    WHERE c.Fecha_Cita BETWEEN @FechaInicio AND @FechaFin
      AND c.Estado_Cita = 'Atendida'
    GROUP BY tr.Nombre_Trabajador, tr.Apellido_Trabajador;
END
GO
-----------------------------------------------------------------------------------------------------------------------
----I)Horario de cita del Trabajador
CREATE PROCEDURE sp_rptHorarioCitaTrabajadorConTurno
    @Id_Trabajador INT,
    @Fecha DATE,
    @IntervalMinutes INT = 30
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @HoraInicio AS TIME, @HoraFin AS TIME;

    -- Obtener horario del trabajador
    SELECT @HoraInicio = t.Hora_Inicio,
           @HoraFin = t.Hora_Fin
    FROM Trabajador AS tr
    INNER JOIN Turno AS t ON tr.Id_Turno = t.Id_Turno
    WHERE tr.Id_Trabajador = @Id_Trabajador;

    -- Si no tiene turno, devolver fila con mensaje
    IF (@HoraInicio IS NULL OR @HoraFin IS NULL)
    BEGIN
        SELECT 
            CAST(NULL AS TIME) AS SlotHora,
            CAST(NULL AS DATETIME) AS SlotStartDateTime,
            CAST(NULL AS DATETIME) AS SlotEndDateTime,
            'Turno no configurado para este trabajador' AS Estado,
            CAST(NULL AS INT) AS Id_Cita,
            CAST(NULL AS NVARCHAR(100)) AS Nombre_Servicio,
            CAST(NULL AS NVARCHAR(200)) AS Cliente,
            CAST(NULL AS NVARCHAR(MAX)) AS Observacion_Cita,
            CAST(NULL AS NVARCHAR(20)) AS Turno
        RETURN;
    END

    DECLARE @StartDT AS DATETIME = DATEADD(SECOND, DATEDIFF(SECOND, 0, CAST(@HoraInicio AS DATETIME)), CAST(@Fecha AS DATETIME));
    DECLARE @EndDT AS DATETIME = DATEADD(SECOND, DATEDIFF(SECOND, 0, CAST(@HoraFin AS DATETIME)), CAST(@Fecha AS DATETIME));

    IF (@EndDT < @StartDT)
        SET @EndDT = DATEADD(DAY, 1, @EndDT);

    DECLARE @TotalMinutes AS INT = DATEDIFF(MINUTE, @StartDT, @EndDT);
    IF (@TotalMinutes < 0) SET @TotalMinutes = 0;

    DECLARE @Slots AS INT = CASE WHEN @IntervalMinutes > 0 THEN CEILING(1.0 * @TotalMinutes / @IntervalMinutes) ELSE 0 END;

    DECLARE @Nums TABLE (n INT PRIMARY KEY);
    IF (@Slots >= 0)
    BEGIN
        INSERT INTO @Nums(n)
        SELECT TOP(@Slots + 1) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) - 1
        FROM sys.all_objects;
    END

    -- Obtener todas las citas del día para este trabajador (excluyendo Canceladas o Atendidas)
    DECLARE @Citas TABLE (
        Id_Cita INT,
        Id_Servicio INT,
        Id_Cliente INT,
        Fecha_Cita DATE,
        Hora_Cita TIME,
        Duracion_Minutos INT,
        Observacion_Cita NVARCHAR(MAX)
    );

    INSERT INTO @Citas
    SELECT c.Id_Cita, c.Id_Servicio, c.Id_Cliente, c.Fecha_Cita, c.Hora_Cita, s.Duracion_Minutos, c.Observacion_Cita
    FROM Cita c
    INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
    WHERE c.Id_Trabajador = @Id_Trabajador
      AND c.Fecha_Cita = @Fecha
      AND c.Estado_Cita NOT IN ('Cancelado', 'Atendido');

    -- Generar slots con estado
    SELECT
        CONVERT(TIME, DATEADD(MINUTE, n * @IntervalMinutes, @StartDT)) AS SlotHora,
        DATEADD(MINUTE, n * @IntervalMinutes, @StartDT) AS SlotStartDateTime,
        DATEADD(MINUTE, (n + 1) * @IntervalMinutes, @StartDT) AS SlotEndDateTime,
        CASE 
            WHEN EXISTS (
                SELECT 1 
                FROM @Citas c
                WHERE DATEADD(SECOND, DATEDIFF(SECOND, 0, c.Hora_Cita), CAST(c.Fecha_Cita AS DATETIME)) 
                      < DATEADD(MINUTE, (n.n + 1) * @IntervalMinutes, @StartDT)
                  AND DATEADD(MINUTE, c.Duracion_Minutos, DATEADD(SECOND, DATEDIFF(SECOND, 0, c.Hora_Cita), CAST(c.Fecha_Cita AS DATETIME))) 
                      > DATEADD(MINUTE, n.n * @IntervalMinutes, @StartDT)
            )
            THEN 'Ocupado'
            ELSE 'Libre'
        END AS Estado,
        c.Id_Cita,
        srv.Nombre_Servicio,
        cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente AS Cliente,
        c.Observacion_Cita,
        CASE 
            WHEN CONVERT(TIME, DATEADD(MINUTE, n * @IntervalMinutes, @StartDT)) BETWEEN '08:00:00' AND '11:59:59' THEN 'Mañana'
            WHEN CONVERT(TIME, DATEADD(MINUTE, n * @IntervalMinutes, @StartDT)) BETWEEN '12:00:00' AND '18:59:59' THEN 'Tarde'
            WHEN CONVERT(TIME, DATEADD(MINUTE, n * @IntervalMinutes, @StartDT)) BETWEEN '19:00:00' AND '23:00:00' THEN 'Noche'
            ELSE 'Fuera de Turno'
        END AS Turno
    FROM @Nums n
    LEFT JOIN @Citas c ON DATEADD(SECOND, DATEDIFF(SECOND, 0, c.Hora_Cita), CAST(c.Fecha_Cita AS DATETIME)) 
                          < DATEADD(MINUTE, (n.n + 1) * @IntervalMinutes, @StartDT)
                      AND DATEADD(MINUTE, c.Duracion_Minutos, DATEADD(SECOND, DATEDIFF(SECOND, 0, c.Hora_Cita), CAST(c.Fecha_Cita AS DATETIME))) 
                          > DATEADD(MINUTE, n.n * @IntervalMinutes, @StartDT)
    LEFT JOIN Servicio srv ON c.Id_Servicio = srv.Id_Servicio
    LEFT JOIN Cliente cl ON c.Id_Cliente = cl.Id_Cliente
    ORDER BY SlotStartDateTime;
END

GO
EXEC sp_rptHorarioCitaTrabajadorConTurno 1, '2025-11-29'

GO
select * from cita WHERE Id_Trabajador=4
go
CREATE PROCEDURE sp_rptDistribucionCitasSemana
AS
BEGIN
    -- Forzar idioma español en la sesión
    SET LANGUAGE Spanish;

    SELECT 
        DATENAME(WEEKDAY, Fecha_Cita) AS DiaSemana,
        COUNT(*) AS TotalCitas
    FROM Cita
    GROUP BY DATENAME(WEEKDAY, Fecha_Cita)
    ORDER BY 
        CASE DATENAME(WEEKDAY, Fecha_Cita)
            WHEN 'lunes' THEN 1
            WHEN 'martes' THEN 2
            WHEN 'miércoles' THEN 3
            WHEN 'jueves' THEN 4
            WHEN 'viernes' THEN 5
            WHEN 'sábado' THEN 6
            WHEN 'domingo' THEN 7
        END;

END
GO

CREATE PROCEDURE sp_rptDistribucionCitasSemanaPorFecha
    @FechaInicio DATE,
    @FechaFin   DATE
AS
BEGIN
    SELECT 
        CASE DATEPART(WEEKDAY, Fecha_Cita)
            WHEN 1 THEN 'Domingo'
            WHEN 2 THEN 'Lunes'
            WHEN 3 THEN 'Martes'
            WHEN 4 THEN 'Miércoles'
            WHEN 5 THEN 'Jueves'
            WHEN 6 THEN 'Viernes'
            WHEN 7 THEN 'Sábado'
        END AS DiaSemana,
        COUNT(*) AS TotalCitas
    FROM Cita
    WHERE Fecha_Cita BETWEEN @FechaInicio AND @FechaFin
    GROUP BY DATEPART(WEEKDAY, Fecha_Cita)
    ORDER BY 
        CASE DATEPART(WEEKDAY, Fecha_Cita)
            WHEN 2 THEN 1   -- Lunes
            WHEN 3 THEN 2   -- Martes
            WHEN 4 THEN 3   -- Miércoles
            WHEN 5 THEN 4   -- Jueves
            WHEN 6 THEN 5   -- Viernes
            WHEN 7 THEN 6   -- Sábado
            WHEN 1 THEN 7   -- Domingo al final
        END;
END


EXEC sp_rptDistribucionCitasSemanaPorFecha '2025-11-01','2025-11-22'
GO

CREATE PROCEDURE sp_rptDistribucionCitasSemanaMes
    @FechaInicio DATE,
    @FechaFin DATE
AS
BEGIN
    SET NOCOUNT ON;
    SET LANGUAGE Spanish;
    SET DATEFIRST 1;   -- <-- Fuerza que Lunes = 1

    SELECT 
        DATENAME(WEEKDAY, c.Fecha_Cita) AS DiaSemana,
        COUNT(*) AS TotalCitas,
        DATEPART(WEEKDAY, c.Fecha_Cita) AS OrdenDia    -- ahora sí 1 = lunes
    FROM 
        Cita c
    WHERE 
        c.Fecha_Cita BETWEEN @FechaInicio AND @FechaFin
        AND c.Estado_Cita <> 'CANCELADA'
    GROUP BY 
        DATENAME(WEEKDAY, c.Fecha_Cita),
        DATEPART(WEEKDAY, c.Fecha_Cita)
    ORDER BY 
        OrdenDia;
END

EXEC sp_rptDistribucionCitasSemanaMes '2025-11-01','2025-11-30'






--------------------------------------------------------------------------------------------------------------------------
/*
======================================================================================================
/*PRUEBA DE REPORTES */
======================================================================================================
-------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_dashboardAgendaCitas
    @Modo VARCHAR(10) = 'DIA',     -- 'DIA', 'SEMANA' o 'MES'
    @FechaBase DATE = NULL         -- fecha de referencia, por defecto hoy
AS
BEGIN
    SET NOCOUNT ON;

    IF @FechaBase IS NULL
        SET @FechaBase = CAST(GETDATE() AS DATE);

    SELECT 
        c.Id_Cita AS [Código],
        FORMAT(c.Fecha_Cita, 'yyyy-MM-dd') AS [Fecha],
        CONVERT(varchar(5), c.Hora_Cita, 108) AS [Hora],
        c.Estado_Cita AS [Estado],
        cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente AS [Cliente],
        t.Nombre_Trabajador + ' ' + t.Apellido_Trabajador AS [Trabajador],
        s.Nombre_Servicio AS [Servicio],
        a.Nombre_Area AS [Área],
        c.Observacion_Cita AS [Observación],
        u.Nombre_Usuario AS [Registrado por]
    FROM Cita c
    INNER JOIN Cliente cl ON c.Id_Cliente = cl.Id_Cliente
    INNER JOIN Trabajador t ON c.Id_Trabajador = t.Id_Trabajador
    INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
    INNER JOIN Area a ON s.Id_Area = a.Id_Area
    INNER JOIN Usuario u ON c.Id_Usuario = u.Id_Usuario
    WHERE 
        (@Modo = 'DIA' AND c.Fecha_Cita = @FechaBase)
        OR (@Modo = 'SEMANA' AND c.Fecha_Cita BETWEEN DATEADD(DAY, -DATEPART(WEEKDAY, @FechaBase)+1, @FechaBase) 
                                                   AND DATEADD(DAY, 7 - DATEPART(WEEKDAY, @FechaBase), @FechaBase))
        OR (@Modo = 'MES' AND YEAR(c.Fecha_Cita) = YEAR(@FechaBase) AND MONTH(c.Fecha_Cita) = MONTH(@FechaBase))
    ORDER BY c.Fecha_Cita, c.Hora_Cita;
END;
GO

EXEC sp_dashboardAgendaCitas @Modo = 'DIA', @FechaBase = '2025-11-07';
EXEC sp_dashboardAgendaCitas @Modo = 'SEMANA';

EXEC sp_dashboardAgendaCitas @Modo = 'MES';

select * from Cita
GO
------------------------------------------------------------------------------------------------
CREATE PROCEDURE [sp_dashboardResumenCitas]
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        Estado_Cita,
        COUNT(*) AS Total
    FROM Cita
    GROUP BY Estado_Cita;

    -- También puedes devolver totales globales:
    SELECT 
        COUNT(*) AS Total_Citas,
        SUM(CASE WHEN Estado_Cita = 'Pendiente' THEN 1 ELSE 0 END) AS Pendientes,
        SUM(CASE WHEN Estado_Cita = 'Atendida' THEN 1 ELSE 0 END) AS Atendidas,
        SUM(CASE WHEN Estado_Cita = 'Cancelada' THEN 1 ELSE 0 END) AS Canceladas
    FROM Cita;
END;
GO
------------------------------------------------------------------------------------------------------
CREATE PROCEDURE sp_dashboardProximasCitas
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
        c.Id_Cita,
        c.Fecha_Cita,
        c.Hora_Cita,
        cl.Nombre_Cliente + ' ' + cl.Apellido_Cliente AS Cliente,
        s.Nombre_Servicio AS Servicio,
        t.Nombre_Trabajador + ' ' + t.Apellido_Trabajador AS Trabajador,
        c.Estado_Cita
    FROM Cita c
    INNER JOIN Cliente cl ON c.Id_Cliente = cl.Id_Cliente
    INNER JOIN Servicio s ON c.Id_Servicio = s.Id_Servicio
    INNER JOIN Trabajador t ON c.Id_Trabajador = t.Id_Trabajador
    WHERE c.Fecha_Cita >= CONVERT(date, GETDATE()) 
    ORDER BY c.Fecha_Cita, c.Hora_Cita;
END;
*/