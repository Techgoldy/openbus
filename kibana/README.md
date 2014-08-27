#Openbus

El resultado de la ingesti�n y an�lisis de los datos ser� visualizado en dashboards de Kibana.
A continuaci�n se detallar�n los informes generados, indicando los indices, queries y filtros usados, as� como la funcionalidad que aportan.

#Dashboards

1. Inicio

Antes de empezar a desarrollar los dashboards es necesario conocer la estructura de datos a explotar que reside en ElasticSearch.
Con el fin de simplificar este paso se ha generado un primer informe que lista los distintos index y types de nuestro ElasticSearch. Dicho informe puede ser usado como punto de partida para construir nuevos.

Los datos son los siguientes:

**index:** `_all`
**query:** * (Todos los datos)
**Paneles:**
  - M�tricas disponibles: �rea de texto con una descripci�n del informe.
  - �ndices: Listado de los `index` definidos en la instancia que se use de ElasticSearch.
  - M�tricas: Listado de los `_type` definidos en la instancia que se use de ElasticSearch.
  
  PANELES | CONTENIDO
  :--------|:-----------
  M�tricas disponibles| �rea de texto con una descripci�n del informe.
  �ndices | Listado de los `index` definidos en la instancia que se use de ElasticSearch.
  M�tricas | Listado de los `_type` definidos en la instancia que se use de ElasticSearch.

2. Env�o entre servidores

Dashboard creado para ver el tr�fico de correos entre los servidores de env�o y recepci�n de los mismos.

Los datos son los siguientes:

**index:** `ob_src_postfix`
**query:** 

  QUERY | DESCRIPCI�N
  :--------|:-----------
  _type:"envio_entre_servidores" | Cantidad y tama�o de env�o de correos v�lidos entre dos servidores (Relaci�n origen/destino)

**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  M�tricas disponibles| �rea de texto con una descripci�n del informe.
  M�tricas | Listado de los `_type` definidos en la instancia que se use de ElasticSearch.
  Stats | Conteo del total de registros que el informe procesa.
  Histograma | Gr�fico en el cual se muestran los env�os en funci�n del tiempo. Pinchando en el gr�fico se puede filtrar el periodo de tiempo.
  Detalle | Tabla editable en la que se pueden ver todos los registros del periodo temporal seleccionado.
  Top Emisores | Lista de los 10 servidores que m�s correos han emitido.
  Top Receptores | Lista de los 10 servidores que m�s correos han recibido.
  
3. M�tricas Ironport

Dashboard con la informaci�n de los informes Cisco para IronPort.

Los datos son los siguientes:

**index:** `ob_src_ironport`
**query:** 

  QUERY | DESCRIPCI�N
  :--------|:-----------
  193.127.200.* AND _type:"total_mensajes"  | Todos los correos Entrantes
  _type:"total_mensajes" AND 180.* | Todos los correos salientes
  193.127.200.* AND _type:"filtro_reputacion" | Correos entrantes eliminados por filtro de reputaci�n
  193.127.200.* AND _type:"receptor_no_valido" | Correos entrantes eliminados por filtro de receptor no v�lido
  193.127.200.* AND _type:"spam" | Correos entrantes eliminados por filtro de spam
  193.127.200.* AND _type:"virus" AND antivirus:"positive" | Correos entrantes eliminados por filtro de antivirus
  193.127.200.* AND _type:"content_filter" | Correos entrantes eliminados por filtro de contenido
  193.127.200.* AND _type:"marketing" | Correos entrantes por filtro de marketing
  193.127.200.* AND _type:"correos_limpios" | Correos entrantes limpios
  180.* AND _type:"filtro_reputacion" | Correos salientes eliminados por filtro de reputaci�n
  180.* AND _type:"receptor_no_valido" | Correos salientes eliminados por filtro de receptor no v�lido
  180.* AND _type:"spam" | Correos salientes eliminados por filtro de spam
  180.* AND _type:"virus" AND antivirus:"positive" | Correos salientes eliminados por filtro de antivirus
  180.* AND _type:"content_filter" | Correos salientes eliminados por filtro de contenido
  180.* AND _type:"marketing" | Correos salientes por filtro de marketing
  180.* AND _type:"correos_limpios" | Correos salientes limpios

**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Entrante vs Saliente | Histograma en el que se muestra de manera superpuesta tanto el correo entrante como el correo saliente. Pinchando en el gr�fico se puede filtrar el periodo de tiempo.
  Correo Entrante | Histograma y tabla en los que, para el correo ENTRANTE, se muestra la divisi�n en funci�n del filtro de descarte o si ha sido correo aceptado.
  Correo Saliente | Histograma y tabla en los que, para el correo SALIENTE, se muestra la divisi�n en funci�n del filtro de descarte o si ha sido correo aceptado.
  
4. M�tricas ironport AWSTATS

Dashboard con la informaci�n mostrada para AWSTATS de IronPort.

Los datos son los siguientes:

**index:** `ob_src_ironport`
**query:** 

  QUERY | DESCRIPCI�N
  :--------|:-----------
  _type:"primult_awstats" | Primer y �ltimo correo por mes/a�o
  _type:"correos_ok_awstats" | Cantidad y tama�o de los correos v�lidos por mes/a�o
  _type:"correos_notok_awstats" | Cantidad y tama�o de los correos no v�lidos por mes/a�o
  _type:"correos_ok_tiempo_awstats" | Cantidad y tama�o de los correos v�lidos por mes/a�o, d�a, hora y d�a de la semana
  _type:"correos_por_servidor_awstats" | Cantidad y tama�o de los correos v�lidos por servidor y mes/a�o
  _type:"top_receptores_awstats" | Cantidad y tama�o total de los correos por Receptor y mes/a�o
  _type:"top_emisores_awstats" | Cantidad y tama�o total de los correos por Emisor y mes/a�o
  _type:"errores_awstats" | Cantidad y tama�o de los correos erroneos por tipo de error (DSN) y mes/a�o

**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Primer y �ltimo correo del mes | Primer y �ltimo correo del mes seleccionado.
  Correos enviados correctamente | Correos (cantidad y tama�o) enviados correctamente para el mes seleccionado.
  Correos erroneos o rechazados | Correos (cantidad y tama�o) erroneos o rechazados para el mes seleccionado.
  Correos por d�a | Cantidad de correos enviados por d�a para el mes seleccionado.
  Correos por d�a de la semana | Cantidad de correos enviados por d�a de la semana para el mes seleccionado.
  Correos por hora | Cantidad de correos enviados por hora del d�a para el mes seleccionado.
  Correos enviados por servidor / Bytes enviados por servidor | Correos (cantidad y tama�o) enviados por servidor para el mes seleccionado.
  Emisores y Receptores - CORREOS | Histograma donde se muestra la cantidad de correos emitidos y recibidos.
  TOP EMISORES - CORREOS / TOP EMISORES - BYTES | Lista de los 10 emisores que m�s correos han enviado por cantidad y tama�o en sus correos del mes.
  TOP RECEPTORES - CORREOS / TOP RECEPTORES - BYTES | Lista de los 10 receptores que m�s correos han recibido por cantidad y tama�o en sus correos del mes.
  Correos err�neos o rechazados | Cantidad de correos erroneos o rechazados clasificados por DSN para el mes seleccionado.
  
5. M�tricas Postfix

Dashboard con la informaci�n del informe AWSTATS para Postfix Mailing.

Los datos son los siguientes:

**index:** `ob_src_postfix`
**query:** 

  QUERY | DESCRIPCI�N
  :--------|:-----------
  _type:"correos_ok" AND mes:7 | Cantidad y tama�o de los correos v�lidos por mes/a�o + filtro de mes=7
  _type:"correos_notok" AND mes:7 | Cantidad y tama�o de los correos no v�lidos por mes/a�o + filtro de mes=7
  _type:"primult_correo" AND mes:7 | Primer y �ltimo correo por mes/a�o + filtro de mes=7
  _type:"top_emisores" AND mes:7 | Cantidad y tama�o total de los correos por Emisor y mes/a�o + filtro de mes=7
  _type:"top_receptores" AND mes:7 | Cantidad y tama�o total de los correos por Receptor y mes/a�o + filtro de mes=7
  _type:"correos_diaok" AND mes:7 | Cantidad y tama�o de los correos v�lidos por d�a/mes/a�o + filtro de mes=7
  _type:"correos_ok" AND ano:2014 | Resumen de cantidad y tama�o de los correos v�lidos por mes + filtro de a�o=2014
  _type:"errores_smtp" AND mes:7 | Cantidad y tama�o de los correos erroneos por tipo de error (DSN) + filtro de mes=7
  
**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Correos enviados | Primer y �ltimo correo enviados en el periodo analizado. En este caso el mes de Julio.
  Correos enviados OK | Cantidad y tama�o total de los correos enviados en el mes seleccionado.
  Correos Rechazados | Cantidad y tama�o total de los correos rechazados o con error.
  Correos enviados por mes | Cantidad y tama�o total de los correos enviados, agrupado por mes para el a�o seleccionado.
  Correos por mes / Bytes enviados por mes | La misma infoemaci�n que el panel anterior en vista de gr�fico de barras.
  Correos por d�a | Cantidad de correos enviados por d�a para el mes seleccionado.
  Top Emisores | Lista de los 20 emisores que m�s correos han enviado por cantidad y tama�o en sus correos del mes.
  Top Receptores | Lista de los 20 receptores que m�s correos han recibido por cantidad y tama�o en sus correos del mes.
  Errores de env�o | Cantidad,tama�o y porcentaje de correos erroneos o rechazados clasificados por DSN para el mes seleccionado.
  
6. Proxy - Peticiones por user, Agent y response

Dashboard con las peticiones proxy por usuario, navegador, respuesta y clasificado por hora/minuto para diferenciar si han sido realizadas dentro o fuera de horario laboral.


Los datos son los siguientes:

**index:** `ob_src_bluecoat`
**query:** 

  QUERY | DESCRIPCI�N
  :--------|:-----------
  _type:"peticiones_user_agent_result" AND hora:(8* 9* 10* 11* 12* 13* 14* 15* 16* 17*) | Peticiones proxy realizadas por usuario, hora-minuto, navegador y resultado en horario laboral
  _type:"peticiones_user_agent_result" AND usercode:* AND hora:(0* 1\:* 2* 3* 4* 5* 6* 7* 18* 19* ) | Peticiones proxy realizadas por usuario, hora-minuto, navegador y resultado en horario NO laboral
  _type:"peticiones_user_agent_result" AND usercode:* | Peticiones proxy realizadas por usuario, hora-minuto, navegador y resultado con el userCode informado
  
**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Descargas fuera de horario laboral | Histograma en el que se muestran las descargas. El colo indica si es dentro de horario laboral (verde) o fuera de �l (rojo)
  Por navegador | Ranking de los 20 Navegadores m�s usados en el periodo seleccionado
  Por resultado | Conteo de la cantidad de peticiones en funci�n del resultado (OBSERVER/DENIED/PROXIED).
  Usuario con mayor cantidad de peticiones |  Ranking con los 10 usuarios que m�s peticiones han realizado en el periodo seleccionado.
  Por extensi�n | Ranking de las 20 extensiones m�s repetidas en las descargas de los usuarios para el periodo seleccionado.
  Detalle de la selecci�n | Todos los registros de descargas para la selecci�n realizada.
  
7. Proxy Bluecoat

Dashboard que muestra datos de proxy por usuario, dominio y tipos de descargas

Los datos son los siguientes:

**index:** `ob_src_bluecoat`
**query:** 

  QUERY | DESCRIPCI�N
  :--------|:-----------
  * | Totalidad de los registros de peticiones proxy
  _type:"peticiones_usuario" | Peticiones proxy realizadas por usuario
  _type:"peticiones_dominio_usuario" | Peticiones proxy realizadas por usuario y dominio
  _type:"peticiones_usuario_maquina" | Peticiones proxy realizadas por usuario y m�quina
  _type:"descarga_dominio" | Peticiones proxy de descarga por dominio
  _type:"descarga_ejecutables_usuario" AND -usercode:"-" AND ( ".exe" OR ".pl") | Peticiones proxy de descarga de ejecutables de tipo EXE y PL y userCode informado distinto de '-'
  _type:"descarga_comprimidos_usuario" AND -usercode:"-" | Peticiones proxy de descarga de archivos comprimidos y userCode informado distinto de '-'
  _type:"descarga_video_usuario" AND -usercode:"-" AND (".flv" OR ".avi" OR ".mp4") | Peticiones proxy de descarga de video (avi, flv o mp4) y userCode informado distinto de '-'

**Paneles:**

  PANELES | CONTENIDO
  :--------|:-----------
  Conteo de peticiones | Histograma que refleja la cantidad de peticiones realizadas.
  Peticiones por usuario | Top de los 10 usuarios con m�s peticiones realizadas para el periodo y filtros seleccionados.
  Peticiones por usuario y dominio | Top de los 10 usuarios con m�s peticiones realizadas sobre un dominio para el periodo y filtros seleccionados.
  Peticiones por usuario y m�quina | Top de los 10 usuarios con m�s peticiones realizadas por m�quina para el periodo y filtros seleccionados.
  Descargas por dominio | Gr�fica que muestra los 20 dominios sobre los cuales se han solicitado m�s descargas.
  Descargas | Histograma que refleja las descargas clasificadas en 3 tipos: Ejecutables, comprimidos y v�deos.
  Tendencias | Cuando se selecciona un periodo en el anterior histograma, calcula la tendencia de dicho periodo.
  Top descargas ejecutables / Detalles | Gr�fico con el top 5 de descargas de ejecutables por usuario. En la tabla de detalle se podr�n ver todos los registros asociados a dichas descargas.
  Top descargas comprimidos / Detalles | Gr�fico con el top 5 de descargas de comprimidos por usuario. En la tabla de detalle se podr�n ver todos los registros asociados a dichas descargas.
  Top descargas videos / Detalles | Gr�fico con el top 5 de descargas de v�deo por usuario. En la tabla de detalle se podr�n ver todos los registros asociados a dichas descargas.

8. Registros diarios

Dashboard que muestra el total de correos enviados, recibidos y err�neos, as� como donde se han generado.

Los datos son los siguientes:

**index:** `prueba_orig`
**query:** 

  QUERY | DESCRIPCI�N
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
  Estad�sticas | Muestra la cantidad de emisores y receptores de correos
  Emisores | Top 10 de los usuarios que m�s correos han emitido.
  Receptores | Top 10 de los usuarios que m�s correos han recibido.
  Localizaci�n | Geolocalizaci�n de la emisi�n de los mensajes.

9. Rangos Proxy

Dashboard para analizar la cantidad de descargas de ejecutables en funci�n del tama�o de los ficheros descargados. Se hacen 3 rangos:
- Inferiores a 1Mb
- De 1 a 10 Mb
- Superiores a 10Mb

Los datos son los siguientes:

**index:** `ob_src_bluecoat`
**query:** 

  QUERY | DESCRIPCI�N
  :--------|:-----------
  _type:"descarga_ejecutables_usuario" AND -usercode:"-" AND ( ".exe" OR ".pl") AND bytes:[0 TO 1048576] | Descargas de ejecutables por usuario inferiores a 1Mb
  _type:"descarga_ejecutables_usuario" AND -usercode:"-" AND ( ".exe" OR ".pl") AND bytes:[1048577 TO 10485770] | Descargas de ejecutables por usuario entre 1 y 10Mb
  _type:"descarga_ejecutables_usuario" AND -usercode:"-" AND ( ".exe" OR ".pl") AND bytes:[10485771 TO *] | Descargas de ejecutables por usuario superiores a 10Mb
**Paneles:**

  PANELES | CONTENIDO
  :--------|:----------- 
  Cantidad de descargas por rango | Histograma que muestra las descargas en funci�n de los 3 rangos de tama�o de las descargas
  Cantidad de descargas | Resumen con la cantidad total y media de descargas por rango para la selecci�n.
  Tama�o de las descargas | Histograma que muestra el tama�o total descargado por cada tipo de rango.
  Tama�o de descargas | Resumen con el tama�o total y media de descargas por rango para la selecci�n.
  Ejecutables menores de 1Mb  por usuario y recurso | Total de registros de descargas inferiores a 1Mb para la selecci�n.
  Ejecutables entre 1Mb y 10 Mb  por usuario y recurso | Total de registros de descargas entre 1Mb y 10Mb para la selecci�n.
  Ejecutables entre 1Mb y 10 Mb  por usuario y recurso | Total de registros de descargas superiores a 10Mb para la selecci�n.
  Top usuario descargas | Top 10 con los usuarias que m�s descargas han realizado.
  
  