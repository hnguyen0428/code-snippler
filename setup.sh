#!/bin/bash


if [ -z $1 ]
then
	echo "Need to provide the base url as the first argument"
	exit 1
fi

if [ -z $ADMIN_KEY ]
then
	echo "No Admin Key specified in Bash Environment"
	exit 1
fi

BASE_URL=$1
ENDPOINT=/api/language

uri="$BASE_URL$ENDPOINT"

echo "Setting up the supported languages"

langs=(
	"Assembly x86" "Batchfile" "C" "C++" 
	"CoffeeScript" "C#" "CSS" "Dockerfile" 
	"EJS" "Fortran" "Go" "HTML" 
	"HTML Elixir" "HTML Ruby" "Haskell" "Java" 
	"JavaScript" "JSON" "JSX" "Julia" 
	"ReactJS" "ReactNative" "LaTex" "LESS" 
	"Lua" "Makefile" "Matlab" "MySQL" 
	"Objective C" "OCaml" "Pascal" "Perl" 
	"PHP" "Plain text" "PowerShell" "Python" 
	"R" "Ruby" "SASS" "Scala" 
	"SCSS" "sh" "SQL" "Swift" "TypeScript" 
	"Verilog" "XML" "Yaml"
)
types=(
	"language" "language" "language" "language"
	"language" "language" "language" "language"
	"language" "language" "language" "language"
	"language" "language" "language" "language"
	"language" "language" "language" "technology"
	"technology" "language" "language" "language"
	"language" "language" "language" "language"
	"language" "language" "language" "language"
	"language" "language" "language" "language"
	"language" "language" "language" "language"
	"language" "language" "language" "language"
	"language" "language" "language"
)
length=${#langs[@]}

i=0
while [ $i -lt $length ]
do
	curl --header "Admin-Key:$ADMIN_KEY" --data "name=${langs[$i]}&type=${types[$i]}" $uri &> setup.log
	i=`expr $i + 1`
done

