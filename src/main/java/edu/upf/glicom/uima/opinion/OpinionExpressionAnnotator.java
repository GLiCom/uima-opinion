package edu.upf.glicom.uima.opinion;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;
import static org.apache.uima.fit.util.JCasUtil.selectCovering;

import java.util.HashMap;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.conceptMapper.ConceptMapper;
import org.apache.uima.conceptMapper.support.dictionaryResource.CompiledDictionaryResource_impl;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import edu.upf.glicom.uima.ts.opinion.OpinionExpression;
import edu.upf.glicom.uima.ts.opinion.Polar;
import edu.upf.glicom.uima.ts.opinion.QuantNeg;

public class OpinionExpressionAnnotator extends JCasAnnotator_ImplBase {

	public static final String PARAM_POLAR_DICT_FILE = "polarDictFile";
	@ConfigurationParameter(name=PARAM_POLAR_DICT_FILE)
	private String polarDictFile;

	public static final String PARAM_POLAR_DICT_TYPE = "polarDictType";
	@ConfigurationParameter(name=PARAM_POLAR_DICT_TYPE, defaultValue="token")
	private String polarDictType;

	public static final String PARAM_QUANTNEG_DICT_FILE = "quantNegDictFile";
	@ConfigurationParameter(name=PARAM_QUANTNEG_DICT_FILE)
	private String quantNegDictFile;

	public static final String PARAM_DESTINATION_VIEW_NAME = "destinationViewName";
	@ConfigurationParameter(name=PARAM_DESTINATION_VIEW_NAME, defaultValue=CAS.NAME_DEFAULT_SOFA)
	private String destinationViewName;
		
	private AnalysisEngine polMap;
	private AnalysisEngine quantNegMap;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		// TODO Auto-generated method stub
		super.initialize(context);

		String tokenName = Token.class.getCanonicalName();
		String featureName = "";
		if (polarDictType.compareTo("lemma") == 0){
			tokenName = Lemma.class.getCanonicalName();
			featureName = "value";
		}
/*		ExternalResourceDescription dictOF, dictSenticNet, dictLIWC, dictQuantNeg;
		dictSenticNet = createExternalResourceDescription(
				CompiledDictionaryResource_impl.class,
				"file:/vmdata/glicom_shared/resources/dictionaries/EN/compiled/dictSenticNet.dic"); //match with the lemma		
		dictLIWC = createExternalResourceDescription(
				CompiledDictionaryResource_impl.class,
				"file:/vmdata/glicom_shared/resources/dictionaries/EN/compiled/dictLIWC2007.dic"); //match with the form
*/
		ExternalResourceDescription dictPolar;
		dictPolar = createExternalResourceDescription(
				CompiledDictionaryResource_impl.class,
				"file:"+polarDictFile); //match with the lemma
				  
		AnalysisEngineDescription polMapDesc = createEngineDescription(
				ConceptMapper.class,
				ConceptMapper.PARAM_DICT_FILE, dictPolar,
				ConceptMapper.PARAM_ANNOTATION_NAME, Polar.class.getCanonicalName(), 
				ConceptMapper.PARAM_ATTRIBUTE_LIST, new String[]{"polarity"},
				ConceptMapper.PARAM_FEATURE_LIST, new String[]{"polarity"},
				ConceptMapper.PARAM_DATA_BLOCK_FS, "uima.tcas.DocumentAnnotation", // can't use DocumentAnnotation.class.getCanonicalName() because it gets the jcas version instead of tcas
				ConceptMapper.PARAM_FINDALLMATCHES, false,
				ConceptMapper.PARAM_ORDERINDEPENDENTLOOKUP, false,
				"caseMatch", "ignoreall",
				ConceptMapper.PARAM_SEARCHSTRATEGY, ConceptMapper.PARAMVALUE_CONTIGUOUSMATCH,
				ConceptMapper.PARAM_TOKENANNOTATION, tokenName,
				ConceptMapper.PARAM_TOKENTEXTFEATURENAME, featureName
				);

		polMap = AnalysisEngineFactory.createEngine(polMapDesc,destinationViewName);

		// Quantifier/Negation Concept Mapper
		//AnalysisEngineDescription quantNegMap = createEngineDescription(
		
		ExternalResourceDescription dictQuantNeg;
		dictQuantNeg = createExternalResourceDescription(
				CompiledDictionaryResource_impl.class,
				"file:"+quantNegDictFile); //match with the lemma
		
		AnalysisEngineDescription quantNegMapDesc = createEngineDescription(
				ConceptMapper.class,
				ConceptMapper.PARAM_DICT_FILE, dictQuantNeg,
				ConceptMapper.PARAM_ANNOTATION_NAME, QuantNeg.class.getCanonicalName(), 
				ConceptMapper.PARAM_ATTRIBUTE_LIST, new String[]{"neg","quant"},
				ConceptMapper.PARAM_FEATURE_LIST, new String[]{"neg","quant"},
				ConceptMapper.PARAM_DATA_BLOCK_FS, "uima.tcas.DocumentAnnotation", // can't use DocumentAnnotation.class.getCanonicalName() because it gets the jcas version instead of tcas
				ConceptMapper.PARAM_FINDALLMATCHES, false,
				ConceptMapper.PARAM_ORDERINDEPENDENTLOOKUP, false,
				"caseMatch", "ignoreall",
				ConceptMapper.PARAM_SEARCHSTRATEGY, ConceptMapper.PARAMVALUE_CONTIGUOUSMATCH,
				ConceptMapper.PARAM_TOKENANNOTATION, Token.class.getCanonicalName()   //matching with the form
				);
		
		quantNegMap = AnalysisEngineFactory.createEngine(quantNegMapDesc,destinationViewName);
				
		/*
		List<AnalysisEngineDescription> analysisEngineDescriptions = new ArrayList<AnalysisEngineDescription>();
	    analysisEngineDescriptions.add(polmapOFDesc);
//		System.out.println("comp name:"+componentName);
		List<String> componentNames = new ArrayList<String>();
		String componentName = polmapOFDesc.getAnalysisEngineMetaData().getName();
	    componentNames.add(componentName);
	    TypePriorities typePriorities = null;
	    polmapOF = AnalysisEngineFactory.createEngine(
				analysisEngineDescriptions,
				componentNames,
				typePriorities,
				context.getSofaMappings().
				);
*/		
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		polMap.process(aJCas);
		quantNegMap.process(aJCas);

		// DEFINE OPINION EXPRESSION BOUNDARIES
		// cluster expressions with 1 or more QuantNeg tokens followed by 1 or more Polar tokens
		for (Sentence sentence : select(aJCas, Sentence.class)) {
			int oeBeg = sentence.getBegin();
			int oeEnd = sentence.getBegin();
			boolean hasPolar = false;
			boolean hasQuantNeg = false;
			for (Token token : selectCovered(Token.class, sentence)){
				List<QuantNeg> quantneglist = selectCovering(QuantNeg.class, token);
				List<Polar> polarlist = selectCovering(Polar.class, token);
				if ( !polarlist.isEmpty() ){
					if ( !hasPolar && !hasQuantNeg ){ // start a new opinion expression
	   					oeBeg = token.getBegin();
					}
  					oeEnd = token.getEnd();
					if (token.getEnd() == sentence.getEnd()){
	   					Annotation opExp = new OpinionExpression(aJCas,oeBeg,oeEnd);
	   					opExp.addToIndexes();						
					}
  					hasPolar = true;
				}else if ( !quantneglist.isEmpty() ){
					if (hasPolar){ // create a new opinion expression and start a new one
	   					Annotation opExp = new OpinionExpression(aJCas,oeBeg,oeEnd);
	   					opExp.addToIndexes();
	   					oeBeg = token.getBegin();
					}else if (hasQuantNeg){ // continue the opinion expression
					}else{ // start a new opinion expression
	   					oeBeg = token.getBegin();
					}
   					oeEnd = token.getEnd();
   					hasPolar = false;
   					hasQuantNeg = true;
				}else{
					if (hasPolar){ // create a new opinion expression
	   					Annotation opExp = new OpinionExpression(aJCas,oeBeg,oeEnd);
	   					opExp.addToIndexes();
					}
					hasPolar = false;
					hasQuantNeg = false;
				}
			}
			
			// CALCULATE OPINION EXPRESSION POLARITY
			//System.out.println("Annotator sentence: "+sentence.getCoveredText());
			/*
			for (Token token : selectCovered(Token.class, sentence)) {
				//System.out.printf("  %-16s\t%-10d\t%-10d\t%-10s%n", 
				System.out.println("Annotator token:"+token.getCoveredText()+" PoS:"+token.getPos().getPosValue()+" lemma:"+token.getLemma().getValue());
			}
			*/
			
			for (OpinionExpression oe : selectCovered(OpinionExpression.class, sentence)) {
				String polStr = ""+calculateOpinionExpressionPolarity(oe);
				oe.setPolarity(polStr);
				//System.out.println("Annotator OE: "+ oe.getCoveredText() + " " + oe.getPolarity());
			}
		}
	}
	
	private double calculateOpinionExpressionPolarity(OpinionExpression opExp){
		double tot_pol = 0;			
		int pos_words = 0;
		int neg_words = 0;
		int pos_quant_words = 0;
		int neg_quant_words = 0;
		int negations = 0;

		HashMap<String, Integer> seenPolars = new HashMap<String, Integer>();
		for (Polar polar : selectCovered(Polar.class, opExp)) {
			if (!seenPolars.containsKey(polar.getCoveredText())){
				seenPolars.put(polar.getCoveredText(), 1);
				//System.out.println("FUNCTION polar: "+ polar.getCoveredText() + " " + polar.getPolarity());
				double polarity = Double.parseDouble(polar.getPolarity());
				if (polarity >= 0.5) {pos_words = pos_words + 1;}
				else if (polarity <= -0.5) {neg_words = neg_words + 1;}						
				tot_pol += polarity;
				// look if polar expression is also a quantifier
				for (QuantNeg quantneg : selectCovered(QuantNeg.class, polar)){
				//List<QuantNeg> quantNegList = selectCovered(QuantNeg.class, polar);
				//if ( !quantNegList.isEmpty() ){
					//QuantNeg quantneg = quantNegList.get(0);
					if (quantneg.getBegin() == polar.getBegin() && quantneg.getEnd() == polar.getEnd() && !quantneg.getQuant().equals("no")){
						//System.out.println("FUNCTION Quant Polar: "+ quantneg.getCoveredText() + " " + quantneg.getQuant());
						if (polarity >= 0.5) {pos_quant_words++;}
						else if (polarity <= -0.5) {neg_quant_words++;}	
						tot_pol -= 0.5*polarity;
						break; // in case of repeated entries in dict, we don't want sums to be incremented several times
					}
				}
			}
		}

		// counting negations which are not polar words
		for (QuantNeg neg : selectCovered(QuantNeg.class, opExp)) {
			if (neg.getNeg().equals("yes")){
				boolean isPolar = false;	
				for (Polar polar : selectCovered(Polar.class, neg)){
					//trace_out.println("Neg Polar: "+ neg.getCoveredText() + " " + neg.getQuant() + " " + neg.getNeg());
					if (polar.getBegin() == neg.getBegin() && polar.getEnd() == neg.getEnd()){
						isPolar = true;
					}
				}
				if (! isPolar){
					negations++;
					//System.out.println("FUNCTION Neg: "+ neg.getCoveredText() + " " + neg.getNeg());
				}
			}
		}
		//System.out.println("FUNCTION tot_pol: "+ tot_pol);
		//tot_pol = pos_words -0.5*pos_quant_words -neg_words + 0.5*neg_quant_words; 
		double final_pol = 0;
		if (tot_pol < 0.5 && tot_pol > -0.5) {final_pol = 0;}
		else if (tot_pol >= 0.5) {
			if (negations > 0){final_pol = -1;}
			else {final_pol = 1;}
		}else if (tot_pol <= -0.5) {
			if (negations > 0){final_pol = 1;}
			else {final_pol = -1;}
		}
		return final_pol;
	}
}
