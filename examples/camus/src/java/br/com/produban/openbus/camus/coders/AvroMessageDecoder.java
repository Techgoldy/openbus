package br.com.produban.openbus.camus.coders;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;

import com.linkedin.camus.coders.CamusWrapper;
import com.linkedin.camus.coders.MessageDecoder;
import com.linkedin.camus.coders.MessageDecoderException;
import com.linkedin.camus.etl.kafka.coders.KafkaAvroMessageDecoder.CamusAvroWrapper;
import com.linkedin.camus.etl.kafka.coders.KafkaAvroMessageEncoder;
import com.linkedin.camus.schemaregistry.SchemaRegistry;

public class AvroMessageDecoder extends MessageDecoder<byte[], Record>  {
	protected DecoderFactory decoderFactory;
	protected SchemaRegistry<Schema> registry;
	private static final byte MAGIC_BYTE = 0x0;
	
	@SuppressWarnings("unchecked")
	@Override
	public void init(Properties props, String topicName) {
		super.init(props, topicName);
		
	    try {
	    	// init registry configured in properties
	    	SchemaRegistry<Schema> registry = (SchemaRegistry<Schema>) Class.forName(
                            props.getProperty(KafkaAvroMessageEncoder.KAFKA_MESSAGE_CODER_SCHEMA_REGISTRY_CLASS)
                       ).newInstance();
            registry.init(props);
            
            // use Cached Registry
            this.registry = registry;// TODO new CachedSchemaRegistry<Schema>(registry);
            
            decoderFactory = DecoderFactory.get();
            
        } catch (Exception e) {
            throw new MessageDecoderException(e);
        }
	}

	@Override
	public CamusWrapper<Record> decode(byte[] message) {
		try {
			// check & discard MAGIC_BYTE
			ByteBuffer buffer = getByteBuffer(message);
			
			// read ID
			int id = buffer.getInt();
			
			// find schema using id
			Schema schema = registry.getSchemaByID(topicName, String.valueOf(id));
			
			DatumReader<Record> reader = new GenericDatumReader<Record>(schema);

//			Logger.getLogger(this.getClass()).info(" ***** ID = " + id);
//			Logger.getLogger(this.getClass()).info(" ***** Schema = " + schema.toString());
//			Logger.getLogger(this.getClass()).info(" ***** TopicName = " + topicName);
//			Logger.getLogger(this.getClass()).info(" ***** Buffer: Position = " + buffer.position() + " ; Remaining = " + buffer.remaining());
						
			return new CamusAvroWrapper(
					reader.read(
							null,
							decoderFactory.binaryDecoder(
									buffer.array(),
									buffer.position(),
									buffer.remaining(),
									null)));
	
		} catch (IOException e) {
			throw new MessageDecoderException(e);
		}
	}
	
	private ByteBuffer getByteBuffer(byte[] payload) {
		ByteBuffer buffer = ByteBuffer.wrap(payload);
		if (buffer.get() != MAGIC_BYTE)
			throw new IllegalArgumentException("Unknown magic byte!");
		return buffer;
	}
}