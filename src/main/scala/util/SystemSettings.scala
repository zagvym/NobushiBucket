package util

import util.Directory._
import java.text.SimpleDateFormat
import java.util.Date
import scala.reflect.ClassTag

object SystemSettings {

  case class SystemSettings(
    allowAccountRegistration: Boolean,
    gravatar: Boolean,
    notification: Boolean,
    smtp: Option[Smtp],
    blowfishKey: String
  )

  case class Smtp(
    host: String,
    port: Option[Int],
    user: Option[String],
    password: Option[String],
    ssl: Option[Boolean]
  )

  private val AllowAccountRegistration = "allow_account_registration"
  private val Gravatar = "gravatar"
  private val Notification = "notification"
  private val SmtpHost = "smtp.host"
  private val SmtpPort = "smtp.port"
  private val SmtpUser = "smtp.user"
  private val SmtpPassword = "smtp.password"
  private val SmtpSsl = "smtp.ssl"
  private val BlowfishKey = "blowfish_key"

  var instance: SystemSettings = null

  /**
   * Load system settings from the configuration file.
   */
  def apply(): SystemSettings = {
    if(instance == null){
      instance = loadSystemSettings()
    }
    instance
  }

  /**
   * Create a new instance of SystemSettings.
   */
  def apply(allowAccountRegistration: Boolean, gravatar: Boolean, notification: Boolean, smtp: Option[Smtp]): SystemSettings =
    new SystemSettings(allowAccountRegistration, gravatar, notification, smtp, "")

  private def getValue[A: ClassTag](props: java.util.Properties, key: String, default: A): A = {
    val value = props.getProperty(key)
    if(value == null || value.isEmpty) default
    else convertType(value).asInstanceOf[A]
  }

  private def getOptionValue[A: ClassTag](props: java.util.Properties, key: String, default: Option[A]): Option[A] = {
    val value = props.getProperty(key)
    if(value == null || value.isEmpty) default
    else Some(convertType(value)).asInstanceOf[Option[A]]
  }

  private def convertType[A: ClassTag](value: String) = {
    val c = implicitly[ClassTag[A]].runtimeClass
    if(c == classOf[Boolean])  value.toBoolean
    else if(c == classOf[Int]) value.toInt
    else value
  }

  /**
   * Save system settings to GITBUCKET_HOME/gitbucket.conf.
   */
  def saveSystemSettings(settings: SystemSettings): Unit = synchronized {
    val props = new java.util.Properties()
    if(GitBucketConf.exists){
      props.load(new java.io.FileInputStream(GitBucketConf))
    }

    props.setProperty(AllowAccountRegistration, settings.allowAccountRegistration.toString)
    props.setProperty(Gravatar, settings.gravatar.toString)
    props.setProperty(Notification, settings.notification.toString)
    if(settings.notification) {
      settings.smtp.foreach { smtp =>
        props.setProperty(SmtpHost, smtp.host)
        smtp.port.foreach(x => props.setProperty(SmtpPort, x.toString))
        smtp.user.foreach(props.setProperty(SmtpUser, _))
        smtp.password.foreach(props.setProperty(SmtpPassword, _))
        smtp.ssl.foreach(x => props.setProperty(SmtpSsl, x.toString))
      }
    }
    props.setProperty(BlowfishKey, settings.blowfishKey)

    props.store(new java.io.FileOutputStream(GitBucketConf), null)

    instance = null
  }

  /**
   * Load system settings from GITBUCKET_HOME/gitbucket.conf.
   */
  private def loadSystemSettings(): SystemSettings = synchronized {
    val props = new java.util.Properties()
    if(GitBucketConf.exists){
      props.load(new java.io.FileInputStream(GitBucketConf))
    }
    SystemSettings(
      getValue(props, AllowAccountRegistration, false),
      getValue(props, Gravatar, true),
      getValue(props, Notification, false),
      if(getValue(props, Notification, false)){
        Some(Smtp(
          getValue(props, SmtpHost, ""),
          getOptionValue(props, SmtpPort, Some(25)),
          getOptionValue(props, SmtpUser, None),
          getOptionValue(props, SmtpPassword, None),
          getOptionValue[Boolean](props, SmtpSsl, None)))
      } else {
        None
      },
      getValue(props, BlowfishKey, new SimpleDateFormat("HHmmss").format(new Date()))
    )
  }
}
