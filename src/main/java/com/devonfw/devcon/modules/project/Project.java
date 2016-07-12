package com.devonfw.devcon.modules.project;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.tuple.Triple;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.google.common.base.Optional;

/**
 * Module to automate tasks related to devonfw projects (server + client)
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "project", description = "Module to automate tasks related to the devon projects (server + client)", deprecated = false)
public class Project extends AbstractCommandModule {

  private final String DEVON4SENCHA = "devon4sencha";

  private final String OASP4J = "oasp4j";

  private final String OASP4JS = "oasp4js";

  private final String SENCHA = "sencha";

  private final String CREATE = "create";

  private final String DEPLOY = "deploy";

  private final String WORKSPACE = "workspace";

  private final String POM_XML = "pom.xml";

  @Command(name = "build", description = "This command will build the server & client project(unified server and client build)", context = ContextType.COMBINEDPROJECT)
  @Parameters(values = {
  @Parameter(name = "serverpath", description = "Path to Server project Workspace (currentDir if not given)", optional = true),
  @Parameter(name = "clienttype", description = "This parameter shows which type of client is integrated with server i.e oasp4js or sencha", optional = false),
  @Parameter(name = "clientpath", description = "path to client directory", optional = false) })
  public void build(String serverpath, String clienttype, String clientpath) {
  

    Optional<ProjectInfo> projectInfo = getContextPathInfo().getProjectRoot(serverpath);

    try {
      Optional<com.devonfw.devcon.common.api.Command> oasp4j = getCommand("oasp4j", "build");
      oasp4j.get().exec(serverpath);
      switch (clienttype == null ? "" : clienttype) {
      case "oasp4js":
        Optional<com.devonfw.devcon.common.api.Command> oasp4js_cmd = getCommand("oasp4js", "build");
        oasp4js_cmd.get().exec(clientpath);
        break;
      case "sencha":
        Optional<com.devonfw.devcon.common.api.Command> sencha_cmd = getCommand("sencha", "build");
        sencha_cmd.get().exec(clientpath);

        break;
      case "":
        getOutput()
            .showError("Clienttype is not specified cannot build client. Please set client type to oasp4js or Sencha");
      }
    } catch (Exception e) {
      getOutput().showError("An error occured during executing Project Cmd");
    }

  }

  @Command(name = "create", description = "This command is used to create new combined server & client project")
  @Parameters(values = {
  @Parameter(name = "distributionpath", description = "path to the Devonfw distribution (currentDir if not given)", optional = true),
  @Parameter(name = "servername", description = "name for the server project"),
  @Parameter(name = "packagename", description = "package name for the server project"),
  @Parameter(name = "groupid", description = "groupid for server project"),
  @Parameter(name = "version", description = "version of server project"),
  @Parameter(name = "clienttype", description = "type of the client project: 'devon4sencha' or 'oasp4js'"),
  @Parameter(name = "clientname", description = "name for the client project"),
  @Parameter(name = "clientpath", description = "path where the client project will be created. In case of sencha project this must point to a Sencha workspace.", optional = true),
  @Parameter(name = "gituser", description = "Only for client type 'devon4sencha': a user with permissions to download the Devon distribution.", optional = true),
  @Parameter(name = "gitpassword", description = "Only for client type 'devon4sencha': the password related to the user with permissions to download the Devon distribution", optional = true),
  @Parameter(name = "gitfolder", description = "Only for client type 'devon4sencha': GIT BIN/CMD folder where git executable is present", optional = true) })
  public void create(String distributionpath, String servername, String packagename, String groupid, String version,
      String clienttype, String clientname, String clientpath, String gituser, String gitpassword, String gitfolder) {

    try {

      Optional<com.devonfw.devcon.common.api.Command> createServer = getCommand(this.OASP4J, this.CREATE);

      if (createServer.isPresent()) {
        createServer.get().exec(distributionpath, servername, packagename, groupid, version);
      } else {
        getOutput().showError("No command create found for oasp4j module.");
      }

      getOutput().showMessage("Creating client project...");
      if (clienttype.equals(this.DEVON4SENCHA)) {

        Optional<com.devonfw.devcon.common.api.Command> createSenchaWorkspace = getCommand(this.SENCHA, this.WORKSPACE);
        if (createSenchaWorkspace.isPresent()) {
          createSenchaWorkspace.get().exec(this.DEVON4SENCHA, clientpath, gituser, gitpassword, gitfolder);
        } else {
          getOutput().showError("No command workspace found for sencha module.");
        }

        Optional<com.devonfw.devcon.common.api.Command> createSenchaApp = getCommand(this.SENCHA, this.CREATE);
        if (createSenchaApp.isPresent()) {
          createSenchaApp.get().exec(clientname, clientpath + File.separator + this.DEVON4SENCHA);
        } else {
          getOutput().showError("No command create found for sencha module.");
        }

      } else if (clienttype.equals(this.OASP4JS)) {

        Optional<com.devonfw.devcon.common.api.Command> createOasp4js = getCommand(this.OASP4JS, this.CREATE);

        if (createOasp4js.isPresent()) {
          createOasp4js.get().exec(clientname, clientpath);
        } else {
          getOutput().showError("No command create found for oasp4js module.");
        }

      } else {
        getOutput()
            .showError(
                "The parameter value for 'clienttype' is not valid. The options for this parameter are: 'devon4sencha' and 'oasp4js'.");
      }

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

  @Parameter(name = "clienttype", description = "This parameter shows which type of client is integrated with server i.e oasp4js or sencha", optional = false),
  @Parameter(name = "clientport", description = "User can configured port if client type is Sencha", optional = true),
  @Parameter(name = "clientpath", description = "Port to start spring boot server", optional = true),
  @Parameter(name = "serverport", description = "Port to start client", optional = true),
  @Parameter(name = "serverpath", description = "Path to Server project Workspace (currentDir if not given)", optional = true) })
  public void run(String clienttype, String clientport, String clientpath, String serverport, String serverpath) {

    this.projectInfo = getContextPathInfo().getProjectRoot(serverpath);
    try {
      Optional<com.devonfw.devcon.common.api.Command> cmd = getCommand(Constants.OASP4J, Constants.RUN);
      cmd.get().exec(serverport, serverpath);

      switch (clienttype == null ? "" : clienttype) {
      case "oasp4js":
        Optional<com.devonfw.devcon.common.api.Command> oasp4js_cmd = getCommand(Constants.OASP4JS, Constants.RUN);
        oasp4js_cmd.get().exec(clientpath);
        break;
      case "sencha":
        Optional<com.devonfw.devcon.common.api.Command> sencha_cmd = getCommand(Constants.SENCHA, Constants.RUN);
        sencha_cmd.get().exec(clientport, clientpath);
        break;
      case "":
        getOutput()
            .showError("Clienttype is not specified cannot build client. Please set client type to oasp4js or Sencha");
      }
    } catch (Exception e) {
      getOutput().showError("An error occured during executing Project Cmd");
    }
  }

  @Command(name = "deploy", description = "This command is to automate the deploy process of a combined server & client project")
  @Parameters(values = { @Parameter(name = "tomcatpath", description = "Path to tomcat folder"),
  @Parameter(name = "distributionpath", description = "path to the Devonfw distribution (currentDir if not given)") })
  public void deploy(String tomcatpath, String distributionpath) {

    ProjectType clientType = ProjectType.OASP4JS;

    try {

      String serverPath = "D:\\zTest\\devon-dist\\workspaces\\oasp4j\\samples\\server";
      String clientPath = "D:\\zTest\\devon-dist\\workspaces\\devon4sencha\\ExtSample";

      this.projectInfo = getContextPathInfo().getProjectRoot(clientPath);
      if (this.projectInfo.isPresent()) {
        if (this.projectInfo.get().getProjecType().equals(ProjectType.DEVON4SENCHA)) {
          clientType = ProjectType.DEVON4SENCHA;
        }
      } else {
        getOutput().showError("devon.json configuration file not found for client project.");
      }

      configureServerPOM(serverPath, clientPath, clientType);

      // Optional<com.devonfw.devcon.common.api.Command> deploy = getCommand(this.OASP4J, this.DEPLOY);
      // if (deploy.isPresent()) {
      // deploy.get().exec(tomcatpath, distributionpath);
      // } else {
      // getOutput().showError("No command deploy found for oasp4j module.");
      // }

    } catch (Exception e) {
      getOutput().showError("An error occurred during the execution of project deploy command. " + e.getMessage());

    }

  }

  public void configureServerPOM(String serverPath, String clientPath, ProjectType clientType) throws Exception {

    try {

      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

      Triple<String, String, String> clientPomInfo = getClientPomInfo(clientPath, docBuilder);

      if (clientPomInfo != null) {
        editServerPom(serverPath, docBuilder, clientPomInfo, clientType);

      } else {
        getOutput().showError(
            "The pom.xml in the java directory of the client project is not found or has some missing information.");
      }

    } catch (Exception e) {
      throw e;
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

        Node jsclientPlugins = getJsclientPluginsNode(doc);

        if (jsclientPlugins == null) {
          throw new Exception("No 'plugins' node found for jsclient profile in the server pom.xml");
        }

        // IF SENCHA PROJECT, DELETE EXECUTIONS IN jsclient PROFILE, IN exec-maven-plugin PLUGIN
        if (clientType.equals(ProjectType.DEVON4SENCHA)) {
          Node execMavenPlugin = getExecMavenPluginNode(jsclientPlugins);
          if (execMavenPlugin != null) {
            jsclientPlugins.removeChild(execMavenPlugin);
            // StringWriter sw = new StringWriter();
            // TransformerFactory transformerFactory = TransformerFactory.newInstance();
            // Transformer transformer = transformerFactory.newTransformer();
            // transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            // // transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            // transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            // DOMSource source = new DOMSource(execMavenPlugin);
            // StreamResult result = new StreamResult(sw);
            // transformer.transform(source, result);
            // // sw.append(" -->");
            // String comment = "<!-- " + sw.toString() + " -->";
            // jsclientPlugins.appendChild(doc.createTextNode(comment).);

          }
        }
        addJsclientPlugin(doc, jsclientPlugins, clientPomInfo);
        applyChangesToPom(doc, serverPom);

        getOutput().showMessage("Server pom.xml configured.");
      }

    } catch (Exception e) {
      throw e;
    }
  }

  private Node getJsclientPluginsNode(Document doc) {

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
      return pluginsNode;
    } catch (Exception e) {
      getOutput().showError("In getPlugins method for jsclient profile. " + e.getMessage());
      return null;
    }
  }

  private Node getExecMavenPluginNode(Node pluginsNode) {

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

      return execMavenPlugin;

    } catch (Exception e) {
      getOutput().showError("In getExecMavenPluginNode method. " + e.getMessage());
      return null;
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
      // StreamResult result = new StreamResult(new File(serverPom.getPath()));
      StreamResult result = new StreamResult(new File("D:\\result.xml"));
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

}
