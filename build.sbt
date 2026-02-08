name := """API-RESTful"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.18"

libraryDependencies ++= Seq(
  guice,
  // 1. Permite o Play falar JDBC (SQL) no Java
  javaJdbc,
  // 2. Gerencia as migrations
  evolutions,
  // 3. O Driver oficial do MySQL para Java 17+
  "com.mysql" % "mysql-connector-j" % "8.3.0",
  javaJpa, // A API do JPA para Play
  "org.hibernate.orm" % "hibernate-core" % "6.4.4.Final",
  "org.mindrot" % "jbcrypt" % "0.4",
  "com.auth0" % "java-jwt" % "4.4.0"
)

libraryDependencies += javaJdbc % Test
libraryDependencies += "com.h2database" % "h2" % "1.4.200" % Test

libraryDependencies += "org.glassfish" % "javax.el" % "3.0.0" % Test

libraryDependencies += "org.mockito" % "mockito-core" % "5.10.0" % Test