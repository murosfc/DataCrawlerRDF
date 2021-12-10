package Crawler;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.OWL;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;


public class Impl implements SemanticCrawler{	
	//variáveis declaradas como atributo para economizar recurso de não instanciar a cada passagem da estrutura de repetição
	private ArrayList<String> URIsVisitadas = new ArrayList<>();	
	private CharsetEncoder enc = Charset.forName("ISO-8859-1").newEncoder(); 

	public Impl() {}
	
	public void search(Model model, String resourceURI) {		
		try {
			model.read(resourceURI); //Lê o RDF da URI		
			URIsVisitadas.add(resourceURI); //adiciona a URI à lista de visitadas
			StmtIterator statements = model.listStatements((Resource)null,OWL.sameAs,(RDFNode)null); //Coloca no statmentes apenas as triplas com o predicado OWN.sameAs
			while (statements.hasNext()) { // varre as triplas do statment				
				Statement statement = statements.nextStatement(); //adiciona a tripla corrente em um statment			
				Resource object = (Resource) statement.getObject();	//recebe o objeto da tripla em análise			
				
				//Verifica se URI já foi visitada para não adicioná-la novamente ao model
				Set<String> set = new HashSet<String>(URIsVisitadas);				  
				boolean isVisited = set.contains(object.getURI());				
				
				/*//monitoramento de URIs no console
				System.out.println("\nNova URI obtida: "+object.getURI());
				if (isVisited) System.out.println("URI: já visitada\n");*/
				
				if (enc.canEncode(object.getURI()) && !isVisited && !object.isAnon()) { //Testa se a URI é composta apenas caracteres do alfabeto latino (ISO 8859-1), se não foi visitada antes e se não é um nó em branco
					search(model, object.getURI());	//chama recursivamente o o método search para analisar a nova URI que é OWL.sameAs com o Sujeito								
				} else if (enc.canEncode(object.getURI()) && !isVisited && object.isAnon()) { //Testa se a URI é composta apenas caracteres do alfabeto latino (ISO 8859-1), se não foi visitada antes e se é um nó em branco
					System.out.println("\nNó em branco: "+object.getId().toString()+"\n");
					search(model, object.getId().toString()); //chama recursivamente o o método search para analisar o nó em branco que é OWL.sameAs com o Sujeito							
				}					
			}	
		//tratamento de erros                                        
		}catch (Exception e) {
			System.err.println ("Erro ao tentar abrir URI: "+e.getMessage());
		}catch (NoClassDefFoundError error) {
			System.err.println ("Erro de classe não encontrada ao tentar abrir URI: "+error.getMessage());
		}
	}


}
