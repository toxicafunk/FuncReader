package com.mindcurv.func

import java.net.{Authenticator, URL}
import javax.xml.parsers.SAXParser

import org.xml.sax.InputSource

import scala.xml.Node
import scala.xml.parsing.NoBindingFactoryAdapter


class HTML5Parser extends NoBindingFactoryAdapter {

  override def loadXML(source : InputSource, _p: SAXParser) = {
    loadXML(source)
  }

  def loadXML(source : InputSource) = {
    import nu.validator.htmlparser.{common, sax}
    import common.XmlViolationPolicy
    import sax.HtmlParser

    val reader = new HtmlParser
    reader.setXmlPolicy(XmlViolationPolicy.ALLOW)
    reader.setContentHandler(this)
    reader.parse(source)
    rootElem
  }
}

object XmlReader {

  val parser: HTML5Parser = new HTML5Parser()

  def main(args: Array[String]): Unit = {
    Authenticator.setDefault(new ProxyAuthenticator());

    System.getProperties().put("http.proxyHost", "proxycorp.geci");
    System.getProperties().put("http.proxyPort", "8080");
    System.getProperties().put("http.proxyUser", "T0000104");
    System.getProperties().put("http.proxyPassword", "12345678");
    System.getProperties().put("proxySet", "true");

    val links: Set[String] = crawl("https://toxicafunk.wordpress.com/", 1, "concurrence", 5)
    print(s"Final Result: ${links.size} links found.\n$links")
  }

  def crawl(url: String, depth: Int, term:String, limit: Int = 10): Set[String] = {

    val processedUrls: List[String] = List()
    val foundLinks: Set[String] = Set()
    val relevantPages: List[String] = List()

    def readURL(url: String): Node =
      try {
          println(s"loading url $url")
        parser.load(new URL(url))
      } catch {
        case _: Throwable => {
          println(url + " not found!")
          <html/>
        }
      }

    //@tailrec
    def loop(urls: Seq[String], depth: Int): Set[String] = {
      println(s"urls size: ${urls.size} depth: $depth")

      def extractLinks(root: Node): Seq[String] = {
        val links: Seq[String] = (root \\ "a" \\ "@href").take(limit).map(node => node.text).map(href => ensureAbsolute(href))
        //println(links)
        links
      }

      def ensureAbsolute(x: String): String = {
        if (x.startsWith("http")) x
        else if (!x.startsWith("/")) {
          val i: Int  = x.indexOf("/")
          if (i != -1) {
            val s: String = x.substring(i)
            if (!s.isEmpty) url + s
            else ""
          }
          else ""
        }
        else url + x
      }

      if (depth == 3) { println("max depth reached"); foundLinks ++ urls.toSet }
      else {
        val ls: Seq[String] = for {
          x: String <- urls
          page: Node <- {
            if (!processedUrls.contains(x)) { x :: processedUrls; readURL(ensureAbsolute(x)) }
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

    loop(Seq(url), depth)
  }
}
