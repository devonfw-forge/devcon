package com.devonfw.devcon.modules.oasp4j.migrate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author VAPADWAL
 *
 */
public class XmlUpdater {

  public void updatePom(String filePath)
      throws FileNotFoundException, IOException, XmlPullParserException, ParseException {

    JSONParser parser = new JSONParser();
    boolean isDependency = false;

    FileReader file = null;
    file = new FileReader(new File(this.getClass().getClassLoader()
        .getResource("xmlprops_" + new PropertyFileReader().getLatetOasp4jVersion() + ".json").getFile()));
    MavenXpp3Reader reader = new MavenXpp3Reader();

    Object obj = parser.parse(file);

    JSONObject jsonObject = (JSONObject) obj;

    JSONArray msg = (JSONArray) jsonObject.get("xml");
    Iterator<JSONObject> iterator = msg.iterator();
    while (iterator.hasNext()) {
      JSONObject jsObject = iterator.next();

      JSONArray update1 = (JSONArray) jsObject.get("update1");
      if (update1 != null) {
        Iterator<JSONObject> iteratorUpd1 = update1.iterator();
        while (iteratorUpd1.hasNext()) {
          JSONObject jsObjectUpd1 = iteratorUpd1.next();
          String rule = (String) jsObjectUpd1.get("rulename");
          String ver = (String) jsObjectUpd1.get("version");
          String pomPath = (String) jsObjectUpd1.get("path");
          String artifactId = (String) jsObjectUpd1.get("artifactId");
          Model model = reader.read(new FileReader(filePath + pomPath));

          if (compareMavenVersion(ver, model.getProperties().getProperty(artifactId)) > 0) {
            model.getProperties().setProperty(artifactId, ver);
            new MavenXpp3Writer().write(new FileOutputStream(new File(filePath + pomPath)), model);
          }
        }
      }
      JSONArray update2 = (JSONArray) jsObject.get("update2");
      if (update2 != null) {
        Iterator<JSONObject> iteratorUpd2 = update2.iterator();
        while (iteratorUpd2.hasNext()) {
          JSONObject jsObjectUpd2 = iteratorUpd2.next();
          String rule = (String) jsObjectUpd2.get("rulename");
          String artifactId = (String) jsObjectUpd2.get("artifactId");
          String groupId = (String) jsObjectUpd2.get("groupId");
          String pomPath = (String) jsObjectUpd2.get("path");
          Model model = reader.read(new FileReader(filePath + pomPath));
          for (Dependency dependency : model.getDependencies()) {
            if (dependency.getArtifactId().equals(artifactId)) {
              isDependency = true;
              break;
            }
          }
          if (rule.startsWith("Add") && !isDependency) {
            Dependency addDependency = new Dependency();
            addDependency.setGroupId(groupId);
            addDependency.setArtifactId(artifactId);
            model.addDependency(addDependency);
            new MavenXpp3Writer().write(new FileOutputStream(new File(filePath + pomPath)), model);
          }

        }
      }

    }
  }

  public int compareMavenVersion(String version1, String version2) {

    ArtifactVersion mavenVersion1 = new DefaultArtifactVersion(version1);
    ArtifactVersion mavenVersion2 = new DefaultArtifactVersion(version2);
    int result = mavenVersion1.compareTo(mavenVersion2);
    return result;
  }

}
