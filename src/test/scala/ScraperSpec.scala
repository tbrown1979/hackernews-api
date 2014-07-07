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

    "return all the newest hacker news posts" in new Context {
      newestPage.items.size === 4
    }

    "return all the front page hacker news posts" in new Context {
      frontPage.items.size === 5
    }

    "return the correct first post from the front page" in new Context {
      val firstPost = Post("bjxrn","The Developer's Dystopian Future","https://the-pastry-box-project.net/ed-finkler/2014-july-6",223,103)
      frontPage.items.head === firstPost
    }

    "return the correct nextId for the front page" in new Context {
      val nextId = frontPage.nextId
      nextId === "/x?fnid=C9PVoO7SUtimj9xVWxXIZ9"
    }
  }
}

trait Context extends Scope {
  val scraper = new MockService{}
  val frontPage = Await.result(scraper.getFrontPagePosts, DurationInt(10).second)
  val newestPage = Await.result(scraper.getNewest, DurationInt(10).second)

}
