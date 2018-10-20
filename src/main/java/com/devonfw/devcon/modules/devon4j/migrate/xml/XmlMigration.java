package com.devonfw.devcon.modules.devon4j.migrate.xml;

import org.w3c.dom.Document;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public interface XmlMigration {

  boolean migrateXml(Document xml) throws Exception;

}
