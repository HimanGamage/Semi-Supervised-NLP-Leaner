import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Profile;
import com.hp.hpl.jena.ontology.ProfileRegistry;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.*;

public class A {

	public static void main(String args[]){
		JenaHandler handler=JenaHandler.getInstance();
		/*
		 * This is a test class for run the program
		 * 
		 * 
		 * 
		 */
		
		
		
		
		
	
			
		try {
			//handler.createDataTypeProperty("hasScore", "Player", "Player scores runs",XSD.xstring);
		//	handler.addIndividuals("Batsman","Dilshan");
		//	handler.addIndividuals("Team","Sri Lanka");
			//	handler.addIndividuals("Team","India");
			//handler.addObjectTypeProperty("India","playedAgainest","Sri Lanka");
			handler.addDataTypeProperty("Sangakkara", "hasScore", "100");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		
	}
}