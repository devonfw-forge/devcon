set DIST_ROOT=%1
echo %DIST_ROOT%

set TARGET_PATH=%2
echo %TARGET_PATH%

set M2_HOME=%DIST_ROOT%\software\maven
set M2_CONF=%DIST_ROOT%\conf\.m2\settings.xml

set MAVEN_OPTS=-Xmx512m -Duser.home=%DIST_ROOT%\conf
set MAVEN_HOME=%M2_HOME%

call cd %TARGET_PATH%
mvn cargo:run
