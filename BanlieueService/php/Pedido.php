<?php
	include "./OperadorBDD.php";
	include "./Herramienta.php";

	$bdd= new OperadorBDD();
	$herr= new Herramienta();

	if($_SERVER["REQUEST_METHOD"]=="POST"){
		date_default_timezone_set("America/Mexico_City");
		$fecha= date("Y-m-d");
		$hora= date("H:i", time());

		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );
		$idNuevoPedido= $herr->consecutivo( $bdd->selColumnaDeTablaOrdenado("idPed", "Pedido", "idPed", "ASC"), "idPed" );
		//responder($json);

		//Decodificar el JSON representativo del cuerpo del pedido
		$jsonCuerpoPedido= stdObj_A_Array( json_decode($json["cuerpoPedido"]) );

		//Primero registrar el pedido...
		$nvoPedido= $bdd->registrarPedido(
			$idNuevoPedido,
			$json["idUsuario"],
			$fecha,
			$hora,
			$json["direccion"],
			"0" //Por defecto se coloca que no está hecho
		);

		//...después el cuerpo del mismo, para eso hay que recorrer el arreglo decodificado del cuerpo de pedido.
		$bndFallaCuerpo=false;
		$nombreServicioFallo="";
		$discriminante=1;
		foreach($jsonCuerpoPedido as $cuerpo){
			$nvoCuerpoPedido= $bdd->registrarCuerpoPedido(
				$idNuevoPedido, 
				(string)$discriminante, 
				 //Por defecto se inserta un 0, ya que al inicio un repartidor aún no toma el pedido 
				$cuerpo["idProdserv"], 
				$cuerpo["cantidad"]
			);	

			if(!$nvoCuerpoPedido){
				$nombreServicioFallo= $bdd->selColumnaDeTablaEspecificado("nombre", "Producto_Servicio", "idProdserv", $cuerpo["idProdserv"]) [0];
				$bdnFallaCuerpo=true;
				break;
			}

			$discriminante+=1;
		}
	
		if($bndFallaCuerpo)
			responder( "Error al insertar su pedido de "+$nombreServicioFallo );
		else
			if($nvoPedido && $nvoCuerpoPedido)
				responder("Se ha publicado su pedido, vaya al lugar o espere al repartidor \u{1F600}");
			else
				responder("ERROR: No se ha podido registrar el pedido");
	}


	/*else if($_SERVER["REQUEST_METHOD"]=="GET"){
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
	}*/



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

