val ScalaVersion = "2.12.10"

lazy val root = (project in file("."))
  .settings(
    organization := "org.test",
    name := "pureconfig-playground",
    scalaVersion := ScalaVersion,
    scalacOptions ++= Seq(),
    javaOptions += "-Xmx2G",
    libraryDependencies ++= Dependencies.Dependencies,
    parallelExecution in Test := true,
  )

