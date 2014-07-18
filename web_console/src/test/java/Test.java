import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;



public class Test {

    public static void main(String[] args) {
	String s = "SELECT " +
	"CONCAT(YEAR(eventTimeStamp),MONTH(eventTimeStamp)) as ID,"+
	"MONTH(eventTimeStamp) as MES,"+
	"YEAR(eventTimeStamp) as ANO,"+
	"MAX(eventTimeStamp) as  ULTIMO,"+
	"test as  TEST,"+
	"MIN(eventTimeStamp) as PRIMERO";
	
	String ss = "SELECT "+
	"CONCAT(ANO,MES) as ID,	MES,ANO,SUM(TAMANO*cuenta) TAMANO_ok,sum(cuenta) as CUENTA_OK "+
	"FROM(SELECT MSGID,MONTH(eventTimeStamp) as MES,YEAR(eventTimeStamp) as ANO,count(1) as cuenta "+
	"FROM POSTFIX_LOGS WHERE DSN in('2.0.0','2.6.0','2.4.0') and AMAVISID ='null' group by MSGID,MONTH(eventTimeStamp),YEAR(eventTimeStamp)) correo "+
	"JOIN (SELECT MSGID,SUM(SIZE) as TAMANO";
	
	Map<String,String> hmSelectFields = new LinkedHashMap<String,String>();
	Map<String,String> hmSelectFieldsModif = new LinkedHashMap<String,String>();
	ss = ss.substring(ss.indexOf("ID,")+3,ss.length());
	String key = null;
    	String value = null;
	for (String firstCharacter : ss.split(",")){
	    for (String secondCharacter : firstCharacter.split(" as ")){
		if (key != null && value != null){
		    hmSelectFields.put(key, value);
		    key = null;
		    value = null;
		}
		if (key == null){
		    key = secondCharacter;
		}
		else if(value == null){
		    value = secondCharacter;
		}
	    }
	}
	hmSelectFields.put(key, value);
	System.out.println(hmSelectFields);
	for (Map.Entry entry : hmSelectFields.entrySet()){
	    String keyModif = entry.getKey().toString();
	    String valueModif = entry.getValue().toString();
	    if (keyModif.indexOf("(") != -1){
		keyModif = keyModif.substring(0,keyModif.indexOf("("));
	    }
	    hmSelectFieldsModif.put(keyModif,valueModif);
	}
	System.out.println(hmSelectFieldsModif);
    }

}
