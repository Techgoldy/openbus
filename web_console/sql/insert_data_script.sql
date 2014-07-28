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

commit;
