package Impl;

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

import Crawler.SemanticCrawler;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;


public class SemanticCrawlerImpl implements SemanticCrawler{	
	private ArrayList<String> URIsVisitadas = new ArrayList<>();	
	private CharsetEncoder enc = Charset.forName("ISO-8859-1").newEncoder(); //variável declaradas como atributo para economizar recurso ao não instanciar a cada passagem da estrutura de repetição
	private Model tempModel = ModelFactory.createDefaultModel();
	
	public SemanticCrawlerImpl() {}
	
	public void search(Model model, String resourceURI) {		
		try {			
			tempModel.read(resourceURI); //Lê o RDF da URI num modelo temporário
			StmtIterator getTriplas = tempModel.listStatements(tempModel.createResource(resourceURI),(Property)null,(RDFNode)null); //lista no statement apenas as triplas com o Recurso sendo URI em análise
			model.add(getTriplas);	// Adiciona as trilas lidas ao modelo		
			URIsVisitadas.add(resourceURI); //adiciona a URI à lista de visitadas
			StmtIterator statements = model.listStatements(model.createResource(resourceURI),OWL.sameAs,(RDFNode)null); //Coloca no statmentes apenas as triplas com o predicado OWN.sameAs
			while (statements.hasNext()) { // varre as triplas do statment				
				Statement statement = statements.nextStatement(); //adiciona a tripla corrente em um statment			
				Resource object = (Resource) statement.getObject();	//recebe o objeto da tripla em análise			
				
				//Verifica se URI já foi visitada para não adicioná-la novamente ao model
				Set<String> set = new HashSet<String>(URIsVisitadas);				  
				boolean isVisited = set.contains(object.getURI());			
				
				//Acompanhamento de informações no console, bloco pode ser colocado como comentário ou excluído para reduzir este tipo de informação
				System.out.println("\nNova URI obtida: "+object.getURI());
				if (isVisited) System.out.println("URI: já visitada\n");
				
				if (enc.canEncode(object.getURI()) && !isVisited && !object.isAnon()) { //Testa se a URI é composta apenas caracteres do alfabeto latino (ISO 8859-1), se não foi visitada antes e se não é um nó em branco
					search(model, object.getURI());	//chama recursivamente o o método search para analisar a nova URI que é OWL.sameAs com o Sujeito								
				} else if (object.getURI() == null || object.isAnon()) { //Verifica se o Objéto é um nó em branco
					System.out.println("\nNó em branco: "+object.getId().toString()+"\n");
					trataNoEmBranco (model, object); //chama a função que adiciona nó em branco ao modelo									
				}
			}
		//tratamento de erros                                        
		}catch (Exception e) {
			System.err.println ("Erro ao tentar abrir URI: "+e.getMessage());
		}catch (NoClassDefFoundError error) {
			System.err.println ("Erro de classe não encontrada ao tentar abrir URI: "+error.getMessage());
		}
	}
	
	private void trataNoEmBranco(Model model, Resource object) {		
		StmtIterator stmSNosEmBranco = model.listStatements((Resource)object.getId(),(Property)null,(RDFNode)null); //lê todos as trilplas do nó em branco
		while (stmSNosEmBranco.hasNext()) { //varre as triplas do nó em branco
			Statement stmNoAtual = stmSNosEmBranco.nextStatement(); //Obtem a tripla atual do nó em branco			
			model.add((Resource) stmNoAtual.getObject(),(Property) stmNoAtual.getPredicate(), (RDFNode) stmNoAtual.getObject()); //adiciona a tripla ao modelo
		}		
		stmSNosEmBranco = model.listStatements((Resource)object.getId(),(Property)null,(RDFNode)null); //lê novamente para procurar outros nós em branco não encontrei na documentação do Jena um procedimento que coloca o cursor no início
		while (stmSNosEmBranco.hasNext()) {
			Statement stmNoAtual = stmSNosEmBranco.nextStatement(); //Obtem a tripla atual do nó em branco
			Resource objectNo = (Resource) stmNoAtual.getObject(); //Obtém o objeto para análise e envio de parâmentro caso necessário
			if (objectNo.isAnon()) { //Se o objeto é outro nó em branco
				trataNoEmBranco(model,objectNo); //chama recursivamente o "trataNoEmBranco" enviando o novo nó em branco obtido
			}
		}
	}
}