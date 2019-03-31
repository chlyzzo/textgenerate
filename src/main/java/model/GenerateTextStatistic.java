package model;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import assist.TopicWordProcess;
import lda.LdaUtil;
import common.Word;
import common.DataProcess;

/**
 * 统计生成文本
 * @author huwenhao02
 * 依赖于lda模型的结果
 */
public class GenerateTextStatistic {

  public static void main(String[] args) throws IOException {
		
	String file = "/home/min/workspace/data/ajk_zixun/generate_text/part-00000";
	GenerateModel generate = DataProcess.getGenerateModel(file);
	System.out.println(generate.getCorpus().size());
	List<String> input = Arrays.asList("上海 房地产 涨价 楼市".split(" "));
	List<String> out = getText(20, input, generate);
	System.out.println(out);
  }
	
  /**
   * 根据输入的关键字和返回的词数量生成一段文本.
   * @param count
   * @param input
   * @param generateModel
   * @return
   * @throws IOException
   * 依赖于lda模型训练的结果，生成的文本与输入的关键词的主题一致
   */
  public static List<String> getText(int count, List<String> input, GenerateModel generateModel) throws IOException{
		
    List<String> output = new LinkedList<>();
	//1,根据候选词得到第一个词
	int maxToipc = LdaUtil.getMaxTopicIndex(input, generateModel.getPhi(), generateModel.getVocabulary());    
	Map<Word, Integer> firstWordsMap = GenarateTextMethod.getFirstSentenceCandidates(maxToipc, input, generateModel);
	//2.1.1,选择挑选第一词的方法,随机的,
	String firstWord = GenarateTextMethod.selectWordInNextWordsRandom(
	  TopicWordProcess.sortMapByValueReturnKeys(firstWordsMap));
	output.add(firstWord);
	//2.1.2,选择挑选第一词的方法,选最大的,
	//GenarateTextMethod.selectWordInNextWordsMax();
	//3,循环生成直到词个数满足
	int start = 1;
	while (start <= count) {
	  firstWordsMap = TopicWordProcess.getWordNextWord(generateModel.getCorpus(), firstWord);
	  //根据随机还是最大选择下一个词
	  firstWord = GenarateTextMethod.selectWordInNextWordsRandom(
		TopicWordProcess.sortMapByValueReturnKeys(firstWordsMap));
	  output.add(firstWord);
	  start ++;
	}
	return output;
  }

}

