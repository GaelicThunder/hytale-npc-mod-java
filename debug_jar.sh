#!/bin/bash
if [ -f "libs/HytaleServer.jar" ]; then
    echo "Found libs/HytaleServer.jar"
    jar tf libs/HytaleServer.jar | grep -E "entity|component|ai|world" > jar_structure.txt
    echo "Dumped structure to jar_structure.txt"
else
    echo "Error: libs/HytaleServer.jar not found!"
    ls -R libs/
fi
