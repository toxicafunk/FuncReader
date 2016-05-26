package com.mindcurv.func

import java.net.{Authenticator, URL}
import javax.xml.parsers.SAXParser

import org.xml.sax.InputSource

import scala.annotation.tailrec
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

  def crawl(url: String, depth: Int): List[String] = {

    /* def readURL(url: String): List[String] = {
      val root = parser.load(new URL(url))
      val nodes: NodeSeq = root \\ "a"
      nodes.foldLeft(List[String]())((acc, node) => {
        val href: String = node.text
        if (href.startsWith("http://")) href :: acc
        else url + href :: acc
      })
    } */

    def readURL(url: String): Node = {
      parser.load(new URL(url))
    }

    def extractLinks(root: Node): Seq[String] = {
      val links: NodeSeq = root \\ "a" \\ "@href"
      links.map(node => node.text)
    }

    @tailrec
    def loop(urls: Seq[String], acc: List[String], index: Int = 0): List[String] = {
      println(index)
      println(urls)
      if (index == depth) acc
      else {
        urls match {
          case x :: xs => {
            val href: String = if (x.startsWith("http")) x else url + x
            println(href)
            href :: acc
            println(acc)
            loop(xs, acc, index + 1)
          }
          case _ => acc
        }
      }
    }

    loop(extractLinks(readURL(url)), List(), 0)
  }

  def main(args: Array[String]): Unit = {
    Authenticator.setDefault(new ProxyAuthenticator());

    System.getProperties().put("http.proxyHost", "proxycorp.geci");
    System.getProperties().put("http.proxyPort", "8080");
    System.getProperties().put("http.proxyUser", "T0000104");
    System.getProperties().put("http.proxyPassword", "12345678");
    System.getProperties().put("proxySet", "true");

    val links: List[String] = crawl("http://www.scala-lang.org", 3)
    print(links)
  }
}