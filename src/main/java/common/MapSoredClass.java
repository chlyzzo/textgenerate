package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author huwenhao
 *
 * @param <T>
 * 
 * Map的排序器
 */
public class MapSoredClass<T> {
    
  public Map<T, Integer> map = new HashMap<T, Integer>();
	 
  public void addObject(T key, Integer oj) {
	map.put(key, oj);
  }

  /**
   * 降序排序
   * @param flag
   * @return
   */
  public Map<T,Integer> getSortedDesc(boolean flag) {		 
	if (map == null || map.isEmpty()) {
	  return null;
    }
	Map<T,Integer> sortedMap = new LinkedHashMap<>();
	List<Map.Entry<T, Integer>> entryList = new ArrayList<Map.Entry<T, Integer>>(map.entrySet());
	if (flag) {
	  Collections.sort(entryList, new MapValueDescComparator());
	} else {
	  Collections.sort(entryList, new MapValueAscComparator());
	}
	Iterator<Map.Entry<T, Integer>> iter = entryList.iterator();
	Map.Entry<T, Integer> tmpEntry = null;
	while (iter.hasNext()) {
	  tmpEntry = iter.next();
	  sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
	}
	return sortedMap;
  }

  /**
   * map按照value排序,然后只取key
   * @param flag
   * @return
   * true是降序，false是升序
   */
  public List<T> sortDescByValueReturnKeys(boolean flag) {	
    if (map == null || map.isEmpty()) {
	  return null;
	}
	List<T> sortedMap = new LinkedList<>();
	List<Map.Entry<T, Integer>> entryList = new ArrayList<Map.Entry<T, Integer>>(map.entrySet());
	if (flag) {
	  Collections.sort(entryList, new MapValueDescComparator());
	} else {
	  Collections.sort(entryList, new MapValueAscComparator());
	}
	Iterator<Map.Entry<T, Integer>> iter = entryList.iterator();
	Map.Entry<T, Integer> tmpEntry = null;
	while (iter.hasNext()) {
	  tmpEntry = iter.next();
	  sortedMap.add(tmpEntry.getKey());
	}
	return sortedMap;
  }

/***比较器类;降序***/
class MapValueDescComparator implements Comparator<Map.Entry<T, Integer>> {
  @Override
  public int compare(Entry<T, Integer> me1, Entry<T, Integer> me2) {
    return me2.getValue().compareTo(me1.getValue());
  }
}


/***比较器类;升序***/
class MapValueAscComparator implements Comparator<Map.Entry<T, Integer>> {
  @Override
  public int compare(Entry<T, Integer> me1, Entry<T, Integer> me2) {
    return me1.getValue().compareTo(me2.getValue());
  }
}

}