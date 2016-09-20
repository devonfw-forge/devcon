package com.devonfw.devcon.input;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.devonfw.devcon.common.CommandResult;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.data.Sentence;
import com.devonfw.devcon.output.GUIOutput;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * TODO ssarmoka This type ...
 *
 * @author ssarmoka
 */
public class ExecuteCommandHandler implements EventHandler<ActionEvent> {

  private Scene popParentScene;

  private Stage screenController;

  private GridPane grid;

  private CommandManager cmdManager;

  private Command command;

  private String paramName;

  private List<String> mandatoryParamList;

  private GUIOutput guiOutput;

  // public static final BlockingQueue<String> blockingQueue = new BlockingQueue<>(2);

  public ExecuteCommandHandler() {

  }

  /**
   * The constructor.
   *
   * @param popParentScene
   * @param command
   * @param cmdManager
   * @param screenController
   * @param grid
   */
  public ExecuteCommandHandler(Scene popParentScene, Command command, CommandManager cmdManager, Stage screenController,
      GridPane grid) {
    this.popParentScene = popParentScene;
    this.screenController = screenController;
    this.cmdManager = cmdManager;
    this.grid = grid;
    this.command = command;

  }

  public ExecuteCommandHandler(Scene popParentScene, Command command, CommandManager cmdManager, Stage screenController,
      GridPane grid, List<String> mandatoryParamList) {
    this.popParentScene = popParentScene;
    this.screenController = screenController;
    this.cmdManager = cmdManager;
    this.grid = grid;
    this.command = command;
    this.mandatoryParamList = mandatoryParamList;

  }

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
  @Override
  public void handle(ActionEvent event) {

    HashMap<String, String> commandParams = new HashMap<>();

    Button b = (Button) event.getSource();
    // b.setId("ok");
    String label = b.getText();
    System.out.println("Label " + label);

    switch (label.toLowerCase().trim()) {
    case "back":
      this.screenController.setScene(this.popParentScene);
      this.screenController.show();
      break;
    case "ok":

      ObservableList<Node> nodList = this.grid.getChildren();
      for (Node e : nodList) {
        String id = e.getId();
        if (id != null && !id.isEmpty()) {
          boolean result;
          String paramName = id.substring(id.indexOf("_") + 1);
          System.out.println("paramName " + paramName);
          if (id.startsWith("text_")) {
            TextField t = (TextField) e;
            System.out.println("val is " + t.getText());
            result = validateParam(paramName, t.getText());
            if (!result) {
              t.setStyle("-fx-text-box-border: red; ");
              return;
            }

            commandParams.put(paramName, t.getText());

          } else if (id.startsWith("combo_")) {
            ComboBox<String> comboBox = (ComboBox<String>) e;
            System.out.println("comboBox param name " + comboBox.getId() + " val is " + comboBox.getValue());
            result = validateParam(paramName, comboBox.getValue());
            if (!result) {
              comboBox.setStyle("-fx-border-color:red;");
              return;
            }
            commandParams.put(paramName, comboBox.getValue());

          } else if (id.startsWith("password_")) {
            PasswordField pw = (PasswordField) e;
            System.out.println(" passsowrd val is " + pw.getText());
            result = validateParam(paramName, pw.getText());
            if (!result) {
              pw.setStyle("-fx-border-color:red;");
              return;
            }

            commandParams.put(paramName, pw.getText());

          } else if (id.startsWith("path_" + paramName)) {
            Button path = (Button) e;
            System.out.println("select path " + path.getText());
            // result = validateParam(paramName, path.getText());
            // if (!result)
            // path.setStyle("-fx-border-color:red;");
            commandParams.put(paramName, path.getText());

          }
        }

      }
      System.out.println("commandParams****************** " + commandParams);
      final Sentence sentence = new Sentence();
      sentence.setModuleName(this.command.getModuleName());
      sentence.setCommandName(this.command.getName());
      Set<String> keys = commandParams.keySet();
      for (String key : keys) {
        sentence.addParam(key, commandParams.get(key));
      }

      try {
        this.cmdManager.setOutput(this.guiOutput);
        //

        ExecuteCommandHandler.this.grid.setDisable(true);
        Platform.runLater(new Runnable() {
          @Override
          public void run() {

            Pair<CommandResult, Object> result = null;
            try {
              result = ExecuteCommandHandler.this.cmdManager.execCmdLine(sentence);
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

            boolean cmdResult =
                ((result.getLeft() == CommandResult.OK) || (result.getLeft() == CommandResult.HELP_SHOWN));
            System.out.println("result******************* " + cmdResult);

            ExecuteCommandHandler.this.grid.setDisable(false);
          }
        });

        b.setDisable(false);

      } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

      // }
      // });
      // this.screenController.setScene(this.popParentScene);
      // this.screenController.show();
      break;
    case "select path":
      DirectoryChooser chooser = new DirectoryChooser();
      File selectedFile = chooser.showDialog(null);
      Button path = (Button) event.getSource();
      if (selectedFile != null) {

        path.setText(selectedFile.getAbsolutePath());

      } else {
        path.setText("File selection cancelled.");
      }
      // Label l = new Label();
      // if (selectedFile != null) {
      //
      // l.setText(selectedFile.getAbsolutePath());
      // } else {
      // l.setText("File selection cancelled.");
      // }
      // int colIndex = GridPane.getColumnIndex(b);
      // int rowIndex = GridPane.getRowIndex(b);
      // int index = this.grid.getChildren().indexOf(b);
      // this.grid.getChildren().remove(index);
      //
      // this.grid.add(l, colIndex, rowIndex);
      break;
    }

  }

  // For valid params method return true
  private boolean validateParam(String paramName, String value) {

    if (this.mandatoryParamList.contains(paramName) && (value == null || value.isEmpty())) {
      return false;
    }
    return true;

  }

  public Task<Pair<CommandResult, Object>> createWorker(final Sentence sentence) {

    return new Task<Pair<CommandResult, Object>>() {
      @Override
      protected Pair<CommandResult, Object> call() throws Exception {

        Pair<CommandResult, Object> result = ExecuteCommandHandler.this.cmdManager.execCmdLine(sentence);

        return result;
      }
    };
  }

}