/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2015/1/29 17:03</create-date>
 *
 * <copyright file="Corpus.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package lda;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * a set of documents
 * 语料库，也就是文档集合
 *
 * @author hankcs
 */
public class Corpus
{
    List<int[]> documentList;
    Vocabulary vocabulary;

    public Corpus()
    {
        documentList = new LinkedList<int[]>();
        vocabulary = new Vocabulary();
    }

    public int[] addDocument(List<String> document)
    {
        int[] doc = new int[document.size()];
        int i = 0;
        for (String word : document)
        {
            doc[i++] = vocabulary.getId(word, true);
        }
        documentList.add(doc);
        return doc;
    }

    public int[][] toArray()
    {
        return (int[][]) documentList.toArray(new int[0][]);
    }

    public int getVocabularySize()
    {
        return vocabulary.size();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        for (int[] doc : documentList)
        {
            sb.append(Arrays.toString(doc)).append("\n");
        }
        sb.append(vocabulary);
        return sb.toString();
    }

    /**
     * 从目录下加载训练集
     *
     * @param folderPath is a folder, which contains text documents.每篇文档一个文件，
     * 文件内容是分好词的，并且去除了停用了，
     * @return a corpus
     * @throws IOException
     */
    public static Corpus load(String folderPath) throws IOException
    {
        Corpus corpus = new Corpus();
        File folder = new File(folderPath);
        int count = 0;
        for (File file : folder.listFiles()){
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;
            List<String> wordList = new LinkedList<String>();
            while ((line = br.readLine()) != null){
            	if (line.split("=").length==2) {
                    String[] words = line.split("=")[1].split(" ");
                    for (String word : words){
                        if (word.trim().length() < 2) continue;
                            wordList.add(word);
                     }
            	}
            }
            br.close();
            corpus.addDocument(wordList);
            count++;
        }
        if (corpus.getVocabularySize() == 0) return null;
        System.out.print("count="+count);
        return corpus;
    }
    /**
     * Load documents from a file
     *
     * @param filePath is a file, which contains documents.每篇文档一行，id+"\t"+words
     * 文档内容是分好词的，并且去除了停用了，空格隔开，
     * @return a corpus
     * @throws IOException
     */
    
    //加载停用词
    public static List<String> loadStopWords(String filePath) throws IOException{
    	List<String> stopWords = new ArrayList<>();
    	InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        try
        {
            InputStream inputStream = new FileInputStream(filePath);
            inputReader = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(inputReader);           
            String line = null;
            while ((line = bufferReader.readLine()) != null)
            {
            	stopWords.add(line.replaceAll("\r\n", ""));
            }
            bufferReader.close();
            inputReader.close();
            inputStream.close();
            return stopWords;

        }
        catch (IOException e)
        {
        	 e.printStackTrace();
        }  

    	return stopWords;
    }
    
    /***
     * 读取训练集
     * docid \t word1[]word2[]word3[]...[]
     * 
     * ***/
    public static Corpus loadFromFile(String filePath) throws IOException
    {
    	InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        Corpus corpus = new Corpus();
        try
        {
            InputStream inputStream = new FileInputStream(filePath);
            inputReader = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(inputReader);           
            String line = null;
            List<String> stopwords = loadStopWords("/home/min/workspace/MyJava/bin/data/dictionary/stopwords.txt");
            while ((line = bufferReader.readLine()) != null)
            {
            	List<String> wordList = new LinkedList<String>();
            	//一些训练集是没有词的
            	if (line.split("=").length==2){
            	       String[] words = line.split("=")[1].split(" ");
                       for (String word : words)
                       {                   	   
                           if (word.trim().length() >= 2 && !stopwords.contains(word)){
                                  wordList.add(word);
                           }
                        }
                       if (wordList.size()>0){
                             corpus.addDocument(wordList);
                       }
            	}
            }
        }
        catch (IOException e)
        {
        	 e.printStackTrace();
        }  
        System.out.println("load ok,the size="+corpus.getVocabularySize());
        if (corpus.getVocabularySize() == 0) return null;
        else return corpus;
    }
   
    public Vocabulary getVocabulary()
    {
        return vocabulary;
    }

    public int[][] getDocument()
    {
        return toArray();
    }
    /**
     * a document, the all word,in the vocabulary, return the [i]
     *
     * @param filePath is a file, which is a document.一篇文档
     * 文档内容是分好词的，并且去除了停用了，空格隔开，
     * @return int[]
     * @throws IOException
     */
    public static int[] loadString(List<String> words, Vocabulary vocabulary) throws IOException
    {
       List<Integer> wordList = new LinkedList<Integer>();
       for (String word : words) {
           if (word.trim().length() < 2) continue;
           Integer id = vocabulary.getId(word);
           if (id != null)
                    wordList.add(id);
       }
   
        int[] result = new int[wordList.size()];
        int i = 0;
        for (Integer integer : wordList) {
            result[i++] = integer;
        }
        return result;
    }
    
    public static int[] loadDocument(String path, Vocabulary vocabulary) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        List<Integer> wordList = new LinkedList<Integer>();
        while ((line = br.readLine()) != null)
        {
            String[] words = line.split(" ");
            for (String word : words)
            {
                if (word.trim().length() < 2) continue;
                Integer id = vocabulary.getId(word);
                if (id != null)
                    wordList.add(id);
            }
        }
        br.close();
        int[] result = new int[wordList.size()];
        int i = 0;
        for (Integer integer : wordList)
        {
            result[i++] = integer;
        }
        return result;
    }
}
