<?php
	include "./ConectorBDD.php";

	class OperadorBDD{		
		/**************** CONSULTAS ****************/
		//Generales
		public function selColumnaDeTabla($columna, $tabla){
			return $this->ejecutarSelectQuery(
				"SELECT $columna FROM $tabla"
			);
		}
		
		public function selColumnaDeTablaEspecificado($columna, $tabla, $colReferencia, $valorReferencia){
			return $this->ejecutarSelectQuery(
				"SELECT $columna FROM $tabla WHERE $colReferencia='$valorReferencia'"
			);
		}
		
		public function selColumnaDeTablaEspecifMax($columna, $tabla, $colReferencia, $colValorMaximo, $tablaDeColValorMaximo){
			return $this->ejecutarSelectQuery(
				"SELECT $columna FROM $tabla WHERE $colReferencia=(SELECT MAX($colValorMaximo) FROM $tablaDeColValorMaximo)"
			);
		}

		public function selColumnaDeTablaOrdenado($columna, $tabla, $refOrden, $tipoOrden){ //$tipoOrden: ASC o DESC
			return $this->ejecutarSelectQuery(
				"SELECT $columna FROM $tabla ORDER BY $refOrden $tipoOrden"
			);
		}

		public function selColumnaDeTablaEspecificadoOrdenado($columna, $tabla, $colReferencia, $valorReferencia, $refOrden, $tipoOrden){ //$tipoOrden: ASC o DESC
			return $this->ejecutarSelectQuery(
				"SELECT $columna FROM $tabla WHERE $colReferencia='$valorReferencia' ORDER BY $refOrden $tipoOrden"
			);
		}
		
		//Específicas
		//// Llamadas a vistas

		//// Llamadas a procedimientos
		public function infoDeUsuario($corrUsuario){
			return $this->ejecutarSelectQuery(
				"CALL infoUsuario('$corrUsuario')",
				array($corrUsuario)
			);
		}

		public function infoDeCliente($corrCliente){
			return $this->ejecutarSelectQuery(
				"CALL infoCliente('$corrCliente')",
				array($corrCliente)
			);
		}

		public function infoDeRepartidor($corrRepartidor){
			return $this->ejecutarSelectQuery(
				"CALL infoRepartidor('$corrRepartidor')",
				array($corrRepartidor)
			);
		}

		//Especiales
		public function obtColumnasDeTabla($tabla){
			return $this->ejecutarSelectQuery(
				"SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME= '$tabla'"
			);
		}

		public function obtTablasDeBdd($BDD_name){
			return $this->ejecutarSelectQuery(
				"SELECT TABLE_NAME from INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='$BDD_name'"
			);
		}
		
		//Ejecución de consultas
		private function ejecutarSelectQuery($query){
			try{
				$comando= ConectorBDD::getDB()->prepare($query);
				$comando->execute();				
				return $comando->fetchAll(PDO::FETCH_ASSOC);
			}catch(PDOException $ex){
				return -1;
			}
		}	
		//////////////////////////////




		/**************** ALTAS ****************/
		//Generales
		public function insertEnTabla($tabla, $valoresConFormatoSQL){
			return $this->ejecutarInsertQuery(
				"INSERT INTO $tabla VALUES $valoresConFormatoSQL",
				array($tabla, $valoresConFormatoSQL)
			);
		}

		//Específicos
		public function nuevaPersona($id, $nombre, $apaterno, $amaterno, $telefono, $fechanac, $correo, $contrasena){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Persona VALUES('$id', '$nombre', '$apaterno', '$amaterno', '$telefono', '$fechanac', '$correo', '$contrasena')",
				array($id, $nombre, $apaterno, $amaterno, $telefono, $fechanac, $correo, $contrasena)
			);
		}

		public function nuevoUsuario($idPersona, $idUsuario){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Usuario VALUES('$idPersona', '$idUsuario')",
				array($idPersona, $idUsuario)
			);
		}

		public function nuevoCliente($idPersona, $idCliente){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Cliente VALUES('$idPersona', '$idCliente')",
				array($idPersona, $idCliente)
			);
		}

		public function nuevoRepartidor($idPersona, $idRepartidor, $CURP){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Repartidor VALUES('$idPersona', '$idRepartidor', '$CURP')",
				array($idPersona, $idRepartidor, $CURP)
			);
		}

		public function nuevoEstablecimiento($id, $idDueno, $nombre, $giro, $direccion, $apertura, $cierre){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Establecimiento VALUES('$id', '$idDueno', '$nombre', '$giro', '$direccion', '$apertura', '$cierre')",
				array($id, $idDueno, $nombre, $giro, $direccion, $apertura, $cierre)
			);
		}

		public function registrarVehiculo($id, $idRepartidor, $tipo, $placa){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Vehiculo VALUES('$id', '$idRepartidor', '$tipo', '$placa')",
				array($id, $idRepartidor, $tipo, $placa)
			);
		}

		public function nuevoProdServ($id, $nombre, $descripcion, $precio){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Producto_Servicio VALUES('$id', '$descripcion', '$nombre', '$precio')",
				array($id, $nombre, $descripcion, $precio)
			);
		}

		public function relProServEstabl($idProserv, $idEstablecimiento){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Tiene VALUES('$idProServ', '$idEstablecimiento')",
				array($idProserv, $idEstablecimiento)
			);
		}

		public function nuevoTelDeEstablecimiento($idEstablecimiento, $telefono){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Establecimiento_telefono VALUES('$idEstablecimiento', '$telefono')",
				array($idEstablecimiento, $telefono)
			);
		}

		public function registrarPedido($idPedido, $idUsuario, $idRepartidor, $fecha, $hora, $direccion, $hecho){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Pedido VALUES('$idPedido', '$idUsuario', '$idRepartidor', '$hora', '$direccion', '$hecho', '$fecha')",
				array($idPedido, $idUsuario, $idRepartidor, $fecha, $hora, $direccion, $hecho)
			);
		}

		public function registrarCuerpoPedido($idPedido, $idDiscriminante, $idProserv, $cantidad){
			return $this->ejecutarInsertQuery(
				"INSERT INTO CuerpoPedido VALUES('$idPedido', '$idDiscriminante', '$idProserv', '$cantidad')",
				array($idPedido, $idDiscriminante, $idProserv, $cantidad)
			);
		}

		//Ejecución de registros
		private function ejecutarInsertQuery($query, $arregloDeValores){
			try{
				$comando= ConectorBDD::getDB()->prepare($query);
				return $comando->execute($arregloDeValores);
			}catch(PDOException $ex){
				return -1;
			}
		}
		//////////////////////////////


		
		
		/**************** BAJAS ****************/
		public function elimUsuario($idUsuario, $idPersona){
			return $this->ejecutarDeleteQuery(
				"CALL elimUsuario($idUsuario, $idPersona)",
				array($idUsuario, $idPersona)
			);
		}

		public function elimCliente($idCliente, $idPersona){
			return $this->ejecutarDeleteQuery(
				"CALL elimCliente($idCliente, $idPersona)",
				array($idCliente, $idPersona)
			);
		}

		public function elimRepartidor($idRepartidor, $idPersona){
			return $this->ejecutarDeleteQuery(
				"CALL elimRepartidor($idRepartidor, $idPersona)",
				array($idRepartidor, $idPersona)
			);
		}

		public function elimEstablecimiento($id){
			return $this->ejecutarDeleteQuery(
				"DELETE FROM Establecimiento WHERE idEst='$id'",
				array($id)
			);
		}

		public function elimRelacionEstablServicio($idEstablecimiento){ //Se debe llamar este método antes de eliminar un establecimiento
			return $this->ejecutarDeleteQuery(
				"DELETE FROM Tiene WHERE idEst='$idEstablecimiento'",
				array($idEstablecimiento)
			);
		}

		public function elimVehiculo($id, $idRepartidor, $tipo, $placa){
			return $this->ejecutarDeleteQuery(
				"INSERT INTO Vehiculo VALUES('$id', '$idRepartidor', '$tipo', '$placa')",
				array($id, $idRepartidor, $tipo, $placa)
			);
		}

		public function elimProdServ($id, $nombre, $descripcion, $precio){
			return $this->ejecutarDeleteQuery(
				"INSERT INTO Producto_Servicio VALUES('$id', '$descripcion', '$nombre', '$precio')",
				array($id, $nombre, $descripcion, $precio)
			);
		}

		public function elimProServEstabl($idProserv, $idEstablecimiento){
			return $this->ejecutarDeleteQuery(
				"INSERT INTO Tiene VALUES('$idProServ', '$idEstablecimiento')",
				array($idProserv, $idEstablecimiento)
			);
		}

		public function elimTelDeEstablecimiento($idEstablecimiento, $telefono){
			return $this->ejecutarDeleteQuery(
				"INSERT INTO Establecimiento_telefono VALUES('$idEstablecimiento', '$telefono')",
				array($idEstablecimiento, $telefono)
			);
		}

		public function elimPedido($idPedido, $idUsuario, $idRepartidor, $fecha, $hora, $direccion, $hecho){
			return $this->ejecutarDeleteQuery(
				"INSERT INTO Pedido VALUES('$idPedido', '$idUsuario', '$idRepartidor', '$hora', '$direccion', '$hecho', '$fecha')",
				array($idPedido, $idUsuario, $idRepartidor, $fecha, $hora, $direccion, $hecho)
			);
		}

		public function elimCuerpoPedido($idPedido, $idDiscriminante, $idProserv, $cantidad){
			return $this->ejecutarDeleteQuery(
				"INSERT INTO CuerpoPedido VALUES('$idPedido', '$idDiscriminante', '$idProserv', '$cantidad')",
				array($idPedido, $idDiscriminante, $idProserv, $cantidad)
			);
		}

		public function ejecutarDeleteQuery($query, $arregloDeValores){
			try{
				$comando= ConectorBDD::getDB()->prepare($query);
				return $comando->execute(array($arregloDeValores));
			}catch(PDOException $ex){
				return -1;
			}
		}
		//////////////////////////////


		/**************** MODIFICACIONES ****************/
		public function modifPersona($id, $nombre, $apaterno, $amaterno, $telefono, $edad, $correo, $contrasena){
			return $this->ejecutarUpdateQuery(
				"UPDATE Persona SET nombre='$nombre', apaterno='$apaterno', amaterno='$amaterno', telefono='$telefono', edad='$edad', correo='$correo', contrasena='$contrasena' WHERE idPersona='$id'",
				array($id, $nombre, $apaterno, $amaterno, $telefono, $edad, $correo, $contrasena)
			);
		}

		///************************
		/*public function modifUsuario($idPersona, $idUsuario){
			return $this->ejecutarUpdateQuery(
				"UPDATE Usuario SET ... WHERE idUs='$idUsuario'",
				array($idPersona, $idUsuario)
			);
		}*/

		/*public function modifCliente($idPersona, $idCliente){
			return $this->ejecutarUpdateQuery(
				"UPDATE Cliente SET ... WHERE idCli='$idCliente'",
				array($idPersona, $idCliente)
			);
		}*/

		public function modifRepartidor($idPersona, $idRepartidor, $CURP){
			return $this->ejecutarUpdateQuery(
				"UPDATE Repartidor SET CURP='$CURP' WHERE idRep='$idRepartidor'",
				array($idPersona, $idRepartidor, $CURP)
			);
		}

		public function modifEstablecimiento($id, $nombre, $giro, $direccion, $apertura, $cierre){
			return $this->ejecutarUpdateQuery(
				"UPDATE Establecimiento SET nombre='$nombre', giro='$giro', direccion='$direccion', apertura='$apertura', cierre='$cierre' WHERE idEst='$id'",
				array($id, $nombre, $giro, $direccion, $apertura, $cierre)
			);
		}

		public function modifVehiculo($id, $tipo, $placa){
			return $this->ejecutarUpdateQuery(
				"UPDATE Vehiculo SET tipo='$tipo', placa='$placa' WHERE idVe='$id'",
				array($id, $tipo, $placa)
			);
		}

		public function modifProdServ($id, $nombre, $descripcion, $precio){
			return $this->ejecutarUpdateQuery(
				"UPDATE Producto_Servicio SET nombre='$nombre', descripcion='$descripcion', precio='$precio' WHERE idProdServ='$id'",
				array($id, $nombre, $descripcion, $precio)
			);
		}

		/*public function modifProServEstabl($idProserv, $idEstablecimiento){
			return $this->ejecutarUpdateQuery(
				"UPDATE Tiene SET ... '$idProServ'",
				array($idProserv, $idEstablecimiento)
			);
		}*/

		public function modifTelDeEstablecimiento($idEstablecimiento, $telefonoActual, $telefonoNuevo){
			return $this->ejecutarUpdateQuery(
				"UPDATE Establecimiento_telefono SET telefono='$telefonoNuevo' WHERE idEst='$idEstablecimiento' AND telefono='$telefonoActual'",
				array($idEstablecimiento, $telefonoActual, $telefonoNuevo)
			);
		}
		///************************
		
		//Ejecución de modificaciones
		private function ejecutarUpdateQuery($query, $arregloDeValores){
			try{
				$comando= ConectorBDD::getDB()->prepare($query);
				$comando->execute($arregloDeValores);
				return $comando->rowCount();
			}catch(PDOException $ex){
				return -1;
			}
		}
		//////////////////////////////
		


		/////QUERYS EXTRAS
		public function eliminarVista($nomVista){
			return $this->ejecutarQuery("DROP VIEW IF EXISTS $nomVista");
		}

		public function crearVista($nomVista, $estructura){
			return $this->ejecutarQuery(
				"CREATE VIEW $nomVista AS $estructura"
			);
		}



		private function ejecutarQuery($query){
			try{
				$comando= ConectorBDD::getDB()->prepare($query);
				return $comando->execute(array($tabla, $folio));
			}catch(PDOException $ex){
				return -1;
			}
		}
	}//clase Funciones
?>
