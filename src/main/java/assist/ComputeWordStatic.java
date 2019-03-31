package assist;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.MapSoredClass;
import common.Word;

/**
 * 文本词频统计
 * 
 * @author huwenhao
 * 2019-03-30
 *
 */
public class ComputeWordStatic {

  public static void main(String[] args) {
		
    Map<String,Integer> wordCount = tf("/Users/huwenhao02/workspace/textGenerateByStastic/src/main/resources/fenghuang_zixun_test");
    System.out.println(wordCount.keySet());
	System.out.println(wordCount.get("脱贫"));
  }
	
  /**
   * 计算tf,仅仅词的频率
   * @param file
   * @return
   * 训练文本格式,文档id=word2[]word2[]word3[]...[],
   */
  public static Map<String, Integer> tf(String file) {
    Map<String,Integer> result = new HashMap<>();
    InputStreamReader inputReader = null;
    BufferedReader bufferReader = null;
    try {
      InputStream inputStream = new FileInputStream(file);
      inputReader = new InputStreamReader(inputStream);
      bufferReader = new BufferedReader(inputReader);           
      String line = null;
            
      while ((line = bufferReader.readLine()) != null) {
        //一些训练集是没有词的
        if (line.split("=").length == 2) {
          String[] words = line.split("=")[1].split(" ");
          for (String word : words) {  
            //统计词的出现频率
            if (!result.keySet().contains(word)) {
              result.put(word, 1);
            } else {
              result.put(word, result.get(word) + 1);
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }  
    return result;
  }
	
  /**
   * 根据读入的语料,统计词的频率
   * @param corpus
   * @return
   */
  public static Map<Word, Integer> tf(List<List<Word>> corpus) {
    Map<Word,Integer> result = new HashMap<>();
    int totalDocs = corpus.size();
    //每篇文档遍历词
    for (int doc = 0; doc < totalDocs; doc++) {
      List<Word> docWords = corpus.get(doc);
      int docWordCount = docWords.size();
      //每个词遍历
      for (int toWord = 0; toWord < docWordCount; toWord++) {
        if (result.keySet().contains(docWords.get(toWord))) {
          result.put(docWords.get(toWord), result.get(docWords.get(toWord)) + 1);
        } else {
          result.put(docWords.get(toWord), 1);
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
   * 根据读入的语料,统计词性下的词的频率
   * @param corpus
   * @return
   */
  public static Map<String,Map<Word,Integer>> tagWordtf(List<List<Word>> corpus) {
    Map<String,Map<Word,Integer>> result = new HashMap<>();
    int totalDocs = corpus.size();
    //每篇文档遍历词
    for (int doc = 0; doc < totalDocs; doc++) {
      List<Word> docWords = corpus.get(doc);
      int docWordCount = docWords.size();
	  //每个词遍历,记录词性
      for (int toWord = 0; toWord < docWordCount; toWord++) {
        String tag = docWords.get(toWord).getWordTag();
        Word word = docWords.get(toWord);
        if (result.keySet().contains(tag)) {			    	
          Map<Word, Integer> alreadyWords = result.get(tag);
          //词性下的词是否已经存在
          if (alreadyWords.keySet().contains(word)) {
            alreadyWords.put(word, alreadyWords.get(word) + 1);
          } else {
            alreadyWords.put(word, 1);
          }
          result.put(tag, alreadyWords);
        } else {
          Map<Word, Integer> alreadyWords = new HashMap<>();
          alreadyWords.put(word, 1);
          result.put(tag, alreadyWords);
        }
      }//end one document,
    }
    //对结果按照value排序,需进行遍历
    for (String tag: result.keySet()) {
      Map<Word, Integer> tagWords = result.get(tag);
      MapSoredClass<Word> mapsored = new MapSoredClass<Word>();
      mapsored.map = tagWords;
      tagWords = mapsored.getSortedDesc(true);
      result.put(tag, tagWords);
    }
    return result; 
  }
}
