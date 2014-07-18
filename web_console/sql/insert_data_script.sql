delete from origen_estructurado_hs_campos_origen;
delete from origen_estructurado;
delete from campos_origen;

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

commit;
