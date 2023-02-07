@echo off
SET JAVA_PATH=C:\PapaWK\ProgramFiles\Java\j32\jdk1.7.0_51\bin\
SET INARG=
IF NOT "%1"=="" SET INARG=-wfl%1
"%JAVA_PATH%java" -classpath ./bin/Digitized/Digitized.jar;./libs/dans-dbf-lib-1.0.0-beta-09.jar -Xms712M -Xmx800M -Dfile.encoding=UTF-8 com.mwlib.app.TBuilderProject0 %INARG%
