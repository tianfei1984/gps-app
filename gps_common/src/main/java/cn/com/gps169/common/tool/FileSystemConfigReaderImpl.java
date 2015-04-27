package cn.com.gps169.common.tool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.input.ReaderInputStream;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemConfigReaderImpl implements IConfigReader {
	private static transient final Logger LOGGER = LoggerFactory
			.getLogger(FileSystemConfigReaderImpl.class);
	public static final String FILE_SEPERATOR = System
			.getProperty("file.separator");
	private String basePath;
	private static Map<String, String> vars = new HashMap<String, String>();

	@SuppressWarnings("rawtypes")
	FileSystemConfigReaderImpl(String basepath) {
		int i = basepath.indexOf("%");
		int j = basepath.indexOf("%", i + 1);
		if (j < 0) {
			this.basePath = basepath;
			LOGGER.warn("config path is set to a absolute path : "
					+ this.basePath
					+ ". please set the configpath arg when start the tomcat server");
		} else {
			String querystring = basepath.substring(i + 1, j);

			String reg = basepath.substring(i, j + 1);
			LOGGER.info("config path is set as a relative path. trying to get the path from tomcat starup parameter... ");

			String replacestring = System.getProperty(querystring);
			if (replacestring == null) {
				LOGGER.error(" could not get the configpath parameter : "
						+ querystring
						+ " it is not set as a tomcat startup parameter,please check it ");
			} else {
				String prefix = basepath.substring(0, i);
				String postfix = basepath.substring(j + 1);
				this.basePath = prefix + replacestring + postfix;
				LOGGER.info("substitute " + reg + " to " + replacestring);
				LOGGER.info("set config base path to: " + this.basePath);
			}
		}

		Properties props = ConfigUtil.getEnvProperties();
		for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = props.getProperty(key);
			vars.put(key, value);
		}
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}

	public String[] getPropertyFiles() {
		try {
			File file = new File(this.basePath);
			return file.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".properties");
				}
			});
		} catch (Exception e) {
			LOGGER.warn(String.format("读取配置文件失败: path = %s, msg = %s",
					this.basePath, e.getMessage()));
			return null;
		}
	}

	public InputStream getResourceAsStream(String filePath)
			throws FileNotFoundException {
		LOGGER.info("read config file " + filePath + " from " + this.basePath);
		FileReader fileReader = new FileReader(new File(this.basePath
				+ FILE_SEPERATOR + filePath));
		String encoding = fileReader.getEncoding();
		InterpolationFilterReader reader = new InterpolationFilterReader(
				fileReader, vars);
		ReaderInputStream inputStream = new ReaderInputStream(reader, encoding);
		LOGGER.info("read config file " + filePath + " complete.");
		return inputStream;
	}

	public Properties getResourceAsProperties(String filePath)
			throws IOException {
		Properties props = new Properties();
		InputStream in = getResourceAsStream(filePath);
		if (in == null) {
			return null;
		}

		props.load(in);
		in.close();
		return props;
	}

	public String getBasePath() {
		return this.basePath;
	}
}
