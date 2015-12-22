package org.postagging;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class POSTagSample {
	 public static void main(String args[]) throws  InvalidFormatException,IOException {
	        POSModel model = new POSModelLoader().load(new File("en-pos-maxent.bin"));
	        PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
	        POSTaggerME tagger = new POSTaggerME(model);

	        String input = "Who communicated with Sanggakkara?";
	        ObjectStream<String> lineStream = new PlainTextByLineStream(
	                new StringReader(input));

	        perfMon.start();
	        String line;


	        while ((line = lineStream.read()) != null) {

	            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
	                    .tokenize(line);
	            String[] tags = tagger.tag(whitespaceTokenizerLine);

	            for(int i=0;i<whitespaceTokenizerLine.length;i++){
	            	System.out.println("Word :"+whitespaceTokenizerLine[i]+" Tag :"+tags[i]);
	            }
	            
	            
	            POSSample sample = new POSSample(whitespaceTokenizerLine, tags);

	           
	          //  System.out.println(sample.toString());

	            System.out.println("--------------------");
	            perfMon.incrementCounter();

	        }
	        perfMon.stopAndPrintFinalResult();
	       
	      
	        
	        


	    }
}
