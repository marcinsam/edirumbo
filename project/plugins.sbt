addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.3")

//resolvers += "Flyway" at "https://flywaydb.org/repo"
resolvers += "Flyway" at "https://davidmweber.github.io/flyway-sbt.repo"

// Database migration
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.2.0")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")