package com.example

import spray.httpx.marshalling.BasicMarshallers
import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.http._
import spray.client.pipelining._
import concurrent.Future
import concurrent.ExecutionContext
import scala.util.{Success, Failure}
import akka.actor.{ActorContext}
import scala.xml.{XML, NodeSeq}
import spray.httpx.unmarshalling._
import java.io.{InputStreamReader, ByteArrayInputStream}

class MyServiceActor extends Actor with MyService {
  val extractor = new XMLExtractor
  def actorRefFactory = context

  def receive = runRoute(myRoute)
}

trait Extractor {
  def extractXML(url: String): Future[NodeSeq]
}
class XMLExtractor(implicit context: ActorContext) extends Extractor {
  import context.dispatcher

  def extractXML(url: String): Future[NodeSeq] = {
    val pipeline: HttpRequest => Future[NodeSeq] = sendReceive ~> unmarshal[NodeSeq]
    val response: Future[NodeSeq] = pipeline(Get("http://www.theverge.com/rss/frontpage"))
    response.map(x => x)
  }
}

object XMLExtractor {
  implicit val NodeSeqUnmarshaller =
    Unmarshaller[NodeSeq](`text/xml`, `text/html`,  `application/xhtml+xml`, `application/xml`) {
      case HttpEntity.NonEmpty(contentType, data) => {
        val parser = XML.parser
        try {
          parser.setProperty("http://apache.org/xml/properties/locale", java.util.Locale.ROOT)
        } catch {
          case e: org.xml.sax.SAXNotRecognizedException ⇒ // property is not needed
        }
        XML.withSAXParser(parser).load(new InputStreamReader(new ByteArrayInputStream(data.toByteArray), contentType.charset.nioCharset))
      }
      case HttpEntity.Empty => NodeSeq.Empty
    }
}


trait MyService extends HttpService {
  val extractor: Extractor
  import ExecutionContext.Implicits.global

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/xml`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            {extractor.extractXML("")}
          }
        }
      }
    }
}
