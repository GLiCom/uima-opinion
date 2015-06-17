

/* First created by JCasGen Thu May 07 14:50:49 CEST 2015 */
package edu.upf.glicom.uima.ts.opinion;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Thu May 14 18:01:24 CEST 2015
 * XML source: /home/plambert/softgit/uima-opinion/src/main/resources/edu/upf/glicom/uima/ts/opinion-types.xml
 * @generated */
public class QuantNeg extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(QuantNeg.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected QuantNeg() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public QuantNeg(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public QuantNeg(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public QuantNeg(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: quant

  /** getter for quant - gets 
   * @generated
   * @return value of the feature 
   */
  public String getQuant() {
    if (QuantNeg_Type.featOkTst && ((QuantNeg_Type)jcasType).casFeat_quant == null)
      jcasType.jcas.throwFeatMissing("quant", "edu.upf.glicom.uima.ts.opinion.QuantNeg");
    return jcasType.ll_cas.ll_getStringValue(addr, ((QuantNeg_Type)jcasType).casFeatCode_quant);}
    
  /** setter for quant - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setQuant(String v) {
    if (QuantNeg_Type.featOkTst && ((QuantNeg_Type)jcasType).casFeat_quant == null)
      jcasType.jcas.throwFeatMissing("quant", "edu.upf.glicom.uima.ts.opinion.QuantNeg");
    jcasType.ll_cas.ll_setStringValue(addr, ((QuantNeg_Type)jcasType).casFeatCode_quant, v);}    
   
    
  //*--------------*
  //* Feature: neg

  /** getter for neg - gets 
   * @generated
   * @return value of the feature 
   */
  public String getNeg() {
    if (QuantNeg_Type.featOkTst && ((QuantNeg_Type)jcasType).casFeat_neg == null)
      jcasType.jcas.throwFeatMissing("neg", "edu.upf.glicom.uima.ts.opinion.QuantNeg");
    return jcasType.ll_cas.ll_getStringValue(addr, ((QuantNeg_Type)jcasType).casFeatCode_neg);}
    
  /** setter for neg - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNeg(String v) {
    if (QuantNeg_Type.featOkTst && ((QuantNeg_Type)jcasType).casFeat_neg == null)
      jcasType.jcas.throwFeatMissing("neg", "edu.upf.glicom.uima.ts.opinion.QuantNeg");
    jcasType.ll_cas.ll_setStringValue(addr, ((QuantNeg_Type)jcasType).casFeatCode_neg, v);}    
  }

    