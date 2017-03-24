package com.knoldus.Services.UserAccountService

import java.util.Random

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.actor.Actor.Receive
import com.knoldus.repos.caseclasses.{User, UserAccount}

/**
  * Created by knoldus on 23/3/17.
  */
class AccountService(accountGeneratorActor: ActorRef) extends Actor with ActorLogging{
  override def receive: Receive = {
    case user: User =>
      if(user.amount < 500) {
        sender ! "The amount is too low. Initial amount should be more than 500"
      } else {
        val accountNumber = (new Random).nextInt((9999999 - 1000001) + 1) + 1000001 //Random account number
        accountGeneratorActor.forward(UserAccount(accountNumber, user.name, user.address, user.userName, user.amount))
      }
    case _ => sender ! new IllegalArgumentException("Not a valid user information")
  }
}
