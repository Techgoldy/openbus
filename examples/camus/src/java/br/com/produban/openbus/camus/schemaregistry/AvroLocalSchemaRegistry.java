package br.com.produban.openbus.camus.schemaregistry;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.CRC32;

import org.apache.avro.Schema;
import org.apache.avro.specific.AvroGenerated;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.log4j.Logger;

import com.linkedin.camus.schemaregistry.SchemaDetails;
import com.linkedin.camus.schemaregistry.SchemaRegistry;

/**
 * This is a little dummy registry that just uses a memory-backed schema
 * registry to store two dummy Avro schemas. You can use this with
 * camus.properties
 */
public class AvroLocalSchemaRegistry implements SchemaRegistry<Schema> {
	private final static String SCHEMA_PACKAGE = "kafka.registry.schemaPackage";
	private final Map<Integer, Schema> schemasById;
	
	public AvroLocalSchemaRegistry() {
		schemasById = new ConcurrentHashMap<Integer, Schema>();
	}

	public void init(Properties properties) {
		// classpath scanning
		String packageName = properties.getProperty(SCHEMA_PACKAGE);
		System.out.println("Schema Package:" +packageName);
		List<Class> classes = scan(packageName);
		
		// Register every Record's Schema 
		for (Class<?> clazz : classes) {
			try {
				SpecificRecordBase genericRecord = (SpecificRecordBase) clazz.newInstance();
				register(null, genericRecord.getSchema());
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String register(String topic, Schema schema) {
		/* register to id-Schema Map */
		byte[] schemaBytes = schema.toString().getBytes();
		
		// calculo do crc32
		CRC32 checksum = new CRC32(); 
		checksum.reset();
		checksum.update(schemaBytes, 0, schemaBytes.length);
		
		// crc32 em string
		int id = (int) checksum.getValue();
		
		// register Schema
		schemasById.put(id, schema);
		
		Logger.getLogger(this.getClass()).info("\t\t\tRegister Schema : ID = " + id ); 
		Logger.getLogger(this.getClass()).info("\t\t\tRegister Schema : " + schema.toString());
		
		return String.valueOf(id);
	}

	@Override
	public Schema getSchemaByID(String topic, String id) {
		return schemasById.get(Integer.parseInt(id));
	}

	@Override
	public SchemaDetails<Schema> getLatestSchemaByTopic(String topic) {
		// Schema is not mapped by topic on this implementation
		return null;
	}
	
	public List<Class> scan(String packageName){
		Logger.getLogger(this.getClass()).info("Scan : PackageName " + packageName);
		List<Class> classList = new ArrayList<Class>();
		
		// Thread classpath scanning
		String classpath = System.getProperty("java.class.path");
		String[] cpEntries = classpath.split(File.pathSeparator);
		
		// convert package to path string 
		packageName = packageName.replace(".", "/");
		
		// jar lookup in every classpath entry
		for (String cpEntry : cpEntries) {
			// only .jar files
			if (!cpEntry.contains("/lib/") && cpEntry.endsWith(".jar") && !cpEntry.contains("*") && !(new File(cpEntry).isDirectory())) {
				Logger.getLogger(this.getClass()).info("\tScanning entry : " + cpEntry);
				try {
					// look for every entry inside .jar file
					JarURLConnection jarURLConnection = (JarURLConnection) new URL("jar:file:" + cpEntry + "!/").openConnection();
					JarFile jarFile = jarURLConnection.getJarFile();
					Enumeration<JarEntry> entries = jarFile.entries();
					
					while (entries.hasMoreElements()) {
						String path = entries.nextElement().getName();
						
						// filter: classes inside a specific package, excluding inner classes
						if (path.endsWith(".class") && path.startsWith(packageName) && !path.contains("$")) {
							String className = path.replace("/", ".").substring(0, path.length() - 6);
							// filter: AvroGenerated classes only  
							Class clazz = Class.forName(className);
							if (clazz.isAnnotationPresent(AvroGenerated.class)) {
								classList.add(clazz);
								Logger.getLogger(this.getClass()).info("\t\tAvro Generated Schema found : " + className);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		
		return classList;
	}
}
