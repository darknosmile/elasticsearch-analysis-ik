package org.elasticsearch.indices.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.analysis.AnalyzerScope;
import org.elasticsearch.index.analysis.PreBuiltAnalyzerProviderFactory;
import org.elasticsearch.index.analysis.PreBuiltTokenizerFactoryFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.plugin.analysis.ik.AnalysisIkPlugin;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.wltea.analyzer.lucene.IKTokenizer;

/**
 * Registers indices level analysis components so, if not explicitly configured,
 * will be shared among all indices.
 */
public class IKIndicesAnalysis extends AbstractComponent {
	
	public static ESLogger logger = Loggers.getLogger("ik-analyzer");

    private boolean useSmart=false;
    
    private String ikPropFile = "IKAnalyzer.properties";

    @Inject
    public IKIndicesAnalysis(final Settings settings,
                                   IndicesAnalysisService indicesAnalysisService,Environment env) {
        super(settings);
        //增加配置读取,通过配置文件增加Analyzer和Dic字典的绑定
        InputStream inStream;
		try {
			//Path conf_dir = env.configFile().resolve(AnalysisIkPlugin.PLUGIN_NAME);
			Path conf_dir = PathUtils.get(
					new File(AnalysisIkPlugin.class.getProtectionDomain()
							.getCodeSource().getLocation().getPath()).getParent(),
					"config").toAbsolutePath();
			Path ikCfg = conf_dir.resolve(ikPropFile);
			inStream = new FileInputStream(ikCfg.toFile());
			Properties prop = new Properties();  
			prop.load(inStream);
			//List<Configuration> cfgs = new ArrayList<Configuration>(); 
			for (final String key : prop.stringPropertyNames()) {  
		        String ikFile = prop.getProperty(key);
		        final Configuration configuration=new Configuration(env,settings,ikFile);
		        if(key.contains("smart")){
		        	configuration.setUseSmart(true);
		        }
		        //注册analyzer
		        indicesAnalysisService.analyzerProviderFactories().put(key,
		                new PreBuiltAnalyzerProviderFactory(key, AnalyzerScope.GLOBAL,
		                        new IKAnalyzer(configuration)));
		        //注册token
		        indicesAnalysisService.tokenizerFactories().put(key,
		                new PreBuiltTokenizerFactoryFactory(new TokenizerFactory() {
		                    @Override
		                    public String name() {
		                        return key;
		                    }

		                    @Override
		                    public Tokenizer create() {
		                        return new IKTokenizer(configuration);
		                    }
		                }));
		        
		    }  
		} catch (Exception ex) {
			logger.error("IKAnalyzer init error:",ex);
		}  
        
    }
}
