package com.example
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._
import org.jsoup.nodes.{Document, Element}
import scala.concurrent.Future

case class PostHtml(title: Element, info: Element)
case class ApiResponse(nextId: String, items: Posts)
case class Post(author: String, title: String, link: String, points: String, comments: String)
case class Posts(posts: List[Post])

object Post extends DefaultJsonProtocol {
  implicit val PostFormat = jsonFormat5(Post.apply)
}

object Posts extends DefaultJsonProtocol {
  implicit val PostsFormat = jsonFormat1(Posts.apply)
}

object ApiResponse extends DefaultJsonProtocol {
  implicit val ApiResponseFormat = jsonFormat2(ApiResponse.apply)
}
