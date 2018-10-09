/*******************************************************************************
 * Copyright 2015-2018 Capgemini SE.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package com.devonfw.devcon.common.utils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.input.ShowCommandHandler;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * General utilities
 *
 * @author pparrado
 */
public class Utils {

  private static final String DEVON_JSON = "devon.json";

  private static final String OPTIONAL = "optionalParameters";

  private static final String DIST_SCRIPTS = "software/devcon/scripts";

  private static final String LOCAL_SCRIPTS = ".devcon/scripts";

  public static File getApplicationPath() {

    try {
      return new File(Devcon.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return null;
    }
  }

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
   * @param isError
   * @param isOutput
   */
  @SuppressWarnings("javadoc")
  public static Thread processErrorAndOutPut(final InputStream isError, final InputStream isOutput,
      final Output output) {

    final InputStreamReader isErrReader = new InputStreamReader(isError);
    final InputStreamReader isOutReader = new InputStreamReader(isOutput);

    Thread newThread = new Thread(new Runnable() {
      @Override
      public void run() {

        BufferedReader bre = new BufferedReader(isOutReader);
        String line;
        try {
          while ((line = bre.readLine()) != null) {

            output.showMessage(line);
          }

        } catch (Exception e) {
        }
      }
    });
    newThread.start();
    return newThread;

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
        String content = "{\"version\": \"" + Devcon.DEVON_DEFAULT_VERSION + "\",\n\"type\":\"" + type.toString()
            + "\"}";
        File settingsfile = pathToApp.resolve("devon.json").toFile();
        FileUtils.writeStringToFile(settingsfile, content, "UTF-8");
      }
    } catch (Exception e) {
      throw new Exception(
          "An error occurred while adding the devon.json file. You may need to add it manually. " + e.getMessage());
    }

  }

  public static void addDevonJsonFile(Path pathToApp, String serverPath, String clientPath) throws Exception {

    try {
      File appFolder = pathToApp.toFile();
      if (appFolder.exists()) {
        String content = "{\"version\": \"" + Devcon.DEVON_DEFAULT_VERSION
            + "\",\n\"type\":\"COMBINED\",\n\"projects\":[\"" + serverPath + "\", \"" + clientPath + "\"]\n}";
        File settingsfile = pathToApp.resolve("devon.json").toFile();
        FileUtils.writeStringToFile(settingsfile, content, "UTF-8");
      }
    } catch (Exception e) {
      throw new Exception(
          "An error occurred while adding the devon.json file. You may need to add it manually. " + e.getMessage());
    }

  }

  public static void setProxy(final String host, final String port) {

    ProxySelector.setDefault(new ProxySelector() {
      final ProxySelector delegate = ProxySelector.getDefault();

      @Override
      public List<Proxy> select(URI uri) {

        return Arrays.asList(new Proxy(Type.HTTP, InetSocketAddress.createUnresolved(host, Integer.parseInt(port))));

      }

      @Override
      public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {

        if (uri == null || sa == null || ioe == null) {
          throw new IllegalArgumentException("Arguments can't be null.");
        }
      }
    });
  }

  /**
   * @param commands
   * @return
   */
  public Collection<Command> sortCommands(Collection<Command> commands) {

    List<Command> lst = new ArrayList<>(commands);
    Collections.sort(lst);
    return lst;
  }

  public Collection<Command> sortCommands(Collection<Command> commands, Comparator<Command> cmdComparator) {

    List<Command> cmdSortValList = new ArrayList<>();
    List<Command> cmdUnOrderList = new ArrayList<>();
    List<Command> finalCmdList = new ArrayList<>();
    for (Command cmd : commands) {
      if (cmd.getSortValue() >= 0) {
        cmdSortValList.add(cmd);
      } else {
        cmdUnOrderList.add(cmd);
      }

    }
    Collections.sort(cmdSortValList, cmdComparator);
    Collections.sort(cmdUnOrderList);
    finalCmdList.addAll(cmdSortValList);
    finalCmdList.addAll(cmdUnOrderList);

    return finalCmdList;
  }

  /**
   * @param modules
   * @return
   */
  public List<CommandModuleInfo> sortModules(List<CommandModuleInfo> modules) {

    Collections.sort(modules);
    return modules;
  }

  public List<CommandModuleInfo> sortModules(List<CommandModuleInfo> modules,
      Comparator<CommandModuleInfo> moduleComparator) {

    List<CommandModuleInfo> moduleSortValList = new ArrayList<>();
    List<CommandModuleInfo> moduleUnOrderList = new ArrayList<>();
    List<CommandModuleInfo> finalModuleList = new ArrayList<>();
    List<CommandModuleInfo> finalOrderedModuleList = new ArrayList<>();
    for (CommandModuleInfo module : modules) {
      if (module.getSortValue() >= 0) {
        moduleSortValList.add(module);
      } else {
        moduleUnOrderList.add(module);
      }

    }
    Collections.sort(moduleSortValList, moduleComparator);
    Collections.sort(moduleUnOrderList);
    finalModuleList.addAll(moduleSortValList);
    finalModuleList.addAll(moduleUnOrderList);

    // to put 'system' module as first menu
    for (CommandModuleInfo finalModule : finalModuleList) {
      if (finalModule.getName().equals("system")) {
        finalOrderedModuleList.add(0, finalModule);
      } else {
        finalOrderedModuleList.add(finalModule);
      }
    }

    return finalOrderedModuleList;
  }

  /**
   * Returns the path where the custom JS modules should be searched
   *
   * @return the JS modules path
   */
  public static Path getScriptDir() {

    Optional<DistributionInfo> distInfo = ContextPathInfo.INSTANCE.getDistributionRoot();

    return distInfo.isPresent() ? distInfo.get().getPath().resolve(DIST_SCRIPTS)
        : ContextPathInfo.INSTANCE.getHomeDirectory().resolve(LOCAL_SCRIPTS);

  }

  public static void processOutput(final InputStream isError, final InputStream isOutput, final Output output) {

    Thread processorThread = processErrorAndOutPut(isError, isOutput, output);
    while (!processorThread.getState().equals(Thread.State.TERMINATED)) {

    }
    ShowCommandHandler.start.setDisable(false);
  }

  /**
   * Checks if the passed path ends with slash. If not, it's added
   *
   * @return the passed path with trailing slash
   */
  public static String addTrailingSlash(String path) {

    if (path == null)
      return path;
    else
      return path.endsWith(File.separator) ? path : path + File.separator;
  }

  /**
   * Remove the ending dot (if exists) from the passed path
   *
   * @return the passed path without ending dot
   */
  public static String removeEndingDot(String path) {

    if (path == null)
      return path;
    else
      return path.endsWith(".") ? path.substring(0, path.length() - 1) : path;
  }

  /**
   * Method to obtain the value of any JSON property file
   *
   * @param filePath Properties file path
   * @param property name of the property
   * @return the value of the passed property
   */
  public static Optional<String> getJSONConfigProperty(String filePath, String property) {

    try {

      File f = new File(filePath);
      if (f.exists()) {
        try (InputStream is = new FileInputStream(f)) {
          String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
          JSONObject json = new JSONObject(jsonTxt);
          String propertyValue = (String) json.get(property);
          return Optional.of(propertyValue);
        } catch (IOException ioex) {
          ioex.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.absent();
  }

  /**
   * Gets the template version. Firstly, tries to get it from the passed config file path. If not found, tries to get it
   * from Internet (the devonfw.github.io repository). Else, raise error to end-users.
   *
   * @param configPath Path where the config path is located on disk
   * @return The template version or empty string if not found
   */
  public static String getTemplateVersion(String configPath) {

    String devonTemplateVersion = "";
    Optional<String> devonTemplateVersionOp = Utils.getJSONConfigProperty(configPath, Constants.DEVON_TEMPLATE_VERSION);
    if (devonTemplateVersionOp.isPresent()) {
      devonTemplateVersion = devonTemplateVersionOp.get();
    } else {
      devonTemplateVersionOp = Downloader.getDevconConfigProperty(Constants.DEVON_TEMPLATE_VERSION);
      if (devonTemplateVersionOp.isPresent()) {
        devonTemplateVersion = devonTemplateVersionOp.get();
      }
    }
    return devonTemplateVersion;
  }

}
