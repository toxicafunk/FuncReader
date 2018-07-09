package com.mindcurv.func

import java.net.{Authenticator, URL}

import com.mindcurv.func.State.State

import scala.xml.Node

object State extends Enumeration {
  type State = Value
  val INITIAL, READ, PROCESSED, NOTFOUND = Value
}

case class URLObj(url: URL, state: State = State.INITIAL) {
  override def hashCode(): Int = 41 * url.hashCode()

  override def equals(obj: scala.Any): Boolean = obj match {
    case that: URLObj => this.url == that.url
    case _  => false
  }
}

trait Crawler {
  def initialUrl: String
  def foundLinks: Set[URLObj]
  def relevantPages: List[URLObj]
  def crawl(term: String, limit: Int)
}

case class SimpleCrawler extends Crawler {

  val parser: HTML5Parser = new HTML5Parser()

  override def relevantPages: List[URLObj] = List[URLObj]()

  override def foundLinks: Set[URLObj] = Set[URLObj]()

  override def crawl(targetUrl: String, term: String, limit: Int): Unit = {
    def readURL(url: URLObj): Node =
      try {
        println(s"loading url $url")
        parser.load(url.url)
      } catch {
        case _: Throwable => {
          println(url + " not found!")
            <html/>
        }
      }

    //@tailrec
    def loop(urls: Seq[URLObj], depth: Int): Set[URLObj] = {
      println(s"urls size: ${urls.size} depth: $depth")

      def extractLinks(root: Node): Seq[String] = {
        val links: Seq[String] = (root \\ "a" \\ "@href").take(limit).map(node => node.text).map(href => ensureAbsolute(href))
        //println(links)
        links
      }

      def ensureAbsolute(x: String): URLObj = {
        if (x.startsWith("http")) x
        else if (!x.startsWith("/")) {
          val i: Int  = x.indexOf("/")
          if (i != -1) {
            val s: String = x.substring(i)
            if (!s.isEmpty) targetUrl + s
            else ""
          }
          else ""
        }
        else targetUrl + x
      }

      if (depth == 3) { println("max depth reached"); foundLinks ++ urls.toSet }
      else {
        val ls: Seq[String] = for {
          x: URLObj <- urls
          page: Node <- {
            if (!foundLinks.contains(x)) { foundLinks :+ x; readURL(ensureAbsolute(x)) }
            else <html/>
          }
          link: String <- {
            page :: relevantPages
            extractLinks(page)
          }
        } yield link
        //println(s"Found ${ls.size} links!")
        foundLinks ++ ls.toSet ++ loop(ls, depth + 1)
      }
    }

    val urlObj = URLObj(new URL(targetUrl))
    loop(Seq(urlObj), 1)
  }
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
