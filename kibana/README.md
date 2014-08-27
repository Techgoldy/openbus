#Openbus

El resultado de la ingestión y análisis de los datos será visualizado en dashboards de Kibana.
A continuación se detallarán los informes generados, indicando los indices, queries y filtros usados, así como la funcionalidad que aportan.

#Dashboards

1. Inicio

Antes de empezar a desarrollar los dashboards es necesario conocer la estructura de datos a explotar que reside en ElasticSearch.
Con el fin de simplificar este paso se ha generado un primer informe que lista los distintos index y types de nuestro ElasticSearch. Dicho informe puede ser usado como punto de partida para construir nuevos.

Los datos son los siguientes:

**index:** `_all`
**query:** * (Todos los datos)
**Paneles:**
  - Métricas disponibles: Área de texto con una descripción del informe.
  - Índices: Listado de los `index` definidos en la instancia que se use de ElasticSearch.
  - Métricas: Listado de los `_type` definidos en la instancia que se use de ElasticSearch.
  
  PANELES | CONTENIDO
  :--------|:-----------
  Métricas disponibles| Área de texto con una descripción del informe.
  Índices | Listado de los `index` definidos en la instancia que se use de ElasticSearch.
  Métricas | Listado de los `_type` definidos en la instancia que se use de ElasticSearch.

2. Envío entre servidores

Dashboard creado para ver el tráfico de correos entre los servidores de envío y recepción de los mismos.

Los datos son los siguientes:

**index:** `ob_src_postfix`
**query:** 

  QUERY | DESCRIPCIÓN
  :--------|:-----------
  _type:"envio_entre_servidores" | Cantidad y tamaño de envío de correos válidos entre dos servidores (Relación origen/destino)

**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Métricas disponibles| Área de texto con una descripción del informe.
  Métricas | Listado de los `_type` definidos en la instancia que se use de ElasticSearch.
  Stats | Conteo del total de registros que el informe procesa.
  Histograma | Gráfico en el cual se muestran los envíos en función del tiempo. Pinchando en el gráfico se puede filtrar el periodo de tiempo.
  Detalle | Tabla editable en la que se pueden ver todos los registros del periodo temporal seleccionado.
  Top Emisores | Lista de los 10 servidores que más correos han emitido.
  Top Receptores | Lista de los 10 servidores que más correos han recibido.
  
3. Métricas Ironport

Dashboard con la información de los informes Cisco para IronPort.

Los datos son los siguientes:

**index:** `ob_src_ironport`
**query:** 

  QUERY | DESCRIPCIÓN
  :--------|:-----------
  193.127.200.* AND _type:"total_mensajes"  | Todos los correos Entrantes
  _type:"total_mensajes" AND 180.* | Todos los correos salientes
  193.127.200.* AND _type:"filtro_reputacion" | Correos entrantes eliminados por filtro de reputación
  193.127.200.* AND _type:"receptor_no_valido" | Correos entrantes eliminados por filtro de receptor no válido
  193.127.200.* AND _type:"spam" | Correos entrantes eliminados por filtro de spam
  193.127.200.* AND _type:"virus" AND antivirus:"positive" | Correos entrantes eliminados por filtro de antivirus
  193.127.200.* AND _type:"content_filter" | Correos entrantes eliminados por filtro de contenido
  193.127.200.* AND _type:"marketing" | Correos entrantes por filtro de marketing
  193.127.200.* AND _type:"correos_limpios" | Correos entrantes limpios
  180.* AND _type:"filtro_reputacion" | Correos salientes eliminados por filtro de reputación
  180.* AND _type:"receptor_no_valido" | Correos salientes eliminados por filtro de receptor no válido
  180.* AND _type:"spam" | Correos salientes eliminados por filtro de spam
  180.* AND _type:"virus" AND antivirus:"positive" | Correos salientes eliminados por filtro de antivirus
  180.* AND _type:"content_filter" | Correos salientes eliminados por filtro de contenido
  180.* AND _type:"marketing" | Correos salientes por filtro de marketing
  180.* AND _type:"correos_limpios" | Correos salientes limpios

**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Entrante vs Saliente | Histograma en el que se muestra de manera superpuesta tanto el correo entrante como el correo saliente. Pinchando en el gráfico se puede filtrar el periodo de tiempo.
  Correo Entrante | Histograma y tabla en los que, para el correo ENTRANTE, se muestra la división en función del filtro de descarte o si ha sido correo aceptado.
  Correo Saliente | Histograma y tabla en los que, para el correo SALIENTE, se muestra la división en función del filtro de descarte o si ha sido correo aceptado.
  
4. Métricas ironport AWSTATS

Dashboard con la información mostrada para AWSTATS de IronPort.

Los datos son los siguientes:

**index:** `ob_src_ironport`
**query:** 

  QUERY | DESCRIPCIÓN
  :--------|:-----------
  _type:"primult_awstats" | Primer y último correo por mes/año
  _type:"correos_ok_awstats" | Cantidad y tamaño de los correos válidos por mes/año
  _type:"correos_notok_awstats" | Cantidad y tamaño de los correos no válidos por mes/año
  _type:"correos_ok_tiempo_awstats" | Cantidad y tamaño de los correos válidos por mes/año, día, hora y día de la semana
  _type:"correos_por_servidor_awstats" | Cantidad y tamaño de los correos válidos por servidor y mes/año
  _type:"top_receptores_awstats" | Cantidad y tamaño total de los correos por Receptor y mes/año
  _type:"top_emisores_awstats" | Cantidad y tamaño total de los correos por Emisor y mes/año
  _type:"errores_awstats" | Cantidad y tamaño de los correos erroneos por tipo de error (DSN) y mes/año

**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Primer y último correo del mes | Primer y último correo del mes seleccionado.
  Correos enviados correctamente | Correos (cantidad y tamaño) enviados correctamente para el mes seleccionado.
  Correos erroneos o rechazados | Correos (cantidad y tamaño) erroneos o rechazados para el mes seleccionado.
  Correos por día | Cantidad de correos enviados por día para el mes seleccionado.
  Correos por día de la semana | Cantidad de correos enviados por día de la semana para el mes seleccionado.
  Correos por hora | Cantidad de correos enviados por hora del día para el mes seleccionado.
  Correos enviados por servidor / Bytes enviados por servidor | Correos (cantidad y tamaño) enviados por servidor para el mes seleccionado.
  Emisores y Receptores - CORREOS | Histograma donde se muestra la cantidad de correos emitidos y recibidos.
  TOP EMISORES - CORREOS / TOP EMISORES - BYTES | Lista de los 10 emisores que más correos han enviado por cantidad y tamaño en sus correos del mes.
  TOP RECEPTORES - CORREOS / TOP RECEPTORES - BYTES | Lista de los 10 receptores que más correos han recibido por cantidad y tamaño en sus correos del mes.
  Correos erróneos o rechazados | Cantidad de correos erroneos o rechazados clasificados por DSN para el mes seleccionado.
  
5. Métricas Postfix

Dashboard con la información del informe AWSTATS para Postfix Mailing.

Los datos son los siguientes:

**index:** `ob_src_postfix`
**query:** 

  QUERY | DESCRIPCIÓN
  :--------|:-----------
  _type:"correos_ok" AND mes:7 | Cantidad y tamaño de los correos válidos por mes/año + filtro de mes=7
  _type:"correos_notok" AND mes:7 | Cantidad y tamaño de los correos no válidos por mes/año + filtro de mes=7
  _type:"primult_correo" AND mes:7 | Primer y último correo por mes/año + filtro de mes=7
  _type:"top_emisores" AND mes:7 | Cantidad y tamaño total de los correos por Emisor y mes/año + filtro de mes=7
  _type:"top_receptores" AND mes:7 | Cantidad y tamaño total de los correos por Receptor y mes/año + filtro de mes=7
  _type:"correos_diaok" AND mes:7 | Cantidad y tamaño de los correos válidos por día/mes/año + filtro de mes=7
  _type:"correos_ok" AND ano:2014 | Resumen de cantidad y tamaño de los correos válidos por mes + filtro de año=2014
  _type:"errores_smtp" AND mes:7 | Cantidad y tamaño de los correos erroneos por tipo de error (DSN) + filtro de mes=7
  
**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Correos enviados | Primer y último correo enviados en el periodo analizado. En este caso el mes de Julio.
  Correos enviados OK | Cantidad y tamaño total de los correos enviados en el mes seleccionado.
  Correos Rechazados | Cantidad y tamaño total de los correos rechazados o con error.
  Correos enviados por mes | Cantidad y tamaño total de los correos enviados, agrupado por mes para el año seleccionado.
  Correos por mes / Bytes enviados por mes | La misma infoemación que el panel anterior en vista de gráfico de barras.
  Correos por día | Cantidad de correos enviados por día para el mes seleccionado.
  Top Emisores | Lista de los 20 emisores que más correos han enviado por cantidad y tamaño en sus correos del mes.
  Top Receptores | Lista de los 20 receptores que más correos han recibido por cantidad y tamaño en sus correos del mes.
  Errores de envío | Cantidad,tamaño y porcentaje de correos erroneos o rechazados clasificados por DSN para el mes seleccionado.
  
6. Proxy - Peticiones por user, Agent y response

Dashboard con las peticiones proxy por usuario, navegador, respuesta y clasificado por hora/minuto para diferenciar si han sido realizadas dentro o fuera de horario laboral.


Los datos son los siguientes:

**index:** `ob_src_bluecoat`
**query:** 

  QUERY | DESCRIPCIÓN
  :--------|:-----------
  _type:"peticiones_user_agent_result" AND hora:(8* 9* 10* 11* 12* 13* 14* 15* 16* 17*) | Peticiones proxy realizadas por usuario, hora-minuto, navegador y resultado en horario laboral
  _type:"peticiones_user_agent_result" AND usercode:* AND hora:(0* 1\:* 2* 3* 4* 5* 6* 7* 18* 19* ) | Peticiones proxy realizadas por usuario, hora-minuto, navegador y resultado en horario NO laboral
  _type:"peticiones_user_agent_result" AND usercode:* | Peticiones proxy realizadas por usuario, hora-minuto, navegador y resultado con el userCode informado
  
**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Descargas fuera de horario laboral | Histograma en el que se muestran las descargas. El colo indica si es dentro de horario laboral (verde) o fuera de él (rojo)
  Por navegador | Ranking de los 20 Navegadores más usados en el periodo seleccionado
  Por resultado | Conteo de la cantidad de peticiones en función del resultado (OBSERVER/DENIED/PROXIED).
  Usuario con mayor cantidad de peticiones |  Ranking con los 10 usuarios que más peticiones han realizado en el periodo seleccionado.
  Por extensión | Ranking de las 20 extensiones más repetidas en las descargas de los usuarios para el periodo seleccionado.
  Detalle de la selección | Todos los registros de descargas para la selección realizada.
  
7. Proxy Bluecoat

Dashboard que muestra datos de proxy por usuario, dominio y tipos de descargas

Los datos son los siguientes:

**index:** `ob_src_bluecoat`
**query:** 

  QUERY | DESCRIPCIÓN
  :--------|:-----------
  * | Totalidad de los registros de peticiones proxy
  _type:"peticiones_usuario" | Peticiones proxy realizadas por usuario
  _type:"peticiones_dominio_usuario" | Peticiones proxy realizadas por usuario y dominio
  _type:"peticiones_usuario_maquina" | Peticiones proxy realizadas por usuario y máquina
  _type:"descarga_dominio" | Peticiones proxy de descarga por dominio
  _type:"descarga_ejecutables_usuario" AND -usercode:"-" AND ( ".exe" OR ".pl") | Peticiones proxy de descarga de ejecutables de tipo EXE y PL y userCode informado distinto de '-'
  _type:"descarga_comprimidos_usuario" AND -usercode:"-" | Peticiones proxy de descarga de archivos comprimidos y userCode informado distinto de '-'
  _type:"descarga_video_usuario" AND -usercode:"-" AND (".flv" OR ".avi" OR ".mp4") | Peticiones proxy de descarga de video (avi, flv o mp4) y userCode informado distinto de '-'

**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Conteo de peticiones | Histograma que refleja la cantidad de peticiones realizadas.
  Peticiones por usuario | Top de los 10 usuarios con más peticiones realizadas para el periodo y filtros seleccionados.
  Peticiones por usuario y dominio | Top de los 10 usuarios con más peticiones realizadas sobre un dominio para el periodo y filtros seleccionados.
  Peticiones por usuario y máquina | Top de los 10 usuarios con más peticiones realizadas por máquina para el periodo y filtros seleccionados.
  Descargas por dominio | Gráfica que muestra los 20 dominios sobre los cuales se han solicitado más descargas.
  Descargas | Histograma que refleja las descargas clasificadas en 3 tipos: Ejecutables, comprimidos y vídeos.
  Tendencias | Cuando se selecciona un periodo en el anterior histograma, calcula la tendencia de dicho periodo.
  Top descargas ejecutables / Detalles | Gráfico con el top 5 de descargas de ejecutables por usuario. En la tabla de detalle se podrán ver todos los registros asociados a dichas descargas.
  Top descargas comprimidos / Detalles | Gráfico con el top 5 de descargas de comprimidos por usuario. En la tabla de detalle se podrán ver todos los registros asociados a dichas descargas.
  Top descargas videos / Detalles | Gráfico con el top 5 de descargas de vídeo por usuario. En la tabla de detalle se podrán ver todos los registros asociados a dichas descargas.

8. Registros diarios

Dashboard que muestra el total de correos enviados, recibidos y erróneos, así como donde se han generado.

Los datos son los siguientes:

**index:** `prueba_orig`
**query:** 

  QUERY | DESCRIPCIÓN
  :--------|:-----------
  _type:"total" | Los registros del LOG original parseados
  _type:"total" AND dsn:(2.0.0 2.4.0 2.6.0) AND -amavisid:"null" | Registros referentes a correos enviados correctamente
  _type:"total" AND -dsn:(2.0.0 2.4.0 2.6.0 null) AND -amavisid:"null" | Registros referidos a correos enviados con error
  _type:"emisores" | Cantidad de correos enviados por usuario
  _type:"receptores" | Cantidad de correos recibidos por usuario

**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Registros de log | Histograma que refleja la cantidad de registros llegados en el log, los correos emitidos OK y los correos con error.
  Estadísticas | Muestra la cantidad de emisores y receptores de correos
  Emisores | Top 10 de los usuarios que más correos han emitido.
  Receptores | Top 10 de los usuarios que más correos han recibido.
  Localización | Geolocalización de la emisión de los mensajes.

9. Rangos Proxy

Dashboard para analizar la cantidad de descargas de ejecutables en función del tamaño de los ficheros descargados. Se hacen 3 rangos:
- Inferiores a 1Mb
- De 1 a 10 Mb
- Superiores a 10Mb

Los datos son los siguientes:

**index:** `ob_src_bluecoat`
**query:** 

  QUERY | DESCRIPCIÓN
  :--------|:-----------
  _type:"descarga_ejecutables_usuario" AND -usercode:"-" AND ( ".exe" OR ".pl") AND bytes:[0 TO 1048576] | Descargas de ejecutables por usuario inferiores a 1Mb
  _type:"descarga_ejecutables_usuario" AND -usercode:"-" AND ( ".exe" OR ".pl") AND bytes:[1048577 TO 10485770] | Descargas de ejecutables por usuario entre 1 y 10Mb
  _type:"descarga_ejecutables_usuario" AND -usercode:"-" AND ( ".exe" OR ".pl") AND bytes:[10485771 TO *] | Descargas de ejecutables por usuario superiores a 10Mb
**Paneles:**

  PANELES | CONTENIDO
  :--------|:----------- 
  Cantidad de descargas por rango | Histograma que muestra las descargas en función de los 3 rangos de tamaño de las descargas
  Cantidad de descargas | Resumen con la cantidad total y media de descargas por rango para la selección.
  Tamaño de las descargas | Histograma que muestra el tamaño total descargado por cada tipo de rango.
  Tamaño de descargas | Resumen con el tamaño total y media de descargas por rango para la selección.
  Ejecutables menores de 1Mb  por usuario y recurso | Total de registros de descargas inferiores a 1Mb para la selección.
  Ejecutables entre 1Mb y 10 Mb  por usuario y recurso | Total de registros de descargas entre 1Mb y 10Mb para la selección.
  Ejecutables entre 1Mb y 10 Mb  por usuario y recurso | Total de registros de descargas superiores a 10Mb para la selección.
  Top usuario descargas | Top 10 con los usuarias que más descargas han realizado.
  
  