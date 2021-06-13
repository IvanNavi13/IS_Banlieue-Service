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
		public function verPedidosDisponibles(){ //Llamado por el repartidor
			return $this->ejecutarSelectQuery(
				"SELECT * FROM pedidosDisponibles",
				array()
			);
		}

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

		public function verCuerpoDePedido($idPedido){
			return $this->ejecutarSelectQuery(
				"CALL pedido($idPedido)",
				array($idPedido)
			);
		}

		public function verLugarDePedido($idPedido){
			return $this->ejecutarSelectQuery(
				"CALL lugDePedido($idPedido)",
				array($idPedido)
			);
		}

		public function verPedidosTomados($idRepartidor){
			return $this->ejecutarSelectQuery(
				"CALL pedidosTomadosPor($idRepartidor)",
				array($idRepartidor)
			);
		}

		public function verPedidosRealizados($idRepartidor){
			return $this->ejecutarSelectQuery(
				"CALL pedidosRealizadosPor($idRepartidor)",
				array($idRepartidor)
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

		public function nuevoProdServ($id, $idEstablecimiento, $descripcion, $nombre, $precio){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Producto_Servicio VALUES('$id', '$idEstablecimiento', '$descripcion', '$nombre', '$precio')",
				array($id, $idEstablecimiento, $descripcion, $nombre, $precio)
			);
		}

		public function nuevoTelDeEstablecimiento($idEstablecimiento, $telefono){ //////
			return $this->ejecutarInsertQuery(
				"INSERT INTO Establecimiento_telefono VALUES('$idEstablecimiento', '$telefono')",
				array($idEstablecimiento, $telefono)
			);
		}

		public function registrarPedido($idPedido, $idUsuario, $fecha, $hora, $direccion, $hecho){
			return $this->ejecutarInsertQuery(
				"INSERT INTO Pedido VALUES('$idPedido', '$idUsuario', null, '$hora', '$direccion', '$hecho', '$fecha')",
				array($idPedido, $idUsuario, $fecha, $hora, $direccion, $hecho)
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
		///GENERALES
		public function elimGeneral($tabla, $campo, $valorCampo){
			return $this->ejecutarDeleteQuery(
				"DELETE FROM $tabla WHERE $campo='$valorCampo'",
				array($tabla, $campo, $valorCampo)
			);
		}
		////////////

		//ESPECÍFICOS
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

		public function elimVehiculo($id, $idRepartidor, $tipo, $placa){
			return $this->ejecutarDeleteQuery(
				"INSERT INTO Vehiculo VALUES('$id', '$idRepartidor', '$tipo', '$placa')",
				array($id, $idRepartidor, $tipo, $placa)
			);
		}

		public function elimProdServ($idProserv){
			return $this->ejecutarDeleteQuery(
				"DELETE FROM Producto_Servicio WHERE idProdServ='$idProserv'",
				array($idProserv)
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

		public function elimCuerpoPedido($idProserv){
			return $this->ejecutarDeleteQuery(
				"DELETE FROM CuerpoPedido WHERE idProdServ='$idProserv'",
				array($idProserv)
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
		public function modifDatosPersonales($id, $nombres, $apaterno, $amaterno, $telefono, $fechanac, $modPer){
			//Preguntar si se modifica el nombre o no
			if($modPer){
				return $this->ejecutarUpdateQuery(
					"UPDATE Persona SET nombres='$nombres', apaterno='$apaterno', amaterno='$amaterno', telefono='$telefono', fechanac='$fechanac' WHERE idPersona='$id'",
					array($id, $nombres, $apaterno, $amaterno, $telefono, $fechanac)
				);
			}
			else{
				return $this->ejecutarUpdateQuery(
					"UPDATE Persona SET telefono='$telefono', fechanac='$fechanac' WHERE idPersona='$id'",
					array($id, $telefono, $fechanac)
				);
			}
		}

		public function modifDatosAcceso($id, $correo, $contrasena){
			return $this->ejecutarUpdateQuery(
				"UPDATE Persona SET correo='$correo', contrasena='$contrasena' WHERE idPersona='$id'",
				array($id, $correo, $contrasena)
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

		public function modifRepartidor($idRepartidor, $CURP){
			return $this->ejecutarUpdateQuery(
				"UPDATE Repartidor SET CURP='$CURP' WHERE idRep='$idRepartidor'",
				array($idRepartidor, $CURP)
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

		public function tomarPedido($idRepartidor, $idPedido){ //Llamado por el repartidor para tomar un pedido
			return $this->ejecutarUpdateQuery(
				"UPDATE Pedido SET idRep='$idRepartidor' WHERE idPed='$idPedido'",
				array($idRepartidor, $idPedido)
			);
		}

		public function marcarPedido($idRepartidor, $idPedido){ //Llamado por el repartidor para tomar un pedido
			return $this->ejecutarUpdateQuery(
				"UPDATE Pedido SET hecho=1 WHERE idPed='$idPedido' AND idRep='$idRepartidor'",
				array($idRepartidor, $idPedido)
			);
		}


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
