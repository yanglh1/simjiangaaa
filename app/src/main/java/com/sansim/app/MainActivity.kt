
package com.sansim.app

import com.sansim.app.data.model.Country
import com.sansim.app.data.model.Countries
import com.sansim.app.data.model.OperatorInfo
import com.sansim.app.data.model.OperatorDatabase
import com.sansim.app.data.model.PhoneNumberRecord
import com.sansim.app.data.model.App设置
import com.sansim.app.i18n.tr
import com.sansim.app.i18n.dayText
import com.sansim.app.i18n.laterText
import com.sansim.app.i18n.expireText
import com.sansim.app.i18n.cycleText
import com.sansim.app.util.LocalAppLanguage
import com.sansim.app.util.L
import com.sansim.app.util.LT



import android.Manifest
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.widget.Toast
import java.io.File
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.SSLSocketFactory
import android.util.Base64
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.core.content.FileProvider
import com.sansim.app.esim.EsimScreen
import androidx.compose.ui.window.Dialog
import kotlin.concurrent.thread
import kotlin.math.roundToInt
import com.sansim.app.update.UpdateInfo
import com.sansim.app.update.UpdateChecker
import com.sansim.app.update.UpdateDialog

val LocalIsDark = compositionLocalOf { false }
@Composable private fun dk(dark: Color, light: Color): Color = if(LocalIsDark.current) dark else light

const val CHANNEL_ID = "san_sim_reminders"
const val PREF = "san_sim_data"

class SanSimApplication: Application() { override fun onCreate(){ super.onCreate(); NotificationHelper.createChannel(this) } }








object DataStore {
    fun load设置(ctx:Context):App设置 {
        val p=ctx.getSharedPreferences(PREF,0); val o=JSONObject(p.getString("settings","{}")!!)
        return App设置(o.optBoolean("dark"),o.optInt("remind天",7),o.optString("trafficUrl","https://speed.cloudflare.com/__down?bytes=10485760"),o.optDouble("trafficKb",1.0),o.optBoolean("tgEnabled"),o.optString("botToken"),o.optString("chatId"),o.optString("keepCycle","月"),o.optString("backgroundUri",""),o.optDouble("backgroundAlpha",0.72).toFloat(),o.optBoolean("reminderEnabled",true),o.optBoolean("notificationEnabled",true),o.optInt("remindHour",9),o.optInt("remindMinute",0),o.optString("language","简体中文"),o.optBoolean("emailQuickEnabled",true),o.optBoolean("smtpEnabled",false),o.optString("smtpHost",""),o.optInt("smtpPort",465),o.optString("smtpUser",""),o.optString("smtpPass",""),o.optString("smtpFrom",""),o.optString("smtpTo",""),o.optBoolean("cloudEnabled",false),o.optString("cloudUrl","https://ccs.ziranaa.top:16670"),o.optString("cloudApiKey",""),o.optBoolean("cloudTelegramEnabled",true),o.optBoolean("cloudEmailEnabled",true),o.optBoolean("cloudAutoSync",false),o.optBoolean("showFlag",true))
    }
    fun save设置(ctx:Context,s:App设置){
        val o=JSONObject().put("dark",s.dark).put("remind天",s.remind天).put("trafficUrl",s.trafficUrl).put("trafficKb",s.trafficKb).put("tgEnabled",s.tgEnabled).put("botToken",s.botToken).put("chatId",s.chatId).put("keepCycle",s.keepCycle).put("backgroundUri",s.backgroundUri).put("backgroundAlpha",s.backgroundAlpha.toDouble()).put("reminderEnabled",s.reminderEnabled).put("notificationEnabled",s.notificationEnabled).put("remindHour",s.remindHour).put("remindMinute",s.remindMinute).put("language",s.language).put("emailQuickEnabled",s.emailQuickEnabled).put("smtpEnabled",s.smtpEnabled).put("smtpHost",s.smtpHost).put("smtpPort",s.smtpPort).put("smtpUser",s.smtpUser).put("smtpPass",s.smtpPass).put("smtpFrom",s.smtpFrom).put("smtpTo",s.smtpTo).put("cloudEnabled",s.cloudEnabled).put("cloudUrl",s.cloudUrl).put("cloudApiKey",s.cloudApiKey).put("cloudTelegramEnabled",s.cloudTelegramEnabled).put("cloudEmailEnabled",s.cloudEmailEnabled).put("cloudAutoSync",s.cloudAutoSync).put("showFlag",s.showFlag)
        ctx.getSharedPreferences(PREF,0).edit().putString("settings",o.toString()).apply(); ReminderScheduler.schedule全部(ctx)
    }
    fun normalizeLongTerm(r:PhoneNumberRecord):PhoneNumberRecord{
        if(!r.longTerm) return r
        val today=LocalDate.now(); var exp=runCatching{LocalDate.parse(r.expireDate)}.getOrNull() ?: return r
        val step=r.cycleDays.coerceIn(1,3650)
        while(exp.isBefore(today)) exp=exp.plusDays(step.toLong())
        return if(exp.toString()!=r.expireDate) r.copy(expireDate=exp.toString()) else r
    }
    fun loadRecords(ctx:Context):List<PhoneNumberRecord>{
        val arr=JSONArray(ctx.getSharedPreferences(PREF,0).getString("records","[]"))
        return (0 until arr.length()).map{ val o=arr.getJSONObject(it)
            normalizeLongTerm(PhoneNumberRecord(
                id=o.optString("id",UUID.randomUUID().toString()), countryCode=o.optString("countryCode","+86"), countryName=o.optString("countryName","中国"), flag=o.optString("flag","🇨🇳"), number=o.optString("number"), operator=o.optString("operator"), expireDate=o.optString("expireDate",LocalDate.now().plusDays(30).toString()), note=o.optString("note"),
                balance=o.optString("balance"), eid=o.optString("eid"), smdp=o.optString("smdp"), activationCode=o.optString("activationCode"), startDate=o.optString("startDate",LocalDate.now().toString()), createdAt=o.optString("createdAt",LocalDate.now().toString()), activatedAt=o.optString("activatedAt"), longTerm=o.optBoolean("longTerm",false), cycleDays=o.optInt("cycleDays",30), signalStatus=o.optString("signalStatus","在线")
            ))
        }
    }
    fun recordJson(r:PhoneNumberRecord)=JSONObject().put("id",r.id).put("countryCode",r.countryCode).put("countryName",r.countryName).put("flag",r.flag).put("number",r.number).put("operator",r.operator).put("expireDate",r.expireDate).put("note",r.note).put("balance",r.balance).put("eid",r.eid).put("smdp",r.smdp).put("activationCode",r.activationCode).put("startDate",r.startDate).put("createdAt",r.createdAt).put("activatedAt",r.activatedAt).put("longTerm",r.longTerm).put("cycleDays",r.cycleDays).put("signalStatus",r.signalStatus)
    fun saveRecords(ctx:Context,list:List<PhoneNumberRecord>){ val arr=JSONArray(); list.forEach{ arr.put(recordJson(it)) }; ctx.getSharedPreferences(PREF,0).edit().putString("records",arr.toString()).apply(); ReminderScheduler.schedule全部(ctx) }
}

object NotificationHelper {
    fun createChannel(ctx:Context){ if(Build.VERSION.SDK_INT>=26){ val nm=ctx.getSystemService(NotificationManager::class.java); nm.createNotificationChannel(NotificationChannel(CHANNEL_ID,"simJ 到期提醒",NotificationManager.IMPORTANCE_HIGH)) } }
    fun notify(ctx:Context,id:Int,title:String,text:String,emailIntent:Intent?=null){
        val b=Notification.Builder(ctx,CHANNEL_ID).setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle(title).setContentText(text).setStyle(Notification.BigTextStyle().bigText(text)).setAutoCancel(true)
        if(emailIntent!=null){
            val pi=PendingIntent.getActivity(ctx,id+900000,emailIntent,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            b.addAction(android.R.drawable.ic_dialog_email,"发邮件",pi)
        }
        ctx.getSystemService(NotificationManager::class.java).notify(id,b.build())
    }
}
object ReminderScheduler {
    fun schedule全部(ctx:Context){
        val am=ctx.getSystemService(AlarmManager::class.java) ?: return
        val settings=DataStore.load设置(ctx)
        if(!settings.reminderEnabled && !settings.smtpEnabled) return
        val canExact = if (Build.VERSION.SDK_INT >= 31) am.canScheduleExactAlarms() else true
        DataStore.loadRecords(ctx).forEach{ r->
            val date=runCatching{LocalDate.parse(r.expireDate)}.getOrNull()?:return@forEach
            val time=date.minusDays(settings.remind天.toLong()).atTime(settings.remindHour.coerceIn(0,23),settings.remindMinute.coerceIn(0,59)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            if(time>System.currentTimeMillis()){
                val pi=PendingIntent.getBroadcast(ctx,r.id.hashCode(),Intent(ctx,ReminderReceiver::class.java).putExtra("id",r.id),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                runCatching{
                    if(canExact) am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pi)
                    else am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,time,pi)
                }
            }
        }
    }
}
class ReminderReceiver: BroadcastReceiver(){ override fun onReceive(ctx:Context,intent:Intent){
    val id=intent.getStringExtra("id"); val r=DataStore.loadRecords(ctx).firstOrNull{it.id==id}?:return
    val s=DataStore.load设置(ctx)
    val subject="simJ 号码到期提醒：${r.operator.ifBlank{r.countryName}} ${r.countryCode} ${formatNumber(r.number)}"
    val body=buildEmailBody(r,s)
    val msg="${r.flag} ${r.countryCode} ${formatNumber(r.number)} 将于 ${r.expireDate} 到期"
    if(s.notificationEnabled){
        val emailIntent=if(s.emailQuickEnabled) makeEmailIntent(s.smtpTo,subject,body) else null
        NotificationHelper.notify(ctx,r.id.hashCode(),"号码即将到期",msg,emailIntent)
    }
    if(s.tgEnabled) sendTelegram(s.botToken,s.chatId,"⏰ Sim Jiang 到期提醒\n$msg")
    if(s.smtpEnabled) sendSmtpMail(s,subject,body)
} }
class BootReceiver: BroadcastReceiver(){ override fun onReceive(ctx:Context,intent:Intent){ ReminderScheduler.schedule全部(ctx) } }
fun sendTelegram(token:String,chatId:String,text:String){ if(token.isBlank()||chatId.isBlank()) return; thread { runCatching{ val u="https://api.telegram.org/bot$token/sendMessage?chat_id=${URLEncoder.encode(chatId,"UTF-8")}&text=${URLEncoder.encode(text,"UTF-8")}"; (URL(u).openConnection() as HttpURLConnection).apply{connectTimeout=8000;readTimeout=8000}.inputStream.close() } } }
fun buildEmailBody(r:PhoneNumberRecord,s:App设置):String = """
Sim Jiang 到期提醒

号码：${r.countryCode} ${formatNumber(r.number)}
国家/地区：${r.countryName}
运营商：${r.operator.ifBlank{r.countryName}}
到期日期：${r.expireDate}
套餐余额：${r.balance.ifBlank{"未填写"}}
EID：${r.eid.ifBlank{"未填写"}}
备注：${r.note.ifBlank{"无"}}

请及时保号、充值或刷流量。
""".trimIndent()
fun makeEmailIntent(to:String,subject:String,body:String):Intent = Intent(Intent.ACTION_SENDTO).apply{
    data=Uri.parse("mailto:")
    if(to.isNotBlank()) putExtra(Intent.EXTRA_EMAIL,arrayOf(to))
    putExtra(Intent.EXTRA_SUBJECT,subject)
    putExtra(Intent.EXTRA_TEXT,body)
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
fun smtpB64(s:String)=Base64.encodeToString(s.toByteArray(Charsets.UTF_8),Base64.NO_WRAP)
fun sendSmtpMail(s:App设置,subject:String,body:String,onResult:((Boolean,String)->Unit)?=null){
    fun done(ok:Boolean,msg:String){ onResult?.let{ Handler(Looper.getMainLooper()).post{ it(ok,msg) } } }
    if(!s.smtpEnabled || s.smtpHost.isBlank() || s.smtpUser.isBlank() || s.smtpPass.isBlank() || s.smtpTo.isBlank()) { done(false,"SMTP 未配置完整"); return }
    thread{
        val res=runCatching{
            val port=s.smtpPort.coerceIn(1,65535)
            val socket=(SSLSocketFactory.getDefault() as SSLSocketFactory).createSocket(s.smtpHost,port)
            val reader=socket.getInputStream().bufferedReader(Charsets.UTF_8)
            val writer=socket.getOutputStream().bufferedWriter(Charsets.UTF_8)
            fun readResp():String = reader.readLine() ?: ""
            fun cmd(x:String):String{ writer.write(x+"\r\n"); writer.flush(); return readResp() }
            readResp()
            cmd("EHLO simjiang.local")
            cmd("AUTH LOGIN")
            cmd(smtpB64(s.smtpUser))
            cmd(smtpB64(s.smtpPass))
            val from=s.smtpFrom.ifBlank{s.smtpUser}
            cmd("MAIL FROM:<$from>")
            cmd("RCPT TO:<${s.smtpTo}>")
            cmd("DATA")
            val mail="From: $from\r\nTo: ${s.smtpTo}\r\nSubject: =?UTF-8?B?${smtpB64(subject)}?=\r\nContent-Type: text/plain; charset=UTF-8\r\n\r\n$body\r\n."
            cmd(mail)
            cmd("QUIT")
            socket.close()
            true
        }
        res.onSuccess{ done(true,"测试邮件已提交 SMTP") }.onFailure{ done(false,"发送失败：${it.javaClass.simpleName}: ${it.message}") }
    }
}

class MainActivity: ComponentActivity(){ private val req=registerForActivityResult(ActivityResultContracts.RequestPermission()){}; override fun onCreate(b:Bundle?){ super.onCreate(b); if(Build.VERSION.SDK_INT>=33) req.launch(Manifest.permission.POST_NOTIFICATIONS); setContent{ App(this) } } }

@Composable fun App(ctx:Context){
    var settings by remember{ mutableStateOf(DataStore.load设置(ctx)) }
    var records by remember{ mutableStateOf(DataStore.loadRecords(ctx)) }
    var screen by remember{ mutableStateOf("home") }
    var edit by remember{ mutableStateOf<PhoneNumberRecord?>(null) }
    var trafficTarget by remember{ mutableStateOf<PhoneNumberRecord?>(null) }
    var toolMessage by remember{ mutableStateOf<String?>(null) }
    var exportDialog by remember{ mutableStateOf<Pair<String,String>?>(null) }
    var filter by remember{ mutableStateOf("全部") }
    var sortMode by remember{ mutableStateOf("到期近") }
    var search by remember{ mutableStateOf("") }
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    val currentVersion = try { ctx.packageManager.getPackageInfo(ctx.packageName,0).versionName ?: "0.0.0" } catch(_:Exception) { "0.0.0" }
    val colors=if(settings.dark) darkColorScheme(primary=Color(0xFF0A84FF),background=Color(0xFF0B0F17),surface=Color(0xFF151922)) else lightColorScheme(primary=Color(0xFF007AFF),background=Color(0xFFF4F5F7),surface=Color.White)
    val lang = settings.language
    fun tx(key:String)=tr(lang,key)
    fun autoCloudSync(rs:List<PhoneNumberRecord>, st:App设置){
        if(st.cloudEnabled && st.cloudAutoSync && cleanCloudApiKey(st.cloudApiKey).isNotBlank()){
            cloudPost(st,"/api/sync",cloudPayload(rs,st)){_,_->}
        }
    }
    MaterialTheme(colors){
    LaunchedEffect(Unit) {
        val now = System.currentTimeMillis()
        val prefs = ctx.getSharedPreferences("update_prefs",0)
        val last = prefs.getLong("last_check",0L)
        if (now - last > 86400000L) {
            val info = runCatching { UpdateChecker.check(currentVersion) }.getOrNull()
            if (info != null) updateInfo = info
            prefs.edit().putLong("last_check", now).apply()
        }
    }
        CompositionLocalProvider(LocalLayoutDirection provides if(settings.language=="阿拉伯语") LayoutDirection.Rtl else LayoutDirection.Ltr, LocalAppLanguage provides settings.language, LocalIsDark provides settings.dark){
        run{ val editing = edit!=null
            if(editing && edit!=null){
                Full编辑Screen(init=edit!!, onDismiss={edit=null}, onSave={r->
                    val c=Countries.list.firstOrNull{it.code==r.countryCode && it.name==r.countryName} ?: Countries.list.firstOrNull{it.code==r.countryCode} ?: Countries.list.first()
                    val nr=r.copy(countryCode=c.code,countryName=c.name,flag=c.flag,operator= if(r.operator.isBlank()) guessOperator(r.number,c.iso) else r.operator, createdAt=if(r.createdAt.isBlank()) LocalDate.now().toString() else r.createdAt, activatedAt=if(r.activatedAt.isBlank() && (r.smdp.isNotBlank() || r.activationCode.isNotBlank())) LocalDate.now().toString() else r.activatedAt)
                    records= if(records.any{it.id==nr.id}) records.map{if(it.id==nr.id)nr else it} else records+nr
                    DataStore.saveRecords(ctx,records); autoCloudSync(records,settings)
                    edit=null
                }, onDelete={r->
                    records=records.filter{it.id!=r.id}
                    DataStore.saveRecords(ctx,records); autoCloudSync(records,settings)
                    edit=null
                })
            } else {
                Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)){
                    if (screen != "esim") {
                        SimHubTopBar(screen,settings.dark,{ settings=settings.copy(dark=!settings.dark); DataStore.save设置(ctx,settings) },search,{ search=it }){ target->
                            when(target){
                                "add" -> edit=PhoneNumberRecord()
                                "export" -> toolMessage=tx("导出数据已准备")+"："+tx("当前共有")+" ${records.size} "+tx("个号码")+"。\n"+tx("数据已保存在本机应用存储中，后续可接入文件导出。")
                                "grid" -> screen="countries"
                                else -> screen=target
                            }
                        }
                    }
                    Box(Modifier.weight(1f).fillMaxWidth()){
                        when(screen){
                            "home"->Home(ctx,records,settings,search,filter,sortMode,{filter=it},{sortMode=if(sortMode=="到期近") "到期远" else "到期近"},{edit=PhoneNumberRecord()},{edit=it},{r->records=records.filter{it.id!=r.id};DataStore.saveRecords(ctx,records); autoCloudSync(records,settings)},{dial(ctx,it)},{trafficTarget=it},{r,months->val nr=r.copy(expireDate=(runCatching{LocalDate.parse(r.expireDate)}.getOrNull()?:LocalDate.now()).plusDays(months.toLong()).toString());records=records.map{if(it.id==r.id)nr else it};DataStore.saveRecords(ctx,records); autoCloudSync(records,settings)})
                            "keep"->KeepPage(records,{r,m-> val nr=r.copy(expireDate=(runCatching{LocalDate.parse(r.expireDate)}.getOrNull()?:LocalDate.now()).plusDays(m.toLong()).toString()); records=records.map{if(it.id==r.id)nr else it}; DataStore.saveRecords(ctx,records); autoCloudSync(records,settings)})
                            "tools"->ToolsPage(ctx,settings,records,{trafficTarget=it},{dial(ctx,it)},{ exportDialog="json" to exportRecordsJson(records,settings) },{ exportDialog="csv" to exportRecordsCsv(records) },{ text-> val (imported,importedSettings)=parseRecordsAndSettings(text); if(imported.isNotEmpty()){ records=imported; DataStore.saveRecords(ctx,records); if(importedSettings!=null){ settings=importedSettings; DataStore.save设置(ctx,settings) }; autoCloudSync(records,settings); toolMessage=tx("导入完成")+"：${records.size} "+tx("个号码")+(if(importedSettings!=null) " + "+tx("配置已恢复") else "") } else toolMessage=tx("导入失败：未识别 JSON/CSV 数据") })
                            "settings"->{
                                设置Page(ctx,settings,records,currentVersion=currentVersion,onUpdateCheck={
                                    kotlin.concurrent.thread {
                                        val info = runCatching { kotlinx.coroutines.runBlocking { UpdateChecker.check(currentVersion) } }.getOrNull()
                                        if (info != null) { updateInfo = info }
                                    }
                                },on={s->settings=s;DataStore.save设置(ctx,s); autoCloudSync(records,s)},onTraffic={trafficTarget=it},onDial={dial(ctx,it)},onExportJson={exportDialog="json" to exportRecordsJson(records,settings)},onExportCsv={exportDialog="csv" to exportRecordsCsv(records)},onImportText={text-> val (imported,importedSettings)=parseRecordsAndSettings(text); if(imported.isNotEmpty()){ records=imported; DataStore.saveRecords(ctx,records); if(importedSettings!=null){ settings=importedSettings; DataStore.save设置(ctx,settings) }; autoCloudSync(records,settings); toolMessage=tx("导入完成")+"：${records.size} "+tx("个号码")+(if(importedSettings!=null) " + "+tx("配置已恢复") else "") } else toolMessage=tx("导入失败：未识别 JSON/CSV 数据") })
                            }
                            "countries"->CountryPage()
                            "esim"->EsimScreen()
                        }
                        updateInfo?.let { info -> UpdateDialog(currentVersion = currentVersion, updateInfo = info, onDismiss = { updateInfo = null }) }
                    }
                    SimHubBottomNav(screen){ screen=it }
                }
            }
        }
        }
    }
    if(trafficTarget!=null) TrafficDialog(ctx,trafficTarget!!,settings,{trafficTarget=null})
    toolMessage?.let { msg ->
        IOSInfoDialog(L("操作结果"),msg){toolMessage=null}
    }
    exportDialog?.let { item -> ExportDataDialog(ctx,item.first,item.second){exportDialog=null} }
}

@Composable fun Header(screen:String,on:(String)->Unit){ SimHubTopBar(screen,false,{},"",{},on) }

@Composable fun IOSInfoDialog(title:String,message:String,onDismiss:()->Unit){
    Dialog(onDismissRequest=onDismiss){ Surface(shape=RoundedCornerShape(24.dp),color=Color(0xFFF2F3F7)){ Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp),horizontalAlignment=Alignment.CenterHorizontally){ Text(title,fontSize=20.sp,fontWeight=FontWeight.Bold,color=Color(0xFF111827)); Text(message,fontSize=13.sp,color=Color(0xFF374151)); Button(onDismiss,modifier=Modifier.fillMaxWidth().height(48.dp),shape=RoundedCornerShape(16.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF007AFF))){Text(L("好"))} } } }
}

@Composable fun ExportDataDialog(ctx:Context,type:String,content:String,onDismiss:()->Unit){
    val clipboard=LocalClipboardManager.current
    val ext=if(type=="csv") "csv" else "json"
    val title=if(type=="csv") L("导出 CSV") else L("导出 JSON")
    val exportFileTitle=L("导出文件")
    Dialog(onDismissRequest=onDismiss){
        Surface(shape=RoundedCornerShape(26.dp),color=Color(0xFFF2F3F7),modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
                Text(title,fontSize=21.sp,fontWeight=FontWeight.Bold,color=Color(0xFF111827))
                Text(L("可以复制到剪贴板，也可以生成文件并调用系统分享。"),fontSize=13.sp,color=Color(0xFF8A94A6))
                Box(Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha=.86f)).border(.7.dp,Color.White,RoundedCornerShape(16.dp)).verticalScroll(rememberScrollState()).padding(12.dp)){
                    Text(content,fontSize=11.sp,color=Color(0xFF374151),lineHeight=16.sp)
                }
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    Button({ clipboard.setText(AnnotatedString(content)) },modifier=Modifier.weight(1f).height(46.dp),shape=RoundedCornerShape(15.dp),colors=ButtonDefaults.buttonColors(containerColor=Color.White,contentColor=Color(0xFF007AFF))){Text(L("复制"))}
                    Button({ shareExportFile(ctx,"simJ-export-${System.currentTimeMillis()}.$ext",if(ext=="csv") "text/csv" else "application/json",content,exportFileTitle) },modifier=Modifier.weight(1f).height(46.dp),shape=RoundedCornerShape(15.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF007AFF))){Text(L("导出文件"))}
                }
                TextButton(onDismiss,modifier=Modifier.align(Alignment.CenterHorizontally)){Text(L("关闭"))}
            }
        }
    }
}

fun shareExportFile(ctx:Context,fileName:String,mime:String,content:String,title:String="导出文件"){
    runCatching{
        val dir=File(ctx.cacheDir,"exports"); dir.mkdirs()
        val f=File(dir,fileName); f.writeText(content,Charsets.UTF_8)
        val uri=FileProvider.getUriForFile(ctx,ctx.packageName+".fileprovider",f)
        val intent=Intent(Intent.ACTION_SEND).setType(mime).putExtra(Intent.EXTRA_STREAM,uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ctx.startActivity(Intent.createChooser(intent,title))
    }
}




@Composable fun SimHubTopBar(screen:String,dark:Boolean,onToggleDark:()->Unit,search:String,onSearch:(String)->Unit,on:(String)->Unit){
    val bg=if(dark) Color(0xFF0B0F17) else Color(0xFFF4F6FA)
    val surface=if(dark) Color(0xFF151922) else Color.White
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(Modifier.fillMaxWidth().background(bg).padding(start=18.dp,end=18.dp,top=statusBarTop+8.dp,bottom=6.dp)){
        if(screen=="home"){
            // search bar + dark mode toggle on same row
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
                TextField(value=search,onValueChange=onSearch,modifier=Modifier.weight(1f).heightIn(min=36.dp).clip(RoundedCornerShape(12.dp)),singleLine=true,
                    placeholder={Text(L("搜索运营商、国家或号码"),fontSize=13.sp,color=Color(0xFF8E8E93),maxLines=1,overflow=TextOverflow.Ellipsis)},leadingIcon={Canvas(Modifier.size(16.dp)){drawCircle(Color(0xFF8E8E93),radius=size.width/2-1.dp.toPx(),style=Stroke(1.5.dp.toPx()));drawLine(Color(0xFF8E8E93),Offset(size.width*.65f,size.height*.65f),Offset(size.width*.85f,size.height*.85f),strokeWidth=1.5.dp.toPx())}},
                    colors=TextFieldDefaults.colors(focusedContainerColor=surface,unfocusedContainerColor=surface,focusedIndicatorColor=Color.Transparent,unfocusedIndicatorColor=Color.Transparent))
                IconCircle(if(dark) "M" else "S",onToggleDark)
            }
        }else{
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                Text(when(screen){"tools"->L("工具");"settings"->L("设置");"esim"->"eSIM";else->L("号码")},fontSize=26.sp,fontWeight=FontWeight.Bold,modifier=Modifier.weight(1f),color=if(dark) Color.White else Color(0xFF111827))
            }
        }
    }
}

@Composable fun IconCircle(text:String,onClick:()->Unit){
    Box(Modifier.size(34.dp).clip(RoundedCornerShape(17.dp)).background(Color.White.copy(alpha=.92f)).border(.6.dp,Color(0xFFE5E7EB),RoundedCornerShape(17.dp)).clickable{onClick()},contentAlignment=Alignment.Center){Text(text,fontSize=15.sp,fontWeight=FontWeight.SemiBold,color=Color(0xFF374151))}
}

@Composable fun FilterToolRow(filter:String,sortMode:String,onFilter:(String)->Unit,onSort:()->Unit,count:Int){
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.Center,verticalAlignment=Alignment.CenterVertically){
        Row(horizontalArrangement=Arrangement.spacedBy(6.dp),verticalAlignment=Alignment.CenterVertically){
            FilterTool("≡",filter,Modifier.height(30.dp)){onFilter(when(filter){"全部"->"正常";"正常"->"即将到期";"即将到期"->"已过期";else->"全部"})}
            FilterTool("↕",if(sortMode=="到期近") L("近到远") else L("远到近"),Modifier.height(30.dp)){onSort()}
            Box(Modifier.height(30.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF93C5FD).copy(alpha=.25f)).clickable{}.padding(horizontal=10.dp),contentAlignment=Alignment.Center){Text("$count",fontSize=12.sp,fontWeight=FontWeight.Bold,color=Color(0xFF3B82F6))}
        }
    }
}

@Composable fun FilterTool(icon:String,text:String,m:Modifier,onClick:()->Unit){
    Row(m.height(30.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFF3B82F6)).clickable{onClick()}.padding(horizontal=10.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center){Text(icon,fontSize=11.sp,color=Color.White);Spacer(Modifier.width(4.dp));Text(text,fontSize=11.sp,fontWeight=FontWeight.SemiBold,color=Color.White,maxLines=1)}
}


@Composable fun CompactSimCard(r:PhoneNumberRecord,on编辑:(PhoneNumberRecord)->Unit,onDel:(PhoneNumberRecord)->Unit,onTraffic:(PhoneNumberRecord)->Unit,onKeep:(PhoneNumberRecord,Int)->Unit,days:Long?,remindDays:Int,showFlag:Boolean=true,dark:Boolean=false){
    val progress=when{days==null->.35f; days<0->.04f; else->(days.coerceIn(0,120).toFloat()/120f).coerceIn(.08f,.98f)}
    var hidden by remember{ mutableStateOf(true) }
    var del by remember{ mutableStateOf(false) }
    var keep by remember{ mutableStateOf(false) }
    val cardBg=if(dark) Color(0xFF1E2430).copy(alpha=.85f) else Color.White.copy(alpha=.35f); val cardBorder=if(dark) Color(0xFF2A3040).copy(alpha=.60f) else Color.White.copy(alpha=.50f); val txtPrimary=if(dark) Color(0xFFE8EAED) else if(showFlag) Color.White else Color(0xFF111827); val txtSecondary=if(dark) Color(0xFF9AA0A6) else if(showFlag) Color.White.copy(alpha=.85f) else Color(0xFF6B7280); val txtBody=if(dark) Color(0xFFD1D5DB) else if(showFlag) Color.White.copy(alpha=.9f) else Color(0xFF374151)
    Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=cardBg),elevation=CardDefaults.cardElevation(0.dp),modifier=Modifier.fillMaxWidth().height(150.dp).border(1.dp,cardBorder,RoundedCornerShape(24.dp))){
        Box(Modifier.fillMaxSize()){
            // frosted glass shimmer
            val glass=if(dark) listOf(Color(0xFF1E2430).copy(alpha=.15f),Color(0xFF1E2430).copy(alpha=.06f),Color(0xFF1E2430).copy(alpha=.12f)) else listOf(Color.White.copy(alpha=.18f),Color.White.copy(alpha=.08f),Color.White.copy(alpha=.15f)); Box(Modifier.fillMaxSize().background(Brush.verticalGradient(glass)).clip(RoundedCornerShape(24.dp)))
            if(showFlag){
                FlagArtPanel(r,Modifier.fillMaxSize())
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha=0.3f)))
            }
            Column(Modifier.fillMaxSize().padding(start=10.dp,end=10.dp,top=7.dp,bottom=6.dp),verticalArrangement=Arrangement.SpaceBetween){
                Row(verticalAlignment=Alignment.CenterVertically){
                    OperatorLogo44(r.operator.ifBlank{r.countryName}, Countries.list.firstOrNull{it.code==r.countryCode && it.name==r.countryName}?.iso ?: Countries.list.firstOrNull{it.code==r.countryCode}?.iso)
                    Spacer(Modifier.width(8.dp))
                    Text(r.operator.ifBlank{r.countryName},fontSize=16.sp,fontWeight=FontWeight.Bold,color=txtPrimary,maxLines=1,overflow=TextOverflow.Ellipsis,modifier=Modifier.weight(1f))
                    if(r.longTerm) Text("Long-term",fontSize=9.sp,fontWeight=FontWeight.Bold,color=Color.White,modifier=Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0xFF34C759)).padding(horizontal=6.dp,vertical=2.dp))
                    else Text("∞",fontSize=11.sp,fontWeight=FontWeight.Bold,color=Color.White,modifier=Modifier.clip(RoundedCornerShape(999.dp)).background(Color(0xFF007AFF)).padding(horizontal=6.dp,vertical=2.dp))
                    Spacer(Modifier.width(5.dp))
                    Text(r.countryName,fontSize=12.sp,color=txtSecondary,maxLines=1,overflow=TextOverflow.Ellipsis,modifier=Modifier.widthIn(max=72.dp))
                }
                Row(verticalAlignment=Alignment.CenterVertically){
                    Text(L("预付费")+" · ",fontSize=12.sp,color=txtBody,maxLines=1)
                    Box(Modifier.size(13.dp).clip(RoundedCornerShape(99.dp)).background(Color(0xFF22C55E)),contentAlignment=Alignment.Center){Text("✓",fontSize=8.sp,color=Color.White)}
                    Spacer(Modifier.width(4.dp))
                    Text("${formatDateByLang(r.expireDate, LocalAppLanguage.current)} · ${expireText(LocalAppLanguage.current,days)}",fontSize=12.sp,color=Color(0xFF16A34A),fontWeight=FontWeight.SemiBold,maxLines=1,overflow=TextOverflow.Ellipsis)
                }
                Row(verticalAlignment=Alignment.CenterVertically){
                    Text("☎ ${r.countryCode} ${if(hidden) "•••• ${r.number.takeLast(4)}" else formatNumber(r.number)}",fontSize=15.sp,fontWeight=FontWeight.Medium,color=txtPrimary,maxLines=1,overflow=TextOverflow.Ellipsis,modifier=Modifier.weight(1f))
                    Text(r.balance.ifBlank{estimateBalance(r)},fontSize=14.sp,fontWeight=FontWeight.SemiBold,color=Color(0xFF007AFF),maxLines=1)
                    Spacer(Modifier.width(8.dp))
                    Text(if(hidden)"◉" else "◎",fontSize=16.sp,color=txtBody,modifier=Modifier.clickable{hidden=!hidden})
                }
                Row(verticalAlignment=Alignment.CenterVertically){Text("EID ${r.eid.ifBlank{fakeEidForCard(r)}}",fontSize=10.sp,color=if(dark) Color(0xFFB0B8C4) else txtSecondary,maxLines=1,overflow=TextOverflow.Ellipsis,modifier=Modifier.weight(1f)); Text(signalIcon(r.signalStatus)+" "+r.signalStatus,fontSize=10.sp,color=Color(0xFF16A34A),maxLines=1)}
                Box(Modifier.fillMaxWidth(.80f).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFE5E7EB))){Box(Modifier.fillMaxWidth(progress).fillMaxHeight().background(Color(0xFF22C55E)))}
                Spacer(Modifier.height(2.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(18.dp)){
                    CardIconAction("keep",Color(0xFF8B5CF6)){keep=true}
                    CardIconAction("traffic",Color(0xFF007AFF)){onTraffic(r)}
                    CardIconAction("edit",Color(0xFFFF9500)){on编辑(r)}
                }
            }
        }
    }
    if(keep) KeepCycleDialog(r,onKeep){keep=false}
    if(del) IOSConfirmDialog(L("删除号码？"),L("删除")+" ${r.countryCode} ${formatNumber(r.number)} "+L("删除后不可恢复"),true,{del=false},{del=false;onDel(r)})
}

@Composable fun MiniAction(text:String,color:Color,onClick:()->Unit){
    Box(Modifier.widthIn(min=48.dp,max=62.dp).height(28.dp).clip(RoundedCornerShape(16.dp)).background(color.copy(alpha=.10f)).clickable{onClick()},contentAlignment=Alignment.Center){Text(text,fontSize=11.sp,fontWeight=FontWeight.SemiBold,color=color,maxLines=1)}
}

@Composable fun CardIconAction(type:String,color:Color,onClick:()->Unit){
    Box(Modifier.width(30.dp).height(40.dp).clip(RoundedCornerShape(13.dp)).background(color.copy(alpha=.14f)).clickable{onClick()},contentAlignment=Alignment.Center){
        Canvas(Modifier.width(16.dp).height(26.dp)){
            val w=size.width; val h=size.height; val st=Stroke(width=1.9f)
            when(type){
                "keep"->{ // shield / 保号
                    drawLine(color,Offset(w*.5f,h*.16f),Offset(w*.82f,h*.30f),strokeWidth=1.9f)
                    drawLine(color,Offset(w*.82f,h*.30f),Offset(w*.82f,h*.54f),strokeWidth=1.9f)
                    drawLine(color,Offset(w*.82f,h*.54f),Offset(w*.5f,h*.86f),strokeWidth=1.9f)
                    drawLine(color,Offset(w*.5f,h*.86f),Offset(w*.18f,h*.54f),strokeWidth=1.9f)
                    drawLine(color,Offset(w*.18f,h*.54f),Offset(w*.18f,h*.30f),strokeWidth=1.9f)
                    drawLine(color,Offset(w*.18f,h*.30f),Offset(w*.5f,h*.16f),strokeWidth=1.9f)
                    drawLine(color,Offset(w*.36f,h*.50f),Offset(w*.46f,h*.62f),strokeWidth=1.9f)
                    drawLine(color,Offset(w*.46f,h*.62f),Offset(w*.66f,h*.38f),strokeWidth=1.9f)
                }
                "traffic"->{ // bars / 刷流量
                    drawLine(color,Offset(w*.24f,h*.80f),Offset(w*.24f,h*.56f),strokeWidth=2.4f)
                    drawLine(color,Offset(w*.5f,h*.80f),Offset(w*.5f,h*.36f),strokeWidth=2.4f)
                    drawLine(color,Offset(w*.76f,h*.80f),Offset(w*.76f,h*.18f),strokeWidth=2.4f)
                }
                "edit"->{ // pencil / 编辑
                    drawLine(color,Offset(w*.26f,h*.74f),Offset(w*.70f,h*.30f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.70f,h*.30f),Offset(w*.82f,h*.42f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.82f,h*.42f),Offset(w*.38f,h*.86f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.38f,h*.86f),Offset(w*.20f,h*.86f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.20f,h*.86f),Offset(w*.26f,h*.74f),strokeWidth=2.0f)
                }
                else->{ // trash / 删除
                    drawLine(color,Offset(w*.22f,h*.30f),Offset(w*.78f,h*.30f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.40f,h*.30f),Offset(w*.42f,h*.18f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.42f,h*.18f),Offset(w*.58f,h*.18f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.58f,h*.18f),Offset(w*.60f,h*.30f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.28f,h*.30f),Offset(w*.32f,h*.84f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.32f,h*.84f),Offset(w*.68f,h*.84f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.68f,h*.84f),Offset(w*.72f,h*.30f),strokeWidth=2.0f)
                    drawLine(color,Offset(w*.5f,h*.40f),Offset(w*.5f,h*.74f),strokeWidth=1.6f)
                }
            }
        }
    }
}

@Composable fun OperatorLogo44(name:String, iso:String?=null){
    val info=remember(name, iso){ OperatorDatabase.find(name, iso) }
    val display=info?.carrierName ?: name
    val localLogo = remember(display, name, iso){ OperatorLogoAssets.assetFor(display, iso).ifBlank { OperatorLogoAssets.assetFor(name, iso) } }
    val onlineLogo = if(localLogo.isBlank()) (info?.logoUrl ?: "") else ""
    val assetPath = localLogo.removePrefix("file:///android_asset/")
    val assetBitmap = rememberAssetBitmap(assetPath)
    val op=display.uppercase()
    val label=when{
        "移动" in display || "CHINA MOBILE" in op -> "CM"
        "联通" in display || "UNICOM" in op -> "CU"
        "电信" in display || "TELECOM" in op -> "CT"
        "广电" in display -> "CB"
        "GIFFGAFF" in op -> "giff"
        "3HK" in op || "THREE" in op -> "3"
        "HKT" in op || "CSL" in op -> "HKT"
        "SMARTONE" in op -> "ST"
        "CMHK" in op || "CHINA MOBILE HONG KONG" in op -> "CMHK"
        "RAKUTEN" in op -> "R"
        "SOFTBANK" in op -> "SB"
        "DOCOMO" in op -> "doc"
        "AIS" in op -> "AIS"
        "TRUE" in op -> "TRUE"
        "DTAC" in op -> "dtac"
        "VODAFONE" in op -> "V"
        "T-MOBILE" in op -> "T"
        "AT&T" in op -> "AT&T"
        "VERIZON" in op -> "VZ"
        info!=null -> display.split(" ").filter{it.isNotBlank()}.take(2).joinToString(""){it.first().uppercase()}.ifBlank{"SIM"}
        else -> "SIM"
    }
    val bg=when(label){"CM"->Color(0xFF0085D0);"CU"->Color(0xFFE60012);"CT"->Color(0xFF005BAC);"AIS"->Color(0xFF78BE20);"R"->Color(0xFFBF0000);"V"->Color(0xFFE60000);"T"->Color(0xFFE20074);else->Color(0xFF111827)}
    Box(Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)).background(if(assetBitmap!=null || onlineLogo.isNotBlank()) Color.White else bg).border(.75.dp,Color(0xFFE5E7EB),RoundedCornerShape(13.dp)),contentAlignment=Alignment.Center){
        when{
            assetBitmap!=null -> Image(bitmap=assetBitmap,contentDescription=display,contentScale=ContentScale.Fit,modifier=Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)))
            onlineLogo.isNotBlank() -> AsyncImage(model=onlineLogo,contentDescription=display,contentScale=ContentScale.Fit,modifier=Modifier.size(38.dp).clip(RoundedCornerShape(10.dp)))
            else -> Text(label,fontSize=if(label.length>3) 8.sp else 12.sp,fontWeight=FontWeight.Bold,color=Color.White,maxLines=1)
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable fun SubscriptionCard(r:PhoneNumberRecord,on编辑:(PhoneNumberRecord)->Unit,onDel:(PhoneNumberRecord)->Unit,onTraffic:(PhoneNumberRecord)->Unit,onKeep:(PhoneNumberRecord,Int)->Unit,days:Long?,remindDays:Int){
    val status=if(days==null)L("未知") else if(days<0)L("已过期") else if(days<=remindDays)L("即将到期") else L("预付费")
    val theme=countryTheme(r.countryCode,r.countryName)
    val progress=when{days==null->.25f; days<0->1f; else->(1f-(days.coerceIn(0,365).toFloat()/365f)).coerceIn(.08f,.92f)}
    var del by remember{ mutableStateOf(false) }; var keep by remember{ mutableStateOf(false) }; var hidden by remember{ mutableStateOf(false) }
    Card(shape=RoundedCornerShape(24.dp),elevation=CardDefaults.cardElevation(3.dp),modifier=Modifier.fillMaxWidth().clickable{on编辑(r)}){
        Box(Modifier.background(Brush.linearGradient(theme)).padding(15.dp)){
            Column(verticalArrangement=Arrangement.spacedBy(9.dp)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    OperatorLogo(r.operator.ifBlank{guessOperator(r.number, Countries.list.firstOrNull{it.code==r.countryCode}?.iso ?: r.countryName)})
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)){
                        Row(verticalAlignment=Alignment.CenterVertically){ Text(r.operator.ifBlank{r.countryName},fontSize=18.sp,fontWeight=FontWeight.Bold,color=Color.White,maxLines=1,overflow=TextOverflow.Ellipsis); Spacer(Modifier.width(6.dp)); Text("CO",fontSize=9.sp,color=Color.White,modifier=Modifier.clip(RoundedCornerShape(5.dp)).background(Color(0xFF007AFF).copy(alpha=.75f)).padding(horizontal=4.dp,vertical=1.dp)) }
                        Text(r.countryName,fontSize=12.sp,color=Color.White.copy(alpha=.85f),maxLines=1,overflow=TextOverflow.Ellipsis)
                    }
                    Text(if(hidden)"◉" else "◎",fontSize=19.sp,color=Color.White.copy(alpha=.9f),modifier=Modifier.clickable{hidden=!hidden})
                }
                Row(verticalAlignment=Alignment.CenterVertically){ Text("✓",fontSize=12.sp,color=Color.White); Spacer(Modifier.width(5.dp)); Text(status,fontSize=12.sp,color=Color.White.copy(alpha=.92f)); Spacer(Modifier.width(7.dp)); Text("${r.expireDate} · ${if(days==null)"未知" else if(days<0)"已过期 ${-days} 天" else "还有 ${days} 天"}",fontSize=12.sp,color=Color.White.copy(alpha=.92f),maxLines=1,overflow=TextOverflow.Ellipsis) }
                Text(if(hidden) maskNumber(formatNumber(r.number)) else "${r.countryCode} ${maskNumber(formatNumber(r.number))}",fontSize=20.sp,fontWeight=FontWeight.SemiBold,color=Color.White,maxLines=1,overflow=TextOverflow.Ellipsis)
                Text(r.note.ifBlank{L("预付费 / 保号套餐")},fontSize=12.sp,color=Color.White.copy(alpha=.82f),maxLines=1,overflow=TextOverflow.Ellipsis)
                Box(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(99.dp)).background(Color.White.copy(alpha=.28f))){ Box(Modifier.fillMaxWidth(progress).fillMaxHeight().background(Color(0xFF34C759))) }
                FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(5.dp)){
                    WhitePill(L("保号")){keep=true}
                    WhitePill(L("刷流量")){onTraffic(r)}
                    WhitePill(L("删除"),danger=true){del=true}
                }
            }
        }
    }
    if(keep) KeepCycleDialog(r,onKeep){keep=false}
    if(del) IOSConfirmDialog(L("删除号码？"),L("删除")+" ${r.countryCode} ${formatNumber(r.number)} "+L("删除后不可恢复"),true,{del=false},{del=false;onDel(r)})
}

@Composable fun WhitePill(text:String,danger:Boolean=false,onClick:()->Unit){
    val c=if(danger) Color(0xFFFF3B30) else Color(0xFF007AFF)
    Text(text,fontSize=12.sp,fontWeight=FontWeight.SemiBold,color=c,modifier=Modifier.clip(RoundedCornerShape(99.dp)).background(Color.White.copy(alpha=.92f)).clickable{onClick()}.padding(horizontal=10.dp,vertical=5.dp))
}

fun countryTheme(code:String,name:String):List<Color>{
    return when{
        code=="+66" || name.contains("泰") -> listOf(Color(0xFF6A5CFF),Color(0xFF21C784))
        code=="+1" -> listOf(Color(0xFF1E3A8A),Color(0xFFDC2626))
        code=="+81" -> listOf(Color(0xFFEF4444),Color(0xFFFFD1D1))
        code=="+49" -> listOf(Color(0xFF111827),Color(0xFFF59E0B))
        code=="+852" || name.contains("香港") -> listOf(Color(0xFFB91C1C),Color(0xFFFF6B6B))
        code=="+853" || name.contains("澳门") -> listOf(Color(0xFF047857),Color(0xFFF59E0B))
        code=="+86" || name.contains("中国") -> listOf(Color(0xFFE60012),Color(0xFFD40000))
        else -> listOf(Color(0xFF2563EB),Color(0xFF0EA5E9))
    }
}

@Composable fun FlagArtPanel(r:PhoneNumberRecord,m:Modifier){
    val ctx = LocalContext.current
    val colors=countryTheme(r.countryCode,r.countryName)
    val iso = Countries.list.firstOrNull{it.code==r.countryCode && it.name==r.countryName}?.iso
        ?: Countries.list.firstOrNull{it.code==r.countryCode}?.iso
        ?: when{
            r.countryName.contains("中国") -> "CN"
            r.countryName.contains("香港") -> "HK"
            r.countryName.contains("澳门") -> "MO"
            else -> ""
        }
    val assetPath = if(iso.isBlank()) "" else "flag_backgrounds/${iso.lowercase()}.png"
    val flagBitmap = rememberAssetBitmap(assetPath)
    Box(m.background(Brush.linearGradient(colors)),contentAlignment=Alignment.Center){
        if(flagBitmap != null){
            Image(bitmap=flagBitmap,contentDescription=r.countryName,contentScale=ContentScale.FillBounds,modifier=Modifier.fillMaxSize().graphicsLayer(alpha=.96f))
            Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.White.copy(alpha=.16f),Color.Transparent,Color.Black.copy(alpha=.20f)))))
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White.copy(alpha=.10f),Color.Transparent,Color.Black.copy(alpha=.10f)))))
        }else{
            when{
                r.countryCode=="+86" || r.countryName.contains("中国") -> Box(Modifier.fillMaxSize()){
                    Text("★",fontSize=46.sp,color=Color(0xFFFFD21F).copy(alpha=.92f),modifier=Modifier.align(Alignment.TopStart).padding(start=20.dp,top=16.dp).graphicsLayer(rotationZ=-8f))
                    Text("★",fontSize=15.sp,color=Color(0xFFFFD21F).copy(alpha=.88f),modifier=Modifier.align(Alignment.TopStart).padding(start=70.dp,top=13.dp).graphicsLayer(rotationZ=18f))
                    Text("★",fontSize=15.sp,color=Color(0xFFFFD21F).copy(alpha=.88f),modifier=Modifier.align(Alignment.TopStart).padding(start=84.dp,top=31.dp).graphicsLayer(rotationZ=36f))
                    Text("★",fontSize=15.sp,color=Color(0xFFFFD21F).copy(alpha=.88f),modifier=Modifier.align(Alignment.TopStart).padding(start=83.dp,top=53.dp).graphicsLayer(rotationZ=10f))
                    Text("★",fontSize=15.sp,color=Color(0xFFFFD21F).copy(alpha=.88f),modifier=Modifier.align(Alignment.TopStart).padding(start=68.dp,top=70.dp).graphicsLayer(rotationZ=-18f))
                }
                else -> Text(r.flag,fontSize=68.sp,color=Color.White.copy(alpha=.82f),modifier=Modifier.graphicsLayer(scaleX=1.08f,scaleY=1.08f))
            }
            Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.White.copy(alpha=.12f),Color.Transparent,Color.Black.copy(alpha=.18f)))))
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White.copy(alpha=.10f),Color.Transparent,Color.Black.copy(alpha=.10f)))))
        }
    }
}

object AssetBitmapCache {
    private val cache = java.util.concurrent.ConcurrentHashMap<String, ImageBitmap>()
    fun cached(path:String): ImageBitmap? = cache[path]
    fun decode(ctx:Context, path:String): ImageBitmap? {
        if(path.isBlank()) return null
        cache[path]?.let { return it }
        val bmp = runCatching { ctx.assets.open(path).use { BitmapFactory.decodeStream(it)?.asImageBitmap() } }.getOrNull()
        if(bmp != null) cache[path] = bmp
        return bmp
    }
}

@Composable
fun rememberAssetBitmap(path:String): ImageBitmap? {
    val ctx = LocalContext.current
    // 命中缓存直接同步返回，避免闪烁；未命中则后台线程解码，主线程不阻塞
    return produceState<ImageBitmap?>(initialValue = AssetBitmapCache.cached(path), key1 = path) {
        if(path.isBlank()) { value = null; return@produceState }
        AssetBitmapCache.cached(path)?.let { value = it; return@produceState }
        value = withContext(Dispatchers.IO) { AssetBitmapCache.decode(ctx, path) }
    }.value
}

fun parseLpa(text:String):Pair<String,String>{
    val raw=text.trim()
    if(raw.isBlank()) return "" to ""
    val lpa=raw.removePrefix("LPA:1$").removePrefix("lpa:1$")
    val parts=lpa.split("$")
    if(parts.size>=2 && parts[0].contains('.')) return parts[0].trim() to parts[1].trim()
    val smdp=Regex("""(?i)(SM-DP\+?|SMDP|服务器|地址)[:：\s]+([^\s,;，；]+)""").find(raw)?.groupValues?.getOrNull(2)?.trim().orEmpty()
    val code=Regex("""(?i)(激活码|Activation\s*Code|code|AC)[:：\s]+([^\s,;，；]+)""").find(raw)?.groupValues?.getOrNull(2)?.trim().orEmpty()
    if(smdp.isNotBlank() || code.isNotBlank()) return smdp to code
    return "" to raw
}
fun formatChineseDate(s:String):String = runCatching{ val d=LocalDate.parse(s); "${d.year}年${d.monthValue}月${d.dayOfMonth}日" }.getOrElse{s}
fun formatDateByLang(s:String,lang:String):String = runCatching{ val d=LocalDate.parse(s); when(lang){"English"->"${d.year}-${d.monthValue.toString().padStart(2,'0')}-${d.dayOfMonth.toString().padStart(2,'0')}";"日本語"->"${d.year}年${d.monthValue}月${d.dayOfMonth}日";"阿拉伯语"->"${d.dayOfMonth}/${d.monthValue}/${d.year}";else->"${d.year}年${d.monthValue}月${d.dayOfMonth}日"} }.getOrElse{s}
fun estimateBalance(r:PhoneNumberRecord):String = when{
    r.countryCode=="+81" -> "250.00 CNY"
    r.countryCode=="+1" -> "4.50 USD"
    r.countryCode=="+49" -> "0.01 USD"
    r.countryCode=="+66" -> "2.40 CNY"
    else -> "--"
}
fun signalIcon(s:String)=when{ s.contains("离线")||s.contains("无") -> "○"; s.contains("弱") -> "▂"; s.contains("强") -> "▂▄▆"; else -> "▂▄" }

@Composable fun SimHubBottomNav(screen:String,on:(String)->Unit){
    Surface(color=MaterialTheme.colorScheme.surface.copy(alpha=.98f),shadowElevation=7.dp){
        Row(Modifier.fillMaxWidth().height(70.dp).padding(horizontal=20.dp),horizontalArrangement=Arrangement.SpaceAround,verticalAlignment=Alignment.CenterVertically){
            listOf("home" to L("号码"),"esim" to "eSIM","settings" to L("设置")).forEach{ item->
                val sel=screen==item.first
                val scale by animateFloatAsState(targetValue=if(sel)1.02f else 1f,animationSpec=tween(120),label="navScale")
                val tint=if(sel) Color(0xFF007AFF) else Color(0xFF8E8E93)
                Column(horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(3.dp),modifier=Modifier.clip(RoundedCornerShape(16.dp)).background(if(sel)Color(0xFF007AFF).copy(alpha=.08f) else Color.Transparent).clickable{on(item.first)}.padding(horizontal=18.dp,vertical=7.dp).graphicsLayer(scaleX=scale,scaleY=scale)){
                    BottomLineIcon(item.first,tint)
                    Text(item.second,fontSize=11.sp,fontWeight=if(sel)FontWeight.SemiBold else FontWeight.Normal,color=tint)
                }
            }
        }
    }
}

@Composable fun BottomLineIcon(type:String,color:Color){
    Canvas(Modifier.size(22.dp)){
        val w=size.width; val h=size.height
        val stroke=Stroke(width=2.2f)
        when(type){
            "home"->{
                drawRoundRect(color,topLeft=Offset(w*.22f,h*.16f),size=Size(w*.56f,h*.68f),cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.09f,w*.09f),style=stroke)
                drawLine(color,Offset(w*.34f,h*.30f),Offset(w*.66f,h*.30f),strokeWidth=2.2f)
                drawCircle(color,radius=w*.045f,center=Offset(w*.50f,h*.70f))
            }
            "tools"->{
                drawRoundRect(color,topLeft=Offset(w*.18f,h*.34f),size=Size(w*.64f,h*.42f),cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.08f,w*.08f),style=stroke)
                drawLine(color,Offset(w*.38f,h*.34f),Offset(w*.38f,h*.24f),strokeWidth=2.2f)
                drawLine(color,Offset(w*.62f,h*.34f),Offset(w*.62f,h*.24f),strokeWidth=2.2f)
                drawLine(color,Offset(w*.38f,h*.24f),Offset(w*.62f,h*.24f),strokeWidth=2.2f)
            }
            "esim"->{
                drawRoundRect(color,topLeft=Offset(w*.18f,h*.20f),size=Size(w*.64f,h*.60f),cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.08f,w*.08f),style=stroke)
                drawLine(color,Offset(w*.32f,h*.48f),Offset(w*.68f,h*.48f),strokeWidth=2.2f)
                drawLine(color,Offset(w*.32f,h*.56f),Offset(w*.58f,h*.56f),strokeWidth=1.8f)
            }
            else->{
                drawCircle(color,radius=w*.26f,center=Offset(w*.5f,h*.5f),style=stroke)
                drawCircle(color,radius=w*.075f,center=Offset(w*.5f,h*.5f))
                for(a in listOf(0f,90f,180f,270f)){
                    val rad=Math.toRadians(a.toDouble()).toFloat()
                    val x1=w*.5f+kotlin.math.cos(rad)*w*.34f; val y1=h*.5f+kotlin.math.sin(rad)*h*.34f
                    val x2=w*.5f+kotlin.math.cos(rad)*w*.43f; val y2=h*.5f+kotlin.math.sin(rad)*h*.43f
                    drawLine(color,Offset(x1,y1),Offset(x2,y2),strokeWidth=2.0f)
                }
            }
        }
    }
}

object OperatorLogoAssets {
    val map: Map<String,String> = mapOf(
        "CN|中国移动" to "file:///android_asset/operator_logos/cn_10086.png",
        "CN|中国联通" to "file:///android_asset/operator_logos/cn_10010.png",
        "CN|中国电信" to "file:///android_asset/operator_logos/cn_189.png",
        "CN|中国广电" to "file:///android_asset/operator_logos/cn_10099.png",
        "HK|csl" to "file:///android_asset/operator_logos/hk_csl.png",
        "HK|3hk" to "file:///android_asset/operator_logos/hk_3hk.png",
        "HK|Clubsim" to "file:///android_asset/operator_logos/hk_club_sim.png",
        "HK|smartone" to "file:///android_asset/operator_logos/hk_smartone.png",
        "HK|china mobile hong kong" to "file:///android_asset/operator_logos/hk_china_mobile_hong_kong.png",
        "MO|ctm" to "file:///android_asset/operator_logos/mo_ctm.png",
        "MO|3 macau" to "file:///android_asset/operator_logos/mo_3_macau.png",
        "TW|中华电信" to "file:///android_asset/operator_logos/tw_cht.png",
        "TW|台湾大哥大" to "file:///android_asset/operator_logos/tw_taiwanmobile.png",
        "TW|远传电信" to "file:///android_asset/operator_logos/tw_fetnet.png",
        "TW|台湾之星" to "file:///android_asset/operator_logos/tw_tstartel.png",
        "US|t-mobile" to "file:///android_asset/operator_logos/us_t_mobile.png",
        "US|at&t" to "file:///android_asset/operator_logos/us_at_t.png",
        "US|verizon" to "file:///android_asset/operator_logos/us_verizon.png",
        "US|us mobile" to "file:///android_asset/operator_logos/us_us_mobile.png",
        "US|mint mobile" to "file:///android_asset/operator_logos/us_mint_mobile.png",
        "US|visible" to "file:///android_asset/operator_logos/us_visible.png",
        "US|google fi" to "file:///android_asset/operator_logos/us_google_fi.png",
        "US|boost mobile" to "file:///android_asset/operator_logos/us_boost_mobile.png",
        "US|cricket wireless" to "file:///android_asset/operator_logos/us_cricket_wireless.png",
        "US|redpcket" to "file:///android_asset/operator_logos/us_redpocket_mobile.png",
        "CA|rogers" to "file:///android_asset/operator_logos/ca_rogers.png",
        "CA|bell" to "file:///android_asset/operator_logos/ca_bell.png",
        "CA|telus" to "file:///android_asset/operator_logos/ca_telus.png",
        "CA|freedom mobile" to "file:///android_asset/operator_logos/ca_freedom_mobile.png",
        "CA|fido" to "file:///android_asset/operator_logos/ca_fido.png",
        "GB|ee" to "file:///android_asset/operator_logos/gb_ee.png",
        "GB|o2 uk" to "file:///android_asset/operator_logos/gb_o2_uk.png",
        "GB|vodafone uk" to "file:///android_asset/operator_logos/gb_vodafone_uk.png",
        "GB|three uk" to "file:///android_asset/operator_logos/gb_three_uk.png",
        "GB|giffgaff" to "file:///android_asset/operator_logos/gb_giffgaff.png",
        "GB|CTEXCEL" to "file:///android_asset/operator_logos/gb_ctexcel.png",
        "DE|deutsche telekom" to "file:///android_asset/operator_logos/de_deutsche_telekom.png",
        "DE|vodafone germany" to "file:///android_asset/operator_logos/de_vodafone_germany.png",
        "DE|o2 germany" to "file:///android_asset/operator_logos/de_o2_germany.png",
        "DE|1&1" to "file:///android_asset/operator_logos/de_1_1.png",
        "FR|orange" to "file:///android_asset/operator_logos/fr_orange.png",
        "FR|sfr" to "file:///android_asset/operator_logos/fr_sfr.png",
        "FR|bouygues telecom" to "file:///android_asset/operator_logos/fr_bouygues_telecom.png",
        "FR|free mobile" to "file:///android_asset/operator_logos/fr_free_mobile.png",
        "IT|tim" to "file:///android_asset/operator_logos/it_tim.png",
        "IT|vodafone italy" to "file:///android_asset/operator_logos/it_vodafone_italy.png",
        "IT|windtre" to "file:///android_asset/operator_logos/it_windtre.png",
        "IT|iliad italy" to "file:///android_asset/operator_logos/it_iliad_italy.png",
        "ES|movistar" to "file:///android_asset/operator_logos/es_movistar.png",
        "ES|orange spain" to "file:///android_asset/operator_logos/es_orange_spain.png",
        "ES|vodafone spain" to "file:///android_asset/operator_logos/es_vodafone_spain.png",
        "ES|yoigo" to "file:///android_asset/operator_logos/es_yoigo.png",
        "NL|kpn" to "file:///android_asset/operator_logos/nl_kpn.png",
        "NL|vodafone netherlands" to "file:///android_asset/operator_logos/nl_vodafone_netherlands.png",
        "NL|odido" to "file:///android_asset/operator_logos/nl_odido.png",
        "BE|proximus" to "file:///android_asset/operator_logos/be_proximus.png",
        "BE|orange belgium" to "file:///android_asset/operator_logos/be_orange_belgium.png",
        "BE|base" to "file:///android_asset/operator_logos/be_base.png",
        "CH|swisscom" to "file:///android_asset/operator_logos/ch_swisscom.png",
        "CH|sunrise" to "file:///android_asset/operator_logos/ch_sunrise.png",
        "CH|salt" to "file:///android_asset/operator_logos/ch_salt.png",
        "AT|a1" to "file:///android_asset/operator_logos/at_a1.png",
        "AT|magenta telekom" to "file:///android_asset/operator_logos/at_magenta_telekom.png",
        "AT|drei austria" to "file:///android_asset/operator_logos/at_drei_austria.png",
        "SE|telia sweden" to "file:///android_asset/operator_logos/se_telia_sweden.png",
        "SE|tele2 sweden" to "file:///android_asset/operator_logos/se_tele2_sweden.png",
        "SE|telenor sweden" to "file:///android_asset/operator_logos/se_telenor_sweden.png",
        "NO|telenor norway" to "file:///android_asset/operator_logos/no_telenor_norway.png",
        "NO|telia norway" to "file:///android_asset/operator_logos/no_telia_norway.png",
        "NO|ice" to "file:///android_asset/operator_logos/no_ice.png",
        "DK|tdc net" to "file:///android_asset/operator_logos/dk_tdc_net.png",
        "DK|telenor denmark" to "file:///android_asset/operator_logos/dk_telenor_denmark.png",
        "DK|3 denmark" to "file:///android_asset/operator_logos/dk_3_denmark.png",
        "FI|elisa" to "file:///android_asset/operator_logos/fi_elisa.png",
        "FI|dna" to "file:///android_asset/operator_logos/fi_dna.png",
        "FI|telia finland" to "file:///android_asset/operator_logos/fi_telia_finland.png",
        "IS|síminn" to "file:///android_asset/operator_logos/is_s_minn.png",
        "IS|vodafone iceland" to "file:///android_asset/operator_logos/is_vodafone_iceland.png",
        "IS|nova" to "file:///android_asset/operator_logos/is_nova.png",
        "IE|vodafone ireland" to "file:///android_asset/operator_logos/ie_vodafone_ireland.png",
        "IE|three ireland" to "file:///android_asset/operator_logos/ie_three_ireland.png",
        "IE|eir" to "file:///android_asset/operator_logos/ie_eir.png",
        "PT|meo" to "file:///android_asset/operator_logos/pt_meo.png",
        "PT|nos" to "file:///android_asset/operator_logos/pt_nos.png",
        "PT|vodafone portugal" to "file:///android_asset/operator_logos/pt_vodafone_portugal.png",
        "GR|cosmote" to "file:///android_asset/operator_logos/gr_cosmote.png",
        "GR|vodafone greece" to "file:///android_asset/operator_logos/gr_vodafone_greece.png",
        "GR|nova greece" to "file:///android_asset/operator_logos/gr_nova_greece.png",
        "PL|orange poland" to "file:///android_asset/operator_logos/pl_orange_poland.png",
        "PL|play" to "file:///android_asset/operator_logos/pl_play.png",
        "PL|plus" to "file:///android_asset/operator_logos/pl_plus.png",
        "PL|t-mobile poland" to "file:///android_asset/operator_logos/pl_t_mobile_poland.png",
        "CZ|o2 czech republic" to "file:///android_asset/operator_logos/cz_o2_czech_republic.png",
        "CZ|t-mobile czech republic" to "file:///android_asset/operator_logos/cz_t_mobile_czech_republic.png",
        "CZ|vodafone czech republic" to "file:///android_asset/operator_logos/cz_vodafone_czech_republic.png",
        "SK|orange slovakia" to "file:///android_asset/operator_logos/sk_orange_slovakia.png",
        "SK|slovak telekom" to "file:///android_asset/operator_logos/sk_slovak_telekom.png",
        "SK|o2 slovakia" to "file:///android_asset/operator_logos/sk_o2_slovakia.png",
        "HU|magyar telekom" to "file:///android_asset/operator_logos/hu_magyar_telekom.png",
        "HU|vodafone hungary" to "file:///android_asset/operator_logos/hu_vodafone_hungary.png",
        "HU|yettel hungary" to "file:///android_asset/operator_logos/hu_yettel_hungary.png",
        "RO|orange romania" to "file:///android_asset/operator_logos/ro_orange_romania.png",
        "RO|vodafone romania" to "file:///android_asset/operator_logos/ro_vodafone_romania.png",
        "RO|digi romania" to "file:///android_asset/operator_logos/ro_digi_romania.png",
        "BG|a1 bulgaria" to "file:///android_asset/operator_logos/bg_a1_bulgaria.png",
        "BG|yettel bulgaria" to "file:///android_asset/operator_logos/bg_yettel_bulgaria.png",
        "BG|vivacom" to "file:///android_asset/operator_logos/bg_vivacom.png",
        "BG|保加利亚 Telecom" to "file:///android_asset/operator_logos/bg_vivacom.png",
        "HR|t-mobile croatia" to "file:///android_asset/operator_logos/hr_t_mobile_croatia.png",
        "HR|a1 croatia" to "file:///android_asset/operator_logos/hr_a1_croatia.png",
        "HR|telemach croatia" to "file:///android_asset/operator_logos/hr_telemach_croatia.png",
        "SI|a1 slovenia" to "file:///android_asset/operator_logos/si_a1_slovenia.png",
        "SI|telekom slovenije" to "file:///android_asset/operator_logos/si_telekom_slovenije.png",
        "SI|telemach slovenia" to "file:///android_asset/operator_logos/si_telemach_slovenia.png",
        "RS|mts serbia" to "file:///android_asset/operator_logos/rs_mts_serbia.png",
        "RS|telenor serbia" to "file:///android_asset/operator_logos/rs_telenor_serbia.png",
        "RS|a1 serbia" to "file:///android_asset/operator_logos/rs_a1_serbia.png",
        "BA|bh telecom" to "file:///android_asset/operator_logos/ba_bh_telecom.png",
        "BA|m:tel" to "file:///android_asset/operator_logos/ba_m_tel.png",
        "BA|eronet" to "file:///android_asset/operator_logos/ba_eronet.png",
        "ME|t-mobile montenegro" to "file:///android_asset/operator_logos/me_t_mobile_montenegro.png",
        "ME|m:tel montenegro" to "file:///android_asset/operator_logos/me_m_tel_montenegro.png",
        "MK|t-mobile macedonia" to "file:///android_asset/operator_logos/mk_t_mobile_macedonia.png",
        "MK|a1 macedonia" to "file:///android_asset/operator_logos/mk_a1_macedonia.png",
        "AL|vodafone albania" to "file:///android_asset/operator_logos/al_vodafone_albania.png",
        "AL|one albania" to "file:///android_asset/operator_logos/al_one_albania.png",
        "LT|telia lithuania" to "file:///android_asset/operator_logos/lt_telia_lithuania.png",
        "LT|bitė lithuania" to "file:///android_asset/operator_logos/lt_bit__lithuania.png",
        "LT|tele2 lithuania" to "file:///android_asset/operator_logos/lt_tele2_lithuania.png",
        "LV|lmt" to "file:///android_asset/operator_logos/lv_lmt.png",
        "LV|tele2 latvia" to "file:///android_asset/operator_logos/lv_tele2_latvia.png",
        "LV|bite latvia" to "file:///android_asset/operator_logos/lv_bite_latvia.png",
        "EE|telia estonia" to "file:///android_asset/operator_logos/ee_telia_estonia.png",
        "EE|elisa estonia" to "file:///android_asset/operator_logos/ee_elisa_estonia.png",
        "EE|tele2 estonia" to "file:///android_asset/operator_logos/ee_tele2_estonia.png",
        "MD|orange moldova" to "file:///android_asset/operator_logos/md_orange_moldova.png",
        "MD|moldcell" to "file:///android_asset/operator_logos/md_moldcell.png",
        "BY|mts belarus" to "file:///android_asset/operator_logos/by_mts_belarus.png",
        "BY|a1 belarus" to "file:///android_asset/operator_logos/by_a1_belarus.png",
        "BY|life:) Belarus" to "file:///android_asset/operator_logos/by_life___belarus.png",
        "UA|kyivstar" to "file:///android_asset/operator_logos/ua_kyivstar.png",
        "UA|vodafone ukraine" to "file:///android_asset/operator_logos/ua_vodafone_ukraine.png",
        "UA|lifecell" to "file:///android_asset/operator_logos/ua_lifecell.png",
        "RU|mts russia" to "file:///android_asset/operator_logos/ru_mts_russia.png",
        "RU|beeline russia" to "file:///android_asset/operator_logos/ru_beeline_russia.png",
        "RU|megafon" to "file:///android_asset/operator_logos/ru_megafon.png",
        "KZ|kcell" to "file:///android_asset/operator_logos/kz_kcell.png",
        "KZ|beeline kazakhstan" to "file:///android_asset/operator_logos/kz_beeline_kazakhstan.png",
        "KZ|tele2 kazakhstan" to "file:///android_asset/operator_logos/kz_tele2_kazakhstan.png",
        "GE|magti" to "file:///android_asset/operator_logos/ge_magti.png",
        "GE|geocell" to "file:///android_asset/operator_logos/ge_geocell.png",
        "GE|beeline georgia" to "file:///android_asset/operator_logos/ge_beeline_georgia.png",
        "AM|viva-mts" to "file:///android_asset/operator_logos/am_viva_mts.png",
        "AM|beeline armenia" to "file:///android_asset/operator_logos/am_beeline_armenia.png",
        "AM|team telecom" to "file:///android_asset/operator_logos/am_team_telecom.png",
        "AZ|azercell" to "file:///android_asset/operator_logos/az_azercell.png",
        "AZ|bakcell" to "file:///android_asset/operator_logos/az_bakcell.png",
        "AZ|nar mobile" to "file:///android_asset/operator_logos/az_nar_mobile.png",
        "TR|turkcell" to "file:///android_asset/operator_logos/tr_turkcell.png",
        "TR|vodafone turkey" to "file:///android_asset/operator_logos/tr_vodafone_turkey.png",
        "TR|turk telekom" to "file:///android_asset/operator_logos/tr_turk_telekom.png",
        "IL|partner" to "file:///android_asset/operator_logos/il_partner.png",
        "IL|cellcom" to "file:///android_asset/operator_logos/il_cellcom.png",
        "IL|pelephone" to "file:///android_asset/operator_logos/il_pelephone.png",
        "AE|etisalat uae" to "file:///android_asset/operator_logos/ae_etisalat_uae.png",
        "AE|du" to "file:///android_asset/operator_logos/ae_du.png",
        "AE|virgin mobile uae" to "file:///android_asset/operator_logos/ae_virgin_mobile_uae.png",
        "SA|stc" to "file:///android_asset/operator_logos/sa_stc.png",
        "SA|mobily" to "file:///android_asset/operator_logos/sa_mobily.png",
        "SA|zain ksa" to "file:///android_asset/operator_logos/sa_zain_ksa.png",
        "QA|ooredoo qatar" to "file:///android_asset/operator_logos/qa_ooredoo_qatar.png",
        "QA|vodafone qatar" to "file:///android_asset/operator_logos/qa_vodafone_qatar.png",
        "KW|zain kuwait" to "file:///android_asset/operator_logos/kw_zain_kuwait.png",
        "KW|ooredoo kuwait" to "file:///android_asset/operator_logos/kw_ooredoo_kuwait.png",
        "KW|stc kuwait" to "file:///android_asset/operator_logos/kw_stc_kuwait.png",
        "BH|batelco" to "file:///android_asset/operator_logos/bh_batelco.png",
        "BH|zain bahrain" to "file:///android_asset/operator_logos/bh_zain_bahrain.png",
        "BH|stc bahrain" to "file:///android_asset/operator_logos/bh_stc_bahrain.png",
        "OM|omantel" to "file:///android_asset/operator_logos/om_omantel.png",
        "OM|ooredoo oman" to "file:///android_asset/operator_logos/om_ooredoo_oman.png",
        "JO|zain jordan" to "file:///android_asset/operator_logos/jo_zain_jordan.png",
        "JO|orange jordan" to "file:///android_asset/operator_logos/jo_orange_jordan.png",
        "JO|umniah" to "file:///android_asset/operator_logos/jo_umniah.png",
        "LB|alfa" to "file:///android_asset/operator_logos/lb_alfa.png",
        "LB|touch lebanon" to "file:///android_asset/operator_logos/lb_touch_lebanon.png",
        "EG|vodafone egypt" to "file:///android_asset/operator_logos/eg_vodafone_egypt.png",
        "EG|orange egypt" to "file:///android_asset/operator_logos/eg_orange_egypt.png",
        "EG|etisalat egypt" to "file:///android_asset/operator_logos/eg_etisalat_egypt.png",
        "MA|maroc telecom" to "file:///android_asset/operator_logos/ma_maroc_telecom.png",
        "MA|orange morocco" to "file:///android_asset/operator_logos/ma_orange_morocco.png",
        "MA|inwi morocco" to "file:///android_asset/operator_logos/ma_inwi_morocco.png",
        "TN|tunisie telecom" to "file:///android_asset/operator_logos/tn_tunisie_telecom.png",
        "TN|ooredoo tunisia" to "file:///android_asset/operator_logos/tn_ooredoo_tunisia.png",
        "TN|orange tunisia" to "file:///android_asset/operator_logos/tn_orange_tunisia.png",
        "DZ|djezzy" to "file:///android_asset/operator_logos/dz_djezzy.png",
        "DZ|ooredoo algeria" to "file:///android_asset/operator_logos/dz_ooredoo_algeria.png",
        "DZ|mobilis" to "file:///android_asset/operator_logos/dz_mobilis.png",
        "NG|mtn nigeria" to "file:///android_asset/operator_logos/ng_mtn_nigeria.png",
        "NG|airtel nigeria" to "file:///android_asset/operator_logos/ng_airtel_nigeria.png",
        "NG|glo nigeria" to "file:///android_asset/operator_logos/ng_glo_nigeria.png",
        "KE|safaricom" to "file:///android_asset/operator_logos/ke_safaricom.png",
        "KE|airtel kenya" to "file:///android_asset/operator_logos/ke_airtel_kenya.png",
        "ZA|vodacom" to "file:///android_asset/operator_logos/za_vodacom.png",
        "ZA|mtn south africa" to "file:///android_asset/operator_logos/za_mtn_south_africa.png",
        "ZA|cell c" to "file:///android_asset/operator_logos/za_cell_c.png",
        "JP|ntt docomo" to "file:///android_asset/operator_logos/jp_ntt_docomo.png",
        "JP|softbank" to "file:///android_asset/operator_logos/jp_softbank.png",
        "JP|au kddi" to "file:///android_asset/operator_logos/jp_au_kddi.png",
        "JP|rakuten mobile" to "file:///android_asset/operator_logos/jp_rakuten_mobile.png",
        "KR|sk telecom" to "file:///android_asset/operator_logos/kr_sk_telecom.png",
        "KR|kt" to "file:///android_asset/operator_logos/kr_kt.png",
        "KR|lg u+" to "file:///android_asset/operator_logos/kr_lg_u.png",
        "SG|singtel" to "file:///android_asset/operator_logos/sg_singtel.png",
        "SG|starhub" to "file:///android_asset/operator_logos/sg_starhub.png",
        "SG|m1" to "file:///android_asset/operator_logos/sg_m1.png",
        "SG|simba" to "file:///android_asset/operator_logos/sg_simba.png",
        "MY|maxis" to "file:///android_asset/operator_logos/my_maxis.png",
        "MY|celcomdigi" to "file:///android_asset/operator_logos/my_celcomdigi.png",
        "MY|u mobile" to "file:///android_asset/operator_logos/my_u_mobile.png",
        "MY|yes" to "file:///android_asset/operator_logos/my_yes.png",
        "MY|unifi mobile" to "file:///android_asset/operator_logos/my_unifi_mobile.png",
        "TH|ais" to "file:///android_asset/operator_logos/th_ais.png",
        "TH|true" to "file:///android_asset/operator_logos/th_true.png",
        "TH|dtac" to "file:///android_asset/operator_logos/th_dtac.png",
        "VN|viettel" to "file:///android_asset/operator_logos/vn_viettel.png",
        "VN|mobifone" to "file:///android_asset/operator_logos/vn_mobifone.png",
        "VN|vinaphone" to "file:///android_asset/operator_logos/vn_vinaphone.png",
        "PH|globe" to "file:///android_asset/operator_logos/ph_globe.png",
        "PH|smart" to "file:///android_asset/operator_logos/ph_smart.png",
        "PH|dito" to "file:///android_asset/operator_logos/ph_dito.png",
        "ID|telkomsel" to "file:///android_asset/operator_logos/id_telkomsel.png",
        "ID|indosat" to "file:///android_asset/operator_logos/id_indosat.png",
        "ID|xl axiata" to "file:///android_asset/operator_logos/id_xl_axiata.png",
        "ID|smartfren" to "file:///android_asset/operator_logos/id_smartfren.png",
        "KH|smart axiata" to "file:///android_asset/operator_logos/kh_smart_axiata.png",
        "KH|cellcard" to "file:///android_asset/operator_logos/kh_cellcard.png",
        "LA|lao telecom" to "file:///android_asset/operator_logos/la_lao_telecom.png",
        "LA|unitel laos" to "file:///android_asset/operator_logos/la_unitel_laos.png",
        "MM|mpt" to "file:///android_asset/operator_logos/mm_mpt.png",
        "MM|ooredoo myanmar" to "file:///android_asset/operator_logos/mm_ooredoo_myanmar.png",
        "BN|dst" to "file:///android_asset/operator_logos/bn_dst.png",
        "BN|progresif" to "file:///android_asset/operator_logos/bn_progresif.png",
        "IN|jio" to "file:///android_asset/operator_logos/in_jio.png",
        "IN|airtel india" to "file:///android_asset/operator_logos/in_airtel_india.png",
        "IN|vi india" to "file:///android_asset/operator_logos/in_vi_india.png",
        "PK|jazz" to "file:///android_asset/operator_logos/pk_jazz.png",
        "PK|zong" to "file:///android_asset/operator_logos/pk_zong.png",
        "PK|telenor pakistan" to "file:///android_asset/operator_logos/pk_telenor_pakistan.png",
        "BD|grameenphone" to "file:///android_asset/operator_logos/bd_grameenphone.png",
        "BD|banglalink" to "file:///android_asset/operator_logos/bd_banglalink.png",
        "BD|robi bangladesh" to "file:///android_asset/operator_logos/bd_robi_bangladesh.png",
        "LK|dialog" to "file:///android_asset/operator_logos/lk_dialog.png",
        "LK|mobitel sri lanka" to "file:///android_asset/operator_logos/lk_mobitel_sri_lanka.png",
        "NP|ncell" to "file:///android_asset/operator_logos/np_ncell.png",
        "NP|ntc nepal" to "file:///android_asset/operator_logos/np_ntc_nepal.png",
        "MV|dhiraagu" to "file:///android_asset/operator_logos/mv_dhiraagu.png",
        "MV|ooredoo maldives" to "file:///android_asset/operator_logos/mv_ooredoo_maldives.png",
        "AU|telstra" to "file:///android_asset/operator_logos/au_telstra.png",
        "AU|optus" to "file:///android_asset/operator_logos/au_optus.png",
        "AU|vodafone au" to "file:///android_asset/operator_logos/au_vodafone_au.png",
        "NZ|spark" to "file:///android_asset/operator_logos/nz_spark.png",
        "NZ|one nz" to "file:///android_asset/operator_logos/nz_one_nz.png",
        "NZ|2degrees" to "file:///android_asset/operator_logos/nz_2degrees.png",
        "FJ|vodafone fiji" to "file:///android_asset/operator_logos/fj_vodafone_fiji.png",
        "FJ|digicel fiji" to "file:///android_asset/operator_logos/fj_digicel_fiji.png",
        "BR|vivo brazil" to "file:///android_asset/operator_logos/br_vivo_brazil.png",
        "BR|claro brazil" to "file:///android_asset/operator_logos/br_claro_brazil.png",
        "BR|tim brazil" to "file:///android_asset/operator_logos/br_tim_brazil.png",
        "AR|personal" to "file:///android_asset/operator_logos/ar_personal.png",
        "AR|claro argentina" to "file:///android_asset/operator_logos/ar_claro_argentina.png",
        "AR|movistar argentina" to "file:///android_asset/operator_logos/ar_movistar_argentina.png",
        "CL|entel chile" to "file:///android_asset/operator_logos/cl_entel_chile.png",
        "CL|movistar chile" to "file:///android_asset/operator_logos/cl_movistar_chile.png",
        "CL|wom chile" to "file:///android_asset/operator_logos/cl_wom_chile.png",
        "CO|claro colombia" to "file:///android_asset/operator_logos/co_claro_colombia.png",
        "CO|movistar colombia" to "file:///android_asset/operator_logos/co_movistar_colombia.png",
        "CO|tigo colombia" to "file:///android_asset/operator_logos/co_tigo_colombia.png",
        "PE|claro peru" to "file:///android_asset/operator_logos/pe_claro_peru.png",
        "PE|movistar peru" to "file:///android_asset/operator_logos/pe_movistar_peru.png",
        "PE|bitel peru" to "file:///android_asset/operator_logos/pe_bitel_peru.png",
        "MX|telcel" to "file:///android_asset/operator_logos/mx_telcel.png",
        "MX|at&t mexico" to "file:///android_asset/operator_logos/mx_at_t_mexico.png",
        "MX|movistar mexico" to "file:///android_asset/operator_logos/mx_movistar_mexico.png",
        "UY|antel" to "file:///android_asset/operator_logos/uy_antel.png",
        "UY|movistar uruguay" to "file:///android_asset/operator_logos/uy_movistar_uruguay.png",
        "PY|tigo paraguay" to "file:///android_asset/operator_logos/py_tigo_paraguay.png",
        "PY|claro paraguay" to "file:///android_asset/operator_logos/py_claro_paraguay.png",
        "PY|personal paraguay" to "file:///android_asset/operator_logos/py_personal_paraguay.png",
        "BO|entel bolivia" to "file:///android_asset/operator_logos/bo_entel_bolivia.png",
        "BO|tigo bolivia" to "file:///android_asset/operator_logos/bo_tigo_bolivia.png",
        "EC|claro ecuador" to "file:///android_asset/operator_logos/ec_claro_ecuador.png",
        "EC|movistar ecuador" to "file:///android_asset/operator_logos/ec_movistar_ecuador.png",
        "EC|cnt ecuador" to "file:///android_asset/operator_logos/ec_cnt_ecuador.png",
        "VE|movistar venezuela" to "file:///android_asset/operator_logos/ve_movistar_venezuela.png",
        "VE|digitel" to "file:///android_asset/operator_logos/ve_digitel.png",
        "CR|ice costa rica" to "file:///android_asset/operator_logos/cr_ice_costa_rica.png",
        "CR|claro costa rica" to "file:///android_asset/operator_logos/cr_claro_costa_rica.png",
        "PA|cable & wireless panama" to "file:///android_asset/operator_logos/pa_cable___wireless_panama.png",
        "PA|claro panama" to "file:///android_asset/operator_logos/pa_claro_panama.png",
        "PA|movistar panama" to "file:///android_asset/operator_logos/pa_movistar_panama.png",
        "GT|claro guatemala" to "file:///android_asset/operator_logos/gt_claro_guatemala.png",
        "GT|tigo guatemala" to "file:///android_asset/operator_logos/gt_tigo_guatemala.png",
        "DO|claro dominican republic" to "file:///android_asset/operator_logos/do_claro_dominican_republic.png",
        "DO|altice dominican republic" to "file:///android_asset/operator_logos/do_altice_dominican_republic.png",
        "DO|viva dominican republic" to "file:///android_asset/operator_logos/do_viva_dominican_republic.png",
        "JM|digicel jamaica" to "file:///android_asset/operator_logos/jm_digicel_jamaica.png",
        "JM|flow jamaica" to "file:///android_asset/operator_logos/jm_flow_jamaica.png",
        "AD|Andorra Telecom" to "file:///android_asset/operator_logos/ad_andorra_telecom.png",
        "AF|Afghan Wireless" to "file:///android_asset/operator_logos/af_afghan_wireless.png",
        "AO|Unitel" to "file:///android_asset/operator_logos/ao_unitel.png",
        "BB|Digicel" to "file:///android_asset/operator_logos/bb_digicel.png",
        "BS|BTC" to "file:///android_asset/operator_logos/bs_btc.png",
        "BS|redpcket" to "file:///android_asset/operator_logos/us_redpocket_mobile.png",
        "BT|TashiCell" to "file:///android_asset/operator_logos/bt_b_mobile.png",
        "BW|Mascom" to "file:///android_asset/operator_logos/bw_mascom.png",
        "BZ|Digi" to "file:///android_asset/operator_logos/bz_digi.png",
        "CD|Vodacom" to "file:///android_asset/operator_logos/cd_vodacom.png",
        "CG|MTN Congo" to "file:///android_asset/operator_logos/cg_mtn_congo.png",
        "CI|Orange" to "file:///android_asset/operator_logos/ci_orange.png",
        "CM|MTN Cameroon" to "file:///android_asset/operator_logos/cm_mtn_cameroon.png",
        "CU|Cubacel" to "file:///android_asset/operator_logos/cu_cubacel.png",
        "CY|Cyta" to "file:///android_asset/operator_logos/cy_cyta.png",
        "ET|Ethio Telecom" to "file:///android_asset/operator_logos/et_ethio_telecom.png",
        "FO|Føroya Tele" to "file:///android_asset/operator_logos/fo_foroya_tele.png",
        "GH|MTN" to "file:///android_asset/operator_logos/gh_mtn.png",
        "GI|Gibtelecom" to "file:///android_asset/operator_logos/gi_gibtelecom.png",
        "GU|GTA" to "file:///android_asset/operator_logos/gu_gta.png",
        "GY|Digicel" to "file:///android_asset/operator_logos/gy_digicel.png",
        "HN|Tigo" to "file:///android_asset/operator_logos/hn_tigo.png",
        "IQ|Zain" to "file:///android_asset/operator_logos/iq_zain.png",
        "IR|Irancell" to "file:///android_asset/operator_logos/ir_irancell.png",
        "KG|Beeline" to "file:///android_asset/operator_logos/kg_beeline.png",
        "LI|FL1" to "file:///android_asset/operator_logos/li_fl1.png",
        "LU|POST Luxembourg" to "file:///android_asset/operator_logos/lu_post_luxembourg.png",
        "LY|Libyana" to "file:///android_asset/operator_logos/ly_libyana.png",
        "MC|Monaco Telecom" to "file:///android_asset/operator_logos/mc_monaco_telecom.png",
        "MG|Telma" to "file:///android_asset/operator_logos/mg_telma.png",
        "MN|Mobicom" to "file:///android_asset/operator_logos/mn_mobicom.png",
        "MT|GO" to "file:///android_asset/operator_logos/mt_go.png",
        "MU|my.t" to "file:///android_asset/operator_logos/mu_myt.png",
        "MZ|Vodacom" to "file:///android_asset/operator_logos/mz_vodacom.png",
        "NA|MTC" to "file:///android_asset/operator_logos/na_mtc.png",
        "NC|OPT-NC" to "file:///android_asset/operator_logos/nc_opt_nc.png",
        "NI|Claro" to "file:///android_asset/operator_logos/ni_claro.png",
        "PF|Vini" to "file:///android_asset/operator_logos/pf_vini.png",
        "PG|Digicel" to "file:///android_asset/operator_logos/pg_digicel.png",
        "PS|Jawwal" to "file:///android_asset/operator_logos/ps_jawwal.png",
        "RE|SFR" to "file:///android_asset/operator_logos/re_sfr.png",
        "RW|MTN" to "file:///android_asset/operator_logos/rw_mtn.png",
        "SB|Our Telekom" to "file:///android_asset/operator_logos/sb_our_telekom.png",
        "SD|Zain" to "file:///android_asset/operator_logos/sd_zain.png",
        "SM|SMT" to "file:///android_asset/operator_logos/sm_smt.png",
        "SN|Orange" to "file:///android_asset/operator_logos/sn_orange.png",
        "SR|Telesur" to "file:///android_asset/operator_logos/sr_telesur.png",
        "SV|Tigo" to "file:///android_asset/operator_logos/sv_tigo.png",
        "SY|Syriatel" to "file:///android_asset/operator_logos/sy_syriatel.png",
        "TJ|Tcell" to "file:///android_asset/operator_logos/tj_tcell.png",
        "TL|Timor Telecom" to "file:///android_asset/operator_logos/tl_timor_telecom.png",
        "TM|TM Cell" to "file:///android_asset/operator_logos/tm_tm_cell.png",
        "TO|Digicel" to "file:///android_asset/operator_logos/to_digicel.png",
        "TT|TSTT" to "file:///android_asset/operator_logos/tt_tstt.png",
        "TZ|Vodacom" to "file:///android_asset/operator_logos/tz_vodacom.png",
        "UG|MTN" to "file:///android_asset/operator_logos/ug_mtn.png",
        "UZ|Ucell" to "file:///android_asset/operator_logos/uz_ucell.png",
        "VU|Vodafone" to "file:///android_asset/operator_logos/vu_vodafone.png",
        "WS|Digicel" to "file:///android_asset/operator_logos/ws_digicel.png",
        "XK|Vala" to "file:///android_asset/operator_logos/xk_vala.png",
        "YE|Yemen Mobile" to "file:///android_asset/operator_logos/ye_yemen_mobile.png",
        "ZM|MTN" to "file:///android_asset/operator_logos/zm_mtn.png",
        "ZW|Econet" to "file:///android_asset/operator_logos/zw_econet.png",
    )
    fun assetFor(name:String, iso:String?=null):String {
        val q=name.trim().lowercase()
        if(q.isBlank()) return ""
        fun norm(x:String)=x.lowercase().replace("&","and").replace("+","plus").replace(Regex("[^a-z0-9一-龥]+"),"")
        val nq=norm(q)
        if(iso!=null) {
            val prefix="${iso.uppercase()}|"
            map[prefix+q]?.let{return it}
            map.entries.firstOrNull{ it.key.startsWith(prefix) && norm(it.key.substringAfter("|"))==nq }?.let{return it.value}
        }
        map.entries.firstOrNull{
            val k=it.key.substringAfter("|")
            it.key.endsWith("|$q") || norm(k)==nq || nq.contains(norm(k)) || norm(k).contains(nq)
        }?.let{return it.value}
        return ""
    }
}

@Composable fun AppBackground(settings:App设置){
    if(settings.backgroundUri.isNotBlank()){
        AsyncImage(model=settings.backgroundUri,contentDescription=null,contentScale=ContentScale.Crop,modifier=Modifier.fillMaxSize().background(Color(0xFFF4F5F7)))
        Box(Modifier.fillMaxSize().background((if(settings.dark) Color.Black else Color.White).copy(alpha=(1f-settings.backgroundAlpha).coerceIn(.18f,.82f))))
    }else Box(Modifier.fillMaxSize().background(if(settings.dark) Color(0xFF0B0F17) else Color(0xFFF4F5F7)))
}

@Composable fun Home(ctx:Context,records:List<PhoneNumberRecord>,settings:App设置,search:String,filter:String,sortMode:String,on筛选:(String)->Unit,on排序:()->Unit,onAdd:()->Unit,on编辑:(PhoneNumberRecord)->Unit,onDel:(PhoneNumberRecord)->Unit,onDial:(PhoneNumberRecord)->Unit,onTraffic:(PhoneNumberRecord)->Unit,onKeep:(PhoneNumberRecord,Int)->Unit){
    val today=LocalDate.now()
    fun daysOf(r:PhoneNumberRecord)=runCatching{LocalDate.parse(r.expireDate).toEpochDay()-today.toEpochDay()}.getOrNull()
    val q=search.trim().lowercase()
    val filtered=records.filter{ r->
        val d=daysOf(r)
        val ok=when(filter){"正常"->d!=null && d>settings.remind天;"即将到期"->d!=null && d in 0..settings.remind天;"已过期"->d!=null && d<0;else->true}
        ok && (q.isEmpty() || (r.number+r.operator+r.countryName+r.countryCode+r.note).lowercase().contains(q))
    }
    val shown=if(sortMode=="到期远") filtered.sortedByDescending{ daysOf(it) ?: Long.MIN_VALUE } else filtered.sortedBy{ daysOf(it) ?: Long.MAX_VALUE }
    Box(Modifier.fillMaxSize()){
        AppBackground(settings)
        LazyColumn(Modifier.fillMaxSize().padding(horizontal=22.dp),verticalArrangement=Arrangement.spacedBy(9.dp)){
            item{ FilterToolRow(filter,sortMode,on筛选,on排序,shown.size) }
            if(shown.isEmpty()) item{ Box(Modifier.fillMaxWidth().height(260.dp),contentAlignment=Alignment.Center){ Column(horizontalAlignment=Alignment.CenterHorizontally){Text(L("暂无号码"),fontSize=18.sp,fontWeight=FontWeight.SemiBold);Text(L("点击右下角添加号码"),fontSize=13.sp,color=Color(0xFF8E8E93))} } }
            else items(shown,key={it.id}){ r-> CompactSimCard(r,on编辑,onDel,onTraffic,onKeep,daysOf(r),settings.remind天,settings.showFlag,settings.dark) }
            item{ Spacer(Modifier.height(90.dp)) }
        }
        Box(Modifier.align(Alignment.BottomEnd).padding(end=20.dp,bottom=86.dp).size(56.dp)){
            FloatingActionButton(onClick=onAdd,containerColor=Color(0xFF3B82F6),contentColor=Color.White,shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxSize()){Text("＋",fontSize=27.sp,fontWeight=FontWeight.Medium)}
        }
    }
}

@Composable fun SmallActionPill(text:String,color:Color=Color(0xFF007AFF),onClick:()->Unit){
    val source=remember{ MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if(pressed).96f else 1f,animationSpec=tween(120),label="pillPress")
    Text(text,fontSize=12.sp,fontWeight=FontWeight.SemiBold,color=color,modifier=Modifier.graphicsLayer(scaleX=scale,scaleY=scale).clip(RoundedCornerShape(999.dp)).background(color.copy(alpha=.08f)).clickable(interactionSource=source,indication=null){onClick()}.padding(horizontal=9.dp,vertical=5.dp))
}

@Composable fun TinyActionButton(text:String,color:Color=Color(0xFF007AFF),onClick:()->Unit){ SmallActionPill(text,color,onClick) }

@OptIn(ExperimentalLayoutApi::class)
@Composable fun DefaultNumberCard(r:PhoneNumberRecord,on编辑:(PhoneNumberRecord)->Unit,onDel:(PhoneNumberRecord)->Unit,onDial:(PhoneNumberRecord)->Unit,onTraffic:(PhoneNumberRecord)->Unit,onKeep:(PhoneNumberRecord,Int)->Unit){
    val today=LocalDate.now(); val days=runCatching{LocalDate.parse(r.expireDate).toEpochDay()-today.toEpochDay()}.getOrNull()
    val color=when{days==null->Color(0xFF8A94A6);days<0->Color(0xFFFF3B30);days<=7->Color(0xFFFF9500);else->Color(0xFF34C759)}
    var confirmDelete by remember{ mutableStateOf(false) }
    var keepDlg by remember{ mutableStateOf(false) }
    Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Color(0xF7FFFFFF)),elevation=CardDefaults.cardElevation(defaultElevation=6.dp),modifier=Modifier.fillMaxWidth().padding(horizontal=4.dp,vertical=2.dp).border(1.dp,Color.White.copy(alpha=.75f),RoundedCornerShape(24.dp))){
        Column(Modifier.padding(horizontal=16.dp,vertical=14.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){
            Row(verticalAlignment=Alignment.CenterVertically){
                Box(Modifier.size(46.dp).clip(RoundedCornerShape(15.dp)).background(Color.White),contentAlignment=Alignment.Center){Text(r.flag,fontSize=27.sp)}
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)){
                    Text(L("默认保号号码"),fontSize=11.sp,color=Color(0xFF007AFF),fontWeight=FontWeight.Bold)
                    Text(r.operator.ifBlank{r.countryName},fontSize=18.sp,fontWeight=FontWeight.Bold,color=Color(0xFF111827),maxLines=1,overflow=TextOverflow.Ellipsis)
                    Text("${r.countryCode} ${maskNumber(formatNumber(r.number))}",fontSize=13.sp,color=Color(0xFF4B5563))
                    Text(L("到期")+"：${r.expireDate} · "+expireText(LocalAppLanguage.current,days),fontSize=12.sp,color=color)
                    Text(L("备注")+"：${r.note.ifBlank{L("预付费 / 保号套餐")}}",fontSize=10.sp,color=Color(0xFF6B7280),maxLines=1,overflow=TextOverflow.Ellipsis)
                }
                Box(Modifier.size(36.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFF007AFF)).clickable{on编辑(r)},contentAlignment=Alignment.Center){Text("›",color=Color.White,fontSize=25.sp)}
            }
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(6.dp),modifier=Modifier.fillMaxWidth()){
                SmallActionPill(L("保号"),Color(0xFF007AFF)){keepDlg=true}
                SmallActionPill(L("刷流量"),Color(0xFF007AFF)){onTraffic(r)}
                SmallActionPill(L("删除"),Color(0xFFFF3B30)){confirmDelete=true}
            }
        }
    }
    if(keepDlg) KeepCycleDialog(r,onKeep){keepDlg=false}
    if(confirmDelete) IOSConfirmDialog(L("删除号码？"),L("删除")+" ${r.countryCode} ${formatNumber(r.number)} "+L("删除后不可恢复"),true,{confirmDelete=false},{confirmDelete=false;onDel(r)})
}


@Composable fun OperatorLogo(name:String){
    val op=name.uppercase()
    val label=when{
        "移动" in name || "CHINA MOBILE" in op -> "CM"
        "联通" in name || "UNICOM" in op -> "CU"
        "电信" in name || "TELECOM" in op -> "CT"
        "广电" in name -> "CB"
        "US MOBILE" in op -> "USM"
        "3HK" in op || "THREE" in op -> "3"
        "HKT" in op || "CSL" in op -> "HKT"
        "SMARTONE" in op -> "ST"
        "CMHK" in op -> "CMHK"
        "CTM" in op -> "CTM"
        "RAKUTEN" in op -> "R"
        "SOFTBANK" in op -> "SB"
        "DOCOMO" in op -> "doc"
        "AIS" in op -> "AIS"
        "TRUE" in op -> "TRUE"
        "DTAC" in op -> "dtac"
        "VODAFONE" in op -> "V"
        "T-MOBILE" in op -> "T"
        "AT&T" in op -> "AT&T"
        "VERIZON" in op -> "VZ"
        else -> name.take(3).ifBlank{"SIM"}
    }
    val bg=when(label){"CM"->Color(0xFF22C55E);"CU"->Color(0xFFE11D48);"CT"->Color(0xFF2563EB);"AIS"->Color(0xFF16A34A);"R"->Color(0xFFE91E63);"V"->Color(0xFFE60000);"USM"->Color(0xFF2563EB);"3"->Color(0xFF7C3AED);else->Color(0xFF111827)}
    Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(bg),contentAlignment=Alignment.Center){Text(label,fontSize=if(label.length>3) 9.sp else 14.sp,fontWeight=FontWeight.Bold,color=Color.White,maxLines=1)}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable fun NumberListRow(r:PhoneNumberRecord,on编辑:(PhoneNumberRecord)->Unit,onDel:(PhoneNumberRecord)->Unit,onTraffic:(PhoneNumberRecord)->Unit,onKeep:(PhoneNumberRecord,Int)->Unit,days:Long?){
    val status=if(days==null)L("未知") else if(days<0)L("已过期") else if(days<=7)L("即将到期") else L("正常")
    val statusColor=when{days!=null && days<0->Color(0xFFFF3B30);days!=null && days<=7->Color(0xFFFF9500);else->Color(0xFF007AFF)}
    var confirmDelete by remember{ mutableStateOf(false) }
    var keepDlg by remember{ mutableStateOf(false) }
    Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=Color.White),elevation=CardDefaults.cardElevation(5.dp),modifier=Modifier.fillMaxWidth()){
        Column(Modifier.padding(horizontal=14.dp,vertical=12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
            Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.clickable{on编辑(r)}){
                Box(Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF1F5FA)),contentAlignment=Alignment.Center){Text(r.flag,fontSize=25.sp)}
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(2.dp)){
                    Text(r.operator.ifBlank{r.countryName},fontSize=16.sp,fontWeight=FontWeight.Bold,maxLines=1,overflow=TextOverflow.Ellipsis)
                    Text("${r.countryCode} ${maskNumber(formatNumber(r.number))}",fontSize=12.sp,color=Color(0xFF6B7280),maxLines=1,overflow=TextOverflow.Ellipsis)
                    Text(L("到期")+"：${r.expireDate} · "+expireText(LocalAppLanguage.current,days),fontSize=12.sp,color=statusColor,maxLines=1,overflow=TextOverflow.Ellipsis)
                }
                Text(status,fontSize=10.sp,color=statusColor,modifier=Modifier.clip(RoundedCornerShape(8.dp)).background(statusColor.copy(alpha=.10f)).padding(horizontal=7.dp,vertical=4.dp))
                Spacer(Modifier.width(6.dp)); Text("›",fontSize=22.sp,color=Color(0xFF9CA3AF))
            }
            FlowRow(horizontalArrangement=Arrangement.spacedBy(7.dp),verticalArrangement=Arrangement.spacedBy(5.dp)){
                TinyActionButton(L("保号")){keepDlg=true}
                TinyActionButton(L("刷流量")){onTraffic(r)}
                TinyActionButton(L("删除"),Color(0xFFFF3B30)){confirmDelete=true}
            }
        }
    }
    if(keepDlg) KeepCycleDialog(r,onKeep){keepDlg=false}
    if(confirmDelete) IOSConfirmDialog(L("删除号码？"),L("删除")+" ${r.countryCode} ${formatNumber(r.number)} "+L("删除后不可恢复"),true,{confirmDelete=false},{confirmDelete=false;onDel(r)})
}

@OptIn(ExperimentalLayoutApi::class)
@Composable fun KeepCycleDialog(r:PhoneNumberRecord,onKeep:(PhoneNumberRecord,Int)->Unit,onDismiss:()->Unit){
    var days by remember{ mutableStateOf(30) }
    Dialog(onDismissRequest=onDismiss){
        Surface(shape=RoundedCornerShape(30.dp),color=Color(0xFFF2F3F7),modifier=Modifier.fillMaxWidth(.92f).widthIn(max=360.dp)){
            Column(Modifier.fillMaxWidth().padding(horizontal=22.dp,vertical=24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(22.dp)){
                Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.fillMaxWidth(),verticalArrangement=Arrangement.spacedBy(8.dp)){
                    Text(L("延长保号"),fontSize=22.sp,fontWeight=FontWeight.Bold,color=Color(0xFF111827))
                    Text("${r.countryCode} ${formatNumber(r.number)}",fontSize=15.sp,color=Color(0xFF8A94A6),maxLines=1,overflow=TextOverflow.Ellipsis)
                }
                Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(12.dp)){
                    Text(L("选择周期"),fontSize=13.sp,color=Color(0xFF8A94A6),modifier=Modifier.fillMaxWidth(),textAlign=TextAlign.Center)
                    FlowRow(modifier=Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),verticalArrangement=Arrangement.spacedBy(12.dp)){
                        listOf(7 to "7",15 to "15",30 to "30",90 to "90",180 to "180",365 to "365").forEach{(d,label)-> IOSChip(label,days==d,Modifier.width(68.dp).height(44.dp)){days=d} }
                    }
                }
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(14.dp)){
                    Button(onClick=onDismiss,modifier=Modifier.weight(1f).height(54.dp),shape=RoundedCornerShape(18.dp),colors=ButtonDefaults.buttonColors(containerColor=Color.White,contentColor=Color(0xFF374151))){Text(L("取消"),fontSize=16.sp)}
                    Button(onClick={onKeep(r,days);onDismiss()},modifier=Modifier.weight(1f).height(54.dp),shape=RoundedCornerShape(18.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF007AFF),contentColor=Color.White)){Text(L("确认延长"),fontSize=16.sp,fontWeight=FontWeight.SemiBold)}
                }
            }
        }
    }
}

@Composable fun KeepPage(records:List<PhoneNumberRecord>,onKeep:(PhoneNumberRecord,Int)->Unit){
    var selectedId by remember{ mutableStateOf(records.firstOrNull()?.id ?: "") }; var months by remember{ mutableStateOf(30) }
    val r=records.firstOrNull{it.id==selectedId} ?: records.firstOrNull()
    Column(Modifier.fillMaxSize().background(Color(0xFFF2F3F7)).padding(20.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
        if(r==null){Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Text(L("暂无号码"))}} else {
            IOSSection(L("选择号码")){ records.forEach{ item-> KeepChoice(item.operator.ifBlank{item.countryName}+"  "+item.countryCode+" "+maskNumber(formatNumber(item.number)), selectedId==item.id){selectedId=item.id} } }
            IOSSection(L("选择保号周期")){ listOf(7 to "7",15 to "15",30 to "30",90 to "90",180 to "180",365 to "365").forEach{(m,label)-> KeepChoice(label, months==m){months=m} } }
            Button(onClick={onKeep(r,months)},modifier=Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(16.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF007AFF))){Text(L("确认延长"))}
        }
    }
}
@Composable fun KeepChoice(text:String,selected:Boolean,onClick:()->Unit){ Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(if(selected) dk(Color(0xFF1C2333),Color(0xFFEAF3FF)) else dk(Color(0xFF1C1C1E),Color.White)).clickable{onClick()}.padding(14.dp),verticalAlignment=Alignment.CenterVertically){Text(if(selected)"●" else "○",color=Color(0xFF007AFF));Spacer(Modifier.width(10.dp));Text(text,fontSize=16.sp,fontWeight=if(selected)FontWeight.Bold else FontWeight.Normal)} }


@Composable fun ToolsPage(ctx:Context,settings:App设置,records:List<PhoneNumberRecord>,onTraffic:(PhoneNumberRecord)->Unit,onDial:(PhoneNumberRecord)->Unit,onExportJson:()->Unit,onExportCsv:()->Unit,onImportText:(String)->Unit){
    var pickTraffic by remember{ mutableStateOf(false) }
    var pickDial by remember{ mutableStateOf(false) }
    var importDlg by remember{ mutableStateOf(false) }
    var importText by remember{ mutableStateOf("") }
    Box(Modifier.fillMaxSize()){
        AppBackground(settings)
        LazyColumn(Modifier.fillMaxSize().padding(horizontal=18.dp,vertical=16.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
            item{
                IOSSection(L("常用工具")){
                    ToolRow("traffic",L("刷流量"),L("选择一个号码执行真实下载流量测试")){ pickTraffic=true }
                    ToolRow("dial",L("拨号测试"),L("选择号码并打开系统拨号器")){ pickDial=true }
                    ToolRow("export_json",L("导出 JSON"),L("生成完整 JSON 备份文本")){ onExportJson() }
                    ToolRow("export_csv",L("导出 CSV"),L("生成 CSV 表格文本")){ onExportCsv() }
                    ToolRow("import",L("导入数据"),L("粘贴 JSON 或 CSV 恢复号码列表")){ importDlg=true }
                }
            }
        }
    }
    if(pickTraffic) NumberPickerDialog(L("选择刷流量号码"),records,{pickTraffic=false}){ pickTraffic=false; onTraffic(it) }
    if(pickDial) NumberPickerDialog(L("选择拨号号码"),records,{pickDial=false}){ pickDial=false; onDial(it) }
    if(importDlg) IOSImportDialog(importText,{importText=it},{importDlg=false},{onImportText(importText);importDlg=false},ctx)
}

@Composable fun ToolRow(iconType:String,title:String,sub:String,onClick:()->Unit){
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable{onClick()}.padding(horizontal=6.dp,vertical=4.dp),verticalAlignment=Alignment.CenterVertically){
        Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(dk(Color(0xFF1C2333),Color(0xFFF2F6FF))),contentAlignment=Alignment.Center){ ToolLineIcon(iconType,Color(0xFF007AFF)) }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)){Text(title,fontSize=16.sp,fontWeight=FontWeight.SemiBold,color=dk(Color(0xFFE5E5E7),Color(0xFF111827)));Text(sub,fontSize=12.sp,color=Color(0xFF8A94A6),maxLines=1,overflow=TextOverflow.Ellipsis)}
        Text("›",fontSize=24.sp,color=dk(Color(0xFF48484A),Color(0xFFC7C7CC)))
    }
}

@Composable fun ToolLineIcon(type:String,color:Color){
    Canvas(Modifier.size(22.dp)){
        val w=size.width; val h=size.height
        val stroke=Stroke(width=2.1f)
        when(type){
            "traffic"->{
                drawRoundRect(color,topLeft=Offset(w*.18f,h*.22f),size=Size(w*.64f,h*.56f),cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.08f,w*.08f),style=stroke)
                drawLine(color,Offset(w*.30f,h*.62f),Offset(w*.30f,h*.44f),strokeWidth=2.1f)
                drawLine(color,Offset(w*.50f,h*.62f),Offset(w*.50f,h*.34f),strokeWidth=2.1f)
                drawLine(color,Offset(w*.70f,h*.62f),Offset(w*.70f,h*.50f),strokeWidth=2.1f)
            }
            "dial"->{
                drawLine(color,Offset(w*.34f,h*.26f),Offset(w*.66f,h*.26f),strokeWidth=2.1f)
                drawArc(color,180f,180f,false,topLeft=Offset(w*.20f,h*.18f),size=Size(w*.60f,h*.42f),style=stroke)
                drawLine(color,Offset(w*.28f,h*.66f),Offset(w*.40f,h*.54f),strokeWidth=2.1f)
                drawLine(color,Offset(w*.72f,h*.66f),Offset(w*.60f,h*.54f),strokeWidth=2.1f)
            }
            "export_json"->{
                drawRoundRect(color,topLeft=Offset(w*.24f,h*.18f),size=Size(w*.52f,h*.60f),cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.07f,w*.07f),style=stroke)
                drawLine(color,Offset(w*.50f,h*.28f),Offset(w*.50f,h*.58f),strokeWidth=2.1f)
                drawLine(color,Offset(w*.40f,h*.48f),Offset(w*.50f,h*.58f),strokeWidth=2.1f)
                drawLine(color,Offset(w*.60f,h*.48f),Offset(w*.50f,h*.58f),strokeWidth=2.1f)
            }
            "export_csv"->{
                drawRoundRect(color,topLeft=Offset(w*.24f,h*.18f),size=Size(w*.52f,h*.60f),cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.07f,w*.07f),style=stroke)
                drawLine(color,Offset(w*.34f,h*.34f),Offset(w*.66f,h*.34f),strokeWidth=2.0f)
                drawLine(color,Offset(w*.34f,h*.46f),Offset(w*.66f,h*.46f),strokeWidth=2.0f)
                drawLine(color,Offset(w*.34f,h*.58f),Offset(w*.58f,h*.58f),strokeWidth=2.0f)
            }
            else->{
                drawRoundRect(color,topLeft=Offset(w*.24f,h*.18f),size=Size(w*.52f,h*.60f),cornerRadius=androidx.compose.ui.geometry.CornerRadius(w*.07f,w*.07f),style=stroke)
                drawLine(color,Offset(w*.50f,h*.58f),Offset(w*.50f,h*.28f),strokeWidth=2.1f)
                drawLine(color,Offset(w*.40f,h*.38f),Offset(w*.50f,h*.28f),strokeWidth=2.1f)
                drawLine(color,Offset(w*.60f,h*.38f),Offset(w*.50f,h*.28f),strokeWidth=2.1f)
            }
        }
    }
}

@Composable fun NumberPickerDialog(title:String,records:List<PhoneNumberRecord>,onDismiss:()->Unit,onPick:(PhoneNumberRecord)->Unit){
    Dialog(onDismissRequest=onDismiss){
        Surface(shape=RoundedCornerShape(26.dp),color=dk(Color(0xFF1C1C1E),Color(0xFFF2F3F7)),modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    Text(title,fontSize=20.sp,fontWeight=FontWeight.Bold,color=dk(Color(0xFFE5E5E7),Color(0xFF111827)),modifier=Modifier.weight(1f))
                    TextButton(onDismiss){Text(L("取消"),color=Color(0xFF007AFF))}
                }
                if(records.isEmpty()) Box(Modifier.fillMaxWidth().height(120.dp),contentAlignment=Alignment.Center){Text(L("暂无号码，请先添加号码。"),color=Color(0xFF8A94A6))}
                else LazyColumn(Modifier.heightIn(max=420.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
                    items(records){ r ->
                        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(dk(Color(0xFF1C1C1E),Color.White)).clickable{onPick(r)}.padding(13.dp),verticalAlignment=Alignment.CenterVertically){
                            Text(r.flag,fontSize=25.sp); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)){Text(r.operator.ifBlank{r.countryName},fontWeight=FontWeight.SemiBold,color=dk(Color(0xFFE5E5E7),Color(0xFF111827))); Text("${r.countryCode} ${maskNumber(formatNumber(r.number))}",fontSize=12.sp,color=Color(0xFF6B7280))}; Text("›",fontSize=24.sp,color=dk(Color(0xFF48484A),Color(0xFFC7C7CC)))
                        }
                    }
                }
            }
        }
    }
}

@Composable fun IOSImportDialog(value:String,onValue:(String)->Unit,onDismiss:()->Unit,onImport:()->Unit,ctx:Context){
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri!=null){
            val text = runCatching{ ctx.contentResolver.openInputStream(uri)?.bufferedReader()?.use{it.readText()} }?.getOrNull()
            if(!text.isNullOrBlank()) onValue(text)
        }
    }
    Dialog(onDismissRequest=onDismiss){
        Surface(shape=RoundedCornerShape(26.dp),color=dk(Color(0xFF1C1C1E),Color(0xFFF2F3F7)),modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
                Column(horizontalAlignment=Alignment.CenterHorizontally,modifier=Modifier.fillMaxWidth()){
                    Text(L("导入数据"),fontSize=20.sp,fontWeight=FontWeight.Bold,color=dk(Color(0xFFE5E5E7),Color(0xFF111827)))
                    Text(L("支持 JSON / CSV，JSON 可同时恢复号码和配置。导入前建议先导出备份。"),fontSize=13.sp,color=Color(0xFF8A94A6),lineHeight=18.sp)
                }
                Button(onClick={filePicker.launch("*/*")},modifier=Modifier.fillMaxWidth().height(44.dp),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF34C759),contentColor=Color.White)){Text(L("从文件导入"))}
                TextField(value=value,onValueChange=onValue,modifier=Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(18.dp)),placeholder={Text(L("或粘贴 JSON / CSV 数据"))},minLines=5,colors=TextFieldDefaults.colors(focusedContainerColor=dk(Color(0xFF2C2C2E),Color.White),unfocusedContainerColor=dk(Color(0xFF2C2C2E),Color.White),focusedIndicatorColor=Color.Transparent,unfocusedIndicatorColor=Color.Transparent))
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){
                    Button(onClick=onDismiss,modifier=Modifier.weight(1f).height(48.dp),shape=RoundedCornerShape(16.dp),colors=ButtonDefaults.buttonColors(containerColor=dk(Color(0xFF2C2C2E),Color.White),contentColor=dk(Color(0xFFD1D5DB),Color(0xFF374151)))){Text(L("取消"))}
                    Button(onClick=onImport,modifier=Modifier.weight(1f).height(48.dp),shape=RoundedCornerShape(16.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF007AFF),contentColor=Color.White)){Text(L("导入"))}
                }
            }
        }
    }
}

@Composable fun SimHubStat(t:String,v:String,c:Color,m:Modifier){ Card(m,shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),elevation=CardDefaults.cardElevation(1.5.dp)){Column(Modifier.padding(vertical=12.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(v,fontSize=22.sp,fontWeight=FontWeight.Bold,color=c);Text(t,fontSize=12.sp,color=dk(Color(0xFF8E8E93),Color(0xFF8A94A6)))}} }

@Composable fun SoftStat(t:String,v:String,c:Color,m:Modifier){ SimHubStat(t,v,c,m) }

@Composable fun QQStat(t:String,v:String,c:Color,m:Modifier){ SimHubStat(t,v,c,m) }

@Composable fun Stat(t:String,v:String,m:Modifier){ SimHubStat(t,v,Color(0xFF007AFF),m) }
@Composable fun NumberCard(r:PhoneNumberRecord,on编辑:(PhoneNumberRecord)->Unit,onDel:(PhoneNumberRecord)->Unit,onDial:(PhoneNumberRecord)->Unit,onTraffic:(PhoneNumberRecord)->Unit,onKeep:(PhoneNumberRecord,Int)->Unit){ SimHubCard(r,on编辑,onDel,onDial,onTraffic,onKeep) }

@Composable fun SimHubCard(r:PhoneNumberRecord,on编辑:(PhoneNumberRecord)->Unit,onDel:(PhoneNumberRecord)->Unit,onDial:(PhoneNumberRecord)->Unit,onTraffic:(PhoneNumberRecord)->Unit,onKeep:(PhoneNumberRecord,Int)->Unit){
    val exp=runCatching{LocalDate.parse(r.expireDate)}.getOrNull()
    val today=LocalDate.now()
    val days=exp?.toEpochDay()?.minus(today.toEpochDay())
    val progress=if(days==null) 0f else (days.coerceIn(0,90)/90f).coerceIn(0f,1f)
    val longTerm = days!=null && days>60
    var menu by remember{ mutableStateOf(false) }
    var confirm删除 by remember{ mutableStateOf(false) }
    Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=dk(Color(0xF81C1C1E),Color(0xF8FFFFFF))),elevation=CardDefaults.cardElevation(defaultElevation=6.dp),modifier=Modifier.fillMaxWidth().padding(vertical=2.dp).border(1.dp,dk(Color(0xFF2C2C2E).copy(alpha=.70f),Color.White.copy(alpha=.70f)),RoundedCornerShape(24.dp))){
        Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(9.dp)){
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.Top){
                Box(Modifier.size(48.dp).clip(RoundedCornerShape(16.dp)).background(dk(Color(0xFF2C2C2E),Color(0xFFF1F5FA))),contentAlignment=Alignment.Center){Text(r.flag,fontSize=25.sp)}
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(3.dp)){
                    Row(verticalAlignment=Alignment.CenterVertically){
                        Text(r.operator.ifBlank{r.countryName},fontSize=18.sp,fontWeight=FontWeight.Bold,color=dk(Color(0xFFE5E5E7),Color(0xFF111827)),maxLines=1,overflow=TextOverflow.Ellipsis,modifier=Modifier.weight(1f,false))
                        if(longTerm){ Spacer(Modifier.width(6.dp)); Text(L("长期号码"),fontSize=11.sp,color=Color.White,fontWeight=FontWeight.Bold,modifier=Modifier.clip(RoundedCornerShape(8.dp)).background(Color(0xFF007AFF)).padding(horizontal=6.dp,vertical=2.dp)) }
                    }
                    Row(verticalAlignment=Alignment.CenterVertically){Text(if(r.note.isBlank()) L("预付费") else r.note,fontSize=12.sp,color=Color(0xFF6B7280),maxLines=1,overflow=TextOverflow.Ellipsis); Spacer(Modifier.width(7.dp)); Text(if(days==null) "无到期日" else if(days<0) "❌ "+expireText(LocalAppLanguage.current,days) else "✅ ${r.expireDate} · "+expireText(LocalAppLanguage.current,days),fontSize=13.sp,color=if(days!=null&&days<0) Color(0xFFFF3B30) else Color(0xFF34C759),maxLines=1,overflow=TextOverflow.Ellipsis)}
                }
            }
            LinearProgressIndicator(progress={progress},modifier=Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(5.dp)),color=Color(0xFF34C759),trackColor=dk(Color(0xFF2C2C2E),Color(0xFFE9EDF3)))
            Row(verticalAlignment=Alignment.CenterVertically){Text("☎",fontSize=16.sp); Spacer(Modifier.width(7.dp)); Text("${r.countryCode} ${maskNumber(formatNumber(r.number))}",fontSize=13.sp,color=Color(0xFF4B5563),modifier=Modifier); Text("👁",fontSize=15.sp)}
            Row(verticalAlignment=Alignment.CenterVertically){Text("#",fontSize=14.sp,fontWeight=FontWeight.Bold); Spacer(Modifier.width(7.dp)); Text("EID: ${fakeEidForCard(r)}",fontSize=12.sp,color=dk(Color(0xFF8E8E93),Color(0xFF6B7280)),maxLines=1,overflow=TextOverflow.Ellipsis)}
            TextButton(onClick={menu=!menu},contentPadding=PaddingValues(0.dp)){Text(if(menu) L("隐藏详情") else "⌄ "+L("显示二维码"),color=Color(0xFF007AFF),fontSize=14.sp)}
        }
    }
    if(menu){
        Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=dk(Color(0xFF1C1C1E),Color.White)),elevation=CardDefaults.cardElevation(0.dp),modifier=Modifier.fillMaxWidth().padding(top=8.dp)){
            Column{
                MenuRow("✎",L("编辑"),Color(0xFF374151)){on编辑(r)}
                Divider(color=dk(Color(0xFF2C2C2E),Color(0xFFE5E7EB)),modifier=Modifier.padding(start=48.dp))
                MenuRow("⧉",L("复制号码"),Color(0xFF374151)){onDial(r)}
                Divider(color=dk(Color(0xFF2C2C2E),Color(0xFFE5E7EB)),modifier=Modifier.padding(start=48.dp))
                MenuRow("🗑",L("删除"),Color(0xFFFF3B30)){confirm删除=true}
            }
        }
    }
    if(confirm删除) IOSConfirmDialog(L("删除号码？"),L("删除")+" ${r.countryCode} ${formatNumber(r.number)} "+L("删除后不可恢复"),true,{confirm删除=false},{confirm删除=false;onDel(r)})
}

@Composable fun MenuRow(icon:String,title:String,color:Color,onClick:()->Unit){ Row(Modifier.fillMaxWidth().clickable{onClick()}.padding(horizontal=16.dp,vertical=14.dp),verticalAlignment=Alignment.CenterVertically){Text(icon,fontSize=18.sp); Spacer(Modifier.width(10.dp)); Text(title,fontSize=16.sp,color=color,fontWeight=if(color==Color(0xFFFF3B30)) FontWeight.Bold else FontWeight.Normal)} }
fun maskNumber(n:String):String{ val ds=n.filter{it.isDigit()}; return if(ds.length<=4) n else ds.take(4)+" •••• "+ds.takeLast(4) }
fun fakeEidForCard(r:PhoneNumberRecord):String{ val seed=(r.id+r.number).hashCode().toString().filter{it.isDigit()}.padEnd(24,'0').take(24); return "89044000 ${seed.chunked(8).joinToString(" ")}" }


@OptIn(ExperimentalLayoutApi::class)
@Composable fun Full编辑Screen(init:PhoneNumberRecord,onDismiss:()->Unit,onSave:(PhoneNumberRecord)->Unit,onDelete:(PhoneNumberRecord)->Unit={}){
    BackHandler { onDismiss() }
    var r by remember { mutableStateOf(init) }
    var countryDlg by remember { mutableStateOf(false) }
    var qrText by remember { mutableStateOf("") }
    var qrDlg by remember { mutableStateOf(false) }
    var qrInput by remember { mutableStateOf("") }
    val editLang = LocalAppLanguage.current
    val albumLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri!=null) {
            qrText = tr(editLang,"已选择相册图片：")+"${uri}"
            r = r.copy(note = (r.note.ifBlank { tr(editLang,"预付费 / 保号套餐") }) + "\n"+tr(editLang,"二维码图片：")+"${uri}")
        }
    }
    Box(Modifier.fillMaxSize().background(dk(Color(0xFF0B0F17),Color(0xFFF2F3F7)))){
        Column(Modifier.fillMaxSize()){
            val editStatusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            Row(Modifier.fillMaxWidth().padding(start=18.dp,end=18.dp,top=editStatusBarTop+8.dp,bottom=12.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
                TextButton(onClick=onDismiss,modifier=Modifier.height(36.dp).clip(RoundedCornerShape(12.dp)).background(dk(Color(0xFF2C2C2E).copy(alpha=.90f),Color.White.copy(alpha=.90f))).border(.7.dp,dk(Color(0xFF38383A).copy(alpha=.75f),Color.White.copy(alpha=.75f)),RoundedCornerShape(12.dp)),contentPadding=PaddingValues(horizontal=12.dp,vertical=0.dp)){Text(L("取消"),color=Color(0xFF007AFF),fontWeight=FontWeight.SemiBold)}
                Text(if(init.number.isBlank()) L("新增 eSIM") else L("编辑 eSIM"),fontSize=19.sp,fontWeight=FontWeight.Bold,color=dk(Color(0xFFE5E5E7),Color(0xFF111827)))
                TextButton(onClick={onSave(r)},modifier=Modifier.height(36.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF007AFF)),contentPadding=PaddingValues(horizontal=14.dp,vertical=0.dp)){Text(L("完成"),fontWeight=FontWeight.Bold,color=Color.White)}
            }
            LazyColumn(Modifier.fillMaxSize().padding(horizontal=18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
                item{
                    SettingsSection(L("运营商与国家")){
                        IOSValueRow(L("国家/地区"),"${r.flag} ${r.countryName} ${r.countryCode}"){ countryDlg=true }
                        IOSDividerLine()
                        IOSField(L("运营商名称"),r.operator,{r=r.copy(operator=it)},L("如 AIS / Vodafone / 中国移动"))
                        val currentIso = remember(r.countryCode, r.countryName){ Countries.list.firstOrNull{it.code==r.countryCode && it.name==r.countryName}?.iso ?: Countries.list.firstOrNull{it.code==r.countryCode}?.iso ?: r.countryName }
                        val detectedOperator = remember(r.number, currentIso){ guessOperator(r.number,currentIso) }
                        val selectedOperator = r.operator.ifBlank { detectedOperator }
                        Text(L("留空时会按号码和国家自动识别。"),fontSize=11.sp,color=Color(0xFF8A94A6))
                        Text(L("当前识别")+"：${detectedOperator}",fontSize=11.sp,color=Color(0xFF8A94A6))
                        Text(L("当前选择")+"：${selectedOperator}",fontSize=11.sp,color=Color(0xFF007AFF),fontWeight=FontWeight.SemiBold)
                        val suggestions = remember(currentIso){ OperatorDatabase.byCountry(currentIso).take(8) }
                        if(suggestions.isNotEmpty()){
                            Text(L("推荐运营商"),fontSize=13.sp,fontWeight=FontWeight.SemiBold,color=Color(0xFF6B7280),modifier=Modifier.padding(top=6.dp))
                            FlowRow(horizontalArrangement=Arrangement.spacedBy(10.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
                                suggestions.forEach{ op ->
                                    val active = selectedOperator.equals(op.carrierName,true)
                                    IOSChip(op.carrierName,active){ r=r.copy(operator=op.carrierName) }
                                }
                            }
                        }
                    }
                }
                item{
                    SettingsSection(L("号码与套餐")){
                        IOSField(L("手机号码"),r.number,{r=r.copy(number=it.filter{c->c.isDigit()})},L("输入手机号码"))
                        IOSDividerLine()
                        IOSField(L("套餐余额"),r.balance,{r=r.copy(balance=it)},L("如 1 RMB / 4.50 USD / 2GB"))
                        IOSDividerLine()
                        IOSField(L("套餐备注"),r.note,{r=r.copy(note=it)},L("预付费 / 资费 / 套餐备注"),singleLine=false,minLines=2)
                        IOSDividerLine()
                        IOSField(L("信号状态"),r.signalStatus,{r=r.copy(signalStatus=it)},L("在线 / 离线 / 漫游 / 无服务"))
                    }
                }
                item{
                    SettingsSection(L("日期与周期")){
                        Text(L("开始日期"),fontSize=12.sp,color=Color(0xFF8A94A6)); DateOnlyEditor(r.startDate){r=r.copy(startDate=it)}
                        IOSDividerLine()
                        Text(L("到期日期"),fontSize=12.sp,color=Color(0xFF8A94A6)); DateOnlyEditor(r.expireDate){r=r.copy(expireDate=it)}
                        IOSDividerLine()
                        Text(L("套餐周期"),fontSize=12.sp,color=Color(0xFF8A94A6))
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)){ listOf(7,15,30).forEach{ d-> IOSChip(cycleText(LocalAppLanguage.current,d),r.cycleDays==d,Modifier.weight(1f)){ r=r.copy(cycleDays=d,expireDate=runCatching{LocalDate.parse(r.startDate).plusDays(d.toLong()).toString()}.getOrElse{LocalDate.now().plusDays(d.toLong()).toString()}) } } }
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)){ listOf(90,180,365).forEach{ d-> IOSChip(cycleText(LocalAppLanguage.current,d),r.cycleDays==d,Modifier.weight(1f)){ r=r.copy(cycleDays=d,expireDate=runCatching{LocalDate.parse(r.startDate).plusDays(d.toLong()).toString()}.getOrElse{LocalDate.now().plusDays(d.toLong()).toString()}) } } }
                        IOSDividerLine()
                        IOSSwitchRow(L("长期号码"),r.longTerm){r=r.copy(longTerm=it)}
                    }
                }
                item{
                    SettingsSection(L("eSIM 激活信息")){
                        IOSField(L("编辑 EID"),r.eid,{r=r.copy(eid=it)},L("输入 EID"))
                        IOSDividerLine()
                        IOSField("SM-DP+",r.smdp,{r=r.copy(smdp=it)},L("服务器地址"))
                        IOSDividerLine()
                        IOSField(L("激活码"),r.activationCode,{r=r.copy(activationCode=it)},"Activation Code")
                        IOSDividerLine()
                        Box(Modifier.fillMaxWidth().height(76.dp).clip(RoundedCornerShape(16.dp)).background(dk(Color(0xFF1C1C1E),Color(0xFFF4F6FA))).border(.7.dp,dk(Color(0xFF2C2C2E),Color(0xFFE5E7EB)),RoundedCornerShape(16.dp)).padding(12.dp)){
                            Text(qrText.ifBlank { L("未填写激活信息")+"\n"+L("可扫描/粘贴二维码内容，或从相册选择二维码图片") },color=Color(0xFF6B7280),fontSize=13.sp,maxLines=3,overflow=TextOverflow.Ellipsis)
                        }
                        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                            IOSChip(L("扫描二维码"),false,Modifier.weight(1f)){ qrDlg=true }
                            IOSChip(L("相册读取"),false,Modifier.weight(1f)){ albumLauncher.launch("image/*") }
                        }
                        Text(if(qrText.isBlank()) L("未填写激活信息") else "✅ "+L("激活信息已填写"),color=if(qrText.isBlank()) Color(0xFF8A94A6) else Color(0xFF34C759),fontSize=13.sp)
                    }
                }
                item{
                    SettingsSection(L("记录信息")){
                        IOSInfoRow(L("创建时间"),r.createdAt.ifBlank{LocalDate.now().toString()})
                        IOSDividerLine()
                        IOSInfoRow(L("激活时间"),r.activatedAt.ifBlank{L("未记录")})
                    }
                }
                item{ Spacer(Modifier.height(28.dp)) }
                item{
                    var showDel by remember{mutableStateOf(false)}
                    Button(onClick={showDel=true},modifier=Modifier.fillMaxWidth().height(50.dp),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFFF3B30)),contentPadding=PaddingValues(horizontal=16.dp)){
                        Text(L("删除"),fontSize=16.sp,fontWeight=FontWeight.SemiBold,color=Color.White)
                    }
                    if(showDel){
                        Dialog(onDismissRequest={showDel=false}){
                            Surface(shape=RoundedCornerShape(20.dp),color=Color.White){
                                Column(Modifier.padding(24.dp),verticalArrangement=Arrangement.spacedBy(16.dp),horizontalAlignment=Alignment.CenterHorizontally){
                                    Text(L("确认删除"),fontSize=20.sp,fontWeight=FontWeight.Bold,color=Color(0xFF111827))
                                    Text(L("删除后无法恢复，确定要删除这个号码吗？"),fontSize=14.sp,color=Color(0xFF6B7280))
                                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)){
                                        Button(onClick={showDel=false},modifier=Modifier.weight(1f).height(48.dp),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFF2F3F7))){Text(L("取消"),color=Color(0xFF007AFF),fontSize=16.sp)}
                                        Button(onClick={onDelete(r)},modifier=Modifier.weight(1f).height(48.dp),shape=RoundedCornerShape(14.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFFF3B30))){Text(L("删除"),color=Color.White,fontSize=16.sp,fontWeight=FontWeight.SemiBold)}
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if(qrDlg) IOSQrInputDialog(qrInput,{qrInput=it},{qrDlg=false}){
        qrText=qrInput.ifBlank{tr(editLang,"已手动触发扫码入口")}
        if(qrInput.isNotBlank()){
            val parts=parseLpa(qrInput)
            r=r.copy(smdp=parts.first.ifBlank{r.smdp},activationCode=parts.second.ifBlank{r.activationCode},note=(r.note.ifBlank{tr(editLang,"预付费 / 保号套餐")})+"\n"+tr(editLang,"二维码：")+qrInput)
        }
        qrDlg=false
    }
    if(countryDlg) CountryDialog({countryDlg=false}){c->r=r.copy(countryCode=c.code,countryName=c.name,flag=c.flag);countryDlg=false}
}

@Composable fun IOSDividerLine(){ Box(Modifier.fillMaxWidth().height(.7.dp).background(dk(Color(0xFF2C2C2E),Color(0xFFE5E7EB)))) }

@Composable fun IOSInfoRow(title:String,value:String){
    Row(Modifier.fillMaxWidth().padding(vertical=2.dp),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){
        Text(title,fontSize=15.sp,color=dk(Color(0xFFE5E5E7),Color(0xFF111827))); Text(value,fontSize=14.sp,color=Color(0xFF8A94A6),maxLines=1,overflow=TextOverflow.Ellipsis)
    }
}

@Composable fun IOSValueRow(title:String,value:String,onClick:()->Unit){
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable{onClick()}.padding(vertical=4.dp),verticalAlignment=Alignment.CenterVertically){
        Text(title,fontSize=15.sp,color=dk(Color(0xFFE5E5E7),Color(0xFF111827)),modifier=Modifier.width(92.dp)); Text(value,fontSize=15.sp,color=dk(Color(0xFFD1D5DB),Color(0xFF374151)),modifier=Modifier.weight(1f),maxLines=1,overflow=TextOverflow.Ellipsis); Text("›",fontSize=24.sp,color=dk(Color(0xFF48484A),Color(0xFFC7C7CC)))
    }
}

@Composable fun IOSField(label:String,value:String,onValue:(String)->Unit,placeholder:String,singleLine:Boolean=true,minLines:Int=1){
    Column(verticalArrangement=Arrangement.spacedBy(5.dp)){
        Text(label,fontSize=13.sp,color=Color(0xFF8A94A6),modifier=Modifier.padding(start=2.dp))
        TextField(value=value,onValueChange=onValue,modifier=Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),singleLine=singleLine,minLines=minLines,placeholder={Text(placeholder,fontSize=13.sp,color=dk(Color(0xFF636366),Color(0xFFB0B7C3)),maxLines=1,overflow=TextOverflow.Ellipsis)},colors=TextFieldDefaults.colors(focusedContainerColor=dk(Color(0xFF1C1C1E),Color(0xFFF7F8FA)),unfocusedContainerColor=dk(Color(0xFF1C1C1E),Color(0xFFF7F8FA)),focusedIndicatorColor=Color.Transparent,unfocusedIndicatorColor=Color.Transparent),textStyle=androidx.compose.ui.text.TextStyle(fontSize=15.sp,color=dk(Color(0xFFE5E5E7),Color(0xFF111827))))
    }
}

@Composable fun IOSQrInputDialog(value:String,onValue:(String)->Unit,onDismiss:()->Unit,onSave:()->Unit){
    Dialog(onDismissRequest=onDismiss){
        Surface(shape=RoundedCornerShape(26.dp),color=dk(Color(0xFF1C1C1E),Color(0xFFF2F3F7)),modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp),horizontalAlignment=Alignment.CenterHorizontally){
                Text(L("填写二维码内容"),fontSize=19.sp,fontWeight=FontWeight.Bold,color=dk(Color(0xFFE5E5E7),Color(0xFF111827)))
                Text(L("可粘贴 LPA、SM-DP+ 或激活码，保存后自动解析。"),fontSize=13.sp,color=Color(0xFF8A94A6),lineHeight=18.sp)
                TextField(value=value,onValueChange=onValue,modifier=Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(16.dp)),placeholder={Text(L("LPA:1\$SM-DP+\$激活码"))},minLines=5,colors=TextFieldDefaults.colors(focusedContainerColor=dk(Color(0xFF2C2C2E),Color.White),unfocusedContainerColor=dk(Color(0xFF2C2C2E),Color.White),focusedIndicatorColor=Color.Transparent,unfocusedIndicatorColor=Color.Transparent))
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){
                    Button(onClick=onDismiss,modifier=Modifier.weight(1f).height(46.dp),shape=RoundedCornerShape(15.dp),colors=ButtonDefaults.buttonColors(containerColor=dk(Color(0xFF2C2C2E),Color.White),contentColor=dk(Color(0xFFD1D5DB),Color(0xFF374151)))){Text(L("取消"))}
                    Button(onClick=onSave,modifier=Modifier.weight(1f).height(46.dp),shape=RoundedCornerShape(15.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF007AFF),contentColor=Color.White)){Text(L("保存"))}
                }
            }
        }
    }
}

@Composable fun CompactDateEditor(value:String,onChange:(String)->Unit){
    val parsed=runCatching{LocalDate.parse(value)}.getOrElse{LocalDate.now().plusDays(30)}
    var y by remember(value){ mutableStateOf(parsed.year.toString()) }
    var m by remember(value){ mutableStateOf(parsed.monthValue.toString().padStart(2,'0')) }
    var d by remember(value){ mutableStateOf(parsed.dayOfMonth.toString().padStart(2,'0')) }
    fun commit(){
        if(y.isBlank() || m.isBlank() || d.isBlank()) return
        val yy=(y.toIntOrNull() ?: parsed.year).coerceIn(1970,9999)
        val mm=(m.toIntOrNull() ?: parsed.monthValue).coerceIn(1,12)
        val maxDay=java.time.YearMonth.of(yy,mm).lengthOfMonth()
        val dd=(d.toIntOrNull() ?: parsed.dayOfMonth).coerceIn(1,maxDay)
        onChange(runCatching{LocalDate.of(yy,mm,dd)}.getOrElse{LocalDate.now().plusDays(30)}.toString())
    }
    Column(verticalArrangement=Arrangement.spacedBy(9.dp)){
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            DateBox(L("年"),y,{ v-> y=v.filter{c->c.isDigit()}.takeLast(4); commit() },Modifier.weight(1.25f))
            DateBox(L("月"),m,{ v-> m=v.filter{c->c.isDigit()}.takeLast(2); commit() },Modifier.weight(.85f))
            DateBox(L("日"),d,{ v-> d=v.filter{c->c.isDigit()}.takeLast(2); commit() },Modifier.weight(.85f))
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)){
            listOf(0,7,30,90).forEach{ n-> DateQuick(laterText(LocalAppLanguage.current,n),Modifier.weight(1f)){onChange(LocalDate.now().plusDays(n.toLong()).toString())} }
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)){
            listOf(7,15,30,90,180,365).forEach{ n-> DateQuick(cycleText(LocalAppLanguage.current,n),Modifier.weight(1f)){onChange(LocalDate.now().plusDays(n.toLong()).toString())} }
        }
    }
}

@Composable fun DateOnlyEditor(value:String,onChange:(String)->Unit){
    val parsed=runCatching{LocalDate.parse(value)}.getOrElse{LocalDate.now()}
    var y by remember{ mutableStateOf(parsed.year.toString()) }
    var m by remember{ mutableStateOf(parsed.monthValue.toString()) }
    var d by remember{ mutableStateOf(parsed.dayOfMonth.toString()) }
    // 仅当外部（如周期按钮）改变日期且与当前输入不一致时才同步，输入过程中不回填
    LaunchedEffect(value){
        val cur=runCatching{ LocalDate.of(y.toIntOrNull()?:0, m.toIntOrNull()?:0, d.toIntOrNull()?:0).toString() }.getOrNull()
        if(cur!=value){
            y=parsed.year.toString(); m=parsed.monthValue.toString(); d=parsed.dayOfMonth.toString()
        }
    }
    fun commit(){
        val yy=y.toIntOrNull() ?: return
        val mm=m.toIntOrNull() ?: return
        val dd=d.toIntOrNull() ?: return
        if(mm !in 1..12) return
        val maxDay=runCatching{ java.time.YearMonth.of(yy,mm).lengthOfMonth() }.getOrElse{31}
        if(dd !in 1..maxDay) return
        onChange(runCatching{LocalDate.of(yy,mm,dd)}.getOrNull()?.toString() ?: return)
    }
    Column(verticalArrangement=Arrangement.spacedBy(9.dp)){
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            DateBox(L("年"),y,{ v-> y=v.filter{c->c.isDigit()}.take(4); commit() },Modifier.weight(1.25f))
            DateBox(L("月"),m,{ v-> m=v.filter{c->c.isDigit()}.take(2); commit() },Modifier.weight(.85f))
            DateBox(L("日"),d,{ v-> d=v.filter{c->c.isDigit()}.take(2); commit() },Modifier.weight(.85f))
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)){
            listOf(0,7,30,90).forEach{ n-> DateQuick(laterText(LocalAppLanguage.current,n),Modifier.weight(1f)){ val nd=LocalDate.now().plusDays(n.toLong()); y=nd.year.toString(); m=nd.monthValue.toString(); d=nd.dayOfMonth.toString(); onChange(nd.toString()) } }
        }
    }
}

@Composable fun DateBox(label:String,value:String,onValue:(String)->Unit,m:Modifier){
    Column(m,verticalArrangement=Arrangement.spacedBy(4.dp)){
        Text(label,fontSize=11.sp,color=Color(0xFF8A94A6))
        OutlinedTextField(value=value,onValueChange=onValue,singleLine=true,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp),shape=RoundedCornerShape(12.dp),textStyle=androidx.compose.ui.text.TextStyle(fontSize=14.sp),colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=dk(Color(0xFF38383A),Color(0xFFD1D5DB)),unfocusedBorderColor=dk(Color(0xFF38383A),Color(0xFFD1D5DB)),focusedContainerColor=dk(Color(0xFF1C1C1E),Color.White),unfocusedContainerColor=dk(Color(0xFF1C1C1E),Color.White)))
    }
}

@Composable fun DateQuick(text:String,m:Modifier,onClick:()->Unit){
    Box(m.height(34.dp).clip(RoundedCornerShape(11.dp)).background(dk(Color(0xFF1C2333),Color(0xFFF4F5F8))).clickable{onClick()},contentAlignment=Alignment.Center){Text(text,fontSize=12.sp,color=Color(0xFF007AFF),maxLines=1)}
}

@Composable fun FormSection(title:String, content:@Composable ColumnScope.()->Unit){
    Column(verticalArrangement=Arrangement.spacedBy(6.dp)){
        Text(title,fontSize=13.sp,color=Color(0xFF8A94A6),modifier=Modifier.padding(start=4.dp))
        Card(shape=RoundedCornerShape(18.dp),colors=CardDefaults.cardColors(containerColor=dk(Color(0xFF1C1C1E),Color.White)),elevation=CardDefaults.cardElevation(0.dp),modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){ content() }
        }
    }
}

@Composable fun 编辑Dialog(init:PhoneNumberRecord,onDismiss:()->Unit,onSave:(PhoneNumberRecord)->Unit){ Full编辑Screen(init,onDismiss,onSave) }
@Composable fun DateFields(value:String,on:(String)->Unit){ var y by remember(value){mutableStateOf(value.split("-").getOrNull(0)?:LocalDate.now().year.toString())}; var m by remember(value){mutableStateOf(value.split("-").getOrNull(1)?:"01")}; var d by remember(value){mutableStateOf(value.split("-").getOrNull(2)?:"01")}; fun emit(){ val mm=m.padStart(2,'0'); val dd=d.padStart(2,'0'); on("$y-$mm-$dd")}; Row(horizontalArrangement=Arrangement.spacedBy(4.dp)){OutlinedTextField(y,{y=it.take(4).filter(Char::isDigit);emit()},Modifier,label={Text("年")});OutlinedTextField(m,{m=it.take(2).filter(Char::isDigit);emit()},Modifier,label={Text("月")});OutlinedTextField(d,{d=it.take(2).filter(Char::isDigit);emit()},Modifier,label={Text("日")})}; Row{ listOf("今天" to 0,"7天后" to 7,"30天后" to 30,"90天后" to 90).forEach{TextButton({on(LocalDate.now().plusDays(it.second.toLong()).toString())}){Text(it.first)}} } }
@Composable fun CountryDialog(onDismiss:()->Unit,onPick:(Country)->Unit){
    var q by remember{mutableStateOf("")}
    Dialog(onDismissRequest=onDismiss){
        Surface(shape=RoundedCornerShape(26.dp),color=dk(Color(0xFF1C1C1E),Color(0xFFF2F3F7)),modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    Text(L("选择国家区号"),fontSize=20.sp,fontWeight=FontWeight.Bold,color=dk(Color(0xFFE5E5E7),Color(0xFF111827)),modifier=Modifier.weight(1f))
                    TextButton(onDismiss){Text(L("取消"),color=Color(0xFF007AFF))}
                }
                TextField(value=q,onValueChange={q=it},modifier=Modifier.fillMaxWidth().heightIn(min=36.dp).clip(RoundedCornerShape(12.dp)),singleLine=true,placeholder={Text(L("搜索国家 / 区号 / ISO"))},leadingIcon={Canvas(Modifier.size(16.dp)){drawCircle(Color(0xFF8E8E93),radius=size.width/2-1.dp.toPx(),style=Stroke(1.5.dp.toPx()));drawLine(Color(0xFF8E8E93),Offset(size.width*.65f,size.height*.65f),Offset(size.width*.85f,size.height*.85f),strokeWidth=1.5.dp.toPx())}},colors=TextFieldDefaults.colors(focusedContainerColor=dk(Color(0xFF2C2C2E),Color.White),unfocusedContainerColor=dk(Color(0xFF2C2C2E),Color.White),focusedIndicatorColor=Color.Transparent,unfocusedIndicatorColor=Color.Transparent))
                LazyColumn(Modifier.heightIn(max=460.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){
                    items(Countries.list.filter{it.name.contains(q,true)||it.code.contains(q)||it.iso.contains(q,true)}){ c->
                        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(15.dp)).background(dk(Color(0xFF1C1C1E),Color.White)).clickable{onPick(c)}.padding(horizontal=13.dp,vertical=12.dp),verticalAlignment=Alignment.CenterVertically){Text(c.flag,fontSize=24.sp);Spacer(Modifier.width(10.dp));Text(c.name,fontSize=16.sp,fontWeight=FontWeight.SemiBold,modifier=Modifier.weight(1f));Text("${c.code}  ${c.iso}",fontSize=13.sp,color=Color(0xFF8A94A6));Spacer(Modifier.width(4.dp));Text("›",fontSize=22.sp,color=Color(0xFFC7C7CC))}
                    }
                }
            }
        }
    }
}

@Composable fun CountryPage(){
    LazyColumn(Modifier.fillMaxSize().background(dk(Color(0xFF0B0F17),Color(0xFFF2F3F7))).padding(horizontal=18.dp,vertical=14.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
        item{ Text(L("国家区号库"),fontSize=28.sp,fontWeight=FontWeight.Bold,color=dk(Color(0xFFE5E5E7),Color(0xFF111827)),modifier=Modifier.padding(horizontal=4.dp,vertical=4.dp)) }
        item{
            IOSSection(L("全部国家 / 地区")){
                Countries.list.forEach{ c->
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).padding(horizontal=4.dp,vertical=8.dp),verticalAlignment=Alignment.CenterVertically){Text(c.flag,fontSize=24.sp);Spacer(Modifier.width(10.dp));Text(c.name,fontSize=16.sp,fontWeight=FontWeight.SemiBold,modifier=Modifier.weight(1f));Text("${c.code}  ${c.iso}",fontSize=13.sp,color=Color(0xFF8A94A6))}
                }
            }
        }
        item{Spacer(Modifier.height(80.dp))}
    }
}

@OptIn(ExperimentalLayoutApi::class)

fun recordToJson(r:PhoneNumberRecord)=JSONObject()
    .put("id",r.id).put("countryCode",r.countryCode).put("countryName",r.countryName).put("flag",r.flag)
    .put("number",r.number).put("operator",r.operator).put("expireDate",r.expireDate).put("note",r.note)
    .put("balance",r.balance).put("eid",r.eid).put("smdp",r.smdp).put("activationCode",r.activationCode)
    .put("startDate",r.startDate).put("createdAt",r.createdAt).put("activatedAt",r.activatedAt)
    .put("longTerm",r.longTerm).put("cycleDays",r.cycleDays).put("signalStatus",r.signalStatus)

fun cleanCloudApiKey(raw:String):String {
    val t=raw.trim()
    if(t.isBlank()) return ""
    val exact=Regex("[A-Za-z0-9_-]{24,80}").findAll(t).map{it.value}.filterNot{ it.equals("API",true) || it.equals("Key",true) }.toList()
    return (exact.lastOrNull() ?: t.lineSequence().map{it.trim()}.firstOrNull{it.isNotBlank()} ?: "").replace(Regex("[\r\n\t ]+"),"").trim()
}
fun cleanCloudUrl(raw:String):String = raw.trim().trimEnd('/')

fun cloudPayload(records:List<PhoneNumberRecord>,s:App设置):String{
    val settings=JSONObject()
        .put("remind天",s.remind天).put("remindDays",s.remind天)
        .put("tgEnabled",s.tgEnabled).put("botToken",s.botToken).put("chatId",s.chatId)
        .put("smtpEnabled",s.smtpEnabled).put("smtpHost",s.smtpHost).put("smtpPort",s.smtpPort)
        .put("smtpUser",s.smtpUser).put("smtpPass",s.smtpPass).put("smtpFrom",s.smtpFrom).put("smtpTo",s.smtpTo)
        .put("cloudTelegramEnabled",s.cloudTelegramEnabled).put("cloudEmailEnabled",s.cloudEmailEnabled)
    val arr=JSONArray(); records.forEach{arr.put(recordToJson(it))}
    return JSONObject().put("settings",settings).put("records",arr).toString()
}
fun cloudPost(s:App设置,path:String,body:String,lang:String="简体中文",onResult:(Boolean,String)->Unit){
    val apiKey=cleanCloudApiKey(s.cloudApiKey)
    val cloudUrl=cleanCloudUrl(if(s.cloudUrl.isBlank()) "https://ccs.ziranaa.top:16670" else s.cloudUrl)
    if(cloudUrl.isBlank()){onResult(false,tr(lang,"云端地址未填写"));return}
    val needsAuth=!(path.startsWith("/api/register")||path.startsWith("/api/status"))
    if(needsAuth&&apiKey.isBlank()){onResult(false,tr(lang,"API Key 未填写"));return}
    thread{
        val res=runCatching{
            val base=cloudUrl
            val c=(URL(base+path).openConnection() as HttpURLConnection)
            if(path.startsWith("/api/status")){
                c.requestMethod="GET"; c.connectTimeout=12000; c.readTimeout=10000
            }else{
                c.requestMethod="POST"; c.connectTimeout=12000; c.readTimeout=20000; c.doOutput=true
                c.setRequestProperty("Content-Type","application/json; charset=utf-8")
                if(needsAuth) c.setRequestProperty("X-API-Key",apiKey)
                c.outputStream.use{it.write(body.toByteArray(Charsets.UTF_8))}
            }
            val respCode=c.responseCode
            val stream=if(respCode in 200..299) c.inputStream else c.errorStream
            val respBody=stream?.bufferedReader(Charsets.UTF_8)?.readText() ?: ""
            if(respCode !in 200..299) "HTTP $respCode: $respBody" else respBody
        }.fold({it},{tr(lang,"失败")+": ${it.javaClass.simpleName}: ${it.message}"})
        Handler(Looper.getMainLooper()).post{onResult(!res.startsWith(tr(lang,"失败")),res)}
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable fun 设置Page(ctx:Context,s:App设置,records:List<PhoneNumberRecord>,currentVersion:String="0.0.0",onUpdateCheck:(()->Unit)?=null,on:(App设置)->Unit,onTraffic:(PhoneNumberRecord)->Unit={},onDial:(PhoneNumberRecord)->Unit={},onExportJson:()->Unit={},onExportCsv:()->Unit={},onImportText:(String)->Unit={}){
    var st by remember{s.mutableState()}
    var cloudMsg by remember{ mutableStateOf("") }
    val pageLang = LocalAppLanguage.current
    fun S(key:String)=tr(pageLang,key)
    val bgPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? -> if(uri!=null){ st=st.copyMut{backgroundUri=uri.toString()}; on(st) } }
    Column(Modifier.fillMaxSize().background(if(st.dark) Color(0xFF0B0F17) else Color(0xFFF2F3F7)).verticalScroll(rememberScrollState()).padding(horizontal=18.dp,vertical=12.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
        SettingsSection(L("外观")){
            IOSSwitchRow(L("深色模式"),st.dark){ st=st.copyMut{dark=it}; on(st) }
            IOSSwitchRow(L("显示首页卡片国旗"),st.showFlag){ st=st.copyMut{showFlag=it}; on(st) }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                Button({bgPicker.launch("image/*")},shape=RoundedCornerShape(14.dp)){Text(L("更改背景图片"))}
                TextButton({st=st.copyMut{backgroundUri=""};on(st)}){Text(L("清除"))}
            }
            if(st.backgroundUri.isNotBlank()){
                Text(L("已设置自定义背景"),fontSize=11.sp,color=Color(0xFF007AFF))
                Text(L("背景遮罩透明度")+"：${(st.backgroundAlpha*100).roundToInt()}%",fontSize=12.sp,color=Color(0xFF8A94A6))
                Slider(value=st.backgroundAlpha,onValueChange={v->st=st.copyMut{backgroundAlpha=v};on(st)},valueRange=0f..1f)
            }
        }
        SettingsSection(L("提醒设置")){
            IOSSwitchRow(L("开启到期提醒"),st.reminderEnabled){ st=st.copyMut{reminderEnabled=it}; on(st) }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)){ listOf(1,3,7).forEach{ d-> IOSChip(L("提前")+cycleText(LocalAppLanguage.current,d),st.remind天==d,Modifier.weight(1f)){ st=st.copyMut{remind天=d}; on(st) } } }
            OutlinedTextField(st.remind天.toString(),{st=st.copyMut{remind天=it.toIntOrNull()?:7};on(st)},modifier=Modifier.fillMaxWidth(),label={Text(L("自定义提前天数"))},singleLine=true)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                OutlinedTextField(st.remindHour.toString(),{st=st.copyMut{remindHour=(it.toIntOrNull()?:9).coerceIn(0,23)};on(st)},modifier=Modifier.weight(1f),label={Text(L("小时"))},singleLine=true)
                OutlinedTextField(st.remindMinute.toString(),{st=st.copyMut{remindMinute=(it.toIntOrNull()?:0).coerceIn(0,59)};on(st)},modifier=Modifier.weight(1f),label={Text(L("分钟"))},singleLine=true)
            }
        }
        TrafficInterfaceSettings(st,{ ns-> st=ns; on(st) })

        SettingsSection(S("云端提醒")){
            IOSSwitchRow(S("启用云端提醒"),st.cloudEnabled){ st=st.copyMut{cloudEnabled=it}; on(st) }
            IOSSwitchRow(S("自动同步"),st.cloudAutoSync){ st=st.copyMut{cloudAutoSync=it}; on(st) }
            Text(S("自动同步说明"),fontSize=11.sp,color=Color(0xFF8A94A6),lineHeight=16.sp)
            PlainInput("API Key",st.cloudApiKey){ st=st.copyMut{cloudApiKey=cleanCloudApiKey(it)}; on(st) }
            Text(S("API Key说明"),fontSize=11.sp,color=Color(0xFF8A94A6),lineHeight=16.sp)
            Text(S("当前 API Key：")+if(st.cloudApiKey.isNotBlank()) cleanCloudApiKey(st.cloudApiKey) else S("未设置"),fontSize=12.sp,color=Color(0xFF8A94A6),lineHeight=17.sp)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                Button({ cloudPost(st.copyMut{cloudUrl="https://ccs.ziranaa.top:16670"},"/api/status",cloudPayload(records,st)){ok,msg-> cloudMsg=if(ok) S("连接成功") else msg } },shape=RoundedCornerShape(14.dp),modifier=Modifier.weight(1f)){Text(S("测试连接"))}
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                Button({
                    if(st.cloudApiKey.isNotBlank()) {
                        val clipboard=ctx.getSystemService(android.content.ClipboardManager::class.java)
                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("api key",cleanCloudApiKey(st.cloudApiKey)))
                        cloudMsg=S("已复制 API Key")
                    }else cloudMsg=S("请先生成或填写 API Key")
                },shape=RoundedCornerShape(14.dp),modifier=Modifier.weight(1f)){Text(S("复制 Key"))}
                Button({
                    val existing=cleanCloudApiKey(st.cloudApiKey)
                    if(existing.isNotBlank()){
                        cloudMsg=S("已有固定Key说明")
                    }else{
                        cloudPost(st,"/api/register","{}"){ok,msg-> if(ok) { try{ val r=JSONObject(msg); val k=r.optString("apiKey",""); if(k.isNotBlank()){ st=st.copyMut{cloudApiKey=k}; on(st); cloudMsg=S("已生成本机固定 Key，已保存") }else cloudMsg=msg } catch(_:Exception){ cloudMsg=msg } }else cloudMsg=msg }
                    }
                },shape=RoundedCornerShape(14.dp),modifier=Modifier.weight(1f)){Text(S("生成我的 Key"))}
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                Button({ cloudPost(st,"/api/sync",cloudPayload(records,st)){ok,msg-> cloudMsg=if(ok) S("同步成功") else msg } },shape=RoundedCornerShape(14.dp),modifier=Modifier.weight(1f)){Text(S("同步到云端"))}
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                IOSSwitchRow(S("云端 Telegram"),st.cloudTelegramEnabled){ st=st.copyMut{cloudTelegramEnabled=it}; on(st) }
            }
            if(st.cloudTelegramEnabled){
                IOSSwitchRow(S("启用 TG 配置"),st.tgEnabled){ st=st.copyMut{tgEnabled=it}; on(st) }
                PlainInput("Bot Token",st.botToken){ st=st.copyMut{botToken=it}; on(st) }
                PlainInput("Chat ID",st.chatId){ st=st.copyMut{chatId=it}; on(st) }
                Text(S("TG配置说明"),fontSize=11.sp,color=Color(0xFF8A94A6),lineHeight=16.sp)
            }
            IOSDividerLine()
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                IOSSwitchRow(S("云端邮件"),st.cloudEmailEnabled){ st=st.copyMut{cloudEmailEnabled=it}; on(st) }
            }
            if(st.cloudEmailEnabled){
                IOSSwitchRow(S("SMTP 自动发邮件"),st.smtpEnabled){ st=st.copyMut{smtpEnabled=it}; on(st) }
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    Box(Modifier.weight(1f)){ PlainInput(S("SMTP 服务器"),st.smtpHost){ st=st.copyMut{smtpHost=it}; on(st) } }
                    Box(Modifier.weight(.45f)){ PlainInput(S("端口"),st.smtpPort.toString()){ st=st.copyMut{smtpPort=it.toIntOrNull()?:465}; on(st) } }
                }
                PlainInput(S("邮箱账号"),st.smtpUser){ st=st.copyMut{smtpUser=it}; on(st) }
                PlainInput(S("授权码"),st.smtpPass){ st=st.copyMut{smtpPass=it}; on(st) }
                PlainInput(S("发件邮箱"),st.smtpFrom){ st=st.copyMut{smtpFrom=it}; on(st) }
                PlainInput(S("收件邮箱"),st.smtpTo){ st=st.copyMut{smtpTo=it}; on(st) }
                Text(S("SMTP授权码说明"),fontSize=11.sp,color=Color(0xFF8A94A6),lineHeight=16.sp)
            }
            IOSDividerLine()
            IOSSwitchRow(S("本地通知提醒"),st.notificationEnabled){ st=st.copyMut{notificationEnabled=it}; on(st) }
            IOSSwitchRow(S("通知一键发邮件"),st.emailQuickEnabled){ st=st.copyMut{emailQuickEnabled=it}; on(st) }
            Text(S("本地通知说明"),fontSize=11.sp,color=Color(0xFF8A94A6),lineHeight=16.sp)
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                Button({ cloudPost(st,"/api/test-telegram",cloudPayload(records,st)){ok,msg-> cloudMsg=if(ok) S("TG 测试已发送") else msg } },shape=RoundedCornerShape(14.dp),modifier=Modifier.weight(1f)){Text(S("测试TG"))}
                Button({ cloudPost(st,"/api/test-email",cloudPayload(records,st)){ok,msg-> cloudMsg=if(ok) S("邮件测试已发送") else msg } },shape=RoundedCornerShape(14.dp),modifier=Modifier.weight(1f)){Text(S("测试邮件"))}
            }
            Button({ cloudPost(st,"/api/check-now",cloudPayload(records,st)){ok,msg-> cloudMsg=if(ok) S("已触发云端检查") else msg } },shape=RoundedCornerShape(14.dp),modifier=Modifier.fillMaxWidth()){Text(S("立即检查到期"))}
            Text(S("云端服务说明"),fontSize=12.sp,color=Color(0xFF8A94A6),lineHeight=17.sp)
            if(cloudMsg.isNotBlank()) Text(cloudMsg,fontSize=12.sp,color=Color(0xFF007AFF),lineHeight=17.sp)
        }


        SettingsSection(L("工具")){
            var pickTraffic by remember { mutableStateOf(false) }
            var pickDial by remember { mutableStateOf(false) }
            var importDlg by remember { mutableStateOf(false) }
            var importText by remember { mutableStateOf("") }
            ToolRow("traffic",L("刷流量"),L("选择一个号码执行真实下载流量测试")){ pickTraffic=true }
            ToolRow("dial",L("拨号测试"),L("选择号码并打开系统拨号器")){ pickDial=true }
            ToolRow("giffgaff","giffgaff eSIM 获取","打开 esim.kim/giffgaff 在线工具"){
                runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://esim.kim/giffgaff"))) }.onFailure { Toast.makeText(ctx, "无法打开浏览器：${it.message}", Toast.LENGTH_SHORT).show() }
            }
            ToolRow("export_json",L("导出 JSON"),L("生成完整 JSON 备份文本")){ onExportJson() }
            ToolRow("export_csv",L("导出 CSV"),L("生成 CSV 表格文本")){ onExportCsv() }
            ToolRow("import",L("导入数据"),L("粘贴 JSON 或 CSV 恢复号码列表")){ importDlg=true }
            if(pickTraffic) NumberPickerDialog(L("选择刷流量号码"),records,{pickTraffic=false}){ pickTraffic=false; onTraffic(it) }
            if(pickDial) NumberPickerDialog(L("选择拨号号码"),records,{pickDial=false}){ pickDial=false; onDial(it) }
            if(importDlg) IOSImportDialog(importText,{importText=it},{importDlg=false},{ onImportText(importText); importDlg=false },ctx)
        }
        SettingsSection(L("语言 / Language")){
            Text(L("当前语言：")+st.language,fontSize=13.sp,color=Color(0xFF8A94A6))
            FlowRow(horizontalArrangement=Arrangement.spacedBy(7.dp),verticalArrangement=Arrangement.spacedBy(7.dp)){
                listOf("简体中文","繁体中文","English","日本語","阿拉伯语").forEach{ lang -> IOSChip(lang,st.language==lang){ st=st.copyMut{language=lang}; on(st) } }
            }
            Text(if(st.language=="阿拉伯语") L("已启用 RTL 右到左布局") else L("支持实时切换，主要页面会立即刷新。"),fontSize=12.sp,color=Color(0xFF8A94A6))
        }
        SettingsSection(L("关于")){
            Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){
                Box(Modifier.size(34.dp).clip(RoundedCornerShape(17.dp)).background(Color(0xFF007AFF)),contentAlignment=Alignment.Center){Text("i",color=Color.White,fontWeight=FontWeight.Bold)}
                Spacer(Modifier.width(10.dp))
                Text("simJ v"+currentVersion+"\n"+L("开发者")+"：伍六柒\n"+L("本地数据存储"),fontSize=13.sp,color=Color(0xFF4B5563),lineHeight=20.sp)
            }
            Spacer(Modifier.height(8.dp))
            var checking by remember { mutableStateOf(false) }
            LaunchedEffect(checking) { if(checking) { kotlinx.coroutines.delay(2000); checking=false } }
            Button(
                onClick = {
                    checking = true
                    onUpdateCheck?.invoke()
                },
                enabled = !checking,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (checking) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (checking) "检查中..." else "检查更新")
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable fun IOSSection(title:String,content:@Composable ColumnScope.()->Unit){
    Column(verticalArrangement=Arrangement.spacedBy(6.dp)){
        Text(title,fontSize=13.sp,color=Color(0xFF8A94A6),modifier=Modifier.padding(start=4.dp))
        Card(shape=RoundedCornerShape(20.dp),colors=CardDefaults.cardColors(containerColor=dk(Color(0xFF1C1C1E).copy(alpha=.82f),Color.White.copy(alpha=.82f))),elevation=CardDefaults.cardElevation(0.dp),modifier=Modifier.fillMaxWidth().border(.7.dp,dk(Color(0xFF2C2C2E).copy(alpha=.9f),Color.White.copy(alpha=.9f)),RoundedCornerShape(20.dp))){
            Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(9.dp)){content()}
        }
    }
}

@Composable fun SettingsSection(title:String,content:@Composable ColumnScope.()->Unit){
    var expanded by remember(title){ mutableStateOf(false) }
    Column(verticalArrangement=Arrangement.spacedBy(0.dp)){
        Surface(shape=RoundedCornerShape(if(expanded) 20.dp else 18.dp),color=dk(Color(0xFF1C1C1E).copy(alpha=.88f),Color.White.copy(alpha=.88f)),tonalElevation=0.dp,modifier=Modifier.fillMaxWidth().border(.7.dp,dk(Color(0xFF38383A).copy(alpha=.95f),Color.White.copy(alpha=.95f)),RoundedCornerShape(if(expanded) 20.dp else 18.dp))){
            Column{
                Row(Modifier.fillMaxWidth().height(52.dp).clickable{expanded=!expanded}.padding(horizontal=14.dp),verticalAlignment=Alignment.CenterVertically){
                    Text(title,fontSize=16.sp,fontWeight=FontWeight.SemiBold,color=dk(Color(0xFFE5E5E7),Color(0xFF111827)),modifier=Modifier.weight(1f))
                    Text(if(expanded) "⌃" else "›",fontSize=22.sp,color=Color(0xFF8A94A6),fontWeight=FontWeight.SemiBold)
                }
                if(expanded){
                    IOSDividerLine()
                    Column(Modifier.padding(12.dp),verticalArrangement=Arrangement.spacedBy(9.dp)){content()}
                }
            }
        }
    }
}
@Composable fun IOSSwitchRow(title:String,checked:Boolean,onChecked:(Boolean)->Unit){
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween){
        Text(title,fontSize=16.sp,color=dk(Color(0xFFE5E5E7),Color(0xFF111827))); Switch(checked,onChecked)
    }
}

fun App设置.mutableState()= mutableStateOf(this)
@Composable fun TrafficInterfaceSettings(st:App设置,onChange:(App设置)->Unit){
    SettingsSection(L("流量接口")){
        PlainInput(label=L("流量接口 URL"),value=st.trafficUrl,onValue={ onChange(st.copyMut{trafficUrl=it}) })
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly){
            listOf(
                "Cloudflare" to "https://speed.cloudflare.com/__down?bytes=10485760",
                "Hetzner" to "https://speed.hetzner.de/10MB.bin",
                "ThinkBroadband" to "https://ipv4.download.thinkbroadband.com/10MB.zip"
            ).forEach{ item-> Text(item.first,fontSize=13.sp,fontWeight=FontWeight.SemiBold,color=Color(0xFF00A7D9),modifier=Modifier.clip(RoundedCornerShape(8.dp)).clickable{onChange(st.copyMut{trafficUrl=item.second})}.padding(horizontal=6.dp,vertical=4.dp)) }
        }
        PlainInput(label=L("默认流量 KB"),value=st.trafficKb.toString(),onValue={ onChange(st.copyMut{trafficKb=it.toDoubleOrNull()?:st.trafficKb}) })
    }
}

@Composable fun PlainInput(label:String,value:String,onValue:(String)->Unit){
    Column(verticalArrangement=Arrangement.spacedBy(4.dp)){
        Text(label,fontSize=13.sp,color=Color(0xFF374151))
        OutlinedTextField(value=value,onValueChange=onValue,modifier=Modifier.fillMaxWidth().heightIn(min=56.dp),singleLine=true,shape=RoundedCornerShape(13.dp),colors=OutlinedTextFieldDefaults.colors(focusedBorderColor=dk(Color(0xFF38383A),Color(0xFFD1D5DB)),unfocusedBorderColor=dk(Color(0xFF38383A),Color(0xFFD1D5DB)),focusedContainerColor=dk(Color(0xFF1C1C1E),Color.White),unfocusedContainerColor=dk(Color(0xFF1C1C1E),Color.White)))
    }
}

fun App设置.copyMut(block:App设置.()->Unit):App设置{ val n=this.copy(); n.block(); return n }
fun App设置.copy()=App设置(dark,remind天,trafficUrl,trafficKb,tgEnabled,botToken,chatId,keepCycle,backgroundUri,backgroundAlpha,reminderEnabled,notificationEnabled,remindHour,remindMinute,language,emailQuickEnabled,smtpEnabled,smtpHost,smtpPort,smtpUser,smtpPass,smtpFrom,smtpTo,cloudEnabled,cloudUrl,cloudApiKey,cloudTelegramEnabled,cloudEmailEnabled,cloudAutoSync,showFlag)
@Composable fun Presets(on:(String)->Unit){
    Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){
        mapOf(
            "Cloudflare" to "https://speed.cloudflare.com/__down?bytes=10485760",
            "Hetzner" to "https://speed.hetzner.de/10MB.bin",
            "ThinkBroadband" to "https://ipv4.download.thinkbroadband.com/10MB.zip",
            "Google204" to "https://www.google.com/generate_204"
        ).forEach{TextButton({on(it.value)}){Text(it.key)}}
    }
}


@Composable fun IOSConfirmDialog(title:String,message:String,danger:Boolean=false,onCancel:()->Unit,onConfirm:()->Unit){
    Dialog(onDismissRequest=onCancel){
        Surface(shape=RoundedCornerShape(24.dp),color=Color(0xFFF2F3F7),tonalElevation=0.dp,modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp),horizontalAlignment=Alignment.CenterHorizontally){
                Text(title,fontSize=20.sp,fontWeight=FontWeight.Bold,color=Color(0xFF111827),textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                Text(message,fontSize=14.sp,color=Color(0xFF6B7280),lineHeight=20.sp,textAlign=androidx.compose.ui.text.style.TextAlign.Center)
                Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){
                    Box(Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(16.dp)).background(Color.White).clickable{onCancel()},contentAlignment=Alignment.Center){Text(L("取消"),fontSize=16.sp,fontWeight=FontWeight.SemiBold,color=Color(0xFF007AFF))}
                    Box(Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(16.dp)).background(if(danger) Color(0xFFFF3B30) else Color(0xFF007AFF)).clickable{onConfirm()},contentAlignment=Alignment.Center){Text(L("确认"),fontSize=16.sp,fontWeight=FontWeight.SemiBold,color=Color.White)}
                }
            }
        }
    }
}

@Composable fun TrafficDialog(ctx:Context,r:PhoneNumberRecord,s:App设置,onDismiss:()->Unit){
    var url by remember{mutableStateOf(if(s.trafficUrl.contains("generate_204")) "https://speed.cloudflare.com/__down?bytes=10485760" else s.trafficUrl)}
    var amount by remember{mutableStateOf(if(s.trafficKb>1.0) "${s.trafficKb.roundToInt()}KB" else "1MB")}
    var confirm by remember{mutableStateOf(false)}
    var result by remember{mutableStateOf<String?>(null)}
    val lang = LocalAppLanguage.current
    Dialog(onDismissRequest=onDismiss){
        Surface(shape=RoundedCornerShape(28.dp),color=Color(0xFFF2F3F7),tonalElevation=0.dp,modifier=Modifier.fillMaxWidth()){
            Column(Modifier.padding(18.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){
                Row(verticalAlignment=Alignment.CenterVertically){
                    Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(Color(0xFF007AFF)),contentAlignment=Alignment.Center){Text("▥",fontSize=22.sp,color=Color.White,fontWeight=FontWeight.Bold)}
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)){Text(L("刷流量"),fontSize=22.sp,fontWeight=FontWeight.Bold,color=Color(0xFF111827));Text(L("真实下载数据测试"),fontSize=12.sp,color=Color(0xFF8A94A6))}
                    TextButton(onDismiss){Text(L("关闭"),color=Color(0xFF007AFF))}
                }
                IOSSection(L("号码")){
                    Row(verticalAlignment=Alignment.CenterVertically){Text(r.flag,fontSize=24.sp);Spacer(Modifier.width(8.dp));Column{Text(r.operator.ifBlank{r.countryName},fontWeight=FontWeight.SemiBold);Text("${r.countryCode} ${formatNumber(r.number)}",fontSize=13.sp,color=Color(0xFF6B7280))}}
                }
                IOSSection(L("下载测试接口")){
                    PlainInput(label="URL",value=url,onValue={url=it})
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)){
                        listOf(
                            "Cloudflare" to "https://speed.cloudflare.com/__down?bytes=10485760",
                            "Hetzner" to "https://speed.hetzner.de/10MB.bin",
                            "Think" to "https://ipv4.download.thinkbroadband.com/10MB.zip"
                        ).forEach{ item-> IOSChip(item.first, url==item.second, Modifier.weight(1f)){url=item.second} }
                    }
                }
                IOSSection(L("目标流量")){
                    PlainInput(label=L("例：100KB / 1MB / 50MB"),value=amount,onValue={amount=it})
                    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)){
                        listOf("100KB","1MB","5MB","10MB").forEach{ IOSChip(it, amount==it, Modifier.weight(1f)){amount=it} }
                    }
                    Text(L("204 / 空响应接口不能真正消耗流量，建议使用 Cloudflare 或 Hetzner。"),fontSize=12.sp,color=Color(0xFF8A94A6),lineHeight=17.sp)
                }
                result?.let{
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(12.dp)){Text(it,fontSize=13.sp,color=Color(0xFF374151))}
                }
                Button(onClick={confirm=true},modifier=Modifier.fillMaxWidth().height(52.dp),shape=RoundedCornerShape(17.dp),colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF007AFF))){Text(L("开始刷流量"),fontSize=16.sp,fontWeight=FontWeight.SemiBold)}
            }
        }
    }
    if(confirm) IOSConfirmDialog(L("确认刷流量？"),L("将实际下载约")+" ${amount} "+L("目标流量")+"。\n"+L("确认后会真实消耗当前网络流量。"),false,{confirm=false},{
        confirm=false
        val targetKb=parseTrafficKb(amount).coerceIn(1.0,1024.0*500.0)
        result=tr(lang,"请求中…")
        consumeTraffic(url,targetKb,lang){msg->result=msg; if(s.tgEnabled) sendTelegram(s.botToken,s.chatId,"📶 Sim Jiang ${tr(lang,"刷流量")}\n${r.countryCode} ${formatNumber(r.number)}\n$msg")}
    })
}

@Composable fun IOSChip(text:String,selected:Boolean,m:Modifier=Modifier,onClick:()->Unit){
    Box(m.height(34.dp).clip(RoundedCornerShape(12.dp)).background(if(selected) Color(0xFF007AFF) else Color(0xFFF4F5F8)).border(.7.dp,if(selected) Color(0xFF007AFF) else Color(0xFFE5E7EB),RoundedCornerShape(12.dp)).clickable{onClick()},contentAlignment=Alignment.Center){Text(text,fontSize=12.sp,fontWeight=FontWeight.SemiBold,color=if(selected) Color.White else Color(0xFF007AFF),maxLines=1,overflow=TextOverflow.Ellipsis)}
}



fun csvEscape(v:String)= if(v.any{ it==',' || it=='"' || it=='\n' || it=='\r' }) "\""+v.replace("\"","\"\"")+"\"" else v
fun csvLine(values:List<String>)=values.joinToString(","){csvEscape(it)}
fun recordFields(r:PhoneNumberRecord)=listOf(r.id,r.countryCode,r.countryName,r.flag,r.number,r.operator,r.expireDate,r.note,r.balance,r.eid,r.smdp,r.activationCode,r.startDate,r.createdAt,r.activatedAt,r.longTerm.toString(),r.cycleDays.toString(),r.signalStatus)
val recordHeader=listOf("id","countryCode","countryName","flag","number","operator","expireDate","note","balance","eid","smdp","activationCode","startDate","createdAt","activatedAt","longTerm","cycleDays","signalStatus")

fun settingsToJson(s:App设置):JSONObject = JSONObject().put("dark",s.dark).put("remind天",s.remind天).put("trafficUrl",s.trafficUrl).put("trafficKb",s.trafficKb).put("tgEnabled",s.tgEnabled).put("botToken",s.botToken).put("chatId",s.chatId).put("keepCycle",s.keepCycle).put("backgroundUri",s.backgroundUri).put("backgroundAlpha",s.backgroundAlpha.toDouble()).put("reminderEnabled",s.reminderEnabled).put("notificationEnabled",s.notificationEnabled).put("remindHour",s.remindHour).put("remindMinute",s.remindMinute).put("language",s.language).put("emailQuickEnabled",s.emailQuickEnabled).put("smtpEnabled",s.smtpEnabled).put("smtpHost",s.smtpHost).put("smtpPort",s.smtpPort).put("smtpUser",s.smtpUser).put("smtpPass",s.smtpPass).put("smtpFrom",s.smtpFrom).put("smtpTo",s.smtpTo).put("cloudEnabled",s.cloudEnabled).put("cloudUrl",s.cloudUrl).put("cloudApiKey",s.cloudApiKey).put("cloudTelegramEnabled",s.cloudTelegramEnabled).put("cloudEmailEnabled",s.cloudEmailEnabled).put("cloudAutoSync",s.cloudAutoSync).put("showFlag",s.showFlag)

fun settingsFromJson(o:JSONObject):App设置 = App设置(dark=o.optBoolean("dark",false),remind天=o.optInt("remind天",7),trafficUrl=o.optString("trafficUrl","https://speed.cloudflare.com/__down?bytes=10485760"),trafficKb=o.optDouble("trafficKb",1.0),tgEnabled=o.optBoolean("tgEnabled",false),botToken=o.optString("botToken",""),chatId=o.optString("chatId",""),keepCycle=o.optString("keepCycle","月"),backgroundUri=o.optString("backgroundUri",""),backgroundAlpha=o.optDouble("backgroundAlpha",0.72).toFloat(),reminderEnabled=o.optBoolean("reminderEnabled",true),notificationEnabled=o.optBoolean("notificationEnabled",true),remindHour=o.optInt("remindHour",9),remindMinute=o.optInt("remindMinute",0),language=o.optString("language","简体中文"),emailQuickEnabled=o.optBoolean("emailQuickEnabled",true),smtpEnabled=o.optBoolean("smtpEnabled",false),smtpHost=o.optString("smtpHost",""),smtpPort=o.optInt("smtpPort",465),smtpUser=o.optString("smtpUser",""),smtpPass=o.optString("smtpPass",""),smtpFrom=o.optString("smtpFrom",""),smtpTo=o.optString("smtpTo",""),cloudEnabled=o.optBoolean("cloudEnabled",false),cloudUrl=o.optString("cloudUrl","https://ccs.ziranaa.top:16670"),cloudApiKey=o.optString("cloudApiKey",""),cloudTelegramEnabled=o.optBoolean("cloudTelegramEnabled",true),cloudEmailEnabled=o.optBoolean("cloudEmailEnabled",true),cloudAutoSync=o.optBoolean("cloudAutoSync",false),showFlag=o.optBoolean("showFlag",true))

fun exportRecordsJson(records:List<PhoneNumberRecord>,settings:App设置):String{
    val root=JSONObject()
    val arr=JSONArray()
    records.forEach{ r-> arr.put(DataStore.recordJson(r)) }
    root.put("type","san-sim-full-backup").put("version",3).put("count",records.size).put("records",arr).put("settings",settingsToJson(settings))
    return root.toString(2)
}

fun exportRecordsCsv(records:List<PhoneNumberRecord>):String{
    return buildString{
        appendLine(csvLine(recordHeader))
        records.forEach{ appendLine(csvLine(recordFields(it))) }
    }
}

fun splitCsvLine(line:String):List<String>{
    val out=mutableListOf<String>(); val sb=StringBuilder(); var q=false; var i=0
    while(i<line.length){ val ch=line[i]; when{
        q && ch=='"' && i+1<line.length && line[i+1]=='"' -> { sb.append('"'); i++ }
        ch=='"' -> q=!q
        ch==',' && !q -> { out.add(sb.toString()); sb.clear() }
        else -> sb.append(ch)
    }; i++ }
    out.add(sb.toString()); return out
}

fun parseRecordObject(o:JSONObject)=PhoneNumberRecord(
    id=o.optString("id",UUID.randomUUID().toString()), countryCode=o.optString("countryCode","+86"), countryName=o.optString("countryName","中国"), flag=o.optString("flag","🇨🇳"), number=o.optString("number"), operator=o.optString("operator"), expireDate=o.optString("expireDate",LocalDate.now().plusDays(30).toString()), note=o.optString("note"),
    balance=o.optString("balance"), eid=o.optString("eid"), smdp=o.optString("smdp"), activationCode=o.optString("activationCode"), startDate=o.optString("startDate",LocalDate.now().toString()), createdAt=o.optString("createdAt",LocalDate.now().toString()), activatedAt=o.optString("activatedAt"), longTerm=o.optBoolean("longTerm",false), cycleDays=o.optInt("cycleDays",30), signalStatus=o.optString("signalStatus","在线")
)

fun parseRecordsJson(text:String):Pair<List<PhoneNumberRecord>,App设置?>{
    return runCatching{
        val trimmed=text.trim()
        if(trimmed.startsWith("[")) Pair(JSONArray(trimmed).let{ arr-> (0 until arr.length()).map{ parseRecordObject(arr.getJSONObject(it)) }.filter{it.number.isNotBlank()} },null)
        else { val obj=JSONObject(trimmed); val arr=obj.getJSONArray("records"); val s=if(obj.has("settings")) settingsFromJson(obj.getJSONObject("settings")) else null; Pair((0 until arr.length()).map{ parseRecordObject(arr.getJSONObject(it)) }.filter{it.number.isNotBlank()},s) }
    }.getOrElse{ Pair(emptyList(),null) }
}

fun parseRecordsCsv(text:String):List<PhoneNumberRecord>{
    return runCatching{
        val lines=text.lines().filter{it.isNotBlank()}
        if(lines.size<2) return@runCatching emptyList<PhoneNumberRecord>()
        val header=splitCsvLine(lines.first()).map{it.trim()}
        lines.drop(1).mapNotNull{ line->
            val vals=splitCsvLine(line); val map=header.mapIndexedNotNull{ i,k-> vals.getOrNull(i)?.let{k to it} }.toMap()
            val o=JSONObject(); map.forEach{(k,v)-> when(k){"longTerm"->o.put(k,v.toBoolean());"cycleDays"->o.put(k,v.toIntOrNull()?:30);else->o.put(k,v)} }
            parseRecordObject(o).takeIf{it.number.isNotBlank()}
        }
    }.getOrElse{ emptyList() }
}

fun parseRecordsAny(text:String):List<PhoneNumberRecord> = parseRecordsJson(text).first.ifEmpty{ parseRecordsCsv(text) }
fun parseRecordsAndSettings(text:String):Pair<List<PhoneNumberRecord>,App设置?> = parseRecordsJson(text).let{ if(it.first.isEmpty()) Pair(parseRecordsCsv(text),null) else it }

fun parseTrafficKb(text:String):Double{
    val t=text.trim().uppercase().replace(" ","")
    val num=Regex("""[0-9]+(\.[0-9]+)?""").find(t)?.value?.toDoubleOrNull() ?: 1.0
    return when{
        t.contains("GB") || t.endsWith("G") -> num*1024.0*1024.0
        t.contains("MB") || t.endsWith("M") -> num*1024.0
        else -> num
    }
}

fun consumeTraffic(url:String,kb:Double,lang:String,cb:(String)->Unit){
    thread{
        val want=(kb*1024).roundToInt().coerceAtLeast(1)
        val res=runCatching{
            var total=0
            val started=System.currentTimeMillis()
            var round=0
            while(total<want && round<30){
                round++
                val sep=if(url.contains("?")) "&" else "?"
                val u=if(url.contains("speed.cloudflare.com/__down")) url.replace(Regex("""bytes=\d+"""),"bytes=${want-total}") else url+sep+"_san="+System.nanoTime()
                val c=(URL(u).openConnection() as HttpURLConnection)
                c.connectTimeout=15000
                c.readTimeout=30000
                c.instanceFollowRedirects=true
                c.useCaches=false
                c.setRequestProperty("Cache-Control","no-cache")
                c.setRequestProperty("Pragma","no-cache")
                c.setRequestProperty("User-Agent","SanSIM/1.5.6")
                val buf=ByteArray(8192)
                c.inputStream.use{ input->
                    while(total<want){
                        val n=input.read(buf,0,minOf(buf.size,want-total))
                        if(n<=0) break
                        total+=n
                    }
                }
                c.disconnect()
            }
            val sec=maxOf(0.001,(System.currentTimeMillis()-started)/1000.0)
            val speed=total/1024.0/sec
            tr(lang,"成功")+"："+tr(lang,"实际读取")+" ${"%.2f".format(total/1024.0)}KB / "+tr(lang,"目标")+" ${"%.2f".format(want/1024.0)}KB，"+tr(lang,"耗时")+" ${"%.1f".format(sec)} "+tr(lang,"秒")+"，"+tr(lang,"约")+" ${"%.1f".format(speed)}KB/s"
        }.getOrElse{tr(lang,"失败")+"：${it.javaClass.simpleName}: ${it.message}"}
        cb(res)
    }
}
fun dial(ctx:Context,r:PhoneNumberRecord){ ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${r.countryCode}${r.number}"))) }
fun formatNumber(n:String)=n.chunked(4).joinToString(" ")
fun guessOperator(n:String,iso:String):String{
    val x=n.filter{it.isDigit()}
    val p3=x.take(3); val p4=x.take(4)
    return when{
        iso=="CN" && (p4 in listOf("1340","1341","1342","1343","1344","1345","1346","1347","1348") || p3 in listOf("135","136","137","138","139","147","148","150","151","152","157","158","159","172","178","182","183","184","187","188","195","197","198"))->"中国移动"
        iso=="CN" && (p3 in listOf("130","131","132","145","146","155","156","166","175","176","185","186","196"))->"中国联通"
        iso=="CN" && (p3 in listOf("133","149","153","173","174","177","180","181","189","190","191","193","199"))->"中国电信"
        iso=="CN" && p3=="192"->"中国广电"
        iso=="CN" && (p3 in listOf("162","165","167","170","171"))->"虚拟运营商"
        iso=="HK"->"3HK"
        iso=="US"||iso=="CA"->OperatorDatabase.firstNameFor(iso)
        iso=="TH"->"AIS"
        iso=="JP"->"NTT Docomo"
        else->OperatorDatabase.firstNameFor(iso)
    }
}
