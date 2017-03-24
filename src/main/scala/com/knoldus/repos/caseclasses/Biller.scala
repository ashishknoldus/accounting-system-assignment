package com.knoldus.repos.caseclasses

/**
  * Created by knoldus on 23/3/17.
  */
case class Biller(
                   category:            Category,
                   billerName:          String,
                   accountNumber:       Long,
                   transDate:           String,
                   amount:              Float
                 )
