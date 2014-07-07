package com.example

import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import org.jsoup.Jsoup
import scala.util.{Success, Failure}
import concurrent.Future
import concurrent.ExecutionContext
import scala.collection.JavaConversions._
import Utils._
import java.io.File
import scala.concurrent._
import scala.concurrent.duration._

object HtmlPath {
  val path = "src/test/scala/html/"
  val newest = "newest.html"
  val frontPage = "front-page.html"
}

trait MockFetcher extends Fetcher {
  import HtmlPath._
  implicit val ec: ExecutionContext
  def getHtml(ext: String): Future[Document] = {
    val file = ext match {
      case "newest" => new File(path + newest)
      case "show" => new File("/html/....html")
      case _ => new File(path + frontPage)
    }
    Future(Jsoup.parse(file, "UTF-8", "http://example.com/"))
  }
}

class MockScraper extends HackerNewsPostScraper with MockFetcher {
  implicit val ec = ExecutionContext.Implicits.global
}

trait MockService extends HN {
  val webScraper: HNScraper = new MockScraper
}
