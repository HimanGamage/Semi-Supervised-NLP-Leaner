package org.wordnet;

import java.util.Set;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

public class WordNetTest {

	public static void main(String[] args) throws JWNLException {
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		Set<String> list1=WordNetController.getSimilarWords("walk", dictionary);
		Set<String> list2=WordNetController.getSimilarWords("communicate", dictionary);
		
		for (String string : list1) {
			for (String string1 : list2) {
				if(string.equals(string1)){
					System.out.println("Equals walksimilar:"+string+" communcatesimilar:"+string1);
				}
			}
		}
		
	}
}
