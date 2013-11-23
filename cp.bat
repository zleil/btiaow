cd btiao.base\bin
jar cfv ..\..\third\lib\com.btiao.base.jar *
cd ..\..

cd btiaoclient
jar cfv ..\btiao#client.war third\* user\* main\* *.png *.js *.html
cd ..

cd btiao.user
jar cfv ..\btiao#usrmgr.war WEB-INF\web.xml WEB-INF\classes\*
cd ..

cd btiao.product
jar cfv ..\btiao#product.war WEB-INF\web.xml WEB-INF\classes\*
cd ..

xcopy /Y third\lib\*.jar D:\dev\apache-tomcat\btiaolib\
xcopy /Y third\lib\*.jar D:\dev\apache-mirror\btiaolib\
xcopy /Y *.war D:\dev\apache-tomcat\myapps\
xcopy /Y *.war D:\dev\apache-mirror\myapps\

pause