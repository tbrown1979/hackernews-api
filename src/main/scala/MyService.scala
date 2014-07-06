package com.example

import spray.httpx.marshalling.BasicMarshallers
import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.http._
//import spray.client.pipelining._
import concurrent.Future
import concurrent.ExecutionContext
import scala.util.{Success, Failure}
import akka.actor.{ActorContext}
import org.jsoup.select.Elements
import spray.httpx.SprayJsonSupport._
import spray.util._

class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context

  def receive = runRoute(myRoute)
}

trait MyService extends HttpService {
  implicit def executionContext = actorRefFactory.dispatcher

  lazy val hns = new HNService{}
  lazy val posts = hns.getFrontPagePosts
  val myRoute =
    path("") {
      get {
        complete {
         "test"// {posts}
        }
      }
    } ~
    path("show") {
      get {
        complete {
          {hns.showHackerNewsPosts}
        }
      }
    } ~
    path("newest") {
      get {
        complete {
          {hns.getNewest}
        }
      }
    }
}