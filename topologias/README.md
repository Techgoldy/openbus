# Topologías estructuradas

El presente documento mostrará la generación de nuevas topologías estructuradas, así como sus funciones dentro del sistema.
En ellas se preprocesarán los datos de origen para darles un formato más adecuado al enfoque del cálculo de métricas.
A la hora de trabajar con logs de diversas fuentes el primer problema encontrado es la estructuración del contenido de los datos.

Habitualmente no resulta sencillo trabajar con el formato de registros de logs que nos llega directamente desde origen o es necesario enriquecer la información. Este es el motivo por el cual se van a definir topologías de estructuración en Storm. Se desplegará una topología por cada origen distinto.

Las topologías se desarrollarán en Storm Trident y constarán de 3 bloques principales: Parseo, enriquecimiento o filtrado de información y volcado de datos.

Para generarar una nueva topología son necesarias unas configraciones previas:

- [Configuración metadata](doc/README_metadata.md)
- [Configuración hive](doc/README_hive.md)
- [Configuración logstash](doc/README_logstash.md)

Una vez realizadas las configuraciones previas se detalla los pasos para poder generar nuevas topologías:

* Descargar el proyecto de topologías que contiene todo el paquete necesario para poder lanzar topologías a storm.
  Pinchar en este [enlace](https://github.com/Produban/openbus/tree/topologias) para ir al repositorio donde se encuentra el proyecto, una vez ahí existe un botón a la derecha para poder descargar el mismo.

* Una vez descargado el proyecto descomprimirlo en una carpeta y dentro de la ruta topologias/src/main/java/com/produban/openbus/topologies es donde hay que crear las dos clases del parseo y de definicion de
la topología para el nuevo origen. La clase parseador es la que utiliza las funciones de Trident que permite procesar cada registro del tópico Kafka y dividirlo en campos. La clase de definición de topologías es la que submitirá cada topología al cluster de Storm. Se podría tomar como ejemplo las clases del origen Radius: RadiusEntityParser.java y
OpenbusRadiusEntityTopology.java para la creación de las nuevas para el nuevo origen.

* Cuando las clases esté implementadas y compiladas hay que generar el archivo .jar con todo el paquete de clases de topología y parseo. Una forma de hacerlo sencillo es con apache maven.



* Es necesario crear un archivo .properties por cada nuevo origen con las siguientes propiedades:

--Configuración acceso HDFS
**HDFS_URL**=Url y puerto donde está el HDFS  
**HDFS_USER**=Usuario para escribir en HDFS  
**HDFS_OUTPUT_DIR**=Directorio donde se escribirá los datos parseados  
**HDFS_OUTPUT_FILENAME**=Prefijo del nombre del fichero de salida  
**HDFS_OUTPUT_FILE_EXTENSION**=Extensión del fichero de salida  
**INPUT_ORIGIN**=Se usa para especificar de dónde serán leídos los datos, las opciones posible son kafka y disco  
--Configuración de KAFKA   
**KAFKA_ZOOKEEPER_LIST**=Lista de url:puerto donde esta corriendo el Zookeeper  
**KAFAKA_BROKER_ID**=Id del broker Kafka  
**KAFKA_TOPIC**=Nombre del tópico Kafka del origen  
**KAFKA_FROM_BEGINNING**=Valor true o false para especificar que Kafka lea desde el principio o no  
--Configuración STORM  
**STORM_CLUSTER**=Indica si la topología sera desplegada en el cluster local o servidor, los valores posibles son local o server  
**STORM_BATCH_MILLIS**=Tiempo batch en milisegundos  
**STORM_NUM_WORKERS**=Número de workers  
**STORM_MAX_SPOUT_PENDING**=Número máximo de spouts pendientes  
**STORM_TOPOLOGY_NAME**=Nombre de la topología  
--Propiedades de la rotación  
**SIZE_ROTATION_UNIT**=Unidades de medida, los valores posibles son KB/MB/GB/TB  
**SIZE_ROTATION_VALUE**=Valor de la medida  
**TIME_ROTATION_UNIT**=Unidad de tiempo, los valores posibles son SECOND/MINUTE/HOUR/DAY  
**TIME_ROTATION_VALUE**=Valor de tiempo  
--Propiedades de la sincronización/escritura a HDFS  
**SYNC_MILLIS_PERIOD**=Periodo en milisegundos en el que los datos parseados serán escritos en disco   
--Configuración ElasticSearch  
**ELASTICSEARCH_HOST**=Url donde está instalado Elasticsearch  
**ELASTICSEARCH_PORT**=Puerto donde está instalado Elasticsearch  
**ELASTICSEARCH_NAME**=Nombre de elasticsearch   
**ELASTICSEARCH_CACHE_SEARCH**=Permite cachear las búsquedas en elasticsearch, los valores posibles son true o false  


* Para ejecutar la topología es necesario desplegar el archivo .jar generado en el entorno de Storm en el directorio de instalación del servidor donde se encuentre storm de esta manera:

`./bin/storm jar <JAR_FILE> <Main_Topology_class> <properties_file>`. Donde <JAR_FILE> es el archivo .jar generado anteriormente con todas las clases de topología y parseo. <Main_Topology_class> es la clase de definición de topología que se quiere desplegar. Y <properties_file> el archivo .properties creado con las propiedades para el nuevo origen.

Por ejemplo

`./bin/storm jar topologies-0.0.1-SNAPSHOT.jar com.produban.openbus.topologies.OpenbusRadiusEntityTopology radius.properties`


[Configuración de la topología de los otros orígenes](doc/README_topologia_conf.md)
