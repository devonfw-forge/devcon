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
package com.devonfw.devcon.modules.project;

import java.io.File;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.InputType;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.ContextType;
import com.devonfw.devcon.common.api.data.InputTypeNames;
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
@CmdModuleRegistry(name = "project", description = "Module to automate tasks related to the devon projects (server + client)", visible = true)
public class Project extends AbstractCommandModule {

  private final String DEVON4J = "devon4j";

  private final String DEVON4NG = "devon4ng";

  private final String CREATE = "create";

  private final String DEPLOY = "deploy";

  private final String WORKSPACE = "copyworkspace";

  private final String POM_XML = "pom.xml";

  @Command(name = "build", description = "This command will build the server & client project (unified server and client build)", context = ContextType.COMBINEDPROJECT)
  // @Parameters(values = {
  // @Parameter(name = "clientpath", description = "path to client directory", optional = true, inputType =
  // @InputType(name = InputTypeNames.PATH)) })
  public void build() {

    if (!this.projectInfo.isPresent()) {
      getOutput().showError("Not in a project or -path param not pointing to a project");
      return;
    }
    try {

      int size = this.projectInfo.get().getSubProjects().size();

      for (int i = 0; i < size; i++) {
        ProjectInfo p = this.projectInfo.get().getSubProjects().get(i);

        if (p.getProjecType() == ProjectType.DEVON4J) {
          Optional<com.devonfw.devcon.common.api.Command> devon4j = getCommand("devon4j", "build", p);
          devon4j.get().exec();
        }
        if (p.getProjecType() == ProjectType.DEVON4NG) {
          Optional<com.devonfw.devcon.common.api.Command> devon4ng_cmd = getCommand("devon4ng", "build", p);
          devon4ng_cmd.get().exec();
        }
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during executing Project Cmd");
    }

  }

  @Command(name = "create", description = "This command creates a new combined server & client project")
  @Parameters(values = {
  @Parameter(name = "combinedprojectpath", description = "where to create the combined server and client project (currentDir if not given)", optional = true, inputType = @InputType(name = InputTypeNames.PATH)),
  @Parameter(name = "servername", description = "name for the server project"),
  @Parameter(name = "packagename", description = "package name for the server project"),
  @Parameter(name = "groupid", description = "groupid for server project"),
  @Parameter(name = "version", description = "version of server project"),
  @Parameter(name = "dbtype", description = "database type in server project(h2|postgresql|mysql|mariadb|oracle|hana|db2)"),
  @Parameter(name = "clientname", description = "name for the devon4ng project"),
  @Parameter(name = "clientpath", description = "path where the client project will be created.", optional = true, inputType = @InputType(name = InputTypeNames.PATH)) })
  public void create(String combinedprojectpath, String servername, String packagename, String groupid, String version,
      String dbtype, String clientname, String clientpath) {

    try {

      Optional<com.devonfw.devcon.common.api.Command> createServer = getCommand(this.DEVON4J, this.CREATE);

      combinedprojectpath = combinedprojectpath.isEmpty() ? getContextPathInfo().getCurrentWorkingDirectory().toString()
          : combinedprojectpath;

      String clientJsonReference;
      if (createServer.isPresent()) {
        createServer.get().exec(combinedprojectpath, servername, packagename, groupid, version, dbtype);
      } else {
        getOutput().showError("No command create found for devon4j module.");
      }

      getOutput().showMessage("Creating client project...");

      Optional<com.devonfw.devcon.common.api.Command> createDevon4ng = getCommand(this.DEVON4NG, this.CREATE);

      if (createDevon4ng.isPresent()) {
        createDevon4ng.get().exec(clientname, clientpath);

        clientJsonReference = clientpath.isEmpty() ? clientname : clientpath + File.separator + clientname;

      } else {
        getOutput().showError("No command create found for devon4ng module.");
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
   * @param clientpath Path for client directory
   * @param serverport Port to run server project
   * @param serverpath Path of server directory
   */
  @Command(name = "run", description = "This command runs the server & client project (unified server and client build) in debug mode. It runs client app and spring boot server seperately.", context = ContextType.COMBINEDPROJECT)
  @Parameters(values = {
  @Parameter(name = "clientpath", description = "Location of the devon4ng app", optional = true, inputType = @InputType(name = InputTypeNames.PATH)),
  @Parameter(name = "serverport", description = "Port to start server", optional = true),
  @Parameter(name = "serverpath", description = "Path to Server project Workspace (currentDir if not given)", optional = true, inputType = @InputType(name = InputTypeNames.PATH)) })
  public void run(String clientpath, String serverport, String serverpath) {

    if (!this.projectInfo.isPresent()) {
      getOutput().showError("Not in a project or -path param not pointing to a project");
      return;
    }

    try {

      int size = this.projectInfo.get().getSubProjects().size();

      for (int i = 0; i < size; i++) {
        ProjectInfo p = this.projectInfo.get().getSubProjects().get(i);

        if (p.getProjecType() == ProjectType.DEVON4J) {
          Optional<com.devonfw.devcon.common.api.Command> cmd = getCommand(Constants.DEVON4J, Constants.RUN, p);
          cmd.get().exec(serverport);
        } else {
          String clienttype = Constants.DEVON4NG;
          Optional<com.devonfw.devcon.common.api.Command> devon4ng_cmd = getCommand(Constants.DEVON4NG, Constants.RUN,
              p);
          devon4ng_cmd.get().exec();
        }
      }

    } catch (Exception e) {
      getOutput().showError("An error occured during executing Project Cmd");
    }
  }

}
