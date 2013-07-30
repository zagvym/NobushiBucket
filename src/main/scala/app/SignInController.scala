package app

import service._
import jp.sf.amateras.scalatra.forms._
import util.StringUtil

class SignInController extends SignInControllerBase with SystemSettingsService with AccountService

trait SignInControllerBase extends ControllerBase { self: SystemSettingsService with AccountService =>
  
  case class SignInForm(userName: String, password: String)
  
  val form = mapping(
    "userName" -> trim(label("Username", text(required))),
    "password" -> trim(label("Password", text(required)))
  )(SignInForm.apply)
  
  get("/signin"){
    val queryString = request.getQueryString
    if(queryString != null && queryString.startsWith("/")){
      session.setAttribute("REDIRECT", queryString)
    }
    html.signin(loadSystemSettings())
  }

  post("/signin", form){ form =>
    getAccountByUserName(form.userName).collect {
      case account if(!account.isGroupAccount && account.password == StringUtil.sha1(form.password)) => {
        cookies += ("gitbucket_login", StringUtil.encrypt(account.userName))
        updateLastLoginDate(account.userName)

        session.get("REDIRECT").map { redirectUrl =>
          session.removeAttribute("REDIRECT")
          redirect(redirectUrl.asInstanceOf[String])
        }.getOrElse {
          redirect("/")
        }
      }
    } getOrElse redirect("/signin")
  }

  get("/signout"){
    cookies -= "gitbucket_login"
    session.invalidate
    redirect("/")
  }

}