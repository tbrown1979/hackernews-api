package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._
import concurrent.Future
import concurrent._
import spray.json._
import spray.httpx.SprayJsonSupport._
import MediaTypes._
import HttpCharsets._
import HttpHeaders._


class MyServiceSpec extends Specification with Specs2RouteTest with MockMyService {
  def actorRefFactory = system
  implicit val routeTestTimeout = RouteTestTimeout(DurationInt(5).second)

  "MyService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        status === StatusCodes.OK
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}

trait MockMyService extends MyServiceRoutes {
  lazy val hns = new MockService{}
}
