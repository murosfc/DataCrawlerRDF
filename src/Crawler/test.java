package Crawler;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class test {
	
	public static void main(String[] args) {
		String URI = "http://dbpedia.org/resource/Zico";	
		Model model = ModelFactory.createDefaultModel();			
		Impl ObjImpl = new Impl();
		ObjImpl.search(model, URI);
		System.out.println("Busca encerrada\n\nRDF:\n");		
		model.write(System.out, "N3");	
	}


}
