package com.devonfw.devcon.modules.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

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
import org.apache.commons.lang3.tuple.Triple;
import org.w3c.dom.Comment;
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
 * Module to automate tasks related to devonfw projects (server + client)
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "project", description = "Module to automate tasks related to the devon projects (server + client)", visible = false)
public class Project extends AbstractCommandModule {

  private final String DEVON4SENCHA = "devon4sencha";

  private final String OASP4J = "oasp4j";

  private final String OASP4JS = "oasp4js";

  private final String SENCHA = "sencha";

  private final String CREATE = "create";

  private final String DEPLOY = "deploy";

  private final String WORKSPACE = "copyworkspace";

  private final String POM_XML = "pom.xml";

  @Command(name = "build", description = "This command will build the server & client project(unified server and client build)", context = ContextType.COMBINEDPROJECT)
  @Parameters(values = {
  @Parameter(name = "clienttype", description = "This parameter shows which type of client is integrated with server i.e oasp4js or devon4sencha", optional = true),
  @Parameter(name = "clientpath", description = "path to client directory", optional = true) })
  public void build(String clienttype, String clientpath) {

    String clientNewType;
    if (!this.projectInfo.isPresent()) {
      getOutput().showError("Not in a project or -path param not pointing to a project");
      return;
    }
    try {

      if (!clienttype.isEmpty() && ((clienttype.equalsIgnoreCase(Constants.OASP4JS)
          || clienttype.equalsIgnoreCase(Constants.DEVON4SENCHA)))) {
        clienttype = clienttype;
      } else if (!clienttype.isEmpty()) {
        getOutput().showError(
            "Clienttype value is not valid. Please set client type to oasp4js or devon4sencha.You can enter it via command or set it in devon.json");
        return;
      }
      int size = this.projectInfo.get().getSubProjects().size();

      for (int i = 0; i < size; i++) {
        ProjectInfo p = this.projectInfo.get().getSubProjects().get(i);

        if (p.getProjecType() == ProjectType.OASP4J) {
          Optional<com.devonfw.devcon.common.api.Command> oasp4j = getCommand("oasp4j", "build", p);
          oasp4j.get().exec();
        } else {
          clienttype = p.getProjecType().toString();
          System.out.println("clienttype " + clienttype);
          switch (clienttype.toLowerCase()) {
          case Constants.OASP4JS:
            Optional<com.devonfw.devcon.common.api.Command> oasp4js_cmd = getCommand("oasp4js", "build", p);
            oasp4js_cmd.get().exec();
            break;
          case Constants.DEVON4SENCHA:
            Optional<com.devonfw.devcon.common.api.Command> sencha_cmd = getCommand("sencha", "build", p);
            sencha_cmd.get().exec();
            break;
          default:
            getOutput().showError(
                "Clienttype value is not valid. Please set client type to oasp4js or devon4sencha.You can enter it via command or set it in devon.json");
            break;

          }
        }
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during executing Project Cmd");
    }

  }

  @Command(name = "create", description = "This command is used to create new combined server & client project")
  @Parameters(values = {
  @Parameter(name = "combinedprojectpath", description = "where to create the combined server and client project (currentDir if not given)", optional = true),
  @Parameter(name = "servername", description = "name for the server project"),
  @Parameter(name = "packagename", description = "package name for the server project"),
  @Parameter(name = "groupid", description = "groupid for server project"),
  @Parameter(name = "version", description = "version of server project"),
  @Parameter(name = "clienttype", description = "type of the client project: 'devon4sencha' or 'oasp4js'"),
  @Parameter(name = "clientname", description = "name for the client project"),
  @Parameter(name = "clientpath", description = "path where the client project will be created.", optional = true),
  @Parameter(name = "createsenchaws", description = "Only for client type 'devon4sencha': if a new Sencha Workspace must be created to store new app. Values TRUE or FALSE (default)", optional = true) })
  public void create(String combinedprojectpath, String servername, String packagename, String groupid, String version,
      String clienttype, String clientname, String clientpath, String createsenchaws) {

    try {

      Optional<com.devonfw.devcon.common.api.Command> createServer = getCommand(this.OASP4J, this.CREATE);

      combinedprojectpath = combinedprojectpath.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString()
          : combinedprojectpath;

      boolean createWs = Boolean.parseBoolean(createsenchaws.toLowerCase());
      String clientJsonReference;
      if (createServer.isPresent()) {
        createServer.get().exec(combinedprojectpath, servername, packagename, groupid, version);
      } else {
        getOutput().showError("No command create found for oasp4j module.");
      }

      getOutput().showMessage("Creating client project...");

      if (clienttype.equals(this.DEVON4SENCHA)) {

        if (!clientpath.isEmpty()) {

          if (createWs) {
            createSenchaWs(clientpath + File.separator + this.DEVON4SENCHA, combinedprojectpath);
            createSenchaApp(clientname, clientpath + File.separator + this.DEVON4SENCHA);
            clientJsonReference = clientpath + File.separator + this.DEVON4SENCHA + File.separator + clientname;
          } else {
            createSenchaApp(clientname, clientpath);
            clientJsonReference = clientpath + File.separator + clientname;
          }

        } else {
          createSenchaWs(combinedprojectpath + File.separator + this.DEVON4SENCHA, combinedprojectpath);
          createSenchaApp(clientname, combinedprojectpath + File.separator + this.DEVON4SENCHA);
          clientJsonReference = this.DEVON4SENCHA + File.separator + clientname;
        }

      } else if (clienttype.equals(this.OASP4JS)) {

        Optional<com.devonfw.devcon.common.api.Command> createOasp4js = getCommand(this.OASP4JS, this.CREATE);

        if (createOasp4js.isPresent()) {
          createOasp4js.get().exec(clientname, clientpath);

          clientJsonReference = clientpath.isEmpty() ? clientname : clientpath + File.separator + clientname;

        } else {
          getOutput().showError("No command create found for oasp4js module.");
          return;
        }

      } else {
        getOutput().showError(
            "The parameter value for 'clienttype' is not valid. The options for this parameter are: 'devon4sencha' and 'oasp4js'.");
        return;
      }

      clientJsonReference = clientJsonReference.replace("\\", "\\\\");
      getOutput().showMessage("Adding devon.json file to combined project...");
      Utils.addDevonJsonFile(new File(combinedprojectpath).toPath(), servername, clientJsonReference);
      getOutput().showMessage("Combined project created successfully.");

    } catch (Exception e) {
      getOutput().showError("An error occurred during execution of project create command. " + e.getMessage());
    }

  }

  /**
   * @param clienttype Defines type of client either oasp4js or Sencha
   * @param clientport Defines client port for Sencha project not configurable for oasp4js project
   * @param clientpath Path for client directory
   * @param serverport Port to run server project
   * @param serverpath Path of server directory
   */
  @Command(name = "run", description = "This command will run the server & client project(unified server and client build) in debug mode (seperate cliet and spring boot server(not on tomcat))", context = ContextType.COMBINEDPROJECT)
  @Parameters(values = {
  @Parameter(name = "clienttype", description = "This parameter shows which type of client is integrated with server i.e oasp4js or devon4sencha", optional = true),
  @Parameter(name = "clientpath", description = "Location of the oasp4js app", optional = true),
  @Parameter(name = "serverport", description = "Port to start server", optional = true),
  @Parameter(name = "serverpath", description = "Path to Server project Workspace (currentDir if not given)", optional = true) })
  public void run(String clienttype, String clientpath, String serverport, String serverpath) {

    if (!this.projectInfo.isPresent()) {
      getOutput().showError("Not in a project or -path param not pointing to a project");
      return;
    }

    try {
      if (!clienttype.isEmpty() && ((clienttype.equalsIgnoreCase(Constants.OASP4JS)
          || clienttype.equalsIgnoreCase(Constants.DEVON4SENCHA)))) {
        clienttype = clienttype;
      } else if (!clienttype.isEmpty()) {
        getOutput().showError(
            "Clienttype value is not valid. Please set client type to oasp4js or devon4sencha.You can enter it via command or set it in devon.json");
        return;
      }
      int size = this.projectInfo.get().getSubProjects().size();
      System.out.println("size " + size);
      for (int i = 0; i < size; i++) {
        ProjectInfo p = this.projectInfo.get().getSubProjects().get(i);
        System.out.println("------------------type " + p.getConfig().get("type").toString());

        if (p.getProjecType() == ProjectType.OASP4J) {
          Optional<com.devonfw.devcon.common.api.Command> cmd = getCommand(Constants.OASP4J, Constants.RUN, p);
          cmd.get().exec(serverport);
        } else {
          clienttype = p.getProjecType().toString();
          System.out.println("clienttype " + clienttype);
          switch (clienttype.toLowerCase()) {
          case Constants.OASP4JS:
            Optional<com.devonfw.devcon.common.api.Command> oasp4js_cmd =
                getCommand(Constants.OASP4JS, Constants.RUN, p);
            oasp4js_cmd.get().exec();
            break;
          case Constants.DEVON4SENCHA:
            Optional<com.devonfw.devcon.common.api.Command> sencha_cmd = getCommand(Constants.SENCHA, Constants.RUN, p);
            sencha_cmd.get().exec();
            break;
          default:
            getOutput().showError(
                "Clienttype value is not valid. Please set client type to oasp4js or devon4sencha.You can enter it via command or set it in devon.json");
            break;

          }
        }
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during executing Project Cmd");
    }
  }

  @Command(name = "deploy", description = "This command is to automate the deploy process of a combined server & client project", context = ContextType.COMBINEDPROJECT)
  @Parameters(values = {
  @Parameter(name = "tomcatpath", description = "Path to tomcat folder (the distribution's Tomcat when not given)", optional = true),
  @Parameter(name = "clienttype", description = "Type of client either angular or Sencha (obtained from 'projects' property in devon.json when not given)", optional = true),
  @Parameter(name = "clientpath", description = "path to client project (obtained from 'projects' property in devon.json when not given)", optional = true),
  @Parameter(name = "serverpath", description = "path to server project (obtained from 'projects' property in devon.json when not given)", optional = true), })
  public void deploy(String tomcatpath, String clienttype, String clientpath, String serverpath) {

    ProcessBuilder install, packageWithClient;
    Process process, process1;
    int errCode, errCode1;

    try {

      if (!this.projectInfo.isPresent()) {
        getOutput().showError("Not in a project or -path param not pointing to a project");
        return;
      }
      // this.projectInfo = getContextPathInfo().getProjectRoot(clientpath);

      getOutput().showMessage(getContextPathInfo().toString());
      getOutput().showMessage(getContextPathInfo().getCurrentWorkingDirectory().toString());
      getOutput().showMessage("Current directory: " + getContextPathInfo().getCurrentWorkingDirectory());
      // this.projectInfo = getContextPathInfo().getProjectRoot(path);
      if (!this.projectInfo.isPresent()) {
        getOutput().showError("No devon.json file found in " + this.projectInfo.get().getPath().toAbsolutePath());
        return;
      }

      if (this.projectInfo.get().getSubProjects().size() == 0) {
        getOutput().showError("The property 'projects' defined in " + getContextPathInfo().getCurrentWorkingDirectory()
            + File.separator + "devon.json file is empty.");
        return;
      }

      getOutput().showMessage("Project type: " + this.projectInfo.get().getProjecType());
      getOutput().showMessage("Version: " + this.projectInfo.get().getVersion());
      List<ProjectInfo> subProjects = this.projectInfo.get().getSubProjects();
      getOutput().showMessage("subProjects: " + subProjects.size());

      for (ProjectInfo projectInfo : subProjects) {
        getOutput().showMessage(projectInfo.getProjecType().toString());
        getOutput().showMessage(projectInfo.getPath().toString());
        boolean thisIsClient = projectInfo.getProjecType() == ProjectType.DEVON4SENCHA
            || projectInfo.getProjecType() == ProjectType.OASP4JS;
        clientpath = (clientpath.isEmpty() && thisIsClient) ? projectInfo.getPath().toString() : clientpath;
        clienttype = (clienttype.isEmpty() && thisIsClient) ? projectInfo.getProjecType().toString() : clienttype;
        boolean thisIsServer = projectInfo.getProjecType() == ProjectType.OASP4J;
        serverpath = (serverpath.isEmpty() && thisIsServer) ? projectInfo.getPath().toString() : serverpath;
      }

      Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot();

      if (!distInfo.isPresent()) {
        getOutput().showError("Not in a Devonfw distribution");
        return;
      }

      Path distRootPath = distInfo.get().getPath();
      String distributionpath = distRootPath.toString();
      getOutput().showMessage("distpath " + distributionpath);

      File mvnBat = new File(distributionpath + "\\software\\maven\\bin\\mvn.bat");

      if (!mvnBat.exists()) {
        getOutput().showMessage(mvnBat.toString() + " not found");
        return;
      }

      if (this.projectInfo.isPresent()) {
        ProjectType clientType = this.projectInfo.get().getProjecType();
        File clientpath_java = new File(clientpath + File.separator + "java");

        if (!clientpath_java.exists()) {
          getOutput().showError(clientpath_java.toString() + " folder not found in client app.");
          return;
        }

        install = new ProcessBuilder(mvnBat.getAbsolutePath(), "install");
        install.directory(clientpath_java);
        process = install.start();
        final InputStream isError = process.getErrorStream();
        final InputStream isOutput = process.getInputStream();

        Utils.processErrorAndOutPut(isError, isOutput);
        errCode = process.waitFor();
        if (errCode == 0) {
          getOutput().showMessage("Execution successful. ");
          getOutput().showMessage("Added client app to local repository.");
        } else {
          getOutput().showError("Execution failed while installing the client app in the local repository.");
          getOutput().showError("Adding client app to local repository failed.");
          return;
        }

        configureServerPOM(serverpath, clientpath, clientType);
        configureWebSecurityClass(serverpath);

        packageWithClient = new ProcessBuilder(mvnBat.getAbsolutePath(), "package", "-P", "jsclient");
        packageWithClient.directory(new File(serverpath));
        process1 = packageWithClient.start();
        final InputStream isError1 = process1.getErrorStream();
        final InputStream isOutput1 = process1.getInputStream();

        Utils.processErrorAndOutPut(isError1, isOutput1);

        errCode1 = process1.waitFor();
        if (errCode1 == 0) {
          getOutput().showMessage("Execution successful ");
          getOutput().showMessage("Server war file created successfully.");
        } else {
          getOutput().showError("Execution failed while creating the package of the client and server apps.");
          getOutput().showError("Error creating server .war file.");
          return;
        }

        Optional<com.devonfw.devcon.common.api.Command> deploy = getCommand(this.OASP4J, this.DEPLOY);
        if (deploy.isPresent()) {

          deploy.get().exec(tomcatpath);

        } else {
          getOutput().showError("No command deploy found for oasp4j module.");
        }
      } else {
        getOutput().showError("devon.json configuration file not found for client project.");
      }
    } catch (Exception e) {
      getOutput().showError("An error occurred during the execution of project deploy command. " + e.getMessage());
    }

  }

  public void configureServerPOM(String serverPath, String clientPath, ProjectType clientType) throws Exception {

    try {

      File serverInServer = new File(serverPath + File.separator + "server");

      if (!serverInServer.exists()) {
        throw new Exception(serverInServer.toString() + " not found.");
      }

      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      Triple<String, String, String> clientPomInfo = getClientPomInfo(clientPath, docBuilder);

      if (clientPomInfo != null) {
        editServerPom(serverInServer.toString(), docBuilder, clientPomInfo, clientType);

      } else {
        getOutput().showError(
            "The pom.xml in the java directory of the client project is not found or has some missing information.");
      }

    } catch (Exception e) {
      throw e;
    }
  }

  private void configureWebSecurityClass(String serverPath) throws Exception {

    FileInputStream fs = null;
    InputStreamReader in = null;
    BufferedReader br = null;
    boolean jsclientAdded = false;
    boolean websocketAdded = false;

    try {
      this.projectInfo = getContextPathInfo().getProjectRoot(serverPath);
      if (this.projectInfo.isPresent()) {
        File projectDirectory = new File(this.projectInfo.get().getPath().toString());

        File webSecurityConfig = getWebSecurityConfigFile(projectDirectory);

        if (webSecurityConfig != null) {
          fs = new FileInputStream(webSecurityConfig);
          in = new InputStreamReader(fs);
          br = new BufferedReader(in);

          StringBuffer sb = new StringBuffer();
          String line;

          while (true) {
            line = br.readLine();
            if (line == null) {
              break;
            }

            sb.append(line);
            sb.append(System.getProperty("line.separator"));
          }
          int unsecuredResourcesIndex = sb.toString().indexOf("unsecuredResources");
          if (unsecuredResourcesIndex > 0) {
            String[] securityConfig = sb.toString().split("unsecuredResources");
            if (securityConfig.length > 1) {
              String token = "new String[] {";
              String unsecuredResources = securityConfig[1].replace(token, "").split("}")[0].trim();

              int tokenIndex = sb.indexOf(token);
              if (!unsecuredResources.contains("jsclient")) {

                sb.insert(tokenIndex + token.length(), "\"/jsclient/**\", ");
                jsclientAdded = true;
              } else {
                getOutput().showMessage("unsecuredResources in WebSecurityConfig already has a 'jsclient' term.");
              }

              if (!unsecuredResources.contains("websocket")) {
                sb.insert(tokenIndex + token.length(), "\"/websocket/**\", ");
                websocketAdded = true;
              } else {
                getOutput().showMessage("unsecuredResources in WebSecurityConfig already has a 'websocket' term.");
              }

              if (jsclientAdded || websocketAdded) {

                BufferedWriter out = new BufferedWriter(new FileWriter(webSecurityConfig));

                out.write(sb.toString());
                out.flush();
                out.close();

              }

              getOutput().showMessage("WebSecurityConfig class configured.");
            }
          } else {
            getOutput().showError("No unsercuredResources found in WebSecurityConfig.java");
          }

        } else {
          getOutput().showError("No WebConfigSecurity.java found in the project.");
        }

      } else {
        getOutput().showError("Not recognized oasp4j project");
      }

    } catch (Exception e) {
      throw e;
    } finally {
      if (br != null) {
        br.close();
      }
      if (in != null) {
        in.close();
      }
      if (fs != null) {
        fs.close();
      }
    }
  }

  private File getWebSecurityConfigFile(File projectDirectory) {

    try {
      File webSecurityConfig = null;

      Collection<File> files =
          FileUtils.listFiles(projectDirectory, new WildcardFileFilter("*WebSecurityConfig*"), TrueFileFilter.TRUE);

      if (files.size() == 0) {
        getOutput().showError("No WebConfigSecurity.java found in the project.");
      } else {

        for (File f : files) {
          if (!f.getPath().contains("eclipse-target") && !f.getPath().contains("classes")
              && !f.getPath().contains("target") && !f.getPath().contains("templates")) {
            webSecurityConfig = f;
            break;
          }
        }
      }

      return webSecurityConfig;
    } catch (Exception e) {
      getOutput().showError("Getting WebSecurityConfig.java file. " + e.getMessage());
      return null;
    }

  }

  private Triple<String, String, String> getClientPomInfo(String clientPath, DocumentBuilder docBuilder) {

    String groupId = null, artifactId = null, version = null;

    try {

      File clientPom = new File(clientPath + File.separator + "java" + File.separator + this.POM_XML);

      if (clientPom.exists()) {

        Document doc = docBuilder.parse(clientPom);
        doc.getDocumentElement().normalize();

        Node groupIdNode = doc.getElementsByTagName("groupId").item(0);
        Node artifactIdNode = doc.getElementsByTagName("artifactId").item(0);
        Node versionNode = doc.getElementsByTagName("version").item(0);

        groupId = groupIdNode != null ? groupIdNode.getTextContent() : "";
        artifactId = artifactIdNode != null ? artifactIdNode.getTextContent() : "";
        version = versionNode != null ? versionNode.getTextContent() : "";

      }

      return Triple.of(groupId, artifactId, version);
    } catch (Exception e) {
      getOutput().showError("Getting client pom.xml info. " + e.getMessage());
      return null;
    }
  }

  private void editServerPom(String serverPath, DocumentBuilder docBuilder,
      Triple<String, String, String> clientPomInfo, ProjectType clientType) throws Exception {

    try {

      File serverPom = new File(serverPath + File.separator + this.POM_XML);

      if (serverPom.exists()) {
        Document doc = docBuilder.parse(serverPom);
        doc.getDocumentElement().normalize();

        addClientDependency(doc, clientPomInfo);

        Optional<Node> jsclientPlugins = getJsclientPluginsNode(doc);

        if (!jsclientPlugins.isPresent()) {
          throw new Exception("No 'plugins' node found for jsclient profile in the server pom.xml");
        }

        // if SENCHA project, delete executions in 'jsclient' profile, in 'exec-maven-plugin' plugin
        // if OASP4JS project only comment these executions
        Optional<Node> execMavenPlugin = getExecMavenPluginNode(jsclientPlugins.get());
        if (execMavenPlugin.isPresent()) {
          if (clientType.equals(ProjectType.DEVON4SENCHA)) {
            jsclientPlugins.get().removeChild(execMavenPlugin.get());
          } else {
            commentNode(doc, jsclientPlugins.get(), execMavenPlugin.get());
          }

        }

        addJsclientPlugin(doc, jsclientPlugins.get(), clientPomInfo);
        applyChangesToPom(doc, serverPom);

        getOutput().showMessage("Server pom.xml configured.");
      }

    } catch (Exception e) {
      throw e;
    }
  }

  private Optional<Node> getJsclientPluginsNode(Document doc) {

    try {

      NodeList pluginsList = doc.getElementsByTagName("plugins");
      Node pluginsNode = null;
      for (int i = 0; i < pluginsList.getLength(); i++) {
        Node n = pluginsList.item(i);
        if (n.getParentNode().getNodeName().equals("build")
            && n.getParentNode().getParentNode().getNodeName().equals("profile")
            && n.getParentNode().getParentNode().getChildNodes().item(1).getTextContent().equals("jsclient")) {
          pluginsNode = n;
          break;
        }
      }
      return Optional.of(pluginsNode);
    } catch (Exception e) {
      getOutput().showError("In getPlugins method for jsclient profile. " + e.getMessage());
      return Optional.absent();
    }
  }

  private Optional<Node> getExecMavenPluginNode(Node pluginsNode) {

    try {

      boolean isExecMavenPluginNode = false;
      Node execMavenPlugin = null;

      for (int i = 0; i < pluginsNode.getChildNodes().getLength(); i++) {

        Node plugin = pluginsNode.getChildNodes().item(i);
        for (int j = 0; j < plugin.getChildNodes().getLength(); j++) {
          Node n = plugin.getChildNodes().item(j);
          if (n.getNodeName().equals("artifactId")) {
            if (n.getTextContent().equals("exec-maven-plugin")) {
              isExecMavenPluginNode = true;
              break;
            }
          }
        }

        if (isExecMavenPluginNode) {
          execMavenPlugin = plugin;
          break;
        }

      }

      return Optional.of(execMavenPlugin);

    } catch (Exception e) {
      getOutput()
          .showMessage("Exec-Maven-Plugin node could not be configured or is already configured. " + e.getMessage());
      return Optional.absent();
    }
  }

  private void addClientDependency(Document doc, Triple<String, String, String> clientPomInfo) throws Exception {

    try {
      Node dependencies = doc.getElementsByTagName("dependencies").item(0);

      if (dependencies == null)
        throw new Exception("No 'dependencies' node found in the server pom.xml");

      String[] terms = { clientPomInfo.getLeft(), clientPomInfo.getMiddle(), clientPomInfo.getRight() };

      if (!xmlAlreadyConfigured(dependencies, terms)) {
        Element dependency = doc.createElement("dependency");
        Node groupId = doc.createElement("groupId");
        groupId.appendChild(doc.createTextNode(clientPomInfo.getLeft()));

        Node artifactId = doc.createElement("artifactId");
        artifactId.appendChild(doc.createTextNode(clientPomInfo.getMiddle()));
        Node version = doc.createElement("version");
        version.appendChild(doc.createTextNode(clientPomInfo.getRight()));
        Node type = doc.createElement("type");
        type.appendChild(doc.createTextNode("zip"));
        Node classifier = doc.createElement("classifier");
        classifier.appendChild(doc.createTextNode("web"));
        Node scope = doc.createElement("scope");
        scope.appendChild(doc.createTextNode("runtime"));

        dependency.appendChild(groupId);
        dependency.appendChild(artifactId);
        dependency.appendChild(version);
        dependency.appendChild(type);
        dependency.appendChild(classifier);
        dependency.appendChild(scope);

        dependencies.appendChild(dependency);
      } else {
        getOutput().showMessage("The dependency was already added to server pom.xml");
      }

    } catch (Exception e) {
      getOutput().showError("Adding client dependency in server pom. " + e.getMessage());
      throw e;
    }

  }

  private void addJsclientPlugin(Document doc, Node jsclientPlugins, Triple<String, String, String> clientPomInfo) {

    try {

      String[] terms =
          { "org.apache.maven.plugins", "maven-war-plugin", clientPomInfo.getLeft(), clientPomInfo.getMiddle() };
      if (!xmlAlreadyConfigured(jsclientPlugins, terms)) {
        Element plugin = doc.createElement("plugin");

        Node pluginGroupId = doc.createElement("groupId");
        pluginGroupId.appendChild(doc.createTextNode("org.apache.maven.plugins"));
        plugin.appendChild(pluginGroupId);

        Node pluginArtifactId = doc.createElement("artifactId");
        pluginArtifactId.appendChild(doc.createTextNode("maven-war-plugin"));
        plugin.appendChild(pluginArtifactId);

        Node configuration = doc.createElement("configuration");

        Node overlays = doc.createElement("overlays");
        configuration.appendChild(overlays);

        Node overlay = doc.createElement("overlay");
        overlays.appendChild(overlay);

        Node groupId = doc.createElement("groupId");
        groupId.appendChild(doc.createTextNode(clientPomInfo.getLeft()));

        Node artifactId = doc.createElement("artifactId");
        artifactId.appendChild(doc.createTextNode(clientPomInfo.getMiddle()));

        Node type = doc.createElement("type");
        type.appendChild(doc.createTextNode("zip"));
        Node classifier = doc.createElement("classifier");
        classifier.appendChild(doc.createTextNode("web"));

        Node targetPath = doc.createElement("targetPath");
        targetPath.appendChild(doc.createTextNode("jsclient"));

        overlay.appendChild(groupId);
        overlay.appendChild(artifactId);
        overlay.appendChild(type);
        overlay.appendChild(classifier);
        overlay.appendChild(targetPath);

        plugin.appendChild(configuration);

        jsclientPlugins.appendChild(plugin);
      } else {
        getOutput().showMessage("The jsclient plugin was already added to server pom.xml");
      }
    } catch (Exception e) {
      getOutput().showError("Adding jsclient plugin in server pom. " + e.getMessage());
      throw e;
    }
  }

  private void applyChangesToPom(Document doc, File serverPom) throws Exception {

    try {

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(new File(serverPom.getPath()));
      transformer.transform(source, result);

    } catch (Exception e) {
      getOutput().showError("Applying changes to server pom. " + e.getMessage());
      throw e;
    }
  }

  private boolean xmlAlreadyConfigured(Node node, String[] terms) {

    boolean result = true;
    for (int i = 0; i < terms.length; i++) {
      if (!node.getTextContent().contains(terms[i])) {
        result = false;
        break;
      }
    }

    return result;
  }

  // /**
  // * @param filepath
  // * @param baseUrl
  // * @param context
  // */
  // @SuppressWarnings("unchecked")
  // public void modifyJsonFile(String filepath, String baseUrl, String context) {
  //
  // JSONParser parser = new JSONParser();
  // try {
  // Object obj = parser.parse(new FileReader(filepath));
  // JSONObject jsonObject = (JSONObject) obj;
  //
  // JSONObject proxy = (JSONObject) jsonObject.get("proxy");
  //
  // proxy.put("baseUrl", baseUrl);
  // proxy.put("context", context);
  //
  // jsonObject.put("proxy", proxy);
  // FileWriter file = new FileWriter(filepath);
  // file.write(jsonObject.toJSONString().replace("\\/", "/"));
  // file.flush();
  // file.close();
  // getOutput().showMessage("Modified baseUrl and context property from config.json file");
  // } catch (Exception e) {
  //
  // getOutput().showError("An error occurred during the execution of project deploy command. " + e.getMessage());
  // }
  // }
  //
  // private void modifyJsFiles(String serverUrl, String js_file_path) {
  //
  // final String content = "window.Config.server =" + serverUrl + ";" + "\n" + "window.Config.CORSEnabled = true;";
  // final String content1 =
  // "window.Config = {" + "\n" + " defaultLocale: 'en_EN'," + "\n" + "supportedLocales: ['en_EN', 'es_ES']," + "\n"
  // + "server: 'http://devon-ci.cloudapp.net" + serverUrl + "," + "\n CORSEnabled: true\n };";
  //
  // try {
  // FileUtils.writeStringToFile(new File(js_file_path + "\\ConfigDevelopment.js"), content);
  //
  // FileUtils.writeStringToFile(new File(js_file_path + "\\Config.js"), content1);
  //
  // getOutput().showMessage("Modified server URL from config.js and configdevelopment.js file");
  //
  // } catch (Exception e) {
  // getOutput().showError("An error occurred during the execution of project deploy command. " + e.getMessage());
  // }
  //
  // }

  private void commentNode(Document doc, Node parent, Node nodeToComment) {

    try {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      StreamResult result = new StreamResult(new StringWriter());
      DOMSource source = new DOMSource(nodeToComment);
      transformer.transform(source, result);

      String nodeContent = result.getWriter().toString();

      Comment comment = doc.createComment(nodeContent);
      parent.insertBefore(comment, nodeToComment);
      parent.removeChild(nodeToComment);
    } catch (Exception e) {
      getOutput().showError(" in commentNode(). " + e.getMessage());
    }

  }

  private void createSenchaWs(String location, String distributionPath) throws Exception {

    try {
      Optional<com.devonfw.devcon.common.api.Command> copySenchaWorkspace = getCommand(this.SENCHA, this.WORKSPACE);
      if (copySenchaWorkspace.isPresent()) {
        File newWorkspace = new File(location);
        if (!newWorkspace.exists()) {
          newWorkspace.mkdirs();
        }
        copySenchaWorkspace.get().exec(newWorkspace.getAbsolutePath(), distributionPath);
      } else {
        getOutput().showError("No command copyworkspace found for sencha module.");
        return;
      }
    } catch (Exception e) {
      getOutput().showError("in createSenchaWs");
      throw e;
    }

  }

  private void createSenchaApp(String clientname, String workspacepath) throws Exception {

    try {
      Optional<com.devonfw.devcon.common.api.Command> createSenchaApp = getCommand(this.SENCHA, this.CREATE);
      if (createSenchaApp.isPresent()) {
        createSenchaApp.get().exec(clientname, workspacepath);
      } else {
        getOutput().showError("No command create found for sencha module.");
        return;
      }
    } catch (Exception e) {
      getOutput().showError("in createSenchaApp");
      throw e;
    }

  }

}
