# Origen Ironport Awstats

####1. Métrica *primult_awstats* : 

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
ANO BIGINT,
MES BIGINT,
PRIMERO TIMESTAMP,
ULTIMO TIMESTAMP`
- **Query Select:** `SELECT
CONCAT(YEAR(eventTimeStamp),"-",MONTH(eventTimeStamp)) as ID,
YEAR(eventTimeStamp) as ANO,
MONTH(eventTimeStamp) as MES,
MIN(eventTimeStamp) as PRIMERO,
MAX(eventTimeStamp) as ULTIMO`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE ICID is not NULL and MID is not NULL and RID is not NULL and mailto <> "null"
GROUP BY MONTH(eventTimeStamp),YEAR(eventTimeStamp)`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE primult_awstats
SELECT
CONCAT(YEAR(eventTimeStamp),"-",MONTH(eventTimeStamp)) as ID,
YEAR(eventTimeStamp) as ANO,
MONTH(eventTimeStamp) as MES,
MIN(eventTimeStamp) as PRIMERO,
MAX(eventTimeStamp) as ULTIMO
FROM ob_src_ironport
WHERE ICID is not NULL and MID is not NULL and RID is not NULL and mailto <> "null"
GROUP BY MONTH(eventTimeStamp),YEAR(eventTimeStamp);`

***

####2. Métrica *correos_ok_awstats* : Correos ok por mes

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
ANO BIGINT,
MES BIGINT,
TOTAL BIGINT,
BYTES BIGINT`
- **Query Select:** `SELECT
CONCAT(ano,"-",mes) as ID,
ANO,MES,
sum(TOTAL) as TOTAL,
SUM(bytes) as BYTES
FROM
(SELECT
MONTH(eventTimeStamp) as MES,
YEAR(eventTimeStamp) as ANO,
MID,
COUNT(1) as TOTAL`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE RESPONSE <> "null"
GROUP BY MONTH(eventTimeStamp),YEAR(eventTimeStamp),MID) CANT
JOIN
(
SELECT 
MID,
SUM(BYTES) as bytes
FROM ob_src_ironport
WHERE bytes is not NULL
GROUP BY MID
)TAM
ON TAM.MID=CANT.MID
GROUP BY MES,ANO`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE correos_ok_awstats
SELECT
CONCAT(ano,"-",mes) as ID,
ANO,MES,
sum(TOTAL) as TOTAL,
SUM(bytes) as BYTES
FROM
(SELECT
MONTH(eventTimeStamp) as MES,
YEAR(eventTimeStamp) as ANO,
MID,
COUNT(1) as TOTAL
FROM ob_src_ironport
WHERE RESPONSE <> "null"
GROUP BY MONTH(eventTimeStamp),YEAR(eventTimeStamp),MID) CANT
JOIN
(
SELECT 
MID,
SUM(BYTES) as bytes
FROM ob_src_ironport
WHERE bytes is not NULL
GROUP BY MID
)TAM
ON TAM.MID=CANT.MID
GROUP BY MES,ANO;`

***

####3. Métrica *correos_notok_awstats* : Correos erróneos o rechazados

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
ANO BIGINT,
MES BIGINT,
TOTAL BIGINT,
BYTES BIGINT`
- **Query Select:** `SELECT
CONCAT(ANO,"-",MES) as ID,
ANO,
MES,
sum(TOTAL) as TOTAL,
SUM(bytes) as BYTES
FROM
(SELECT
MONTH(eventTimeStamp) as MES,
YEAR(eventTimeStamp) as ANO,
MID,
COUNT(1) as TOTAL`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE DSNBOUNCE <> "null" or DSNDELAY  <> "null"
GROUP BY MONTH(eventTimeStamp),YEAR(eventTimeStamp),MID) CANT
JOIN
(
SELECT 
MID,
SUM(BYTES) as bytes
FROM ob_src_ironport
WHERE bytes is not NULL
GROUP BY MID
)TAM
ON TAM.MID=CANT.MID
GROUP BY MES,ANO`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE correos_notok_awstats
SELECT
CONCAT(ANO,"-",MES) as ID,
ANO,
MES,
sum(TOTAL) as TOTAL,
SUM(bytes) as BYTES
FROM
(SELECT
MONTH(eventTimeStamp) as MES,
YEAR(eventTimeStamp) as ANO,
MID,
COUNT(1) as TOTAL
FROM ob_src_ironport
WHERE DSNBOUNCE <> "null" or DSNDELAY  <> "null"
GROUP BY MONTH(eventTimeStamp),YEAR(eventTimeStamp),MID) CANT
JOIN
(
SELECT 
MID,
SUM(BYTES) as bytes
FROM ob_src_ironport
WHERE bytes is not NULL
GROUP BY MID
)TAM
ON TAM.MID=CANT.MID
GROUP BY MES,ANO;`

***

####4. Métrica *correos_ok_tiempo_awstats* :  Correos OK por unidad de tiempo

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp TIMESTAMP,
ANO BIGINT,
MES BIGINT,
DIA BIGINT,
HORA BIGINT,
DIASEMANA STRING,
TOTAL BIGINT,
BYTES BIGINT`
- **Query Select:** `SELECT
eventTimestamp as ID,
eventTimestamp ,
YEAR(eventTimeStamp) as ANO,
MONTH(eventTimeStamp) as MES,
DAY(eventTimestamp) as DIA,
HOUR(eventTimestamp) as HORA,
from_unixtime(unix_timestamp(eventTimestamp), 'EEEE') as DIASEMANA,
sum(TOTAL) as TOTAL,
SUM(bytes) as BYTES
FROM
(SELECT
eventTimestamp ,
MID,
COUNT(1) as TOTAL`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE RESPONSE <> "null"
GROUP BY eventTimestamp,MID) CANT
JOIN
(
SELECT 
MID,
SUM(BYTES) as bytes
FROM ob_src_ironport
WHERE bytes is not NULL
GROUP BY MID
)TAM
ON TAM.MID=CANT.MID
GROUP BY eventTimestamp,MONTH(eventTimeStamp),
YEAR(eventTimeStamp),
DAY(eventTimestamp),
HOUR(eventTimestamp),
from_unixtime(unix_timestamp(eventTimestamp), 'EEEE')`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE correos_ok_tiempo_awstats
SELECT
eventTimestamp as ID,
eventTimestamp ,
YEAR(eventTimeStamp) as ANO,
MONTH(eventTimeStamp) as MES,
DAY(eventTimestamp) as DIA,
HOUR(eventTimestamp) as HORA,
from_unixtime(unix_timestamp(eventTimestamp), 'EEEE') as DIASEMANA,
sum(TOTAL) as TOTAL,
SUM(bytes) as BYTES
FROM
(SELECT
eventTimestamp ,
MID,
COUNT(1) as TOTAL
FROM ob_src_ironport
WHERE RESPONSE <> "null"
GROUP BY eventTimestamp,MID) CANT
JOIN
(
SELECT 
MID,
SUM(BYTES) as bytes
FROM ob_src_ironport
WHERE bytes is not NULL
GROUP BY MID
)TAM
ON TAM.MID=CANT.MID
GROUP BY eventTimestamp,MONTH(eventTimeStamp),
YEAR(eventTimeStamp),
DAY(eventTimestamp),
HOUR(eventTimestamp),
from_unixtime(unix_timestamp(eventTimestamp), 'EEEE');`

***

####5. Métrica *correos_por_servidor_awstats* : Correos enviados por servidor

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp TIMESTAMP,
HOSTNAMEIP STRING,
HOSTIP STRING,
HOSTNAME STRING,
TOTAL BIGINT,
BYTES BIGINT`
- **Query Select:** `SELECT
CONCAT(eventTimeStamp,"-",HOSTIP,"-",HOSTNAME) as ID,
eventTimeStamp,
CONCAT(HOSTNAME,"-[",HOSTIP,"]") as HOSTNAMEIP,
HOSTIP,
HOSTNAME,
COUNT(1) as TOTAL,
SUM(BYTES) as BYTES
FROM
(
SELECT
eventTimeStamp,
ICID,
HOSTIP,
HOSTNAME`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE ICID is not NULL and HOSTIP <> "null") SERV
JOIN
(
SELECT
MID,ICID
FROM ob_src_ironport
WHERE MID is not NULL and ICID is not NULL
GROUP BY MID,ICID
)REL
ON REL.ICID=SERV.ICID
JOIN
(
SELECT
MID,
SUM(BYTES) as bytes
FROM ob_src_ironport
WHERE bytes is not NULL
GROUP BY MID
)TAM
ON TAM.MID=REL.MID
GROUP BY eventTimeStamp,HOSTIP,HOSTNAME`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE correos_por_servidor_awstats
SELECT
CONCAT(eventTimeStamp,"-",HOSTIP,"-",HOSTNAME) as ID,
eventTimeStamp,
CONCAT(HOSTNAME,"-[",HOSTIP,"]") as HOSTNAMEIP,
HOSTIP,
HOSTNAME,
COUNT(1) as TOTAL,
SUM(BYTES) as BYTES
FROM
(
SELECT
eventTimeStamp,
ICID,
HOSTIP,
HOSTNAME
FROM ob_src_ironport
WHERE ICID is not NULL and HOSTIP <> "null") SERV
JOIN
(
SELECT
MID,ICID
FROM ob_src_ironport
WHERE MID is not NULL and ICID is not NULL
GROUP BY MID,ICID
)REL
ON REL.ICID=SERV.ICID
JOIN
(
SELECT
MID,
SUM(BYTES) as bytes
FROM ob_src_ironport
WHERE bytes is not NULL
GROUP BY MID
)TAM
ON TAM.MID=REL.MID
GROUP BY eventTimeStamp,HOSTIP,HOSTNAME;`

***

####6. Métrica *top_emisores_awstats* : Cantidad de correos enviados por emisor

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp TIMESTAMP,
MAILFROM STRING,
TOTAL BIGINT,
BYTES BIGINT`
- **Query Select:** `SELECT
CONCAT(eventTimeStamp,"-",MAILFROM) as ID,
eventTimeStamp,
MAILFROM,
COUNT(1) as TOTAL,
SUM(BYTES) as BYTES
FROM(
SELECT
MID,
eventTimeStamp,
MAILFROM,
SUM(bytes) as BYTES`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE MAILFROM <> "null" and MID is not NULL  and BYTES is not NULL
GROUP BY MAILFROM, eventtimestamp,MID)EMI
JOIN
(
SELECT MID
FROM ob_src_ironport
WHERE RESPONSE <> "null"
GROUP BY MID
)REC
ON REC.MID=EMI.MID
GROUP BY eventTimeStamp,MAILFROM`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE top_emisores_awstats
SELECT
CONCAT(eventTimeStamp,"-",MAILFROM) as ID,
eventTimeStamp,
MAILFROM,
COUNT(1) as TOTAL,
SUM(BYTES) as BYTES
FROM(
SELECT
MID,
eventTimeStamp,
MAILFROM,
SUM(bytes) as BYTES
FROM ob_src_ironport
WHERE MAILFROM <> "null" and MID is not NULL  and BYTES is not NULL
GROUP BY MAILFROM, eventtimestamp,MID)EMI
JOIN
(
SELECT MID
FROM ob_src_ironport
WHERE RESPONSE <> "null"
GROUP BY MID
)REC
ON REC.MID=EMI.MID
GROUP BY eventTimeStamp,MAILFROM`

***

####7. Métrica *top_receptores_awstats* : Cantidad de correos por receptor

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp TIMESTAMP,
MAILTO STRING,
TOTAL BIGINT,
BYTES BIGINT`
- **Query Select:** `SELECT
CONCAT(eventTimeStamp,"-",MAILTO) as ID,
eventTimeStamp,
MAILTO,
count(1) as TOTAL,
SUM(BYTES) as BYTES
FROM
(
SELECT
eventTimeStamp,
MID,
mailto`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE ICID is not null and MID is not NULL and RID is not NULL and MAILTO <> "null"
GROUP BY eventTimeStamp,MID,mailto
)RECEP
JOIN
(
SELECT
MID,
SUM(bytes) as BYTES
FROM ob_src_ironport
WHERE bytes is not NULL
GROUP BY MID
)TAM
ON TAM.MID=RECEP.MID
group by eventTimeStamp,MAILTO`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE top_receptores_awstats
SELECT
CONCAT(eventTimeStamp,"-",MAILTO) as ID,
eventTimeStamp,
MAILTO,
count(1) as TOTAL,
SUM(BYTES) as BYTES
FROM
(
SELECT
eventTimeStamp,
MID,
mailto
FROM ob_src_ironport
WHERE ICID is not null and MID is not NULL and RID is not NULL and MAILTO <> "null"
GROUP BY eventTimeStamp,MID,mailto
)RECEP
JOIN
(
SELECT
MID,
SUM(bytes) as BYTES
FROM ob_src_ironport
WHERE bytes is not NULL
GROUP BY MID
)TAM
ON TAM.MID=RECEP.MID
group by eventTimeStamp,MAILTO;`

***

####8. Métrica *errores_awstats* : Correos descartados por error

- **Origen de datos:** `ob_src_ironport`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
eventTimeStamp TIMESTAMP,
ERROR STRING,
TOTAL BIGINT,
BYTES BIGINT`
- **Query Select:** `SELECT
CONCAT(eventTimeStamp,"-",ERROR) as ID,
eventTimeStamp,
ERROR,
SUM(TOTAL) as TOTAL,
SUM(BYTES) as BYTES
FROM
(SELECT
eventTimeStamp,
MID,
CASE 
WHEN DSNBOUNCE="null" THEN DSNDELAY
ELSE DSNBOUNCE
END as ERROR,
count(1) as TOTAL`
- **Query From:** `FROM ob_src_ironport`
- **Query Where:** `WHERE DSNBOUNCE <>"null" or DSNDELAY<>"null"
GROUP BY eventTimeStamp,MID,DSNBOUNCE, DSNDELAY
)ERR
JOIN
(
SELECT
MID,
SUM(BYTES) as BYTES
FROM ob_src_ironport
WHERE BYTES is not NULL
GROUP BY MID
)TAM
ON TAM.MID=ERR.MID
GROUP BY eventTimeStamp,ERROR`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE errores_awstats
SELECT
CONCAT(eventTimeStamp,"-",ERROR) as ID,
eventTimeStamp,
ERROR,
SUM(TOTAL) as TOTAL,
SUM(BYTES) as BYTES
FROM
(SELECT
eventTimeStamp,
MID,
CASE 
WHEN DSNBOUNCE="null" THEN DSNDELAY
ELSE DSNBOUNCE
END as ERROR,
count(1) as TOTAL
FROM ob_src_ironport
WHERE DSNBOUNCE <>"null" or DSNDELAY<>"null"
GROUP BY eventTimeStamp,MID,DSNBOUNCE, DSNDELAY
)ERR
JOIN
(
SELECT
MID,
SUM(BYTES) as BYTES
FROM ob_src_ironport
WHERE BYTES is not NULL
GROUP BY MID
)TAM
ON TAM.MID=ERR.MID
GROUP BY eventTimeStamp,ERROR
`

***
