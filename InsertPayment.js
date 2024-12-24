/*
Action name: Util-JDBC-InsertPayment
Application: Global
*/

var probe = new JavascriptProbe(inputs.midservername);
probe.setName("IMS_InsertPayment");
probe.setJavascript("var pdf = new FortegraUtil(); res = pdf.insertPayment();");
probe.addParameter("ClaimId", inputs.ClaimId );
probe.addParameter("ClaimantGuid", inputs.ClaimantGuid );
probe.addParameter("CoverageTypeId", inputs.CoverageTypeId );
probe.addParameter("CoverageTypeDescriptionId", inputs.CoverageTypeDescriptionId);
probe.addParameter("ResPayTypeId", inputs.ResPayTypeId);
probe.addParameter("ResPaySubTypeId", inputs.ResPaySubTypeId);
probe.addParameter("ResPayAmount", inputs.ResPayAmount);
probe.addParameter("CreatedByGuid", inputs.CreatedByGuid);
probe.addParameter("Comments", inputs.Comments);
probe.addParameter("PayeeGuid", inputs.PayeeGuid);
probe.addParameter("PayeeName", inputs.PayeeName);
probe.addParameter("IsPayeeClaimant", inputs.IsPayeeClaimant);
probe.addParameter("IsPayeeInsured", inputs.IsPayeeInsured);
probe.addParameter("AdditionalPayees", null);
probe.addParameter("Override_Address1", null);
probe.addParameter("Override_Address2", null);
probe.addParameter("Override_City",null);
probe.addParameter("Override_State", null);
probe.addParameter("Override_ZipCode", null);
probe.addParameter("Override_ISOCountryCode", null);
probe.addParameter("date", inputs.DateCreated);
probe.addParameter("PaymentResPayId", inputs.PaymentResPayId);
probe.addParameter("IsPayeeDefenseAttorney", inputs.IsPayeeDefenseAttorney);
probe.addParameter("IsPayeeClaimantAttorney", inputs.IsPayeeClaimantAttorney);
probe.addParameter("ChildLineGUID", inputs.ChildLineGUID);
probe.addParameter("PaymentType", inputs.PaymentType);
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