package org.wordnet;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTree;
import net.sf.extjwnl.dictionary.Dictionary;
/*
 * Created by Himan Gamage
 * 
 * 
 * 
 */
public class WordNetController {
	public static Set<String> getSimilarWords(String word1,Dictionary dictionary) throws JWNLException{
		IndexWord word=dictionary.getIndexWord(POS.VERB, word1);
		Set<String> wordSet = new LinkedHashSet<String>();
		wordSet.add(word1);
		if(word==null){
			return wordSet;
		}else{
			PointerTargetTree hyponyms = PointerUtils.getHyponymTree(word
					.getSenses().get(0));
			
			List<PointerTargetNodeList> hyponymsList = hyponyms.toList();
			for (int i = 0; i < hyponymsList.size(); i++) {
				PointerTargetNodeList pointerTargetNodeList = hyponymsList.get(i);
				for (int j = 0; j < pointerTargetNodeList.size(); j++) {
					Synset s = pointerTargetNodeList.get(j).getSynset();
					List<Word> words = s.getWords();
					for (int k = 0; k < words.size(); k++) {
						wordSet.add(words.get(k).getLemma());
					}
				}
			}
			
			PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(word
					.getSenses().get(0));
			for (int i = 0; i < hypernyms.size(); i++) {
				Synset s = hypernyms.get(i).getSynset();
				List<Word> words = s.getWords();
				for (int j = 0; j < words.size(); j++) {
					wordSet.add(words.get(j).getLemma());
				}
			}
			return wordSet;
		}
	

	}
	public static Set<String> demonstrateTreeOperation(String word1,
			Dictionary dictionary) throws JWNLException {
		IndexWord word=dictionary.getIndexWord(POS.VERB, word1);
		PointerTargetTree hyponyms = PointerUtils.getHyponymTree(word
				.getSenses().get(0));
		Set<String> wordSet = new LinkedHashSet<String>();
		List<PointerTargetNodeList> hyponymsList = hyponyms.toList();
		for (int i = 0; i < hyponymsList.size(); i++) {
			PointerTargetNodeList pointerTargetNodeList = hyponymsList.get(i);
			for (int j = 0; j < pointerTargetNodeList.size(); j++) {
				Synset s = pointerTargetNodeList.get(j).getSynset();
				List<Word> words = s.getWords();
				for (int k = 0; k < words.size(); k++) {
					wordSet.add(words.get(k).getLemma());
				}
			}
		}
		return wordSet;
	}

	public static Set<String> demonstrateListOperation(String word1,
			Dictionary dictionary) throws JWNLException {
		// Get all of the hypernyms (parents) of the first sense of
		// <var>word</var>
		IndexWord word=dictionary.getIndexWord(POS.VERB, word1);
		PointerTargetNodeList hypernyms = PointerUtils.getDirectHypernyms(word
				.getSenses().get(0));
		Set<String> wordSet = new LinkedHashSet<String>();
		for (int i = 0; i < hypernyms.size(); i++) {
			Synset s = hypernyms.get(i).getSynset();
			List<Word> words = s.getWords();
			for (int j = 0; j < words.size(); j++) {
				wordSet.add(words.get(j).getLemma());
			}
		}
		return wordSet;
	}

	
	public static String getBaseForm(String word,Dictionary dictionary) throws JWNLException{
		IndexWord indexWord=dictionary.lookupIndexWord(POS.VERB, word);

		if(indexWord==null){
			return word;
		}else{
			String wordBase=indexWord.getLemma();
			return wordBase;
		}
		
	}
}
