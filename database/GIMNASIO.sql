-- ============================================================
-- Sistema Web Gimnasio - Grupo 1
-- Script convertido de Oracle 11g a sintaxis MySQL
-- Base: modelo de datos de referencia (Práctica original)
-- ============================================================

DROP DATABASE IF EXISTS gimnasio_db;
CREATE DATABASE gimnasio_db;
USE gimnasio_db;

-- Limpieza previa (orden inverso a las dependencias, por las FK)
DROP TABLE IF EXISTS historial_curso;
DROP TABLE IF EXISTS rutinas;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS instructores;
DROP TABLE IF EXISTS cursos;
DROP TABLE IF EXISTS maquinas;

-- ============================================================
-- b. Creación de tablas
-- ============================================================

CREATE TABLE cliente (
   cedula INT NOT NULL,
   nombre VARCHAR(30) NOT NULL,
   apellido_1 VARCHAR(30) NOT NULL,
   apellido_2 VARCHAR(30) NOT NULL,
   direccion VARCHAR(50),
   e_mail VARCHAR(30),
   fecha_inscripcion DATE NOT NULL,
   CONSTRAINT pkcliente PRIMARY KEY (cedula)
);

CREATE TABLE instructores (
    cod_instructor INT NOT NULL,
    nombre VARCHAR(30) NOT NULL,
    apellido_1 VARCHAR(30) NOT NULL,
    apellido_2 VARCHAR(30) NOT NULL,
    direccion VARCHAR(50),
    e_mail VARCHAR(30),
    tel_cel INT NOT NULL,
    tel_habitacion INT NOT NULL,
    fecha_contratacion DATE NOT NULL,
    CONSTRAINT pkinstructores PRIMARY KEY (cod_instructor)
);

CREATE TABLE cursos (
    id_curso INT NOT NULL,
    descripcion VARCHAR(50) NOT NULL,
    CONSTRAINT pkcursos PRIMARY KEY (id_curso)
);

CREATE TABLE maquinas (
    id_maquina INT NOT NULL,
    descripcion VARCHAR(50) NOT NULL,
    CONSTRAINT pkmaquinas PRIMARY KEY (id_maquina)
);

CREATE TABLE rutinas (
    id_rutina INT NOT NULL,
    cliente INT NOT NULL,
    instructor INT NOT NULL,
    maquina INT NOT NULL,
    fecha DATE NOT NULL,
    horas INT NOT NULL,
    CONSTRAINT pkrutinas PRIMARY KEY (id_rutina)
);

CREATE TABLE historial_curso (
    id_historial INT NOT NULL,
    cliente INT NOT NULL,
    instructor INT NOT NULL,
    curso INT NOT NULL,
    fecha DATE NOT NULL,
    horas INT NOT NULL,
    CONSTRAINT pkhistorial_curso PRIMARY KEY (id_historial),
    CONSTRAINT fk1historial_curso FOREIGN KEY (cliente) REFERENCES cliente (cedula),
    CONSTRAINT fk2historial_curso FOREIGN KEY (instructor) REFERENCES instructores (cod_instructor),
    CONSTRAINT fk3historial_curso FOREIGN KEY (curso) REFERENCES cursos (id_curso)
);

-- ============================================================
-- c. Agregar columnas celular y tel_habitacion a cliente
-- ============================================================
-- Nota: MySQL no permite ADD ... NOT NULL sobre una tabla con filas
-- sin un valor por defecto. Se agrega con DEFAULT 0 para permitirlo;
-- podés quitar el DEFAULT después si querés forzarlo en los inserts.
ALTER TABLE cliente ADD COLUMN celular INT NOT NULL DEFAULT 0;
ALTER TABLE cliente ADD COLUMN tel_habitacion INT NOT NULL DEFAULT 0;

-- ============================================================
-- d. Agregar columna estado a maquinas
-- ============================================================
ALTER TABLE maquinas ADD COLUMN estado VARCHAR(15);

-- ============================================================
-- e. Valores por defecto para email y dirección
-- ============================================================
ALTER TABLE cliente ALTER COLUMN direccion SET DEFAULT 'n/a';
ALTER TABLE instructores ALTER COLUMN direccion SET DEFAULT 'n/a';
ALTER TABLE cliente ALTER COLUMN e_mail SET DEFAULT '*@*.com';
ALTER TABLE instructores ALTER COLUMN e_mail SET DEFAULT '*@*.com';

-- ============================================================
-- f. Llaves foráneas en rutinas
-- ============================================================
ALTER TABLE rutinas ADD CONSTRAINT fkrutina_cliente FOREIGN KEY (cliente) REFERENCES cliente (cedula);
ALTER TABLE rutinas ADD CONSTRAINT fk2rutina_instructor FOREIGN KEY (instructor) REFERENCES instructores (cod_instructor);
ALTER TABLE rutinas ADD CONSTRAINT fk3rutina_maquina FOREIGN KEY (maquina) REFERENCES maquinas (id_maquina);

-- ============================================================
-- g. Datos de prueba
-- Fechas convertidas de DDMMYYYY (Oracle) a YYYY-MM-DD (MySQL)
-- ============================================================

-- Tabla cliente
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, fecha_inscripcion, celular, tel_habitacion) VALUES (200,'maria','ruiz','ruiz','barva','1995-01-01',11111111,22222222);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, e_mail, fecha_inscripcion, celular, tel_habitacion) VALUES (201,'juan','paz','arias','alajuela','juan@hotmail.com','1995-01-01',22222222,11111111);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, fecha_inscripcion, celular, tel_habitacion) VALUES (202,'pedro','perez','perez','1998-04-20',33333333,33333335);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, e_mail, fecha_inscripcion, celular, tel_habitacion) VALUES (203,'jose','castro','ruiz','santo domingo','jruiz@gmail.com','1998-06-20',44444444,55555555);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, e_mail, fecha_inscripcion, celular, tel_habitacion) VALUES (204,'martha','diaz','ruiz','pavas','mdiaz@yahoo.es','2000-01-02',22222229,33333333);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, e_mail, fecha_inscripcion, celular, tel_habitacion) VALUES (205,'xiomara','diaz','diaz','xdiaz@hotmail.com','2000-02-03',55555555,11111119);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, fecha_inscripcion, celular, tel_habitacion) VALUES (206,'pablo','arias','arias','san jose','2001-04-20',55555556,11111112);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, e_mail, fecha_inscripcion, celular, tel_habitacion) VALUES (207,'ana','arias','arias','san pedro','arias@gmail.com','2001-04-25',55555556,11111115);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, fecha_inscripcion, celular, tel_habitacion) VALUES (208,'carmen','paz','arias','san jose','2002-04-20',55555556,11111115);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, fecha_inscripcion, celular, tel_habitacion) VALUES (209,'miguel','orias','arias','2002-08-20',55555557,11111114);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, fecha_inscripcion, celular, tel_habitacion) VALUES (210,'julia','arias','cruz','san rafael','2003-04-20',55555559,11111119);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, fecha_inscripcion, celular, tel_habitacion) VALUES (211,'paula','castillo','reyes','2003-05-15',66666666,77777777);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, e_mail, fecha_inscripcion, celular, tel_habitacion) VALUES (212,'david','arias','arias','san jose','darias@gmail.com','2005-10-20',88888888,99999999);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, e_mail, fecha_inscripcion, celular, tel_habitacion) VALUES (213,'andres','aguilar','rios','guadalupe','aaguilar@yahoo.com','2007-12-10',99999999,88888888);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, e_mail, fecha_inscripcion, celular, tel_habitacion) VALUES (214,'maria jose','villalta','paz','heredia','mjvillalta@gmail.com','2011-04-20',77777777,66666666);
INSERT INTO cliente (cedula, nombre, apellido_1, apellido_2, direccion, fecha_inscripcion, celular, tel_habitacion) VALUES (215,'pablo jose','castillo','arias','san jose','2011-04-20',33333333,66666666);

SELECT * FROM cliente;

-- Tabla instructores
INSERT INTO instructores (cod_instructor,nombre,apellido_1,apellido_2,direccion,tel_cel,tel_habitacion,fecha_contratacion) VALUES (100,'matio','jhoson','ruiz','san jose',11111111,22222222,'1995-01-01');
INSERT INTO instructores (cod_instructor,nombre,apellido_1,apellido_2,direccion,e_mail,tel_cel,tel_habitacion,fecha_contratacion) VALUES (101,'juliana','blackz','arias','alajuela','jul@hotmail.com',22222222,11111111,'1995-01-01');
INSERT INTO instructores (cod_instructor,nombre,apellido_1,apellido_2,tel_cel,tel_habitacion,fecha_contratacion) VALUES (102,'maria','perez','perez',33333333,33333335,'1998-04-20');
INSERT INTO instructores (cod_instructor,nombre,apellido_1,apellido_2,direccion,e_mail,tel_cel,tel_habitacion,fecha_contratacion) VALUES (103,'cristian','castro','ruiz','alajuela','cruiz@gmail.com',44444444,55555555,'1998-06-20');
INSERT INTO instructores (cod_instructor,nombre,apellido_1,apellido_2,direccion,e_mail,tel_cel,tel_habitacion,fecha_contratacion) VALUES (104,'margarita','mata','ruiz','pavas','mmata@yahoo.es',11111112,22222228,'2000-01-20');
INSERT INTO instructores (cod_instructor,nombre,apellido_1,apellido_2,e_mail,tel_cel,tel_habitacion,fecha_contratacion) VALUES (105,'shirley','ruiz','diaz','sruiz@hotmail.com',22222229,33333333,'2000-02-03');
INSERT INTO instructores (cod_instructor,nombre,apellido_1,apellido_2,tel_cel,tel_habitacion,fecha_contratacion) VALUES (106,'cameron','rojas','rojas',88888889,77777777,'2010-07-20');
INSERT INTO instructores (cod_instructor,nombre,apellido_1,apellido_2,e_mail,tel_cel,tel_habitacion,fecha_contratacion) VALUES (107,'patrick','ruiz','diaz','pactrick@hotmail.com',10101010,98989898,'2011-12-15');
INSERT INTO instructores (cod_instructor,nombre,apellido_1,apellido_2,tel_cel,tel_habitacion,fecha_contratacion) VALUES (110,'sharlotte','castillo','paz',99999999,22222233,'2010-05-03');

SELECT * FROM instructores;

-- Tabla cursos
INSERT INTO cursos VALUES (1,'yoga');
INSERT INTO cursos VALUES (2,'defensa personal');
INSERT INTO cursos VALUES (3,'kinboxing');
INSERT INTO cursos VALUES (4,'spinnig');
INSERT INTO cursos VALUES (5,'taebo');
INSERT INTO cursos VALUES (6,'zumba');

SELECT * FROM cursos;

-- Tabla maquinas
INSERT INTO maquinas VALUES (50,'pesas','bueno');
INSERT INTO maquinas VALUES (51,'mancuernas','excelente');
INSERT INTO maquinas VALUES (52,'caminadora','regular');
INSERT INTO maquinas VALUES (53,'bicicleta estacionaria','excelente');
INSERT INTO maquinas VALUES (54,'bicicleta spinning','bueno');
INSERT INTO maquinas VALUES (55,'press de banca','regular');
INSERT INTO maquinas VALUES (56,'press de pecho','bueno');

-- Tabla historial_curso
INSERT INTO historial_curso VALUES (1,200,110,6,'2000-02-20',2);
INSERT INTO historial_curso VALUES (2,200,101,1,'2000-02-20',2);
INSERT INTO historial_curso VALUES (3,205,105,2,'2000-02-20',3);
INSERT INTO historial_curso VALUES (4,206,102,3,'2000-05-03',2);
INSERT INTO historial_curso VALUES (5,201,101,4,'2000-07-20',4);
INSERT INTO historial_curso VALUES (6,208,110,3,'2000-08-18',3);
INSERT INTO historial_curso VALUES (7,208,110,1,'2000-08-18',2);
INSERT INTO historial_curso VALUES (8,201,110,6,'2000-08-18',1);
INSERT INTO historial_curso VALUES (9,210,102,1,'2007-04-15',1);
INSERT INTO historial_curso VALUES (10,210,101,4,'2007-04-15',2);
INSERT INTO historial_curso VALUES (11,210,105,3,'2007-04-15',1);
INSERT INTO historial_curso VALUES (12,212,105,1,'2008-05-10',2);
INSERT INTO historial_curso VALUES (13,213,105,2,'2008-05-10',2);
INSERT INTO historial_curso VALUES (14,210,105,3,'2008-05-10',2);
INSERT INTO historial_curso VALUES (15,201,105,4,'2008-05-10',2);
INSERT INTO historial_curso VALUES (16,202,105,5,'2008-05-10',2);

-- Tabla rutinas
INSERT INTO rutinas VALUES (1,209,110,50,'2000-02-20',2);
INSERT INTO rutinas VALUES (3,209,101,50,'2000-02-20',2);
INSERT INTO rutinas VALUES (5,205,105,55,'2000-02-20',3);
INSERT INTO rutinas VALUES (7,215,102,53,'2000-05-03',2);
INSERT INTO rutinas VALUES (9,215,101,55,'2000-07-20',4);
INSERT INTO rutinas VALUES (11,208,110,56,'2000-08-18',3);
INSERT INTO rutinas VALUES (13,208,110,52,'2000-08-18',2);
INSERT INTO rutinas VALUES (15,201,110,53,'2000-08-18',1);
INSERT INTO rutinas VALUES (17,210,102,55,'2007-04-15',1);
INSERT INTO rutinas VALUES (19,210,101,50,'2007-04-15',2);
INSERT INTO rutinas VALUES (21,210,107,50,'2007-04-15',1);
INSERT INTO rutinas VALUES (23,212,107,51,'2008-05-10',2);
INSERT INTO rutinas VALUES (25,213,107,52,'2008-05-10',2);
INSERT INTO rutinas VALUES (27,210,107,53,'2008-05-10',2);
INSERT INTO rutinas VALUES (29,201,107,54,'2008-05-10',2);
INSERT INTO rutinas VALUES (31,202,105,55,'2008-05-10',2);


-- ============================================================
-- Consultas de análisis (convertidas de Oracle a MySQL)
-- ============================================================

-- a. Cliente con mayor antigüedad
SELECT c1.cedula, c1.nombre, c1.apellido_1, c1.apellido_2, c1.fecha_inscripcion
FROM cliente c1
WHERE c1.fecha_inscripcion = (SELECT MIN(DISTINCT fecha_inscripcion) FROM cliente);

-- b. Cliente más reciente
SELECT c1.cedula, c1.nombre, c1.apellido_1, c1.apellido_2, c1.fecha_inscripcion
FROM cliente c1
WHERE c1.fecha_inscripcion = (SELECT MAX(DISTINCT fecha_inscripcion) FROM cliente);

-- c. Instructor con más antigüedad
SELECT c1.cod_instructor, c1.nombre, c1.apellido_1, c1.apellido_2, c1.fecha_contratacion
FROM instructores c1
WHERE c1.fecha_contratacion = (SELECT MIN(DISTINCT fecha_contratacion) FROM instructores);

-- d. Instructor más reciente
SELECT c1.cod_instructor, c1.nombre, c1.apellido_1, c1.apellido_2, c1.fecha_contratacion
FROM instructores c1
WHERE c1.fecha_contratacion = (SELECT MAX(DISTINCT fecha_contratacion) FROM instructores);

-- e. Cliente que ha estado en todos los cursos
-- (Oracle: TO_CHAR(x)||TO_CHAR(y)  ->  MySQL: CONCAT(x, y))
SELECT c1.cedula, c1.nombre, c1.apellido_1, c1.apellido_2
FROM cliente c1
WHERE c1.cedula = (
    SELECT cliente
    FROM historial_curso
    GROUP BY cliente
    HAVING COUNT(DISTINCT CONCAT(cliente, '-', curso)) = (SELECT COUNT(c.id_curso) FROM cursos c)
);

-- f. Cliente que ha utilizado todas las máquinas
SELECT c1.cedula, c1.nombre, c1.apellido_1, c1.apellido_2
FROM cliente c1
WHERE c1.cedula = (
    SELECT cliente
    FROM rutinas
    GROUP BY cliente
    HAVING COUNT(DISTINCT CONCAT(cliente, '-', maquina)) = (SELECT COUNT(m.id_maquina) FROM maquinas m)
);

-- ============================================================
-- g. Crear una tabla que contenga cedula, nombre, apellido_1, apellido_2:
-- del cliente que tiene cursos matriculados, pero nunca ha realizado
-- una rutina. La tabla debe llamarse Curso_cliente.
--
-- Pista: usá CREATE TABLE ... AS SELECT ... y un LEFT JOIN o NOT IN
-- entre historial_curso y rutinas.
-- ============================================================

CREATE TABLE Curso_cliente AS
SELECT DISTINCT c.cedula, c.nombre, c.apellido_1, c.apellido_2
FROM cliente c
         JOIN historial_curso hc ON hc.cliente = c.cedula
WHERE c.cedula NOT IN (SELECT r.cliente FROM rutinas r);

SELECT * FROM Curso_cliente;

-- ============================================================
-- h.Crear una tabla que contenga cedula, nombre, apellido_1, apellido_2:
-- del cliente que tiene rutinas, pero nunca ha llevado un curso.
-- La tabla debe llamarse Rutina_cliente.
--
-- Pista: es el caso simétrico al punto g, invirtiendo las tablas.
-- ============================================================

CREATE TABLE Rutina_cliente AS
SELECT DISTINCT c.cedula, c.nombre, c.apellido_1, c.apellido_2
FROM cliente c
         JOIN rutinas r ON r.cliente = c.cedula
WHERE c.cedula NOT IN (SELECT hc.cliente FROM historial_curso hc);

SELECT * FROM Rutina_cliente;

-- i. Cliente que nunca asistió a una rutina ni a un curso
-- (Oracle INTERSECT funciona igual en MySQL 8.0.31+ / 9.x)
SELECT c1.cedula, c1.nombre, c1.apellido_1, c1.apellido_2
FROM cliente c1
WHERE c1.cedula NOT IN (SELECT hc.cliente FROM historial_curso hc)
INTERSECT
SELECT c1.cedula, c1.nombre, c1.apellido_1, c1.apellido_2
FROM cliente c1
WHERE c1.cedula NOT IN (SELECT r.cliente FROM rutinas r);

-- j. Instructor que nunca dio un curso, solo impartió rutinas
-- (Oracle MINUS  ->  MySQL EXCEPT)
SELECT i.cod_instructor, i.nombre, i.apellido_1, i.apellido_2
FROM instructores i
WHERE i.cod_instructor IN (SELECT r.instructor FROM rutinas r)
EXCEPT
SELECT i.cod_instructor, i.nombre, i.apellido_1, i.apellido_2
FROM instructores i
WHERE i.cod_instructor IN (SELECT hc.instructor FROM historial_curso hc);

-- k. Instructor que nunca impartió rutinas, solo dio cursos
SELECT i.cod_instructor, i.nombre, i.apellido_1, i.apellido_2
FROM instructores i
WHERE i.cod_instructor IN (SELECT hc.instructor FROM historial_curso hc)
EXCEPT
SELECT i.cod_instructor, i.nombre, i.apellido_1, i.apellido_2
FROM instructores i
WHERE i.cod_instructor IN (SELECT r.instructor FROM rutinas r);

-- l. Clientes sin cursos matriculados (LEFT JOIN)
SELECT c1.*
FROM cliente c1
LEFT JOIN historial_curso hc ON c1.cedula = hc.cliente
WHERE hc.cliente IS NULL;

-- m. Clientes sin rutinas matriculadas (RIGHT JOIN)
SELECT c1.*
FROM rutinas r
RIGHT JOIN cliente c1 ON c1.cedula = r.cliente
WHERE r.cliente IS NULL;

-- n. Máquinas nunca utilizadas (LEFT JOIN)
SELECT m.*
FROM maquinas m
LEFT JOIN rutinas r ON m.id_maquina = r.maquina
WHERE r.maquina IS NULL;

-- o. Cursos nunca matriculados (RIGHT JOIN)
SELECT c.*
FROM historial_curso hc
RIGHT JOIN cursos c ON c.id_curso = hc.curso
WHERE hc.curso IS NULL;