package com.devonfw.devcon.common.utils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.data.ProjectType;

/**
 * General utilities
 *
 * @author pparrado
 */
public class Utils {

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
  /*
   * public static void cloneRepository(String repoUrl, String cloneDir, String gitDir) throws Exception {
   *
   * try {
   *
   * if (gitDir == null || gitDir.isEmpty()) { gitDir = Utils.getGITBinPath(); } ProcessBuilder processBuilder = new
   * ProcessBuilder(gitDir + Constants.GIT_EXE, Constants.CLONE_OPTION, repoUrl, cloneDir); processBuilder.directory(new
   * File(gitDir));
   *
   * Process process = processBuilder.start();
   *
   * final InputStream isError = process.getErrorStream(); final InputStream isOutput = process.getInputStream();
   *
   * processErrorAndOutPut(isError, isOutput);
   *
   * // Wait to get exit value try { process.waitFor(); } catch (InterruptedException e) { throw e; } } catch (Exception
   * e) { throw e; } }
   */

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

  /**
   * @param pathToApp
   * @param type
   * @throws Exception
   */
  public static void addDevonJsonFile(Path pathToApp, ProjectType type) throws Exception {

    try {
      File appFolder = pathToApp.toFile();
      if (appFolder.exists()) {
        String content =
            "{\"version\": \"" + Devcon.DEVON_DEFAULT_VERSION + "\",\n\"type\":\"" + type.toString() + "\"}";
        File settingsfile = pathToApp.resolve("devon.json").toFile();
        FileUtils.writeStringToFile(settingsfile, content, "UTF-8");
      }
    } catch (Exception e) {
      throw new Exception(
          "An error occurred while adding the devon.json file. You may need to add it manually. " + e.getMessage());
    }

  }

}