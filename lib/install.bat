@echo off
call mvn install:install-file -Dfile=org-netbeans-swing-tabcontrol.jar -DgroupId=org.netbeans -DartifactId=org-netbeans-swing-tabcontrol -Dversion=200804211638 -Dpackaging=jar
call mvn install:install-file -Dfile=org-openide-awt.jar -DgroupId=org.netbeans -DartifactId=org-openide-awt -Dversion=200804211638 -Dpackaging=jar
call mvn install:install-file -Dfile=org-openide-util.jar -DgroupId=org.netbeans -DartifactId=org-openide-util -Dversion=200804211638 -Dpackaging=jar
call mvn install:install-file -Dfile=org-openide-windows.jar -DgroupId=org.netbeans -DartifactId=org-openide-windows -Dversion=200804211638 -Dpackaging=jar
call mvn install:install-file -Dfile=rsyntaxtextarea.jar -DgroupId=com.fifesoft -DartifactId=rsyntaxtextarea -Dversion=2.6.1 -Dpackaging=jar
pause