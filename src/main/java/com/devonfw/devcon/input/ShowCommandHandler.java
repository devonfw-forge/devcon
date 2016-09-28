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
import com.devonfw.devcon.output.GUIOutput;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextAreaBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * TODO ssarmoka This type ...
 *
 * @author ssarmoka
 */
public class ShowCommandHandler implements EventHandler<ActionEvent> {

  private Command command;

  private Stage screenController;

  private Scene pushedScene;

  private CommandRegistry registry;

  private CommandManager cmdManager;

  private TextArea console;// = TextAreaBuilder.create().prefWidth(600).prefHeight(300).wrapText(true).build();

  private List<String> mandatoryParamList = new ArrayList<>();

  public static final double HEIGHT = 25;

  public static final double WIDTH = 180;

  public static final String MANDATORY_FIELD = "* : Indicates mandatory field";

  public static final String ASTRIKE = "*";

  public static final String SELECT_PATH = "Choose Directory";

  public static final String CONSOLE_PROMPT_TEXT = "Console output here... ";

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
   * @param cmdManager
   *
   * @param primaryStage
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
   * @param registry
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
    String menu = item.getParentMenu().getText();
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
    Scene scene = new Scene(grid, this.screenController.getWidth(), this.screenController.getHeight());
    showCommandControls(grid);

    this.screenController.setScene(scene);
  }

  private void closeForm() {

    this.screenController.setScene(popParentScene());
  }

  /**
   * @return
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
   * @param scene
   */
  private void showCommandControls(final GridPane grid) {

    Text mandtory_field_text = new Text("");
    // set scene title
    Text scenetitle = new Text(this.command.getModuleName() + " " + this.command.getName());
    scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    grid.add(scenetitle, 0, 0, 2, 1);

    // Displaying helptext
    TextArea helpText = new TextArea();
    helpText.setStyle("-fx-background-color: #dcecf5;");
    helpText.setWrapText(true);
    helpText.setText(this.command.getHelpText());
    helpText.setEditable(false);
    grid.add(helpText, 0, 1, 2, 2);

    // clearing console output , set prompt text and set background color
    // this.console.clear();
    // this.console.setPromptText("OUTPUT HERE");
    // this.console.setStyle("-fx-background-color: #b5c9c9;");
    int order = 4;

    grid.getColumnConstraints().clear();
    grid.getRowConstraints().clear();
    if (this.command.getDefinedParameters().size() > 0) {
      mandtory_field_text = new Text(MANDATORY_FIELD);
      mandtory_field_text.setFill(Color.RED);
    }

    HBox hbox1 = new HBox();
    hbox1.getChildren().add(mandtory_field_text);
    grid.add(hbox1, 0, 3, 1, 1);
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
        Text text = new Text(ASTRIKE);
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
        comboBox.setMinHeight(HEIGHT);
        comboBox.setPrefWidth(WIDTH);
        grid.add(comboBox, 1, order);
        break;

      case PASSWORD:
        final Label message = new Label("");
        Text text1 = new Text(ASTRIKE);
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
        pwBox.setMinHeight(HEIGHT);
        pwBox.setMaxWidth(WIDTH);
        pwBox.setPrefWidth(WIDTH);
        grid.add(pwBox, 1, order);
        break;

      case PATH:
        Label path = new Label(param.getName());
        Text text2 = new Text(ASTRIKE);
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
        Button pathSelector = new Button(SELECT_PATH);
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
        // pathSelector.setPrefWidth(WIDTH);
        // pathSelector.setMinHeight(HEIGHT);
        // selectedPath.setPrefWidth(WIDTH);
        // selectedPath.setMinHeight(HEIGHT);
        directorySelector.setSpacing(5);
        directorySelector.setPrefHeight(HEIGHT);
        directorySelector.setPrefWidth(WIDTH);
        grid.add(directorySelector, 1, order);
        // grid.add(pathSelector, 2, order);
        break;

      default: /* GENERIC */
        final Label genLabel = new Label(param.getName());
        Text text3 = new Text(ASTRIKE);
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
        userTextField.setMinHeight(HEIGHT);
        userTextField.setMaxWidth(WIDTH);
        userTextField.setId("text_" + param.getName());
        userTextField.setTooltip(toolTip);
        userTextField.setPromptText("Enter " + param.getName());
        grid.add(userTextField, 1, order);

      }

      order++;

    }

    int rowNum = grid.getChildren().size();
    // System.out.println("Row Number " + rowNum);

    HBox hbox = new HBox(10);

    Button ok = new Button("Ok");
    hbox.getChildren().add(ok);
    this.console = TextAreaBuilder.create().prefWidth(600).prefHeight(300).wrapText(true).build();
    this.guiOutput = new GUIOutput(this.console);
    this.console.setEditable(false);
    this.console.setId("console");
    this.console.setPromptText(CONSOLE_PROMPT_TEXT);

    // this.console.setStyle("-fx-background-color: #b5c9c9;");
    grid.add(this.console, 0, rowNum + 5, 2, 2);
    ExecuteCommandHandler cmdHandler = new ExecuteCommandHandler(popParentScene(), this.command, this.cmdManager,
        this.screenController, grid, this.mandatoryParamList, this.guiOutput);
    ok.setOnAction(cmdHandler);

    Button back = new Button("Back");
    hbox.getChildren().add(back);
    back.setOnAction(cmdHandler);
    // grid.add(hbox1, 1, rowNum + 1);
    grid.add(hbox, 1, rowNum + 1);

    // this.console = TextAreaBuilder.create().prefWidth(600).prefHeight(300).wrapText(true).build();
    // HBox console = new HBox();
    // console.getChildren().add(ta);

  }

  private void chooseDirectory(Stage primaryStage, TextField filePath) {

    DirectoryChooser directoryChooser = new DirectoryChooser();
    File selectedFile = directoryChooser.showDialog(primaryStage);
    directoryChooser.setInitialDirectory(this.cmdManager.getContextPathInfo().getCurrentWorkingDirectory().toFile());

    if (selectedFile != null) {

      filePath.setText(selectedFile.getAbsolutePath());
      Tooltip tp = new Tooltip(selectedFile.getAbsolutePath());

    }
  }

}
