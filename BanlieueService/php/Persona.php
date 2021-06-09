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
				responder("POST: Código de persona incorrecto. ".$json["tipoPersona"]);
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
			responder("GET: Código de consulta incorrecto.");	
		}
	}



	else if($_SERVER["REQUEST_METHOD"]=="PUT"){
		//Al JSON llega: datos, opc, id, fechanac, telefono, *nombre
		//datos= per | acc //Saber si se está solicitando cambio de datos personales o de acceso
		//opc= mod | cualquier_otro //En caso de que sean datos personales, preguntar si se modifica el nombre
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );

		if($json["datos"]=="per"){
			$json["opc"]=="mod"? $bndPer=true : $bndPer=false;
			$modif= $bdd->modifDatosPersonales($json["id"], $json["nombres"], $json["apaterno"], $json["amaterno"], $json["telefono"], $json["fechanac"], $bndPer);
			if($modif)
				responder("Modificación realizada con éxito, recargue la vista para ver los cambios.");
			else
				responder("Algo falló al modificar los datos personales.");
		}
		else if($json["datos"]=="acc"){
			$modif= $bdd->modifDatosAcceso($json["id"], $json["correo"], $json["contrasena"]);
			if($modif)
				responder("Modificación realizada con éxito, inicie sesión con sus nuevos datos.");
			else
				responder("Código de modificación específica incorrecto.");
		}
		else if($json["datos"]=="serv"){
			$bdd->modifRepartidor($json["idrep"], "no");
			$bdd->modifVehiculo($json["idve"], "no", "no");
			$modif1= $bdd->modifRepartidor($json["idrep"], $json["CURP"]);
			$modif2= $bdd->modifVehiculo($json["idve"], $json["vehiculo"], $json["placa"]);
			($modif1 && $modif2)? responder("Datos de servicio modificados con éxito.") : responder("Algo falló al modificar los datos de servicio.");
		}
		else{
			responder("PUT: Código de modificación incorrecto");
		}
	}


	else if($_SERVER["REQUEST_METHOD"]=="PATCH"){
		$json= stdObj_A_Array( json_decode( file_get_contents("php://input") ) );

		//Enviar correo a los desarrolladores
		//$json["comentarios"];

		if($json["tipoPersona"]=="cli"){
			$elim= $bdd->elimCliente( $json["idPartic"], $json["idPersona"] );
			$elim? responder("Cliente eliminado, regrese pronto a Banlieue Service") : responder("Error al eliminar");
		}
		else if($json["tipoPersona"]=="usr"){
			$elim= $bdd->elimUsuario( $json["idPartic"], $json["idPersona"] );
			$elim? responder("Usuario eliminado, regrese pronto a Banlieue Service") : responder("Error al eliminar");
		}
		else if($json["tipoPersona"]=="rep"){
			$elim= $bdd->elimRepartidor( $json["idPartic"], $json["idPersona"] );
			$elim? responder("Repartidor eliminado, regrese pronto a Banlieue Service") : responder("Error al eliminar");
		}
		else{
			responder("DELETE (PATCH): Código de eliminación incorrecto.");
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

