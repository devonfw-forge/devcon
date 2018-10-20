package com.devonfw.devcon.modules.devon4j.migrate.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public class XmlStringReplacement extends AbstractXmlMigration {

  private String search;

  private String replacement;

  /**
   * The constructor.
   *
   * @param search
   * @param replacement
   */
  public XmlStringReplacement(String search, String replacement) {

    super();
    this.search = search;
    this.replacement = replacement;
  }

  @Override
  public boolean migrateXml(Document xml) throws Exception {

    return migrateXmlElement(xml.getDocumentElement());
  }

  private boolean migrateXmlElement(Element element) {

    boolean updated = false;
    NodeList childNodes = element.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node node = childNodes.item(i);
      short nodeType = node.getNodeType();
      if (nodeType == Node.ELEMENT_NODE) {
        boolean childUpdated = migrateXmlElement((Element) node);
        if (childUpdated) {
          updated = true;
        }
      } else if (nodeType == Node.TEXT_NODE) {
        Text text = (Text) node;
        if (this.search.equals(text.getData())) {
          text.setData(this.replacement);
          updated = true;
        }
      }
    }
    return updated;
  }

}
