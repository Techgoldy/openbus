# Origen Ironport

####1. Métrica *total_mensajes* : Total de mensajes de ironport

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
INTERFACEIP STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",MID) as ID,
eventTimeStamp,
REL.ICID,
MID,
INTERFACEIP
FROM(
select ICID,MID `
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE ICID is not NULL AND mid is not NULL AND RID is NULL and mailfrom ="null"
GROUP BY ICID,MID) REL
LEFT JOIN
(
SELECT 
ICID
,eventTimeStamp,INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
)ENTR
ON ENTR.ICID=REL.ICID
GROUP BY eventTimeStamp,MID,REL.ICID,INTERFACEIP`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE total_mensajes
SELECT
CONCAT(REL.ICID,"-",MID) as ID,
eventTimeStamp,
REL.ICID,
MID,
INTERFACEIP
FROM(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL and mailfrom ="null"
GROUP BY ICID,MID) REL
LEFT JOIN
(
SELECT 
ICID
,eventTimeStamp,INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
)ENTR
ON ENTR.ICID=REL.ICID
GROUP BY eventTimeStamp,MID,REL.ICID,INTERFACEIP;`

***

####2. Métrica *filtro_reputacion* : Correos rechazados por Filtro de Reputación

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
REPUTATION STRING,
INTERFACEIP STRING`
- **Query Select:** `SELECT
DATOS.ICID as ID,
eventTimeStamp,
DATOS.ICID,
REPUTATION,
INTERFACEIP
FROM 
(
SELECT 
REPUTATION,
eventTimeStamp,
ICID`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE REPUTATION like "%TCPREFUSE%" OR REPUTATION like "%REJECTED%") DATOS
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=DATOS.ICID`

- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE filtro_reputacion
SELECT
DATOS.ICID as ID,
eventTimeStamp,
DATOS.ICID,
REPUTATION,
INTERFACEIP
FROM 
(
SELECT 
REPUTATION,
eventTimeStamp,
ICID
FROM ob_src_ironport
WHERE REPUTATION like "%TCPREFUSE%" OR REPUTATION like "%REJECTED%") DATOS
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=DATOS.ICID`

***

####3. Métrica *receptor_no_valido* : Correos descartados por Receptor no válido

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
DSNBOUNCE STRING,
BOUNCEDESC STRING,
INTERFACEIP STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
DSNBOUNCE,
BOUNCEDESC,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,
DSNBOUNCE,
BOUNCEDESC`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where BOUNCEDESC like "%nvalid recipient%" AND DSNBOUNCE ="5.1.0") BOUNCE
 LEFT JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=BOUNCE.MID
LEFT JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE receptor_no_valido
SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
DSNBOUNCE,
BOUNCEDESC,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,
DSNBOUNCE,
BOUNCEDESC 
from ob_Src_ironport where BOUNCEDESC like "%nvalid recipient%" AND DSNBOUNCE ="5.1.0") BOUNCE
 LEFT JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=BOUNCE.MID
LEFT JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID;`

***

####4. Métrica *spam* : Correos detectados como Spam

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
SPAMCASE STRING,
INTERFACEIP STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
SPAMCASE,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,"POSITIVE" as SPAMCASE`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where SPAMCASE <> "null") SPAM
JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=SPAM.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE spam
SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
SPAMCASE,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,"POSITIVE" as SPAMCASE
from ob_Src_ironport where SPAMCASE <> "null") SPAM
JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=SPAM.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID;`

***

####5. Métrica *virus* : Correos detectados con virus

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
ANTIVIRUS STRING,
INTERFACEIP STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
ANTIVIRUS,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,
ANTIVIRUS`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where ANTIVIRUS<>"null" ) VIRUS
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=VIRUS.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE virus
SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
ANTIVIRUS,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,
ANTIVIRUS
from ob_Src_ironport where ANTIVIRUS<>"null" ) VIRUS
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=VIRUS.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID;`

***

####6. Métrica *content_filter* : Correos bloqueados por filtro de contenido

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
FILTROCONTENIDO STRING,
INTERFACEIP STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
FILTROCONTENIDO,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,
FILTROCONTENIDO`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where FILTROCONTENIDO<>"null" ) FILTR
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=FILTR.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE content_filter
SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
FILTROCONTENIDO,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,
FILTROCONTENIDO
from ob_Src_ironport where FILTROCONTENIDO<>"null" ) FILTR
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=FILTR.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID;`

***

####7. Métrica *marketing* : Correos de marketing

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
MARKETINGCASE STRING,
INTERFACEIP STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
MARKETINGCASE,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,
MARKETINGCASE`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where MARKETINGCASE <>"null" ) MARKET
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=MARKET.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE marketing
SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
MARKETINGCASE,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,
MARKETINGCASE
from ob_Src_ironport where MARKETINGCASE <>"null" ) MARKET
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=MARKET.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID;`

***

####8. Métrica *correos_limpios* : Correos limpios

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
RID BIGINT,
RESPONSE STRING,
INTERFACEIP STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
RID,
RESPONSE,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,RID,
RESPONSE`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where RESPONSE <>"null" ) OK
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=OK.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE correos_limpios
SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
RID,
RESPONSE,
INTERFACEIP
FROM
(
SELECT
eventTimeStamp,
MID,RID,
RESPONSE
from ob_Src_ironport where RESPONSE <>"null" ) OK
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=OK.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID;`

***

####9. Métrica *correos_limpios_georef* : Correos limpios georeferenciados

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
RID BIGINT,
RESPONSE STRING,
INTERFACEIP STRING,
COORDS ARRAY<DOUBLE>,
CITY STRING,
COUNTRY STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
RID,
RESPONSE,
INTERFACEIP,
COORDS,
CITY,
COUNTRY
FROM
(
SELECT
eventTimeStamp,
MID,RID,
RESPONSE,
COORDS,
CITY,
COUNTRY`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where RESPONSE <>"null" and eventtimestamp>"2014-09-28 00:00:00") OK
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=OK.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID
`
- **Timestamp:**`eventtimestamp`
- **Id Es:*`ID`*
- **Query Hive:** `INSERT OVERWRITE TABLE correos_limpios_georef SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
RID,
RESPONSE,
INTERFACEIP,
COORDS,
CITY,
COUNTRY
FROM
(
SELECT
eventTimeStamp,
MID,RID,
RESPONSE,
COORDS,
CITY,
COUNTRY FROM ob_src_ironport where RESPONSE <>"null" and eventtimestamp>"2014-09-28 00:00:00") OK
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=OK.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID
D
JOIN
(
SELECT
MSGID as AMAVISID,
CONCAT(TOSERVERNAME,'[',TOSERVERIP,']') as SERVDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID;`

***

####10. Métrica *marketing_georef* : Correos de marketing georeferenciados

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
MARKETINGCASE STRING,
INTERFACEIP STRING,
COORDS ARRAY<DOUBLE>,
CITY STRING,
COUNTRY STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
MARKETINGCASE,
INTERFACEIP,COORDS,
CITY,
COUNTRY 
FROM
(
SELECT
eventTimeStamp,
MID,
MARKETINGCASE,COORDS,
CITY,
COUNTRY `
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where MARKETINGCASE <>"null" and eventtimestamp>"2014-09-28 00:00:00") MARKET
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=MARKET.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**`ID`
- **Query Hive:** `INSERT OVERWRITE TABLE marketing_georef SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
MARKETINGCASE,
INTERFACEIP,COORDS,
CITY,
COUNTRY 
FROM
(
SELECT
eventTimeStamp,
MID,
MARKETINGCASE,COORDS,
CITY,
COUNTRY  FROM ob_src_ironport where MARKETINGCASE <>"null" and eventtimestamp>"2014-09-28 00:00:00") MARKET
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=MARKET.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID(
SELECT
MSGID as AMAVISID,
CONCAT(TOSERVERNAME,'[',TOSERVERIP,']') as SERVDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID;`

***

####11. Métrica *content_filter_georef* : Correos bloqueados por filtro de contenido georeferenciados

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
FILTROCONTENIDO STRING,
INTERFACEIP STRING,
COORDS ARRAY<DOUBLE>,
CITY STRING,
COUNTRY STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
FILTROCONTENIDO,
INTERFACEIP,COORDS,
CITY,
COUNTRY 
FROM
(
SELECT
eventTimeStamp,
MID,
FILTROCONTENIDO,COORDS,
CITY,
COUNTRY `
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where FILTROCONTENIDO<>"null" and eventtimestamp>"2014-09-28 00:00:00" ) FILTR
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=FILTR.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**`ID`
- **Query Hive:** `INSERT OVERWRITE TABLE content_filter_georef SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
FILTROCONTENIDO,
INTERFACEIP,COORDS,
CITY,
COUNTRY 
FROM
(
SELECT
eventTimeStamp,
MID,
FILTROCONTENIDO,COORDS,
CITY,
COUNTRY  FROM ob_src_ironport where FILTROCONTENIDO<>"null" and eventtimestamp>"2014-09-28 00:00:00" ) FILTR
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=FILTR.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICIDMSGID as AMAVISID,
CONCAT(TOSERVERNAME,'[',TOSERVERIP,']') as SERVDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID;`

***

####12. Métrica *virus_georef* : Correos detectados con virus georeferenciados

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
ANTIVIRUS STRING,
INTERFACEIP STRING,
COORDS ARRAY<DOUBLE>,
CITY STRING,
COUNTRY STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
ANTIVIRUS,
INTERFACEIP,COORDS,
CITY,
COUNTRY 
FROM
(
SELECT
eventTimeStamp,
MID,
ANTIVIRUS,COORDS,
CITY,
COUNTRY `
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where ANTIVIRUS<>"null" and eventtimestamp>"2014-09-28 00:00:00") VIRUS
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=VIRUS.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**`ID`
- **Query Hive:** `INSERT OVERWRITE TABLE virus_georef SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
ANTIVIRUS,
INTERFACEIP,COORDS,
CITY,
COUNTRY 
FROM
(
SELECT
eventTimeStamp,
MID,
ANTIVIRUS,COORDS,
CITY,
COUNTRY  FROM ob_src_ironport where ANTIVIRUS<>"null" and eventtimestamp>"2014-09-28 00:00:00") VIRUS
 JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=VIRUS.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICIDTERF
ON INTERF.ICID=REL.ICIDMSGID as AMAVISID,
CONCAT(TOSERVERNAME,'[',TOSERVERIP,']') as SERVDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID;`

***

####13. Métrica *filtro_reputacion_georef* : Correos rechazados por Filtro de Reputación georeferenciados

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
REPUTATION STRING,
INTERFACEIP STRING,
COORDS ARRAY<DOUBLE>,
CITY STRING,
COUNTRY STRING`
- **Query Select:** `SELECT
DATOS.ICID as ID,
eventTimeStamp,
DATOS.ICID,
REPUTATION,
INTERFACEIP,
COORDS,
CITY,
COUNTRY
FROM 
(
SELECT 
REPUTATION,
eventTimeStamp,
ICID,COORDS,
CITY,
COUNTRY`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE (REPUTATION like "%TCPREFUSE%" OR REPUTATION like "%REJECTED%") and eventtimestamp>"2014-09-28 00:00:00") DATOS
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null" 
) INTERF
ON INTERF.ICID=DATOS.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**`ID`
- **Query Hive:** `INSERT OVERWRITE TABLE filtro_reputacion_georef SELECT
DATOS.ICID as ID,
eventTimeStamp,
DATOS.ICID,
REPUTATION,
INTERFACEIP,
COORDS,
CITY,
COUNTRY
FROM 
(
SELECT 
REPUTATION,
eventTimeStamp,
ICID,COORDS,
CITY,
COUNTRY FROM ob_src_ironport WHERE (REPUTATION like "%TCPREFUSE%" OR REPUTATION like "%REJECTED%") and eventtimestamp>"2014-09-28 00:00:00") DATOS
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null" 
) INTERF
ON INTERF.ICID=DATOS.ICIDRFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICIDTERF
ON INTERF.ICID=REL.ICIDMSGID as AMAVISID,
CONCAT(TOSERVERNAME,'[',TOSERVERIP,']') as SERVDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID;`

***

####14. Métrica *spam_georef* : Correos detectados como Spam georeferenciados

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
SPAMCASE STRING,
INTERFACEIP STRING,
COORDS ARRAY<DOUBLE>,
CITY STRING,
COUNTRY STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
SPAMCASE,
INTERFACEIP,
COORDS,
CITY,
COUNTRY
FROM
(
SELECT
eventTimeStamp,
MID,"POSITIVE" as SPAMCASE,
COORDS,
CITY,
COUNTRY
`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where SPAMCASE <> "null" and eventtimestamp>"2014-09-28 00:00:00") SPAM
JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=SPAM.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**`ID`
- **Query Hive:** `INSERT OVERWRITE TABLE spam_georef 
SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
SPAMCASE,
INTERFACEIP,
COORDS,
CITY,
COUNTRY
FROM
(
SELECT
eventTimeStamp,
MID,"POSITIVE" as SPAMCASE,
COORDS,
CITY,
COUNTRY
 FROM ob_src_ironport where SPAMCASE <> "null" and eventtimestamp>"2014-09-28 00:00:00") SPAM
JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=SPAM.MID
JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICIDTERF.ICID=REL.ICIDMSGID as AMAVISID,
CONCAT(TOSERVERNAME,'[',TOSERVERIP,']') as SERVDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID;`

***

####15. Métrica *receptor_no_valido_georef* : Correos descartados por Receptor no válido georeferenciados

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
DSNBOUNCE STRING,
BOUNCEDESC STRING,
INTERFACEIP STRING,
COORDS ARRAY<DOUBLE>,
CITY STRING,
COUNTRY STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
DSNBOUNCE,
BOUNCEDESC,
INTERFACEIP,
COORDS,
CITY,
COUNTRY
FROM
(
SELECT
eventTimeStamp,
MID,
DSNBOUNCE,
BOUNCEDESC,COORDS,
CITY,
COUNTRY 
`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `where BOUNCEDESC like "%nvalid recipient%" AND DSNBOUNCE ="5.1.0" and eventtimestamp>"2014-09-28 00:00:00") BOUNCE
 LEFT JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=BOUNCE.MID
LEFT JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID
`
- **Timestamp:**`eventtimestamp`
- **Id Es:**`ID`
- **Query Hive:** `INSERT OVERWRITE TABLE receptor_no_valido_georef 
SELECT
CONCAT(REL.ICID,"-",REL.MID) as ID,
eventTimeStamp,
REL.ICID,
REL.MID,
DSNBOUNCE,
BOUNCEDESC,
INTERFACEIP,
COORDS,
CITY,
COUNTRY
FROM
(
SELECT
eventTimeStamp,
MID,
DSNBOUNCE,
BOUNCEDESC,COORDS,
CITY,
COUNTRY  FROM ob_src_ironport where BOUNCEDESC like "%nvalid recipient%" AND DSNBOUNCE ="5.1.0" and eventtimestamp>"2014-09-28 00:00:00") BOUNCE
 LEFT JOIN
(
select ICID,MID 
from ob_src_ironport 
WHERE ICID is not NULL AND mid is not NULL AND RID is NULL
GROUP BY ICID,MID) REL
ON REL.MID=BOUNCE.MID
LEFT JOIN
(
SELECT 
ICID,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID
VDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID;`

***

####16. Métrica *total_mensajes_georef* : Total de mensajes de ironport georeferenciados

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp timestamp,
ICID BIGINT,
MID BIGINT,
INTERFACEIP STRING,
COORDS ARRAY<DOUBLE>,
CITY STRING,
COUNTRY STRING`
- **Query Select:** `SELECT
CONCAT(REL.ICID,"-",MID) as ID,
eventTimeStamp,
REL.ICID,
MID,
INTERFACEIP,
COORDS,
CITY,
COUNTRY
FROM(
select ICID,MID`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE ICID is not NULL AND mid is not NULL AND RID is NULL and mailfrom ="null"
GROUP BY ICID,MID) REL
LEFT JOIN
(
SELECT 
ICID
,eventTimeStamp,INTERFACEIP,COORDS,
CITY,
COUNTRY
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null" and eventtimestamp>"2014-09-28 00:00:00"
)ENTR
ON ENTR.ICID=REL.ICID
GROUP BY eventTimeStamp,MID,REL.ICID,INTERFACEIP,COORDS,
CITY,
COUNTRY
`
- **Timestamp:**`eventtimestamp`
- **Id Es:**`ID`
- **Query Hive:** `INSERT OVERWRITE TABLE total_mensajes_georef SELECT
CONCAT(REL.ICID,"-",MID) as ID,
eventTimeStamp,
REL.ICID,
MID,
INTERFACEIP,
COORDS,
CITY,
COUNTRY
FROM(
select ICID,MID  FROM ob_src_ironport WHERE ICID is not NULL AND mid is not NULL AND RID is NULL and mailfrom ="null"
GROUP BY ICID,MID) REL
LEFT JOIN
(
SELECT 
ICID
,eventTimeStamp,INTERFACEIP,COORDS,
CITY,
COUNTRY
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null" and eventtimestamp>"2014-09-28 00:00:00"
)ENTR
ON ENTR.ICID=REL.ICID
GROUP BY eventTimeStamp,MID,REL.ICID,INTERFACEIP,COORDS,
CITY,
COUNTRYD,
INTERFACEIP
from ob_src_ironport 
WHERE ICID is not NULL and INTERFACEIP <>"null"
) INTERF
ON INTERF.ICID=REL.ICID
VDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID;`
