package com.medyas.itransfoapp

import org.json.JSONObject

class DeviceList {
     var device_name:String = ""
     var company_name:String = ""
     var device_ref:String = ""
     var device_uid:String = ""

    init {

    }

    constructor(name:String, company:String, ref:String, uid:String) {
        this.device_name = name
        this.company_name = company
        this.device_ref = ref
        this.device_uid = uid
    }


}