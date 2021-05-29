En esta carpata pondré todo lo que está relacionado con la BDD


Para quien esté usando Windows, este es un enlace a la versión de workbench
que tengo, la uso para generar el esquema relacional a partir del script "bdd.sql" (en este caso)
y quede mamalón, solo hay que descomprimir y ejecutar el archivo "MySQLWorkbench.exe", si les sirve
para todo lo demás, adelante XD.

https://drive.google.com/file/d/1Xf6yCPk4w3uveigAoe4a9XSN8sDDID5n/view?usp=sharing



Para quien esté utilizando Linux, encontré una herramienta que me agradó por lo rápida que es, 
además de que especifica más cómodamente cuáles son PK y FK, solo que no es tan estético, el diagrama 
no queda tan chido como workbench

sudo apt install sqlfairy

funciona con línea de comandos pero les dejo el .sh que hice para
usarlo más fácil, por ejemplo: 
./genrel.sh bdd.sql relacional.png s

significa que a partir del archivo sql generará el diagrama guardado como relacional.png, 's' significa
que sí (se exportará el daigrama a color) y 'n' que no (se exporta en B/N).

Si saben de otra cosa más chida en linux, que al role plox XD.
