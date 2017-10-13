call RecastDemo.exe %1

:LOOP
PSLIST RecastDemo >nul 2>&1
IF ERRORLEVEL 1 (
  GOTO CONTINUE
) ELSE (
  ECHO NavMesh is building...
  SLEEP 5
  GOTO LOOP
)

:CONTINUE

