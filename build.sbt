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
  "org.hibernate.orm" % "hibernate-core" % "6.4.4.Final"
)