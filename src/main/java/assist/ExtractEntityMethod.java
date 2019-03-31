package assist;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.dataBaseProcess;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;

/**
 * 
 * 抽取出实体,留下作为模板,
 * 2019-03-30
 * 
 * nr人名
 * ns地名
 * nt机构团体名
 * nh医药
 * nn工作类
 * ni机构相关
 * nm物品
 * nz专有名词
 * 
 * 
 */
public class ExtractEntityMethod {

  private static List<String> SignificancePOSPrefix = Arrays.asList("nr,ns,nt,nh,nn,ng,ni,nm,nb,nz,g,j,i,l,t,s,f,b,z,d,c".split(","));
  private static List<String> SignificancePOSAll = Arrays.asList("ad, an, ag, al,vd,vn,vshi,vyou,vf,vx,vi,vl,vg".split(","));
	
  public static void main(String[] args) {
		
//		List<String> termList = extractSignificanceAndSaveOthersFromDocument("2015年花都区区府板块成交均价基本在1.1万/㎡起，2016年具有“前景板块”之称的花都区府板块又将迎来全新大盘荔园悦享花醍，虽未开盘，但其所推的户型赠送面积多，灵动N+1户型深受刚需买家青睐。");
//		System.out.println(termList.size());
//		for (String one:termList){
//			System.out.println(one);
//		}
//		String rule = getTopKSentence(termList,5);
//		System.out.println("*******************************");
//		System.out.println(rule);
//		String[] vs = rule.split("=");
//		System.out.println(vs.length);
//		for (String one:vs){
//			System.out.println(one);
//		}
		selectModelForRuleGenerateArticle("/home/min/workspace/data/ajk_zixun/ajk_zixun_text.txt","/home/min/workspace/data/ajk_zixun/rules/ajk_zixun_rule.txt");
	
//		Map<String,Map<String,Integer>> posCount = statisticPOSWordCount("/home/min/workspace/data/ajk_zixun/ajk_zixun_text.txt");
//		System.out.println(posCount.get("nt").keySet().size());
	}
	
  /**
   * 抽取出实意词,剩余词作为模板;
   * @param sentence
   * @return
   * 即实意词用APTO替换掉,并记录该抽取的是哪类实意词
   */
  public static String extractSignificanceAndSaveOthersFromSentence(String sentence) {
	List<Term> termList = NLPTokenizer.segment(sentence);
	StringBuffer result = new StringBuffer();
	if (termList != null) {
	  int len = termList.size();
	  for (int i = 0; i < len; i++) {
	    Term one = termList.get(i);
		//前缀实意词
		String pref = getSignificancePOSPrefix(one.nature);
		if (!pref.equals("default")) {
		  result.append("[" + pref + "]=");
		} else {
		  //前缀为空,看全部
		  String all = getSignificancePOSAll(one.nature);
		  if (!all.equals("default")) {
		    result.append("[" + all + "]=");
		  } else {
		    //前缀没有,全部也没有,则加词
		    result.append(one.word + "=");
		  }
	    }//end extract
	  }//end sentence
	}//sentence is not null
    return result.substring(0, result.length() - 1);
  }

  /**
   * 处理实意词性词
   * @param result
   * @param pos
   * @param word
   * @return
   */
  public static Map<String, Map<String,Integer>> processPosWordCount(Map<String, Map<String,Integer>> result, String pos, String word) {
    //词性是否存在
	if (result.keySet().contains(pos)) {
	  Map<String,Integer> ones = result.get(pos);
	  //词是否存在
	  if (ones.keySet().contains(word)) {
	    ones.put(word, ones.get(word) + 1);
	  } else {
		ones.put(word, 1);
	  }
	  result.put(pos, ones);
	 } else {
       Map<String,Integer> one = new HashMap<String, Integer>();
	   one.put(word, 1);
	   result.put(pos, one);
	 }
	 return result;
  }

  /**
   * 获得前缀词性
   * @param na
   * @return
   */
  public static String getSignificancePOSPrefix(Nature na) {
    String prefPOS = "default";
	for (String pos: SignificancePOSPrefix) {
	  if (na.startsWith(pos)) {
	    prefPOS = pos;
		break;
	   }
	}
    return prefPOS;
  }

  /**
   * 获得全部词性
   * @param na
   * @return
   */
  public static String getSignificancePOSAll(Nature na) {
    String prefPOS = "default";
	for (String pos: SignificancePOSAll) {
	if (na.name().equals(pos)){
	    prefPOS = pos;
		break;
	  }
	}
    return prefPOS;
  }
	
  /**
   * 从文档中提取出有意义的词
   * @param document
   * @return
   */
  public static List <String> extractSignificanceAndSaveOthersFromDocument(String document){
	List <String> result = new ArrayList<>();
	if (document != null) {
	  //1,先进行句子切分,注意不是细粒度,不是碰到标点符号就去切;粗粒度的切分.
	  List<String> sentences = dataBaseProcess.spiltSentence(document);
	  //2,再根据切分的句子进行实体抽取.
	  for (String line: sentences) {
	    result.add(extractSignificanceAndSaveOthersFromSentence(line));
	  }
	  //3,返回结果.
	}
	return result;
  }

  /**
   * 读取拉取的资讯,只取前5句作为模板,不够5句就取多少句.
   * @param filename
   * @param newfile
   * 每篇文档是一行，语料太多只取部分句子作为训练
   */
  public static void getRuleTrainData(String filename, String newfile){
	InputStreamReader inputReader = null;
    BufferedReader bufferReader = null;
    try {
      InputStream inputStream = new FileInputStream(filename);
      inputReader = new InputStreamReader(inputStream);
      bufferReader = new BufferedReader(inputReader);           
      String doc = null;
      while ((doc = bufferReader.readLine()) != null) {
        String[] vs = doc.split("\t");
        List<String> allSentences = extractSignificanceAndSaveOthersFromDocument(vs[1]);
        String rule = getTopKSentence(allSentences, 5);
        dataBaseProcess.writeLineToFile(vs[0] + "\t" + rule, newfile);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }  
  }
	
  /**
   * 取固定前几句话,作为规则的模板
   * @param list
   * @param k
   * @return
   */
  public static String getTopKSentence(List<String> list, int k){
	StringBuffer result = new StringBuffer();
	if (list != null && list.size() >= 1) {
	  List<String> res = list.subList(0, Math.min(list.size(), k));
	  int len = res.size();
	  for (int i = 0; i < len; i++) {
		result.append(res.get(i) + "=,=");
	  }
    }
	return result.substring(0, result.length() - 1);
  }

  /**
   * 从语料中抽取实意词,并统计其出现的频率
   * @param filename
   * @return
   * 每一类实意词性作为一个key,其后面是一个大串Map
   */
  public static Map<String, Map<String, Integer>> statisticPOSWordCount(String filename){
    InputStreamReader inputReader = null;
    BufferedReader bufferReader = null;
    Map<String, Map<String, Integer>> result = new HashMap<>();       
    try {
      InputStream inputStream = new FileInputStream(filename);
      inputReader = new InputStreamReader(inputStream);
      bufferReader = new BufferedReader(inputReader);           
      String doc = null;
      while ((doc = bufferReader.readLine()) != null) {           	
        List<Term> termList = NLPTokenizer.segment(doc);
        if (termList != null) {
          int len = termList.size();
          for (int i=0; i < len; i++) {
        	Term one = termList.get(i);
        	//前缀实意词
        	String pref = getSignificancePOSPrefix(one.nature);
        	if (!pref.equals("default")) {
        	  result = processPosWordCount(result,pref,one.word);
        	} else {
        	  //前缀为空,看全部
        	  String all = getSignificancePOSAll(one.nature);
        	  if (!all.equals("default")) {
        	    result = processPosWordCount(result, all, one.word);
        	  }
        	}//end extract
          }//end sentence
        }//sentence is not null
       }//排序
    } catch (IOException e){
      e.printStackTrace();
    } 
    return result;
  }
	
  /**
   * 挑选文章模板,
   * @param filename
   * @param newfile
   * 即选择需填入的词与剩余字的占比
   */
  public static void selectModelForRuleGenerateArticle(String filename, String newfile) {
    InputStreamReader inputReader = null;
    BufferedReader bufferReader = null;
    try {
      InputStream inputStream = new FileInputStream(filename);
      inputReader = new InputStreamReader(inputStream);
      bufferReader = new BufferedReader(inputReader);           
      String doc = null;
      while ((doc = bufferReader.readLine()) != null) {
        String[] vs = doc.split("\t");
        //抽取前文章字符长度
        double totalSentenChar = vs[1].length();
        //抽取后剩余字符长度
        double leaveSentenChar = 0;
        List<String> allSentences = extractSignificanceAndSaveOthersFromDocument(vs[1]);
        for (String one: allSentences) {
          List<String> onevs = Arrays.asList(one.split("="));
          int len = onevs.size();
          for (int i = 0; i < len; i++) {
            if (!(onevs.get(i).contains("[") && onevs.get(i).contains("]"))) {
              leaveSentenChar = leaveSentenChar + 1;
            }			
          }
        }
        String rule = getTopKSentence(allSentences, allSentences.size());
        dataBaseProcess.writeLineToFile(leaveSentenChar/totalSentenChar + "\t" + rule, newfile);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }  
  }
	
  /**
   * 给定m为中心的组块,抽取指定词性的块,两边的词性是挨着最近的那个
   * @param segs
   * @return
   */
  public static List<String> findAllDefineTagsIndex(List<Term> segs) {
    //0, 先把标点符号去掉
    //1,先去除标点符号
	List<Term> newSegs = new LinkedList<>();
	for(Term term:segs){
	  if (!term.nature.startsWith("w")) {
		newSegs.add(term);
	  }
	}
    //获取tag的连接关系
	Map<Character,List<Character>> firstToTwo = setFirtToTwoTagsMap();
	Map<Character,List<Character>> twoToThree = setTwoToThreeTagsMap();
	List<String> twos = Arrays.asList("nm,mv,mn,vm,mq".split(","));
	List<String> result = new ArrayList<>();
		
	int len = newSegs.size();
	int i = 0;
	while (i < len) {
	  char one = newSegs.get(i).nature.firstChar();
	  if (one == 'n') {// 在其下4个词内是否含n,
	    //搜索,搜索两次,即8个名词,一般不会超过这么多了,
		int newI1=againHasN(newSegs, i);
		i = i > newI1 ? i : newI1;
		newI1 = againHasN(newSegs, i);
		i = i > newI1 ? i : newI1;
		//找到最近的m,然后把n移到m的前一个,
		int firstM = nAfterFirstM(newSegs, i);
		if (firstM > i) {
		  //i和firstM-1进行交换,值也交换
		  Term termI = newSegs.get(i);
		  newSegs.set(firstM - 1, termI);
		  i = firstM - 1;
		} else {
		  i = i + 1;
		}
	   }
	   if (i >= len) {
		 break;
	   }
	   one = newSegs.get(i).nature.firstChar();
	   if(firstToTwo.keySet().contains(one)) {
	     //第一个tag在组块内
		 if (i+1<len && firstToTwo.get(one).contains(newSegs.get(i+1).nature.firstChar())) {
		   //第二个tag在组块内
		   char two = newSegs.get(i + 1).nature.firstChar();
		   if (i + 2 < len && twoToThree.get(two).contains(newSegs.get(i + 2).nature.firstChar())) {
		     //第三个tag在组块内
		     String threeWord = newSegs.get(i + 2).word;
			 String twoWord = newSegs.get(i + 1).word;
			 String oneWord = newSegs.get(i).word;
			 result.add(oneWord + "=" + twoWord + "=" + threeWord);
			 i = i + 3;
		   } else {
		     //第三个tag不在组块内,则取两个的,连续连个的tag有固定几个
		     if (twos.contains(String.valueOf(one) + String.valueOf(two))) {
			   String twoWord = newSegs.get(i + 1).word;
			   String oneWord = newSegs.get(i).word;
			   result.add(oneWord + "=" + twoWord);
			   i = i + 2;
		     } else {
		       i = i + 1;
		     }
		   }
		 } else {
		   i = i + 1;
		 }
	   } else { //第一个tag在不在
	     i = i + 1;
	   }
	}
	return result;
  }

  /**
   * 构造组块的连接顺序
   * @return
   */
  public static Map<Character, List<Character>> setFirtToTwoTagsMap() {
	Map<Character, List<Character>> firstToTwo = new HashMap<>();
	firstToTwo.put('n', Arrays.asList('m', 'v'));
	firstToTwo.put('m', Arrays.asList('v', 'n', 'q', 'm'));
	firstToTwo.put('s', Arrays.asList('m'));
	firstToTwo.put('a', Arrays.asList('m'));
	firstToTwo.put('v', Arrays.asList('m'));
	return firstToTwo;
  }
	
  /**
   * 第一个是n,在其4个之内之内是否再含n
   * @param segs
   * @param i
   * @return
   */
  public static int againHasN(List<Term> segs, int i){
    int len = segs.size();
	int end = (i + 4) > len ? len: i + 4;
	int result = -1;
	for (int j = i + 1; j < end; j++) {
	  if (segs.get(j).nature.firstChar() == 'n'){
	    result = j;
	  }
	}
    return result;
  }
	
  /**
   * 找到n后面4个词内的第一个m
   * @param segs
   * @param n
   * @return
   * 第一个是n,在其4个之内之内是否再含n
   */
  public static int nAfterFirstM(List<Term> segs, int n) {
    int len = segs.size();
	int end = (n + 4) > len ? len: n + 4;
	int result = -1;
	for (int j = n + 1; j < end; j++) {
	  if (segs.get(j).nature.firstChar() == 'm') {
	    result = j;
		break;
	  }
	}
	return result;
  }
	
  /**
   * 构造组块的连接顺序
   * @return
   */
  public static Map<Character, List<Character>> setTwoToThreeTagsMap() {
	Map<Character, List<Character>> TwoToThree = new HashMap<>();
	TwoToThree.put('n', Arrays.asList('n'));
	TwoToThree.put('q', Arrays.asList('n'));
	TwoToThree.put('m', Arrays.asList('q', 'v'));
	TwoToThree.put('v', Arrays.asList('n', 'm'));
	return TwoToThree;
  }
}
