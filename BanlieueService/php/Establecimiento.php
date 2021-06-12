<?php
	include "./OperadorBDD.php";
	include "./Herramienta.php";

	$bdd= new OperadorBDD();
	$herr= new Herramienta();

	if($_SERVER["REQUEST_METHOD"]=="POST"){
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );

		//responder($json);
		$nvoLug= $bdd->nuevoEstablecimiento(
			$herr->consecutivo( $bdd->selColumnaDeTablaOrdenado("idEst", "Establecimiento", "idEst", "ASC"), "idEst" ),
			$json["idCliente"], //ID de cliente
			$json["nombre"],
			$json["giro"],
			$json["direccion"],
			$json["apertura"],
			$json["cierre"]
		);
		$nvoLug? responder("Su nuevo negocio se registró correctamente") : responder("No se ha podido registrar su negocio");
	}


	else if($_SERVER["REQUEST_METHOD"]=="GET"){
		$json= stdObj_A_Array( json_decode( $_GET["json"] ) );

		if($json["todo"]=="s"){ //Seleccionar todos los establecimientos (usado por el usuario)
			$lista["listaNegocios"]= $bdd->selColumnaDeTabla("*", "Establecimiento");
		}
		else if($json["est"]=="s"){
			$lista= $bdd->selColumnaDeTablaEspecificado("*", "Establecimiento", "idEst", $json["idEst"])[0];
		}
		else{ //Seleccionar los establecimientos propios (usado por el cliente)
			$lista["listaNegocios"]= $bdd->selColumnaDeTablaEspecificado("*", "Establecimiento", "idCli", $json["idCliente"]);
		}
		responder( 
			 json_encode($lista)
		);
	}



	else if($_SERVER["REQUEST_METHOD"]=="PUT"){
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );

		$bdd->modifEstablecimiento($json["idEst"], "no", "no", "no", "no", "no");
		$modifLug= $bdd->modifEstablecimiento(
			$json["idEst"],
			$json["nombre"],
			$json["giro"],
			$json["direccion"],
			$json["apertura"],
			$json["cierre"]
		);
		$modifLug? responder("Datos de local modificados con éxito. Recargue la vista para ver los cambios.") : responder("No se han podido modificar datos.");
	}


	else if($_SERVER["REQUEST_METHOD"]=="PATCH"){
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );

		$elimDep= $bdd->elimGeneral("Producto_Servicio", "idEst", $json["idEst"]);
		$elimEst= $bdd->elimEstablecimiento($json["idEst"]);

		($elimDep && $elimEst)? responder("Eliminado con éxito, recargue la vista o vuelta a su inicio.") : responder("Error al eliminar");
	}

	
	else{
		responder("Método HTTP no implementado");
	}



	function stdObj_A_Array($obj){
		$reaged = (array)$obj;
		foreach($reaged as $key => &$field){
			if(is_object($field))
				$field = stdObj_A_Array($field);
		}
		return $reaged;
	}

	function responder($mensaje){
		print json_encode(array(
			"respuesta" => $mensaje
		));	
	}
?>

