package com.knoldus.Services.BillProcessingService

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.pattern.ask
import akka.pattern.pipe
import akka.actor.Actor.Receive
import akka.util.Timeout
import com.knoldus.repos.DatabaseRepo.DBInMemory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationDouble

/**
  * Created by knoldus on 23/3/17.
  */
class ReportGeneratorActor(dBInMemory: ActorRef, reportSaverActor: ActorRef) extends Actor with ActorLogging{

  implicit val timeout = Timeout(5 seconds)
  override def receive: Receive = {
    case "generate report" =>

      log.info("Asking for report in ReportGeneratorActor")
      val report = dBInMemory ? "generate report"
      pipe(report) to reportSaverActor

    case _ => sender ! new IllegalArgumentException("Expected (\"get report\", userName: String)")
  }
}
