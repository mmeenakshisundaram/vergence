/*
Action name: Util-JDBC-Update-LossLocation
Application: Global
*/

var probe = new JavascriptProbe(inputs.MidServerName);
probe.setName("IMS_InsertAccidentInformation");
probe.setJavascript("var pdf = new FortegraUtil();res=pdf.insertAccidentInformation();");
var jsonContent = {};
jsonContent["ClaimId"] = parseInt(inputs.ClaimId);
jsonContent["Address1"] = inputs.Address1 == '' ? null :  inputs.Address1;
jsonContent["City"] = inputs.City == '' ? null :  inputs.City;
jsonContent["State"] = inputs.State == '' ? null :  inputs.State;
jsonContent["ZipCode"] =inputs.ZipCode == '' ? null :  inputs.ZipCode;
jsonContent["ISOCountryCode"] =  inputs.ISOCountryCode == '' ? null :  inputs.ISOCountryCode;
jsonContent["AccidentDescription"] = inputs.AccidentDescription == '' ? null :  inputs.AccidentDescription;
jsonContent["AccidentTime"] = inputs.AccidentTime == '' ? null :  inputs.AccidentTime;
jsonContent["County"] = inputs.County == '' ? null :  inputs.County;
jsonContent["AccidentTypeId"] = inputs.AccidentTypeId == '' ? null : parseInt(inputs.AccidentTypeId);
outputs.request = JSON.stringify(jsonContent);
probe.addParameter("requestBody", JSON.stringify(jsonContent));
var strOutputEccId = probe.create();

var waitMS = 2500000;
var start = new GlideDateTime;
var resp = new GlideRecord("ecc_queue");
resp.addQuery("response_to", strOutputEccId);
resp.addQuery("queue", "input");
do{
	resp.query();
	resp.next();
	gs.sleep(1000);
	if (GlideDateTime.subtract(start, new GlideDateTime()).getNumericValue() > waitMS) {
		break;
	}
} while(!resp.sys_id);

if(resp.payload != "<see_attachment/>"){
	gs.info("No Attachment - "+ resp.payload);
}
else{
	var SysAttachment = Packages.com.glide.ui.SysAttachment;
	var sa = new SysAttachment();
	var payload = sa.get(resp, "payload");
	gs.info("Attachment -"+ resp.payload);
}
outputs.resp_payload = resp.payload;