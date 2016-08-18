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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.json.JSONObject;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.common.impl.utils.WindowsReqistry;
import com.devonfw.devcon.common.utils.Utils;
import com.devonfw.devcon.output.Output;
import com.github.zafarkhaja.semver.Version;

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
  @Command(name = "install", description = "Install Devcon on user´s HOME folder or alternative path", proxyParams = true)
  @Parameters(values = { @Parameter(name = "addToPath", description = "Add to %PATH% (by default \"true\")", optional = true) })
  public void install(String addToPath, String proxyHost, String proxyPort) {

    Output out = getOutput();
    boolean addPath = Boolean.valueOf(addToPath.isEmpty() ? "true" : addToPath);

    Path devconPath = getContextPathInfo().getHomeDirectory().resolve(DOT_DEVCON_DIR);
    System.out.println("User's HOME Directory: " + getContextPathInfo().getHomeDirectory());
    File devconDir = devconPath.toFile();
    File devconFile = devconPath.resolve(DEVCON_JAR_FILE).toFile();

    if (!devconFile.exists()) {

      getOutput().showMessage("Installing...");
      try {

        // Create .decvon dir in User $HOME directory
        devconDir.mkdir();

        if (!proxyHost.isEmpty() && !proxyPort.isEmpty()) {
          Utils.setProxy("devcon", proxyHost, proxyPort);
        }

        Pair<Version, String> downloadInfo = getDownloadData(Devcon.VERSION_URL);
        // TODO need change/fix??; file is downloaded again;
        updating(downloadInfo.getRight(), devconFile);

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

        out.showMessage("Intallation  successful!");
        if (!SystemUtils.IS_OS_WINDOWS) {
          out.showMessage("You´ll need to add the %s folder to your $PATH env variable.", devconPath.toString());
        }
        out.showMessage("The application has been installed. You need to close this console and open another one.");
        out.showMessage("Devcon is available as the command 'devcon' and its alias 'devon'.");

      } catch (ConnectException e) {
        out.showError("Connection error. Please verify your proxy or use the -ProxyHost and -ProxyPort parameters");
      } catch (JSONException | IOException e) {
        out.showError("while installing Devcon: %s", e.getMessage());
      }
    } else {

      out.showError("Devcon is already installed!");
    }

  }

  @SuppressWarnings("javadoc")
  @Command(name = "update", description = "Update Devcon as installed on user´s system", proxyParams = true)
  @Parameters(values = {})
  public void update(String proxyHost, String proxyPort) {

    Output out = getOutput();

    Path devconPath = getContextPathInfo().getHomeDirectory().resolve(DOT_DEVCON_DIR);
    System.out.println("User's HOME Directory: " + getContextPathInfo().getHomeDirectory());
    File devconDir = devconPath.toFile();
    File devconFile = devconPath.resolve(DEVCON_JAR_FILE).toFile();

    if (devconFile.exists()) {

      try {

        if (!proxyHost.isEmpty() && !proxyPort.isEmpty()) {
          Utils.setProxy("devcon", proxyHost, proxyPort);
        }

        Pair<Version, String> downloadInfo = getDownloadData(Devcon.VERSION_URL);
        if (downloadInfo.getLeft().compareTo(Devcon.VERSION_) > 0) {

          updating(downloadInfo.getRight(), devconFile);
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

  private void updating(String url, File devconFile) throws IOException {

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
