package com.server.model

import org.scalatra.{ScalatraBase, FutureSupport, ScalatraServlet}

import slick.jdbc.PostgresProfile.api._
import com.github.tototoshi.slick.PostgresJodaSupport._

import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

object Tables {

  final case class Vote(vote: Int, date: DateTime, id: Int = 0)

  class Votes(tag: Tag) extends Table[Vote](tag, "VOTES") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def vote = column[Int]("VOTE")
    def date = column[DateTime]("DATE")

    def * = (vote, date, id) <> (Vote.tupled, Vote.unapply)
  }

  val votes = TableQuery[Votes]

  val findVote = {
    for {
      v <- votes
    } yield (v)
  }

  // DBIO Action which creates the schema
  val createSchemaAction = (votes.schema).create

  // DBIO Action which drops the schema
  val dropSchemaAction = (votes.schema).drop

  // Create database, composing create schema and insert sample data actions
  val createDatabase = DBIO.seq(createSchemaAction)

}

trait SlickRoutes extends ScalatraBase with FutureSupport {

  def db: Database

  get("/db/create-db") {
    db.run(Tables.createDatabase)
  }

  get("/db/drop-db") {
    db.run(Tables.dropSchemaAction)
  }

  get("/votes") {
    db.run(Tables.findVote.result) map { xs =>
      println(xs)
      contentType = "text/plain"
      xs map { case (s1) => f"  $s1" } mkString "\n"
    }
  }

}

class SlickApp(val db: Database) extends ScalatraServlet with FutureSupport with SlickRoutes {

  protected implicit def executor = scala.concurrent.ExecutionContext.Implicits.global

}