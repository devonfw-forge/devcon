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
package com.devonfw.devcon.modules.devon4j;

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
import com.devonfw.devcon.common.api.annotations.InputType;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.api.data.ProjectInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Utils;
import com.devonfw.devcon.modules.devon4j.migrate.Migrations;
import com.devonfw.devcon.modules.devon4j.migrate.Migrator;
import com.google.common.base.Optional;

/**
 * This class implements a Command Module with Devon4j(server project) related commands
 *
 * @author ssarmoka
 */
@CmdModuleRegistry(name = "devon4j", description = "devon4j (Java server project) related commands", sort = 3)
public class Devon4j extends AbstractCommandModule {

  /**
   * The constructor.
   */
  public Devon4j() {

    super();
  }

  /**
   * @param serverpath Path to Server Project
   * @param servername Name of Server Project
   * @param packagename Package Name of Server Project
   * @param groupid Group Id of the Server Project
   * @param version Version of the Server Project
   * @param dbType the database type (e.g. "postgresql", "hana", "oracle", etc.)
   * @throws Exception on error.
   */
  @Command(name = "create", description = "This creates a new server project based on devon4j template")
  @Parameters(values = {
  @Parameter(name = "serverpath", description = "where to create", optional = true, inputType = @InputType(name = InputTypeNames.PATH)),
  @Parameter(name = "servername", description = "Name of project"),
  @Parameter(name = "packagename", description = "package name in server project"),
  @Parameter(name = "groupid", description = "groupid for server project"),
  @Parameter(name = "version", description = "version of server project"),
  @Parameter(name = "dbtype", description = "database type in server project(h2|postgresql|mysql|mariadb|oracle|hana|db2)") })
  public void create(String serverpath, String servername, String packagename, String groupid, String version,
      String dbType) throws Exception {

    serverpath = serverpath.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString() : serverpath;

    String devonTemplateVersion = Utils.getTemplateVersion(
        Utils.addTrailingSlash(Utils.removeEndingDot(serverpath)) + Constants.VERSION_PARAMS_FILE_FULL_PATH);
    if (devonTemplateVersion.isEmpty())
      this.output.showError(
          "Devon template version not found neither in config file '{devonfwPath}/conf/version.json' nor Internet. Please, go online or setup the config file correctly.");

    this.output.showMessage("Using the devon template version: " + devonTemplateVersion);

    String baseCommand = new StringBuffer("mvn -DarchetypeVersion=").append(devonTemplateVersion)
        .append(" -DarchetypeGroupId=").append(Constants.DEVON_TEMPLATE_GROUP_ID).append(" -DarchetypeArtifactId=")
        .append(Constants.DEVON_ARTIFACT_ID).append(" archetype:generate -DgroupId=").append(groupid)
        .append(" -DartifactId=").append(servername).append(" -Dversion=").append(version).append(" -Dpackage=")
        .append(packagename).append(" -DdbType=").append(dbType).append(" -DinteractiveMode=false").toString();

    getOutput().showMessage("Command executed to create project is -- " + baseCommand);

    File projectDir = new File(serverpath);

    if (!projectDir.exists()) {
      projectDir.mkdirs();
    }
    File project = new File(serverpath + File.separator + servername);

    if (!project.exists()) {

      Process process = null;

      try {

        if (SystemUtils.IS_OS_WINDOWS) {
          process = Runtime.getRuntime().exec(Constants.WINDOWS_CMD_PROMPT + baseCommand, null, new File(serverpath));
        } else if (SystemUtils.IS_OS_LINUX) {
          String args[] = new String[] { Constants.LINUX_BASH, "-c", baseCommand };
          process = Runtime.getRuntime().exec(args, null, new File(serverpath));
        } else {
          throw new IllegalStateException("Unsupported OS!");
        }
        final InputStream isError = process.getErrorStream();
        final InputStream isOutput = process.getInputStream();

        Utils.processErrorAndOutPut(isError, isOutput, this.output);

        int result = process.waitFor();
        if (result == 0) {
          getOutput().showMessage("Adding devon.json file...");
          Utils.addDevonJsonFile(project.toPath(), ProjectType.DEVON4J);

          if (Integer.parseInt(devonTemplateVersion.replaceAll("\\.", "")) <= new Integer("211")) {
            modifyPom(serverpath + File.separator + servername + File.separator + "server" + File.separator + "pom.xml",
                packagename);
          }

          getOutput().showMessage("devon4j project created successfully");

        } else {
          throw new Exception("Project creation failed");
        }

      } catch (Exception e) {
        e.printStackTrace();
        getOutput().showError("Error creating workspace: " + e.getMessage());
      }

    } else {
      getOutput().showError("The project " + project.toString() + " already exists!");
    }

  }

  /**
   * Run Devon4j Project from the command line ContextType Project makes this into a "special" command which gets an
   * extra parameter '-path' allowing to specify the project root (in reality, any directory below the root is valid as
   * well) Alternatively, the current dir is used. When the file devon.json is found at the project root, it is
   * available as type ProjectInfo in the field propertyInfo. Apart from "version" and "type" (default properties) *ANY*
   * property can be specified
   *
   * @param port Server will be started at this port
   */
  @Command(name = "run", description = "This command runs the application from spring boot embedded tomcat", context = ContextType.PROJECT)
  @Parameters(values = {
  @Parameter(name = "port", description = "Port to start Spring boot app (port 8081 by default)", optional = true) })
  public void run(String port) {

    Process process = null;
    if (!this.projectInfo.isPresent()) {
      getOutput().showError("Not in a project or -path param not pointing to a project");
      return;
    }

    ProjectInfo info = this.projectInfo.get();

    // Get port from a) parameter or b) devon.json file or c) default value passed as 2nd paranter to info.getProperty

    try {
      String port_ = (port.isEmpty()) ? info.getProperty("port", "8081").toString() : port.trim();
      String path_ = info.getPath().toString() + File.separator + "server";

      if (SystemUtils.IS_OS_WINDOWS) {
        String baseCommand = "mvn spring-boot:run -Drun.jvmArguments=\"-Dserver.port=" + port_ + "\"";
        process = Runtime.getRuntime().exec(Constants.WINDOWS_CMD_PROMPT + baseCommand, null, new File(path_));
      } else if (SystemUtils.IS_OS_LINUX) {
        String baseCommand = "mvn spring-boot:run -Drun.jvmArguments='-Dserver.port=" + port_ + "'";
        String args[] = new String[] { Constants.LINUX_BASH, "-c", baseCommand };
        process = Runtime.getRuntime().exec(args, null, new File(path_));
      } else {
        throw new IllegalStateException("Unsupported OS!");
      }

      final InputStream isError = process.getErrorStream();
      final InputStream isOutput = process.getInputStream();

      Utils.processOutput(isError, isOutput, this.output);

    } catch (Exception e) {

      getOutput().showError("An error occured during executing devon4j Cmd: %s", e.getMessage());
    }
  }

  /**
   * Builds the project using maven.
   */
  @Command(name = "build", description = "This command will build the server project", context = ContextType.PROJECT)
  public void build() {

    // Check projectInfo loaded. If not, abort
    if (!this.projectInfo.isPresent()) {
      getOutput().showError("Not in a project or -path param not pointing to a project");
      return;
    }

    Process p = null;
    try {

      String baseCommand = "mvn clean install";
      if (SystemUtils.IS_OS_WINDOWS) {
        p = Runtime.getRuntime().exec(Constants.WINDOWS_CMD_PROMPT + baseCommand, null,
            this.projectInfo.get().getPath().toFile());
      } else if (SystemUtils.IS_OS_LINUX) {
        String args[] = new String[] { Constants.LINUX_BASH, "-c", baseCommand };
        p = Runtime.getRuntime().exec(args, null, this.projectInfo.get().getPath().toFile());
      } else {
        throw new IllegalStateException("Unsupported OS!");
      }

      final InputStream isError = p.getErrorStream();
      final InputStream isOutput = p.getInputStream();

      Utils.processOutput(isError, isOutput, this.output);

    } catch (Exception e) {
      getOutput().showError("An error occured during executing devon4j Cmd" + e.getMessage());
    }
  }

  /**
   * @param tomcatPath Path to tomcat
   */
  @Command(name = "deploy", description = "This command will deploy the server project on tomcat", context = ContextType.PROJECT)
  @Parameters(values = {
  @Parameter(name = "tomcatpath", description = "Path to tomcat folder (if not provided and the project is in a Devonfw distribution the default software/tomcat folder will be used)", optional = true, inputType = @InputType(name = InputTypeNames.PATH)) })
  public void deploy(String tomcatPath) {

    String path;
    try {

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

        tomcatPath = tomcatPath.isEmpty()
            ? distInfo.get().getPath().toFile().toString() + File.separator + "software" + File.separator + "tomcat"
            : tomcatPath;

        File tomcatDir = new File(tomcatPath);

        if (!tomcatDir.exists()) {
          getOutput().showError("Tomcat directory " + tomcatDir.toString() + " not found.");
          return;

        }

        File newTomcat4app = new File(tomcatPath + "_" + appName.get().toString());

        if (!newTomcat4app.exists()) {
          newTomcat4app.mkdirs();
          FileUtils.copyDirectory(tomcatDir, newTomcat4app);
        } else {
          getOutput().showMessage("Tomcat " + newTomcat4app.getAbsolutePath().toString() + " already exists.");
        }

        File project = new File(path);

        if (project.exists()) {

          // PACKAGING THE APP (creating the .war file)

          File mvnBat = null;
          if (SystemUtils.IS_OS_WINDOWS) {
            mvnBat = new File(distInfo.get().getPath().toString() + File.separator + "software\\maven\\bin\\mvn.bat");
          } else if (SystemUtils.IS_OS_LINUX) {
            mvnBat = new File(distInfo.get().getPath().toString() + File.separator + "software" + File.separator
                + "maven" + File.separator + "bin" + File.separator + "mvn.cmd");
          } else {
            throw new IllegalStateException("Unsupported OS!");
          }

          ProcessBuilder processBuilder = null;
          File startTomcatBat = null;
          if (mvnBat.exists()) {
            if (SystemUtils.IS_OS_WINDOWS) {
              startTomcatBat = new File(newTomcat4app + File.separator + "bin" + File.separator + "startup.bat");
              processBuilder = new ProcessBuilder(mvnBat.getAbsolutePath(), "package");
            } else if (SystemUtils.IS_OS_LINUX) {
              startTomcatBat = new File(newTomcat4app + File.separator + "bin" + File.separator + "startup.sh");
              String args[] = new String[] { Constants.LINUX_BASH, "-c", mvnBat.getAbsolutePath(), "package" };
              processBuilder = new ProcessBuilder(args);
            } else {
              throw new IllegalStateException("Unsupported OS!");
            }
            processBuilder.directory(project);
            Process process = processBuilder.start();

            final InputStream isError = process.getErrorStream();
            final InputStream isOutput = process.getInputStream();

            Utils.processErrorAndOutPut(isError, isOutput, getOutput());

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

                  ProcessBuilder tomcatProcessBuilder = null;
                  if (startTomcatBat.exists()) {

                    if (SystemUtils.IS_OS_WINDOWS) {
                      tomcatProcessBuilder = new ProcessBuilder(startTomcatBat.getAbsolutePath());
                      tomcatProcessBuilder.directory(new File(newTomcat4app + File.separator + "bin"));
                    } else if (SystemUtils.IS_OS_LINUX) {
                      String args1[] = new String[] { Constants.LINUX_BASH, "-c", "sh catalina.sh start" };
                      tomcatProcessBuilder = new ProcessBuilder(args1);
                      tomcatProcessBuilder.directory(new File(newTomcat4app + File.separator + "bin"));
                    } else {
                      throw new IllegalStateException("Unsupported OS!");
                    }

                    Process tomcatProcess = tomcatProcessBuilder.start();

                    final InputStream isTomcatError = tomcatProcess.getErrorStream();
                    final InputStream isTomcatOutput = tomcatProcess.getInputStream();

                    Utils.processErrorAndOutPut(isTomcatError, isTomcatOutput, getOutput());

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
      getOutput().showError("In devon4j deploy command. " + e.getMessage());
    }
  }

  /**
   * Migrates an oasp4j or devon4j project to the latest version.
   *
   * @param projectPath the path of the project to migrate.
   * @param singleVersion - {@code true} to only migrate to the next version, {@code false} otherwise (migrate to latest
   *        version).
   */
  @Command(name = "migrate", description = "This command will migrate the project to latest version")
  @Parameters(values = {
  @Parameter(name = "projectPath", description = "Path to project folder", optional = false, inputType = @InputType(name = InputTypeNames.PATH)),
  @Parameter(name = "singleVersion", description = "Migrate only to next version (rather than the latest version)", optional = true, inputType = @InputType(name = InputTypeNames.BOOLEAN)), })
  public void migrate(String projectPath, String singleVersion) {

    try {
      Migrator migrator = Migrations.devon4j(getOutput());
      migrator.migrate(new File(projectPath), "true".equals(singleVersion));
    } catch (Exception e) {
      getOutput().showError("Migration failed.", e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * @param path the {@link File} to read.
   * @return the content of the {@link File}.
   * @throws IOException on error.
   */
  public String readFile(File path) throws IOException {

    StringBuilder sb = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {

      String sCurrentLine;
      while ((sCurrentLine = br.readLine()) != null) {
        sb.append(sCurrentLine);
      }

    }

    return sb.toString();
  }

  private File getWarFile(Path server) {

    File warFile = null;
    File serverTarget = new File(server.toFile().getAbsolutePath() + File.separator + "target");
    if (serverTarget.exists()) {
      Collection<File> warFiles = FileUtils.listFiles(serverTarget, new WildcardFileFilter("*.war*"),
          TrueFileFilter.TRUE);

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
      getOutput().showError("Error executing devon4j command " + e.getMessage());

    }

    try {

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(fXmlFile);
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
