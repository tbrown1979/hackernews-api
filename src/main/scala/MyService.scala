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

trait MyServiceRoutes extends HttpService {
  implicit def executionContext = actorRefFactory.dispatcher

  val hns: HN

  val myRoute =
    path("") {
      get {
        complete {
          hns.getFrontPagePosts
        }
      }
    } ~
    path("show") {
      get {
        complete {
          "test"//hns.showHackerNewsPosts
        }
      }
    } ~
    path("newest") {
      get {
        complete {
          "test"//hns.getNewest
        }
      }
    }
}

trait MyService extends MyServiceRoutes {
  val hns = new HNService{}
}
