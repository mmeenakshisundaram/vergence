var probe = new JavascriptProbe("test_mid_server");
probe.setName("test");
probe.setJavascript("var pdf = new FortegraUtil(); res = pdf.getAccessToken();");
probe.addParameter("granttype", "client_credentials");
probe.addParameter("clientid", "085249de-c6a7-4f50-95e8-5a5b3be09c64");
probe.addParameter("clientSecret", "eKR8Q~Oo9wfEjZ8hUEUV4D2Z-zaCa1IFsRXZXcdj");
probe.addParameter("scope", "https://graph.microsoft.com/.default");
probe.addParameter("tenant", "e49e9863-0f2e-4860-891b-48221b674dc2");
var strOutputEccId = probe.create();
gs.info(strOutputEccId);

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