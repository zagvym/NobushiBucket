package app

import service.AccountService
import util.{SystemSettings, AdminAuthenticator}
import SystemSettings._
import jp.sf.amateras.scalatra.forms._
import org.scalatra.FlashMapSupport

class SystemSettingsController extends SystemSettingsControllerBase
  with AccountService with AdminAuthenticator

trait SystemSettingsControllerBase extends ControllerBase with FlashMapSupport {
  self: AccountService with AdminAuthenticator =>

  private val form = mapping(
    "allowAccountRegistration" -> trim(label("Account registration", boolean())),
    "gravatar"                 -> trim(label("Gravatar", boolean())),
    "notification"             -> trim(label("Notification", boolean())),
    "smtp"                     -> optionalIfNotChecked("notification", mapping(
        "host"     -> trim(label("SMTP Host", text(required))),
        "port"     -> trim(label("SMTP Port", optional(number()))),
        "user"     -> trim(label("SMTP User", optional(text()))),
        "password" -> trim(label("SMTP Password", optional(text()))),
        "ssl"      -> trim(label("Enable SSL", optional(boolean())))
    )(Smtp.apply))
  )(SystemSettings.apply)


  get("/admin/system")(adminOnly {
    admin.html.system(flash.get("info"))
  })

  post("/admin/system", form)(adminOnly { form =>
    saveSystemSettings(form.copy(blowfishKey = context.systemSettings.blowfishKey))
    flash += "info" -> "System settings has been updated."
    redirect("/admin/system")
  })

}
