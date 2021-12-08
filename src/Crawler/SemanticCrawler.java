package Crawler;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;



public interface SemanticCrawler {
	ArrayList<String> URIsVisitadas = new ArrayList<>();
	
	public static void search(Model model, String resourceURI) {
		System.out.println(resourceURI);
		model.read(resourceURI);
		URIsVisitadas.add(resourceURI);		
		StmtIterator statements = model.listStatements((Resource)null,OWL.sameAs,(RDFNode)null);
		while (statements.hasNext()) {
			Statement statement = statements.nextStatement();
			if (statement.getObject() instanceof Resource) {
				Resource ObjResource = (Resource) statement.getObject();
				if (!URIsVisitadas.contains(ObjResource.getURI())) {
					search(model, ObjResource.getURI());
					System.out.println("URI obtida: "+ObjResource.getURI());
				}
			} else {
				RDFNode ObjNode = (RDFNode) statement.getObject();
			}			
		}
		}

}
