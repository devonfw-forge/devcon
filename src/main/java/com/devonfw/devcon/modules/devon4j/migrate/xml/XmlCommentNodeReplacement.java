package com.devonfw.devcon.modules.devon4j.migrate.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of {@link AbstractXmlMigration} for a simple "comment node" replacement
 */
public class XmlCommentNodeReplacement extends AbstractXmlMigration {

  private String search;

  private String replacement;

  public static final String SINGLE_SPACE = " ";

  /**
   * The cnstructor.
   *
   * @param search the {@link String} to search for.
   * @param replacement the replacement for the given {@code search} {@link String}.
   */
  public XmlCommentNodeReplacement(String search, String replacement) {

    super();
    this.search = search;
    this.replacement = replacement;
  }

  @Override
  public boolean migrateXml(Document xml) throws Exception {

    return migrateXmlCommentNode(xml.getChildNodes());
  }

  private boolean migrateXmlCommentNode(NodeList childNodes) {

    boolean updated = false;

    for (int i = 0; i < childNodes.getLength(); i++) {
      Node node = childNodes.item(i);
      short nodeType = node.getNodeType();
      if (nodeType == Node.COMMENT_NODE) {
        if (this.search.trim().replaceAll("\\s+", SINGLE_SPACE)
            .equals(node.getNodeValue().trim().replaceAll("\\s+", SINGLE_SPACE))) {
          node.setNodeValue(this.replacement);
          updated = true;
        }
      }
    }
    return updated;
  }
}
