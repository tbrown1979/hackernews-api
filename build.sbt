import com.typesafe.sbt.SbtStartScript

organization  := "com.example"

version       := "0.1"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.1.4"
  val sprayV = "1.1.1"
  Seq(
    "io.spray"            %   "spray-can"         % sprayV,
    "io.spray"            %   "spray-routing"     % sprayV,
    "io.spray"            %   "spray-client"      % sprayV,
    "io.spray"            %   "spray-json_2.10"   % "1.2.5",
    "com.ning"            %   "async-http-client" % "1.7.14",
    "io.spray"            %   "spray-testkit"     % sprayV,
    "com.typesafe.akka"   %%  "akka-actor"        % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"      % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"       % "2.3.7" % "test",
    "org.jsoup" % "jsoup" % "1.6.3"
  )
}

Revolver.settings

seq(SbtStartScript.startScriptForClassesSettings: _*)
