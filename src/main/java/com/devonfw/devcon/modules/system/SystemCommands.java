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
package com.devonfw.devcon.modules.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.json.JSONObject;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.InputType;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.api.data.DistributionInfo;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.impl.utils.WindowsReqistry;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.common.utils.Utils;
import com.devonfw.devcon.output.Output;
import com.github.zafarkhaja.semver.Version;
import com.google.common.base.Optional;

/**
 * System-wide commands and those related with Devcon itself
 *
 * @author ivanderk
 *
 */

@CmdModuleRegistry(name = "system", description = "Devcon and system-wide commands")
// TODO update for sub-systems?
public class SystemCommands extends AbstractCommandModule {

  /**
   * File name of devcon app
   */
  private static final String DEVCON_JAR_FILE = "devcon.jar";

  /**
   * Directory name for devcon files and settings in users HOME dir
   */
  private static final String DOT_DEVCON_DIR = ".devcon";

  /**
   *
   */

  @SuppressWarnings("javadoc")
  @Command(name = "installDevcon", description = "Install Devcon on user´s HOME folder or alternative path", proxyParams = true)
  @Parameters(values = {
  @Parameter(name = "addToPath", description = "Add to %PATH% (by default \"true\")", optional = true, inputType = @InputType(name = InputTypeNames.LIST, values = {
  "true", "false" })) })
  public void install(String addToPath/* , String proxyHost, String proxyPort */) {

    Output out = getOutput();
    try {

      boolean addPath = Boolean.valueOf(addToPath.isEmpty() ? "true" : addToPath);

      Path devconPath = getContextPathInfo().getHomeDirectory().resolve(DOT_DEVCON_DIR);
      File devconDir = devconPath.toFile();
      File devconFile = devconPath.resolve(DEVCON_JAR_FILE).toFile();

      if (!devconFile.exists()) {

        getOutput().showMessage("Installing...");
        try {

          // Create .decvon dir in User $HOME directory
          devconDir.mkdir();

          File devonJar = Utils.getApplicationPath();
          FileUtils.copyFile(devonJar, devconFile);

          if (SystemUtils.IS_OS_WINDOWS) {
            if (addPath) {
              updatePath(devconDir.toString());
            }

            OutputStreamWriter devconCmd =
                new OutputStreamWriter(new FileOutputStream(devconPath.resolve("devcon.cmd").toFile()));
            OutputStreamWriter devonCmd =
                new OutputStreamWriter(new FileOutputStream(devconPath.resolve("devon.cmd").toFile()));

            String source = String.format("@echo off\n" + "java -jar %s %%*\n", devconFile.toString());
            devconCmd.write(source);
            devconCmd.close();
            devonCmd.write(source);
            devonCmd.close();
          } else if (SystemUtils.IS_OS_LINUX) {
            if (addPath) {

              System.out.println("Devcon.IN_EXEC_JAR ------- " + Devcon.IN_EXEC_JAR);
              String root = (Devcon.IN_EXEC_JAR) ? ("resources" + File.separator) : "";
              System.out.println("root value ----------- " + root);

              String scriptPath =
                  SystemCommands.class.getClassLoader().getResource(root + Constants.DEVCON_SCRIPT).toExternalForm();
              System.out.println("sriptPath value ----------- " + scriptPath);

              Process procBuildScript =
                  new ProcessBuilder(Constants.LINUX_BASH, "-c", "/" + Constants.DEVCON_SCRIPT, devconDir.toString())
                      .start();

            }

            OutputStreamWriter devconCmd =
                new OutputStreamWriter(new FileOutputStream(devconPath.resolve("devcon.sh").toFile()));
            OutputStreamWriter devonCmd =
                new OutputStreamWriter(new FileOutputStream(devconPath.resolve("devon.sh").toFile()));

            String source = String.format("@echo off\n" + "java -jar %s $*\n", devconFile.toString());

            devconCmd.write(source);
            devconCmd.close();
            devonCmd.write(source);
            devonCmd.close();
          }

          out.showMessage("Installation  successful!");
          if (!SystemUtils.IS_OS_WINDOWS) {
            out.showMessage("You´ll need to add the %s folder to your $PATH env variable.", devconPath.toString());
          }
          out.showMessage("The application has been installed.");
          out.showMessage("You need to restart your session or reboot your PC before start using Devcon.");
          out.showMessage("After that Devcon will be available as the command 'devcon' and its alias 'devon'.");

        } catch (ConnectException e) {
          out.showError("Connection error. Please verify your proxy or use the -proxyHost and -proxyPort parameters");
        } catch (JSONException | IOException e) {
          out.showError("while installing Devcon: %s", e.getMessage());
        }
      } else {

        out.showError("Devcon is already installed!");
      }
    } catch (Throwable err) {

      out.showError("Unexpected error: %s", err.getMessage());
      // TODO show stacktrace

    }
  }

  @SuppressWarnings("javadoc")
  @Command(name = "configureEnvironment", description = "Update Devcon as installed on user´s system", proxyParams = true)
  @Parameters(values = {})
  public void update() {

    Output out = getOutput();

    Optional<DistributionInfo> distInfo = ContextPathInfo.INSTANCE.getDistributionRoot();

    Path devconPath = distInfo.isPresent() ? distInfo.get().getPath().resolve("software/devcon")
        : getContextPathInfo().getHomeDirectory().resolve(DOT_DEVCON_DIR);
    File devconDir = devconPath.toFile();

    // if devconDir is not in the 'default' user's Home directory we will look for it in other root drives
    if (!devconDir.exists()) {
      File[] roots = File.listRoots();
      for (int i = 0; i < roots.length; i++) {
        String drive = devconDir.getAbsolutePath().substring(0, 1);
        File devconDirInOtherDrive =
            new File(devconDir.getAbsolutePath().replace(drive, roots[i].toString().split(":")[0]));
        if (devconDirInOtherDrive.exists()) {
          devconPath = devconDirInOtherDrive.toPath();
          break;
        }
      }
    }

    getOutput().showMessage("Devcon found in: " + devconPath.toString());

    File devconFile = devconPath.resolve(DEVCON_JAR_FILE).toFile();

    if (devconFile.exists()) {

      try {

        // if (!proxyHost.isEmpty() && !proxyPort.isEmpty()) {
        // Utils.setProxy("devcon", proxyHost, proxyPort);
        // }

        Pair<Version, String> downloadInfo = getDownloadData(Devcon.VERSION_URL);
        if (downloadInfo.getLeft().compareTo(Devcon.VERSION_) > 0) {

          update(downloadInfo.getRight(), devconFile);
          out.showMessage("Update successful!");

          // The apps own jar file has been overwritten by the updating method
          // The result is that the return code-path generates errors.
          // So an System.exit is required
          System.exit(0);

        } else {
          out.showMessage("Version up to date. No change is needed.");
        }

      } catch (ConnectException e) {
        out.showError("Connection error. Please verify your proxy or use the -ProxyHost and -ProxyPort parameters");
      } catch (JSONException | IOException e) {

        out.showError("while updating Devcon: " + e.getMessage());
      }

    } else {

      out.showError("Devcon is not installed. Please install it before attempting to update.");
    }

  }

  /**
   * @param devconDir
   */
  private void updatePath(String devconDir) {

    String dirs = WindowsReqistry.readRegistry("HKCU\\Environment", "Path");
    // Only add when not present
    if (!dirs.contains(devconDir)) {
      WindowsReqistry.writeRegistry("HKCU\\Environment", "Path", dirs + ";" + devconDir);
    }
  }

  private Pair<Version, String> getDownloadData(String url) throws JSONException, MalformedURLException, IOException {

    Version version = null;
    String url_ = null;
    JSONObject json = null;

    json = new JSONObject(IOUtils.toString(new URL(Devcon.VERSION_URL), Charset.forName("UTF-8")));

    version = Version.valueOf((String) json.get("version"));
    url_ = (String) json.get("url");

    return Pair.of(version, url_);
  }

  private void update(String url, File devconFile) throws IOException {

    InputStream in_ = new URL(url).openStream();
    FileOutputStream outFile = new FileOutputStream(devconFile);

    byte[] buffer = new byte[4069];
    int read;
    int offset = 0;
    try {
      while ((read = in_.read(buffer, 0, buffer.length)) > -1) {
        outFile.write(buffer, 0, read);
        offset += read;
      }
    } finally {
      outFile.close();
    }

    outFile.close();
  }

}
