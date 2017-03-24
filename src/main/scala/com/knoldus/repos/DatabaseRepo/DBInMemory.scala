package com.knoldus.repos.DatabaseRepo

import akka.actor.{Actor, ActorLogging, Props}
import com.knoldus.Application
import com.knoldus.repos.caseclasses._
import com.knoldus.Services.BillProcessingService.BillProcessorActor

import scala.collection.mutable

/**
  * Created by knoldus on 23/3/17.
  */
class DBInMemory extends Actor with ActorLogging{

  private case class UserBill(
                               category:  Category,
                               billerName:String,
                               transDate: String,
                               amount:    Float
                             )

  private case class BillIteration(
                                    totalIterations:     Int,
                                    executedIterations:  Int,
                                    paidAmount:          Float
                                  )




  private val userAccounts: mutable.Map[Long, UserAccount] = mutable.Map[Long, UserAccount]()
  //accNumToUserName is created to keep track of unique userName's - we won't have to drill down userAccounts
  private val accNumToUserName: mutable.Map[String, Long] = mutable.Map[String, Long]()
  private val userBills: mutable.Map[Long, List[UserBill]] = mutable.Map[Long, List[UserBill]]()
  //userBillIterations NORMALIZE the use bill information
  private val userBillIterations: mutable.Map[Long, BillIteration] = mutable.Map[Long, BillIteration]()



  override def receive: Receive = {

    case userAccount: UserAccount =>
      this.userBills(userAccount.accountNumber) = List[UserBill]() //Linking Biller with a user account
      this.userBillIterations(userAccount.accountNumber) = BillIteration(20, 0, 0) //Total Iterations are fixed = 20

      val saveUserAccountResult = saveUserAccount(userAccount)
      log.info(saveUserAccountResult)
      sender ! saveUserAccountResult

    case ("credit", amountToCredit: UserAmountPair) =>
      val creditAmountResult = creditAmount(amountToCredit)
      log.info(creditAmountResult)

      sender ! creditAmountResult

    case ("debit", amountToDebit: UserAmountPair) =>
      val debitAmountResult = debitAmount(amountToDebit)
      log.info(debitAmountResult)

      sender ! debitAmountResult

    case biller: Biller =>
      val saveBillerResult = saveBiller(biller)
      log.info(saveBillerResult)

      sender ! saveBillerResult

    case "generate report" =>
      val reports = getReport()
      log.info(s"${reports.size} reports have been generated.")

      sender ! reports

    case _ => sender ! new IllegalArgumentException("Valid options are : UserAccount | Credit Amount | Debit Amount | Biller")

  }



  private def saveUserAccount(userAccount: UserAccount): String = {

    val initialSize = this.userAccounts.size

    if(userAccount.amount < 500) {

      "Initial amount must be at least Rs. 500"
    }else if(this.userAccounts.contains(userAccount.accountNumber)){

      "The account number is already taken. Please choose another one"
    } else if (this.accNumToUserName.contains(userAccount.userName)) {

      s"The username ${userAccount.userName} is already taken. Available in ${this.accNumToUserName.keys} Please choose another one"
    } else {

      val billProcessor = Application.billProcessorActor

      billProcessor ! userAccount.accountNumber

      log.info(s"The account number : ${userAccount.accountNumber} for user ${userAccount.userName} is being saved")

      this.accNumToUserName(userAccount.userName) = userAccount.accountNumber //Link userName to accNumber

      this.userAccounts += (userAccount.accountNumber -> userAccount)

      if(this.userAccounts.size == initialSize + 1)
        "User account created"
      else
        "User account couldn't be created. Internal error while creating account in database (Map)"
    }

  }




  private def creditAmount(creditAmount:UserAmountPair): String = {

    if (!this.userAccounts.contains(creditAmount.accNumber)) {

      "There is no user with such username"
    }
    else {

      val userAccount: UserAccount = this.userAccounts(creditAmount.accNumber)
      val updatedUserAccount = userAccount.copy(amount = userAccount.amount + creditAmount.amount)
      this.userAccounts(creditAmount.accNumber) = updatedUserAccount

      s"Rs. ${creditAmount.amount} debited from account number ${updatedUserAccount.accountNumber}. "+
        s"Current balance is ${updatedUserAccount.amount}"
    }
  }



  private def debitAmount(debitAmount:UserAmountPair): String = {

    if (!this.userAccounts.contains(debitAmount.accNumber)) {

      "There is no user with such account number"
    }
    else {

      val userAccount: UserAccount = this.userAccounts(debitAmount.accNumber)
      val updatedUserAccount = userAccount.copy(amount = userAccount.amount - debitAmount.amount)
      this.userAccounts(debitAmount.accNumber) = updatedUserAccount

      s"Rs. ${debitAmount.amount} debited from account number ${updatedUserAccount.accountNumber}. "+
      s"Current balance is ${updatedUserAccount.amount}"
    }
  }



  private def saveBiller(biller : Biller) : String = {
    if(! this.userBills.contains(biller.accountNumber))
      "There is no user with such account number"
    else if(this.userBillIterations(biller.accountNumber).executedIterations ==
      this.userBillIterations(biller.accountNumber).totalIterations) {
      "You have reached the limits of transactions."
    }
    else {

      val bill: UserBill = UserBill(biller.category, biller.billerName, biller.transDate, biller.amount)

      this.userBills(biller.accountNumber) = this.userBills(biller.accountNumber) :+ bill

      val billIterations = this.userBillIterations(biller.accountNumber)

      val newBillIterations = billIterations.copy(
              executedIterations = billIterations.executedIterations + 1,
                paidAmount = billIterations.paidAmount + biller.amount
            )

      debitAmount(UserAmountPair(biller.accountNumber, biller.amount)) //Debit amount from account for the bill

      this.userBillIterations(biller.accountNumber) = newBillIterations
      s"The biller for the account number ${biller.accountNumber} has been saved"
    }
  }


  private def getReport(): List[Report] = {

    accNumToUserName.values.toList.map( accountNumber => {

          Report(
            this.userAccounts(accountNumber).accHolderName,
            accountNumber,
            this.userBills(accountNumber).head.billerName,
            this.userBills(accountNumber).head.category,
            this.userBillIterations(accountNumber).paidAmount
          )
    })

  }

}
