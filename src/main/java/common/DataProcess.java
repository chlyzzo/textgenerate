package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import com.hankcs.hanlp.utility.SentencesUtil;
import lda.LdaUtil;
import lda.Vocabulary;
import model.GenerateModel;

public class DataProcess {

    /**
     * 根据生成文本的语料,读取训练集并获得生成model
     * @param file
     * @return
     */
    public static GenerateModel getGenerateModel(String file) {
	    Vocabulary vocabulary = LdaUtil.getModelVocabulary();
	    double[][] phi = LdaUtil.getTopicWordDistribute();
	    List<List<Word>> corpos = DataProcess.readOnlySegCorpus(file);
	    GenerateModel generate = new GenerateModel(phi, vocabulary, corpos);
	    return generate;
    }
	
    /**
     * 切分出句子
     * @param doc
     * @return
     * 按照标点符号切分,细粒度,
     */
    public static List<String> getSentences(String doc) {
	    return SentencesUtil.toSentenceList(doc);
    }
  
    /**
     * 将文章分割为句子
     * @param document
     * @return
     * 按照固定的标点切分,粗粒度;
     */
    public static List<String> spiltSentence(String document) {
        List<String> sentences = new ArrayList<String>();
        for (String line: document.split("[\r\n]")) {
            line = line.trim();
            if (line.length() == 0) continue;
            for (String sent: line.split("[。；？：！]")) {
                sent = sent.trim();
                if (sent.length() == 0) continue;
                sentences.add(sent);
            }
        }
        return sentences;
    }

    /**
     * 读取生成文本的语料集
     * @param file
     * @return
     * 注意生成文本的语料格式
     * docid=word->tag[]word->tag[]..[]word->tag
     */
    public static List<List<Word>> readOnlySegCorpus(String file) {
	    List<List<Word>> result = new ArrayList<>();
	    InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            inputReader = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(inputReader);           
            String line = null;
            while ((line = bufferReader.readLine()) != null) {
                List<Word> wordList = new LinkedList<Word>();
                //一些训练集是没有词的
                if (line.split("=").length == 2) {
                    String[] words = line.split("=")[1].split(" ");
                    for (String word: words) {    
                        if (word.split("->").length == 2) {
                            String[] wordTag = word.split("->");
                            Word word1 = new Word(wordTag[0], wordTag[1]);
                            wordList.add(word1);
                        }
                    }
                    if (wordList.size() > 0) {
                        result.add(wordList);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }  
        return result;
    }

    /**
     * 读取训练集的词性排列
     * @param file
     * @return
     */
    public static List<List<String>> readTrainTag(String file) {
        List<List<String>> result = new ArrayList<>();
	    InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        try {
            InputStream inputStream = new FileInputStream(file);
            inputReader = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(inputReader);           
            String line = null;
            while ((line = bufferReader.readLine()) != null) {
                List<String> oneSort = Arrays.asList(line.split(","));
                result.add(oneSort);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }  
        return result;
    }
	
    /**
     * 随机选择一个词性排列
     * @param data
     * @return
     */
    public static List<String> selectOneTagSortByRandom(List<List<String>> data) {
	    List<String> oneSort = new ArrayList<>();
	    if (data != null) {
	        int len = data.size();
	        Random ne =new Random();
	        Integer randomndex = ne.nextInt(len) % (len + 1);
	        oneSort = data.get(randomndex);
	        data.clear();
	    }
	    return oneSort;
    }
	
    /**
     * 每次追加写入文件,每次写入一行
     * @param line
     * @param filepath
     * @throws IOException
     */
    public static void writeLineToFile (String line, String filepath) throws IOException {
	    File writename = new File(filepath); // 相对路径，如果没有则要建立一个新的output。txt文件  
        writename.createNewFile(); // 创建新文件  
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath, true)));  
        out.write(line + "\r\n"); // \r\n即为换行  
        out.flush(); // 把缓存区内容压入文件  
        out.close(); // 最后记得关闭文件  
    }
}
