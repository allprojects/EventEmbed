name := "tuple-join"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "com.chuusai" % "shapeless" % "2.0.0-SNAPSHOT" cross CrossVersion.full changing(),
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
  "com.espertech" % "esper" % "4.10.0",
  "com.chuusai" % "shapeless_2.10.2" % "2.0.0-M1"
)

resolvers ++= Seq(
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)
