/*
Action name: Util-JDBC-VoidPayment
Application: Global
input: IMSConnectionString, respayid, userguid, midservername
output: Resp_Payload
*/

var probe = new JavascriptProbe(inputs.midservername);
probe.setName("IMS_voidPayment");
probe.setJavascript("var pdf = new FortegraUtil(); res = pdf.voidPayment();");
probe.addParameter("imsConnectionString",inputs.imsConnectionString);
probe.addParameter("respayid", inputs.respayid );
probe.addParameter("claimid", inputs.claimid );
probe.addParameter("claimantguid", inputs.claimantguid );
probe.addParameter("userguid", inputs.userguid);
var strOutputEccId = probe.create();

var waitMS = 250000;
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