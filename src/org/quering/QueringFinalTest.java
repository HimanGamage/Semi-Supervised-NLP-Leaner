package org.quering;

import java.io.IOException;

import net.sf.extjwnl.JWNLException;

public class QueringFinalTest {
	public static void main(String[] args) {

		try {
			SentenceProcessor.patternIdentifier(" Who is Dilshan ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
