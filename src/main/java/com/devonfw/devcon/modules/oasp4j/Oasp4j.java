package com.devonfw.devcon.modules.oasp4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
  @Parameter(name = "servername", description = "Name of project"),
  @Parameter(name = "packagename", description = "package name in server project"),
  @Parameter(name = "groupid", description = "groupid for server project"),
  @Parameter(name = "version", description = "version of server project") })
  public void create(String serverpath, String servername, String packagename, String groupid, String version)
      throws IOException {

    String command =
        new StringBuffer("cmd /c start mvn -DarchetypeVersion=").append(Constants.OASP_TEMPLATE_VERSION)
            .append(" -DarchetypeGroupId=").append(Constants.OASP_TEMPLATE_GROUP_ID).append(" -DarchetypeArtifactId=")
            .append(Constants.OASP_TEMPLATE_GROUP_ID).append(" -DarchetypeArtifactId=")
            .append(Constants.OASP_ARTIFACT_ID).append(" archetype:generate -DgroupId=").append(groupid)
            .append(" -DartifactId=").append(servername).append(" -Dversion=").append(version).append(" -Dpackage=")
            .append(packagename).append(" -DinteractiveMode=false").toString();

    // Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot(serverpath);

    if (!SystemUtils.IS_OS_WINDOWS) {
      getOutput().showMessage("This task is currently only supported on Windows");
      return;
    }

    // if (distInfo.isPresent()) {

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

        int result = process.waitFor();
        if (result == 0) {
          getOutput().showMessage("Project Creation complete");
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
    // } else {
    // getOutput().showError("Not a Devon Distribution Workspace");
    // }

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
  @Parameter(name = "port", description = "Port to start Spring boot app (port 8081 by default)", optional = true),
  @Parameter(name = "path", description = "Path to server project (default is current working directory + \\server)", optional = true) })
  public void run(String port, String path) {

    this.projectInfo = getContextPathInfo().getProjectRoot(path);
    // Check projectInfo loaded. If not, abort
    if (!this.projectInfo.isPresent()) {
      getOutput().showError("Not in a project or -path param not pointing to a project");
      return;
    }

    ProjectInfo info = this.projectInfo.get();
    System.out.println("path before modification " + info.getPath().toString());

    // Get port from a) parameter or b) devon.json file or c) default value passed as 2nd paranter to info.getProperty
    String port_ = (port.isEmpty()) ? info.getProperty("port", "8081").toString() : port.trim();
    String path_ = (path.isEmpty()) ? (info.getPath().toString() + "\\server") : path;

    System.out.println("Server project path " + path_);

    try {
      String commandStr = "mvn spring-boot:run -Drun.arguments=\"server.port=" + port_ + "\" ";
      String cmd = "cmd /c start " + commandStr;

      Process p = Runtime.getRuntime().exec(cmd, null, new File(path_));
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
  @Parameters(values = { @Parameter(name = "path", description = "Path to Server project Workspace (currentDir if not given)", optional = true) })
  public void build(String path) {

    // Check projectInfo loaded. If not, abort
    // if (!this.projectInfo.isPresent()) {
    // getOutput().showError("Not in a project or -path param not pointing to a project");
    // return;
    // }
    this.projectInfo = getContextPathInfo().getProjectRoot(path);
    ProjectInfo info = this.projectInfo.get();
    System.out.println("projectInfo read...");
    System.out.println("path " + this.projectInfo.get().getPath() + "project type "
        + this.projectInfo.get().getProjecType());

    Process p;
    try {
      String cmd = "cmd /c start mvn clean install";

      p = Runtime.getRuntime().exec(cmd, null, this.projectInfo.get().getPath().toFile());
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
  @Parameter(name = "tomcatpath", description = "Path to tomcat folder (if not provided and the project is in a Devonfw distribution the default software/tomcat folder will be used)", optional = true),
  @Parameter(name = "path", description = "Path to project (current directory if not provided).", optional = true) })
  public void deploy(String tomcatpath, String path) {

    try {

      this.projectInfo = getContextPathInfo().getProjectRoot(path);

      Optional<DistributionInfo> distInfo = this.contextPathInfo.getDistributionRoot();

      if (!distInfo.isPresent()) {
        getOutput().showError("Not in a Devon distribution");
        return;
      }

      if (!this.projectInfo.isPresent()) {
        getOutput().showError("Not in a project or -path param not pointing to a project");
        return;
      }

      path = path.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString() : path;
      tomcatpath =
          tomcatpath.isEmpty() ? distInfo.get().getPath().toFile().toString() + File.separator + "software"
              + File.separator + "tomcat" : tomcatpath;

      File tomcatDir = new File(tomcatpath);

      if (!tomcatDir.exists()) {
        getOutput().showError("Tomcat directory " + tomcatDir.toString() + " not found.");
        return;
      }

      File project = new File(path);

      if (project.exists()) {

        // PACKAGING THE APP (creating the .war file)
        File mvnBat = new File(distInfo.get().getPath().toString() + File.separator + "software\\maven\\bin\\mvn.bat");
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
              File tomcatWebApps = new File(tomcatDir + File.separator + "webapps");

              if (tomcatWebApps.exists()) {
                FileUtils.copyFileToDirectory(warFile, tomcatWebApps, true);

                // LAUNCHING TOMCAT
                File startTomcatBat = new File(tomcatDir + File.separator + "bin" + File.separator + "startup.bat");

                if (startTomcatBat.exists()) {
                  ProcessBuilder tomcatProcessBuilder = new ProcessBuilder(startTomcatBat.getAbsolutePath());
                  tomcatProcessBuilder.directory(new File(tomcatDir + File.separator + "bin"));

                  Process tomcaProcess = tomcatProcessBuilder.start();

                  final InputStream isTomcatError = tomcaProcess.getErrorStream();
                  final InputStream isTomcatOutput = tomcaProcess.getInputStream();

                  Utils.processErrorAndOutPut(isTomcatError, isTomcatOutput);
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

    } catch (Exception e) {
      getOutput().showError("In oasp4j deploy command. " + e.getMessage());
    }
  }

  private void update_setting_file(File inputFile) {

    String[] terms = { "TomcatServer", "tomcat" };
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;
    try {
      dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(inputFile);

      doc.getDocumentElement().normalize();
      Node servers = doc.getDocumentElement().getElementsByTagName(Constants.SERVERS).item(0);

      if (!xmlAlreadyConfigured(servers, terms)) {
        Element server = doc.createElement(Constants.SERVER);
        Node id = doc.createElement("id");
        id.appendChild(doc.createTextNode(Constants.TOMCAT_SERVER));
        server.appendChild(id);

        Node username = doc.createElement(Constants.USERNAME);
        username.appendChild(doc.createTextNode(Constants.TOMCAT));
        server.appendChild(username);

        Node password = doc.createElement(Constants.PASSWORD);
        password.appendChild(doc.createTextNode(Constants.TOMCAT));
        server.appendChild(password);
        servers.appendChild(server);

        writeToXml(doc, inputFile);
      }
    } catch (Exception e) {
      getOutput().showError("An error occured while updating setting.xml file from .m2 folder");
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

  private void update_tomcat_user_file(File tomcat_user_file) {

    String[] rolenameList = { "admin-gui", "manager-gui", "manager-script" };
    String[] contents =
        { "<Role rolename=\"admin-gui\"/>", "<Role rolename=\"manager-gui\"/>", "<Role rolename=\"manager-script\"/>",
        "<user password=\"tomcat\" roles=\"manager-script,admin-gui,manager-gui\" username=\"tomcat\"/>" };
    boolean result = false;

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;
    try {

      String temp = new StringBuffer(readFile(tomcat_user_file)).toString();
      for (String roleName : contents) {
        if (temp.contains(roleName)) {
          result = true;
        } else {
          result = false;
          break;
        }
      }
      dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(tomcat_user_file);

      doc.getDocumentElement().normalize();

      Node tomcat = doc.getDocumentElement();

      if (!result) {

        for (int i = 0; i < rolenameList.length; i++) {
          Element role = doc.createElement(Constants.ROLE);
          role.setAttribute(Constants.ROLE_NAME, rolenameList[i]);
          tomcat.appendChild(role);
          // DOMSource source = new DOMSource(doc);
          // TransformerFactory transformerFactory = TransformerFactory.newInstance();
          // Transformer transformer = transformerFactory.newTransformer();
          // StreamResult result = new StreamResult(tomcat_user_file);
          // transformer.transform(source, result);
        }

        Element user = doc.createElement(Constants.USER);
        user.setAttribute(Constants.USERNAME, Constants.TOMCAT);
        user.setAttribute(Constants.PASSWORD, Constants.TOMCAT);
        user.setAttribute(Constants.ROLES, Constants.ALL_ROLES);

        tomcat.appendChild(user);

        writeToXml(doc, tomcat_user_file);
      }
    } catch (Exception e) {
      getOutput().showError("An error occured while updating tomcat-user.xml file");
    }
  }

  private void modifyPom(File inputFile, String warFileName, String serverport) {

    String[] terms = { "org.apache.tomcat.maven", "tomcat7-maven-plugin" };
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder;
    try {
      dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(inputFile);
      doc.getDocumentElement().normalize();

      NodeList buildNodes = doc.getDocumentElement().getElementsByTagName(Constants.BUILD);

      for (int k = 0; k < buildNodes.getLength(); k++) {
        if (!(buildNodes.item(k).getParentNode().getNodeName().equalsIgnoreCase(Constants.PROFILE))) {

          Node build = buildNodes.item(k);
          if (!xmlAlreadyConfigured(build, terms)) {
            NodeList childNode = build.getChildNodes();
            for (int j = 0; j < childNode.getLength(); j++) {

              if (childNode.item(j).getNodeName().equalsIgnoreCase(Constants.PLUGINS)) {
                Node plugins = childNode.item(j);
                Element plugin = doc.createElement(Constants.PLUGIN);

                Node groupId = doc.createElement(Constants.GROUP_ID);
                groupId.appendChild(doc.createTextNode("org.apache.tomcat.maven"));
                plugin.appendChild(groupId);

                Node artifactId = doc.createElement(Constants.ARTIFACT_ID);
                artifactId.appendChild(doc.createTextNode("tomcat7-maven-plugin"));
                plugin.appendChild(artifactId);

                Node version = doc.createElement("version");
                version.appendChild(doc.createTextNode("2.2"));
                plugin.appendChild(version);

                Node configuration = doc.createElement("configuration");

                Node url = doc.createElement("url");
                url.appendChild(doc.createTextNode("http://localhost:" + serverport + "/manager/text"));
                configuration.appendChild(url);

                Node server = doc.createElement(Constants.SERVER);
                server.appendChild(doc.createTextNode(Constants.TOMCAT_SERVER));
                configuration.appendChild(server);

                Node path = doc.createElement("path");
                path.appendChild(doc.createTextNode("/" + warFileName));
                configuration.appendChild(path);

                Node user = doc.createElement(Constants.USER);
                user.appendChild(doc.createTextNode(Constants.TOMCAT));
                configuration.appendChild(user);

                Node password = doc.createElement(Constants.PASSWORD);
                password.appendChild(doc.createTextNode(Constants.TOMCAT));
                configuration.appendChild(password);

                Node update = doc.createElement("update");
                update.appendChild(doc.createTextNode("true"));
                configuration.appendChild(update);

                plugin.appendChild(configuration);

                plugins.appendChild(plugin);

                writeToXml(doc, inputFile);
              }
            } // end of internal for
          } else {
            // NOt required as we do not need to modify pom file as plugin already exists
          }
        }
      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      getOutput().showError("An error occured " + e.getMessage());

    }
  }

  private String findWarName(String path) {

    String name = null;
    File target = new File(path + "\\target");
    if (target.exists()) {
      String[] fileList = target.list();

      for (int i = 0; i < fileList.length; i++) {
        if (fileList[i].endsWith(".war")) {

          name = fileList[i].substring(0, fileList[i].indexOf("."));

        } else {
          getOutput().showError("No war file present in path");
        }
      }

    }

    return name;
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

  private void writeToXml(Node doc, File inputFile) {

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer;
    try {
      transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(inputFile);
      transformer.transform(source, result);
    } catch (Exception e) {

      getOutput().showError("An error occured " + e.getMessage());

    }

  }

  private boolean xmlAlreadyConfigured(Node node, String[] terms) {

    boolean result = true;
    for (String term : terms) {
      if (!node.getTextContent().contains(term)) {
        result = false;
        break;
      }
    }
    return result;
  }

}