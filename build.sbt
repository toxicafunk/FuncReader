name := "FuncReader"

version := "1.0"

scalaVersion := "2.11.8"

// val html5parser = "nu.validator.htmlparser" % "htmlparser" % "1.2.1"

//val scales = "org.scalesxml" %% "scales-xml" % "0.5.0"

resolvers += "Scales Repo" at "http://scala-scales.googlecode.com/svn/repo"

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.13.0" % "test",
  "nu.validator.htmlparser" % "htmlparser" % "1.2.1",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.4",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scala-lang.modules" %% "scala-swing" % "1.0.1"
)