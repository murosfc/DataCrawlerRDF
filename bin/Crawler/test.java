package Crawler;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class test {
	
	public static void main(String[] args) {
		String URI = "http://dbpedia.org/resource/Zico";
		//String URI = "http://dbpedia.org/resource/Roger_Federer";	
		Model model = ModelFactory.createDefaultModel();			
		Impl ObjImpl = new Impl();
		ObjImpl.search(model, URI);		
		model.write(System.out, "TTL");	
	}


}
