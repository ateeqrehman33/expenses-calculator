package attitude.designs.myexpen;


class  SmsModel{



    var msgType: String? = null
    var msgAmt: String? = null
    var msgDate: String? = null
    var msgBal: String? = null




    constructor(msgType: String, msgAmt: String?, msgDate: String?, msgBal: String) {

        this.msgType = msgType
        this.msgAmt = msgAmt
        this.msgDate = msgDate
        this.msgBal = msgBal


    }
}