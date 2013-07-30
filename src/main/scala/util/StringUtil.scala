package util

import java.net.{URLDecoder, URLEncoder}
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.text.SimpleDateFormat

object StringUtil {

  val BlowfishKey = new SimpleDateFormat("HHmmss").format(new java.util.Date())

  def sha1(value: String): String = {
    val md = java.security.MessageDigest.getInstance("SHA-1")
    md.update(value.getBytes)
    md.digest.map(b => "%02x".format(b)).mkString
  }

  def md5(value: String): String = {
    val md = java.security.MessageDigest.getInstance("MD5")
    md.update(value.getBytes)
    md.digest.map(b => "%02x".format(b)).mkString
  }

  def encrypt(value: String, key: String = BlowfishKey): String = {
    val cipher = Cipher.getInstance("Blowfish")
    cipher.init(Cipher.ENCRYPT_MODE,  new SecretKeySpec(key.getBytes, "Blowfish"));
    cipher.doFinal(value.getBytes).map(b => "%02x".format(b)).mkString
  }

  def decrypt(value: String, key: String = BlowfishKey): String = {
    val cipher = Cipher.getInstance("Blowfish")
    cipher.init(Cipher.DECRYPT_MODE,  new SecretKeySpec(key.getBytes, "Blowfish"));
    new String(cipher.doFinal(value.grouped(2).map(Integer.parseInt(_, 16).asInstanceOf[Byte]).toArray))
  }

  def urlEncode(value: String): String = URLEncoder.encode(value, "UTF-8")

  def urlDecode(value: String): String = URLDecoder.decode(value, "UTF-8")

  def splitWords(value: String): Array[String] = value.split("[ \\t　]+")

  def escapeHtml(value: String): String =
    value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")

}
