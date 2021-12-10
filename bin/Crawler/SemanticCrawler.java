package Crawler;

import org.apache.jena.rdf.model.Model;

public interface SemanticCrawler {		
	
	public  void search(Model model, String resourceURI) ;

}
