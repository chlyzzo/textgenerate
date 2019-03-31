package common;

/**
 * @author huwenhao
 *词类，词名，词性
 *
 */
public class Word {
	
  String wordStr=null;
  String wordTag = null;
	
  public Word(String term,String posTag ){
	wordStr = term;
	wordTag = posTag;
  }
	
  public String getWordName (){
	return this.wordStr;
  }
	
  public String getWordTag (){
	return this.wordTag;
  }
	
}
