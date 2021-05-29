<?php
	include "./OperadorBDD.php";

	$bdd= new OperadorBDD();

	if($_SERVER["REQUEST_METHOD"]=="GET"){
		$json= stdObj_A_Array( json_decode( $_GET["json"] ) );

		$res= $bdd->selColumnaDeTablaEspecificado("correo, contrasena", "Persona", "correo", $json["correo"]) [0];

		if(sizeof($res)==0)
			responder(array("ini" => "El correo no pertenece a una cuenta registrada"));
		else
			if($json["contrasena"] != $res["contrasena"])
				responder(array("ini" => "La contraseña es incorrecta"));
			else
				responder(array("ini" => "acceso"));
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