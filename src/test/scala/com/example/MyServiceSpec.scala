package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import concurrent.Future
import concurrent._
import scala.xml.NodeSeq

class MyServiceSpec extends Specification with Specs2RouteTest with MyService {
  def actorRefFactory = system

  val extractor = new MockExtractor{}

  "MyService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("test")
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

trait MockExtractor extends Extractor {
  import ExecutionContext.Implicits.global
  def extractXML(url: String): Future[NodeSeq] =
    Future {
      <html> test </html>
    }

}
