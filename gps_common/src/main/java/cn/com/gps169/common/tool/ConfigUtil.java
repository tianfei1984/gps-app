package cn.com.gps169.common.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUtil {
	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(ConfigUtil.class);
	private static final String READER_CONFIG = "/ReaderConfig.xml";
	private static final String CONFIG_PATH = "configpath";
	private static final String FILE_SEPERATOR = System
			.getProperty("file.separator");
	private static final String ENV_PROPERTIES = "env.properties";

	private static IConfigReader configReader = null;

	public enum ConfigType {
		filesystem, classpath, zookeeper, url
	}

	public static IConfigReader getConfigReader() {
		if (configReader == null) {
			setConfigReader();
		}

		return configReader;
	}

	public static void setConfigReader() {
		String readertype = null;
		String basepath = null;
		URL url = ConfigUtil.class.getResource(READER_CONFIG);
		InputStream stream = ConfigUtil.class
				.getResourceAsStream(READER_CONFIG);
		if (stream == null || url.toString().contains(".jar!")) {
			LOGGER.info("config reader type is set to default classpath loading way.");
		} else {
			SAXReader saxReader = new SAXReader();
			Document document = null;
			try {
				document = saxReader.read(stream);
			} catch (DocumentException e) {
				LOGGER.error("error while reading conifg file." + e);
			}

			Element root = document.getRootElement();
			Iterator<?> iter = root.elementIterator("readerconfig");
			Element data = (Element) iter.next();
			readertype = data.elementText("readertype").trim().toLowerCase();
			basepath = data.elementText("basepath").trim().toLowerCase();
			initialConfig(readertype, basepath);
		}
	}

	public static void initialConfig(String readertype, String basepath) {
		ConfigType configType = ConfigType.valueOf(readertype);
		LOGGER.info("config reader type is set to  :" + readertype);
		switch (configType) {
		case filesystem:
			configReader = new FileSystemConfigReaderImpl(basepath);
			break;
		}
	}

	public static URL getFileURL(String filePath) {
		return getFileURL(new File(filePath));
	}

	public static URL getFileURL(File file) {
		try {
			if (file == null) {
				return null;
			}

			if (!file.exists()) {
				LOGGER.warn(String.format("%s 不存在", file.getAbsolutePath()));
				return null;
			}

			return file.toURI().toURL();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return null;
		}
	}

	public static URL getResourceURL(String fileName) {
		IConfigReader reader = getConfigReader();
		if (reader instanceof FileSystemConfigReaderImpl) {
			String basePath = ((FileSystemConfigReaderImpl) reader)
					.getBasePath();
			basePath += "/" + fileName;

			return getFileURL(basePath);
		} else {
			return null;
		}
	}

	public static String getConfigPath() {
		return System.getProperty(CONFIG_PATH) == null ? "" : System
				.getProperty(CONFIG_PATH);
	}

	public static Properties getEnvProperties() {
		Properties props = new Properties();
		File envFile = new File(getConfigPath() + FILE_SEPERATOR
				+ ENV_PROPERTIES);
		if (!envFile.exists()) {
			LOGGER.error("config path is not set or env.properties doesn't exist");
		} else {
			InputStream inputStream;
			try {
				inputStream = new FileInputStream(envFile);
				props.load(inputStream);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("config path is not set or env.properties doesn't exist");
			}
		}

		return props;
	}
}
