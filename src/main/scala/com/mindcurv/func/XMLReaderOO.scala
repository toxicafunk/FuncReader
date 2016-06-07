package com.mindcurv.func

import java.net.{Authenticator, URL}

trait URLObj {
  def url: URL
  def state: String
  def foundLinks: Set[String]
  def relevantPages: List[String]
  def crawl(term: String, limit: Int)
}

case class UrlHtmlReader(url: URL) extends URLObj {
  override def state: String = ???

  override def relevantPages: List[String] = List[String]()

  override def foundLinks: Set[String] = Set[String]()

  override def crawl(term: String, limit: Int): Unit = ???
}

/**
  * Created by erodriguez on 07/06/16.
  */
object XMLReaderOO {

  val parser: HTML5Parser = new HTML5Parser()

  def main(args: Array[String]): Unit = {
    Authenticator.setDefault(new ProxyAuthenticator());

    System.getProperties().put("http.proxyHost", "proxycorp.geci");
    System.getProperties().put("http.proxyPort", "8080");
    System.getProperties().put("http.proxyUser", "T0000104");
    System.getProperties().put("http.proxyPassword", "12345678");
    System.getProperties().put("proxySet", "true");

    //val links: Set[String] = crawl("https://toxicafunk.wordpress.com/", "concurrence", 5)
    //print(s"Final Result: ${links.size} links found.\n$links")
  }

}
