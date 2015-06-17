package edu.upf.glicom.uima.opinion;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.selectPreceding;
import static org.apache.uima.fit.util.JCasUtil.selectFollowing;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.upf.glicom.uima.ts.opinion.OpinionExpression;

public class DistanceBasedOpinionTargetExtractor extends JCasAnnotator_ImplBase  {

	public static final String PARAM_WINDOW_SIZE = "windowSize";
	@ConfigurationParameter(name=PARAM_WINDOW_SIZE, defaultValue="2")
	private int windowSize;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		for (Sentence sentence : select(aJCas, Sentence.class)) {
			//System.out.println("Target extractor:"+sentence.getCoveredText());
			for (OpinionExpression oe : selectCovered(OpinionExpression.class, sentence)) {
				//System.out.println("OE:"+oe.getCoveredText());
				//if (Double.parseDouble(oe.getPolarity()) != 0){
					// look at opinion expression categories	
					int nVerbs = 0;
					int nAdjectives = 0;
					//int nAdverbs = 0;
					int nNouns = 0;
					int nOthers = 0;
					for (Token token : selectCovered(Token.class, oe)){
						if (token.getPos().getPosValue().startsWith("J") || token.getPos().getPosValue().compareTo("VBN") == 0){nAdjectives++;}
						else if (token.getPos().getPosValue().startsWith("V")){nVerbs++;}
						else if (token.getPos().getPosValue().startsWith("N")){nNouns++;}
						//else if (token.getPos().getPosValue().startsWith("R")){nAdverbs++;}
						else {nOthers++;}
						//System.out.print(token.getPos().getPosValue()+" ");
					}
					//System.out.print(" nVerbs"+nVerbs+" nAdjectives"+nAdjectives+" nNouns"+nNouns+" nOthers"+nOthers);
					//System.out.println();
					//String[] precedingPos= new String[windowSize];
					List<Token> precedingToks = new ArrayList<Token>();
					for (Token token : selectPreceding(Token.class, oe, windowSize)){
						precedingToks.add(0,token); // reverse list: from closest to farthest
					}
					for (Token token : precedingToks){
						//System.out.println("Preceding:"+token.getCoveredText()+" ");					
						//precedingPos[windowSize-i] = token.getPos().getPosValue();
						//System.out.println("Preceding:"+token.getCoveredText()+" "+token.getPos().getPosValue());
					}

					//String[] followingPos= new String[windowSize];
					List<Token> followingToks = selectFollowing(Token.class, oe, windowSize);
					/*
					for (Token token : selectFollowing(Token.class, oe, windowSize)){
						//followingPos[i] = token.getPos().getPosValue();
						//System.out.println("Following:"+token.getCoveredText()+" "+token.getPos().getPosValue());
						System.out.println("Following:"+token.getCoveredText()+" ");
						i++;
					}*/
					// if oe adjectival phrase and noun in [-n, +n] window+- (select preceding, select following)
					// (if there are several nouns in window, take the closest one; if at same distance, take following one)
					// (if polarity is -1 or +1)
					// if oe adverbial phrase and verb in [-n,+n] window, select it as target
					for (int i=0; i<windowSize; i++){
						if (nAdjectives>0){
							if (i < followingToks.size() && followingToks.get(i).getPos().getPosValue().startsWith("N")){
								Annotation target = new Annotation(aJCas,followingToks.get(i).getBegin(),followingToks.get(i).getEnd());
								oe.setTarget(target);
								break;
							}else if (i < precedingToks.size() && precedingToks.get(i).getPos().getPosValue().startsWith("N")){
								Annotation target = new Annotation(aJCas,precedingToks.get(i).getBegin(),precedingToks.get(i).getEnd());
								oe.setTarget(target);
								break;
							}
						/*}else if (nAdverbs>0){
							if (i < followingToks.size() && followingToks.get(i).getPos().getPosValue().startsWith("V")){
								Annotation target = new Annotation(aJCas,followingToks.get(i).getBegin(),followingToks.get(i).getEnd());
								oe.setTarget(target);
								break;
							}else if (i < precedingToks.size() && precedingToks.get(i).getPos().getPosValue().startsWith("V")){
								Annotation target = new Annotation(aJCas,precedingToks.get(i).getBegin(),precedingToks.get(i).getEnd());
								oe.setTarget(target);
								break;
							}*/							
						}
					}
				//}

			}			
		}
	}
}
