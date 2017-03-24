package com.knoldus.Services.SalaryDepositeService

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef}
import akka.pattern.{ask, pipe}
import com.knoldus.repos.caseclasses.UserAmountPair
import com.knoldus.repos.DatabaseRepo.DBInMemory
import com.knoldus.Services.UserAccountService.AccountGeneratorActor

/**
  * Created by knoldus on 23/3/17.
  */
class SalaryDepositeActor(dBInMemory: ActorRef) extends Actor{

  override def receive: Receive = {

    case salary: UserAmountPair => dBInMemory.forward(salary)

    case _ => sender ! "Wrong input. Please provide the amount you want to enter with the account number"

  }

}
