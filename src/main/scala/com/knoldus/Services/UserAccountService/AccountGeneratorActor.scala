package com.knoldus.Services.UserAccountService

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.knoldus.repos.caseclasses.UserAccount
import akka.pattern.pipe
import akka.pattern.ask

/**
  * Created by knoldus on 23/3/17.
  */
class AccountGeneratorActor(dBInMemory: ActorRef) extends Actor with ActorLogging{

  override def receive: Receive = {

    case account: UserAccount =>
      dBInMemory.forward(account)

    case _ => throw new IllegalArgumentException("Now valid account format.")

  }

}
