package com.example
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import org.jsoup.nodes.{Document, Element}
import scala.concurrent.Future

case class PostHtml(title: Element, info: Element)
case class Post(author:   String,
                title:    String,
                link:     String,
                points:   Int,
                comments: Int)

case class Posts(nextId: String, items: List[Post])

object Post extends DefaultJsonProtocol {
  implicit val PostFormat = jsonFormat5(Post.apply)
}

object Posts extends DefaultJsonProtocol {
  implicit val PostsFormat = jsonFormat2(Posts.apply)
}
