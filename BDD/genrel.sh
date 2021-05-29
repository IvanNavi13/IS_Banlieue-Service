#!/bin/bash

cont=0

for i in "$@"; 
do
	let cont=cont+1
done

if [[ $cont -lt 3 ]] 
then
	echo "Se requieren más parámetros: sql imagen color"
elif [[ $cont -gt 3 ]] 
then
	echo "Demasiados parámetros, debe ser: sql imagen color"
else 
	if [[ $3 = "s" ]] 
	then
		sqlt-diagram -d=MySQL -o=$2 $1 --color
	elif [[ $3 = "n" ]] 
	then
		sqlt-diagram -d=MySQL -o=$2 $1
	else
		echo "Error en el parámetro de imagen a color: s=sí, n=no"
	fi
fi


