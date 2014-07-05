package com.example
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport._

case class Post(author: String, title: String, link: String, points: String, comments: Int)

object Post extends DefaultJsonProtocol {
  implicit val PostFormat = jsonFormat5(Post.apply)
}
