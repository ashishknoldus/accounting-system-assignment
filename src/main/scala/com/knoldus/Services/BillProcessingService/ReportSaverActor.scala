package com.knoldus.Services.BillProcessingService

import java.io.{File, PrintWriter}

import akka.actor.{Actor, ActorLogging}
import akka.actor.Actor.Receive
import com.knoldus.repos.caseclasses.Report

/**
  * Created by knoldus on 24/3/17.
  */
class ReportSaverActor extends Actor with ActorLogging{

  var reportCounter = 0

  override def receive: Receive = {
    case reports: List[Report] =>

      val pw = new PrintWriter(new File("hello.txt" ))

      reports.foreach(report => {

        pw.append(report.accountHolder + " | " +
          report.accountNumber + " | " +
          report.billerName + " | " +
          report.category + " | " +
          report.totalPaidAMount + " | \n"
        )

      })


      pw.close()
  }
}
