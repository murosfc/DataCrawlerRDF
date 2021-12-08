package Crawler;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;


public class Impl implements SemanticCrawler{
	private String URI;
	private Model model;
	
	public Impl(String URI) {
		System.out.println(URI);
		this.URI = URI;
		System.out.println("Gate 1");
		this.model = ModelFactory.createDefaultModel();
		System.out.println("Gate 2");
		SemanticCrawler.search(model, URI);
	}
	

}
