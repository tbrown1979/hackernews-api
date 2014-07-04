package com.example

import org.jsoup.nodes.Document
import org.jsoup.Jsoup
import scala.util.{Success, Failure}
import concurrent.Future
import concurrent.ExecutionContext

// class HtmlScraper(val url: String) {
//   implicit val actorSystem = Boot.system
//   import actorSystem.dispatcher

//   val doc: Future[Document] = Future{ Jsoup.connect(url).get() }

//   def getElement(e: String):
// }

// case class ScrapedHtml(doc: Document) {
//   def select(element: String):
// }
object Scraper {
  implicit val actorSystem = Boot.system
  import actorSystem.dispatcher

  def scrape(url: String): Future[Document] =
    Future { Jsoup.connect(url)
      .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
      .referrer("http://www.google.com").get()
    }
}
