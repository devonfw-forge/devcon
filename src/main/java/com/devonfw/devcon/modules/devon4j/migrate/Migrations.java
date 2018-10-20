package com.devonfw.devcon.modules.devon4j.migrate;

import com.devonfw.devcon.modules.devon4j.migrate.builder.MigrationBuilder;
import com.devonfw.devcon.modules.devon4j.migrate.version.VersionIdentifier;
import com.devonfw.devcon.output.Output;

/**
 * TODO hohwille This type ...
 */
public class Migrations {

  public static Migrator devon4j(Output output) {

    return new MigrationBuilder(output, VersionIdentifier.ofOasp4j("2.6.0")) //

        .to(VersionIdentifier.ofOasp4j("2.6.1")) //
        .pom().replaceVariable("oasp4j.version", "2.6.1").and().next() //

        .to(VersionIdentifier.ofOasp4j("3.0.0")) //
        .pom().replaceVariable("oasp4j.version", "3.0.0").and() //
        .java().replace("org.hibernate.Query", "org.hibernate.query.Query")
        .replace("com.mysema.query.alias.Alias", "com.querydsl.core.alias.Alias")
        .replace("com.mysema.query.jpa.impl.JPAQuery", "com.querydsl.jpa.impl.JPAQuery").and() //
        .applicationProperties().replace("flyway.", "spring.flyway.").and().next() //

        .to(VersionIdentifier.ofDevon4j("3.0.0")) //
        .pom().replaceVariable("oasp4j.version", "3.0.0", "devon4j.version") //
        .replaceString("${oasp4j.version}", "${devon4j.version}") //
        .replaceDependency(new VersionIdentifier(VersionIdentifier.GROUP_ID_OASP4J, "oasp4j-bom", null),
            new VersionIdentifier(VersionIdentifier.GROUP_ID_DEVON4J_BOMS, "devon4j-bom", null))
        .replaceDependency(new VersionIdentifier(VersionIdentifier.GROUP_ID_OASP4J + "*", "oasp4j*", null),
            new VersionIdentifier(VersionIdentifier.GROUP_ID_DEVON4J + "*", "devon4j*", null))
        .and().java()
        .replace("net.sf.mmm.util.entity.api.GenericEntity", "com.devonfw.module.basic.common.api.entity.GenericEntity")
        .replace("net.sf.mmm.util.entity.api.RevisionedEntity",
            "com.devonfw.module.basic.common.api.entity.RevisionedEntity")
        .replace("net.sf.mmm.util.entity.api.MutableRevisionedEntity",
            "com.devonfw.module.basic.common.api.entity.RevisionedEntity")
        .replace("net.sf.mmm.util.entity.api.PersistenceEntity",
            "com.devonfw.module.basic.common.api.entity.PersistenceEntity")
        .replace("net.sf.mmm.util.transferobject.api.EntityTo", "com.devonfw.module.basic.common.api.to.AbstractEto")
        .and() //
        .next().build();
  }

}
