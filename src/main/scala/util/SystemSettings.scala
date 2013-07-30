package util

import util.Directory._
import java.text.SimpleDateFormat
import java.util.Date

case class SystemSettings(
  allowAccountRegistration: Boolean,
  blowfishKey: String
)


object SystemSettings {

  private val AllowAccountRegistration = "allow_account_registration"
  private val BlowfishKey              = "blowfish_key"

  var instance: SystemSettings = null

  def apply(): SystemSettings = {
    if(instance == null){
      instance = loadSystemSettings()
    }
    instance
  }

  private def getBoolean(props: java.util.Properties, key: String, default: Boolean = false): Boolean = {
    val value = props.getProperty(key)
    if(value == null || value.isEmpty){
      default
    } else {
      value.toBoolean
    }
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
      getBoolean(props, AllowAccountRegistration),
      props.getProperty(BlowfishKey, new SimpleDateFormat("HHmmss").format(new Date()))
    )
  }
}
