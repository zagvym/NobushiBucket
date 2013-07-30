package util

import org.specs2.mutable._

class StringUtilSpec extends Specification {

  "encrypt() and decrypt()" should {
    "decrypt encrypted string to raw string" in {
      val encrypted = StringUtil.encrypt("abcd", "1234")
      val decrypted = StringUtil.decrypt(encrypted, "1234")
      decrypted mustEqual "abcd"
    }
  }

}
