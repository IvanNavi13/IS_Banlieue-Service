//************CONTENIDO GENERAL DE ETIQUETA <HEAD>************//
//cargaContenido("./html/contgral/head.html", "cont-head");

//************CABECERA************//
//cargaContenido("./contgral/cabecera.html", "apgb-header");

//************BARRA DE NAVEGACIÓN************//
//cargaContenido("./contgral/barraNavegacion.html", "apgb-navigation");

//************PIE DE PÁGINA************//
//cargaContenido("./contgral/piePagina.html", "apgb-footer");

//************FORMULARIO PARA ENVÍO DE CORREO A APEG-B************//
//cargaContenido("./contgral/formContacto.html", "formContacto");



//Función para carga en etiquetas <source>.
//Recibe: Ubicación del archivo html a cargar e ID del elemento <source> correspondiente.
//Devuelve: Nada.
function cargaContenido(url, id){
	fetch(url)
	  .then(response => {
	  return response.text()
	})
	.then(data => {
	  document.getElementById(id).innerHTML= data
	});
}
