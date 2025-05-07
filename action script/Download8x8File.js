/*
Action name: Util-JDBC-VoidPayment
Application: Global
input: IMSConnectionString, respayid, userguid, midservername
output: Resp_Payload
*/

var probe = new JavascriptProbe(inputs.midservername);
probe.setName("8x8_Download8x8File");
probe.setJavascript("var pdf = new FortegraUtil(); res = pdf.Download8x8File();");
probe.addParameter("objectid", inputs.objectid );
probe.addParameter("authtoken", inputs.authtoken );
probe.addParameter("odate", inputs.odate );
var strOutputEccId = probe.create();
outputs.resp_payload = strOutputEccId;