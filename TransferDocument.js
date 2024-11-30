/*
Action name: Util-JDBC
Application: Global
input: DocumentStoreGUID, IMSConnectionString, SharepointToken, FolderName, DriveId, MidServerName
output: Resp_Payload
*/

var probe = new JavascriptProbe(inputs.midservername);
probe.setName("IMS_Sharepoint_Integration");
probe.setJavascript("var pdf = new FortegraUtil(); res = pdf.transferdocument();");
probe.addParameter("documentStoreGUID", inputs.documentStoreGUID);
probe.addParameter("imsConnectionString",inputs.imsConnectionString);
probe.addParameter("sharepointToken", inputs.sharepointToken );
probe.addParameter("folderName", inputs.folderName);
probe.addParameter("driveId", inputs.driveId);
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