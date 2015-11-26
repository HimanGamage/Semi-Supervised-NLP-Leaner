package org.postagging;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.HTMLEditorKit.Parser;

import org.wordnet.WordNetController;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;

/*
 * Created by Himan Gamage
 * 
 * 
 * 
 */
public class POSTagging {
	
	public static Map<String, String> getPosTag(String sentence) throws IOException{
		Map<String, String> joinedTags=new HashMap<String, String>();
		POSModel model = new POSModelLoader().load(new File("en-pos-maxent.bin"));
        PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
        POSTaggerME tagger = new POSTaggerME(model);
        ObjectStream<String> lineStream = new PlainTextByLineStream(
                new StringReader(sentence));
        perfMon.start();
        String line;
        while ((line = lineStream.read()) != null) {

            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                    .tokenize(line);
            String[] tags = tagger.tag(whitespaceTokenizerLine);

            for(int i=0;i<whitespaceTokenizerLine.length;i++){
            	joinedTags.put(whitespaceTokenizerLine[i],tags[i]);
            	System.out.println("Word :"+whitespaceTokenizerLine[i]+" Tag :"+tags[i]);
            }
           // POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
            perfMon.incrementCounter();

        }
        perfMon.stopAndPrintFinalResult();
	
        return joinedTags;
	}

	
	//only get the VB currently-> need to upgrade C1 VB Prep C1 etc
	public static String getTheSentenceVerb(String sentence) throws IOException, JWNLException{
		String verb="";
		POSModel model = new POSModelLoader().load(new File("en-pos-maxent.bin"));
        PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
        POSTaggerME tagger = new POSTaggerME(model);
        ObjectStream<String> lineStream = new PlainTextByLineStream(
                new StringReader(sentence));
        perfMon.start();
        String line;
        while ((line = lineStream.read()) != null) {

            String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                    .tokenize(line);
            String[] tags = tagger.tag(whitespaceTokenizerLine);

            for(int i=0;i<whitespaceTokenizerLine.length;i++){
            	System.out.println("Tag :"+tags[i]+" whiteSpace:"+whitespaceTokenizerLine[i]);
            	if(tags[i].startsWith("VB")){
            		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
            		verb=whitespaceTokenizerLine[i];
            		break;
            	}
            	//System.out.println("Word :"+whitespaceTokenizerLine[i]+" Tag :"+tags[i]);
            }
            perfMon.incrementCounter();

        }
        perfMon.stopAndPrintFinalResult();
	
        return verb;
	}
}
