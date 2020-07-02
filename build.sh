#!/bin/bash
./gradlew build
mv ./build/libs/CopywritingConverter-*.jar ./relese/
java -jar proguard.jar @proguard.pro
rm -rf ./relese/CopywritingConverter-*.jar
open ./relese
echo "-------------- build complete --------------"

