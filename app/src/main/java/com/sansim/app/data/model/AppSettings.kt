package com.sansim.app.data.model


data class App设置(var dark:Boolean=false,var remind天:Int=7,var trafficUrl:String="https://speed.cloudflare.com/__down?bytes=10485760",var trafficKb:Double=1.0,var tgEnabled:Boolean=false,var botToken:String="",var chatId:String="",var keepCycle:String="月",var backgroundUri:String="",var backgroundAlpha:Float=.72f,var reminderEnabled:Boolean=true,var notificationEnabled:Boolean=true,var remindHour:Int=9,var remindMinute:Int=0,var language:String="简体中文",var emailQuickEnabled:Boolean=true,var smtpEnabled:Boolean=false,var smtpHost:String="",var smtpPort:Int=465,var smtpUser:String="",var smtpPass:String="",var smtpFrom:String="",var smtpTo:String="",var cloudEnabled:Boolean=false,var cloudUrl:String="https://ccs.ziranaa.top:16670",var cloudApiKey:String="",var cloudTelegramEnabled:Boolean=true,var cloudEmailEnabled:Boolean=true,var cloudAutoSync:Boolean=false,var showFlag:Boolean=true)

