package com.devonfw.devcon.modules.devon4j.migrate.builder;

import com.devonfw.devcon.modules.devon4j.migrate.file.XmlFileMigration;
import com.devonfw.devcon.modules.devon4j.migrate.version.VersionIdentifier;
import com.devonfw.devcon.modules.devon4j.migrate.xml.MavenDependencyReplacement;
import com.devonfw.devcon.modules.devon4j.migrate.xml.MavenPropertyReplacement;
import com.devonfw.devcon.modules.devon4j.migrate.xml.XmlStringReplacement;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public class PomXmlMigrationBuilder extends XmlMigrationBuilder {

  /**
   * The constructor.
   *
   * @param parent
   */
  public PomXmlMigrationBuilder(MigrationStepBuilder parent) {

    super(parent, XmlFileMigration.POM_XML_PATTERN);
  }

  public PomXmlMigrationBuilder replaceDependency(VersionIdentifier pattern, VersionIdentifier replacement) {

    this.migration.getMigrations().add(new MavenDependencyReplacement(pattern, replacement));
    return this;
  }

  public PomXmlMigrationBuilder replaceVariable(String variable, String value) {

    return replaceVariable(variable, value, variable);
  }

  public PomXmlMigrationBuilder replaceVariable(String variable, String value, String newVariable) {

    this.migration.getMigrations().add(new MavenPropertyReplacement(variable, value, newVariable));
    return this;
  }

  public PomXmlMigrationBuilder replaceString(String search, String replacement) {

    this.migration.getMigrations().add(new XmlStringReplacement(search, replacement));
    return this;
  }

}
