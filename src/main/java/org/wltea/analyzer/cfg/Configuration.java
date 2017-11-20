/**
 * 
 */
package org.wltea.analyzer.cfg;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.plugin.analysis.ik.AnalysisIkPlugin;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.dic.Dictionarys;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
public class Configuration {


	private Environment environment;
	private Settings settings;

	// 是否启用智能分词
	private boolean useSmart;

	// 是否启用远程词典加载
	private boolean enableRemoteDict = false;

	// 是否启用小写处理
	private boolean enableLowercase = true;

	private String dicFileXml = "IKAnalyzer.cfg.xml";

	@Inject
	public Configuration(Environment env, Settings settings) {
		init(env,settings,dicFileXml);
	}

	@Inject
	public Configuration(Environment env, Settings settings,
			String dicFileXml) {
		init(env,settings,dicFileXml);
	}

	private void init(Environment env, Settings settings, String dicFileXml) {
		this.environment = env;
		this.settings = settings;
		this.dicFileXml = dicFileXml;

		this.useSmart = settings.get("use_smart", "false").equals("true");
		this.enableLowercase = settings.get("enable_lowercase", "true").equals(
				"true");
		this.enableRemoteDict = settings.get("enable_remote_dict", "true")
				.equals("true");
		
		
		// 修改为DictionaryFactory初始化
		// Dictionary.initial(this);
		Dictionarys.initialDic(this);
		
	}
	
	public Path getConfigInPluginDir() {
		return PathUtils.get(
				new File(AnalysisIkPlugin.class.getProtectionDomain()
						.getCodeSource().getLocation().getPath()).getParent(),
				"config").toAbsolutePath();
	}

	public boolean isUseSmart() {
		return useSmart;
	}

	public Configuration setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
		return this;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public Settings getSettings() {
		return settings;
	}

	public boolean isEnableRemoteDict() {
		return enableRemoteDict;
	}

	public boolean isEnableLowercase() {
		return enableLowercase;
	}

	public String getDicFileXml() {
		return dicFileXml;
	}

	public void setDicFileXml(String dicFileXml) {
		this.dicFileXml = dicFileXml;
	}

}
