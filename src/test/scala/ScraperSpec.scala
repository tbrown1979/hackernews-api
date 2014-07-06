package com.example

import org.specs2.specification.Scope
import org.specs2.mutable.Specification
import org.specs2.execute._
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

object ScraperSpec extends Specification {

  "HNScraper" should {

    "return all posts for a given page" in new context {
      Await.result(scraper.getNewest, 1000000000 nanos).items.size === 4
    }
  }
}

trait context extends Scope {
  val scraper = new MockService
}

object HtmlPath {
  val path = "src/test/scala/html/"
  val newest = "newest.html"
}

trait MockFetcher extends Fetcher {
  import HtmlPath._
  implicit val ec: ExecutionContext
  def getHtml(ext: String): Future[Document] = {
    val file = ext match {
      case "newest" => new File(path + newest)
      case "show" => new File("/html/....html")
      case _ => new File("asdf.html")
    }
    Future(Jsoup.parse(file, "UTF-8", "http://example.com/"))
  }
}

class MockScraper extends HackerNewsPostScraper with MockFetcher {
  implicit val ec = ExecutionContext.Implicits.global
}

class MockService extends HN {
  val webScraper: HNScraper = new MockScraper
}
