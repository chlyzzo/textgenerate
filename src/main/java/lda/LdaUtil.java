/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2015/1/29 19:07</create-date>
 *
 * <copyright file="LdaUtil.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package lda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * @author hankcs,chlyzzo
 * 新增模型保存和模型读取
 */
public class LdaUtil
{
    /**
     * To translate a LDA matrix to readable result
     * @param phi the LDA model
     * @param vocabulary
     * @param limit limit of max words in a topic
     * @return a map array
     */
    public static Map<String, Double>[] translate(double[][] phi, Vocabulary vocabulary, int limit)
    {
        limit = Math.min(limit, phi[0].length);
        @SuppressWarnings("unchecked")
		Map<String, Double>[] result = new HashMap[phi.length];
        for (int k = 0; k < phi.length; k++)
        {
            Map<Double, String> rankMap = new TreeMap<Double, String>(Collections.reverseOrder());
            for (int i = 0; i < phi[k].length; i++)
            {
                rankMap.put(phi[k][i], vocabulary.getWord(i));
            }
            Iterator<Map.Entry<Double, String>> iterator = rankMap.entrySet().iterator();
            result[k] = new LinkedHashMap<String, Double>();
            for (int i = 0; i < limit; ++i)
            {
                Map.Entry<Double, String> entry = iterator.next();
                result[k].put(entry.getValue(), entry.getKey());
            }
        }
        return result;
    }

    public static Map<String, Double> translate(double[] tp, double[][] phi, Vocabulary vocabulary, int limit)
    {
        Map<String, Double>[] topicMapArray = translate(phi, vocabulary, limit);
        double p = -1.0;
        int t = -1;
        for (int k = 0; k < tp.length; k++)
        {
            if (tp[k] > p)
            {
                p = tp[k];
                t = k;
            }
        }
        System.out.print("t="+t);
        return topicMapArray[t];
    }

    /**
     * To print the result in a well formatted form
     * @param result
     */
    //输出所有主题和主题下的词+概率，lda训练学校的主题，
    public static void explain(Map<String, Double>[] result)
    {
        int i = 0;
        for (Map<String, Double> topicMap : result)
        {
            System.out.printf("topic %d :\n", i++);
            explain(topicMap);
            System.out.println();
        }
    }
    //输出指定主题下的所有词+概率
    public static void explain(Map<String, Double> topicMap)
    {
        for (Map.Entry<String, Double> entry : topicMap.entrySet())
        {
            System.out.println(entry);
        }
    }
    
    /**模型保存
     * topic-word写入phi.txt
     * 词典写入 voca.txt
     * **/
    public static void saveModel(double[][] phi,Vocabulary vocabulary) throws IOException{
    	//1,写入phi
    	int row = phi.length;
    	//int column = phi[0].length;
    	String phiPath = "phi.txt";
    	File writename = new File(phiPath); // 相对路径，如果没有则要建立一个新的output。txt文件
    	if (writename.exists()) {
            System.out.println("File existed");
            writename.delete();
        }
         writename.createNewFile(); // 创建新文件  
         BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(phiPath, true)));  
         for(int i=0;i<row;i++){
        	 String line = arrayPut(phi[i]);
        	 out.write(i+"="+line+"\r\n"); // \r\n即为换行  
         }
         out.flush(); // 把缓存区内容压入文件  
         out.close(); // 最后记得关闭文件  
         //2,写入vocabulary,主要是一个map,和一个String[]
         String[] id2wordMap = vocabulary.id2wordMap;
         row = phi[0].length;
         String vocaPath = "voca.txt";
    	 writename = new File(vocaPath); // 相对路径，如果没有则要建立一个新的output。txt文件 
    	 if (writename.exists()) {
             System.out.println("File existed");
             writename.delete();
         }
         writename.createNewFile(); // 创建新文件  
         out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(vocaPath, true)));      
         for(int i=0;i<row;i++){
        	 String line = id2wordMap[i];        	 
        	 out.write(i+"="+line+"\r\n"); // \r\n即为换行  
         }              
         out.flush(); // 把缓存区内容压入文件  
         out.close(); // 最后记得关闭文件 
         System.out.print("save model over");
    }
    
    /**模型读取***/
    public static double[][] getTopicWordDistribute(){
    	//1,读取txt文件
    	String phiPath = "phi.txt";
    	InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;  
        List<String> strbuffer = new ArrayList<>();    
        try {
            InputStream inputStream = new FileInputStream(phiPath);
            inputReader = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(inputReader);           
            // 读取一行
            String line = null;
            while ((line = bufferReader.readLine()) != null){            	
            	strbuffer.add(line);
            } 
            bufferReader.close();
            //
            int row = strbuffer.size();
            int column = strbuffer.get(0).split(",").length;
            double[][]res =new double[row][column];
            row = 0;
            for (String s: strbuffer){
            	String str = s.split("=")[1];
            	res[row] = arrayStringToDouble(str.split(","));
            	row ++;
            }
            return res;
        }
        catch (IOException e){
        	 e.printStackTrace();
        }
        return null;        
    }
    
    /**读取词典**/
    public static Vocabulary getModelVocabulary(){
    	//1,读取txt文件
    	String phiPath = "voca.txt";
    	InputStreamReader inputReader = null;
        BufferedReader bufferReader = null; 
        Vocabulary vocabulary=new Vocabulary();
        try{
            InputStream inputStream = new FileInputStream(phiPath);
            inputReader = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(inputReader);           
            // 读取一行
            String line = null;
            Map<String,Integer> word2idMap =new HashMap<>();
            StringBuffer id2wordMap = new StringBuffer();
            while ((line = bufferReader.readLine()) != null){   
            	if (line.split("=").length==2){
            	    int id = Integer.parseInt(line.split("=")[0]);
            	    String word = line.split("=")[1];
            	    word2idMap.put(word,id);
            	    id2wordMap.append(word+",");
            	}
            	else {
            		int id = Integer.parseInt(line.split("=")[0]);
            		String word = "stop";
            	    word2idMap.put(word,id);
            	    id2wordMap.append(word+",");
            	}
            } 
            bufferReader.close();           
            vocabulary.word2idMap = word2idMap;
            vocabulary.id2wordMap = id2wordMap.substring(0, id2wordMap.length()-1).toString().split(",");
            
            return vocabulary;
        }
        catch (IOException e){
        	 e.printStackTrace();
        }
        return null;        
    }
    
    public static String arrayPut(double[] tp)
    {
    	int len=tp.length;
    	StringBuffer res = new StringBuffer();
    	for(int i=0;i<len;i++)
    	{
    		res.append(tp[i]+",");
    	}
    	return res.substring(0, res.length()-1);
    }
    public static String arrayPut(int[] tp)
    {
    	int len=tp.length;
    	StringBuffer res = new StringBuffer();
    	for(int i=0;i<len;i++)
    	{
    		res.append(tp[i]+",");
    	}
    	return res.substring(0, res.length()-1);
    }
    public static String arrayPut(String[] tp)
    {
    	int len=tp.length;
    	StringBuffer res = new StringBuffer();
    	for(int i=0;i<len;i++)
    	{
    		res.append(tp[i]+",");
    	}
    	return res.substring(0, res.length()-1);
    }
    public static int arrayMax(double[] tp1)
    {
    	int len=tp1.length;
    	Double max= 0.0;
    	int res = -1;
    	for(int i=0;i<len;i++)
    	{
    		if (tp1[i]>max){
    			res= i;
    		}
    	}
    	return res;
    }
    
    public static double[] arrayStringToDouble(String[] tp)
    {
    	int len=tp.length;
    	double[] res = new double[len];
    	for(int i=0;i<len;i++)
    	{
    		res[i]=Double.valueOf(tp[i]);
    	}
    	return res;
    }
    
    /**找出数组中最大值的下标**/
    public static int getMaxInArrayIndex(double[] data){
    	List<Double> list = new ArrayList<>();
    	List<Double> list2 = new ArrayList<>();
    	for (int i = 0; i < data.length; i++) {
    		list.add(data[i]);
    		list2.add(data[i]);
    	}
    	Collections.sort(list);
    	int max = list2.indexOf(list.get(data.length - 1));
    	System.out.println("the max topic index="+max);
    	return max;
    }
    
    /**找出数组中前k大的下标**/
    public static int[] getMaxKInArrayIndex(double[] data,int k){
    	List<Double> list = new ArrayList<>();
    	List<Double> list2 = new ArrayList<>();
    	int total = data.length;
    	for (int i = 0; i < total; i++) {
    		list.add(data[i]);
    		list2.add(data[i]);
    	}
    	Collections.sort(list);
    	k = Math.min(k, total);
    	int[] result = new int[k];
    	for (int i=0;i<k;i++){
    		result[i]=list2.indexOf(list.get(total-1));
    		total=total-1;
    	}
    	return result;
    }
    

    /**根据输入的文本,得到主题分布,返回选择最大的那个主题,并返回最大的那个主题下的limit个词id
     * inputDoc 文本,词,以空格隔开,
     * @throws IOException **/
    public static int[] getMaxTopicWordDistribute(int maxTopicId,double[][] phi,int limit) throws IOException{
        double[] maxTopicWordDistri = phi[maxTopicId];
        //返回最大的那几个词的id
        return getMaxKInArrayIndex(maxTopicWordDistri,limit);
    }
    /**获得最大主题的索引id***/
    public static int getMaxTopicIndex(int[] document,double[][] phi){
    	return getMaxInArrayIndex(LdaGibbsSampler.inference(phi, document));
    }
    /**
     * @throws IOException **/
    public static int getMaxTopicIndex(List<String> words,double[][] phi,Vocabulary vocabulary) throws IOException{
    	
    	int[] document = Corpus.loadString(words,vocabulary);
    	return getMaxInArrayIndex(LdaGibbsSampler.inference(phi, document));
    }
    
    
}
