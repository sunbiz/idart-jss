#!/bin/bash
# Removes the Cell-Life Pharmacy Application from the System. 
# windows version: uninstall.bat

cd %INSTALL_PATH/Uninstaller
java -jar uninstaller.jar
rm $HOME/.local/share/applications/Cell-Life.desktop
rm $HOME/Desktop/Cell-Life.desktop