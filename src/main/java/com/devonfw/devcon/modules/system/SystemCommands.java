package com.devonfw.devcon.modules.system;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.annotations.Parameter;
import com.devonfw.devcon.common.api.annotations.Parameters;
import com.devonfw.devcon.common.impl.AbstractCommandModule;
import com.devonfw.devcon.output.Output;
import com.github.zafarkhaja.semver.Version;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * TODO ivanderk This type ...
 *
 * @author ivanderk
 * @since 0.0.1
 */

@CmdModuleRegistry(name = "system", description = "Devcon and system-wide commands") // TODO update for sub-systems?
public class SystemCommands extends AbstractCommandModule {

  /**
   *
   */

  @SuppressWarnings("javadoc")
  @Command(name = "install", help = "Install Devcon on user´s HOME folder or alternative path")
  @Parameters(values = {
  @Parameter(name = "addToPATHVAR", description = "Add to %PATH% (by default \"true\")", optional = true) })
  public void install(String path, String appDir) {

    getOutput().showMessage("NOT IMPLEMENTED");
  }

  @SuppressWarnings("javadoc")
  @Command(name = "update", help = "Update Devcon as installed on user´s system")
  @Parameters(values = {})
  public void update() {

    Output out = getOutput();

    File devconFile = getContextPathInfo().getHomeDirectory().resolve(".devcon/devcon.jar").toFile();
    if (devconFile.exists()) {

      HttpResponse<JsonNode> resp;
      try {
        JSONObject json = Unirest.get(Devcon.VERSION_URL).asJson().getBody().getObject();
        Version version = Version.valueOf((String) json.get("version"));
        if (version.compareTo(Devcon.VERSION_) > 0) {

          updating((String) json.get("url"), devconFile);

          out.showMessage("Update successful!");
          System.exit(0);

        } else {
          out.showMessage("Version up to date");
        }

      } catch (UnirestException | JSONException | IOException e) {

        out.showError("while updating Devcon: " + e.getMessage());
      }

    } else {

      out.showError("Devcon is not installed. Please install it before attempting to update.");
    }

  }

  private void updating(String url, File devconFile) throws UnirestException, IOException {

    HttpResponse<InputStream> binary = Unirest.get(url).asBinary();
    InputStream in_ = binary.getRawBody();
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
