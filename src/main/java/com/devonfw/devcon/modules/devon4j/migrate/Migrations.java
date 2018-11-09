package com.devonfw.devcon.modules.devon4j.migrate;

import com.devonfw.devcon.modules.devon4j.migrate.builder.MigrationBuilder;
import com.devonfw.devcon.modules.devon4j.migrate.file.FileFilterPattern;
import com.devonfw.devcon.modules.devon4j.migrate.line.QueryDslJpaQueryLineMigration;
import com.devonfw.devcon.modules.devon4j.migrate.version.VersionIdentifier;
import com.devonfw.devcon.output.Output;

/**
 * The "configuration" with the business logic for the migration as fluent API (DSL).
 */
public class Migrations {

  /**
   * @param output the {@link Output}.
   * @return the {@link Migrator} for devon4j.
   */
  public static Migrator devon4j(Output output) {

    return new MigrationBuilder(output, VersionIdentifier.ofOasp4j("2.6.0")) //

        .to(VersionIdentifier.ofOasp4j("2.6.1")) //
        .pom().replaceProperty("oasp4j.version", "2.6.1").and().next() //

        .to(VersionIdentifier.ofOasp4j("3.0.0")) //
        .pom().replaceProperty("oasp4j.version", "3.0.0").and() //
        .java().replace("org.hibernate.Query", "org.hibernate.query.Query")
        .replace("com.mysema.query.alias.Alias", "com.querydsl.core.alias.Alias")
        .replace("com.mysema.query.jpa.impl.JPAQuery", "com.querydsl.jpa.impl.JPAQuery")
        .replace("com.mysema.query.types.path.EntityPathBase", "com.querydsl.core.types.dsl.EntityPathBase")
        .replace("query.clone().count()", "query.clone().fetchCount()").replace("query.count()", "query.fetchCount()")
        .replace("query.list(expr)", "query.select(expr).fetch()").replace("query.list()", "query.fetch()")
        .replace("query.firstResult(", "query.fetchFirst(").replace("query.uniqueResult(", "query.fetchUnique(")
        .replace("query.listResults(", "query.fetchResults(").add(new QueryDslJpaQueryLineMigration()).and() //
        .applicationProperties().replace("flyway.", "spring.flyway.").and().next() //

        .to(VersionIdentifier.ofDevon4j("3.0.0")) //
        .pom().replaceProperty("oasp4j.version", "3.0.0-SNAPSHOT", "devon4j.version") //
        .replaceString("${oasp4j.version}", "${devon4j.version}") //
        .replaceDependency(new VersionIdentifier(VersionIdentifier.GROUP_ID_OASP4J, "oasp4j-bom", null),
            new VersionIdentifier(VersionIdentifier.GROUP_ID_DEVON4J_BOMS, "devon4j-bom", null))
        .replaceDependency(new VersionIdentifier(VersionIdentifier.GROUP_ID_OASP4J + "*", "oasp4j*", null),
            new VersionIdentifier(VersionIdentifier.GROUP_ID_DEVON4J + "*", "devon4j*", null))
        .addDependency(new VersionIdentifier(null, "*-api", null),
            new VersionIdentifier(VersionIdentifier.GROUP_ID_OASP4J_MODULES, "oasp4j-jpa", "3.0.0"))
        .and().java() //
        .replace("io.oasp.module.rest.service.impl.json.ObjectMapperFactory",
            "com.devonfw.module.json.common.base.ObjectMapperFactory")
        .replace("io.oasp.module.jpa.dataaccess.api.MutablePersistenceEntity",
            "com.devonfw.module.basic.common.api.entity.RevisionedPersistenceEntity")
        .replace("ApplicationPersistenceEntity implements ApplicationEntity, MutablePersistenceEntity<Long>",
            "ApplicationPersistenceEntity implements ApplicationEntity, RevisionedPersistenceEntity<Long>")
        .replace("ApplicationDao<ENTITY extends MutablePersistenceEntity<Long>>",
            "ApplicationDao<ENTITY extends com.devonfw.module.basic.common.api.entity.PersistenceEntity<Long>>")
        .replace("ApplicationRevisionedDao<ENTITY extends MutablePersistenceEntity<Long>>",
            "ApplicationRevisionedDao<ENTITY extends RevisionedPersistenceEntity<Long>>")
        .replace("DaoImpl<ENTITY extends MutablePersistenceEntity<Long>>",
            "DaoImpl<ENTITY extends RevisionedPersistenceEntity<Long>>")
        .replace("MutablePersistenceEntity", "RevisionedPersistenceEntity")
        .replace("io.oasp.module.jpa.common.api.to.", "hack.oasp.module.jpa.common.api.to.")
        .replace("io.oasp.module.", "com.devonfw.module.")
        .replace("hack.oasp.module.jpa.common.api.to.", "io.oasp.module.jpa.common.api.to.")
        .replace("net.sf.mmm.util.entity.api.GenericEntity", "com.devonfw.module.basic.common.api.entity.GenericEntity")
        .replace("net.sf.mmm.util.entity.api.RevisionedEntity",
            "com.devonfw.module.basic.common.api.entity.RevisionedEntity")
        .replace("net.sf.mmm.util.entity.api.MutableRevisionedEntity",
            "com.devonfw.module.basic.common.api.entity.RevisionedEntity")
        .replace("net.sf.mmm.util.entity.api.PersistenceEntity",
            "com.devonfw.module.basic.common.api.entity.PersistenceEntity")
        .replace("net.sf.mmm.util.entity.api.MutableGenericEntity",
            "com.devonfw.module.basic.common.api.entity.GenericEntity")
        .replace("net.sf.mmm.util.transferobject.api.CompositeTo", "com.devonfw.module.basic.common.api.to.AbstractCto")
        .replace("net.sf.mmm.util.transferobject.api.AbstractTransferObject",
            "com.devonfw.module.basic.common.api.to.AbstractTo")
        .replace("net.sf.mmm.util.transferobject.api.TransferObject",
            "com.devonfw.module.basic.common.api.to.AbstractTo")
        .replace("TransferObject", "AbstractTo")
        .replace("AbstractCto extends CompositeTo",
            "AbstractCto extends com.devonfw.module.basic.common.api.to.AbstractCto")
        .replace("CompositeTo", "AbstractCto")
        .replace("AbstractEto extends EntityTo<Long>",
            "AbstractEto extends com.devonfw.module.basic.common.api.to.AbstractEto")
        .replace("EntityTo<Long>", "AbstractEto").replace("MutableGenericEntity<", "GenericEntity<")
        .replace("net.sf.mmm.util.transferobject.api.EntityTo", "com.devonfw.module.basic.common.api.to.AbstractEto")
        .replace(".OaspPackage", ".Devon4jPackage").replace("OaspPackage ", "Devon4jPackage ")
        .replace("OaspPackage.", "Devon4jPackage.")
        .replace("import com.devonfw.module.basic.common.api.to.AbstractEto;", "",
            FileFilterPattern.accept("AbstractEto\\.java"))
        .replace("import com.devonfw.module.basic.common.api.to.AbstractCto;", "",
            FileFilterPattern.accept("AbstractCto\\.java"))
        .replaceRegex("implements ([a-zA-Z0-9_]*)Dao",
            "implements $1Dao, io.oasp.module.jpa.common.base.LegacyDaoQuerySupport<$1Entity>",
            FileFilterPattern.reject("Application(MasterData)?DaoImpl\\.java")) //
        .and() //
        .next().build();
  }

}
