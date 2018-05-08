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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.ProcessUtils;
import com.devonfw.devcon.output.GUIOutput;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * This class is for handling events on forms, executing respective commands.
 *
 * @author ssarmoka
 */
public class ExecuteCommandHandler implements EventHandler<ActionEvent> {

  private Scene popParentScene;

  private Stage screenController;

  private GridPane grid;

  private CommandManager cmdManager;

  private Command command;

  private List<String> mandatoryParamList;

  private GUIOutput guiOutput;

  private ProcessUtils processUtils = new ProcessUtils();

  @SuppressWarnings("javadoc")
  public ExecuteCommandHandler() {

  }

  /**
   * The constructor.
   *
   * @param popParentScene -stored parent scene (home screen)
   * @param command -command instance
   * @param cmdManager - CommandManager instance
   * @param screenController - Stage
   * @param grid -gridpane in scene
   */
  public ExecuteCommandHandler(Scene popParentScene, Command command, CommandManager cmdManager, Stage screenController,
      GridPane grid) {

    this.popParentScene = popParentScene;
    this.screenController = screenController;
    this.cmdManager = cmdManager;
    this.grid = grid;
    this.command = command;

  }

  /**
   * The constructor.
   *
   * @param popParentScene -stored parent scene (home screen)
   * @param command -command instance
   * @param cmdManager - CommandManager instance
   * @param screenController - Stage
   * @param grid -gridpane in scene
   * @param mandatoryParamList -mandatory parameter list
   */
  public ExecuteCommandHandler(Scene popParentScene, Command command, CommandManager cmdManager, Stage screenController,
      GridPane grid, List<String> mandatoryParamList) {

    this.popParentScene = popParentScene;
    this.screenController = screenController;
    this.cmdManager = cmdManager;
    this.grid = grid;
    this.command = command;
    this.mandatoryParamList = mandatoryParamList;

  }

  /**
   * The constructor.
   *
   * @param popParentScene -stored parent scene (home screen)
   * @param command -command instance
   * @param cmdManager - CommandManager instance
   * @param screenController - Stage
   * @param grid -gridpane in scene
   * @param mandatoryParamList -mandatory parameter list
   * @param guiOutput -output instance
   */
  public ExecuteCommandHandler(Scene popParentScene, Command command, CommandManager cmdManager, Stage screenController,
      GridPane grid, List<String> mandatoryParamList, GUIOutput guiOutput) {

    this.popParentScene = popParentScene;
    this.screenController = screenController;
    this.cmdManager = cmdManager;
    this.grid = grid;
    this.command = command;
    this.mandatoryParamList = mandatoryParamList;
    this.guiOutput = guiOutput;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void handle(ActionEvent event) {

    HashMap<String, String> commandParams = new HashMap<>();

    final Button button = (Button) event.getSource();

    String label = button.getText();

    switch (label.toLowerCase().trim()) {
    case Constants.BACK:

      new Thread(new Runnable() {
        @Override
        public void run() {

          try {
            ExecuteCommandHandler.this.processUtils.killProcess();
          } catch (IOException e2) {
            // TODO Auto-generated catch block
            ExecuteCommandHandler.this.guiOutput
                .showError("Exception occured while exiting from process " + e2.getMessage());
          }
        }
      }).start();

      this.screenController.setScene(this.popParentScene);
      this.screenController.show();
      break;
    case Constants.START:

      button.setDisable(true);
      ObservableList<Node> nodList = this.grid.getChildren();
      for (Node e : nodList) {
        String id = e.getId();
        if (id != null && !id.isEmpty()) {
          boolean result;
          String paramName = id.substring(id.indexOf("_") + 1);

          if (id.startsWith("text_")) {
            TextField t = (TextField) e;
            result = validateParam(paramName, t.getText());
            if (!result) {
              t.setStyle("-fx-text-box-border: red; ");
              return;
            }

            commandParams.put(paramName, t.getText());

          } else if (id.startsWith("combo_")) {
            ComboBox<String> comboBox = (ComboBox<String>) e;

            result = validateParam(paramName, comboBox.getValue());
            if (!result) {
              comboBox.setStyle("-fx-border-color:red;");
              return;
            }
            commandParams.put(paramName, comboBox.getValue());

          } else if (id.startsWith("password_")) {
            PasswordField pw = (PasswordField) e;

            result = validateParam(paramName, pw.getText());
            if (!result) {
              pw.setStyle("-fx-border-color:red;");
              return;
            }

            commandParams.put(paramName, pw.getText());

          } else if (id.startsWith("path_" + paramName)) {
            HBox path = (HBox) e;
            TextField selectedPath = (TextField) path.getChildren().get(0);

            commandParams.put(paramName, selectedPath.getText());

          }
        }

      }

      final Sentence sentence = new Sentence();
      sentence.setModuleName(this.command.getModuleName());
      sentence.setCommandName(this.command.getName());
      Set<String> keys = commandParams.keySet();
      for (String key : keys) {
        sentence.addParam(key, commandParams.get(key));
      }

      try {

        this.cmdManager.setOutput(this.guiOutput);

        new Thread(new Runnable() {
          @Override
          public void run() {

            Pair<CommandResult, Object> result = null;
            try {
              result = ExecuteCommandHandler.this.cmdManager.execCmdLine(sentence);
              boolean cmdResult =
                  ((result.getLeft() == CommandResult.OK) || (result.getLeft() == CommandResult.HELP_SHOWN));
              button.setDisable(false);
            } catch (Exception e) {

              ExecuteCommandHandler.this.guiOutput.showError("ERROR : ", e.getMessage());

            }

          }
        }).start();

      } catch (Exception e1) {
        ExecuteCommandHandler.this.guiOutput.showError("ERROR : ", e1.getMessage());
      }

      break;

    }

  }

  /**
   * Validating mandatory parameters For valid params method return true
   *
   * @param paramName
   * @param value
   * @return
   */
  private boolean validateParam(String paramName, String value) {

    if (this.mandatoryParamList.contains(paramName) && (value == null || value.isEmpty())) {
      return false;
    }
    return true;

  }

}
