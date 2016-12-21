set NOPAUSE=true
set JBOSS_HOME=C:\tools\jboss-eap-6.4.0\jboss-eap-6.4
rem Do something and wait for deployment completion
call:wait_for_jboss
rem Do something after deployment is done

goto:eof

:wait_for_jboss
timeout /t 5
call %JBOSS_HOME%\bin\jboss-cli.bat -c --command="deployment-info --name=C:\tools\jenkins.war" | findstr "OK"
if %ERRORLEVEL% neq 0 goto wait_for_jboss
goto:eof