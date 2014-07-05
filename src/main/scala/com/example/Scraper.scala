package com.example

import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import org.jsoup.Jsoup
import scala.util.{Success, Failure}
import concurrent.Future
import concurrent.ExecutionContext
import scala.collection.JavaConversions._

object HackerNewsInfo {
  val baseUrl: String = "https://news.ycombinator.com/"
}

class HackerNewsScraper {
  implicit val actorSystem = Boot.system
  import actorSystem.dispatcher
  import HackerNewsInfo._

  def doc(ext: String = ""): Future[Document] = Future {
    Jsoup.connect(baseUrl + ext)
      .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
      .referrer("http://www.google.com")
      .get()
  }

  def getFrontPagePosts: Future[ApiResponse] = {
    val mainPage = doc()
    val scrapedPosts = mainPage.map(scrapePosts(_))

    val posts: Future[Posts] = scrapedPosts.map(getPosts(_))

    posts.flatMap(p => mainPage.map(d => ApiResponse(getNextId(d), p)))
  }

  def showHackerNewsPosts: Future[ApiResponse] = {
    val showHNPage = doc("show")
    val scrapedPosts = showHNPage.map(scrapePosts(_))

    val posts: Future[Posts] = scrapedPosts.map(getPosts(_, 3))

    posts.flatMap(p => showHNPage.map(d => ApiResponse(getNextId(d), p)))
  }

  def getPosts(scrapedPosts: Elements, offset: Int = 0): Posts =
    Posts(zipPostsWithInfo(scrapedPosts, offset)
      .map(implicit html => Post(author, title, link, points, numberOfComments)))

  private def scrapePosts(html: Document): Elements =
    html.select("tbody").get(0).select("table").get(1).select("table").select("tr:not([style])")

  private def getNextId(html: Document): String =
    html.select("td.title > a").attr("href")

  private def author(implicit html: PostHtml): String =
    html.info.select("td > a").first.text

  private def title(implicit html: PostHtml): String =
    html.title.select("td > a").text

  private def link(implicit html: PostHtml): String =
    html.title.select("td > a").attr("href").toString

  private def points(implicit html: PostHtml): String =
    html.info.select("span").text

  private def numberOfComments(implicit html: PostHtml): String =
    html.info.select("a").get(1).text.trim.filter(_.isDigit).toString

  private def zipPostsWithInfo(posts: Elements, offset: Int = 0): List[PostHtml] = {
    val cleanedPosts = posts.drop(offset)
    cleanedPosts.zip(cleanedPosts.drop(1)).map{case (a, b) => PostHtml(a, b)}.sliding(1,2).flatten.toList
  }
}
