package org.quering;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;






import net.sf.extjwnl.JWNLException;

import org.ont.JenaHandler;
import org.postagging.POSTagging;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;


public class QueryHandler {
	
	private static Model model = JenaHandler.getInstance().getOntModel();


	public static List<QuerySolution> queryingOntologySubject(String subject) {
		String queryString = " SELECT ?x ?y  WHERE { <" + JenaHandler.NS + subject + "> ?x  ?y   } ";
		Query query = QueryFactory.create(queryString);
		System.out.println("Q :" + query);

		List<QuerySolution> qs=new ArrayList<QuerySolution>();
		
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				qs.add(soln);
				System.out.println("Solution :"+soln);
		//		RDFNode x = soln.get("a"); // Get a result variable by name.
				Resource r = soln.getResource("x"); // Get a result variable -
													// must be a resource

				// Literal l = soln.getLiteral("a") ; // Get a result variable -
				// must be a literal
			//	System.out.println("local name:" + r.getLocalName()+" & localname:"+);
			}
		}
		return qs;
	}

	public static List<QuerySolution> queryingOntologySubjectWithBe(String subject,String predicate) {
		String queryString = " SELECT ?x ?y  WHERE {{ <" + JenaHandler.NS + subject + "> ?x  ?y} UNION { ?z  ?x   <" + JenaHandler.NS + subject + ">}UNION { ?z  <" + JenaHandler.NS + predicate + ">   <" + JenaHandler.NS + subject + ">} }";
		Query query = QueryFactory.create(queryString);
		System.out.println("Q :" + query);

		List<QuerySolution> qs=new ArrayList<QuerySolution>();
		
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				qs.add(soln);
				System.out.println("Solution :"+soln);
		//		RDFNode x = soln.get("a"); // Get a result variable by name.
				Resource r = soln.getResource("x"); // Get a result variable -
													// must be a resource

				// Literal l = soln.getLiteral("a") ; // Get a result variable -
				// must be a literal
			//	System.out.println("local name:" + r.getLocalName()+" & localname:"+);
			}
		}
		return qs;

	}
	public static List<QuerySolution> queryingOntologyPredicate(String predicate) {
		String queryString = " SELECT ?x ?a WHERE { ?x  <" + JenaHandler.NS + predicate + "> ?a } ";
		Query query = QueryFactory.create(queryString);
		System.out.println("Q :" + query);

		List<QuerySolution> qs=new ArrayList<QuerySolution>();
		
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				qs.add(soln);
				System.out.println(soln);
				RDFNode x = soln.get("a"); // Get a result variable by name.
				Resource r = soln.getResource("x"); // Get a result variable -
													// must be a resource

				// Literal l = soln.getLiteral("a") ; // Get a result variable -
				// must be a literal
				System.out.println("local name:" + r.getLocalName() + " val:" + x.toString());
			}
		}
		return qs;
	}
	public static List<QuerySolution> queryingOntologyObject(String object) {
		String queryString = " SELECT ?x ?y  WHERE { ?x  ?y  <" + JenaHandler.NS + object + "> } ";
		Query query = QueryFactory.create(queryString);
		System.out.println("Q :" + query);



		List<QuerySolution> qs=new ArrayList<QuerySolution>();
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				qs.add(soln);
				System.out.println("Solution :"+soln);
		//		RDFNode x = soln.get("a"); // Get a result variable by name.
				Resource r = soln.getResource("x"); // Get a result variable -
													// must be a resource

				// Literal l = soln.getLiteral("a") ; // Get a result variable -
				// must be a literal
			//	System.out.println("local name:" + r.getLocalName()+" & localname:"+);
			}
		}
		return qs;
	}
	
	public static List<QuerySolution> queryingOntologySubjectPredicate(String subject,String predicate) {
		String queryString = " SELECT ?x  WHERE { <" + JenaHandler.NS + subject + ">  <" + JenaHandler.NS + predicate + "> ?x  } ";
		Query query = QueryFactory.create(queryString);
		System.out.println("Q :" + query);

		List<QuerySolution> qs=new ArrayList<QuerySolution>();
		
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				qs.add(soln);
				System.out.println(soln);
		//		RDFNode x = soln.get("a"); // Get a result variable by name.
				Resource r = soln.getResource("x"); // Get a result variable -
													// must be a resource

				// Literal l = soln.getLiteral("a") ; // Get a result variable -
				// must be a literal
				System.out.println("local name:" + r.getLocalName());
			}
		}
		return qs;
	}


	public static List<QuerySolution> queryingOntologyPredicateObject(String predicate,String object) {
		String queryString = " SELECT ?x  WHERE { ?x  <" + JenaHandler.NS + predicate + ">  <" + JenaHandler.NS + object + "> } ";
		Query query = QueryFactory.create(queryString);

		System.out.println("Q :" + query);

		List<QuerySolution> qs=new ArrayList<QuerySolution>();
		
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				qs.add(soln);
				System.out.println(soln);
		//		RDFNode x = soln.get("a"); // Get a result variable by name.
				Resource r = soln.getResource("x"); // Get a result variable -
													// must be a resource

				// Literal l = soln.getLiteral("a") ; // Get a result variable -
				// must be a literal
				System.out.println("local name:" + r.getLocalName());
			}
		}
		return qs;
	}


}
