package com.devonfw.devcon.modules.devon4j.migrate.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.devonfw.devcon.modules.devon4j.migrate.xml.XmlMigration;
import com.devonfw.devcon.output.Output;

/**
 * Implementation of {@link FileMigration} for XML {@link File}.
 */
public class XmlFileMigration extends FileMigration implements XmlMigration {

  /** {@link Pattern} for {@code pom.xml}. */
  public static final Pattern POM_XML_PATTERN = Pattern.compile("pom\\.xml");

  private final List<XmlMigration> migrations;

  /**
   * The constructor.
   *
   * @param output the {@link Output}.
   * @param namePattern the {@link Pattern} to match the filename.
   */
  public XmlFileMigration(Output output, Pattern namePattern) {

    super(output, namePattern);
    this.migrations = new ArrayList<>();
  }

  @Override
  protected void migrateFile(File file) throws Exception {

    Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
    boolean change = migrateXml(xml);
    if (change) {
      this.output.showMessage("Migrating file: %s", file.getPath());
      Result target = new StreamResult(file);
      TransformerFactory.newInstance().newTransformer().transform(new DOMSource(xml), target);
    }
  }

  public boolean migrateXml(Document xml) throws Exception {

    boolean change = false;
    for (XmlMigration migration : this.migrations) {
      boolean changed = migration.migrateXml(xml);
      if (changed) {
        change = true;
      }
    }
    return change;
  }

  /**
   * @return migrations
   */
  public List<XmlMigration> getMigrations() {

    return this.migrations;
  }

}
