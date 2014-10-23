import java.util.Set;
import java.util.TreeSet;

import com.produban.openbus.webservice.QueryCep;


public class SorterTest {
	public static void main(String[] args) {
	
		
		QueryCep q1 = new QueryCep();
		QueryCep q2 = new QueryCep();
		QueryCep q3 = new QueryCep();
		QueryCep q4 = new QueryCep();
		QueryCep q5 = new QueryCep();
		
		q1.setQueryName("q1");
		q2.setQueryName("q2");
		q3.setQueryName("q3");
		q4.setQueryName("q4");
		q5.setQueryName("q5");
		
		q1.setQueryOrder(1);
		q2.setQueryOrder(30);
		q3.setQueryOrder(4);
		q4.setQueryOrder(1);
		q5.setQueryOrder(0);
		
		Set<QueryCep> set= new TreeSet<QueryCep>();
		
		set.add(q1);
		set.add(q2);
		set.add(q3);
		set.add(q4);
		set.add(q5);
		
		System.out.println("pre sort");
		for(QueryCep q:set){
			System.out.println(q.getQueryName());
		}
		
		Set<QueryCep> setSort= new TreeSet<QueryCep>(set);
		System.out.println("post sort");
		for(QueryCep q:setSort){
			System.out.println(q.getQueryName());
		}
		
		for(QueryCep q:set){
			System.out.println(q.getQueryName());
		}
	}
}
