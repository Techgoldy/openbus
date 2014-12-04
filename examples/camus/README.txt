Premissas:

	Kafka 0.8
	Hadoop 2.x


Ajustes:

1. Clonar o projeto Camus do GitHub do Linkedin:

		https://github.com/linkedin/camus

2. Adicionar todos os projetos camus-* no workspace

3. Adicionar o projeto pai (camus-parent) no workspace

4. No pom.xml do camus-etl-kafka e do camus-kafka-coders, trocar a dependcy do kafka por: 
 	
	    <dependency>
	        <groupId>org.apache.kafka</groupId>
	        <artifactId>kafka_2.10</artifactId>
	        <version>0.8.0</version>
	        <exclusions>
	          <exclusion>
	            <groupId>org.slf4j</groupId>
	            <artifactId>slf4j-simple</artifactId>
	          </exclusion>
	        </exclusions>
	    </dependency>

5. No pom.xml do  projeto Camus, subir a versão do Hadoop Client para 2.2.0:
			<dependency>
				<groupId>org.apache.hadoop</groupId>
				<artifactId>hadoop-client</artifactId>
				<version>2.2.0</version>
			</dependency>

6. Fazer o build com o Maven

7. Copiar os arquivos abaixo para o servidor que contém o Hadoop:

		schemas/openbus-model-0.0.1-SNAPSHOT.jar
		openbus.camus-0.0.1-SNAPSHOT-shaded.jar
		camus.properties
		log4j.xml
		runJob.sh
		


		