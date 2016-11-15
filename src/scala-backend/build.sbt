name := """ggmtools"""

version := "0.1"

lazy val commonSettings = Seq(scalaVersion := "2.11.8")

lazy val ggmtools = project in file(".") enablePlugins PlayScala settings (commonSettings: _*)

lazy val preloader = project dependsOn ggmtools settings (commonSettings: _*)

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(jdbc, cache, ws, filters)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.6"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "1.0.1"

libraryDependencies += "com.github.t3hnar" % "scala-bcrypt_2.11" % "2.5"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test"

libraryDependencies += "com.sksamuel.scrimage" %% "scrimage-core" % "2.1.7"

libraryDependencies += "org.mockito" % "mockito-core" % "1.10.19"

fork in run := true