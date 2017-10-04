import com.typesafe.sbt.SbtScalariform._
import scalariform.formatter.preferences._

name := """engineserver"""
organization := "enginservermaker"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
	defaultScalariformSettings,

	ScalariformKeys.preferences := ScalariformKeys.preferences.value
	.setPreference(FormatXml, false)
	.setPreference(DoubleIndentClassDeclaration, false)
	.setPreference(DanglingCloseParenthesis, Preserve),

	excludeFilter in scalariformFormat := ((excludeFilter in scalariformFormat).value ||
	"Routes.scala" ||
	"ReverseRoutes.scala" ||
	"JavaScriptReverseRoutes.scala" ||
	"RoutesPrefix.scala")
)

scalaVersion := "2.11.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "com.lihaoyi" %% "upickle" % "0.4.3"
libraryDependencies += "commons-io" % "commons-io" % "2.5"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "enginservermaker.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "enginservermaker.binders._"

/////////////////////////////////////////////////////

addCommandAlias("c","~compile")
addCommandAlias("r","run -Dhttp.port=9001")
