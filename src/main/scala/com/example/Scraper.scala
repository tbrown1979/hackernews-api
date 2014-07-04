package com.example

import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.Jsoup
import scala.util.{Success, Failure}
import concurrent.Future
import concurrent.ExecutionContext
import scala.collection.JavaConversions._

object HackerNewsInfo {
  val url: String = "https://news.ycombinator.com/"
}
//need models
case class Post(title: String, link: String, points: String)

class HackerNewsScraper {
  implicit val actorSystem = Boot.system
  import actorSystem.dispatcher
  import HackerNewsInfo._

  val doc: Future[Document] = Future {
    Jsoup.connect(url)
      .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
      .referrer("http://www.google.com")
      .get()
  }
  //h3.r > a
  def getFrontPagePosts: Future[String] = {
    val posts = doc.map(_.select("tbody").get(0).select("table").get(1).select("table").select("tr:not([style])"))
    //val p1 = posts.map(_.select("tr:contains(()"))
    //val p2 = posts.map(_.select("tr:contains(points)").toList)
    val postsWithPoints =
      for {
        p <- posts
        p_ <- posts.map(_.drop(1))
      } yield p.zip(p_)
    postsWithPoints.map(_.take(1).map(e => Post(e._1.select("a").get(1).text, "", "")).toString)


    //Post(e.select("a").toString, e.select("a").attr("url"), e.select("span[id]").toString)).toString)
  }
}
