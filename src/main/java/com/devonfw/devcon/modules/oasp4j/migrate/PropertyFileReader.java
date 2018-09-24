package com.devonfw.devcon.modules.oasp4j.migrate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author VAPADWAL
 *
 */
public class PropertyFileReader {
  public String getLatetOasp4jVersion() throws IOException, FileNotFoundException {

    Properties prop = new Properties();
    InputStream inputStream;
    inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");
    if (inputStream != null) {
      prop.load(inputStream);
    } else {
      throw new FileNotFoundException("property file application.properties not found in the classpath");
    }
    String version = prop.getProperty("latest.oasp4j");
    return version;
  }
}
