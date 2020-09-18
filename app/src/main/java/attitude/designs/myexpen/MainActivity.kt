package attitude.designs.myexpen

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chipset.pieviewgroup.ChartTypes
import com.chipset.pieviewgroup.PieViewGroup
import java.math.BigDecimal
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {



    var inc : TextView ? = null
    var exp : TextView ? = null



    var monthSpinner : Spinner? = null
    var pieChart : PieViewGroup ? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        inc  = findViewById(R.id.inc)
        exp = findViewById(R.id.exp)

        monthSpinner = findViewById(R.id.appCompatSpinner2)
        pieChart  = findViewById(R.id.myPie)


        requestSmsPermission()







    }





    private fun requestSmsPermission() {
        val permission = Manifest.permission.READ_SMS
        val grant = ContextCompat.checkSelfPermission(this, permission)
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permission_list = arrayOfNulls<String>(1)
            permission_list[0] = permission
            ActivityCompat.requestPermissions(this, permission_list, 1)
        }

        else{

            fetchInbox(15)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                fetchInbox(15)


            } else {
                Toast.makeText(this, "permission not granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }



    fun setupPieChart(translist : ArrayList<SmsModel> ){


        var totalexpenses = BigDecimal.ONE
        var totalincome = BigDecimal.ONE

        for (i in 0 until translist.size) {

            println("null of a i9 : "+translist.get(i).msgDate)


            if(translist.get(i).msgType?.contains("credited")!!){


                  totalincome = totalincome + translist.get(i).msgAmt?.toBigDecimalOrNull()!!




          }

          else if(translist.get(i).msgType!!.contains("debited")){



              totalexpenses = totalexpenses + translist.get(i).msgAmt?.toBigDecimalOrNull()!!

          }

    }

      println("null of a totalexpenses : "+totalexpenses)
      println("null of a totalincome : "+totalincome)


        var totinc = NumberFormat.getCurrencyInstance().format(totalincome)
        var totexp = NumberFormat.getCurrencyInstance().format(totalexpenses)



        inc?.setText("Total Income : "+totinc)

        exp?.setText("Total Expenses : "+totexp)



        pieChart?.showLabels(true);
        pieChart?.setChartType(ChartTypes.DONUT);

        val dataSource: MutableMap<String, Int> = HashMap()


          var ONE_THOUSAND =  BigDecimal(1000);

           var ONE_HUNDRED =  BigDecimal(100);



        if(totalexpenses>ONE_THOUSAND){

            dataSource["Total Income"] =  totalincome.divide(ONE_THOUSAND).toInt()
            dataSource["Total Expense"] = totalexpenses.divide(ONE_THOUSAND).toInt()

            }

            else if (totalexpenses>ONE_HUNDRED){


            dataSource["Total Income"] =  totalincome.divide(ONE_HUNDRED).toInt()
            dataSource["Total Expense"] = totalexpenses.divide(ONE_HUNDRED).toInt()
        }








        if (dataSource != null) {
            pieChart?.setData(dataSource)
        };
        pieChart?.build();


//        monthSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                fetchInbox(15)
//
//            }
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//
//                fetchInbox(position+1)
//
//
//            }

      //  }

    }



    fun fetchInbox(month : Int ){
        val sms = ArrayList<SmsModel>()
        val uriSms = Uri.parse("content://sms/inbox")
        val cursor: Cursor? = contentResolver.query(uriSms, arrayOf("_id", "address", "date", "body"), null, null, null)
        cursor?.moveToFirst()
        while (cursor?.moveToNext()!!) {
            val address: String = cursor.getString(1)
            var body: String = cursor.getString(3)
            var date : String = cursor.getString(2)


            body = body.toLowerCase();



            if((body.contains("credited")|| body.contains("debited") || body.contains("withdrawn")) && body.contains("available balance is")) {



                if((body.contains("debited for") && body.contains("credited to")) || ( body.contains("debited for") && body.contains("towards"))) {


                    if(month == 15){

                        sms.add(SmsModel("debited",getAmount(body),getDate(date.toLong()),""))




                    }

                    else {


                        val c = Calendar.getInstance()
//Set time in milliseconds
//Set time in milliseconds
                        c.timeInMillis = date.toLong()
                        val mYear = c[Calendar.YEAR]
                        val mMonth = c[Calendar.MONTH]
                        val mDay = c[Calendar.DAY_OF_MONTH]
                        val hr = c[Calendar.HOUR]
                        val min = c[Calendar.MINUTE]
                        val sec = c[Calendar.SECOND]


                        if(month == mMonth){

                            sms.add(SmsModel("debited",getAmount(body),getDate(date.toLong()),""))


                        }





                    }




                }

                else if(body.contains("credited for") && body.contains("towards") ){



                    println("credited  amt : "+getAmount(body)+ " address : " +address)



                    if(month == 15) {

                        sms.add(SmsModel("credited",getAmount(body),getDate(date.toLong()),""))


                    }

                    else{



                        val c = Calendar.getInstance()
//Set time in milliseconds
//Set time in milliseconds
                        c.timeInMillis = date.toLong()
                        val mYear = c[Calendar.YEAR]
                        val mMonth = c[Calendar.MONTH]
                        val mDay = c[Calendar.DAY_OF_MONTH]
                        val hr = c[Calendar.HOUR]
                        val min = c[Calendar.MINUTE]
                        val sec = c[Calendar.SECOND]


                        if(month == mMonth){

                            sms.add(SmsModel("credited",getAmount(body),getDate(date.toLong()),""))


                        }


                    }




                }

            }


//            msgType: String?,
//            msgAmt: String?,
//            msgDate: String?,
//            msgBal: String?




        }

        setupPieChart(sms)

        println("null of a i : "+sms.size)


    }


    // getting amount by matching the pattern
    // getting amount by matching the pattern
    open fun getAmount(data: String?): String? {
        // pattern - rs. **,***.**
        val pattern1 = "(inr)+[\\s]?+[0-9]*+[\\\\,]*+[0-9]*+[\\\\.][0-9]{2}"
        val regex1 = Pattern.compile(pattern1)
        // pattern - inr **,***.**
        val pattern2 = "(rs)+[\\\\.][\\s]?+[0-9]*+[\\\\,]*+[0-9]*+[\\\\.][0-9]{2}"
        val regex2 = Pattern.compile(pattern2)
        val matcher1 = regex1.matcher(data)
        val matcher2 = regex2.matcher(data)
        if (matcher1.find()) {
            try {
                var a = matcher1.group(0)
                a = a.replace("inr", "")
                a = a.replace(" ", "")
                a = a.replace(",", "")
                return a
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (matcher2.find()) {
            try {
                // searched for rs or inr preceding number in the form of rs. **,***.**
                var a = matcher2.group(0)
                a = a.replace("rs", "")
                a = a.replaceFirst(".".toRegex(), "")
                a = a.replace(" ", "")
                a = a.replace(",", "")
                return a
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }


    private fun getDate(milliSeconds: Long): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter: DateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}




