package org.iotemp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class IOHandler {
    public static ArrayList<String> readCorpusFromFile (int currentIteration){
    	InputStreamReader read = null;
		try {
			read = new InputStreamReader(
					new FileInputStream("sentences.txt"), "UTF-8");
		} catch (UnsupportedEncodingException | FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        BufferedReader br = new BufferedReader(read);
        ArrayList<String> finedTextsArrayList = new ArrayList<>();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                finedTextsArrayList.add(line);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return finedTextsArrayList;
    }
    public static void main(String args[]){
    	readCorpusFromFile(0);
    }
}
