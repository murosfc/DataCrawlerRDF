package Crawler;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.shared.JenaException;
import org.apache.jena.vocabulary.OWL;





public interface SemanticCrawler {
	ArrayList<String> URIsVisitadas = new ArrayList<>();	
	CharsetEncoder enc = Charset.forName("ISO-8859-1").newEncoder();
	
	public static void search(Model model, String resourceURI) {		
		try {
			model.read(resourceURI);
			URIsVisitadas.add(resourceURI);		
			StmtIterator statements = model.listStatements((Resource)null,OWL.sameAs,(RDFNode)null);
			while (statements.hasNext()) {
				Statement statement = statements.nextStatement();			
				Resource ObjResource = (Resource) statement.getObject();
				if (enc.canEncode(ObjResource.getURI()) && !URIsVisitadas.contains(ObjResource.getURI()) && !ObjResource.isAnon()) { //Tem URI
					search(model, ObjResource.getURI());
					System.out.println("URI obtida: "+ObjResource.getURI());
				} else if (enc.canEncode(ObjResource.getURI()) && !URIsVisitadas.contains(ObjResource.getURI()) && ObjResource.isAnon()) { //Nó em branco
					search(model, ObjResource.getId().toString());
					System.out.println("Nó em branco: "+ObjResource.getId().toString());
				}					
			}			
		}catch (JenaException e) {
			System.err.println ("Erro ao abrir URI: "+e.getMessage());
		}
		
		}

}
