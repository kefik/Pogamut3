FOR /f %%f in ('dir /b "%1\Maps"') DO call export-one.bat "%1" "%%f"