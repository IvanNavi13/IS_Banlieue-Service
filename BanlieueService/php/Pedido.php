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



	else if($_SERVER["REQUEST_METHOD"]=="GET"){
		$json= stdObj_A_Array( json_decode( $_GET["json"] ) );
		//responder($json);

		if($json["pedido"]=="disp"){ //Pedidos disponibles (campo "hecho" de Pedido es 0 e idRep en CuerpoPedido es NULL)
			$lista["listaPedidosDisponibles"]= $bdd->verPedidosDisponibles();
		}
		else if($json["pedido"]=="pend"){ //Pedidos pendientes (campo "hecho" de Pedido es 0 e idRep en CuerpoPedido tiene valor)
			$lista["listaPedidosTomados"]= $bdd->verPedidosTomados($json["idRepartidor"]);
		}
		else if($json["pedido"]=="real"){ //Pedidos realizados (campo "hecho" de Pedido es 1 e idRep en CuerpoPedido tiene valor)
			$lista["listaPedidosRealizados"]= $bdd->verPedidosRealizados($json["idRepartidor"]);
		}
		else if($json["pedido"]=="cuerpo"){
			$lista["cuerpoDelPedido"]= $bdd->verCuerpoDePedido($json["idPedido"]);
		}
		else if($json["pedido"]=="lugar"){
			$lista= $bdd->verLugarDePedido($json["idPedido"])[0];
		}

		responder( 
			 json_encode($lista)
		);
	}



	else if($_SERVER["REQUEST_METHOD"]=="PUT"){
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );

		if($json["pedido"]=="tomar"){
			$tomarPed= $bdd->tomarPedido(
				$json["idRepartidor"],
				$json["idPedido"]
			);	
			$tomarPed? responder("Ok, tomado y pendiente \u{1F600}\u{1F44C}, recarga la pantalla") : responder("No puedes tomar este pedido");
		}
		else if($json["pedido"]=="marcar"){
			$tomarPed= $bdd->marcarPedido(
				$json["idRepartidor"],
				$json["idPedido"]
			);	
			$tomarPed? responder("Ok, misión cumplida \u{1F600}\u{1F440}, recarga la pantalla") : responder("No puedes marcar este pedido como hecho");
		}
		
		
	}


	/*else if($_SERVER["REQUEST_METHOD"]=="PATCH"){
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );
		
		$elimDep= $bdd->elimCuerpoPedido($json["idProdserv"]);
		$elimEst= $bdd->elimProdServ($json["idProdserv"]);

		($elimDep && $elimEst)? responder("Eliminado con éxito") : responder("Error al eliminar");
	}*/

	
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

