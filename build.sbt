lazy val scalatraVersion = "2.3.1"

lazy val root = (project in file(".")).settings(
  organization := "com.server",
  name := "server",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.6",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
  libraryDependencies ++= Seq(
    "org.scalatra"      %% "scalatra"          % scalatraVersion,
    "org.scalatra"      %% "scalatra-scalate"  % scalatraVersion,
    "org.scalatra"      %% "scalatra-specs2"   % scalatraVersion    % "test",
    "ch.qos.logback"    %  "logback-classic"   % "1.1.3"            % "runtime",
    "org.eclipse.jetty" %  "jetty-webapp"      % "9.2.10.v20150310" % "container",
    "javax.servlet"     %  "javax.servlet-api" % "3.1.0"            % "provided",
    "com.typesafe.slick" %% "slick" % "3.2.0",
    "com.mchange" % "c3p0" % "0.9.5.2",
    "org.postgresql" % "postgresql" % "9.4-1206-jdbc41",
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",
    "joda-time" % "joda-time" % "2.7",
    "org.joda" % "joda-convert" % "1.7",
    "com.fazecast" % "jSerialComm" % "1.3.11"
  )
).settings(jetty(): _*)
