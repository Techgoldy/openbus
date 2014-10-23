
public class Tuple2StreamTest {

	

	 public static void main(String[] args) {
		 
		 String entrada="MSGID string, USERFROM string, TOUSER string,DSN string, SIZE int";
		 String[] campos=entrada.split(",");
		 for(int i=0;i<campos.length;i++){
			 campos[i]=campos[i].trim().split(" ")[0];
		 }
		 System.out.println("eee");
	 }
}
