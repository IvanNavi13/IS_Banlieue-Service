<?php
	class ConectorBDD{
		//Se usará PDO porque se necesita comunicar los datos de la app al php, y para ello se requiere el PDO	

		/*
		Para que la conexión funciones solamente hay que editar lo que está en mayúsculas con los datos de su servidor, por eso preferí usar 
		PDO en lugar de mysqli, solo ahy que cambiar aquí y todo fucniona.
		*/

		private $_connection;

		private static $dsn= "mysql:dbname=NOMBRE_DE_LA_BASE;host=IP_SERVIDOR_O_LOCALHOST";
		private static $usuario= "USUARIO";
		private static $contra= "CONTRASEÑA";

		public static function getDB(){
			try{
				$_connection= new PDO(self::$dsn, self::$usuario, self::$contra, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES latin1"));
			}catch(PDOException $ex){
				echo "Error de conexión ".$ex->getMessage();
				$_connection= null;
			}
		
			return $_connection;
		}
	}
?>
