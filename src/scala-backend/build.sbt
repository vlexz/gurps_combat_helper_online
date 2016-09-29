name := """ggmtools"""

version := "1.0"

lazy val `ggmtools` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(jdbc, cache, ws, filters)

unmanagedResourceDirectories in Test <+= baseDirectory(_ / "target/web/public/test")

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "1.0.1"

libraryDependencies += "com.github.t3hnar" % "scala-bcrypt_2.11" % "2.5"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test"

libraryDependencies += "org.mockito" % "mockito-core" % "1.10.19"

fork in run := true