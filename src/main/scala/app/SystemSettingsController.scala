package app

import service.AccountService
import util.{SystemSettings, AdminAuthenticator}
import jp.sf.amateras.scalatra.forms._
import org.scalatra.FlashMapSupport

class SystemSettingsController extends SystemSettingsControllerBase
  with AccountService with AdminAuthenticator

trait SystemSettingsControllerBase extends ControllerBase with FlashMapSupport {
  self: AccountService with AdminAuthenticator =>

  private case class SystemSettingsForm(allowAccountRegistration: Boolean)

  private val form = mapping(
    "allowAccountRegistration" -> trim(label("Account registration", boolean()))
  )(SystemSettingsForm.apply)


  get("/admin/system")(adminOnly {
    admin.html.system(flash.get("info"))
  })

  post("/admin/system", form)(adminOnly { form =>
    SystemSettings.saveSystemSettings(context.systemSettings.copy(
      allowAccountRegistration = form.allowAccountRegistration)
    )
    flash += "info" -> "System settings has been updated."
    redirect("/admin/system")
  })

}
