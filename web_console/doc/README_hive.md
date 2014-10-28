# Configuración de Hive para los orígenes ya creados

-- Tabla Hive para postfix
```
CREATE EXTERNAL TABLE ob_src_postfix(
EVENTTIMESTAMP  timestamp,
SMTPDID INT     ,
MSGID   STRING,
CLEANUPID       INT     ,
QMGRID  INT     ,
SMTPID  INT     ,
ERRORID INT     ,
CLIENTE String  ,
CLIENTEIP       String  ,
ACCION  String  ,
SERVER  String  ,
SERVERIP        String  ,
MESSAGEID       String  ,
USERFROM        String  ,
SIZE    int     ,
NRCPT   int     ,
USERTO  String  ,
TOSERVERNAME    String  ,
TOSERVERIP      String  ,
TOSERVERPORT    int     ,
DELAY   decimal ,
DSN     String  ,
STATUS  String  ,
STATUSDESC      String  ,
AMAVISID        String  ,
coords ARRAY <Double>,
city String,
postalCode String,
areaCode String,
metroCode String,
region String,
country String)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001'
COLLECTION ITEMS TERMINATED BY ','
LOCATION '/user/hdfs/input/postfix/';
```

-- Tabla Hive para bluecoat

```
CREATE EXTERNAL TABLE ob_src_bluecoat(
eventTimeStamp            String,
timeTaken          int ,
clientIP String,
userCode            String,
userGroup         String,
Exception           String,
filterResult         String,
category              String,
referer String,
responseCode  int ,
action   String,
method               String,
contentType     String,
protocol              String,
requestDomain               String,
requestPort       int ,
requestPath      String,
requestQuery   String,
requestURIExtension    String,
userAgent          String,
serverIP              String,
scBytes                int ,
csBytes                int ,
virusID  String,
destinationIP    String,
coords ARRAY <Double>,
city String,
postalCode String,
areaCode String,
metroCode String,
region String,
country String
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001'
COLLECTION ITEMS TERMINATED BY ','
LOCATION '/user/hdfs/input/proxy';
```

-- Tabla Hive para ironport

```
CREATE EXTERNAL TABLE ob_src_ironport(
eventTimeStamp            String
,ICID      int
,MID      Int
,RID       Int
,DCID    Int
,SUBJECT             String
,MAILFROM       String
,MAILTO              String
,RESPONSE         String
,BYTES  Int
,INTERFACE        String
,PUERTO             Int
,INTERFACEIP    string
,HOSTIP               String
,HOSTNAME      String
,HOSTVERIFIED String
,DSNBOUNCE    String
,BOUNCEDESC  String
,SPAMCASE       String
,DCIDDELAY       Int
,MIDDELAY         Int
,RIDDELAY          Int
,DSNDELAY         String
,DELAYDESC       String
,ANTIVIRUS       String
,REPUTATION   String
,RANGO              String
,SCORE String
,FILTROCONTENIDO       String
,MARKETINGCASE          String,
coords ARRAY <Double>,
city String,
postalCode String,
areaCode String,
metroCode String,
region String,
country String
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001'
COLLECTION ITEMS TERMINATED BY ','
LOCATION '/user/hdfs/input/ironport/';
```

-- Tabla Hive para radius

```
```
