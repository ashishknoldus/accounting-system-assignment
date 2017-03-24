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
    case reportsList: List[List[Report]] =>

      val pw = new PrintWriter(new File("hello.txt" ))

      reportsList.foreach(reportsOfOneUser => {
        pw.append(s"Bills of ${reportsOfOneUser(0).accountHolder}\n")
        reportsOfOneUser.foreach(report => {
          pw.append(report.accountHolder + " | " +
            report.accountNumber + " | " +
            report.billerName + " | " +
            report.category + " | " +
            report.totalPaidAMount + " | \n"
          )
        })

      })

      log.info("The reports are saved")

      pw.close()
  }
}
