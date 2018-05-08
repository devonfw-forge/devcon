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
package com.devonfw.devcon.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.lang3.tuple.Pair;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.DevconOption;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.exception.InvalidEnvironentException;
import com.devonfw.devcon.common.utils.ContextPathInfo;
import com.devonfw.devcon.output.Output;
import com.google.common.base.Optional;

/**
 * Component responsible for parsing and processing console input
 *
 * @author pparrado
 */
public class ConsoleInputManager {

  private CommandManager commandManager;

  private Input input;

  private Output output;

  private CommandRegistry registry;

  public ConsoleInputManager(CommandRegistry registry, Input input, Output output, CommandManager commandManager) {

    this.registry = registry;
    this.input = input;
    this.output = output;
    this.commandManager = commandManager;
  }

  public boolean parse(String[] args) {

    String path;
    Sentence sentence;

    try {

      CommandLineParser parser = new BasicParser();
      CommandLine cmd = parser.parse(getOptions(), args);
      ContextPathInfo contextPathInfo = new ContextPathInfo();
      if (contextPathInfo.getDistributionRoot() != null && contextPathInfo.getDistributionRoot().isPresent()) {
        path = contextPathInfo.getDistributionRoot().get().getPath().toAbsolutePath() + "\\software\\devcon";
      } else {
        path = contextPathInfo.getCurrentWorkingDirectory().toString();
      }
      String lock_file_path = path + "\\devcon.lock";
      File devcon_lock_file = new File(lock_file_path);
      if (devcon_lock_file.exists()) {

        FileChannel channel = new RandomAccessFile(devcon_lock_file, "rw").getChannel();
        FileLock lock = channel.tryLock();
        if (lock == null) {
          throw new InvalidEnvironentException("Devcon Instance Already Running");

        } else {
          lock.release();
        }

      }

      if (cmd.hasOption("nl")) {

        if (devcon_lock_file.exists()) {

          boolean result = lockInstance(lock_file_path);

        } else {
          devcon_lock_file.createNewFile();
          boolean result = lockInstance(lock_file_path);

        }

      }

      if (cmd.hasOption("v")) {
        this.output.showMessage(Devcon.DEVCON_VERSION);
        System.exit(0);
      }

      if (cmd.hasOption("g")) {

        GUIAppManager.main(this.registry, this.commandManager, args);
        // Exit after return from GUI
        System.exit(0);

      }

      if (cmd.hasOption("p") && (cmd.hasOption("h"))) {
        this.output.showError("Cannot specifiy -h and -p at the same time");
        System.exit(0);
      }

      if (cmd.hasOption("s")) {
        Devcon.SHOW_STACK_TRACE = true;
      }

      if (cmd.hasOption("p")) {
        // obtain user input from interactively displaying all params screen and gettng data from user
        sentence = prompUserForParams(cmd);
      } else {
        // from command line
        sentence = getParamsFromCmdLine(cmd);
      }

      if ((sentence.getModuleName() == null) && (sentence.getCommandName() == null)) {
        this.commandManager.showMainHelp();
        return true;
      }

      Pair<CommandResult, Object> result = this.commandManager.execCmdLine(sentence);

      if (result.getLeft() == CommandResult.FAILURE) {

        Object ex = result.getRight();
        if ((ex != null) && (ex instanceof Throwable)) {

          Throwable err = (Throwable) ex;
          this.output.showError("An error occurred. Message: %s", err.getMessage());
          if (Devcon.SHOW_STACK_TRACE) {
            this.output.showError("Stacktrace:");
            err.printStackTrace();
          }
        } else {
          this.output.showError("Unexpected Error without error information");
        }
        return false;
      }

      return ((result.getLeft() == CommandResult.OK) || (result.getLeft() == CommandResult.HELP_SHOWN));

    } catch (InvalidEnvironentException ie) {
      this.output.showError("Devcon instance already running");
      return false;
    } catch (UnrecognizedOptionException e) {

      this.output.showError(e.getMessage());
      return false;
    } catch (Throwable e) {
      e.printStackTrace();
      this.output.showError("An unexcpected error occurred");
      if (Devcon.SHOW_STACK_TRACE) {
        this.output.showError("Stacktrace:");
        e.printStackTrace();
      }
      return false;
    }
  }

  /**
   * @param cmd
   * @return
   */
  private Sentence prompUserForParams(CommandLine cmd) {

    Sentence sentence = new Sentence();

    List<?> argList = cmd.getArgList();
    if ((argList.size() > 0) && (argList.get(0) != null)) {
      sentence.setModuleName(argList.get(0).toString());
    } else {
      sentence.setModuleName(this.input.promptUser("Give module: "));
    }

    if ((argList.size() > 1) && (argList.get(1) != null)) {
      sentence.setCommandName(argList.get(1).toString());
    } else {
      sentence.setCommandName(this.input.promptUser("Give command: "));
    }

    Optional<Command> command = this.registry.getCommand(sentence.getModuleName(), sentence.getCommandName());
    if (!command.isPresent()) {
      this.output.showError("Module and/or command not valid");
      System.exit(0);
    }

    Command command_ = command.get();
    this.output.showMessage("Command: devon %s %s\nDescription: %s\n", command_.getModuleName(), command_.getName(),
        command_.getDescription());

    for (CommandParameter p : command_.getDefinedParameters()) {

      boolean given = false;
      for (Option parsedParam : cmd.getOptions()) {
        if (parsedParam.getLongOpt().equals(p.getName())) {
          given = true;
        }
      }
      if (!given) {
        String inp = this.input.promptUser("Parameter: %s - %s\n-> ", p.getName(), p.getDescription());
        sentence.addParam(p.getName(), inp);
      }
    }

    return sentence;

  }

  /**
   * @returns sentence
   * @param cmd
   */
  private Sentence getParamsFromCmdLine(CommandLine cmd) {

    Sentence sentence = new Sentence();
    for (Option parsedParam : cmd.getOptions()) {
      if (cmd.getOptionValue(parsedParam.getOpt()) != null)
        sentence.addParam(parsedParam.getOpt(), parsedParam.getValue());
    }

    List<?> argList = cmd.getArgList();

    if (argList.size() == 1) {
      sentence.setModuleName(argList.get(0).toString());
    } else if (argList.size() > 1) {
      sentence.setModuleName(argList.get(0).toString());
      sentence.setCommandName(argList.get(1).toString());
    }

    sentence.setHelpRequested(cmd.hasOption("h"));

    return sentence;
  }

  private Options getOptions() {

    List<DevconOption> devconOptions = new ContextPathInfo().getGlobalOptions();
    Options options = new Options();

    for (DevconOption gOpt : devconOptions) {
      options.addOption(new Option(gOpt.getOpt(), gOpt.getLongOpt(), false, gOpt.getDescription()));
    }

    for (String name : this.commandManager.getParameterNames()) {
      options.addOption(name, true, null);
    }

    return options;
  }

  private static boolean lockInstance(final String lockFile) {

    RandomAccessFile file = null;
    FileLock fileLock = null;
    try {
      file = new RandomAccessFile(lockFile, "rw");
      FileChannel fileChannel = file.getChannel();

      fileLock = fileChannel.tryLock();
      if (fileLock != null) {
        System.out.println("File is locked");
        return true;
      } else {
        throw new InvalidEnvironentException("Devcon Instance Already Running");
      }
    } catch (FileNotFoundException e) {

      e.printStackTrace();
    } catch (IOException e) {

      e.printStackTrace();
    }
    return false;
  }

  private static boolean lockInstance1(FileInputStream fis) {

    FileLock fileLock = null;
    try {

      FileChannel fileChannel = fis.getChannel();

      fileLock = fileChannel.lock();
      if (fileLock != null) {
        System.out.println("File is locked");
        return true;
      } else {
        throw new InvalidEnvironentException("Devcon Instance Already Running");
      }
    } catch (FileNotFoundException e) {

      e.printStackTrace();
    } catch (IOException e) {

      e.printStackTrace();
    }
    return false;

  }

}
