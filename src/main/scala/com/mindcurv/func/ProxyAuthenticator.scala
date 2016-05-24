package com.mindcurv.func

import java.net.Authenticator
import java.net.PasswordAuthentication

class ProxyAuthenticator(user: String, password: String) extends Authenticator {

  def this() = this("T0000104", "12345678")

  override def getPasswordAuthentication(): PasswordAuthentication = {
    return new PasswordAuthentication(user, password.toCharArray());
  }
}