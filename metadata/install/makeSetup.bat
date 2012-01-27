rem Build project into Iz-pack installer first.

"C:\Program Files\7-Zip\7z.exe" a -sfx files.7z @listfile.txt
copy /B 7zS.sfx + config.txt + files.7z iDART-install-win32-2.1.exe