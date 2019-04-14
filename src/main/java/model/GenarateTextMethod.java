package model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import assist.TopicWordProcess;
import lda.Corpus;
import lda.LdaUtil;
import lda.Vocabulary;
import common.Word;

/**
 * 生成文本的方法,这里可拓展出多个方法
 * @author huwenhao
 *
 */
public class GenarateTextMethod {
  
    private static int MAX_TOPIC_WORDS = 200; //一个主题下选取的前最大的词个数
  
    public static void main(String[] args) throws IOException {
		

    }
	
    /**
     * 根据生成的结果转成句子
     * @param output
     * @return
     */
    public static String generateSentenceFromOutputWords(List<String> output) {
	if (output != null) {
	    int len = output.size();
	    String result = "";
	    for(int i = 0; i < len; i++) {
		result += output.get(i);
	    }
	    return result;
	}
	return null;
    }
	
    /**
     * lda模型中词id转为词串
     * @param ids
     * @param vocabulary
     * @return
     */
    public static Set<String> getWordFromLdaVocaByIds(Set<Integer> ids, Vocabulary vocabulary) {
        Set<String> result = new HashSet<>();
	for (Integer id: ids) {
	    result.add(vocabulary.getWord(id));
	}
	return result;
    }
  
    /**
     * lda模型中词id转为词串
     * @param ids
     * @param vocabulary
     * @return
     */
    public static Set<String> getWordFromLdaVocaByIds(int[] ids, Vocabulary vocabulary) {
	Set<String> result = new HashSet<>();
	for (Integer id: ids) {
	    result.add(vocabulary.getWord(id));
	}
	return result;
    }
  
    /**
     * 随机从当前词的下一个候选中选取一个作为下一个词
     * @param nextWords
     * @return
     */
    public static String selectWordInNextWordsRandom(List<Word> nextWords) {
	String nextWord = "";
	if (nextWords != null) {
            int len = nextWords.size();
	    Random ne =new Random();
	    Integer randomWordIndex = ne.nextInt(len) % (len + 1);
            nextWord = nextWords.get(randomWordIndex).getWordName();
	}
	return nextWord;
    }

    /**
     * 随机从当前词的下一个候选中选取一个作为下一个词
     * @param nextWords
     * @return
     */
    public static String selectWordInNextWordsRandomString(List<String> nextWords) {
	String nextWord = "";
	if (nextWords != null) {
	    int len = nextWords.size();
	    Random ne =new Random();
	    Integer randomWordIndex = ne.nextInt(len) % (len + 1);
            nextWord = nextWords.get(randomWordIndex);
	}
	return nextWord;
    }
	
    /**
     * 随机挑选符合词性的词
     * @param nextWords
     * @param tag
     * @return
     */
    public static String selectWordInNextWordsRandom(List<Word> nextWords, String tag) {
	Word nextWord = null;
	if (nextWords != null) {
	    int len = nextWords.size();
	    int randomMax = len;
	    String selectTag = null;
	    while (!tag.equals(selectTag) && len > 0) {
                Random ne =new Random();
		Integer randomWordIndex = ne.nextInt(randomMax) % (randomMax + 1);
		nextWord = nextWords.get(randomWordIndex);
		selectTag = nextWord.getWordTag();
		len--;
	    }
	}
	if (nextWord != null) {
	    return nextWord.getWordName();
	}
	return null;
    }
	
    /**
     * 选择最大概率那个词
     * @param nextWords
     * @return
     */
    public static String selectWordInNextWordsMax(List<Word> nextWords) {
	String nextWord = "";
	if (nextWords != null) {
	    nextWord = nextWords.get(0).getWordName();
	}
	return nextWord;
    }
	
    /**
     * 挑选符合词性的最大概率的那个词
     * @param nextWords
     * @param tag
     * @return
     */
    public static String selectWordInNextWordsMax(List<Word> nextWords, String tag) {
	String nextWord = null;
	if (nextWords != null) {
	    int len = nextWords.size();
	    for(int i = 0; i < len; i ++) {
                if (tag.equals(nextWords.get(i).getWordTag())) {
		    nextWord = nextWords.get(i).getWordName();
		    break;
                }
	    }
	}
	return nextWord;
    }

    /**
     * 得到一个句子或者文章的开始词候选集合
     * @param maxTopic
     * @param inputDoc
     * @param generateModel
     * @return
     * @throws IOException
     */
    public static Map<Word, Integer> getFirstSentenceCandidates(int maxTopic, List<String> inputDoc, GenerateModel generateModel) throws IOException {
	System.out.println("get fisrt words");
	int[] maxTopicWordsId = LdaUtil.getMaxTopicWordDistribute(maxTopic, generateModel.getPhi(), MAX_TOPIC_WORDS);
	Set<String> basedWords = getWordFromLdaVocaByIds(maxTopicWordsId,generateModel.getVocabulary());
	basedWords.addAll(inputDoc);
	System.out.println(basedWords.size());
	//获得第一个词id和频率
	Map<Word, Integer> wordGenerate = new HashMap<>();
	for (String word: basedWords) {
	    if (TopicWordProcess.getWordFirstWord(generateModel.getCorpus(), word) != null) {
	   	wordGenerate.putAll(TopicWordProcess.getWordFirstWord(generateModel.getCorpus(), word));
	    }
	}
	basedWords.clear();
 	System.out.println("first words has " + wordGenerate.keySet().size() + "!!!");
	return wordGenerate;
    }
	
    /**
     * 根据主题判断一个句子是否符合既定主题
     * @param input
     * @param generateModel
     * @param topicIndex
     * @return
     * @throws IOException
     */
    public static boolean generateSentJudgeByTopic(List<String> input, GenerateModel generateModel, int topicIndex) throws IOException{
		
	int[] document = Corpus.loadString(input, generateModel.vocabulary);
	int topic = LdaUtil.getMaxTopicIndex(document, generateModel.topicWord);
	if (topic == topicIndex) {
	    return true;
	} else {
	    return false;
	}
    }
	
}
