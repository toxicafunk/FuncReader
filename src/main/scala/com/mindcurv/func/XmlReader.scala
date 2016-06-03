package com.mindcurv.func

import java.net.{Authenticator, URL}
import javax.xml.parsers.SAXParser

import org.xml.sax.InputSource

import scala.annotation.tailrec
import scala.collection.immutable.Stream.Empty
import scala.xml.{Node, NodeSeq}
import scala.xml.parsing.NoBindingFactoryAdapter

/**
  * Created by erodriguez on 11/05/16.
  */

/*import scales.utils._
import ScalesUtils._
import scales.xml._
import ScalesXml._


object XmlReader {
  // Contains the document
  val doc = loadXml(new FileReader("document.xml"))

  // gets Path from the documents root
  val path = top(doc)

  // query for all nodes that match the XPath
  // path.\*("NoNamespace").\*(prefixedQName)
}*/


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

  def crawl(url: String, depth: Int): Seq[String] = {

    def readURL(url: String): Node =
      try {
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
    def loop(urls: Seq[String], acc: Seq[String], depth: Int): Seq[String] = {
      println("depth: " + depth)
      println(acc)
      def ensureAbsolute(x: String): String = {
        if (x.startsWith("http")) x else url + x
      }

      if (depth == 3) { println("max depth reached"); acc }
      else {
        val l: List[String] = List[String]()
        val ls = for {
          x: String <- urls
          page: Node <- readURL(ensureAbsolute(x))
          link <- extractLinks(page)
        } yield link :: l
        val s: Seq[String] = ls.flatten
        loop(s, s ++ acc, depth + 1)
      }
    }

    loop(extractLinks(readURL(url)), Seq[String](), 0)
  }

  def main(args: Array[String]): Unit = {
    /* Authenticator.setDefault(new ProxyAuthenticator());

    System.getProperties().put("http.proxyHost", "proxycorp.geci");
    System.getProperties().put("http.proxyPort", "8080");
    System.getProperties().put("http.proxyUser", "T0000104");
    System.getProperties().put("http.proxyPassword", "12345678");
    System.getProperties().put("proxySet", "true"); */

    val links: Seq[String] = crawl("http://www.scala-lang.org", 0)
    print(links)
  }
}

/* def readURL(url: String): List[String] = {
      val root = parser.load(new URL(url))
      val nodes: NodeSeq = root \\ "a"
      nodes.foldLeft(List[String]())((acc, node) => {
        val href: String = node.text
        if (href.startsWith("http://")) href :: acc
        else url + href :: acc
      })
    } */