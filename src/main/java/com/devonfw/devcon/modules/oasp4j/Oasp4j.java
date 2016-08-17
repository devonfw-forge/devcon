package com.devonfw.devcon.modules.oasp4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.SystemUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

/**
 * This class implements a Command Module with Oasp4j(server project) related commands
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "oasp4j", description = "Oasp4j(server project) related commands")
public class Oasp4j extends AbstractCommandModule {

  /**
   * The constructor.
   */
  public Oasp4j() {

    super();
  }

  /**
   * @param serverpath Path to Server Project
   * @param servername Name of Server Project
   * @param packagename Package Name of Server Project
   * @param groupid Group Id of the Server Project
   * @param version Version of the Server Project
   * @throws IOException
   */
  @Command(name = "create", description = "This command is used to create new server project")
  @Parameters(values = { @Parameter(name = "serverpath", description = "where to create", optional = true),
  @Parameter(name = "servername", description = "Name of project", optional = true),
  @Parameter(name = "packagename", description = "package name in server project"),
  @Parameter(name = "groupid", description = "groupid for server project"),
  @Parameter(name = "version", description = "version of server project") })
  public void create(String serverpath, String servername, String packagename, String groupid, String version)
      throws IOException {

    String command = new StringBuffer("cmd /c mvn -DarchetypeVersion=").append(Constants.OASP_TEMPLATE_VERSION)
        .append(" -DarchetypeGroupId=").append(Constants.OASP_TEMPLATE_GROUP_ID).append(" -DarchetypeArtifactId=")
        .append(Constants.OASP_TEMPLATE_GROUP_ID).append(" -DarchetypeArtifactId=").append(Constants.OASP_ARTIFACT_ID)
        .append(" archetype:generate -DgroupId=").append(groupid).append(" -DartifactId=").append(servername)
        .append(" -Dversion=").append(version).append(" -Dpackage=").append(packagename)
        .append(" -DinteractiveMode=false").toString();

    if (!SystemUtils.IS_OS_WINDOWS) {
      getOutput().showMessage("This task is currently only supported on Windows");
      return;
    }

    serverpath = serverpath.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString() : serverpath;

    System.out.println("serverpath is " + serverpath);

    File projectDir = new File(serverpath);

    if (!projectDir.exists()) {
      projectDir.mkdirs();
    }
    File project = new File(serverpath + File.separator + servername);

    if (!project.exists()) {

      Runtime rt = Runtime.getRuntime();
      Process process = null;

      try {
        process = rt.exec(command, null, new File(serverpath));

        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = in.readLine()) != null) {
          System.out.println(line);
        }
        in.close();

        int result = process.waitFor();
        if (result == 0) {
          getOutput().showMessage("Adding devon.json file...");
          Utils.addDevonJsonFile(project.toPath(), ProjectType.OASP4J);

          if (Integer.parseInt(Constants.OASP_TEMPLATE_VERSION.replaceAll("\\.", "")) <= new Integer("211")) {
            modifyPom(serverpath + "\\" + servername + "\\server\\pom.xml", packagename);
          }

          getOutput().showMessage("Project Creation completed successfully");

        } else {
          throw new Exception("Project creation failed");
        }

      } catch (Exception e) {
        e.printStackTrace();
        getOutput().showError("Errr creating workspace: " + e.getMessage());
      }

    } else {
      getOutput().showError("The project " + project.toString() + " already exists!");
    }

  }

  /**
   * Run OASP4j Project from the command line ContextType Project makes this into a "special" command which gets an
   * extra parameter '-path' allowing to specify the project root (in reality, any directory below the root is valid as
   * well) Alternatively, the current dir is used. When the file devon.json is found at the project root, it is
   * available as type ProjectInfo in the field propertyInfo. Apart from "version" and "type" (default properties) *ANY*
   * property can be specified
   *
   * @param port Server will be started at this port
   */
  @Command(name = "run", description = "runs application from embedded tomcat", context = ContextType.PROJECT)
  @Parameters(values = {
  @Parameter(name = "port", description = "Port to start Spring boot app (port 8081 by default)", optional = true) })
  public void run(String port) {

    if (!this.projectInfo.isPresent()) {
      getOutput().showError("Not in a project or -path param not pointing to a project");
      return;
    }

    ProjectInfo info = this.projectInfo.get();
    System.out.println("path before modification " + info.getPath().toString());

    // Get port from a) parameter or b) devon.json file or c) default value passed as 2nd paranter to info.getProperty
    String port_ = (port.isEmpty()) ? info.getProperty("port", "8081").toString() : port.trim();
    String path_ = info.getPath().toString() + "\\server";

    try {
      String commandStr = "mvn spring-boot:run -Drun.jvmArguments='-Dserver.port=" + port_ + "'";
      System.out.println("Command generated ---------- " + commandStr);
      String cmd = "cmd /c start " + commandStr;

      Process p = Runtime.getRuntime().exec(cmd, null, new File(path_));
      // String line;
      // BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
      // while ((line = in.readLine()) != null) {
      // System.out.println(line);
      // }
      // in.close();
      int result = p.waitFor();
      if (result == 0) {
        getOutput().showMessage("Application started");
      } else {
        getOutput().showError("Application failed to start");
      }
    } catch (Exception e) {

      getOutput().showError("An error occured during executing oasp4j Cmd: %s", e.getMessage());
    }
  }

  /**
   * @param path path to server project
   */
  @Command(name = "build", description = "This command will build the server project", context = ContextType.PROJECT)
  public void build() {

    // Check projectInfo loaded. If not, abort
    if (!this.projectInfo.isPresent()) {
      getOutput().showError("Not in a project or -path param not pointing to a project");
      return;
    }

    Process p;
    try {
      String cmd = "cmd /c mvn clean install";

      p = Runtime.getRuntime().exec(cmd, null, this.projectInfo.get().getPath().toFile());

      String line;
      BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
      while ((line = in.readLine()) != null) {
        System.out.println(line);
      }
      in.close();
      p.waitFor();
      getOutput().showMessage("Completed");
    } catch (Exception e) {
      getOutput().showError("An error occured during executing oasp4j Cmd" + e.getMessage());
    }
  }

  /**
   * @param tomcatpath Path to tomcat
   * @param path server project path
   */
  @Command(name = "deploy", description = "This command will deploy the server project on tomcat", context = ContextType.PROJECT)
  @Parameters(values = {
  @Parameter(name = "tomcatpath", description = "Path to tomcat folder (if not provided and the project is in a Devonfw distribution the default software/tomcat folder will be used)", optional = true) })
  public void deploy(String tomcatpath) {

    String path;
    try {

      // this.projectInfo = getContextPathInfo().getProjectRoot(path);

      Optional<DistributionInfo> distInfo = this.contextPathInfo.getDistributionRoot();

      if (!distInfo.isPresent()) {
        getOutput().showError("Not in a Devon distribution");
        return;
      }

      if (!this.projectInfo.isPresent()) {
        getOutput().showError("Not in a project or -path param not pointing to a project");
        return;
      }

      path = this.projectInfo.get().getPath().toString();

      Optional<String> appName = getAppName(path);

      if (appName.isPresent()) {

        tomcatpath = tomcatpath.isEmpty()
            ? distInfo.get().getPath().toFile().toString() + File.separator + "software" + File.separator + "tomcat"
            : tomcatpath;

        File tomcatDir = new File(tomcatpath);

        if (!tomcatDir.exists()) {
          getOutput().showError("Tomcat directory " + tomcatDir.toString() + " not found.");
          return;

        }

        File newTomcat4app = new File(tomcatpath + "_" + appName.get().toString());

        if (!newTomcat4app.exists()) {
          newTomcat4app.mkdirs();
          FileUtils.copyDirectory(tomcatDir, newTomcat4app);
        } else {
          getOutput().showMessage("Tomcat " + newTomcat4app.getAbsolutePath().toString() + " already exists.");
        }

        File project = new File(path);

        if (project.exists()) {

          // PACKAGING THE APP (creating the .war file)
          File mvnBat =
              new File(distInfo.get().getPath().toString() + File.separator + "software\\maven\\bin\\mvn.bat");
          if (mvnBat.exists()) {

            ProcessBuilder processBuilder = new ProcessBuilder(mvnBat.getAbsolutePath(), "package");
            processBuilder.directory(project);

            Process process = processBuilder.start();

            final InputStream isError = process.getErrorStream();
            final InputStream isOutput = process.getInputStream();

            Utils.processErrorAndOutPut(isError, isOutput);

            process.waitFor();

            // ADDING THE .WAR TO THE tomcat/webapps DIRECTORY
            File server = new File(path + File.separator + "server");

            if (server.exists()) {
              File warFile = getWarFile(server.toPath());
              if (warFile.exists()) {
                File tomcatWebApps = new File(newTomcat4app + File.separator + "webapps");

                if (tomcatWebApps.exists()) {
                  FileUtils.copyFileToDirectory(warFile, tomcatWebApps, true);

                  // LAUNCHING TOMCAT
                  File startTomcatBat =
                      new File(newTomcat4app + File.separator + "bin" + File.separator + "startup.bat");

                  if (startTomcatBat.exists()) {
                    ProcessBuilder tomcatProcessBuilder = new ProcessBuilder(startTomcatBat.getAbsolutePath());
                    tomcatProcessBuilder.directory(new File(newTomcat4app + File.separator + "bin"));

                    Process tomcatProcess = tomcatProcessBuilder.start();

                    final InputStream isTomcatError = tomcatProcess.getErrorStream();
                    final InputStream isTomcatOutput = tomcatProcess.getInputStream();

                    Utils.processErrorAndOutPut(isTomcatError, isTomcatOutput);

                    int tomcatResult = tomcatProcess.waitFor();

                    if (tomcatResult == 0) {
                      getOutput()
                          .showMessage("##########################################################################");
                      getOutput()
                          .showMessage("After Tomcat finishes the loading process the app should be available in: ");
                      getOutput().showMessage("localhost:8080/" + warFile.getName().replace(".war", ""));
                      getOutput()
                          .showMessage("##########################################################################");
                    }

                  } else {
                    getOutput().showError("No tomcat/bin/startup.bat file found.");
                  }

                } else {
                  getOutput().showError("No tomcat/webapps directory found.");
                }
              }

            } else {
              getOutput().showError("No server project found.");
            }

          } else {
            getOutput().showError("No mvn.bat found.");
          }

        } else {
          getOutput().showError("The project does not exist.");
        }
      } else {
        getOutput().showError("'artifactId' element not found in the pom.xml");
      }

    } catch (Exception e) {
      getOutput().showError("In oasp4j deploy command. " + e.getMessage());
    }
  }

  public String readFile(File path) throws IOException {

    StringBuilder sb = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {

      String sCurrentLine;
      while ((sCurrentLine = br.readLine()) != null) {
        sb.append(sCurrentLine);
      }
      // System.out.println("sCurrentLine " + sb);
    }

    return sb.toString();
  }

  private File getWarFile(Path server) {

    File warFile = null;
    File serverTarget = new File(server.toFile().getAbsolutePath() + File.separator + "target");
    if (serverTarget.exists()) {
      Collection<File> warFiles =
          FileUtils.listFiles(serverTarget, new WildcardFileFilter("*.war*"), TrueFileFilter.TRUE);

      if (warFiles.size() > 0) {
        warFile = warFiles.iterator().next();
        Iterator<File> it = warFiles.iterator();
        while (it.hasNext()) {
          File f = it.next();

          if (warFile.lastModified() < warFiles.iterator().next().lastModified()) {
            warFile = f;
          }

        }

      } else {
        getOutput().showError("No WAR file found");
      }

    } else {
      getOutput().showError("No server/target directory found");
    }
    return warFile;
  }

  private Optional<String> getAppName(String path) {

    String appName = null;

    try {

      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      File pomFile = new File(path + File.separator + "pom.xml");

      if (pomFile.exists()) {

        Document doc = docBuilder.parse(pomFile);
        doc.getDocumentElement().normalize();

        Node artifactIdNode = doc.getElementsByTagName("artifactId").item(0);

        appName = artifactIdNode != null ? artifactIdNode.getTextContent() : "";

      }

      return Optional.of(appName);

    } catch (Exception e) {
      return Optional.of(appName);
    }
  }

  private void modifyPom(String filename, String packagname) {

    File fXmlFile = new File(filename);
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;
    Document doc = null;
    try {
      dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.parse(fXmlFile);
      doc.getDocumentElement().normalize();

      NodeList nList = doc.getElementsByTagName("plugin");
      Node parent = null;
      for (int i = 0; i < nList.getLength(); i++) {
        Node n = nList.item(i);
        if (n.getTextContent().contains("org.springframework.boot")
            && n.getTextContent().contains("spring-boot-maven-plugin")) {

          parent = n.getParentNode();
          n.getParentNode().removeChild(n);

          parent.appendChild(addNode(doc, packagname));
          break;
        }
      }

    } catch (Exception e) {
      getOutput().showError("Error executing oasp4j command " + e.getMessage());

    }

    try {

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(fXmlFile);
      // StreamResult result = new StreamResult(new File("D:\\result.xml"));
      transformer.transform(source, result);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Node addNode(Document doc, String packagname) {

    Element dependency = doc.createElement("plugin");

    Node groupId = doc.createElement("groupId");
    groupId.appendChild(doc.createTextNode("org.springframework.boot"));

    Node artifactId = doc.createElement("artifactId");
    artifactId.appendChild(doc.createTextNode("spring-boot-maven-plugin"));

    Node configuration = doc.createElement("configuration");

    Node mainClass = doc.createElement("mainClass");
    mainClass.appendChild(doc.createTextNode(packagname + ".SpringBootApp"));

    Node classifier = doc.createElement("classifier");
    classifier.appendChild(doc.createTextNode("bootified"));

    Node finalName = doc.createElement("finalName");
    finalName.appendChild(doc.createTextNode("${project.artifactId}"));

    Node layout = doc.createElement("layout");
    layout.appendChild(doc.createTextNode("WAR"));

    configuration.appendChild(mainClass);
    configuration.appendChild(classifier);
    configuration.appendChild(finalName);
    configuration.appendChild(layout);

    dependency.appendChild(groupId);
    dependency.appendChild(artifactId);
    dependency.appendChild(configuration);

    return dependency;
  }

}