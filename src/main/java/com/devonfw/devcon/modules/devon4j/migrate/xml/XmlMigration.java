package com.devonfw.devcon.modules.devon4j.migrate.xml;

import org.w3c.dom.Document;

/**
 * Migration of XML.
 */
public interface XmlMigration {

  /**
   * @param xml the XML {@link Document} to migrate. Will be modified with any change needed for migration.
   * @return {@code true} if the XML was modified, {@code false} otherwise.
   * @throws Exception on error.
   */
  boolean migrateXml(Document xml) throws Exception;

}
