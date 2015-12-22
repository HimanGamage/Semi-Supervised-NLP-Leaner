package org.quering;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

import org.postagging.POSTagging;
import org.wordnet.WordNetController;

import com.hp.hpl.jena.query.QuerySolution;

public class SentenceProcessor {

	private static void nnpMerge(){
		
	}
	public static void patternIdentifier(String sentence) throws IOException, JWNLException{
		Map<String, String> taggedMap=POSTagging.getPosTag(sentence);
		
		String[] pattern={"","",""};
		
		Set<String> keySet=taggedMap.keySet();
		boolean firstNounOk=false;
		for (String string : keySet) {
			
			String key=string;
			String value=taggedMap.get(string);
			
			if(value.startsWith("NNP") && !firstNounOk){
				firstNounOk=true;
				pattern[0]=key;
			}else if(value.startsWith("NNP") && firstNounOk){
				pattern[2]=key;
			}else if(value.startsWith("VB") && firstNounOk ){
				pattern[1]=key;
			}else if(value.startsWith("VB") && !firstNounOk ){
				pattern[1]=key;
				firstNounOk=true;
			}
			
		}
		System.out.println("Pattern ----------");
		for (String string : pattern) {
			System.out.print(string+"\t");
		}
		System.out.println();
		
		routeQuery(pattern);
	}
	
	public static void routeQuery(String[] pattern) throws JWNLException{
	System.out.println(pattern[0].equals("") +" "+ pattern[1].equals("") +" "+ pattern[2].equals(""));
		if(!pattern[0].equals("") && pattern[1].equals("") && pattern[2].equals("") ){
			QueryHandler.queryingOntologySubject(pattern[0]);
			System.out.println("Pattern 1 running");
		}else if(pattern[0].equals("") && !pattern[1].equals("") && pattern[2].equals("") ){
			QueryHandler.queryingOntologyPredicate(pattern[0]);
			System.out.println("Pattern 2 running");
		}else if(pattern[0].equals("") && pattern[1].equals("") && !pattern[2].equals("") ){
			QueryHandler.queryingOntologyObject(pattern[0]);
			System.out.println("Pattern 3 running");
		}else if(!pattern[0].equals("") && !pattern[1].equals("") && pattern[2].equals("") ){
			QueryHandler.queryingOntologySubjectPredicate(pattern[0],pattern[1]);
			System.out.println("Pattern 4 running");
		}else if(pattern[0].equals("") && !pattern[1].equals("") && !pattern[2].equals("") ){
			Dictionary dictionary = Dictionary.getDefaultResourceInstance();
			
			if(WordNetController.getBaseForm(pattern[1], dictionary).equalsIgnoreCase("be")){
				List<QuerySolution> qs=QueryHandler.queryingOntologySubjectWithBe(pattern[2],pattern[1]);
				System.out.println("Pattern 5 running A");
				for (QuerySolution querySolution : qs) {
					System.out.println("--"+querySolution);
				}
			}else{
				QueryHandler.queryingOntologyPredicateObject(pattern[1],pattern[2]);
				System.out.println("Pattern 5 running B");
			}
			
		}else{
			
			System.out.println("Pattern did not match");
		}
	}
}
