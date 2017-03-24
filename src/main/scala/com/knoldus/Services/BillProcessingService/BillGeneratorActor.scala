package com.knoldus.Services.BillProcessingService

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.knoldus.repos.caseclasses._
import com.knoldus.repos.DatabaseRepo.DBInMemory

/**
  * Created by knoldus on 23/3/17.
  */
class BillGeneratorActor(dBInMemory: ActorRef) extends Actor with ActorLogging{

  override def receive = {

    case biller: Biller =>
      log.info("A biller is received in BillGeneratorActor")
      dBInMemory.forward(biller)

    case _ =>
      sender ! new IllegalArgumentException("Wrong amount format. Expected (String, UserAmountPair)")

  }

}
