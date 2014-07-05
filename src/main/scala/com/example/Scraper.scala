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

  val doc: Future[Document] = Future {
    Jsoup.connect(baseUrl)
      .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
      .referrer("http://www.google.com")
      .get()
  }
  case class PostHtml(title: Element, info: Element)
  def getFrontPagePosts: Future[List[Post]] = {
    val posts = doc.map(_.select("tbody").get(0).select("table").get(1).select("table").select("tr:not([style])"))

    val postsWithPoints: Future[List[PostHtml]] = zipPostsWithInfo(posts)
    postsWithPoints.map(_.map(implicit html =>
      Post(
        getAuthor(html),
        getTitle,
        getLink(html),
        getPoints(html),
        getNumberOfComments(html)
      )))
    }

  private def getAuthor(implicit html: PostHtml): String =
    html.info.select("td > a").first.text

  private def getTitle(implicit html: PostHtml): String =
    html.title.select("td > a").text

  private def getLink(implicit html: PostHtml): String =
    html.title.select("td > a").attr("href").toString

  private def getPoints(implicit html: PostHtml): String =
    html.info.select("span").text

  private def getNumberOfComments(implicit html: PostHtml): Int =
    html.info.select("a").get(1).text.trim.filter(_.isDigit).toInt

  private def zipPostsWithInfo(posts: Future[Elements]): Future[List[PostHtml]] =
    posts.map(p => p.zip(p.drop(1)).map{case (a, b) => PostHtml(a, b)}.sliding(1,2).flatten.toList)

}
