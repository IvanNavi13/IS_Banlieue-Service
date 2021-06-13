DROP TABLE IF EXISTS Persona;
CREATE TABLE Persona
(
  idPersona INT NOT NULL,
  nombres VARCHAR(50) NOT NULL,
  apaterno VARCHAR(50) NOT NULL,
  amaterno VARCHAR(50) NOT NULL,
  telefono VARCHAR(15) NOT NULL,
  fechanac DATE NOT NULL,
  correo VARCHAR(150) NOT NULL,
  contrasena VARCHAR(50) NOT NULL,
  PRIMARY KEY (idPersona)
);

DROP TABLE IF EXISTS Usuario;
CREATE TABLE Usuario
(
  idPersona INT NOT NULL,
  idUs INT NOT NULL,
  PRIMARY KEY (idUs),
  FOREIGN KEY (idPersona) REFERENCES Persona(idPersona)
);

DROP TABLE IF EXISTS Cliente;
CREATE TABLE Cliente
(
  idPersona INT NOT NULL,
  idCli INT NOT NULL,
  PRIMARY KEY (idCli),
  FOREIGN KEY (idPersona) REFERENCES Persona(idPersona)
);

DROP TABLE IF EXISTS Repartidor;
CREATE TABLE Repartidor
(
  idPersona INT NOT NULL,
  idRep INT NOT NULL,
  CURP VARCHAR(18) NOT NULL,
  PRIMARY KEY (idRep),
  FOREIGN KEY (idPersona) REFERENCES Persona(idPersona)
);

DROP TABLE IF EXISTS Establecimiento;
CREATE TABLE Establecimiento
(
  idEst INT NOT NULL,
  idCli INT NOT NULL,
  nombre TEXT NOT NULL,
  giro TEXT NOT NULL,
  direccion TEXT NOT NULL,
  apertura TIME NOT NULL,
  cierre TIME NOT NULL,
  PRIMARY KEY (idEst),
  FOREIGN KEY (idCli) REFERENCES Cliente(idCli)
);

DROP TABLE IF EXISTS Producto_Servicio;
CREATE TABLE Producto_Servicio
(
  idProdserv INT NOT NULL,
  idEst INT NOT NULL,
  descripcion TEXT NOT NULL,
  nombre TEXT NOT NULL,
  precio DOUBLE NOT NULL,
  PRIMARY KEY (idProdserv),
  FOREIGN KEY (idEst) REFERENCES Establecimiento(idEst)
);


DROP TABLE IF EXISTS Establecimiento_telefono;
CREATE TABLE Establecimiento_telefono
(
  idEst INT NOT NULL,
  telefono VARCHAR(15) NOT NULL,
  PRIMARY KEY (idEst, telefono),
  FOREIGN KEY (idEst) REFERENCES Establecimiento(idEst)
);

DROP TABLE IF EXISTS Vehiculo;
CREATE TABLE Vehiculo
(
  idVe INT NOT NULL,
  idRep INT NOT NULL,
  tipo TEXT NOT NULL,
  placa TEXT NOT NULL,
  PRIMARY KEY (idVe),
  FOREIGN KEY (idRep) REFERENCES Repartidor(idRep)
);

DROP TABLE IF EXISTS Pedido;
CREATE TABLE Pedido
(
  idPed INT NOT NULL,
  idUs INT NOT NULL,
  idRep INT,
  hora TIME NOT NULL,
  direccion TEXT NOT NULL,
  hecho INT(1) NOT NULL,
  fecha DATE NOT NULL,
  PRIMARY KEY (idPed),
  FOREIGN KEY (idUs) REFERENCES Usuario(idUs),
  FOREIGN KEY (idRep) REFERENCES Repartidor(idRep)
);

DROP TABLE IF EXISTS CuerpoPedido;
CREATE TABLE CuerpoPedido
(
  idPed INT NOT NULL,
  idDiscr INT NOT NULL,
  idProdserv INT NOT NULL,
  cantidad INT NOT NULL,
  PRIMARY KEY (idPed, idDiscr),
  FOREIGN KEY (idPed) REFERENCES Pedido(idPed),
  FOREIGN KEY (idProdserv) REFERENCES Producto_Servicio(idProdserv)
);
