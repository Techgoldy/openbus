#Logstash / Kafka

Desde la máquina donde esté instalado Kafka:
- Arrancar el servidor de zookeeper si no lo estuviera ya:
`/root/kafka_2.9.2-0.8.1.1/bin/zookeeper-server-start.sh config/zookeeper.properties &`

- Arrancar el servidor de kafka si no lo estuviera ya:
`/root/kafka_2.9.2-0.8.1.1/bin/kafka-server-start.sh config/server.properties &`

- Crear un topico kafka para el nuevo orígen:
`/root/kafka_2.9.2-0.8.1.1bin/kafka-topics.sh --create --zookeeper 180.133.240.174:218,180.133.240.175:2181,180.133.240.176:2181 --replication-factor 1 --partitions 1 --topic nuevo_topico_origen`


Desde la máquina donde esté instalado Logstash:
- Configurar Logstash para recibir la información del nuevo origen al tópico de kafka creado anteriormente:
	- Editar el archivo /root/software/logstash-1.4.1/conf/kafka.conf. 
	- Añadir en la etiqueta output la condición de filtrado del nuevo origen:
```
else if [program] == "cabecera_propia_del_nuevo_origen"{
	kafka{
		topic_id => "nombre_del_nuevo_topico"
		broker_list => "180.133.240.165:9092,180.133.240.166:9092,180.133.240.167:9092"
		codec => plain {
			format => "%{message}"
		}			
	}
}
```

Desde la máquina donde esté instalado Logstash:
- Parar primero y arrancar después parar el servidor de Logstash con la configuración creada anteriormente:
`nohup /root/software/logstash-1.4.1/bin/logstash -f kafka.conf --log ./logs_logstash.log -w 1 -v &`

##Configuración previa de Logstash##

Frameworks:

- logstash-1.4.1 : Distribución de logstash
- kafka_2.9.2-0.8.1.1 : Distribución de apache kafka
- logstash-kafka-0.5.0 : Plugin de kafka para Logstash
- jruby-1.7.12 : Distribución de JRuby
- jdk1.7.0_51 : Distribución de Java

Instalación:

- Instalar las versiones de Java y JRuby
- Extraer los frameworks cada uno en un directorio, por ejemplo:
 
/opt/logstash-1.4.1
/opt/kafka_2.9.2-0.8.1.1
/opt/logstash-kafka-0.5.0

- Copiar todos los archivos .jar de /opt/kafka_2.9.2-0.8.1.1/libs a /opt/logstash-1.4.1/vendor/jar/kafka_2.9.2-0.8.1.1/libs
````
mkdir /opt/logstash-1.4.1/vendor/jar/kafka_2.9.2-0.8.1.1
mkdir /opt/logstash-1.4.1/vendor/jar/kafka_2.9.2-0.8.1.1/libs
cp /opt/kafka_2.9.2-0.8.1.1/libs/*.jar /opt/logstash-1.4.1/vendor/jar/kafka_2.9.2-0.8.1.1/libs
````
- Copiar todos los archivos .jar de /opt/logstash-kafka-0.5.0/lib a /opt/logstash-1.4.1/lib
```
cp -R /opt/logstash-kafka-0.5.0/lib  /opt/logstash-1.4.1/lib
```

- Desde /opt/logstash-1.4.1 lanzar un script ruby para instalar la librería jruby-kafka en logstash
```
cd /opt/logstash-1.4.1
GEM_HOME=vendor/bundle/jruby/1.9 GEM_PATH= java -jar vendor/jar/jruby-complete-1.7.11.jar --1.9 ../logstash-kafka-0.5.0/gembag.rb ../logstash-kafka-0.5.0/logstash-kafka.gemspec
```

Logstash:

- Crear el archivo kafka.conf (por ejemplo) con la configuración necesaria para indicar como parámetros de entrada syslog y de salida apache kafka a un tópico determinado.

```
# Configuración de entrada, en este caso se recibe la información por syslog
input {
  syslog{
    type => syslog
    port => 514
  }
}

# Filtros, se recoge una parte del mensaje en la variable mensaje para su posterior uso
filter {
  grok {
    match => { "message" => "((\S+)\|\|)?(?<content>(.*))"}
  }
  mutate {
    replace => ["message", "%{content}"]
  }
}

# Configuración de salida, se redirecciona a un tópico kafka dependiendo de la variable program
output {
        if [program] == "Multientidad.NetIX.LogicalEntity.ColaborativeServer.production.postfix.postfix"{
                kafka{
					topic_id => "ob_src_postfix"
					broker_list => "180.133.240.165:9092,180.133.240.166:9092,180.133.240.167:9092"
					codec => plain {
						format => "%{message}"
					}
                }
    }
    else if [program] == "Multientidad.NetIX.LogicalEntity.ColaborativeServer.production.postfix.amavis"{
                kafka{
					topic_id => "ob_src_amavis"
					broker_list => "180.133.240.165:9092,180.133.240.166:9092,180.133.240.167:9092"
					codec => plain {
						format => "%{message}"
					}			
                }
    }
    else if [program] == "Multientidad.NetIX.CommunicationSystems.MTA.production.IronPort.mail"{
                kafka{
					broker_list => "180.133.240.165:9092,180.133.240.166:9092,180.133.240.167:9092"
					topic_id => "op_src_ironport"
					codec => plain {
						format => "%{message}"
					}
                }
    }
    else if [program] == "Multientidad.NetIX.CommunicationSystems.Proxy.production.Bluecoat.bopxnxusrsp01.main"{
                kafka{
					topic_id => "ob_src_bluecoat"
					broker_list => "180.133.240.165:9092,180.133.240.166:9092,180.133.240.167:9092"
					codec => plain {
						format => "%{message}"
					}
                }
	}
    else if [program] == "Multientidad.NetIX.CommunicationSystems.Proxy.production.Bluecoat.bepxnxusrsp01.main"{
                kafka{
                                        topic_id => "ob_src_bluecoat"
                                        broker_list => "180.133.240.165:9092,180.133.240.166:9092,180.133.240.167:9092"
                                        codec => plain {
                                                format => "%{message}"
                                        }
                }
        }

	else{
		file{
			path => '/root/software/logstash-1.4.1/error.out'
		}
	}
}

```

- Arrancar el servidor de Logstash con la configuración creada anteriormente:
``` 
 /opt/logstash-1.4.1/bin/logstash -f kafka.conf
```

