#Openbus

Aplicación Big data fara la ingestión y análisis de cantidades masivas de eventos generados por una infraestructura IT de banca.

El objetivo de la topología Online de Openbus es poder procesar en tiempo real todos los registros de los orígenes parseados por la topología de parseo.

Para ello, la topología de parseo, además de volcar a HDFS, incorporará el identificador del origen en las tuplas de salida y las mandará a un tópico Kafka común para todos los orígenes de datos. Las tuplas resultado enviadas a dicho tópico serán un string con las siguientes características:
-	String que contiene todos los campos concatenados y separados por el carácter ‘\001’.
-	El primer campo (una vez se separen) simpre será el nombre del origen de datos.
-	Dicho nombre del origen debe estar definido en la metadata del sistema.


#Dependencias

Desplegar la arquitectura de Openbus en un entorno implica las siguientes dependencias:

- Hadoop 2.2.0 or higher version
- Storm 0.9.1 or higher version
- Kafka 0.8.8.1 or higher version
- Storm-HDFS plugin
- Storm-Kafka plugin
- Siddhi-Cep 3.0.0

Dado que Storm-HDFS ya contiene su propia dependencia con Hadoop (2.2.0 por defecto), si se va a usar una versión distinta en un cluster, se deberá EXCLUIR la dependencia del plugin de la siguiente manera:

```xml
<dependency>
  <groupId>com.github.ptgoetz</groupId>
  <artifactId>storm-hdfs</artifactId>
  <version>0.1.2</version>
  <exclusions>
		<exclusion>
			<groupId>org.apache.hadoop</groupId>
			<artifactId>hadoop-hdfs</artifactId>
		</exclusion>
		<exclusion>
			<groupId>org.apache.hadoop</groupId>
			<artifactId>hadoop-client</artifactId>
		</exclusion>
	</exclusions>
</dependency>
```

Una vez excluida la versión del Hadoop de Storm-HDFS habrá que definir la dependencia con la versión de Hadoop que se desea usar.


#Metadata y sus elementos

Para facilitar la definición y gestión de las métricas, mantendremos toda la información de las mismas en base de datos(MySQL).
Todos los objetos será accesibles mediante una serie de servición REST para su consulta, creación, moidficación y borrado.

- Metricas: Representa la unidad más global de toda la metadata. Una Metrica estará compuesta de los siguientes elementos:

	- onLineMetricName: Nombre de la métrica.
	- onLineMetricDesc: Texto descriptivo de la métrica
	- esIndex: Índice de ElasticSearch donde se almacenará el resultado.
	- esType: Obsoleto
	- esCamposId: Obsoleto??
	- fechaCreacion: Fecha de creación de la Mérica en la metadata
	- fechaUltModif: Fecha de última modificación de la Mérica en la metadata
	- usuarioCreacion: Usuario creador de la Métrica
	- usuarioModificacion: Usuario quemodificó la Métrica por última vez.
	
	- Streams: Stream de origen del SiddhiCep. Cada métrica tiene un Streram origen.
		- streamCepId: Identificador del Stream
		- streamFields: Campos que contendrá el Stream en formato: <CAMPO> <FORMATO>(,<CAMPO> <FORMATO>)*, donde los formatos son los soportados por Siddhi.
		- streamName: Nombre del Streram para Siddhi.
		- streamFinal: Composición de la sentencia de creación del Stream a partir del nombre y los campos.
		- origenEstructurado: Un Streram siempre será referente a un origen.
			- topologyName: Nombre de la topología de parseo origen.
			- kafkaTopic: Nombre del tópico Kafka de donde lee la topología de parseo
			- isKafkaOnline: Indica si la topología de parseo del origen deja resultado en el tópico kafka online.
			- CamposOrigen: Cada origen tiene una secuencia de campos (en orden determinado) tanto para HDFS como para el tópico Kafka online:
				- nombreCampo: Nombre del campo
				- tipoCampo: Formato HIVE del campo
				- ordenEnTabla: Orden (del 1 en adelante) en el que el campo fue definido en la tabla HIVE de destino. Ese orden es el mismo para la tupla enviada el tópico Kafka Online.
			
	- Tables: Permite almacenar información tanto de tablas en memoria como de tablas externas al CEP. De momento solo damos soporte a las tablas en memoria.
		- tableCepId: Identificador de la Tabla para el SiddhiCep
		- tableCepName: Nombre de la tabla en el SiddhiCep
		- tableCepFields: Campos que contendrá ta tabla en formato: <CAMPO> <FORMATO>(,<CAMPO> <FORMATO>)*, donde los formatos son los soportados por Siddhi.
		- tableCepFinal: Composición de la sentencia de creación de la Tabla a partir del nombre y los campos.
		
	- Queries:
		- queryCepId: Identificador de la query para el SiddhiCep.
		- queryName: Nombre representativo de la query
		- queryDefinition: Parte FROM y JOIN (junto con la cláusula ON) de la query
		- outputFieldUser: Parte de la proyección de la query (SELECT ...). Todos los campos de salida DEBEN ser renombrados con el operador AS (incluso si mantienen el nombre).
		- outputFieldNames: Listado de los campos del anterior campo (sacados del renombrado del AS)
		- outputStream: Cláusula de salida de datos ((insert into)|(update)|(delete)) <Stream o Tabla> (for (all-events|current-events|remove-events))*
		- groupBy: Cláusula del GROUP BY de la query. Este campo no es obligatorio.
		- queryFinal: Composición final de la query de Siddhi.
		- hasCallback: Boolean que indica si la query genera callback para enviar la salida a ElasticSearch
		- esId: Si la query tiene callback, es la serie de campos que hacen de ID en ElasticSearch por sise quiere sobrescritura por clave.
		- outputFieldFormat: Secuencia de campos de salida junto con su formato en ElasticSearch
		- esTTL: Se especifica el tiempo de vida de los documentos indexados. El formato es <numero><unidad> donde las unidadesson: s,m,d,... Por ejemplo 20m (20 minutos).
		- esType: Type de ElasticSearch donde se indexarán los documentos
		- queryOrder: Orden en que se crerará la query en el SiddhiCep
	


Elementos comunes a Métricas, Streams, Tables y Queries: Además de los campos con anterioridad, cada uno de los elementos tienen 3 datosque indican:

- Estado: Indica en que estado se encuentra el componente. En el siguiente punto explicaremos los posibles valores para el estado
	- code: Código de estado
	- descripción: Descripción del estado
- Versiones: Valor numérico que indica la versión del componente. Cada vez que se realice una modificación de algún elemento, se incrementará en 1 la versión.
- Errores: En el caso de que el elemento tenga un código de estado de error, este campo contendrá una descripción más detallada de dicho error.

#Códigos de estado

Para poder identificar en que estado se encuentra cada elemento se han definido los siguientes códigos de estado para los componentes:

##Streams

CÓDIGO | DESCRIPCIÓN
  :--------|:-----------
0 | Stream creado correctamente
1 | Stream a la espera de ser creado en Siddhi
2 | Stream con sintaxis incorrecta
3 | Error en la creación del Stream en Siddhi
4 | Stream a la espera de ser actualizado
5 | Stream a la espera de ser borrado
6 | Streram actualizado correctamente
7 | Error en la actualización del Stream
8 | Stream Borrado correctamente


##Tablas

CÓDIGO | DESCRIPCIÓN
:--------|:-----------
300 | Tabla creada correctamente
301 | Tabla a la espera de ser creada en Siddhi
302 | Tabla con sintaxis incorrecta
303 | Error en la creación de la Tabla en Siddhi
304 | Tabla a la espera de ser actualizada
305 | Tabla a la espera de ser borrada
306 | Tabla actualizada correctamente
307 | Error en la actualización de la Tabla
308 | Tabla Borrada correctamente

##Queries

CÓDIGO | DESCRIPCIÓN
:--------|:-----------
100	Query creada correctamente
101	Query a la espera de ser creada en Siddhi
102	Query con sintaxis incorrecta
103	Error en la creación de la query en Siddhi
104	Error en la creación del CALLBACK en Siddhi
105	Query a la espera de ser actualizado
106	Query a la espera de ser borrado
107	Query actualizada correctamente
108	Query borrada correctamente
109	Mapping de la query no ha podido ser definido tras la creación. 
112	Query con sintaxis incorrecta en la actualización
113	Error en la actualización de la query en Siddhi
114	Error en la actualización del CALLBACK en Siddhi
119	Mapping de la query no ha podido ser definido tras la actualización.

##Métricas

CÓDIGO | DESCRIPCIÓN
:--------|:-----------

200	Métrica creada correctamente
201	Métrica a la espera de ser creada en Siddhi
202	Error en la creación del Stream origen
203	Error en la creación de las queries
204	Error en la actualización de un Stream
205	Error en la actualización de una query
206	Error en el borrado de un Stream
207	Error en el borrado de una Query
208	Métrica actualizada con éxito
209	Métrica borrada con éxito
210	Métrica a la espera de ser actualizada
211	Métrica a la espera de ser borrada
212	Error en la creación de una tabla
213	Error en la actualización de una tabla
214	Error en el borrado de una tabla


#Elementos de la topología(Bolts)

La topología para métricas online consta de los siguientes bolts

- KafkaSpout/FileReader: Existe la opción de elegir el Spout para la entrada de datos entre leer de kafka o de fichero. Las tuplas generadas serán un único String que contendrá en su primera posición el origen y a continuación (separados por el caracter '\001') los valores para cada campo.

- Tuple2Streram: Con el objetivo de no sobrecargar el Bolt que lanzará el SiddhiCep, este Bolt tiene 2 funciones:

	- Leer la metadata: Mediante el uso de TickTuples, se activa una lectura de la metadata de manera periódica. Una vez se haya leido la metadata, por cada métrica, se enviará una tupla al siguiente Bolt con la siguiente estructura:
		- Tipo: String con el valor "METADATA"
		- Datos: Objeto MetricaOnLine
	- Leer tuplas de datos: Las tuplas recibidas de kafka(o fichero) se procesarán, separando los distintos campos. Por cada tupla, se identificará el origen y con él todas las métricas asociadas. Para cada métrica se enviará la tupla con los campos necesarios al Stream de origen de cada una. La tupla enviada quedaría como sigue:
		- Tipo: String con el valor "datos"
		- Datos: Lista donde el primer objeto es un String con el nombre del Streamde origen y el segundo es una lista con los valores a insertar en éste.
		
- SiddhiBolt: Este bolt contiene todo el procesado del SiddhiCep, tanto la creación de reglas como la ingestión y salida de los datos. Principalmente tiene 2 funciones:

	- Lectura de la metadata y generación de reglas: Al recibir una métrica la procesaremos con la siguiente secuencia de pasos:
		- Verificación de la versión de la métrica: Internamente el SiddhiCEP lleva la cuenta de los componentes (métricas, streams, tablas y queries) que ha procesado y su versión correspondiente.
		- Verificación de la versión del Stream y su estado: Si no hemos procesado esa versión, se crea/borra/modifica el Stream según indique el código de estado.
		- Verificación de las versiones de las tablas: Si no hemos procesado esa versión, se crean/borran/modifican las Tablas según indique el código de estadode cada una.
		- Verificación de las versiones de las queries: Si no hemos procesado esa versión, se crean/borran/modifican las Queries según indique el código de estadode cada una. Además de crear la Query y el callback para la salida a ElasticSearch, se analiza la estructura resultado de la query y se crea el mapping correspondiente.
		- Actualización/borrado de la métrica y envío del resultado a la metadata: Indicaremos si todos los componentes de la métrica han podido ser creados/modificados/borrados correctamente. En el caso de fallar alguna operación el resto se ignorarán, actualizando el estado de la Métrica al del primer ERROR ocurrido.
	- Ingestión de los datos: Cuando se recibe una tupla de datos se obtiene el nombre del Stream origen y, con él, el inputHandler con el que pasaremos los datos al SiddhiCEP


- ElasticSearchBolt: Bolt que recibe una tupla con los siguientes campos:
	- index: Índice de ElasticSearch donde se indexará el documento
	- type: Type de ElasticSearch donde se indexará el documento
	- id: Id con el que se guardará el documento. Si el id es nulo o vacío, se generará uno únuico aleatorio.
	- document: Documento a indexar. Será un String que contenga el JSON.
	
- EchoBolt: Nos permie ver la salida generada por el SiddhiBolt sin necesidad de tener ElasticSearch activo.

#Running examples (configuración)

Una vez tenemos la metadata lista para usar, hay que preparar el fichero de propiedades que configurará las piezas de la topología online:
Los campos requeridos son:


- INPUT_ORIGIN: Especifica el origen desde el que se lee la información. Se permiten dos valores:
	- kafka: La información se leerá desde un tópico kafka.
	- disco: La información se leerá desde un fichero de disco.
	
- INPUT_FILE: URL del fichero. Solamente será usado si "INPUT_ORIGIN=disco"

- KAFKA_ZOOKEEPER_LIST: Lista de servidores de zookeeper para kafka <ip:port>,<ip:port>,...

- KAFKA_TOPIC: Nombre del tópico desde el se leerán los datos

- METADATA_SINCRO_SECS: Frecuencia de actualización de la metadata por parte de la topología.(en segundos)
- GET_METADATA_SERVICE_URL: URL del servicio REST para obtener todas las métricas
- PUT_METRICA_SERVICE_URL: URL del servicio REST para actualizar una mérica en concreto
- DELETE_METRICA_SERVICE_URL: URL del servicio REST para borrar una métrica
- METADATA_FILE_JSON:  Fichero JSON que contendrá la metadata si la URL del servicio de lectura de metadata no está informado.

- STORM_MAX_SPOUT_PENDING: Número máximo de spouts pendientes
- STORM_TOPOLOGY_NAME: Nombre de la topología
- STORM_NUM_WORKERS: Número de workers
- STORM_CLUSTER: Si el valor es "local", la topología se desplegará en un LocalCluster

- ES_CLUSTER_NAME: Nombre del cluster ElasticSearch al que seescribiran los resultados
- ES_HOST: Host ElasticSearch
- ES_PORT: Puerto para ElasticSearch
- ECHO_OUTPUT: Si el valor es true, la salida del procesado siddhi saldrá por pantalla
- ELASTICSEARCH_OUTPUT: Si el valor es true, la salida del procesado siddhi saldrá se escribirá en Elasticsearch
