/**
 *  Raspberry Pi
 *
 *  Copyright 2017 Todd Gould
 *
 *  Monitor & control your Raspberry Pi using SmartThings and WebIOPi <https://code.google.com/p/webiopi/>
 *
 *  Companion WebIOPi python script can be found here:
 *  <https://github.com/nicholaswilde/smartthings/blob/master/device-types/raspberry-pi/raspberrypi.py>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
import groovy.json.JsonSlurper

preferences {
        input("ip", "string", title:"IP Address", description: "192.168.1.150", required: true, displayDuringSetup: true)
        input("port", "string", title:"Port", description: "8000", defaultValue: 8000 , required: true, displayDuringSetup: true)
        input("username", "string", title:"Username", description: "webiopi", required: true, displayDuringSetup: true)
        input("password", "password", title:"Password", description: "Password", required: true, displayDuringSetup: true)
        input("door1Name", "string", title:"Door 1 Name", description: "Tundra or Left", required: true, displayDuringSetup: true)
        input("door2Name", "string", title:"Door 2 Name", description: "Tundra or Left", required: true, displayDuringSetup: true)
        input("door3Name", "string", title:"Door 3 Name", description: "Tundra or Left", required: true, displayDuringSetup: true)
        input("door1gpio", "string", title:"Door 1 GPIO", description: "21", required: true, displayDuringSetup: true)
        input("door2gpio", "string", title:"Door 2 GPIO", description: "21", required: true, displayDuringSetup: true)
        input("door3gpio", "string", title:"Door 3 GPIO", description: "21", required: true, displayDuringSetup: true)
        input("ledgpio", "string", title:"LED GPIO", description: "7", required: true, displayDuringSetup: true)
}

metadata {
	definition (name: "garagePi", namespace: "tgould", author: "Todd Gould") {
		capability "Polling"
		capability "Refresh"
		capability "Temperature Measurement"
        capability "Switch"
        capability "Momentary"
        capability "Sensor"
        capability "Actuator"        
        
        attribute "cpuPercentage", "string"
        attribute "memory", "string"
        attribute "diskUsage", "string"
        
        command "restart"
        command "push1"
        command "push2"
        command "push3"
        command "ledOn"
        command "ledOff"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles (scale: 2){
		standardTile("button", "device.switch", width: 6, height: 4, canChangeIcon: true) {
			state "off", label: 'Off', icon: "st.Electronics.electronics18", backgroundColor: "#ffffff", nextState: "on"
			state "on", label: 'On', icon: "st.Electronics.electronics18", backgroundColor: "#79b821", nextState: "off"
		}
        
        standardTile("door1", "device.door1", width: 2, height: 2, canChangeIcon: true) {
			state "on", label:"Pickup", action:"push1", icon:"st.doors.garage.garage-opening", backgroundColor:"#79b821"
			state "off", label:"Pickup", action:"push1", icon:"st.doors.garage.garage-closing", backgroundColor:"#ffffff"
		}
        standardTile("door2", "device.door2", width: 2, height: 2, canChangeIcon: true) {
			state "on", label:"Tundra", action:"push2", icon:"st.doors.garage.garage-opening", backgroundColor:"#79b821"
			state "off", label:"Tundra", action:"push2", icon:"st.doors.garage.garage-closing", backgroundColor:"#ffffff"
		}
        standardTile("door3", "device.door3", width: 2, height: 2, canChangeIcon: true) {
			state "on", label:'Sequoia', action:"push3", icon:"st.doors.garage.garage-opening", backgroundColor:"#79b821"
			state "off", label:'Sequoia', action:"push3", icon:"st.doors.garage.garage-closing", backgroundColor:"#ffffff"
		}
        
        valueTile("temperature", "device.temperature", width: 2, height: 2) {
            state "temperature", label:'${currentValue}° CPU', unit: "C",
            backgroundColors:[
                [value: 25, color: "#153591"],
                [value: 35, color: "#1e9cbb"],
                [value: 47, color: "#90d2a7"],
                [value: 59, color: "#44b621"],
                [value: 67, color: "#f1d801"],
                [value: 76, color: "#d04e00"],
                [value: 77, color: "#bc2323"]
            ]
        }
        
        valueTile("cpuPercentage", "device.cpuPercentage", , width: 2, height: 2, inactiveLabel: false) {
        	state "default", label:'${currentValue}% CPU', unit:"Percentage",
            backgroundColors:[
                [value: 31, color: "#153591"],
                [value: 44, color: "#1e9cbb"],
                [value: 59, color: "#90d2a7"],
                [value: 74, color: "#44b621"],
                [value: 84, color: "#f1d801"],
                [value: 95, color: "#d04e00"],
                [value: 96, color: "#bc2323"]
            ]
        }
        
        valueTile("memory", "device.memory", width: 2, height: 2) {
        	state "default", label:'${currentValue}% Mem', unit:"Percentage",
            backgroundColors:[
                [value: 31, color: "#153591"],
                [value: 44, color: "#1e9cbb"],
                [value: 59, color: "#90d2a7"],
                [value: 74, color: "#44b621"],
                [value: 84, color: "#f1d801"],
                [value: 95, color: "#d04e00"],
                [value: 96, color: "#bc2323"]
            ]
        }
        
        valueTile("diskUsage", "device.diskUsage", width: 2, height: 2) {
        	state "default", label:'${currentValue}% Disk', unit:"Percent",
            backgroundColors:[
                [value: 31, color: "#153591"],
                [value: 44, color: "#1e9cbb"],
                [value: 59, color: "#90d2a7"],
                [value: 74, color: "#44b621"],
                [value: 84, color: "#f1d801"],
                [value: 95, color: "#d04e00"],
                [value: 96, color: "#bc2323"]
            ]
        }
        
        standardTile("led", "device.led", width: 2, height: 2, canChangeIcon: true) {
			state "off", action:"ledOn", label: '${currentValue}', icon: "st.illuminance.illuminance.light", backgroundColor: "#ffffff", nextState: "on"
			state "on", action:"ledOff", label: '${currentValue}', icon: "st.illuminance.illuminance.light", backgroundColor: "#00a0dc", nextState: "off"
		}
        
        standardTile("refresh", "device.refresh", width: 2, height: 2, inactiveLabel: false) {
        	state "default", action:"refresh.refresh", icon: "st.secondary.refresh", backgroundColor: "#79b821"
        }
        
        standardTile("restart", "device.restart", width: 2, height: 2, inactiveLabel: false) {
        	state "default", action:"restart", label: "Restart", displayName: "Restart", icon: "st.samsung.da.RC_ic_power", backgroundColor: "#e86d13"
        }
        
        main "button"
        details(["button", "door1", "door2", "door3", "temperature", "cpuPercentage", "memory", "led", "refresh", "restart"])
    }
}

// ------------------------------------------------------------------


def parse(description) {
    def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)
	//log.debug "Status: ${status}"
    //log.debug "Body: ${body}"
    //log.debug "JSON: ${json}"
    //log.debug "Data: ${data}"
    
    if (status == 200){
        log.debug "Computer is up"
        sendEvent(name: "switch", value: "on")
        sendEvent(name: "door1", value: "on", isStateChange: true)
        sendEvent(name: "door2", value: "on", isStateChange: true)
        sendEvent(name: "door3", value: "on", isStateChange: true)
    }
    
    if (body?.startsWith("{")) {
        def slurper = new JsonSlurper()
        def result = slurper.parseText(body)
        //log.debug "result: ${result}"
        
        if (result.containsKey("cpu_temp")) {
            log.debug "cpu_temp: ${cpu_temp}"
            sendEvent(name: "temperature", value: result.cpu_temp)
        }

        if (result.containsKey("cpu_perc")) {
            log.debug "cpu_perc: ${result.cpu_perc}"
            sendEvent(name: "cpuPercentage", value: result.cpu_perc)
        }

        if (result.containsKey("mem_avail")) {
            log.debug "mem_avail: ${result.mem_avail}"
            sendEvent(name: "memory", value: result.mem_avail)
        }
        if (result.containsKey("disk_usage")) {
            log.debug "disk_usage: ${result.disk_usage}"
            sendEvent(name: "diskUsage", value: result.disk_usage)
        }
    }
}



// handle commands
def poll() {
	log.debug "Executing 'poll'"
    sendEvent(name: "switch", value: "off")
    getRPiData()
}

def refresh() {
	log.debug "Executing 'refresh'"
    sendEvent(name: "switch", value: "off", isStateChange: true)
    getRPiData()
}

def restart() {
	log.debug "Restart was pressed"
    sendEvent(name: "switch", value: "off", isStateChange: true)
    def uri = "/macros/reboot"
    postAction(uri, "POST")
}

def push1() {
    push("door1", door1gpio, door1Name)
}

def push2() {
    push("door2", door2gpio, door2Name)	
}

def push3() {
    push("door3", door3gpio, door3Name)
}

def push(device, GPIO, Name) {
	log.debug "Pushed ${GPIO} | ${Name}"
    def uri = "/GPIO/" + GPIO + "/value/0"
    log.debug "URI: ${uri}"
    postAction(uri, "POST")
    //sendEvent(name: device, value: "on", isStateChange: true)
}

// Implemented the switch release in a python script in webiopi

def ledOn() {
	sendEvent(name: "led", value: "on")
    def uri = "/GPIO/" + ledgpio + "/value/1"
    log.debug "URI: ${uri}"
    postAction(uri, "POST")
}

def ledOff() {
	sendEvent(name: "led", value: "off")
    def uri = "/GPIO/" + ledgpio + "/value/0"
    log.debug "URI: ${uri}"
    postAction(uri, "POST")
}

// Get CPU percentage reading
private getRPiData() {
	log.debug "GET Macro Raspberry Pi Stats"
    def uri = "/macros/getData"
    postAction(uri, "POST")
}

private getGPIO() {
	log.debug "GET GPIO Status"
    def uri = "/*"
    postAction(uri, "GET")
}
// ------------------------------------------------------------------

private postAction(uri, method){
  setDeviceNetworkId(ip, port)  
  
  def userpass = encodeCredentials(username, password)
  
  def headers = getHeader(userpass)
  
  def hubAction = new physicalgraph.device.HubAction(
    method: method,
    path: uri,
    headers: headers
  )
  log.debug("Executing hubAction on " + getHostAddress())
  log.debug hubAction
  return hubAction    
}

// ------------------------------------------------------------------
// Helper methods
// ------------------------------------------------------------------

def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
		map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
	}
}

private encodeCredentials(username, password){
	log.debug "Encoding credentials"
	def userpassascii = "${username}:${password}"
    def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    //log.debug "ASCII credentials are ${userpassascii}"
    //log.debug "Credentials are ${userpass}"
    return userpass
}

private getHeader(userpass){
	log.debug "Getting headers"
    def headers = [:]
    headers.put("HOST", getHostAddress())
    headers.put("Authorization", userpass)
    log.debug "Headers are ${headers}"
    return headers
}

private delayAction(long time) {
	new physicalgraph.device.HubAction("delay $time")
}

private setDeviceNetworkId(ip,port){
  	def iphex = convertIPtoHex(ip)
  	def porthex = convertPortToHex(port)
  	device.deviceNetworkId = "$iphex:$porthex"
  	log.debug "Device Network Id set to ${iphex}:${porthex}"
}

private getHostAddress() {
	return "${ip}:${port}"
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    return hex

}

private String convertPortToHex(port) {
	String hexport = port.toString().format( '%04x', port.toInteger() )
    return hexport
}