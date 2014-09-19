delete from origen_estructurado_hs_campos_origen;
delete from origen_estructurado;
delete from campos_origen;
delete from metrica_batch;

alter table campos_origen auto_increment=1;

insert into origen_estructurado (is_kafka_online, kafka_topic, topology_name) values (true, 'ob_src_postfix', 'ob_src_postfix');
insert into origen_estructurado (is_kafka_online, kafka_topic, topology_name) values (true, 'ob_src_ironport', 'ob_src_ironport');
insert into origen_estructurado (is_kafka_online, kafka_topic, topology_name) values (true, 'ob_src_amavis', 'ob_src_amavis');
insert into origen_estructurado (is_kafka_online, kafka_topic, topology_name) values (true, 'ob_src_bluecoat', 'ob_src_bluecoat');

insert into campos_origen (nombre_campo, tipo_campo) values ('EVENTTIMESTAMP', 'timestamp');
insert into campos_origen (nombre_campo, tipo_campo) values ('SMTPDID', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('MSGID', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('CLEANUPID', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('QMGRID', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('SMTPID', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('ERRORID', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('CLIENTE', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('CLIENTEIP', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('ACCION', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('SERVER', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('SERVERIP', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('MESSAGEID', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('USERFROM', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('SIZE', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('NRCPT', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('USERTO', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('TOSERVERNAME', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('TOSERVERIP', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('TOSERVERPORT', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('DELAY', 'decimal');
insert into campos_origen (nombre_campo, tipo_campo) values ('DSN', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('STATUS', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('STATUSDESC', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('AMAVISID', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('TIMETAKEN', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('MSGID', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('CLIENTIP', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('USERCODE', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('USERGROUP', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('EXCEPTION', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('FILTERRESULT', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('CATEGORY', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('REFERER', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('RESPONSECODE', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('ACTION', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('METHOD', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('CONTENTTYPE', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('PROTOCOL', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('REQUESTDOMAIN', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('REQUESTPORT', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('REQUESTPATH', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('REQUESTQUERY', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('REQUESTURIEXCEPTION', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('USERAGENT', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('SERVERIP', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('SCBYTES', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('CSBYTES', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('VIRUSID', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('DESTINATIONIP', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('ICID', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('MID', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('RID', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('DCID', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('SUBJECT', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('MAILFROM', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('MAILTO', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('RESPONSE', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('BYTES', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('INTERFACE', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('PUERTO', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('INTERFACEIP', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('HOSTIP', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('HOSTNAME', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('HOSTVERIFIED', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('DSNBOUNCE', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('BOUNCEDESC', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('SPAMCASE', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('DCIDDELAY', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('MIDDELAY', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('RIDDELAY', 'int');
insert into campos_origen (nombre_campo, tipo_campo) values ('DSNDELAY', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('DELAYDESC', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('ANTIVIRUS', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('REPUTATION', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('RANGO', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('SCORE', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('FILTROCONTENIDO', 'string');
insert into campos_origen (nombre_campo, tipo_campo) values ('MARKETINGCASE', 'string');

insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 1);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 2);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 3);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 4);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 5);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 6);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 7);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 8);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 9);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 10);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 11);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 12);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 13);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 14);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 15);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 16);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 17);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 18);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 19);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 20);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 21);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 22);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 23);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 24);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_postfix'), 25);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 1);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 26);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 27);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 28);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 29);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 30);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 31);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 32);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 33);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 34);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 35);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 36);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 37);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 38);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 39);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 40);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 41);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 42);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 43);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 44);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 45);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 46);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 47);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 48);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 49);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_bluecoat'), 50);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 1);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 51);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 52);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 53);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 54);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 55);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 56);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 57);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 58);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 59);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 60);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 61);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 62);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 63);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 64);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 65);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 66);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 67);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 68);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 69);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 70);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 71);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 72);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 73);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 74);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 75);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 76);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 77);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 78);
insert into origen_estructurado_hs_campos_origen (origen_estructurado, hs_campos_origen) 
values ((select id from origen_estructurado where origen_estructurado.kafka_topic = 'ob_src_ironport'), 79);

commit;
