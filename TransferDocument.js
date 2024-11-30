var probe = new JavascriptProbe("test_mid_server");
probe.setName("test");
probe.setJavascript("var pdf = new FortegraUtil(); res = pdf.transferdocument();");
probe.addParameter("documentStoreGUID", "6D720FC5-4F3A-4F24-BC57-C305CC6CF9B0");
probe.addParameter("imsConnectionString", "jdbc:sqlserver://MGADS0002-NJ.NY.MGASYSTEMS.COM:1433;"
                        + "database=Fortegra;"
                        + "user=muthukumar.meenakshisundaram@augustahitech.com;"
                        + "password=the.tooN0.swore6.a.sign.hugs.the.noodle;");
probe.addParameter("sharepointToken", "eyJ0eXAiOiJKV1QiLCJub25jZSI6IlU5dDhTYVYwT1pIS0ROUTZZMmE5Y1dBeFJVQ1RJNWdnTll1bUxKS1ZkeFEiLCJhbGciOiJSUzI1NiIsIng1dCI6Inp4ZWcyV09OcFRrd041R21lWWN1VGR0QzZKMCIsImtpZCI6Inp4ZWcyV09OcFRrd041R21lWWN1VGR0QzZKMCJ9.eyJhdWQiOiJodHRwczovL2dyYXBoLm1pY3Jvc29mdC5jb20iLCJpc3MiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC9lNDllOTg2My0wZjJlLTQ4NjAtODkxYi00ODIyMWI2NzRkYzIvIiwiaWF0IjoxNzMyNzc1NTA5LCJuYmYiOjE3MzI3NzU1MDksImV4cCI6MTczMjc3OTQwOSwiYWlvIjoiazJCZ1lFaFhMdTdhbjN6Ymg5OUhaWGVJZXQ1ZUFBPT0iLCJhcHBfZGlzcGxheW5hbWUiOiJTUFNlYXJjaCBTZXJ2aWNlTm93IiwiYXBwaWQiOiIwODUyNDlkZS1jNmE3LTRmNTAtOTVlOC01YTViM2JlMDljNjQiLCJhcHBpZGFjciI6IjEiLCJpZHAiOiJodHRwczovL3N0cy53aW5kb3dzLm5ldC9lNDllOTg2My0wZjJlLTQ4NjAtODkxYi00ODIyMWI2NzRkYzIvIiwiaWR0eXAiOiJhcHAiLCJvaWQiOiI4MDBkZGU5OS05OTY0LTRjYmItYWRhOC1jMDA4MDJjMTRmYjkiLCJyaCI6IjEuQVZzQVk1aWU1QzRQWUVpSkcwZ2lHMmROd2dNQUFBQUFBQUFBd0FBQUFBQUFBQUJiQUFCYkFBLiIsInJvbGVzIjpbIkZpbGVzLlJlYWRXcml0ZS5BcHBGb2xkZXIiLCJTaXRlcy5SZWFkLkFsbCIsIkZpbGVzLlJlYWRXcml0ZS5BbGwiLCJGaWxlcy5SZWFkLkFsbCJdLCJzdWIiOiI4MDBkZGU5OS05OTY0LTRjYmItYWRhOC1jMDA4MDJjMTRmYjkiLCJ0ZW5hbnRfcmVnaW9uX3Njb3BlIjoiTkEiLCJ0aWQiOiJlNDllOTg2My0wZjJlLTQ4NjAtODkxYi00ODIyMWI2NzRkYzIiLCJ1dGkiOiJINmRIY1lEQUYwbVU2TmNPVkxvTkFBIiwidmVyIjoiMS4wIiwid2lkcyI6WyIwOTk3YTFkMC0wZDFkLTRhY2ItYjQwOC1kNWNhNzMxMjFlOTAiXSwieG1zX2lkcmVsIjoiMjIgNyIsInhtc190Y2R0IjoxMzE1MzIzMDIwfQ.kq13JRPh1iBIO7HLj34sxgDDhxOaDd54Dm9evhhm2922KW6wskv1AqEfCY4hJzf6sVPP3BpMeOnvRfz9Y0uZmYiDhc35VzhDpGUugjGBmhqQnZHtc8AoEeREU6xU0dgLTsEtk3XUB8ohVaCkYsdsftGEIcDIAmF651Vi-12NmeaBw-Y1je4tWEJA4cwY-flJrabLShtxlymZctUDsIR64HXBVw5jjPK9PXIVItCRyKGzdgQ05XMaWtHuxtuScFfw_Oy22dRUTr5PALHYZb9bdCbJwd2MnFYxv_dZTvBRDeUTZNQ_Hxa02Z10FnP7gXPMlX5Agtok2MYQ6qdKmL59MQ");
probe.addParameter("folderName", "abc/pdf");
probe.addParameter("driveId", "b!AXAKxzJkf02Shd-28ta_4kxl-h-3llJFvPvbCNsmniWUXHmML35hQZKP5y87-lge");
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