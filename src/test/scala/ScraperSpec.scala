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
  val scraper = new MockService{}
}
