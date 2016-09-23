package com.devonfw.devcon.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * TODO This is the class which launch Devcon GUI.
 *
 * @author ssarmoka
 */
public class GUIAppManager extends Application {

  public static CommandRegistry registry;

  public static CommandManager cmdManager;

  public static void main(CommandRegistry registry, CommandManager commandManager, String[] args) {

    GUIAppManager.registry = registry;
    GUIAppManager.cmdManager = commandManager;

    main(args);
  }

  public static void main(String[] args) {

    launch(args);
  }

  private Stage primaryStage;

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(Stage primaryStage) throws Exception {

    try {

      // To load logo from eclipse
      // String image = GUIAppManager.class.getClassLoader().getResource("Logo_Devcon-background.jpg").toExternalForm();
      // To load logo from Devcon jar uncomment following
      String image =
          GUIAppManager.class.getClassLoader().getResource("resources/Logo_Devcon-background.jpg").toExternalForm();

      this.primaryStage = primaryStage;

      // TODO Auto-generated method stub
      primaryStage.setTitle("Devcon");

      BorderPane borderPane = new BorderPane();
      borderPane.setTop(getMenus());

      borderPane.setStyle("-fx-background-image: url('" + image + "'); " + "-fx-background-position: center center; "
          + "-fx-background-repeat: stretch; -fx-background-color: #5b5150;");

      Scene scene = new Scene(borderPane, 700, 650); // 889, 600
      primaryStage.setScene(scene);
      primaryStage.show();
    } catch (Exception e) {
      // TODO: handle exception
      e.printStackTrace();
    }

  }

  public MenuBar getMenus() {

    // anti-pattern. These kind of functionality a) should NOT be in generic "Utils" classed
    // and b) not instantiated manually (no "New"). Either you set them as instances or you usse single-tons (jn Java:
    // statics)
    // Need refactoring
    Utils utils = new Utils();

    final MenuBar menuBar = new MenuBar();

    List<CommandModuleInfo> modules =
        utils.sortModules(registry.getCommandModules(), new NumericSortComparator<CommandModuleInfo>());
    List<Menu> menuList = new ArrayList<>();
    for (int i = 0; i < modules.size(); i++) {

      if (!modules.get(i).isVisible())
        continue;

      final Menu menu = new Menu(modules.get(i).getName());

      Optional<CommandModuleInfo> commands = GUIAppManager.registry.getCommandModule(modules.get(i).getName());
      Collection<Command> sortedCommands =
          utils.sortCommands(commands.get().getCommands(), new NumericSortComparator<Command>());
      Iterator<Command> itrCommands = sortedCommands.iterator();
      while (itrCommands.hasNext()) {
        Command cmd = itrCommands.next();
        MenuItem item = new MenuItem(cmd.getName());
        menu.getItems().add(item);
        item.setOnAction(new ShowCommandHandler(cmd, GUIAppManager.cmdManager, this.primaryStage));
      }
      if (i == 0) {
        MenuItem item = new MenuItem("Exit");
        menu.getItems().add(item);
        item.setOnAction(new ShowCommandHandler());
      }
      menuBar.getMenus().add(menu);
    }
    return menuBar;

  }

  public Stage getStage() {

    return this.primaryStage;
  }
}
