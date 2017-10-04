package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import play.api.i18n.{ I18nSupport, Messages, MessagesApi }

case class EngineRecord(
  name: String = "",
  path: String = "",
  config: String = ""
) {
}

case class EngineRecords(
  var items: List[EngineRecord] = List[EngineRecord]()
) {
  def add(er: EngineRecord) {
    delete(er.name)
    items = er +: items
  }
  def delete(name: String) {
    items = items.filter(_.name != name)
  }
  def get(name: String): EngineRecord = {
    for (item <- items) if (item.name == name) return item
    EngineRecord()
  }
  def available: List[String] = (for (item <- items) yield item.name).toList
}

object Utils {
  val userForm: Form[EngineRecord] = Form(
    mapping(
      "name" -> nonEmptyText,
      "path" -> nonEmptyText,
      "config" -> text
    )(EngineRecord.apply)(EngineRecord.unapply)
  )

  def WriteStringToFile(path: String, content: String) {
    org.apache.commons.io.FileUtils.writeStringToFile(
      new java.io.File(path),
      content,
      null.asInstanceOf[String]
    )
  }

  def ReadFileToString(path: String): String = {
    val f = new java.io.File(path)
    if (!f.exists()) return null
    org.apache.commons.io.FileUtils.readFileToString(
      f,
      null.asInstanceOf[String]
    )
  }

  var enginerecords = EngineRecords()

  def loadrecords {
    val content = ReadFileToString("enginerecords.json")
    enginerecords = EngineRecords()
    if (content != null) enginerecords = upickle.default.read[EngineRecords](content)
  }

  def saverecords {
    WriteStringToFile("enginerecords.json", upickle.default.write[EngineRecords](enginerecords))
  }
}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
  cc: ControllerComponents
) extends AbstractController(cc) with I18nSupport {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    import Utils._
    loadrecords
    Ok(views.html.index(userForm, enginerecords))
  }

  def delete(name: String) = Action { implicit request: Request[AnyContent] =>
    import Utils._
    loadrecords
    enginerecords.delete(name)
    saverecords
    Ok(views.html.index(userForm, enginerecords))
  }

  def edit(name: String) = Action { implicit request: Request[AnyContent] =>
    import Utils._
    loadrecords
    Ok(views.html.index(userForm.fill(enginerecords.get(name)), enginerecords))
  }

  def submit() = Action { implicit request: Request[AnyContent] =>
    import Utils._
    userForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(views.html.index(formWithErrors, enginerecords))
      },
      engineRecord => {
        /* binding success, you get the actual value. */
        enginerecords.add(engineRecord)
        saverecords
        Redirect(routes.HomeController.index())
      }
    )
  }
}
