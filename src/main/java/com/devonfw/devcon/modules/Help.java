package com.devonfw.devcon.modules;

import java.util.List;

import com.devonfw.devcon.common.api.annotations.CmdModuleRegistry;
import com.devonfw.devcon.common.api.annotations.Command;
import com.devonfw.devcon.common.api.data.Response;
import com.devonfw.devcon.common.utils.DevconUtils;
import com.devonfw.devcon.output.OutputConsole;

/**
 * Module to show general help info to the devcon user
 *
 * @author pparrado
 */
@CmdModuleRegistry(name = "help", description = "This module shows help info about devcon", context = "global", deprecated = false)
public class Help {

  @SuppressWarnings("javadoc")
  @Command(name = "guide", help = "This command is used to show a general vision about the basic usage of devcon.")
  public void guide() {

    Response response = new Response();
    OutputConsole output = new OutputConsole();
    DevconUtils utils = new DevconUtils();

    response.usage = "devon <<module>> <<command>> [parameters...]";
    response.header =
        "Devcon is a command line tool that provides many automated tasks around the full life-cycle of Devon applications. \n\n\n"
            + "You can also use the global parameters: \n"
            + "-h to show help info related to each module/command \n"
            + "-np to avoid the app asking for user input \n" + "-v to show the devcon version \n\n";

    // -------------------------------------------
    // TODO read the global parameters from a file
    // -------------------------------------------

    // getting the modules list
    List<String> modules = utils.getAvailableModules();
    StringBuilder strb = new StringBuilder();
    strb.append("List of available modules: \n");
    for (String module : modules) {
      strb.append("> " + module + "\n");
    }
    response.footer = strb.toString();

    // output manager call
    output.showGeneralHelp(response);
  }
}
