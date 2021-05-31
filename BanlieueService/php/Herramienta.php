<?php
	class Herramienta{
		private function obtMeses(){
			return array("01" => "Enero", "02" => "Febrero", "03" => "Marzo", "04" => "Abril", "05" => "Mayo", "06" => "Junio", "07" => "Julio", "08" => "Agosto", "09" => "Septiembre", "10" => "Octubre", "11" => "Noviembre", "12" => "Diciembre");
		}

		/*
		Redireccionamiento de página.
		Recibe: Entero (segundos) que se esperará para la recarga y el enlace de la nueva dirección.
		Devuelve: Redireccionamiento a la página especificada.
		*/
		public function redireccionar($tiempo, $enlace){
			?>
			<META HTTP-EQUIV="REFRESH" CONTENT="<?php echo $tiempo; ?>;URL='<?php echo $enlace; ?>'"/>
			<?php
		}


		/*
		Cálculo del número consecutivo.
		Recibe: Arreglo ORDENADO de una consulta a la base de datos cuyo PK sea un entero que debe "auto incrementarse".
		Devuelve: el número que falte en la cuenta consecutiva de la consulta, así se evitan huecos.
		*/
		public function consecutivo($arregloBDD, $PK){
			$consecutivo=0;
			$cualFalta=0;
			$numeroAsignado=0;
			$n=1;
			
			for($i=0; $i<sizeof($arregloBDD); $i++){
				if(($n+$i)==$arregloBDD[$i][$PK]){
					$consecutivo=0;
				}
				else{
					$consecutivo=1;
					$cualFalta= ($n+$i);
					$i= sizeof($arregloBDD)-1; //Equiv break
				}
			}
			
			if($consecutivo==1)
				$numeroAsignado= $cualFalta;
			else
				$numeroAsignado= ($n+$i);
			
			return $numeroAsignado;
		}


		/*
		Conversión de mes a número de mes.
		Recibe: Cadena del mes en cuestión.
		Devuelve: Número (cadena) correspondiente a dicho mes.
		*/
		public function mesAnumero($mes){
			return array_search(
				mb_convert_case($mes, MB_CASE_TITLE, "UTF-8"), 
				$this->obtMeses(), 
				true
			);
		}

		/*
		Conversión de número a mes.
		Recibe: Número (entero) correspondiente a dicho mes.
		Devuelve: Cadena del mes en cuestión.
		*/
		public function numeroAmes($numero){
			$numero<=9? $numCad="0".$numero : $numCad="".$numero;
			return ( $this->obtMeses() )[$numCad];
		}

		/*
		Conversión de stdObject a Array de PHP.
		Recibe: Objeto estándar.
		Devuelve: Array.
		*/
		public static function stdObj_A_Array($obj){
			$reaged = (array)$obj;
			foreach($reaged as $key => &$field){
				if(is_object($field))
					$field = stdObj_A_Array($field);
			}
			return $reaged;
		}
	}
?>