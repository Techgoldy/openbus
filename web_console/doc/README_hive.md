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
CREATE EXTERNAL TABLE ob_src_radius(
ID int 
,Message_Text string 
,TIMESTAMP_MILLIS LONG 
,ACS_Timestamp timestamp 
,ACSView_Timestamp timestamp 
,ACS_Server string 
,ACS_Session_ID string 
,Access_Service string 
,Service_Selection_Policy string 
,Authorization_Policy string 
,User_Name string 
,Identity_Store string 
,Authentication_Method string 
,Network_Device_Name string 
,Identity_Group string 
,Network_Device_Groups string 
,Calling_Station_ID string 
,NAS_Port string 
,Service_Type string 
,Audit_Session_ID string 
,CTS_Security_Group string 
,Failure_Reason string 
,Use_Case string 
,Framed_IP_Address string 
,NAS_Identifier string 
,NAS_IP_Address string 
,NAS_Port_Id int 
,Cisco_AV_Pair string 
,AD_Domain string 
,Response_Time int 
,Passed int 
,Failed int 
,Authentication_Status string 
,Radius_Daignostic_link string 
,Active_Session_Link string 
,ACS_UserName string
,NAC_Role string
,NAC_Policy_Compliance string
,NAC_Username string
,NAC_Posture_Token string
,Selected_Posture_Server string
,Selected_Identity_Store string
,Authentication_Identity_Store string
,Authorization_Exception_Policy_Matched_Rule string
,External_Policy_Server_Matched_Rule string
,Group_Mapping_Policy_Matched_Rule string
,Identity_Policy_Matched_Rule string
,NAS_Port_Type string
,Query_Identity_Stores string
,Selected_Authorization_Profiles string
,Selected_Exception_Authorization_Profiles string
,Selected_Query_Identity_Stores string
,Tunnel_Details string
,Cisco_H323_Attributes string
,Cisco_SSG_Attributes string
,Other_Attributes string
,More_Details string
,EAP_Tunnel string
,EAP_Authentication string
,Eap_Tunnel string
,Eap_Authentication string
,RADIUS_User_Name string
,NAS_Failure string
,timestamp string
,Response string
,TOTAL_COLUMN_0 string
,TOTAL_COLUMN_1 string
)
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\001'
COLLECTION ITEMS TERMINATED BY ','
LOCATION '/user/hdfs/input/radius';
```
