package com.devonfw.devcon.modules.oasp4js;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.InputType;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.api.data.ProjectType;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.utils.Downloader;
import com.devonfw.devcon.common.utils.Extractor;
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

  private static String[] STATE = { "successfully", "failed" };

  private static String OASP4JS_BASE = "software\\nodejs\\oasp4js_base";

  private static String OASP4JS_ang1_ID = "oasp4js_ang1_id";

  private static String OASP4JS_ang2_ID = "oasp4js_ang2_id";

  @Command(name = "create", description = "This command creates a basic Oasp4js app")
  @Parameters(values = { @Parameter(name = "clientname", description = "The name for the project"),
  @Parameter(name = "clientpath", description = "The location for the new project", optional = true, inputType = @InputType(name = InputTypeNames.PATH)) })
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

          String cmd = "cmd /c ng new " + clientname;
          Process p = Runtime.getRuntime().exec(cmd, null, new File(clientpath));

          String line;
          BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
          while ((line = in.readLine()) != null) {
            getOutput().showMessage(line);
          }
          in.close();
          int result = p.exitValue();
          if (result == 0) {
            getOutput().showMessage("Adding devon.json file...");
            Utils.addDevonJsonFile(projectFile.toPath(), ProjectType.OASP4JS);
          }

          getOutput().showMessage("Project build " + STATE[result]);

        }
      } else {
        getOutput().showError("Seems that you are not in a Devon distribution.");
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during execution of create command. " + e.getMessage());
    }
  }

  @Command(name = "build", description = "This command will build the oasp4js project.", context = ContextType.PROJECT)
  public void build() {

    try {

      if (!this.projectInfo.isPresent()) {
        getOutput().showError("Not in a project or -path param not pointing to a project");
        return;
      }

      Process p;
      if (this.projectInfo.get().getProjecType().equals(ProjectType.OASP4JS)) {
        try {
          String cmd = "cmd /c ng build --progress false";

          p = Runtime.getRuntime().exec(cmd, null, this.projectInfo.get().getPath().toFile());
          getOutput().showError("Building project...");
          String line;
          BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
          while ((line = in.readLine()) != null) {
            System.out.println(line);
            getOutput().showMessage(line);
          }
          in.close();
          int result = p.exitValue();

          getOutput().showMessage("Project build " + STATE[result]);

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
          String cmd = "cmd /c ng serve --progress false";
          p = Runtime.getRuntime().exec(cmd, null, this.projectInfo.get().getPath().toFile());
          getOutput().showMessage("Project starting");
          String line;
          BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
          while ((line = in.readLine()) != null) {
            System.out.println(line);
            getOutput().showMessage(line);
          }
          in.close();
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

  @Command(name = "jumpstart", description = "This command downloads the Oasp4js sample app with all its dependencies from Teamforge", context = ContextType.NONE)
  @Parameters(values = {
  @Parameter(name = "path", description = "a location for the Devon distribution download", optional = true, inputType = @InputType(name = InputTypeNames.PATH)),
  @Parameter(name = "angularVersion", description = "the version of the , the options are: \n '1' to download OASP4js based on Angular 1 \n '2' to download OASP4js based on Angular 2", optional = true, inputType = @InputType(name = InputTypeNames.LIST, values = {
  "1", "2" })), @Parameter(name = "user", description = "a user with download permissions in Teamforge"),
  @Parameter(name = "password", description = "the password related to the user with download permissions", inputType = @InputType(name = InputTypeNames.PASSWORD)) })
  public void jumpstart(String path, String angularVersion, String user, String password) {

    Optional<String> frsFileId;

    // Default parameters
    path = path.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString() : path.trim();
    angularVersion = angularVersion.isEmpty() ? "1" : angularVersion.trim();

    this.output.status("Downloading file...");

    try {

      if (angularVersion.equals("1")) {
        // frsFileId = OASP4JS_ang1;
        frsFileId = Downloader.getDevconConfigProperty(OASP4JS_ang1_ID);
        if (!frsFileId.isPresent())
          throw new Exception("Property " + OASP4JS_ang1_ID + " not found.");
      } else if (angularVersion.equals("2")) {
        // frsFileId = OASP4JS_ang2;
        frsFileId = Downloader.getDevconConfigProperty(OASP4JS_ang2_ID);
        if (!frsFileId.isPresent())
          throw new Exception("Property " + OASP4JS_ang2_ID + " not found.");
      } else {
        throw new Exception("The value for the parameter 'angularVersion' is invalid.");
      }

      Optional<String> fileDownloaded = Downloader.downloadFromTeamForge(path, user, password, frsFileId.get());

      if (fileDownloaded.isPresent()) {
        Extractor.unZip(path + File.separator + fileDownloaded.get().toString(), path);

        this.output.showMessage("File successfully downloaded.");

      } else {
        throw new Exception("An error occurred while downloading the file.");
      }

    } catch (Exception e) {
      getOutput().showError("Download Failed." + e.getMessage());
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
