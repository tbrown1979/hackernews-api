package com.example
import concurrent.Future

trait HN {
  import HackerNewsInfo._

  val webScraper: HNScraper

  def getFrontPagePosts: Future[Posts] =
    webScraper.getPostsForPage()

  def getNewest: Future[Posts] =
    webScraper.getPostsForPage(newest)

  def showHackerNewsPosts: Future[Posts] =
    webScraper.getPostsForPage(showHN, showHNOffset)
}

trait HNService extends HN {
  lazy val webScraper: HNScraper = new HackerNewsWebsiteScraper{}
}

object HackerNewsInfo {
  val baseUrl: String = "https://news.ycombinator.com/"
  val newest: String = "newest"
  val showHN: String = "show"
  val showHNOffset: Int = 1
}
