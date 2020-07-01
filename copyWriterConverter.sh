#!/bin/bash

excelPath="$1"
xmlPathth="$2"

if  [ ! -n "$excelPath" ] ;then
    echo "请输入文档路径"
    exit
fi

if  [ ! -n "$xmlPathth" ] ;then
    echo "请输入输出路径：例如./app/src/main/res"
    exit
fi

java -jar CopywritingConverter-1.0.jar "$1", "$2"