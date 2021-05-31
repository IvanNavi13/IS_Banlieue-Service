<?php
	include "./OperadorBDD.php";
	include "./Herramienta.php";

	$bdd= new OperadorBDD();
	$herr= new Herramienta();

	if($_SERVER["REQUEST_METHOD"]=="POST"){
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );
		$verif= $bdd->selColumnaDeTablaEspecificado("idPersona", "Persona", "correo", $json["correo"])[0];

		if(sizeof($verif)==0){
			$nvoIdPer= $herr->consecutivo( $bdd->selColumnaDeTablaOrdenado("idPersona", "Persona", "idPersona", "ASC"), "idPersona" );
			$bdd->nuevaPersona(
				$nvoIdPer,
				$json["nombres"],
				$json["apaterno"], 
				$json["amaterno"], 
				$json["telefono"],
				$json["fechanac"], 
				$json["correo"], 
				$json["contrasena"]
			);

			if($json["tipoPersona"]=="cli"){
				$res= $bdd->nuevoCliente(
					$nvoIdPer,
					$herr->consecutivo( $bdd->selColumnaDeTablaOrdenado("idCli", "Cliente", "idCli", "ASC"), "idCli" )
				);
				$res? responder("Cliente registrado con éxito") : responder("Fallo al registrar cliente");
			}
			else if($json["tipoPersona"]=="usr"){
				$res= $bdd->nuevoUsuario(
					$nvoIdPer,
					$herr->consecutivo( $bdd->selColumnaDeTablaOrdenado("idUs", "Usuario", "idUs", "ASC"), "idUs" )
				);
				$res? responder("Usuario registrado con éxito") : responder("Fallo al registrar usuario");
			}
			else if($json["tipoPersona"]=="rep"){
				$nvoRep= $herr->consecutivo( $bdd->selColumnaDeTablaOrdenado("idRep", "Repartidor", "idRep", "ASC"), "idRep" );
				$res= $bdd->nuevoRepartidor(
					$nvoIdPer,
					$nvoRep,
					$json["CURP"]
				);
				$res2= $bdd->registrarVehiculo(
					$herr->consecutivo( $bdd->selColumnaDeTablaOrdenado("idVe", "Vehiculo", "idVe", "ASC"), "idVe" ),
					$nvoRep,
					$json["tipoVe"],
					$json["placa"]
				);
				($res && $res2)? responder("Repartidor registrado con éxito") : responder("Fallo al registrar repartidor");
			}
			else{
				responder("POST: Mala opción al registrar persona. ".$json["tipoPersona"]);
			}
		}
		else{
			responder("Ya existe una cuenta asociada a ese correo");
		}
	}
	else if($_SERVER["REQUEST_METHOD"]=="GET"){
		$json= stdObj_A_Array( json_decode( $_GET["json"] ) );

		if($json["tipoPersona"]=="cli"){
			responder( $bdd->infoDeCliente( $json["correo"] ) [0] );
		}
		else if($json["tipoPersona"]=="usr"){
			responder( $bdd->infoDeUsuario( $json["correo"] ) [0] );
		}
		else if($json["tipoPersona"]=="rep"){
			responder( $bdd->infoDeRepartidor( $json["correo"] ) [0] );
		}
		else{
			responder("GET: Mala opción al consultar persona.");	
		}
	}
	else if($_SERVER["REQUEST_METHOD"]=="PUT"){

	}
	else if($_SERVER["REQUEST_METHOD"]=="DELETE"){
		//Probado sin interfaz en android
		$json= stdObj_A_Array( json_decode( $_DELETE["json"] ) );

		if($json["tipoPersona"]=="cli"){
			$idp= $bdd->selColumnaDeTablaEspecificado("idPersona", "Cliente", "idCli", $json["id"]) [0];
			responder( $bdd->elimCliente( $json["id"], $idp ) [0] );
		}
		else if($json["tipoPersona"]=="usr"){
			$idp= $bdd->selColumnaDeTablaEspecificado("idPersona", "Usuario", "idUs", $json["id"]) [0];
			responder( $bdd->elimUsuario( $json["id"], $idp ) [0] );
		}
		else if($json["tipoPersona"]=="rep"){
			$idp= $bdd->selColumnaDeTablaEspecificado("idPersona", "Repartidor", "idRep", $json["id"]) [0];
			responder( $bdd->elimRepartidor( $json["id"], $idp ) [0] );
		}
		else{
			responder("GET: Mala opción al consultar persona.");
		}
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

/*
FORMATO DEL JSON
{
	"tipoPersona": "usr" | "cli" | "rep",
	"nombres": "dato", 
	"apaterno": "dato", 
	"amaterno": "dato", 
	"edad": "dato", 
	"correo": "dato", 
	"contrasena": "dato"
	// Si es rep, entonces //
	"tipoVe": "dato",
	"placa": "dato",
	"CURP": "dato"
}
*/
	
?>

