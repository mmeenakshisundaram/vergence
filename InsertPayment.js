/*
Action name: Util-JDBC-InsertPayment
Application: Global
*/

var probe = new JavascriptProbe(inputs.midservername);
probe.setName("IMS_InsertPayment");
probe.setJavascript("var pdf = new FortegraUtil(); res = pdf.insertPayment();");
probe.addParameter("ClaimId", inputs.respayid );
probe.addParameter("ClaimantGuid", inputs.claimid );
probe.addParameter("CoverageTypeId", inputs.claimantguid );
probe.addParameter("CoverageTypeDescriptionId", inputs.userguid);
probe.addParameter("ResPayTypeId", inputs.userguid);
probe.addParameter("ResPaySubTypeId", inputs.userguid);
probe.addParameter("ResPayAmount", inputs.userguid);
probe.addParameter("CreatedByGuid", inputs.userguid);
probe.addParameter("Comments", inputs.userguid);
probe.addParameter("PayeeGuid", inputs.userguid);
probe.addParameter("PayeeName", inputs.userguid);
probe.addParameter("IsPayeeClaimant", inputs.userguid);
probe.addParameter("IsPayeeInsured", inputs.userguid);
probe.addParameter("AdditionalPayees", inputs.userguid);
probe.addParameter("Override_Address1", inputs.userguid);
probe.addParameter("Override_Address2", inputs.userguid);
probe.addParameter("Override_City", inputs.userguid);
probe.addParameter("Override_State", inputs.userguid);
probe.addParameter("Override_ZipCode", inputs.userguid);
probe.addParameter("Override_ISOCountryCode", inputs.userguid);
probe.addParameter("date", inputs.userguid);
probe.addParameter("PaymentResPayId", inputs.userguid);
probe.addParameter("IsPayeeDefenseAttorney", inputs.userguid);
probe.addParameter("IsPayeeClaimantAttorney", inputs.userguid);
probe.addParameter("ChildLineGUID", inputs.userguid);
probe.addParameter("PaymentType", inputs.userguid);
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