package com.sansim.app.data.model

import java.util.UUID
import java.time.LocalDate

data class PhoneNumberRecord(val id:String=UUID.randomUUID().toString(), val countryCode:String="+86", val countryName:String="中国", val flag:String="🇨🇳", val number:String="", val operator:String="", val expireDate:String=LocalDate.now().plusMonths(1).toString(), val note:String="", val balance:String="", val eid:String="", val smdp:String="", val activationCode:String="", val startDate:String=LocalDate.now().toString(), val createdAt:String=LocalDate.now().toString(), val activatedAt:String="", val longTerm:Boolean=false, val cycleDays:Int=30, val signalStatus:String="在线")

