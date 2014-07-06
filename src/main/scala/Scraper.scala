package com.example

import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import org.jsoup.Jsoup
import scala.util.{Success, Failure}
import concurrent.Future
import concurrent.ExecutionContext
import scala.collection.JavaConversions._
import Utils._

trait HNScraper {
  def getPostsForPage(ext: String = "", offset: Int = 0): Future[Posts]
}

trait Fetcher {
  def getHtml(ext: String): Future[Document]
}

trait HackerNewsFetcher extends Fetcher {
  import HackerNewsInfo._
  implicit val ec: ExecutionContext

  def getHtml(ext: String = ""): Future[Document] = Future {
    Jsoup.connect(baseUrl + ext)
      .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
      .referrer("http://www.google.com")
      .get()
  }
}

trait HackerNewsPostScraper extends HNScraper {
  implicit val ec: ExecutionContext

  def getHtml(ext: String): Future[Document]

  def getPostsForPage(ext: String = "", offset: Int = 0): Future[Posts] = {
    val page: Future[Document] = getHtml(ext)
    val scrapedPosts: Future[Elements] = page.map(scrapePosts(_))

    val posts: Future[List[Post]] = scrapedPosts.map(getPosts(_, offset))

    posts.flatMap(p => page.map(d => Posts(getNextId(d), p)))
  }

  private def getPosts(scrapedPosts: Elements, offset: Int = 0): List[Post] =
    zipRows(scrapedPosts, offset)
      .map(implicit html => Post(author, title, link, points, numberOfComments))

  private def scrapePosts(html: Document): Elements =
    html.select("tbody").get(0).select("table").get(1).select("table").select("tr:not([style])")

  private def getNextId(html: Document): String =
    html.select("td.title").last.select("a").attr("href")

  private def author(implicit html: PostHtml): String =
    html.info.select("td > a").first.text

  private def title(implicit html: PostHtml): String =
    html.title.select("td > a").text.toString

  private def link(implicit html: PostHtml): String =
    html.title.select("td > a").attr("href").toString

  private def points(implicit html: PostHtml): Int =
    html.info.select("span").text.safeToInt()

  private def numberOfComments(implicit html: PostHtml): Int =
    html.info.select("a").get(1).text.safeToInt()

  private def zipRows(posts: Elements, offset: Int = 0): List[PostHtml] = {
    val cleanedPosts = posts.drop(offset)
    cleanedPosts.zip(cleanedPosts.drop(1)).map{case (a, b) => PostHtml(a, b)}.sliding(1,2).flatten.toList
  }
}

trait HackerNewsWebsiteScraper extends HackerNewsFetcher with HackerNewsPostScraper {
  implicit lazy val ec: ExecutionContext = Boot.system.dispatcher//some problems with this
}
