# Origen Bluecoat

####1. Métrica *peticiones_usuario* : Peticiones proxy por usuario

- **Origen de datos:** `ob_src_bluecoat`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,userCode STRING,eventTimeStamp timestamp,peticiones BIGINT`
- **Query Select:** `SELECT CONCAT(eventTimeStamp,"-",userCode) as ID,userCode,eventTimeStamp,Count(1) as peticiones`
- **Query From:** `FROM ob_src_bluecoat`
- **Query Where:** `WHERE eventTimeStamp>"2014-07-29 10:00:00" and eventTimeStamp<="2014-07-30 10:00:00"
GROUP BY eventTimeStamp,usercode`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE peticiones_usuario SELECT CONCAT(eventTimeStamp,"-",userCode) as ID,userCode,eventTimeStamp,Count(1) as peticiones FROM ob_src_bluecoat GROUP BY eventTimeStamp,usercode;`

***

####2. Métrica *peticiones_dominio_usuario* : Peticiones de dominios por usuario

- **Origen de datos:** `ob_src_bluecoat`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,usuario_dominio STRING,userCode STRING,requestDomain STRING,eventTimeStamp timestamp,peticiones BIGINT`
- **Query Select:** `SELECT CONCAT(eventTimeStamp,"-",requestDomain,"-",userCode) as ID,CONCAT(requestDomain,"-",usercode) as  usuario_dominio,userCode,requestDomain,eventTimeStamp,Count(1) as peticiones`
- **Query From:** `FROM ob_src_bluecoat`
- **Query Where:** `WHERE eventTimeStamp>"2014-07-26 22:00:00" AND eventTimeStamp <="2014-07-27 10:00:00" GROUP BY eventTimeStamp,usercode,requestDomain`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE peticiones_dominio_usuario SELECT CONCAT(eventTimeStamp,"-",requestDomain,"-",userCode) as ID,CONCAT(requestDomain,"-",usercode) as usuario_dominio,userCode,requestDomain,eventTimeStamp,Count(1) as peticiones FROM ob_src_bluecoat GROUP BY eventTimeStamp,usercode,requestDomain;`

***

####3. Métrica *peticiones_usuario_maquina* : Cantidad de peticiones por usuario y máquina

- **Origen de datos:** `ob_src_bluecoat`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,usuario_maquina STRING,userCode STRING,clientIP STRING,eventTimeStamp timestamp,peticiones BIGINT`
- **Query Select:** `SELECT CONCAT(eventTimeStamp,"-",clientIP,"-",userCode) as ID,CONCAT(clientIP,"-",usercode) as usuario_maquina,userCode,clientIP,eventTimeStamp,Count(1) as peticiones`
- **Query From:** `FROM ob_src_bluecoat`
- **Query Where:** `WHERE eventtimestamp>"2014-07-25 10:00:00" and eventtimestamp<="2014-07-25 22:00:00" GROUP BY eventTimeStamp,usercode,clientIP`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE peticiones_usuario_maquina SELECT CONCAT(eventTimeStamp,"-",clientIP,"-",userCode) as ID,
CONCAT(clientIP,"-",usercode) as usuario_maquina,userCode,clientIP,eventTimeStamp,Count(1) as peticiones FROM ob_src_bluecoat
GROUP BY eventTimeStamp,usercode,clientIP;`

***

####4. Métrica *descarga_dominio* : Cantidad de descargas por dominio

- **Origen de datos:** `ob_src_bluecoat`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,requestDomain STRING,eventTimeStamp timestamp,descargas BIGINT`
- **Query Select:** `SELECT CONCAT(eventTimeStamp,"-",requestDomain) as ID,requestDomain,eventTimeStamp,Count(1) as descargas`
- **Query From:** `FROM ob_src_bluecoat`
- **Query Where:** `WHERE requestURIExtension not in ("html", "htm", "-", "php") AND eventtimestamp>"2014-07-25 10:00:00" AND eventtimestamp<="2014-07-25 22:00:00" GROUP BY eventTimeStamp,requestDomain`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE descarga_dominio SELECT CONCAT(eventTimeStamp,"-",requestDomain) as ID,requestDomain,
eventTimeStamp,Count(1) as descargas FROM ob_src_bluecoat WHERE requestURIExtension not in ("html", "htm", "-", "php") GROUP BY eventTimeStamp,requestDomain;`

***

####5. Métrica *descarga_ejecutables_usuario* : Descargas de archivos ejecutables por usuario

- **Origen de datos:** `ob_src_bluecoat`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,usuario_descarga STRING,userCode STRING,dominio STRING,recurso STRING,eventTimeStamp timestamp,
descargas BIGINT,bytes BIGINT`
- **Query Select:** `SELECT CONCAT(eventTimeStamp,"-",requestPath,"-",userCode) as ID,CONCAT(requestPath,"-",userCode) as usuario_descarga,userCode,requestDomain as dominio,requestPath as recurso,eventTimeStamp,Count(1) as descargas,SUM(scBytes) as bytes`
- **Query From:** `FROM ob_src_bluecoat`
- **Query Where:** `WHERE requestURIExtension in ("exe", "pl", "js") OR contentType in("application/x-msdos-program","application/x-javascript","application/javascript")GROUP BY eventTimeStamp,requestPath,userCode,requestDomain`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE descarga_ejecutables_usuario SELECT CONCAT(eventTimeStamp,"-",requestPath,"-",userCode) as ID,CONCAT(requestPath,"-",userCode) as usuario_descarga,userCode,requestDomain as dominio,requestPath as recurso,eventTimeStamp,
Count(1) as descargas,SUM(scBytes) as bytes FROM ob_src_bluecoat WHERE requestURIExtension in ("exe", "pl", "js") OR
contentType in ("application/x-msdos-program","application/x-javascript","application/javascript")GROUP BY eventTimeStamp,requestPath,userCode,requestDomain;`

***

####6. Métrica *descarga_comprimidos_usuario* : Descarga de archivos comprimidos por usuario

- **Origen de datos:** `ob_src_bluecoat`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,usuario_descarga STRING,userCode STRING,dominio STRING,recurso STRING,eventTimeStamp timestamp,
descargas BIGINT,bytes BIGINT`
- **Query Select:** `SELECT CONCAT(eventTimeStamp,"-",requestPath,"-",userCode) as ID,CONCAT(requestPath,"-",userCode) as usuario_descarga,userCode,requestDomain as dominio,requestPath as recurso,eventTimeStamp,Count(1) as descargas,SUM(scBytes) as bytes`
- **Query From:** `FROM ob_src_bluecoat`
- **Query Where:** `WHERE requestURIExtension in ("rar", "zip", "jar", "tar", "gz", "Z", "tgz", "gzip", "ace") and eventtimestamp>"2014-07-25 11:00:00" GROUP BY eventTimeStamp,requestPath,userCode,requestDomain`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE descarga_comprimidos_usuario SELECT CONCAT(eventTimeStamp,"-",requestPath,"-",userCode) as ID,CONCAT(requestPath,"-",userCode) as usuario_descarga,userCode,requestDomain as dominio,requestPath as recurso,eventTimeStamp,Count(1) as descargas,SUM(scBytes) as bytes FROM ob_src_bluecoat WHERE requestURIExtension in ("rar", "zip", "jar", "tar", "gz", "Z", "tgz", "gzip", "ace")GROUP BY eventTimeStamp,requestPath,userCode,requestDomain;`

***

####7. Métrica *descarga_video_usuario* : Descargas de archivos de vídeo

- **Origen de datos:** `ob_src_bluecoat`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,usuario_descarga STRING,userCode STRING,dominio STRING,recurso STRING,eventTimeStamp timestamp,
descargas BIGINT,bytes BIGINT`
- **Query Select:** `SELECT CONCAT(eventTimeStamp,"-",requestPath,"-",userCode) as ID,CONCAT(requestPath,"-",userCode) as usuario_descarga,userCode,requestDomain as dominio,requestPath as recurso,eventTimeStamp,Count(1) as descargas,SUM(scBytes) as bytes`
- **Query From:** `FROM ob_src_bluecoat`
- **Query Where:** `WHERE requestURIExtension in ("flv", "mpeg", "avi", "f4m", "bootstrap", "mp4")  OR contentType in ("video/mp4","video/f4f","video/x-flv","video/f4m","video/abst","video/webm","video/x-ms-asf") and eventtimestamp>"2014-07-25 11:00:00" GROUP BY eventTimeStamp,requestPath,userCode,requestDomain`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE descarga_video_usuario SELECT CONCAT(eventTimeStamp,"-",requestPath,"-",userCode) as ID,
CONCAT(requestPath,"-",userCode) as usuario_descarga,userCode,requestDomain as dominio,requestPath as recurso,eventTimeStamp,
Count(1) as descargas,SUM(scBytes) as bytes FROM ob_src_bluecoat WHERE requestURIExtension in ("flv", "mpeg", "avi", "f4m", "bootstrap", "mp4")  OR contentType in("video/mp4","video/f4f","video/x-flv","video/f4m","video/abst","video/webm","video/x-ms-asf") GROUP BY eventTimeStamp,requestPath,userCode,requestDomain;`

***

####8. Métrica *peticiones_user_agent_result_geo* : Peticiones por usuario, navegador, resultado y geolocalización

- **Origen de datos:** `ob_src_bluecoat`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
userCode STRING,
filterResult STRING,
userAgent String,
dominio STRING,
recurso STRING,
requestURIExtension STRING,
eventTimeStamp timestamp,
hora STRING,
peticiones BIGINT,
coords ARRAY<DOUBLE>,
city STRING,
country STRING
`
- **Query Select:** `SELECT
CONCAT(eventTimeStamp,"-",userCode,"-",requestDomain,"-",requestPath) as ID,
userCode,
filterResult,
userAgent,
requestDomain as dominio,
requestPath as recurso,
requestURIExtension,
eventTimeStamp,
CONCAT(HOUR(eventTimeStamp),":",MINUTE(eventtimestamp)) as hora,
Count(1) as peticiones,
coords,
city,
country`
- **Query From:** `FROM ob_src_bluecoat`
- **Query Where:** `WHERE eventTimestamp>"2014-09-29 07:00:00" and  eventTimestamp<="2014-09-29 15:00:00" and filterResult in ("OBSERVED","DENIED","PROXIED")
GROUP BY eventTimeStamp,usercode,filterResult,userAgent,requestDomain,requestPath,requestURIExtension,coords,
city,
country`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT OVERWRITE TABLE peticiones_user_agent_result_geo SELECT
CONCAT(eventTimeStamp,"-",userCode,"-",requestDomain,"-",requestPath) as ID,
userCode,
filterResult,
userAgent,
requestDomain as dominio,
requestPath as recurso,
requestURIExtension,
eventTimeStamp,
CONCAT(HOUR(eventTimeStamp),":",MINUTE(eventtimestamp)) as hora,
Count(1) as peticiones,
coords,
city,
country FROM ob_src_bluecoat WHERE eventTimestamp>"2014-09-29 07:00:00" and  eventTimestamp<="2014-09-29 15:00:00" and filterResult in ("OBSERVED","DENIED","PROXIED")
GROUP BY eventTimeStamp,usercode,filterResult,userAgent,requestDomain,requestPath,requestURIExtension,coords,
city,
countryP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID
VDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID;`

