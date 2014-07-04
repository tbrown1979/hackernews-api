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

class MyServiceActor extends Actor with MyService {
  def actorRefFactory = context

  def receive = runRoute(myRoute)
}

trait MyService extends HttpService {
  implicit def executionContext = actorRefFactory.dispatcher

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            {Scraper.scrape("https://news.ycombinator.com/").map(_.toString)}
          }
        }
      }
    }
}
