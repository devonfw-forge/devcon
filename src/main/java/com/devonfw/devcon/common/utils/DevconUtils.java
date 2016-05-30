package com.devonfw.devcon.common.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.data.DevconOption;

/**
 * TODO pparrado This type ...
 *
 * @author pparrado
 */
public class DevconUtils {

  Reflections reflections = new Reflections(ClasspathHelper.forPackage(Constants.MODULES_PACKAGE),
      new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

  public List<String> getAvailableModules() {

    List<String> modules = new ArrayList<String>();
    try {
      Set<Class<?>> annotatedClasses = this.reflections.getTypesAnnotatedWith(CmdModuleRegistry.class);

      Iterator<Class<?>> iterator = annotatedClasses.iterator();
      while (iterator.hasNext()) {
        Class<?> currentClass = iterator.next();
        Annotation annotation = currentClass.getAnnotation(CmdModuleRegistry.class);
        CmdModuleRegistry module = (CmdModuleRegistry) annotation;
        modules.add(module.name());
      }

      return modules;

    } catch (Exception e) {
      System.out.println("An error occurred trying to obtain the available modules. Message: " + e.getMessage());
      return modules;
    }
  }

  public List<DevconOption> getGlobalOptions() {

    List<DevconOption> globalOptions = new ArrayList<DevconOption>();

    try {
      ClassLoader classLoader = getClass().getClassLoader();
      URL globalParamsFileURL = classLoader.getResource(Constants.GLOBAL_PARAMS_FILE);

      if (globalParamsFileURL != null) {

        globalOptions = getGlobalOptionsFromFile(globalParamsFileURL);

      } else {
        // TODO implement logs
        System.out.println("Getting the default global options...");
        globalOptions = getDefaultGlobalOptions();
      }

      return globalOptions;
    } catch (Exception e) {
      System.out.println("ERROR: " + e.getMessage());
      return globalOptions;
    }

  }

  private List<DevconOption> getDefaultGlobalOptions() {

    List<DevconOption> defaultGlobalOptions = new ArrayList<DevconOption>();

    DevconOption h = new DevconOption("h", "help", "show help info for each module/command");
    DevconOption np = new DevconOption("np", "noprompt", "the process will not ask for user input");
    DevconOption v = new DevconOption("v", "version", "show devcon version");

    defaultGlobalOptions.add(h);
    defaultGlobalOptions.add(np);
    defaultGlobalOptions.add(v);

    return defaultGlobalOptions;
  }

  private List<DevconOption> getGlobalOptionsFromFile(URL fileURL) throws FileNotFoundException, IOException,
      ParseException {

    JSONParser parser = new JSONParser();
    List<DevconOption> globalOptions = new ArrayList<DevconOption>();

    String jsonPath = fileURL.getPath();
    Object obj = parser.parse(new FileReader(jsonPath));
    // ****************

    // InputStream in = DevconUtils.class.getResourceAsStream("/" + Constants.GLOBAL_PARAMS_FILE);
    // StringWriter writer = new StringWriter();
    // IOUtils.copy(in, writer, StandardCharsets.UTF_8);
    // String s = writer.toString();
    // System.out.println(s);
    // System.out.println(s.length());

    // URL url = DevconUtils.class.getResource("/" + Constants.GLOBAL_PARAMS_FILE);
    // Object obj = parser.parse(new FileReader(url.getPath()));

    // ***************
    // File f = new File(DevconUtils.class.getResource("/" + Constants.GLOBAL_PARAMS_FILE).toURI());
    // Object obj = parser.parse(new FileReader(f));
    // ***************

    // Object obj = null;
    // try {
    // obj = parser.parse(s);
    // } catch (Exception e) {
    // System.out.println(e.getMessage());
    // }

    // ------------------------------------------
    // WORKS WITH BOTH COMMAND LINE AND ECLIPSE
    // Object obj = parser.parse(new FileReader("C:\\Temp\\" + Constants.GLOBAL_PARAMS_FILE));
    // ------------------------------------------

    JSONArray json = (JSONArray) obj;

    Iterator<Object> it = json.iterator();

    while (it.hasNext()) {
      try {
        JSONObject j = (JSONObject) it.next();

        String opt = j.get("opt") != null ? j.get("opt").toString() : " ";
        String longOpt = j.get("longOpt") != null ? j.get("longOpt").toString() : " ";
        String description = j.get("description") != null ? j.get("description").toString() : " ";

        globalOptions.add(new DevconOption(opt, longOpt, description));

      } catch (Exception e) {
        // TODO implement logs
        System.out.println("Error reading a global option. Please check the global options file.");
      }

    }

    return globalOptions;
  }
}
