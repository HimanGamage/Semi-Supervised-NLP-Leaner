package org.expanding;

import java.awt.PrintGraphics;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;
import opennlp.tools.postag.POSTagger;

import org.db.RelationshipController;
import org.iotemp.IOHandler;
import org.kmeans3d.Cluster;
import org.kmeans3d.KMeans;
import org.kmeans3d.Point;
import org.ont.JenaHandler;
import org.postagging.POSTagging;
import org.wordnet.WordNetController;

import com.github.jsonldjava.utils.Obj;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.impl.AllValuesFromRestrictionImpl;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/*
 * Created by Himan Gamage

 * 
 */
public class ExpandingController extends Thread {

	private static final int SEEDS_LIMIT = 20;
	private final int MIN_NO_OF_SEEDS_FOR_RELATIONSHIP = 3;
	private final int NO_OF_CLUSTERS = 3;

	// Set<Object[]>
	// relationships-->category1,individual1,sentenceverb,individual2,category2,sentence
	public Map<String, Map<String, List<String[]>>> groupingSentences(Set<String[]> relationships) {
		// HashMap(category1+category2,Map<String,List<String[]>>)String->Relationship
		// and String[]->(individual1,individual2,sentence)
		Map<String, Map<String, List<String[]>>> groupedSentences = new HashMap<String, Map<String, List<String[]>>>();

		for (String[] relationship : relationships) {
			String key = ((String) relationship[0]).concat("#" + (String) relationship[4]);

			if (!groupedSentences.containsKey((key))) {
				List<String[]> data = new ArrayList<String[]>();
				data.add(new String[] { relationship[1], relationship[3], relationship[5] });
				Map<String, List<String[]>> relationMap = new HashMap<String, List<String[]>>();
				relationMap.put(relationship[2], data);
				groupedSentences.put(key, relationMap);
			} else {
				Map<String, List<String[]>> map = groupedSentences.get(key);
				if (!map.containsKey(relationship[2])) {
					List<String[]> data = new ArrayList<String[]>();
					data.add(new String[] { relationship[1], relationship[3], relationship[5] });
					map.put(relationship[2], data);
				} else {
					map.get(relationship[2]).add(new String[] { relationship[1], relationship[3], relationship[5] });
				}
			}
		}

		return groupedSentences;
	}

	public Set<String[]> getAllNewRelationships(List<String> sentences) throws IOException, JWNLException {

		Set<String[]> tempNewRelationships = new HashSet<String[]>();
		ArrayList<OntClass> ontClassList = new ArrayList<OntClass>();
		Set<String> coupledIndividuals = new HashSet<String>();

		// adding the categories to a list
		for (Iterator<OntClass> i = JenaHandler.getInstance().getOntModel().listClasses(); i.hasNext();) {
			ontClassList.add(i.next());
		}

		// coupling them
		for (int i = 0; i < ontClassList.size(); i++) {
			for (int j = i; j < ontClassList.size(); j++) {

				OntClass ontClass1 = ontClassList.get(i);
				OntClass ontClass2 = ontClassList.get(j);

				System.out.println("ontclass 1 :" + ontClass1.getURI() + "\t ontclass 2 :" + ontClass2.getURI() + "\t");

				// looping the instances of ontclass 1
				for (Iterator<Individual> k = JenaHandler.getInstance().getOntModel().listIndividuals(ontClass1); k.hasNext();) {
					Individual individual1 = k.next();

					// looping the individuals of ontclass2
					for (Iterator<Individual> l = JenaHandler.getInstance().getOntModel().listIndividuals(ontClass2); l.hasNext();) {
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

										// only get the VB currently-> need to
										// upgrade C1 VB Prep C1 etc
										 String sentenceVerb =
										 POSTagging.getTheSentenceVerb(sentence,
										 individual1.getLocalName(),
										 individual2.getLocalName());
										//String sentenceVerb = pattern[1];
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
											// category1,individual1,sentenceverb,individual2,category2,sentence
											String[] obj = { ontClass1.getLocalName(), individual1.getLocalName(), sentenceVerb, individual2.getLocalName(), ontClass2.getLocalName(), sentence };
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

	public void upgradeOntology(List<Cluster> pointsClusters) throws FileNotFoundException {
		JenaHandler hand = JenaHandler.getInstance();
		if (pointsClusters != null) {
			for (int i = 0; i < pointsClusters.size(); i++) {
				System.out.println("Centernoid :" + pointsClusters.get(i).getCentroid());
				System.out.println("Closest point :" + pointsClusters.get(i).getClosestPoint() + " Proposed Relationship:" + pointsClusters.get(i).getRelationMapping().get((int) pointsClusters.get(i).getClosestPoint().x));
				System.out.println("Cluster " + i + ": " + pointsClusters.get(i) + "\ncategory pair:" + pointsClusters.get(i).getClosestPoint().getCategoryPair() + "\nSeeds:");
				String catPair[] = pointsClusters.get(i).getClosestPoint().getCategoryPair().split("#");
				hand.createObjectTypeProperty(pointsClusters.get(i).getRelationMapping().get((int) pointsClusters.get(i).getClosestPoint().x), catPair[0], catPair[1], pointsClusters.get(i).getRelationMapping().get((int) pointsClusters.get(i).getClosestPoint().x));
				String seeds = "";
				int limitSeeds = 0;
				for (String ar[] : pointsClusters.get(i).getClosestPoint().getSeeds()) {
					hand.addObjectTypeProperty(ar[0], pointsClusters.get(i).getRelationMapping().get((int) pointsClusters.get(i).getClosestPoint().x), ar[1]);

					if (limitSeeds < SEEDS_LIMIT) {
						seeds += ar[2] + "---";
						limitSeeds++;
					}

					for (String string : ar) {
						System.out.print(string + "---");
					}
					System.out.println();

				}
				try {
					String newSeeds = seeds.replaceAll("\'", "");
					RelationshipController.addRelationshipData(catPair[0], catPair[1], pointsClusters.get(i).getRelationMapping().get((int) pointsClusters.get(i).getClosestPoint().x), newSeeds);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	private List<Cluster> generateRelationships(List<Object[]> objList) {
		List<Cluster> pointsClusters = null;
		for (Object[] objects : objList) {

			LinkedHashMap<Integer, String> relationMapping = (LinkedHashMap<Integer, String>) objects[0];
			List<Point> pointList = (List<Point>) objects[1];

			if (pointList.size() >= NO_OF_CLUSTERS) {

				KMeans kMeans = new KMeans(pointList, NO_OF_CLUSTERS, relationMapping);
				pointsClusters = kMeans.getPointsClusters();

			}
		}
		return pointsClusters;
	}

	private List<Object[]> createCoOccuranceMatrix(Map<String, Map<String, List<String[]>>> grouped) {
		Map<String, Map<String, List<String[]>>> groupedSentences = grouped;
		// removing the relations which have
		// seeds<MIN_NO_OF_SEEDS_FOR_RELATIONSHIP
		Set<String> keyset1New = groupedSentences.keySet();
		for (String stringR : keyset1New) {
			Iterator it = groupedSentences.get(stringR).entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, List<String[]>> e = (Entry<String, List<String[]>>) it.next();
				if (e.getValue().size() < MIN_NO_OF_SEEDS_FOR_RELATIONSHIP) {
					it.remove();
				}
			}
		}
		List<Object[]> matrices = new ArrayList<Object[]>();

		Set<String> keyset1 = groupedSentences.keySet();
		for (String string : keyset1) {
			LinkedHashMap<Integer, String> relationMapping = new LinkedHashMap<Integer, String>();
			List<Point> pointList = new ArrayList<Point>();

			boolean b = true;

			Set<String> keyset2 = groupedSentences.get(string).keySet();
			System.out.println("Category pair is : " + string);
			int j = 1;
			// groupedSentences.get(string)
			for (String stringRow : keyset2) {
				double totalSentencesInTheRow = 0;

				for (String stringColumn : keyset2) {

					totalSentencesInTheRow += groupedSentences.get(string).get(stringColumn).size();
					totalSentencesInTheRow += groupedSentences.get(string).get(stringRow).size();
					if (b) {
						System.out.print("\t\t\t" + stringColumn);
					}
				}
				b = false;
				System.out.println();

				int i = 1;
				System.out.print(stringRow);
				for (String stringColumn : keyset2) {
					relationMapping.put(i, stringColumn);
					double d = ((groupedSentences.get(string).get(stringColumn).size() + groupedSentences.get(string).get(stringRow).size()) / totalSentencesInTheRow);
					DecimalFormat df2 = new DecimalFormat(".####");
					Point p = new Point(i, j, Double.valueOf(df2.format(d)));
					p.setSeeds(groupedSentences.get(string).get(stringColumn));
					p.setCategoryPair(string);
					pointList.add(p);

					System.out.print("\t\t\t" + Double.valueOf(df2.format(d)));
					i++;
				}
				System.out.println();
				j++;
			}
			matrices.add(new Object[] { relationMapping, pointList });
		}

		return matrices;
	}

	private void printGroupedSentences(Map<String, Map<String, List<String[]>>> l) {
		System.out.println("---------");

		Set<String> s = l.keySet();
		for (String string : s) {
			Set<String> r = l.get(string).keySet();
			System.out.println("Key:" + string);
			for (String string2 : r) {
				List<String[]> t = l.get(string).get(string2);
				System.out.println("\tKey:" + string2 + " size:" + t.size());
				for (String[] strings : t) {
					System.out.println("\t\tind 1:" + strings[0] + " ind2:" + strings[1] + " seed:" + strings[2]);
				}
			}

		}
	}

	@Override
	public void run() {

		try {
			// temp sentences string arry init
//			 List<String> sentences = new ArrayList<String>();
//			 sentences.add("Mahela and Sangakkara consorted 369 matches");
//			 sentences.add("Sangakkara talked with Thisara");
//			 sentences.add("Thisara walked with Mahela");
//			 sentences.add("Sangakkara walked with Mahela");
//			 sentences.add("Sri Lanka is beaten India");

			List<String> sentences = IOHandler.readCorpusFromFile(0);
			// -------------
			ExpandingController exp = new ExpandingController();
			Set<String[]> obj = exp.getAllNewRelationships(sentences);
			Map<String, Map<String, List<String[]>>> l = exp.groupingSentences(obj);
			exp.printGroupedSentences(l);
		
			List<Object[]> o = exp.createCoOccuranceMatrix(l);
			List<Cluster> pointsClusters = exp.generateRelationships(o);
			exp.upgradeOntology(pointsClusters);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		ExpandingController ex = new ExpandingController();
		ex.start();
	}

}
