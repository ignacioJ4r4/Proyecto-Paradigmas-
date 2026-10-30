@echo off
REM Compila y ejecuta Horda Nocturna (requiere JDK 11 o superior en el PATH)
cd /d "%~dp0"
if exist out rmdir /s /q out
javac -encoding UTF-8 -d out -sourcepath src src\hordanocturna\Main.java
if errorlevel 1 (
    echo Error al compilar. Verifica que tienes el JDK instalado: javac -version
    pause
    exit /b 1
)
java -cp out hordanocturna.Main
