/*
Action name: Util-JDBC-InsertPayment
Application: Global
*/

var probe = new JavascriptProbe(inputs.MidServerName);
probe.setName("IMS_InsertPayment");
probe.setJavascript("var pdf = new FortegraUtil();res=pdf.insertClaimPayment();");
var jsonContent = {};
jsonContent["ClaimId"] = parseInt(inputs.ClaimId);
jsonContent["ClaimantGuid"] = inputs.ClaimantGuid;
jsonContent["CoverageTypeId"] = inputs.CoverageTypeId == '' ? null : parseInt(inputs.CoverageTypeId);
jsonContent["CoverageTypeDescriptionId"] = inputs.CoverageTypeDescriptionId == '' ? null : parseInt(inputs.CoverageTypeDescriptionId);
jsonContent["ResPayTypeId"] = parseInt(inputs.ResPayTypeId);
jsonContent["ResPaySubTypeId"] =  inputs.ResPaySubTypeId == '' ? null : parseInt(inputs.ResPaySubTypeId);
jsonContent["ResPayAmount"] = String(inputs.ResPayAmount);
jsonContent["CreatedByGuid"] = inputs.CreatedByGuid == '' ? null : inputs.CreatedByGuid;
jsonContent["Comments"] = inputs.Comments == '' ? null : inputs.Comments;
jsonContent["PayeeGuid"] = inputs.PayeeGuid == '' ? null : inputs.PayeeGuid;
jsonContent["PayeeName"] = inputs.PayeeName == '' ? null : inputs.PayeeName;
jsonContent["IsPayeeClaimant"] = parseInt(inputs.IsPayeeClaimant);
jsonContent["IsPayeeInsured"] = parseInt(inputs.IsPayeeInsured);
jsonContent["AdditionalPayees"] = null;
jsonContent["Override_Address1"] = null;
jsonContent["Override_Address2"] = null;
jsonContent["Override_City"] = null;
jsonContent["Override_State"] = null;
jsonContent["Override_ZipCode"] = null;
jsonContent["Override_ISOCountryCode"] = null;
jsonContent["dateCreated"] = inputs.DateCreated == '' ? null : inputs.DateCreated;
jsonContent["PaymentResPayId"] =  inputs.PaymentResPayId == '' ? null : parseInt(inputs.PaymentResPayId);
jsonContent["IsPayeeDefenseAttorney"] = parseInt(inputs.IsPayeeDefenseAttorney);
jsonContent["IsPayeeClaimantAttorney"] = parseInt(inputs.IsPayeeClaimantAttorney);
jsonContent["ChildLineGUID"] = inputs.ChildLineGUID == '' ? null :inputs.ChildLineGUID;
jsonContent["PaymentType"] = inputs.PaymentType == '' ? null : parseInt(inputs.PaymentType);
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