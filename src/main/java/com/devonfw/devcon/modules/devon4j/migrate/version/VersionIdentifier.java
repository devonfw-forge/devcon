package com.devonfw.devcon.modules.devon4j.migrate.version;

import java.util.Objects;

/**
 * Identifier of a project version.
 */
public class VersionIdentifier {

  /** {@link #getGroupId() GroupId} for OASP: {@value} */
  public static final String GROUP_ID_OASP = "io.oasp";

  /** {@link #getGroupId() GroupId} for OASP4J: {@value} */
  public static final String GROUP_ID_OASP4J = GROUP_ID_OASP + ".java";

  /** {@link #getGroupId() GroupId} for OASP4J modules: {@value} */
  public static final String GROUP_ID_OASP4J_MODULES = GROUP_ID_OASP4J + ".modules";

  /** {@link #getGroupId() GroupId} for OASP4J starters: {@value} */
  public static final String GROUP_ID_OASP4J_STARTERS = GROUP_ID_OASP4J + ".starters";

  /** {@link #getGroupId() GroupId} for OASP4J boms: {@value} */
  public static final String GROUP_ID_OASP4J_BOMS = GROUP_ID_OASP4J + ".boms";

  /** {@link #getGroupId() GroupId} for devonfw: {@value} */
  public static final String GROUP_ID_DEVON = "com.devonfw";

  /** {@link #getGroupId() GroupId} for devon4j: {@value} */
  public static final String GROUP_ID_DEVON4J = GROUP_ID_DEVON + ".java";

  /** {@link #getGroupId() GroupId} for devon4j modules: {@value} */
  public static final String GROUP_ID_DEVON4J_MODULES = GROUP_ID_DEVON4J + ".modules";

  /** {@link #getGroupId() GroupId} for devon4j starters: {@value} */
  public static final String GROUP_ID_DEVON4J_STARTERS = GROUP_ID_DEVON4J + ".starters";

  /** {@link #getGroupId() GroupId} for devon4j boms: {@value} */
  public static final String GROUP_ID_DEVON4J_BOMS = GROUP_ID_DEVON4J + ".boms";

  private final String groupId;

  private final String artifactId;

  private final String version;

  /**
   * The constructor.
   *
   * @param artifactId - see {@link #getArtifactId()}.
   * @param version - see {@link #getVersion()}.
   */
  public VersionIdentifier(String artifactId, String version) {

    this(null, artifactId, version);
  }

  /**
   * The constructor.
   *
   * @param groupId - see {@link #getGroupId()}.
   * @param artifactId - see {@link #getArtifactId()}.
   * @param version - see {@link #getVersion()}.
   */
  public VersionIdentifier(String groupId, String artifactId, String version) {

    super();
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
  }

  /**
   * @return the maven groupId (namespace)
   */
  public String getGroupId() {

    return this.groupId;
  }

  /**
   * @return the ID of the project artifact.
   */
  public String getArtifactId() {

    return this.artifactId;
  }

  /**
   * @return version the actual version.
   */
  public String getVersion() {

    return this.version;
  }

  /**
   * @param other the {@link VersionIdentifier} to match.
   * @return {@code true} if this {@link VersionIdentifier} pattern matches the given {@link VersionIdentifier},
   *         {@code false} otherwise.
   */
  public boolean matches(VersionIdentifier other) {

    if (!matches(this.groupId, other.groupId)) {
      return false;
    }
    if (!matches(this.artifactId, other.artifactId)) {
      return false;
    }
    if ((other.version != null) && !matches(this.artifactId, other.artifactId)) {
      return false;
    }
    return true;
  }

  private boolean matches(String pattern, String value) {

    if (pattern == null) {
      return true;
    }
    if (pattern.endsWith("*")) {
      if (value == null) {
        return pattern.length() == 1;
      }
      String prefix = pattern.substring(0, pattern.length() - 1);
      return value.startsWith(prefix);
    } else if (pattern.startsWith("*")) {
      if (value == null) {
        return pattern.length() == 1;
      }
      String suffix = pattern.substring(1);
      return value.endsWith(suffix);
    } else {
      return (pattern.equals(value));
    }
  }

  @Override
  public int hashCode() {

    return Objects.hash(this.groupId, this.artifactId, this.version);
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    VersionIdentifier other = (VersionIdentifier) obj;
    if (!Objects.equals(this.groupId, other.groupId)) {
      return false;
    } else if (!Objects.equals(this.artifactId, other.artifactId)) {
      return false;
    } else if (!Objects.equals(this.version, other.version)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {

    if (this.groupId == null) {
      return this.artifactId + ":" + this.version;
    } else {
      return this.groupId + ":" + this.artifactId + ":" + this.version;
    }
  }

  /**
   * @param version the {@link #getVersion() version}.
   * @return the {@link VersionIdentifier} for the specified devon4j {@link VersionIdentifier}.
   */
  public static VersionIdentifier ofDevon4j(String version) {

    return new VersionIdentifier("devon4j", version);
  }

  /**
   * @param version the {@link #getVersion() version}.
   * @return the {@link VersionIdentifier} for the specified oasp4j {@link VersionIdentifier}.
   */
  public static VersionIdentifier ofOasp4j(String version) {

    return new VersionIdentifier("oasp4j", version);
  }

}
