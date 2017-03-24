
name := "Akka-Assignment"

version := "1.0"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.17"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "org.scalatest" % "scalatest_2.11" % "3.0.1",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion
)

coverageExcludedPackages:="com.knoldus.Main.*"
