#!/usr/bin/env bash
cd client && npm run build-local
cd ..
rm -rf src/main/resources/public
cp -r client/dist src/main/resources/public
gradle buildJarAndCopyToDocker