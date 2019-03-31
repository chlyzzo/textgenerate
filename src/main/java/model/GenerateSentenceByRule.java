package model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import common.MapSoredClass;
import assist.ExtractEntityMethod;


/**
 * 基于规则的生成,即人工给出模板,往里添加一些词,使之成为一句话.
 * @author huwenhao
 *
 */
public class GenerateSentenceByRule {

  public static void main(String[] args) {

    String input = "从=[nr]=建筑=，=到=[nt]=，=[d]=到=[nz]=户型=……=，=[nz]=[d]=产品=[g]=升级=，=[d]=给=[ns]=带来=惊喜=,=[c]=这=次=[nz]=[d]=是=要=打=造=一个=以=阅读=为=主题=的=[nt]=,";
	String file = "/home/min/workspace/data/ajk_zixun/ajk_zixun_text.txt";
	Map<String,Map<String,Integer>> posWord = ExtractEntityMethod.statisticPOSWordCount(file);
	getText(input,posWord);
		
  }

  /**
   * 根据给定的模板,往里添加词,得到新的文章.
   * @param input
   * @param posWord
   * 取下一个词;词性统计,
   */
  public static void getText(String input, Map<String, Map<String, Integer>> posWord){
	List<String> vs = Arrays.asList(input.split("="));
	int len = vs.size();
	StringBuffer result = new StringBuffer();
	for (int i = 0; i < len; i ++) {
	  if (vs.get(i).contains("[") && vs.get(i).contains("]")) {
	    String currPos = vs.get(i).replace("[","").replace("]", "");
		String word = getOneWordFromPosWordCount("", posWord.get(currPos));
		vs.set(i, word);
	  }			
	  result.append(vs.get(i));
	}
	System.out.println(result.toString());	
  }
	
  /**
   * 实意词对应词性选择一个词
   * @param forward
   * @param posWord
   * @return
   */
  public static String getOneWordFromPosWordCount(String forward, Map<String, Integer> posWord){		
	String word = "";
	MapSoredClass<String> mapsored = new MapSoredClass<String>();
	mapsored.map = posWord;
	List<String> canditeWords = mapsored.sortDescByValueReturnKeys(true);		
	//随机选一个词
	word = GenarateTextMethod.selectWordInNextWordsRandomString(canditeWords);		
	return word;
  }
}
