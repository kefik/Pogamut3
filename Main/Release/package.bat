cd ..\..\Utils\ProcessExecution
call mvn install -Dmaven.test.skip=true
cd ..\..\Main\Release
call mvn clean package