package com.devonfw.devcon.common.utils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.devonfw.devcon.common.api.data.DevconOption;

/**
 * General utilities
 *
 * @author pparrado
 */
public class Utils {

  Reflections reflections = new Reflections(ClasspathHelper.forPackage(Constants.MODULES_PACKAGE),
      new SubTypesScanner(), new TypeAnnotationsScanner(), new MethodAnnotationsScanner());

  private static final String DEVON_JSON = "devon.json";

  private static final String OPTIONAL = "optionalParameters";

  public static <T, U> Pair<List<T>, List<U>> unzipList(List<Pair<T, U>> lst) {

    List<T> left = new ArrayList<>();
    List<U> right = new ArrayList<>();
    for (Pair<T, U> pair : lst) {
      left.add(pair.getLeft());
      right.add(pair.getRight());
    }
    return Pair.of(left, right);
  }

  /**
   * This method is used to encode special characters in a given string.
   *
   * @param str String to be encoded
   * @return pass encoded String.
   * @throws UnsupportedEncodingException
   */
  public static String encode(String str) throws UnsupportedEncodingException {

    String pass;

    pass = URLEncoder.encode(str, "UTF-8");
    return pass;
  }

  /**
   * This method is used to decode encoded string.
   *
   * @param str String to be decoded
   * @return pass decoded String.
   * @throws UnsupportedEncodingException
   */
  public static String decode(String str) throws UnsupportedEncodingException {

    String url;

    url = URLDecoder.decode(str, "UTF-8");
    return url;
  }

  public static <T, U> List<Pair<T, U>> zipLists(List<T> left, List<U> right) {

    List<Pair<T, U>> lst = new ArrayList<>();
    for (int i = 0; i < left.size(); i++) {
      lst.add(Pair.of(left.get(i), right.get(i)));
    }
    return lst;
  }

  /**
   * @param list List of Pairs
   * @return Map
   */
  public static <T, U> Map<T, U> pairsToMap(List<Pair<T, U>> list) {

    LinkedHashMap<T, U> map = new LinkedHashMap<>();
    for (Pair<T, U> pair : list) {

      map.put(pair.getLeft(), pair.getRight());
    }
    return map;
  }

  /**
   * @param list List of Pairs
   * @return Map
   */
  public static <T, U> List<Pair<T, U>> mapToPairs(Map<T, U> map) {

    ArrayList<Pair<T, U>> list = new ArrayList<>();
    for (T key : map.keySet()) {

      list.add(Pair.of(key, map.get(key)));
    }
    return list;
  }

  public static List<DevconOption> getGlobalOptions() {

    List<DevconOption> globalOptions = new ArrayList<DevconOption>();

    try {

      URL globalParamsFileURL = Utils.class.getClassLoader().getResource(Constants.GLOBAL_PARAMS_FILE);

      if (globalParamsFileURL != null) {

        globalOptions = getGlobalOptionsFromFile(globalParamsFileURL);

      } else {
        // TODO implement logs
        // System.out.println("Getting the default global options...");
        globalOptions = getDefaultGlobalOptions();
      }

      return globalOptions;
    } catch (Exception e) {
      System.out.println("ERROR: " + e.getMessage());
      return globalOptions;
    }

  }

  private static List<DevconOption> getDefaultGlobalOptions() {

    List<DevconOption> defaultGlobalOptions = new ArrayList<DevconOption>();

    DevconOption h = new DevconOption("h", "help", "show help info for each module/command");
    DevconOption v = new DevconOption("v", "version", "show devcon version");

    defaultGlobalOptions.add(h);
    defaultGlobalOptions.add(v);

    return defaultGlobalOptions;
  }

  private static List<DevconOption> getGlobalOptionsFromFile(URL fileURL)
      throws FileNotFoundException, IOException, ParseException {

    JSONParser parser = new JSONParser();
    List<DevconOption> globalOptions = new ArrayList<>();

    String jsonPath = fileURL.getPath();
    Object obj = parser.parse(new FileReader(jsonPath));
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

  /**
   *
   * @param url Web page to open in Desktop browser
   * @return indication whether on Desktop i.e. whether web browser could be accessed
   *
   */
  public static boolean openUri(String url) {

    if (Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().browse(new URI(url));
        return true;
      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * @return GIT Bin Path
   */
  public static String getGITBinPath() {

    String gitBinPath = null;
    try {
      String path = System.getenv("PATH");
      String tokens[] = path.split(";");
      for (String token : tokens) {
        if (token.endsWith("Git\\cmd") || token.endsWith("Git\\bin")) {
          gitBinPath = token;
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return gitBinPath;
  }

  /**
   * @param repoUrl
   * @param cloneDir
   * @param gitDir
   * @throws Exception
   */
  public static void cloneRepository(String repoUrl, String cloneDir, String gitDir) throws Exception {

    try {

      if (gitDir == null || gitDir.isEmpty()) {
        gitDir = Utils.getGITBinPath();
      }
      ProcessBuilder processBuilder =
          new ProcessBuilder(gitDir + Constants.GIT_EXE, Constants.CLONE_OPTION, repoUrl, cloneDir);
      processBuilder.directory(new File(gitDir));

      Process process = processBuilder.start();

      final InputStream isError = process.getErrorStream();
      final InputStream isOutput = process.getInputStream();

      processErrorAndOutPut(isError, isOutput);

      // Wait to get exit value
      try {
        process.waitFor();
      } catch (InterruptedException e) {
        throw e;
      }
    } catch (Exception e) {
      throw e;
    }
  }

  /**
   * @param isError
   * @param isOutput
   */
  @SuppressWarnings("javadoc")
  public static void processErrorAndOutPut(final InputStream isError, final InputStream isOutput) {

    final InputStreamReader isErrReader = new InputStreamReader(isError);
    final InputStreamReader isOutReader = new InputStreamReader(isOutput);

    // Thread to process error
    new Thread(new Runnable() {
      @Override
      public void run() {

        BufferedReader bre = new BufferedReader(isErrReader);
        String line;
        try {
          while ((line = bre.readLine()) != null) {
            System.out.println(line);
          }
        } catch (Exception e) {
        }
      }
    }).start();

    // Thread to process output
    new Thread(new Runnable() {
      @Override
      public void run() {

        BufferedReader bre = new BufferedReader(isOutReader);
        String line;
        try {
          while ((line = bre.readLine()) != null) {
            System.out.println("OUTPUT:" + line);
          }
        } catch (Exception e) {
        }
      }
    }).start();
  }
}