lazy val app = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(FlywayPlugin)
  .settings(
    name := "edirumbo-app",
    version := "0.1",
    scalaVersion := "2.12.0",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % "2.7.3",
      "com.typesafe.play" %% "play-slick" % "4.0.2",
//      "com.typesafe.slick" %% "slick" % "3.3.2",
      "com.h2database" % "h2" % "1.4.199",
      "org.flywaydb" %% "flyway-play" % "5.3.2",
      guice,
      specs2 % Test,
      "org.typelevel" %% "cats-core" % "2.1.1"
    ),
    routesImport ++= Seq(
      "java.time.LocalDateTime",
      "pl.marboz.scala.edirumbo.utils.Binders._"
    )
  )

mainClass in assembly := Some("play.core.server.ProdServerStart")
fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

assemblyMergeStrategy in assembly := {
  case manifest if manifest.contains("MANIFEST.MF") =>
    // We don't need manifest files since sbt-assembly will create
    // one with the given settings
    MergeStrategy.discard
  case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
    // Keep the content for all reference-overrides.conf files
    MergeStrategy.concat
  case x =>
    // For all the other files, use the default sbt-assembly merge strategy
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}