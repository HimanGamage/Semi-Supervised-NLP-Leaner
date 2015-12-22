package org.ont;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

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
/*
 * Created by Himan Gamage
 * 
 * 
 * 
 */
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
		//	handler.createDataTypeProperty("hasScore", "Player", "Player scores runs",XSD.xstring);
			//handler.addIndividuals("Batsman","Thisara");
		//	handler.addIndividuals("Team","Sri Lanka");
			//JenaHandler.getInstance().addIndividuals("Team","Pakisthan");
			//handler.createObjectTypeProperty("communicated", "Batsman", "Batsman", "They play with together");
			handler.addObjectTypeProperty("Mahela","consorted","Dilshan");
			//handler.addDataTypeProperty("Dilshan", "hasSixes", "70");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
		
	}
}