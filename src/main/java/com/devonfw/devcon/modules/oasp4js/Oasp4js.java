package com.devonfw.devcon.modules.oasp4js;

import java.io.File;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

/**
 * Module with tasks related to oasp4js (Angular client)
 *
 * @author pparrado
 */

@CmdModuleRegistry(name = "oasp4js", description = "Module to automate tasks related to oasp4js")
public class Oasp4js extends AbstractCommandModule {

  private static String GULP_SERV = "cmd /c start gulp serve";

  private static String OASP4JS_BASE = "software\\nodejs\\oasp4js_base";

  @Command(name = "create", description = "This command creates a basic Oasp4js app")
  @Parameters(values = { @Parameter(name = "clientname", description = "The name for the project"),
  @Parameter(name = "clientpath", description = "The location for the new project", optional = true) })
  public void create(String clientname, String clientpath) {

    getOutput().showMessage("Creating project " + clientname + "...");

    try {

      Optional<DistributionInfo> distInfo = getContextPathInfo().getDistributionRoot();
      clientpath = clientpath.isEmpty() ? this.contextPathInfo.getCurrentWorkingDirectory().toString() : clientpath;

      if (distInfo.isPresent()) {

        String projectPath = clientpath + File.separator + clientname;
        File projectFile = new File(projectPath);
        if (projectFile.exists()) {
          getOutput()
              .showError("The project " + projectPath + " already exists. Please delete it or choose other location.");
        } else {

          File templateFile = new File(distInfo.get().getPath().toString() + File.separator + OASP4JS_BASE);
          if (templateFile.exists()) {

            FileUtils.copyDirectory(templateFile, projectFile, false);

            getOutput().showMessage("Adding devon.json file...");
            Utils.addDevonJsonFile(projectFile.toPath(), ProjectType.OASP4JS);
            getOutput().showMessage("Editing java/pom.xml...");
            editPom(projectFile.toPath(), clientname);

            getOutput().showMessage(
                "Project created successfully. Please launch 'npm install' to resolve the project dependencies.");
          } else {
            getOutput().showError("Base project " + OASP4JS_BASE + " not found.");
          }
        }
      } else {
        getOutput().showError("Seems that you are not in a Devon distribution.");
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during execution of create command. " + e.getMessage());
    }
  }

  @Command(name = "build", description = "This command will build the server project", context = ContextType.PROJECT)
  public void build() {

    try {

      // this.projectInfo = getContextPathInfo().getProjectRoot(path);
      if (!this.projectInfo.isPresent()) {
        getOutput().showError("Not in a project or -path param not pointing to a project");
        return;
      }

      Process p;
      if (this.projectInfo.get().getProjecType().equals(ProjectType.OASP4JS)) {
        try {
          String cmd = "cmd /c start npm install";

          p = Runtime.getRuntime().exec(cmd, null, this.projectInfo.get().getPath().toFile());
          p.waitFor();
          getOutput().showMessage("Completed");
        } catch (Exception e) {

          getOutput().showError(
              "Seems that you are not in a OASP4JS project. Please verify the devon.json configuration file");
        }
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during execution of build command. " + e.getMessage());
    }
  }

  @Command(name = "run", description = "This command runs a debug build of oasp4js", context = ContextType.PROJECT)
  public void run() {

    try {
      if (!this.projectInfo.isPresent()) {
        getOutput().showError("Not in a project or -path param not pointing to a project");
        return;
      }

      if (this.projectInfo.isPresent()) {
        if (this.projectInfo.get().getProjecType().equals(ProjectType.OASP4JS)) {

          Process p;
          p = Runtime.getRuntime().exec(GULP_SERV, null, new File(this.projectInfo.get().getPath().toString()));
          p.waitFor();
          getOutput().showMessage("Starting application");

        } else {
          getOutput().showError(
              "Seems that you are not in a OASP4JS project. Please verify the devon.json configuration file");
        }

      } else {
        getOutput().showError("devon.json configuration file not found.");
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during execution of run command. " + e.getMessage());
    }
  }

  private void editPom(Path project, String clientname) throws Exception {

    try {
      File pom = new File(project.toString() + File.separator + "java" + File.separator + "pom.xml");
      if (pom.exists()) {
        // getting the pom content
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(pom);
        doc.getDocumentElement().normalize();

        // setting the artifactId
        Node artifactId = doc.getElementsByTagName("artifactId").item(0);
        artifactId.setTextContent(clientname);

        // writing changes
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(pom.getPath()));
        transformer.transform(source, result);

      } else {
        getOutput().showError(pom.toString() + " not found. You may need to configure it manually.");
      }
    } catch (Exception e) {
      throw e;
    }

  }

}
