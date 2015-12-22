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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLEditorKit.Parser;

import org.iotemp.IOHandler;
import org.wordnet.WordNetController;

import com.hp.hpl.jena.sparql.pfunction.library.str;

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

	private static POSModel model = null;
	private static PerformanceMonitor perfMon = null;
	private static POSTaggerME tagger = null;
	static {
		model = new POSModelLoader().load(new File("en-pos-maxent.bin"));
		perfMon = new PerformanceMonitor(System.err, "sent");
		tagger = new POSTaggerME(model);
	}

	public static Map<String, String> getPosTag(String sentence) throws IOException {
		Map<String, String> joinedTags = new LinkedHashMap<String, String>();
		boolean nnpFlag = false;
		String nnpTempWord = "";
		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(sentence));

		String line;
		while ((line = lineStream.read()) != null) {

			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
			String[] tags = tagger.tag(whitespaceTokenizerLine);

			for (int i = 0; i < whitespaceTokenizerLine.length; i++) {
				String tag = tags[i];
				String token = whitespaceTokenizerLine[i];
				if (tag.contains("NNP") && !nnpFlag) {
					nnpFlag = true;
					nnpTempWord = token;
					joinedTags.put(token, tag);
				} else if (tag.contains("NNP") && nnpFlag) {
					joinedTags.remove(nnpTempWord);
					joinedTags.put(nnpTempWord + "_" + token, tag);
					nnpFlag = false;
					nnpTempWord = "";
				} else {
					nnpFlag = false;
					joinedTags.put(token, tag);
				}

				System.out.println("Word :" + whitespaceTokenizerLine[i] + " Tag :" + tags[i]);
			}

			// POSSample sample = new POSSample(whitespaceTokenizerLine, tags);

		}

		return joinedTags;
	}

	// only get the VB currently-> need to upgrade C1 VB Prep C1 etc
	public static String getTheSentenceVerb(String sentence, String individual1, String individual2) throws IOException, JWNLException {
		String verb = "";

		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(sentence));
		String line;
		while ((line = lineStream.read()) != null) {

			String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
			String[] tags = tagger.tag(whitespaceTokenizerLine);

			for (int i = 0; i < whitespaceTokenizerLine.length; i++) {
				System.out.println("Tag :" + tags[i] + " whiteSpace:" + whitespaceTokenizerLine[i]);
				if (tags[i].startsWith("VB")) {
					Dictionary dictionary = Dictionary.getDefaultResourceInstance();
					verb = whitespaceTokenizerLine[i];
					break;
				}
			}

		}

		return verb;
	}

	public static String[] patternIdentifier(String sentence) throws IOException, JWNLException {
		Map<String, String> taggedMap = POSTagging.getPosTag(sentence);

		String[] pattern = { "", "", "" };
		boolean isPassive = false;
		Set<String> keySet = taggedMap.keySet();
		boolean firstNounOk = false;
		for (String string : keySet) {
			String key = string;
			String value = taggedMap.get(string);

			if (value.startsWith("NNP") && !firstNounOk) {
				firstNounOk = true;
				pattern[0] = key;
			} else if (value.startsWith("NNP") && firstNounOk) {
				pattern[2] = key;
			} else if (value.startsWith("VB") && firstNounOk) {
				pattern[1] = key;
			} else if (value.startsWith("VB") && !firstNounOk) {
				pattern[1] = key;
				firstNounOk = true;
			}

		}
		System.out.println("Pattern ----------");
		for (String string : pattern) {
			System.out.println(string);
		}
		System.out.println();
		return pattern;
	}

	public static void getTheSentenceVerbTemp() throws IOException, JWNLException {
		// List<String> sentences=IOHandler.readCorpusFromFile(0);

		String indi1 = "Lanka";
		String indi2 = "India";
		List<String> sentences = new ArrayList<String>();
		// sentences.add("Mahela and Sangakkara consorted 369 matches");
		sentences.add("Sangakkara talked to Thisara");
		sentences.add("Thisara walked with Mahela");
		sentences.add("Sangakkara walked with Mahela");
		sentences.add("Lanka beaten India");
		sentences.add("Sangakkara is the captain of SriLanka");
		sentences.add("The children were laughing");

		for (String string : sentences) {
			System.out.println("Sentence :" + string);
			System.out.println(string.matches(indi1 + "_NNP\\s.*$_VBN\\s" + indi2 + "_NNP.*$"));
			ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(string));
			String line;
			while ((line = lineStream.read()) != null) {

				String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
				String[] tags = tagger.tag(whitespaceTokenizerLine);
				POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
				System.out.println(sample.toString() + "\n");

			}
		}

	}

	public static void main(String args[]) {
		try {
			patternIdentifier("Sri Lanka is beaten by India");
		} catch (IOException | JWNLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
