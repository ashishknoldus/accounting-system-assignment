package com.knoldus.Services.BillProcessingService

import akka.actor.{Actor, ActorRef}
import com.knoldus.repos.caseclasses._
import scala.util.Random

/**
  * Created by knoldus on 24/3/17.
  */
class BillProcessorActor(billGeneratorActor: ActorRef) extends Actor{

  override def receive: Receive = {
    case accountNumber: Long =>
      val dummyBills: List[Biller] = getDummyBIllers(accountNumber)

      for ( dummyBill <- dummyBills)
        billGeneratorActor.forward(dummyBill)

    case _ => sender ! new IllegalArgumentException("Provide a valid Long account number")
  }

  private def getDummyBIllers(accountNumber: Long): List[Biller] = {

    List[Biller] (

      Biller(CarBill, "Malwa", accountNumber, "12-2-2017", Random.nextInt((25000 - 10) + 1) + 10),
      Biller(ElectricityBill, "NTPG", accountNumber, "12-3-2017", Random.nextInt((5000 - 180) + 1) + 180),
      Biller(FoodBill, "Sukhdev Dhaba", accountNumber, "2-2-2017", Random.nextInt((2500 - 50) + 1) + 50),
      Biller(InternetBill, "BSNL", accountNumber, "11-3-2017", Random.nextInt((5000 - 250) + 1) + 250),
      Biller(PhoneBill, "JIO", accountNumber, "7-3-2017", Random.nextInt((1000 - 150) + 1) + 150)

    )

  }

}
