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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.api.data.CommandParameter;
import com.devonfw.devcon.common.api.data.InputTypeNames;
import com.devonfw.devcon.common.api.data.ParameterInputType;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.output.DownloadingProgress;
import com.devonfw.devcon.output.GUIOutput;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextAreaBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * This class is to create form for respective command in menu items. This will create UI control depending on inputType
 * provided for each parameter. for e.g if inputtype is generic it will add textfield in form,
 *
 * @author ssarmoka
 */
public class ShowCommandHandler implements EventHandler<ActionEvent> {

  private Command command;

  private Stage screenController;

  private Scene pushedScene;

  private CommandRegistry registry;

  private CommandManager cmdManager;

  private TextArea console;

  private List<String> mandatoryParamList = new ArrayList<>();

  public static Button start;

  private ProgressBar p2 = new ProgressBar();

  /**
   * Instance of output for Devcon GUI
   */
  public GUIOutput guiOutput;

  /**
   * @return mandatoryParamList
   */
  public List<String> getMandatoryParamList() {

    return this.mandatoryParamList;
  }

  /**
   * The constructor.
   */
  public ShowCommandHandler() {

  }

  /**
   * The constructor.
   *
   * @param command - command instance
   *
   * @param cmdManager -command manager instance
   *
   * @param primaryStage -stage from javafx application
   */
  public ShowCommandHandler(Command command, CommandManager cmdManager, Stage primaryStage) {

    this();
    this.command = command;
    this.screenController = primaryStage;
    this.cmdManager = cmdManager;
  }

  /**
   * The constructor.
   *
   * @param registry - CommandRegistry instance
   */
  public ShowCommandHandler(CommandRegistry registry) {

    this.registry = registry;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void handle(ActionEvent event) {

    MenuItem item = ((MenuItem) event.getSource());

    String menuItem = item.getText().toLowerCase();
    if (menuItem.equalsIgnoreCase("Exit")) {
      System.exit(0);
    }
    showForm();

  }

  private void showForm() {

    // Saving home screen so that after command executes control will go back
    saveParentScene();

    GridPane grid = new GridPane();
    grid.setStyle("-fx-background-color: #dcecf5;");
    grid.setAlignment(Pos.TOP_CENTER);
    grid.setHgap(10);
    grid.setVgap(10);
    grid.setPadding(new Insets(25, 25, 25, 25));
    ScrollPane scrollPane = new ScrollPane(grid);
    scrollPane.setFitToWidth(true);
    Scene scene = new Scene(scrollPane, this.screenController.getWidth(), this.screenController.getHeight());
    showCommandControls(grid);

    this.screenController.setScene(scene);
  }

  /**
   * @return Scene
   */
  public Scene popParentScene() {

    return this.pushedScene;
  }

  /**
   *
   */
  private void saveParentScene() {

    this.pushedScene = this.screenController.getScene();

  }

  /**
   * @param grid
   */
  private void showCommandControls(final GridPane grid) {

    Text mandtory_field_text = new Text("");
    // set scene title
    Text scenetitle = new Text(this.command.getModuleName() + " " + this.command.getName());
    scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    grid.add(scenetitle, 0, 1, 2, 1);

    // Displaying helptext
    TextArea helpText = new TextArea();
    helpText.setStyle("-fx-background-color: #dcecf5;");
    helpText.setWrapText(true);
    helpText.setText(this.command.getDescription());
    helpText.setEditable(false);
    grid.add(helpText, 0, 2, 2, 2);

    int order = 5;

    // mandatory fields message
    grid.getColumnConstraints().clear();
    grid.getRowConstraints().clear();
    if (this.command.getDefinedParameters().size() > 0) {
      mandtory_field_text = new Text(Constants.MANDATORY_FIELD);
      mandtory_field_text.setFill(Color.RED);
    }
    HBox hbox1 = new HBox();
    hbox1.getChildren().add(mandtory_field_text);
    grid.add(hbox1, 0, 4, 1, 1);

    for (final CommandParameter param : this.command.getDefinedParameters()) {
      Tooltip toolTip = new Tooltip(param.getDescription());
      ParameterInputType inputType = param.getInputType();

      // To Handle Proxy paraams on GUI
      if (inputType == null) {
        inputType = new ParameterInputType(InputTypeNames.GENERIC);
      }
      Text blankText = new Text();
      switch (inputType.getName()) {

        case LIST:
          Label list = new Label(param.getName());
          Text text = new Text(Constants.ASTRIKE);
          text.setFill(Color.RED);
          HBox hb = new HBox();
          hb.getChildren().add(list);
          if (param.isOptional()) {
            hb.getChildren().add(blankText);
          } else {
            this.mandatoryParamList.add(param.getName());
            hb.getChildren().add(text);
          }

          list.setStyle("-fx-background-color: #efeeee;");
          grid.add(hb, 0, order); // col row

          ObservableList<String> options = FXCollections.observableArrayList();
          String[] inputVals = param.getInputType().getValues();
          options.addAll(Arrays.asList(inputVals));
          ComboBox<String> comboBox = new ComboBox<>(options);
          comboBox.getSelectionModel().select(0); // default selection for option in drop down list
          comboBox.setId("combo_" + param.getName());
          comboBox.setTooltip(toolTip);
          comboBox.setMinHeight(Constants.HEIGHT);
          comboBox.setPrefWidth(Constants.WIDTH);
          grid.add(comboBox, 1, order);
          break;

        case PASSWORD:
          final Label message = new Label("");
          Text text1 = new Text(Constants.ASTRIKE);
          text1.setFill(Color.RED);
          Label pw = new Label(param.getName());
          pw.setStyle("-fx-background-color: #efeeee;");
          HBox hb1 = new HBox();
          hb1.getChildren().add(pw);
          if (param.isOptional()) {
            hb1.getChildren().add(blankText);
          } else {
            this.mandatoryParamList.add(param.getName());
            hb1.getChildren().add(text1);
          }
          grid.add(hb1, 0, order); // col row
          final PasswordField pwBox = new PasswordField();
          pwBox.setId("password_" + param.getName());
          pwBox.setTooltip(toolTip);
          pwBox.setMinHeight(Constants.HEIGHT);
          pwBox.setMaxWidth(Constants.WIDTH);
          pwBox.setPrefWidth(Constants.WIDTH);
          grid.add(pwBox, 1, order);
          break;

        case PATH:
          Label path = new Label(param.getName());
          Text text2 = new Text(Constants.ASTRIKE);
          text2.setFill(Color.RED);
          path.setStyle("-fx-background-color: #efeeee;");
          HBox hb2 = new HBox();
          hb2.getChildren().add(path);
          if (param.isOptional()) {
            hb2.getChildren().add(blankText);
          } else {
            this.mandatoryParamList.add(param.getName());
            hb2.getChildren().add(text2);
          }

          grid.add(hb2, 0, order); // col row

          HBox directorySelector = new HBox();

          final TextField selectedPath = new TextField();
          selectedPath.setText(this.cmdManager.getContextPathInfo().getCurrentWorkingDirectory().toString());
          selectedPath.setEditable(false);
          Button pathSelector = new Button(Constants.SELECT_PATH);
          pathSelector.setTooltip(toolTip);
          pathSelector.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

              chooseDirectory(ShowCommandHandler.this.screenController, selectedPath);
            }
          });

          ;

          directorySelector.getChildren().add(selectedPath);
          directorySelector.getChildren().add(pathSelector);
          directorySelector.setId("path_" + param.getName());

          directorySelector.setSpacing(5);
          directorySelector.setPrefHeight(Constants.HEIGHT);
          directorySelector.setPrefWidth(Constants.WIDTH);
          grid.add(directorySelector, 1, order);

          break;

        case BOOLEAN:
          final Label flagLabel = new Label(param.getName());
          flagLabel.setStyle("-fx-background-color: #efeeee;");
          HBox hbFlag = new HBox();
          hbFlag.getChildren().add(flagLabel);
          if (param.isOptional()) {
            hbFlag.getChildren().add(blankText);
          } else {
            this.mandatoryParamList.add(param.getName());
            Text mandatoryMarker = new Text(Constants.ASTRIKE);
            mandatoryMarker.setFill(Color.RED);
            hbFlag.getChildren().add(mandatoryMarker);
          }
          grid.add(hbFlag, 0, order);

          CheckBox checkbox = new CheckBox();
          checkbox.setMinHeight(Constants.HEIGHT);
          checkbox.setMaxWidth(Constants.WIDTH);
          checkbox.setId("checkbox_" + param.getName());
          checkbox.setTooltip(toolTip);
          grid.add(checkbox, 1, order);

        default: /* GENERIC */
          final Label genLabel = new Label(param.getName());
          Text text3 = new Text(Constants.ASTRIKE);
          text3.setFill(Color.RED);
          genLabel.setStyle("-fx-background-color: #efeeee;");
          HBox hb3 = new HBox();
          hb3.getChildren().add(genLabel);
          if (param.isOptional()) {
            hb3.getChildren().add(blankText);
          } else {
            this.mandatoryParamList.add(param.getName());
            hb3.getChildren().add(text3);
          }
          grid.add(hb3, 0, order);

          TextField userTextField = new TextField();
          userTextField.setMinHeight(Constants.HEIGHT);
          userTextField.setMaxWidth(Constants.WIDTH);
          userTextField.setId("text_" + param.getName());
          userTextField.setTooltip(toolTip);
          userTextField.setPromptText("Enter " + param.getName());
          grid.add(userTextField, 1, order);

      }

      order++;

    }

    final ProgressIndicator pi = new ProgressIndicator();
    int rowNum = grid.getChildren().size();
    if (this.command.getModuleName().equalsIgnoreCase("Dist") && this.command.getName().equalsIgnoreCase("install")) {
      HBox progressBarHbox = new HBox();
      progressBarHbox.getChildren().add(this.p2);
      progressBarHbox.getChildren().add(pi);

      grid.add(progressBarHbox, 0, rowNum + 2, 2, 2);
      progressBarHbox.setPrefWidth(600);

      // grid.add(pi, 4, rowNum + 2, 2, 2);

    }

    this.p2.setPrefWidth(575);
    Thread progressBarUpdate = new Thread(new Runnable() {
      @Override
      public void run() {

        while ((DownloadingProgress.downloadProgress / 100) != 1) {

          ShowCommandHandler.this.p2.progressProperty().setValue((DownloadingProgress.downloadProgress) / 100);

          pi.setVisible(true);
          pi.setProgress((DownloadingProgress.downloadProgress) / 100);

          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {

            e.printStackTrace();
          }
        }

        if ((DownloadingProgress.downloadProgress / 100) == 1) {
          pi.setProgress((DownloadingProgress.downloadProgress) / 100);
        }
      }
    });
    progressBarUpdate.start();

    // int rowNum = grid.getChildren().size();

    this.console = TextAreaBuilder.create().prefWidth(600).prefHeight(300).wrapText(true).build();
    this.guiOutput = new GUIOutput(this.console);
    this.console.setEditable(false);
    this.console.setId("console");
    this.console.setPromptText(Constants.CONSOLE_PROMPT_TEXT);

    // this.console.setStyle("-fx-background-color: #b5c9c9;");
    grid.add(this.console, 0, rowNum + 5, 2, 2);
    ExecuteCommandHandler cmdHandler = new ExecuteCommandHandler(popParentScene(), this.command, this.cmdManager,
        this.screenController, grid, this.mandatoryParamList, this.guiOutput);

    // Start button
    HBox hbox = new HBox(10);
    hbox.setPrefWidth(100);
    start = new Button("Start");
    start.setMinWidth(hbox.getPrefWidth());
    changeBackgroundOnHover(start);
    start.setCursor(Cursor.HAND);
    hbox.getChildren().add(start);
    start.setOnAction(cmdHandler);

    // back button
    VBox vbox = new VBox();
    vbox.setPrefWidth(80);
    Button back = new Button("back");
    back.setAlignment(Pos.BASELINE_RIGHT);
    back.setMinWidth(vbox.getPrefWidth());
    vbox.getChildren().add(back);
    grid.add(vbox, 0, 0);
    back.setOnAction(cmdHandler);

    // left arrow
    Polygon leftArrow = new Polygon();
    leftArrow.getPoints()
        .addAll(new Double[] { 15.0, 10.0, 25.0, 2.0, 25.0, 7.0, 35.0, 7.0, 35.0, 13.0, 25.0, 13.0, 25.0, 18.0 });
    grid.add(leftArrow, 0, 0);

    grid.add(hbox, 1, rowNum + 1);
  }

  private void chooseDirectory(Stage primaryStage, TextField filePath) {

    DirectoryChooser directoryChooser = new DirectoryChooser();
    String currentWorkingDir = this.cmdManager.getContextPathInfo().getCurrentWorkingDirectory().toString();
    String defaultLocation = currentWorkingDir.substring(0, currentWorkingDir.lastIndexOf(File.separator));
    directoryChooser.setInitialDirectory(new File(defaultLocation));
    File selectedFile = directoryChooser.showDialog(primaryStage);
    if (selectedFile != null) {
      filePath.setText(selectedFile.getAbsolutePath());
    }
  }

  public void changeBackgroundOnHover(final Node node) {

    node.setStyle(Constants.STANDARD_BUTTON_START_STYLE);
    node.setOnMouseEntered(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {

        node.setStyle(Constants.HOVERED_BUTTON_START_STYLE);
      }
    });
    node.setOnMouseExited(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent mouseEvent) {

        node.setStyle(Constants.STANDARD_BUTTON_START_STYLE);
      }
    });
  }

}
