# Consola Web

##Requerimientos mínimos para su utilización en un entorno local

- Java 1.7
- Apache Maven 3
- Apache Tomcat 7
- MySql 5.6
 
Es conveniente que los directorios instalados de Java, Maven y MySql estén definidos en el path del sistema.    
La base de datos MySql debe estar precargada con una serie de scripts necesarios para poder utilizar la aplicación web.  
El proyecto web_console está subido al github en este [enlace](https://github.com/Produban/openbus/tree/web_console).    
Para poder compilarlo y desplegarlo hay que descargarlo y descomprimirlo en una carpeta.  
Es recomendable utilizar un IDE como Eclipse para el desarrollo, compilación y generación del archivo .war. 
Estos sería los pasos que habría que lanzar en orden:  

###MySql (Desde línea de comandos)
- `mysql -u root -p;`
- `create database openbus;` (Sólo la primera vez)
- Descargar el archivo [openbus.sql](https://github.com/Produban/openbus/blob/web_console/web_console/sql/openbus.sql) y cargar la base de datos con `mysql -u root -p openbus < openbus.sql`

###Compilación y despliegue
- Descomprimir el proyecto [web_console](https://github.com/Produban/openbus/tree/web_console) de github en una carpeta.
- En la ruta \openbus-web_console\web_console\src\main\resources\META-INF\spring se encuentran los archivos **environment.properties** donde se pueden cambiar las constantes necesarias para la conexión con hive y elasticseach y **database.properties** donde se pueden cambiar las constantes necesarias para la conexión con MySql.
- Si no se utiliza Eclipse  habría que situarse en el directorio descomprimido \openbus-web_console\web_console y lanzar las siguientes ordenes desde línea de comandos: `mvn clean install`. Esto generará el archivo *web_console.war* que podrá ser desplegado en el servidor de aplicaciones Tomcat, para ello copiar *web_console.war* en la carpeta webapps del directorio de instalación de Tomcat.
- Para arrancar el servidor Tomcat lanzar desde la carpeta *bin* de su directorio de instalación el comando `startup.sh` o *startup.bat*

