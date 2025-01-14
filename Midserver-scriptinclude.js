/*
Javascript proxy for the midserver java library
*/

var FortegraUtil = Class.create();

FortegraUtil.prototype = {

    initialize: function() {
		this.PaymentRepo = Packages.repository.PaymentRepository;
		this.Pgp = Packages.util.CommonUtil;
		this.ClaimRepo = Packages.repository.ClaimRepository;
    },

    getAccessToken: function() {
		this.granttype = probe.getParameter("granttype");
		this.clientid = probe.getParameter("clientid");
		this.clientSecret = probe.getParameter("clientSecret");
		this.scope = probe.getParameter("scope");
		this.tenant = probe.getParameter("tenant");
        var pgpObj = new this.Pgp().getAccessToken(this.granttype,this.clientid,this.clientSecret,this.scope,this.tenant);
		return pgpObj;
    },

	insertClaimPayment: function() {
		this.input = probe.getParameter("requestBody");
		var pgpObj = new this.PaymentRepo("test").insertClaimPayment(this.input);
		return pgpObj;
    },

	transferdocument: function() {
		this.documentStoreGUID = probe.getParameter("documentStoreGUID");
		this.imsConnectionString = probe.getParameter("imsConnectionString");
		this.sharepointToken = probe.getParameter("sharepointToken");
		this.folderName = probe.getParameter("folderName");
		this.driveId = probe.getParameter("driveId");
        var pgpObj = new this.Pgp().transferdocument(this.documentStoreGUID,
													this.imsConnectionString,
													this.sharepointToken,
													this.folderName,
													this.driveId);
		return pgpObj;
    },

	updateAccidentInformation: function() {
		this.input = probe.getParameter("requestBody");
		var pgpObj = new this.ClaimRepo("test").updateAccidentInformation(this.input);
		return pgpObj;
    },

    insertAccidentInformation: function() {
    		this.input = probe.getParameter("requestBody");
    		var pgpObj = new this.ClaimRepo("test").insertAccidentInformation(this.input);
    		return pgpObj;
        },

	// transferPayment: function() {

    //     this.imsConnectionString = probe.getParameter("imsConnectionString");
    //     this.respayid = probe.getParameter("respayid");
    //     this.userguid = probe.getParameter("userguid");
    //     var pgpObj = new this.Pgp().invoke_spClaims_TransferPayment(
    //                                                 this.imsConnectionString,
    //                                                 this.respayid,
    //                                                 this.userguid);
    //     return pgpObj;
    // },

    type: FortegraUtil
};