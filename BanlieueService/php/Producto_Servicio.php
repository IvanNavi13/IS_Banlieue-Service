<?php
	include "./OperadorBDD.php";
	include "./Herramienta.php";

	$bdd= new OperadorBDD();
	$herr= new Herramienta();

	if($_SERVER["REQUEST_METHOD"]=="POST"){
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );
		
		$nvoProServ= $bdd->nuevoProdServ(
			$herr->consecutivo( $bdd->selColumnaDeTablaOrdenado("idProdserv", "Producto_Servicio", "idProdserv", "ASC"), "idProdserv" ),
			$json["idEst"],
			$json["descripcion"],
			$json["nombre"],
			$json["precio"]
		);
		$nvoProServ? responder("Nuevo servicio agregado") : responder("ERROR: No se ha podido registrar el nuevo producto/servicio");
	}


	else if($_SERVER["REQUEST_METHOD"]=="GET"){
		$json= stdObj_A_Array( json_decode( $_GET["json"] ) );
		//responder($json);

		$lista["listaServicios"]= $bdd->selColumnaDeTablaEspecificado("*", "Producto_Servicio", "idEst", $json["idEst"]);
		responder( 
			 json_encode($lista)
		);
	}



	else if($_SERVER["REQUEST_METHOD"]=="PUT"){
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );

		$bdd->modifProdServ($json["idProdserv"], "no", "no", "no");
		$modifLug= $bdd->modifProdServ(
			$json["idProdserv"],
			$json["nombre"],
			$json["descripcion"],
			$json["precio"]
		);
		$modifLug? responder("Datos de producto/servicio modificados con éxito") : responder("No se han podido modificar datos.");
	}


	else if($_SERVER["REQUEST_METHOD"]=="PATCH"){
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );
		
		$elimDep= $bdd->elimCuerpoPedido($json["idProdserv"]);
		$elimEst= $bdd->elimProdServ($json["idProdserv"]);

		($elimDep && $elimEst)? responder("Eliminado con éxito") : responder("Error al eliminar");
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

