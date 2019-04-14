package assist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lda.LdaUtil;
import lda.Vocabulary;
import common.Word;
import common.DataProcess;
import common.MapSoredClass;

/**
 * 生成text的主题和词的统计
 * @author huwenhao
 *
 */
public class TopicWordProcess {

    public static void main(String[] args) {
        List<List<Word>> corpus = DataProcess.readOnlySegCorpus("/home/min/workspace/data/ajk_zixun/generate_text/part-00000");
	TopicWordProcess.getWordNextWord(corpus, "另一方面").keySet().forEach(r -> {
            System.out.println(((Word) r).getWordName());
        });
    }

    /**
     * 获取topic下的词分布
     * @return
     */
    public static double[][] getTopicWordDistribute() {
	return LdaUtil.getTopicWordDistribute();
    }
	
    /**
     * 获取词的topic分布
     * @param topicword
     * @return
     */
    public static double[][] getWordTopicDistribute(double[][] topicword) {
	double [][] wordtopic = new double[topicword[0].length][topicword.length];
	long allWord = topicword[0].length;
	for (int i = 0; i < allWord; i++) {
	    wordtopic[i] = getTwoArraycolumn(topicword, i);
	}
	return wordtopic;
    }
  
    /**
     * 获取词的topic分布
     * @param topicword
     * @return
     */
    public static double[][] getWordTopicDistribute() {
	double[][] topicword = getTopicWordDistribute();
	double [][] wordtopic = new double[topicword[0].length][topicword.length];
	long allWord = topicword[0].length;
	for (int i = 0; i < allWord; i++) {
	    wordtopic[i] = getTwoArraycolumn(topicword, i);
	}
	return wordtopic;
    }
    
    /**
     * 获取二维数组的某一列
     * @param strarray
     * @param column
     * @return
     */
    public static double[] getTwoArraycolumn(double[][] strarray, int column){
        int rowlength = strarray.length;
        double[] templist = new double[rowlength];
        for(int i = 0; i < rowlength; i++) {
            templist[i] = strarray[i][column];
        }
        return templist;
    }

    /**
     * 获取词典,词--id,id--词
     * @return
     */
    public static Vocabulary getModelVocabulary() {
        return LdaUtil.getModelVocabulary();
    }
	
    /**
     * 指定词的下一个词
     * @param corpus
     * @param word
     * @return
     * 没法全量计算,量太大;均用词的id来算;
     * 词id与词的对应关系在词典中获得
     */
    public static Map<Word, Integer> getWordNextWord(List<List<Word>> corpus, String word){
	Map<Word, Integer> result = new HashMap<>();
	int totalDocs = corpus.size();
	//每篇文档中词查找next
	for (int doc = 0; doc < totalDocs; doc++) {
	    List<Word> docWords = corpus.get(doc);
	    int docWordCount = docWords.size();
	    //每个词遍历
	    for (int i = 0; i < docWordCount; i++) {
	        if(docWords.get(i).getWordName().equals(word) && i + 1 < docWordCount) { //找到,则下一个字id保存,
		    if (result.keySet().contains(docWords.get(i + 1))) {
		        result.put(docWords.get(i + 1), result.get(docWords.get(i + 1)) + 1);
		    } else {
		        result.put(docWords.get(i + 1), 1);
		    }
		    i = i + 2;
		}
	    }//end one document,
	}
	//对结果按照value排序
	MapSoredClass<Word> mapsored = new MapSoredClass<Word>();
	mapsored.map = result;
	result = mapsored.getSortedDesc(true);
	return result;
    }

    /**
     * 指定词的第一个词
     * @param corpus
     * @param word
     * @return
     */
    public static Map<Word, Integer> getWordFirstWord(List<List<Word>> corpus, String word){	
	Map<Word, Integer> result = new HashMap<>();
	int totalDocs = corpus.size();
	//每篇文档中词查找开头第一词
	for (int doc = 0; doc < totalDocs; doc++) {
	    List<String> docWords = new ArrayList<>();
	    corpus.get(doc).forEach(r -> { docWords.add(r.getWordName());});
	    Word first = corpus.get(doc).get(0);
	    if(docWords.contains(word)) { 
		if (result.keySet().contains(first)) {
		    result.put(first, result.get(first) + 1);
		} else {
		    result.put(first, 1);
		}
	    }
	}//end one document,
	//对结果按照value排序
	MapSoredClass<Word> mapsored = new MapSoredClass<Word>();
	mapsored.map = result;
	result = mapsored.getSortedDesc(true);
	return result;
    }
  
    /**
     * List转Set
     * @param list
     * @return
     */
    public static Set<Integer> listToSet(List<Integer> list) {
        Set<Integer> set= new HashSet<Integer>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return set;
    }

    /**
     * array转set
     * @param array
     * @return
     */
    public static Set<Integer> arrayToSet(int[] array) {
	Set<Integer> set = new HashSet<>();
        int len = array.length;
        for(int i = 0; i < len; i++) {
            set.add(array[i]);
        }
        return set;
    }
    
    /**
     * 两个Set求并集
     * @param s1
     * @param s2
     * @return
     */
    public static Set<Integer> setUnion(Set<Integer> s1, Set<Integer> s2) {
        Set<Integer> unionSet = new HashSet<Integer>();
        for(Integer s: s1) {
            unionSet.add(s);
        }
        for(Integer s: s2) {
            unionSet.add(s);
        }
        return unionSet;
    }
    
    /**
     * 排序map返回key
     * @param map
     * @return
     */
    public static List<Word> sortMapByValueReturnKeys(Map<Word, Integer> map) {
        MapSoredClass<Word> mapsored = new MapSoredClass<Word>();
	mapsored.map = map;
        return mapsored.sortDescByValueReturnKeys(true);
    } 
}
