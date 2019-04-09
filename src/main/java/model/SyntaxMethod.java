package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import common.Word;
import common.MapSoredClass;

/**
 * 句子的句法
 * @author huwenhao
 * 待添加
 */

public class SyntaxMethod {

    public static void main(String[] args) {
		
	    Map<String,Integer> map = new HashMap<>();
	    map.put("a", 1);map.put("b", 16);map.put("c", 15);map.put("d", 10);
	    MapSoredClass<String> mapsored = new MapSoredClass<String>();
	    mapsored.map = map;
	    map = mapsored.getSortedDesc(true);
	    System.out.println(map);  
    }
	
    /**
     * 给定的词能否组成一句话,并把这些词做一些操作,使之成为一句话,
     * @param words
     * 即计算给定词构成一句话的概率
     */
    public static void judgeSentence(List<Word> words) {
		
    }

}
