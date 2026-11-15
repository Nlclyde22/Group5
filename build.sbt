name := "statusbot"
version := "1.0"
scalaVersion := "2.13.14"

libraryDependencies ++= Seq(
  "net.dv8tion" % "JDA" % "5.0.2",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

assembly / mainClass := Some("StatusBot")
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
