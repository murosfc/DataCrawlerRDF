package Crawler;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;


public class Impl implements SemanticCrawler{
	private String URI;	
	
	public Impl(String URI) {
		System.out.println(URI);
		this.URI = URI;
		Model model = ModelFactory.createDefaultModel();			
		SemanticCrawler.search(model, URI);
	}
	

}
