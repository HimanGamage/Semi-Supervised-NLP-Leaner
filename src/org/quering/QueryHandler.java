package org.quering;
import org.ont.JenaHandler;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
/*
 * Created by Himan Gamage
 * 
 * 
 * 
 */
public class QueryHandler {

	private static Model model = JenaHandler.getInstance().getOntModel();

	public static void queryingOntology(String subject, String predicate) {
		String queryString = " SELECT ?a ?b WHERE {?Batsman <"+JenaHandler.NS+"Batsman> \""+subject+"\" . ?Batsman <"+JenaHandler.NS+predicate+"> ?b  } " ;
		Query query = QueryFactory.create(queryString) ;
	
		  try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			    ResultSet results = qexec.execSelect() ;

			    for ( ; results.hasNext() ; )
			    {
			      QuerySolution soln = results.nextSolution() ;
			      System.out.println(soln);
//			     
			    }
			  }	
	}

	public static void queryingOntology(String predicate) {
		String queryString = " SELECT ?x ?a WHERE { ?x  <"+JenaHandler.NS+predicate+"> ?a } " ;
		Query query = QueryFactory.create(queryString) ;
	
		  try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			    ResultSet results = qexec.execSelect() ;
			    
			    for ( ; results.hasNext() ; )
			    {
			      QuerySolution soln = results.nextSolution() ;
			      System.out.println(soln);
//			      RDFNode x = soln.get("varName") ;       // Get a result variable by name.
			      Resource r = soln.getResource("x") ; // Get a result variable - must be a resource
			      
			      Literal l = soln.getLiteral("a") ;   // Get a result variable - must be a literal
			      System.out.println("local name:"+r.getLocalName()+" val:"+l);
			    }
			  }
		
	}

	

}
