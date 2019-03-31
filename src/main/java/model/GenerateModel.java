package model;

import java.util.List;
import common.Word;
import lda.Vocabulary;

/**
 * 生成文本的模型类
 * @author huwenhao
 * 2019-03-30
 * 生成文本的模型类，存储主题下的词，词典，语料等
 */
public class GenerateModel {
	
  double[][] topicWord = null;
	Vocabulary vocabulary = null;
	List<List<Word>> corpus = null;
	
  public GenerateModel(double[][] TopicWord, Vocabulary Vocabulary, List<List<Word>> Corpus ) {
	topicWord = TopicWord;
	vocabulary = Vocabulary;
	corpus = Corpus;
  }
	
  public List<List<Word>> getCorpus() {
	return this.corpus;
  }
	
  public Vocabulary getVocabulary() {
	return this.vocabulary;
  }
	
  public double[][] getPhi() {
	return this.topicWord;
  }
}
