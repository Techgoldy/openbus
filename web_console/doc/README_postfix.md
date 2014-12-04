# Origen Postfix

####1. Métrica *primult_correo* : Primer y ultimo correo por cada mes

- **Origen de datos:** `ob_src_postfix`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,MES INT,ANO INT,ULTIMO STRING,PRIMERO STRING`
- **Query Select:** `SELECT CONCAT(YEAR(eventTimeStamp),MONTH(eventTimeStamp)) as ID,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO,MAX(eventTimeStamp) as  ULTIMO,MIN(eventTimeStamp) as PRIMERO`
- **Query From:** `FROM ob_src_postfix`
- **Query Where:** `where MSGID is not NULL and QMGRID is not NULL and USERFROM !='null' GROUP BY MONTH(eventTimeStamp),YEAR(eventTimeStamp)`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE primult_correo SELECT CONCAT(YEAR(eventTimeStamp),MONTH(eventTimeStamp)) as ID,MONTH(eventTimeStamp) as MES,
YEAR(eventTimeStamp) as ANO,MAX(eventTimeStamp) as  ULTIMO,MIN(eventTimeStamp) as PRIMERO FROM postfix_logs where MSGID is not NULL and QMGRID is not NULL and USERFROM !='null' GROUP BY MONTH(eventTimeStamp), YEAR(eventTimeStamp);`

***

####2. Métrica *correos_ok* : Correos Ok por mes

- **Origen de datos:** `ob_src_postfix`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,MES INT, ANO INT,TAMANO_OK BIGINT, CUENTA_OK BIGINT`
- **Query Select:** `SELECT CONCAT(ANO,MES) as ID,MES,ANO,SUM(TAMANO*cuenta) TAMANO_ok,sum(cuenta) as CUENTA_OK FROM(
SELECT MSGID,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO,count(1) as cuenta FROM ob_src_postfix WHERE DSN  in("2.0.0","2.6.0","2.4.0") and AMAVISID ='null' group by MSGID,MONTH(eventTimeStamp),YEAR(eventTimeStamp)) correo JOIN (SELECT MSGID,SUM(SIZE) as TAMANO`
- **Query From:** `FROM ob_src_postfix`
- **Query Where:** `WHERE QMGRID is not NULL and SIZE is not NULL GROUP BY MSGID)tam ON tam.MSGID=correo.MSGID GROUP BY MES,ANO`
- **Timestamp:**`eventtimestamp`
- **Id Es:**

- **Query Hive:** `INSERT INTO TABLE primult_correo SELECT CONCAT(YEAR(eventTimeStamp),MONTH(eventTimeStamp)) as ID,MONTH(eventTimeStamp) as MES,
YEAR(eventTimeStamp) as ANO,MAX(eventTimeStamp) as  ULTIMO,MIN(eventTimeStamp) as PRIMERO FROM postfix_logs where MSGID is not NULL and QMGRID is not NULL and USERFROM !='null' GROUP BY MONTH(eventTimeStamp), YEAR(eventTimeStamp);`

***

####3. Métrica *correos_notok* : Correos erróneos o devueltos

- **Origen de datos:** `ob_src_postfix`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,MES BIGINT,ANO BIGINT,TAMANO_notok BIGINT,CUENTA_NOTOK BIGINT`
- **Query Select:** `SELECT CONCAT(ANO,MES) as ID,MES,ANO,SUM(TAMANO*cuenta) TAMANO_notok,sum(cuenta) as CUENTA_NOTOK FROM(
SELECT MSGID,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO,count(1) as cuenta`
- **Query From:** `FROM ob_src_postfix`
- **Query Where:** `WHERE DSN not in("2.0.0","2.6.0","2.4.0","null") and AMAVISID ='null' group by MSGID,MONTH(eventTimeStamp),YEAR(eventTimeStamp)) correo JOIN(SELECT MSGID,SUM(SIZE) as TAMANO FROM ob_src_postfix WHERE QMGRID is not NULL and SIZE is not NULL GROUP BY MSGID)tam ON tam.MSGID=correo.MSGID GROUP BY MES,ANO`
- **Timestamp:**`eventtimestamp`
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE correos_notok SELECT CONCAT(ANO,MES) as ID,MES,ANO,SUM(TAMANO*cuenta) TAMANO_notok,sum(cuenta) as CUENTA_NOTOK FROM(SELECT MSGID,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO,count(1) as cuenta FROM ob_src_postfix WHERE DSN not in("2.0.0","2.6.0","2.4.0","null") and AMAVISID ='null' group by MSGID,MONTH(eventTimeStamp),YEAR(eventTimeStamp)) correo JOIN(SELECT MSGID,SUM(SIZE) as TAMANO FROM ob_src_postfix WHERE QMGRID is not NULL and SIZE is not NULL GROUP BY MSGID)tam ON tam.MSGID=correo.MSGID GROUP BY MES,ANO;`

***

####4. Métrica *correos_diaok* : Cantidad de correos OK por día

- **Origen de datos:** `ob_src_postfix`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,DIA BIGINT,MES BIGINT,ANO BIGINT,TAMANO_ok BIGINT,CUENTA_OK BIGINT`
- **Query Select:** `SELECT CONCAT(ANO,"/",MES,"/",DIA) as ID,DIA,MES,ANO,SUM(TAMANO*cuenta) TAMANO_ok,sum(cuenta) as CUENTA_OK
FROM(SELECT MSGID,DAY(eventTimeStamp) as DIA,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO,count(1) as cuenta`
- **Query From:** `FROM ob_src_postfix`
- **Query Where:** `WHERE DSN in("2.0.0","2.6.0","2.4.0") and AMAVISID ='null' group by MSGID,DAY(eventTimeStamp) ,MONTH(eventTimeStamp),YEAR(eventTimeStamp)) correo JOIN(SELECT MSGID,SUM(SIZE) as TAMANO FROM POSTFIX_LOGS WHERE QMGRID is not NULL and SIZE is not NULL GROUP BY MSGID)tam ON tam.MSGID=correo.MSGID GROUP BY DIA,MES,ANO;`
- **Timestamp:**
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE correos_diaok SELECT CONCAT(ANO,"/",MES,"/",DIA) as ID,DIA,MES,ANO,SUM(TAMANO*cuenta) TAMANO_ok,sum(cuenta) as CUENTA_OK FROM(SELECT MSGID,DAY(eventTimeStamp) as DIA,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO,count(1) as cuenta FROM ob_src_postfix WHERE DSN in("2.0.0","2.6.0","2.4.0") and AMAVISID ='null' group by MSGID,DAY(eventTimeStamp) ,MONTH(eventTimeStamp),YEAR(eventTimeStamp)) correo JOIN
(SELECT MSGID,SUM(SIZE) as TAMANO FROM ob_src_postfix WHERE QMGRID is not NULL and SIZE is not NULL GROUP BY MSGID)tam
ON tam.MSGID=correo.MSGID GROUP BY DIA,MES,ANO;`

***

####5. Métrica *top_emisores* : Correos enviados por emisor y mes

- **Origen de datos:** `ob_src_postfix`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,USERFROM STRING,CUENTA BIGINT,TAMANO BIGINT,ULTIMO TIMESTAMP,MES BIGINT,ANO BIGINT`
- **Query Select:** `SELECT CONCAT(ANO,MES,"-",USERFROM) as ID,USERFROM,count(CUENTA) as CUENTA,sum(TAMANO_enviados) as TAMANO,
MAX(ULTIMO) as ULTIMO,MES,ANO FROM(SELECT MSGID,count(1) as CUENTA`
- **Query From:** `FROM ob_src_postfix`
- **Query Where:** `WHERE DSN in("2.0.0","2.6.0","2.4.0") and AMAVISID ='null' GROUP BY MSGID) DES JOIN(SELECT MSGID,
USERFROM,sum(SIZE) as TAMANO_enviados,MAX(eventTimeStamp) as ULTIMO,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO 
FROM ob_src_postfix WHERE SIZE is not NULL GROUP BY MSGID, USERFROM, MONTH(eventTimeStamp) ,YEAR(eventTimeStamp)) REM 
ON REM.MSGID=DES.MSGID GROUP BY USERFROM, MES,ANO`
- **Timestamp:**
- **Id Es:**
- **Query Hive:** `insert into table top_emisores SELECT CONCAT(ANO,MES,"-",USERFROM) as ID,USERFROM,count(CUENTA) as CUENTA,
sum(TAMANO_enviados) as TAMANO,MAX(ULTIMO) as ULTIMO,MES,ANO FROM(SELECT MSGID,count(1) as CUENTA FROM ob_src_postfix WHERE DSN in("2.0.0","2.6.0","2.4.0") and AMAVISID ='null' GROUP BY MSGID) DES JOIN(SELECT MSGID,USERFROM,sum(SIZE) as TAMANO_enviados,
MAX(eventTimeStamp) as ULTIMO,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO FROM ob_src_postfix WHERE SIZE is not NULL GROUP BY MSGID, USERFROM, MONTH(eventTimeStamp) ,YEAR(eventTimeStamp)) REM ON REM.MSGID=DES.MSGID GROUP BY USERFROM,MES,ANO;`

***

####6. Métrica *top_receptores* : Cantidad de correos por receptor y mes

- **Origen de datos:** `ob_src_postfix`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,USERTO STRING,CUENTA BIGINT,TAMANO BIGINT,ULTIMO TIMESTAMP,MES BIGINT,ANO BIGINT`
- **Query Select:** `SELECT CONCAT(ANO,MES,"-",USERTO) as ID,USERTO,count(1) as CUENTA,sum(TAMANO_enviados) as TAMANO,MAX(ULTIMO) as ULTIMO,MES,ANO FROM(SELECT MSGID,USERTO,MAX(eventTimeStamp) as ULTIMO,MONTH(eventTimeStamp) as MES,
YEAR(eventTimeStamp) as ANO`
- **Query From:** `FROM ob_src_postfix`
- **Query Where:** `WHERE DSN in("2.0.0","2.6.0","2.4.0") and AMAVISID ='null' GROUP BY MSGID,USERTO, MONTH(eventTimeStamp) ,YEAR(eventTimeStamp)) DES JOIN(SELECT MSGID,sum(SIZE) as TAMANO_enviados FROM ob_src_postfix WHERE SIZE is not NULL GROUP BY MSGID) REM ON REM.MSGID=DES.MSGID GROUP BY USERTO, MES,ANO`
- **Timestamp:**
- **Id Es:**
- **Query Hive:** `insert into table top_receptores SELECT CONCAT(ANO,MES,"-",USERTO) as ID,USERTO,count(1) as CUENTA,sum(TAMANO_enviados) as TAMANO,MAX(ULTIMO) as ULTIMO,MES,ANO FROM(SELECT MSGID,USERTO,MAX(eventTimeStamp) as ULTIMO,
MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO FROM ob_src_postfix WHERE DSN in("2.0.0","2.6.0","2.4.0") and AMAVISID ='null' GROUP BY MSGID,USERTO, MONTH(eventTimeStamp) ,YEAR(eventTimeStamp)) DES JOIN(SELECT MSGID,sum(SIZE) as TAMANO_enviados
FROM ob_src_postfix WHERE SIZE is not NULL GROUP BY MSGID) REM ON REM.MSGID=DES.MSGID GROUP BY USERTO, MES,ANHO;`

***

####7. Métrica *errores_smtp* : Correos con error por mes

- **Origen de datos:** `ob_src_postfix`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,DSN STRING,cuenta BIGINT,tamano BIGINT,porcentaje BIGINT,MES BIGINT, ANO BIGINT`
- **Query Select:** `SELECT CONCAT(ERR.ANO,ERR.MES,"-",ERR.DSN) as ID,ERR.DSN,sum(correos) as correos,(sum(correos)*100)/total  as porcentaje,sum(TAMANO) as TAMANO,ERR.MES,ERR.ANO FROM(SELECT MSGID,DSN,count(1) as correos,MONTH(eventTimeStamp) as MES,
YEAR(eventTimeStamp) as ANO`
- **Query From:** `FROM ob_src_postfix`
- **Query Where:** `WHERE DSN not in ("2.0.0","2.6.0","2.4.0","null")GROUP BY MSGID,DSN, MONTH(eventTimeStamp),YEAR(eventTimeStamp)) ERR JOIN(SELECT MSGID,sum(SIZE) as TAMANO FROM ob_src_postfix WHERE SIZE is not NULL GROUP BY MSGID)TAM ON TAM.MSGID=ERR.MSGID JOIN(SELECT count(1) as total,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp)as ANO FROM ob_src_postfix WHERE DSN not in ("2.0.0","2.6.0","2.4.0","null") GROUP BY MONTH(eventTimeStamp) ,YEAR(eventTimeStamp))TOT ON TOT.ANO=ERR.ANO AND TOT.MES=ERR.MES GROUP BY ERR.DSN,ERR.MES,ERR.ANO,TOTALes`
- **Timestamp:**
- **Id Es:**
- **Query Hive:** `INSERT INTO TABLE errores_SMTP SELECT CONCAT(ERR.ANO,ERR.MES,"-",ERR.DSN) as ID,ERR.DSN,sum(correos) as correos,(sum(correos)*100)/total as porcentaje,sum(TAMANO) as TAMANO,ERR.MES,ERR.ANO FROM(SELECT MSGID,DSN,count(1) as correos,
MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO FROM ob_src_postfix WHERE DSN not in ("2.0.0","2.6.0","2.4.0","null")GROUP BY MSGID,DSN, MONTH(eventTimeStamp), YEAR(eventTimeStamp)) ERR JOIN(SELECT MSGID,sum(SIZE) as TAMANO FROM ob_src_postfix WHERE SIZE is not NULL GROUP BY MSGID)TAM ON TAM.MSGID=ERR.MSGID JOIN(SELECT count(1) as total,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO FROM ob_src_postfix WHERE DSN not in("2.0.0","2.6.0","2.4.0","null")GROUP BY MONTH(eventTimeStamp) ,YEAR(eventTimeStamp))TOT ON TOT.ANO=ERR.ANO AND TOT.MES=ERR.MES GROUP BY ERR.DSN,ERR.MES,ERR.ANO,TOTAL;`

***

####8. Métrica *envio_entre_servidores_georef* : Envío entre servidores georeferenciado

- **Origen de datos:** `ob_src_postfix`
- **Tipo:** `Batch`
- **Query Type:** `ID STRING,
MSGID STRING,
AMAVISID STRING,
SERVORIGEN STRING,
SERVDESTINO STRING,
coords ARRAY<Double>,
city STRING,
country STRING,
eventTimeStamp timestamp
`
- **Query Select:** `SELECT
CONCAT(eventTimeStamp,'-',EMI.MSGID) as ID,
EMI.MSGID,
REc.AMAVISID,
SERVORIGEN,
SERVDESTINO,
coords,
city,
country,
eventTimeStamp
FROM
(SELECT
MSGID,
CONCAT(CLIENTE,'[',CLIENTEIP,']') as SERVORIGEN,
coords,
city,
country,
eventTimeStamp`
- **Query From:** `FROM ob_src_postfix`
- **Query Where:** `WHERE SMTPDID is not null and MSGID!= 'null' and CLIENTEIP!='null' and eventtimestamp>"2014-09-27 00:00:00") EMI
JOIN
(
SELECT
MSGID,
AMAVISID
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID != 'null' and DSN in("2.0.0","2.6.0","2.4.0")
GROUP BY MSGID, AMAVISID
) ENV
ON ENV.MSGID=EMI.MSGID
JOIN
(
SELECT
MSGID as AMAVISID,
CONCAT(TOSERVERNAME,'[',TOSERVERIP,']') as SERVDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID`
- **Timestamp:**`eventtimestamp`
- **Id Es:**`ID`
- **Query Hive:** `INSERT OVERWRITE TABLE envio_entre_servidores_georef SELECT
CONCAT(eventTimeStamp,'-',EMI.MSGID) as ID,
EMI.MSGID,
REc.AMAVISID,
SERVORIGEN,
SERVDESTINO,
coords,
city,
country,
eventTimeStamp
FROM
(SELECT
MSGID,
CONCAT(CLIENTE,'[',CLIENTEIP,']') as SERVORIGEN,
coords,
city,
country,
eventTimeStamp FROM ob_src_postfix WHERE SMTPDID is not null and MSGID!= 'null' and CLIENTEIP!='null' and eventtimestamp>"2014-09-27 00:00:00") EMI
JOIN
(
SELECT
MSGID,
AMAVISID
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID != 'null' and DSN in("2.0.0","2.6.0","2.4.0")
GROUP BY MSGID, AMAVISID
) ENV
ON ENV.MSGID=EMI.MSGID
JOIN
(
SELECT
MSGID as AMAVISID,
CONCAT(TOSERVERNAME,'[',TOSERVERIP,']') as SERVDESTINO
FROM ob_src_postfix
WHERE SMTPID is not null and AMAVISID = 'null' and DSN in("2.0.0","2.6.0","2.4.0")
) REC
ON ENV.AMAVISID=REC.AMAVISID;`

