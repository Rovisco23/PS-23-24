@echo off
echo Starting Front-end
cd web-app/js/front-end
echo Building FrontEnd
call npm install
echo Done Building!
echo Starting FrontEnd Service...
start "" cmd /c "ng serve && pause"