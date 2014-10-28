# Metadata

Para la generación de la metadata del nuevo origen es necesario ejecutar varias sentencias sql en la base de datos MySql 
instalada en el servidor:

Un registro en la tabla origen_estructurado con el nuevo origen.
```
insert into origen_estructurado (is_kafka_online, kafka_topic, topology_name, version) values ([online],['nombre_del_topico'],['nombre_de_la_topología'], 1);
```
Donde:

- **online**: true o false si será un origen para tratar métricas online o batch.
- **nombre_del_topico**: Nombre del tópico kafka que se corresponderá con el creado anteriormente en este framework.
- **nombre_de_la_topología**: Nombre que tendrá la topología del origen a crear.


Tantos registros en la tabla campos_origen como campos tenga el nuevo origen.
```
insert into campos_origen (nombre_campo, tipo_campo, origen_estructurado, orden_en_tabla) values (['nombre_campo'], ['tipo_dato'], [foreign_key_creada_en_origen_estructurado], [orden]);
```

Donde:

- **nombre_campo**: El nombre del campo del nuevo origen.
- **tipo_dato**: El tipo de dato del campo del nuevo origen, puede ser string, decimal, array<double>, int o timestamp.
- **foreign_key_creada_en_origen_estructurado**: Será el índice de la tabla de origen_estructurado del elemento creado anteriormente.
- **orden**: Orden del campo a procesar.

        
[Configuración previa de la metadata](https://github.com/mmesa/readmes_openbus/blob/master/README_metadata_conf.md)
