package com.devonfw.devcon.modules.devon4j.migrate.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public class MavenPropertyReplacement extends AbstractXmlMigration {

  private final String propertyName;

  private final String newPropertyName;

  private final String newValue;

  /**
   * The constructor.
   *
   * @param propertyName
   * @param newValue
   */
  public MavenPropertyReplacement(String propertyName, String newValue) {

    this(propertyName, newValue, propertyName);
  }

  /**
   * The constructor.
   *
   * @param propertyName
   * @param newValue
   * @param newPropertyName
   */
  public MavenPropertyReplacement(String propertyName, String newValue, String newPropertyName) {

    super();
    this.propertyName = propertyName;
    this.newPropertyName = newPropertyName;
    this.newValue = newValue;
  }

  @Override
  public boolean migrateXml(Document xml) throws Exception {

    Element properties = getChildElement(xml.getDocumentElement(), "properties");
    Element property = getChildElement(properties, this.propertyName);
    if (property == null) {
      return false;
    }
    if (this.newPropertyName.equals(this.propertyName)) {
      property.setTextContent(this.newValue);
    } else {
      Element newProperty = xml.createElement(this.newPropertyName);
      newProperty.setTextContent(this.newValue);
      properties.replaceChild(newProperty, property);
    }
    return true;
  }

}
