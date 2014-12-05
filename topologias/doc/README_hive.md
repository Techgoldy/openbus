#HIVE

Para poder crear y ejecutar métricas con el nuevo origen previamente hay que crear una tabla inicial en Hive que 
se comunicará con el HDFS.

Este sería el formato:
```
CREATE EXTERNAL TABLE [nombre_del_topico_kafka](
[campo] [tipo],[campo] [tipo],[campo] [tipo],etc)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001'
COLLECTION ITEMS TERMINATED BY ','
LOCATION '[directorio_hdfs]';
````

Donde:<sdsdsd>

- **nombre_del_topico_kafka**: Nombre del tópico kafka creado para el nuevo origen.
- **campo**: Nombre del campo del origen.
- **tipo**: Tipo del campo del origen, puede ser String, int, ARRAY<'Double'>(Sin comillas) o timestamp.
- **directorio_hdfs**: Directorio hdfs donde se almacenará la información relativa al nuevo origen.

[Configuración previa de Hive](https://github.com/Produban/openbus/blob/web_console/web_console/doc/README_hive.md)
