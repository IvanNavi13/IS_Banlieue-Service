<?php
	include "./OperadorBDD.php";
	include "./Herramienta.php";
	SESSION_START();

	$bdd= new OperadorBDD();
	$herr= new Herramienta();

	if($_SERVER["REQUEST_METHOD"]=="POST"){
		//$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );
		$nvoIdEst= $herr->consecutivo( $bdd->selColumnaDeTablaOrdenado("idEst", "Establecimiento", "idEst", "ASC"), "idEst" );
		$imgPath= "../imgLug/$nvoIdEst";
		//chmod($imgPath, 777);

		//responder($json);
		$nvoLug= $bdd->nuevoEstablecimiento(
			$nvoIdEst,
			$_POST["idCliente"], //ID de cliente
			$_POST["nombre"],
			$_POST["giro"],
			$_POST["direccion"],
			$_POST["apertura"],
			$_POST["cierre"]
		);

		//$foto= move_uploaded_file( base64_decode($json["img"]), $imgPath );
		$foto= file_put_contents($imgPath, base64_decode($_POST["img"]));
		//responder($foto);
		($nvoLug && $foto)? print("Su nuevo negocio se registró correctamente") : print("Algo no se ha podido registrar sobre su negocio");
	}


	else if($_SERVER["REQUEST_METHOD"]=="GET"){
		$json= stdObj_A_Array( json_decode( $_GET["json"] ) );

		if($json["todo"]=="s"){ //Seleccionar todos los establecimientos (usado por el usuario)
			$lista["listaNegocios"]= $bdd->selColumnaDeTabla("*", "Establecimiento");
		}
		else if($json["est"]=="s"){ //Seleccionar 
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
		$elimDep2= $bdd->elimGeneral("Establecimiento_telefono", "idEst", $json["idEst"]);
		$elimEst= $bdd->elimEstablecimiento($json["idEst"]);

		($elimDep && $elimDep2 && $elimEst)? responder("Eliminado con éxito, recargue la vista o vuelta a su inicio.") : responder("Error al eliminar");
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

