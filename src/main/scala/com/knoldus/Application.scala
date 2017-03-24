package com.knoldus

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Cancellable, Props}
import akka.util.Timeout
import com.knoldus.repos.caseclasses.User
import com.knoldus.repos.DatabaseRepo.DBInMemory
import com.knoldus.Services.BillProcessingService.{BillGeneratorActor, BillProcessorActor, ReportGeneratorActor, ReportSaverActor}
import com.knoldus.Services.SalaryDepositeService.SalaryDepositeActor
import com.knoldus.Services.UserAccountService.{AccountGeneratorActor, AccountService}

import scala.concurrent.duration.{Duration, DurationDouble}

/**
  * Created by knoldus on 24/3/17.
  */
object Application extends App{

  val system = ActorSystem("RouterSystem")

  implicit val executionContext = system.dispatchers.lookup("my-dispatcher")

  val dBInMemoryActor: ActorRef = system.actorOf(Props(classOf[DBInMemory])
    .withDispatcher("my-dispatcher"), "dBInMemory-actor")

  val accountGeneratorActor: ActorRef = system.actorOf(Props(classOf[AccountGeneratorActor], dBInMemoryActor)
    .withDispatcher("my-dispatcher"), "account-generator")

  val accountServiceActor: ActorRef = system.actorOf(Props(classOf[AccountService],accountGeneratorActor)
    .withDispatcher("my-dispatcher") , "account-service")

  val salaryDepositeActor: ActorRef = system.actorOf(Props(classOf[SalaryDepositeActor], dBInMemoryActor)
    .withDispatcher("my-dispatcher"), "salary-depositor")

  val billGeneratorActor: ActorRef = system.actorOf(Props(classOf[BillGeneratorActor], dBInMemoryActor)
    .withDispatcher("my-dispatcher"), "bill-generator")

  val reportSaverActor: ActorRef = system.actorOf(Props(classOf[ReportSaverActor])
    .withDispatcher("my-dispatcher"), "report-saver")

  val reportGeneratorActor: ActorRef = system.actorOf(Props(classOf[ReportGeneratorActor], dBInMemoryActor, reportSaverActor)
    .withDispatcher("my-dispatcher"), "report-generator")


  val billProcessorActor: ActorRef = system.actorOf(Props(classOf[BillProcessorActor], billGeneratorActor)
    .withDispatcher("my-dispatcher"), "bill-processor")

  val scheduleReport: Cancellable = system.scheduler.schedule(Duration.create(5, TimeUnit.SECONDS),
    Duration.create(5, TimeUnit.MINUTES), reportGeneratorActor, "generate report")


  implicit val timeout = Timeout(1000 seconds)

  val user1 = User("Ashish Tomer", "249-R, Model Town", "ashish1269", 1000)
  val user2 = User("Vishesh Kumar", "Mohali", "vishesh2784", 2000)
  val user3 = User("Rahul Yadav", "Chandigarh", "rahulchandi", 2000)
  val user4 = User("Pushpendra", "Noida", "pushpa1269", 4000)
  val user5 = User("Neha Bhardwaj", "Vaishali", "neha1269", 500)

  accountServiceActor ! user1
  accountServiceActor ! user2
  accountServiceActor ! user3
  accountServiceActor ! user4
  accountServiceActor ! user5

}
