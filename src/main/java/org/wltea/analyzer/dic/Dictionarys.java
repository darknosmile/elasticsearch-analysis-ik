package org.wltea.analyzer.dic;

import java.util.HashMap;

import org.wltea.analyzer.cfg.Configuration;

public class Dictionarys {

	public static HashMap<String,Dictionary> dictFactory = new HashMap<String,Dictionary>();
	
	public static synchronized Dictionary getDic(String dictName){
		Dictionary dic = dictFactory.get(dictName);
		if(null == dic){
		}
		return dic;
	}
	
	public static synchronized void putDic(String indexName,Dictionary dictionary){
		dictFactory.put(indexName, dictionary);
	}

	public static synchronized void initialDic(Configuration configuration) {
		if(null==getDic(configuration.getDicFileXml())){
			Dictionary dic = new Dictionary(configuration);
			dic.initial();
			dictFactory.put(configuration.getDicFileXml(), dic);
		}
		
	}

	public static Dictionary getSingleton() {
		return getSingleton("default");
	}

	public static Dictionary getSingleton(String dictionaryName) {
		if(null == dictionaryName || dictionaryName.length()==0){
			dictionaryName = "default";
		}
		return getDic(dictionaryName);
	}
	
	
	
}
