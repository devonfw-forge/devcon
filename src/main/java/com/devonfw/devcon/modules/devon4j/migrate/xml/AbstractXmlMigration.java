package com.devonfw.devcon.modules.devon4j.migrate.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TODO hohwille This type ...
 *
 * @since 1.5.0
 */
public abstract class AbstractXmlMigration implements XmlMigration {

  protected void setText(Element element, String text) {

    if ((element != null) && (text != null)) {
      element.setTextContent(text);
    }
  }

  protected String getText(Element element) {

    if (element == null) {
      return null;
    }
    return element.getTextContent();
  }

  protected Element getChildElement(Element parent, String tag) {

    if (parent == null) {
      return null;
    }
    NodeList childNodes = parent.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node node = childNodes.item(i);
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) node;
        if (element.getTagName().equals(tag)) {
          return element;
        }
      }
    }
    return null;
  }

}
