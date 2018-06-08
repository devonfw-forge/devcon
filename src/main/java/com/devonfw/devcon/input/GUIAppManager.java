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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.devonfw.devcon.Devcon;
import com.devonfw.devcon.common.api.Command;
import com.devonfw.devcon.common.api.CommandManager;
import com.devonfw.devcon.common.api.CommandModuleInfo;
import com.devonfw.devcon.common.api.CommandRegistry;
import com.devonfw.devcon.common.utils.Constants;
import com.devonfw.devcon.common.utils.Utils;
import com.google.common.base.Optional;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * TODO This is the class which launch Devcon GUI.
 *
 * @author ssarmoka
 */
public class GUIAppManager extends Application {

  /**
   * CommandRegistry instance
   */
  public static CommandRegistry registry;

  /**
   * CommandManager instance
   */
  public static CommandManager cmdManager;

  private final int MENU_NUMBER = 9;

  private Stage primaryStage;

  /**
   * @param registry CommandRegistry instance
   * @param commandManager CommandManager instance
   * @param args arguments
   */
  public static void main(CommandRegistry registry, CommandManager commandManager, String[] args) {

    GUIAppManager.registry = registry;
    GUIAppManager.cmdManager = commandManager;

    main(args);
  }

  /**
   * @param args input arguments
   */
  public static void main(String[] args) {

    launch(args);
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("hiding")
  @Override
  public void start(Stage primaryStage) throws Exception {

    try {

      String root = (Devcon.IN_EXEC_JAR) ? "resources/" : "";

      String image = GUIAppManager.class.getClassLoader().getResource(root + Constants.DEVCON_LOGO).toExternalForm();
      String icon = GUIAppManager.class.getClassLoader().getResource(root + Constants.DEVCON_ICON).toExternalForm();

      this.primaryStage = primaryStage;

      primaryStage.setTitle("Devcon");

      BorderPane borderPane = new BorderPane();
      borderPane.setTop(getMenus());

      borderPane.setStyle("-fx-background-image: url('" + image + "'); " + "-fx-background-position: center center; "
          + "-fx-background-repeat: stretch; -fx-background-color: #5b5150;");

      Scene scene = new Scene(borderPane, 700, 800);
      primaryStage.setScene(scene);
      primaryStage.getIcons().add(new Image(icon));
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

    List<CommandModuleInfo> modules = utils.sortModules(registry.getCommandModules(),
        new NumericSortComparator<CommandModuleInfo>());

    for (int i = 0; i <= this.MENU_NUMBER; i++) {

      if (!modules.get(i).isVisible())
        continue;

      final Menu menu = new Menu(modules.get(i).getName());

      Optional<CommandModuleInfo> commands = GUIAppManager.registry.getCommandModule(modules.get(i).getName());
      Collection<Command> sortedCommands = utils.sortCommands(commands.get().getCommands(),
          new NumericSortComparator<Command>());
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

    if (modules.size() > this.MENU_NUMBER) {
      List<CommandModuleInfo> dropDownModule = modules.subList(this.MENU_NUMBER + 1, modules.size());
      Menu dropDownMenu = new Menu("other modules");
      for (int l = 0; l < dropDownModule.size(); l++) {
        // fetch modulename
        StringBuilder moduleName = new StringBuilder(dropDownModule.get(l).getName());
        // create menu for module
        Menu newModule = new Menu(moduleName.toString());
        // add menu as menuiteam
        dropDownMenu.getItems().add(newModule);

        Optional<CommandModuleInfo> commands = GUIAppManager.registry.getCommandModule(moduleName.toString());
        Collection<Command> sortedCommands = utils.sortCommands(commands.get().getCommands(),
            new NumericSortComparator<Command>());
        Iterator<Command> itrCommands = sortedCommands.iterator();
        while (itrCommands.hasNext()) {
          Command cmd = itrCommands.next();
          MenuItem item = new MenuItem(cmd.getName());
          newModule.getItems().add(item);
          item.setOnAction(new ShowCommandHandler(cmd, GUIAppManager.cmdManager, this.primaryStage));
        }

      }
      menuBar.getMenus().add(dropDownMenu);
    }

    return menuBar;

  }

}
