package org.expanding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;
import opennlp.tools.postag.POSTagger;

import org.ont.JenaHandler;
import org.postagging.POSTagging;
import org.wordnet.WordNetController;

import com.github.jsonldjava.utils.Obj;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/*
 * Created by Himan Gamage
 * 
 * 
 * 
 */
public class ExpandingController {

	JenaHandler handler = JenaHandler.getInstance();
	
	public void expandingRelationshipPromotion(Set<Object[]> relationships){
		
		//ont_class_1 predicate ont_class_2
		Object[] obj=new Object[3];
	}
	

	public Set<Object[]> getAllNewRelationships(List<String> sentences) throws IOException, JWNLException {
		
		Set<Object[]> tempNewRelationships = new HashSet<Object[]>();
		ArrayList<OntClass> ontClassList = new ArrayList<OntClass>();
		Set<String> coupledIndividuals = new HashSet<String>();

		// adding the categories to a list
		for (Iterator<OntClass> i = handler.getOntModel().listClasses(); i.hasNext();) {
			ontClassList.add(i.next());
		}

		// coupling them
		for (int i = 0; i < ontClassList.size(); i++) {
			for (int j = i; j < ontClassList.size(); j++) {

				OntClass ontClass1 = ontClassList.get(i);
				OntClass ontClass2 = ontClassList.get(j);

				System.out.println("ontclass 1 :" + ontClass1.getURI() + "\t ontclass 2 :" + ontClass2.getURI() + "\t");

				// looping the instances of ontclass 1
				for (Iterator<Individual> k = handler.getOntModel().listIndividuals(ontClass1); k.hasNext();) {
					Individual individual1 = k.next();

					// looping the individuals of ontclass2
					for (Iterator<Individual> l = handler.getOntModel().listIndividuals(ontClass2); l.hasNext();) {
						Individual individual2 = l.next();

						// check both catogories instances are equal
						if (!individual1.getURI().equals(individual2.getURI())) {
							String checkingValue = "[" + individual1.getURI() + "*****" + individual2.getURI() + "]";

							// removing the duplicate checking couples
							if (!coupledIndividuals.contains(checkingValue)) {
								coupledIndividuals.add(checkingValue);
								System.out.println("\t\tChecking value :" + checkingValue);

								
								// ---------

								for (int m = 0; m < sentences.size(); m++) {
									String sentence = sentences.get(m);

									// check the sentence has both instances
									if (sentence.contains(individual1.getLocalName()) && sentence.contains(individual2.getLocalName())) {
										System.out.println(sentence + " and it contains ind 1:" + individual1.getLocalName() + " and ind 2:" + individual2.getLocalName());
										Set<String> relationshipSet = getExistingRelationship(individual1);
										System.out.println("ind 1 relationship size:" + relationshipSet.size());

										// check the given sentence relationship
										// is already in the ontology

										boolean notInRelationshipList = true;
										boolean notInSentenceList = true;
										Dictionary dictionary = Dictionary.getDefaultResourceInstance();

										//only get the VB currently-> need to upgrade C1 VB Prep C1 etc
										String sentenceVerb = POSTagging.getTheSentenceVerb(sentence);
										relatinshipLoop: for (String relationship : relationshipSet) {

											if (relationship.equalsIgnoreCase(sentenceVerb)) {
												System.out.println("Already in the ont first if-------------");
												notInRelationshipList = false;
												break relatinshipLoop;
											} else {
												// get the base form of the
												// predicate
												String relationshipVerbBase = WordNetController.getBaseForm(relationship, dictionary);
												// get the base form of the
												// sentence verb
												String sentenceVerbBase = WordNetController.getBaseForm(sentenceVerb, dictionary);
												System.out.println("relationship :" + relationshipVerbBase + " sentence:" + sentenceVerb);

												Set<String> relationshipVerbWordList = WordNetController.getSimilarWords(relationshipVerbBase, dictionary);

												Set<String> sentenceVerbWordList = WordNetController.getSimilarWords(sentenceVerbBase, dictionary);
												System.out.println("List 1 size :" + sentenceVerbWordList.size() + " List 2 size :" + relationshipVerbWordList.size());

												for (String relationshipWordFromList : relationshipVerbWordList) {
													if (relationshipWordFromList.equalsIgnoreCase(sentenceVerbBase)) {
														notInRelationshipList = false;
														System.out.println("breaking......");
														break relatinshipLoop;
													}
												}
												for (String sentenceWordFromList : sentenceVerbWordList) {
													if (sentenceWordFromList.equalsIgnoreCase(relationshipVerbBase)) {
														notInSentenceList = false;
														System.out.println("breaking......");
														break relatinshipLoop;
													}
												}

											}

										}
										System.out.println("<<<<<<<<<<<<<<Not in :" + (notInRelationshipList & notInSentenceList));
										if (notInRelationshipList && notInSentenceList) {
											System.out.println("Added word :" + sentenceVerb);
											Object[] obj = { ontClass1, individual1, sentenceVerb, individual2, ontClass2, sentence };
											tempNewRelationships.add(obj);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return tempNewRelationships;
	}

	private boolean hasRelationship(Individual individual1, Individual individual2) {
		boolean flag = false;
		for (StmtIterator m = individual1.listProperties(); m.hasNext();) {
			Statement s = m.next();
			if (!s.getPredicate().getLocalName().equals("type")) {
				if (s.getObject().toString().equals(individual2.getURI())) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	private Set<String> getExistingRelationship(Individual individual1) {
		Set<String> relationships = new HashSet<String>();
		for (StmtIterator m = individual1.listProperties(); m.hasNext();) {
			Statement s = m.next();
			if (!s.getPredicate().getLocalName().equals("type")) {
				relationships.add(s.getPredicate().getLocalName());

			}
		}
		return relationships;
	}

	public static void main(String args[]) {
		try {
			// temp sentences string arry init
			 List<String> sentences = new ArrayList<String>();
			sentences.add("Mahela and Sangakkara consorted 369 matches");
			sentences.add("Sangakkara talked Thisara");
			sentences.add("Thisara walked with Mahela");
			sentences.add("Sangakkara walked with Mahela");

			// -------------
			ExpandingController exp=new ExpandingController();
			Set<Object[]> obj=exp.getAllNewRelationships(sentences);
			exp.expandingRelationshipPromotion(obj);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
