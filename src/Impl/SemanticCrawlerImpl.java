package Impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
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
	private CharsetEncoder enc = Charset.forName("ISO-8859-1").newEncoder(); //vari�vel declaradas como atributo para economizar recurso ao n�o instanciar a cada passagem da estrutura de repeti��o

	public SemanticCrawlerImpl() {}
	
	public void search(Model model, String resourceURI) {		
		try {
			model.read(resourceURI); //L� o RDF da URI		
			URIsVisitadas.add(resourceURI); //adiciona a URI � lista de visitadas
			StmtIterator statements = model.listStatements((Resource)null,OWL.sameAs,(RDFNode)null); //Coloca no statmentes apenas as triplas com o predicado OWN.sameAs
			while (statements.hasNext()) { // varre as triplas do statment				
				Statement statement = statements.nextStatement(); //adiciona a tripla corrente em um statment			
				Resource object = (Resource) statement.getObject();	//recebe o objeto da tripla em an�lise			
				
				//Verifica se URI j� foi visitada para n�o adicion�-la novamente ao model
				Set<String> set = new HashSet<String>(URIsVisitadas);				  
				boolean isVisited = set.contains(object.getURI());			
				
				//Acompanhamento de informa��es no console, bloco pode ser colocado como coment�rio ou exclu�do para reduzir este tipo de informa��o
				System.out.println("\nNova URI obtida: "+object.getURI());
				if (isVisited) System.out.println("URI: j� visitada\n");
				
				if (enc.canEncode(object.getURI()) && !isVisited && !object.isAnon()) { //Testa se a URI � composta apenas caracteres do alfabeto latino (ISO 8859-1), se n�o foi visitada antes e se n�o � um n� em branco
					search(model, object.getURI());	//chama recursivamente o o m�todo search para analisar a nova URI que � OWL.sameAs com o Sujeito								
				} else if (object.getURI() == null || object.isAnon()) { //Verifica se o Obj�to � um n� em branco
					System.out.println("\nN� em branco: "+object.getId().toString()+"\n");
					trataNoEmBranco (model, object); //chama a fun��o que adiciona n� em branco ao modelo									
				}
			}
		//tratamento de erros                                        
		}catch (Exception e) {
			System.err.println ("Erro ao tentar abrir URI: "+e.getMessage());
		}catch (NoClassDefFoundError error) {
			System.err.println ("Erro de classe n�o encontrada ao tentar abrir URI: "+error.getMessage());
		}
	}
	
	private void trataNoEmBranco(Model model, Resource object) {		
		StmtIterator stmSNosEmBranco = model.listStatements((Resource)object.getId(),(Property)null,(RDFNode)null); //l� todos as trilplas do n� em branco
		while (stmSNosEmBranco.hasNext()) { //varre as triplas do n� em branco
			Statement stmNoAtual = stmSNosEmBranco.nextStatement(); //Obtem a tripla atual do n� em branco			
			model.add((Resource) stmNoAtual.getObject(),(Property) stmNoAtual.getPredicate(), (RDFNode) stmNoAtual.getObject()); //adiciona a tripla ao modelo
		}		
		stmSNosEmBranco = model.listStatements((Resource)object.getId(),(Property)null,(RDFNode)null); //l� novamente para procurar outros n�s em branco n�o encontrei na documenta��o do Jena um procedimento que coloca o cursor no in�cio
		while (stmSNosEmBranco.hasNext()) {
			Statement stmNoAtual = stmSNosEmBranco.nextStatement(); //Obtem a tripla atual do n� em branco
			Resource objectNo = (Resource) stmNoAtual.getObject(); //Obt�m o objeto para an�lise e envio de par�mentro caso necess�rio
			if (objectNo.isAnon()) { //Se o objeto � outro n� em branco
				trataNoEmBranco(model,objectNo); //chama recursivamente o "trataNoEmBranco" enviando o novo n� em branco obtido
			}
		}
	}
}