package com.mindcurv.func

import java.net.URL
import javax.xml.parsers.SAXParser

import org.xml.sax.InputSource

import scala.xml.parsing.NoBindingFactoryAdapter
import scala.xml.{Node, NodeSeq}


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
    /* Authenticator.setDefault(new ProxyAuthenticator());

    System.getProperties().put("http.proxyHost", "proxycorp.geci");
    System.getProperties().put("http.proxyPort", "8080");
    System.getProperties().put("http.proxyUser", "T0000104");
    System.getProperties().put("http.proxyPassword", "12345678");
    System.getProperties().put("proxySet", "true"); */

    val links: Set[String] = crawl("https://toxicafunk.wordpress.com/", 0)
    print(s"Final Result: $links")
  }

  def crawl(url: String, depth: Int): Set[String] = {

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

    def extractLinks(root: Node): Seq[String] = {
      val links: NodeSeq = root \\ "a" \\ "@href"
      links.map(node => node.text)
    }

    //@tailrec
    def loop(urls: Seq[String], acc: Set[String], depth: Int): Set[String] = {
      println("depth: " + depth)

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

      if (depth == 3) { println("max depth reached"); acc }
      else {
        val ls = for {
          x: String <- urls
          page: Node <- {
            if (!acc.contains(x)) readURL(ensureAbsolute(x))
            else <html/>
          }
          links <- extractLinks(page)
        } yield links
        loop(ls, ls.toSet ++ acc, depth + 1)
      }
    }

    loop(extractLinks(readURL(url)).toSeq, Set[String](), 0)
  }
}
