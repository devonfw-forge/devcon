package com.devonfw.devcon.modules.oasp4j.migrate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.devonfw.devcon.common.api.data.FileBucket;
import com.devonfw.devcon.common.api.data.FileMessage;
import com.devonfw.devcon.output.Output;

/**
 * @author VAPADWAL
 *
 */
public class JavaCrawler {

  private List<FileBucket> listFileBucket = new ArrayList<>();

  private List<FileMessage> listFileMessages = new ArrayList<>();

  public void processFiles(File directory, final Output output) throws IOException, ParseException {

    JSONParser parser = new JSONParser();
    FileReader file = null;

    file = new FileReader(new File(this.getClass().getClassLoader()
        .getResource("javaprops_" + new PropertyFileReader().getLatetOasp4jVersion() + ".json").getFile()));
    Object obj = parser.parse(file);

    JSONObject jsonObject = (JSONObject) obj;
    System.out.println(jsonObject);

    JSONArray msg = (JSONArray) jsonObject.get("java");
    Iterator<JSONObject> iterator = msg.iterator();
    while (iterator.hasNext()) {
      JSONObject jsObject = iterator.next();
      {
        directoryCrawler(directory, (String) jsObject.get("find_String"), (String) jsObject.get("replace_String"));
      }
    }
    replaceFiles(output);
  }

  public void directoryCrawler(File directory, String searchWord, String replaceWord) throws IOException {

    Scanner inputConsole;
    File[] filesAndDirs = directory.listFiles();

    for (File file : filesAndDirs) {

      if (file.isFile()) {
        try {
          searchWord(file, searchWord, replaceWord);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        directoryCrawler(file, searchWord, replaceWord);
      }
    }
  }

  /**
   * @throws IOException
   */
  private void replaceFiles(final Output output) throws IOException {

    Scanner inputConsole;
    if (this.listFileBucket != null && !this.listFileBucket.isEmpty()) {
      output.showMessage("Please find the below java files that are replace " + "\n");

      for (FileMessage fileMsg : this.listFileMessages) {
        output.showMessage(fileMsg.getFilePath());
        output.showMessage(fileMsg.getFileMessage());
      }
      inputConsole = new Scanner(System.in);
      // System.out.println("Do you want to replace the above java files ? Enter(Y/N)");
      String confirm = "Y";// inputConsole.nextLine();//TODO need to check how to get user input from Devcon
      if (confirm.equalsIgnoreCase("Y")) {
        for (FileBucket fileBucket : this.listFileBucket) {
          replaceWord(fileBucket.getFile(), fileBucket.getSearch(), fileBucket.getReplace());

        }
      }
      inputConsole.close();
    }
  }

  private void searchWord(File file, String searchWord, String replaceWord) throws IOException {

    boolean isMatchFound = false;
    Scanner scanFile;
    FileMessage fileMessage = null;
    FileBucket fileBucket = new FileBucket();
    try {
      scanFile = new Scanner(file);
      while (null != scanFile.findWithinHorizon(searchWord, 0)) {
        MatchResult mr = scanFile.match();
        fileMessage = new FileMessage();
        fileMessage.setFilePath(file.getPath());
        fileMessage.setFileMessage("Word found : " + mr.group() + " at index " + mr.start() + " to " + mr.end());
        this.listFileMessages.add(fileMessage);
        isMatchFound = true;
      }
      if (isMatchFound) {
        fileBucket.setFile(file);
        fileBucket.setSearch(searchWord);
        fileBucket.setReplace(replaceWord);
        this.listFileBucket.add(fileBucket);
      }
      scanFile.close();
    } catch (FileNotFoundException e) {
      System.err.println("Search File Not Found !!!!! ");
      e.printStackTrace();
    }
  }

  private static void replaceWord(File file, String searchStr, String repStr) throws IOException

  {

    String line;
    BufferedReader br = new BufferedReader(new FileReader(file));
    final BufferedWriter writer = null;
    StringBuffer outputStrbuf = null;
    StringBuffer writerbuff = new StringBuffer();
    while ((line = br.readLine()) != null) {
      Pattern p = Pattern.compile(searchStr);
      Matcher matcher = p.matcher(line);
      boolean found = matcher.find();
      outputStrbuf = new StringBuffer();
      while (found) {
        matcher.appendReplacement(outputStrbuf, repStr);
        found = matcher.find();
      }
      matcher.appendTail(outputStrbuf);
      writerbuff.append(outputStrbuf.toString() + "\n");
    }
    FileWriter fw = new FileWriter(file);
    BufferedWriter bf = new BufferedWriter(fw);
    bf.write(writerbuff.toString());
    bf.close();
    fw.close();

  }
}
