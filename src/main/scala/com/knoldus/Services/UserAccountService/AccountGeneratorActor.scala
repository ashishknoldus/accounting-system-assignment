package com.knoldus.Services.UserAccountService

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.dispatch.{BoundedMessageQueueSemantics, RequiresMessageQueue}
import com.knoldus.repos.caseclasses.UserAccount
import akka.pattern.pipe
import akka.pattern.ask

/**
  * Created by knoldus on 23/3/17.
  */
class AccountGeneratorActor(dBInMemory: ActorRef) extends Actor with ActorLogging
  with RequiresMessageQueue[BoundedMessageQueueSemantics]{

  override def receive: Receive = {

    case account: UserAccount =>
      log.info("The object of UserAccount is received... forwarding to dBInMemory")

      dBInMemory.forward(account)

    case _ => throw new IllegalArgumentException("Now valid account format.")

  }

}
