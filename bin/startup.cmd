@echo off

setlocal

call "%~dp0checkEnv.cmd"

set MAIN=com.thinkerwolf.gamer.test.NettyServerTests

set JAVA_OPT=%JAVA_OPT% -server -Xms1g -Xmx1g -Xmn512m
set JAVA_OPT=%JAVA_OPT% -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercent=25 -XX:SurvivorRatio=8

set JWDP_OPT=%JWDP_OPT% -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8787

call %JAVA% %JAVA_OPT% %JWDP_OPT% -cp "%CLASSPATH%" %MAIN% %*

endlocal

pause