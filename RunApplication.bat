echo Starting Services
cd code/jvm/build/libs
echo Starting BackEnd Service...
start "" cmd /c "java -jar sitediary-1.0.0-BETA.jar && pause"
cd ../../../js/front-end
echo Starting FrontEnd Service...
start "" cmd /c "ng serve && pause"