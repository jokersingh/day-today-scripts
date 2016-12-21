@echo on 
set JBOSS_HOME=C:\tools\jboss-eap-6.4.0\jboss-eap-6.4
call %JBOSS_HOME%\bin\jboss-cli.bat --commands="connect --controller=localhost:9990 --user=user1234 --password=user@1234,read-attribute server-state"
REM call %JBOSS_HOME%\bin\jboss-cli.bat --commands="connect --controller=localhost:9990 --user=user1234 --password=user@1234, deploy C:\tools\jenkins.war --force"
echo %ERRORLEVEL%
if %ERRORLEVEL% == 0 (call %JBOSS_HOME%\bin\jboss-cli.bat --commands="connect --controller=localhost:9990 --user=user1234 --password=user@1234, deploy C:\tools\jenkins.war --force") else (call standalone.bat)
