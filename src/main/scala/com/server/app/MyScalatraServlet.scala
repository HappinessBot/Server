package com.server.app

import org.scalatra._
import scalate.ScalateSupport
import scala.util.parsing.json._

import slick.jdbc.PostgresProfile.api._
import com.github.tototoshi.slick.PostgresJodaSupport._

import com.server.model.Tables

import org.joda.time.DateTime
import org.joda.time.LocalDate

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import com.server.robot.ControllRobot

trait SlickRoutes extends ScalatraBase with FutureSupport with ScalateSupport {

  def db: Database

  val robot: ControllRobot = new ControllRobot()

  post("/vote") {
    val result = JSON.parseFull(request.body)
    result match {
      case Some(map: Map[String, Any]) => {
          var vote: Int = 0
          var date: String = ""
          
          map.get("vote") match {
            case Some(i: Double) => vote = i.asInstanceOf[Int]
            case _ => halt(400)
          }
          map.get("date") match {
            case Some(s: String) => date = s
            case _ => halt(400)
          }

          db.run(Tables.votes += (Tables.Vote(vote, new DateTime(date))))

          halt(200)
        }
      case _ => halt(400)
    }
  }

  get("/votes") {
    val avr_vote: String = get_votes().toString
    halt(body = avr_vote)
  }


  get("/config") {
    robot.config()
  }

  get("/move") {
    val avr_vote: Long = get_votes()

    if (avr_vote <= -2) {
      robot.sendCommand("fffrfflffrfflffffdffrfffflffrffrfffdfffflffrfflffrfffd")
    } else if (avr_vote >= 2) {
      robot.sendCommand("frffflffrfflfffffffflfflfffdfflff")
      Thread.sleep(10000)
      robot.sendCommand("ffrfflfflfffrfflfffffflffrfflfffl")
    } else {
      robot.sendCommand("ffrfffflfffflffrffrfflfffflfflffrffffr")
      Thread.sleep(20000)
      robot.sendCommand("fflfflffrfflfffflfflffdffflffrfffflffl")
    }
    // frffflfffffflflfrrflfffflfrflfffflfrflffl
    // robot.sendCommand("ffrfffflfffffflfflffrrfflffffflffrfflffffflffrfflfffl")
  }

  def get_votes(): Long = {
    val now: DateTime = new DateTime()

    val today: LocalDate = now.toLocalDate()
    val tomorrow: LocalDate = today.plusDays(1)

    val startOfToday: DateTime  = today.toDateTimeAtStartOfDay(now.getZone())
    val startOfTomorrow: DateTime = tomorrow.toDateTimeAtStartOfDay(now.getZone())

    val q1 = Tables.votes.filter(x => x.date >= startOfToday && x.date <= startOfTomorrow)
    val result = db.run(q1.result)
    val votes = Await.result(result, Duration.Inf)
    
    val avr_vote: Long = (votes.map(_.vote).sum.toDouble / votes.length).round
    avr_vote
  }

}

class MyScalatraServlet(val db: Database) extends ScalatraServlet with FutureSupport with SlickRoutes  {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

  notFound {
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound() 
  } 

}
