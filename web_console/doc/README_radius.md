# Origen Radius

####1. Métrica *Radius* 

- **Origen de datos:** `ob_src_radius`
- **Tipo:** `Online`
- **Stream:** 
	1. Nombre: `radius`
	2. Campos: `ACS_Timestamp string,TIMESTAMP_MILLIS long,Access_Service string, User_Name string,Calling_Station_ID string,Authentication_Status string,Failure_Reason string
`
- **Tabla:** 
	1. Nombre: `hosts`
	2. Campos: `ID long, maquina string, mac string, cuenta long
`	
- **Queries:** 
	1. Query1: Inserta en la table toda petición de logeo de una máquina.
		1. Nombre: `Query1`
		2. Callback: `No`
		3. Orden: `1`
		4. Query from: `from radius[(User_Name contains 'host/')]
`
		5. Query into: `insert into hosts
`
		6. Query as: `select TIMESTAMP_MILLIS as ID, User_Name as maquina, Calling_Station_ID as mac, count(1) as cuenta 
`
		7. Query group by: 

	2. Query2: Borrado de todos los registros que no sean el último recibido por MAC. Esto se hace para mantener la última versión.
		1. Nombre: `Query2`
		2. Callback: `No`
		3. Orden: `2`
		4. Query from: `from radius[(User_Name contains 'host/')]
join hosts as hosts 
on radius.Calling_Station_ID == hosts.mac and hosts.ID<radius.TIMESTAMP_MILLIS`
		5. Query into: `delete hosts for current-events`
		6. Query as: `select radius.TIMESTAMP_MILLIS as ID,radius.User_Name as maquina, radius.Calling_Station_ID as mac, count(1) as cuenta 
`
		7. Query group by:

	3. Query3: Peticiones de usuarios por la interfaz 802.1x.

		1. Nombre: `Query3`
		2. Callback: `Sí`
		3. Orden: `3`
		4. Query from: `from radius[not(User_Name contains 'host/' and Access_Service=='802.1x_SanHQ')] 
`
		5. Query into: `insert into peticionesUsuario
`
		6. Query as: `select ACS_Timestamp as timestamp, TIMESTAMP_MILLIS as ID,User_Name as usuario, Calling_Station_ID as mac, Authentication_Status as status, Failure_Reason as motivo
`
		7. Query group by:

	4. Query4: Se obtiene el nombre de la máquina para los usuarios de Query4 cruzando por MAC
		1. Nombre: `Query4`
		2. Callback: `Sí`
		3. Orden: `4`
		4. Query from: `from peticionesUsuario as peticionesUsuario unidirectional
join 
hosts as hosts
on hosts.mac==peticionesUsuario.mac`
		5. Query into: `insert into peticionConMaquina
`
		6. Query as: `select timestamp as timestamp, hosts.ID as idHost, peticionesUsuario.ID as ID , usuario as usuario, maquina as maquina, peticionesUsuario.mac as mac, status as status, motivo as motivo
`
		7. Query group by:

	5. Query5: Como no hay left join, se obtiene la parte de Q3 que no cruza con la tabla, es decir, las personas para las cuales su MAC no hemos podido asignar nombre de host. La salida va al mismo Stream que Query4.
		1. Nombre: `Query5`
		2. Callback: `Sí`
		3. Orden: `5`
		4. Query from: `from peticionesUsuario[not(peticionesUsuario.mac==hosts.mac in hosts)]  
`
		5. Query into: `insert into peticionConMaquina
`
		6. Query as: `select timestamp as timestamp,0L as idHost, ID as ID, usuario as usuario, "not defined" as maquina, mac as mac, status as status, motivo as motivo
`
		7. Query group by:

	6. Query6: Se calcula el número de error consecutivo para cada usuario. La función es un count que se reinicia a 0 si la petición de acceso es OK
		1. Nombre: `Query6`
		2. Callback: `Sí`
		3. Orden: `6`
		4. Query from: `from peticionConMaquina#window.time(20000) 
`
		5. Query into: `insert into fallosConsecutivos for current-events 
`
		6. Query as: `select timestamp as timestamp, idHost as idHost, ID as ID, usuario as usuario, maquina as maquina, mac as mac, status as status, motivo as motivo, agregacion:sumConReset(1L,status,'Passed') as errorsConsecutivos,1 as tamano 
`
		7. Query group by: `group by usuario`

	7. Query7: Se cuentan cuantas de las peticiones de Query6 están por encima de las 2 peticiones fallidas consecutivas== bloqueo de la cuenta de usuario
		1. Nombre: `Query7`
		2. Callback: `Sí`
		3. Orden: `7`
		4. Query from: `from fallosConsecutivos[errorsConsecutivos>=2]#window.time(20000) 
`
		5. Query into: `insert into masDeTresFallos for current-events 
`
		6. Query as: `select timestamp as timestamp,idHost as idHost, ID as ID, usuario as usuario, maquina as maquina, mac as mac, status as status, motivo as motivo, errorsConsecutivos as errorsConsecutivos
`
		7. Query group by:
		
