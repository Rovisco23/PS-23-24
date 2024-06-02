@echo off
echo Building BackEnd
cd code/jvm
call gradlew build
cd ../js/front-end
echo Building FrontEnd
call npm install
echo Done Building!
pause