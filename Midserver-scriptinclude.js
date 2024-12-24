/*
Javascript proxy for the midserver java library
*/

var FortegraUtil = Class.create();

FortegraUtil.prototype = {

    initialize: function() {
        this.Pgp = Packages.util.CommonUtil;
        this.Pgp_PaymentRepo = Packages.repository.PaymentRepository;
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

    insertPayment: function() {
            this.ClaimId = probe.getParameter("ClaimId");
            this.ClaimantGuid = probe.getParameter("ClaimantGuid");
            this.CoverageTypeId = probe.getParameter("CoverageTypeId");
            this.CoverageTypeDescriptionId = probe.getParameter("CoverageTypeDescriptionId");
            this.ResPayTypeId = probe.getParameter("ResPayTypeId");
            this.ResPaySubTypeId = probe.getParameter("ResPaySubTypeId");
            this.ResPayAmount = probe.getParameter("ResPayAmount");
            this.CreatedByGuid = probe.getParameter("CreatedByGuid");
            this.Comments = probe.getParameter("Comments");
            this.PayeeGuid = probe.getParameter("PayeeGuid");
            this.PayeeName = probe.getParameter("PayeeName");
            this.IsPayeeClaimant = probe.getParameter("IsPayeeClaimant");
            this.IsPayeeInsured = probe.getParameter("IsPayeeInsured");
            this.AdditionalPayees = probe.getParameter("AdditionalPayees");
            this.Override_Address1 = probe.getParameter("Override_Address1");
            this.Override_Address2 = probe.getParameter("Override_Address2");
            this.Override_City = probe.getParameter("Override_City");
            this.Override_State = probe.getParameter("Override_State");
            this.Override_ZipCode = probe.getParameter("Override_ZipCode");
            this.Override_ISOCountryCode = probe.getParameter("Override_ISOCountryCode");
            this.date = probe.getParameter("date");
            this.PaymentResPayId = probe.getParameter("PaymentResPayId");
            this.IsPayeeDefenseAttorney = probe.getParameter("IsPayeeDefenseAttorney");
            this.IsPayeeClaimantAttorney = probe.getParameter("IsPayeeClaimantAttorney");
            this.ChildLineGUID = probe.getParameter("ChildLineGUID");
            this.PaymentType = probe.getParameter("PaymentType");
            var pgpObj = new this.Pgp_PaymentRepo("test").insert_Claim_Payment(
                                                        this.ClaimId,
                                                        this.ClaimantGuid,
                                                        this.CoverageTypeId,
                                                        this.CoverageTypeDescriptionId,
                                                        this.ResPayTypeId,
                                                        this.ResPaySubTypeId,
                                                        this.ResPayAmount,
                                                        this.CreatedByGuid,
                                                        this.Comments,
                                                        this.PayeeGuid,
                                                        this.PayeeName,
                                                        this.IsPayeeClaimant,
                                                        this.IsPayeeInsured,
                                                        this.AdditionalPayees,
                                                        this.Override_Address1,
                                                        this.Override_Address2,
                                                        this.Override_City,
                                                        this.Override_State,
                                                        this.Override_ZipCode,
                                                        this.Override_ISOCountryCode,
                                                        this.date,
                                                        this.PaymentResPayId,
                                                        this.IsPayeeDefenseAttorney,
                                                        this.IsPayeeClaimantAttorney,
                                                        this.ChildLineGUID,
                                                        this.PaymentType);
            return pgpObj;
    },

//    transferPayment: function() {
//
//        this.imsConnectionString = probe.getParameter("imsConnectionString");
//        this.respayid = probe.getParameter("respayid");
//        this.userguid = probe.getParameter("userguid");
//        var pgpObj = new this.Pgp().invoke_spClaims_TransferPayment(
//                                                    this.imsConnectionString,
//                                                    this.respayid,
//                                                    this.userguid);
//        return pgpObj;
//    },

    voidPayment: function() {
            this.imsConnectionString = probe.getParameter("imsConnectionString");
            this.respayid = probe.getParameter("respayid");
            this.claimid = probe.getParameter("claimid");
            this.claimantguid = probe.getParameter("claimantguid");
            this.userguid = probe.getParameter("userguid");
            var pgpObj = new this.Pgp_PaymentRepo().invoke_spClaims_InsertVoidPayment(
                                                        this.imsConnectionString,
                                                        this.respayid,
                                                        this.claimid,
                                                        this.claimantguid,
                                                        this.userguid);
            return pgpObj;
    },

    cleanMidServer: function() {
            this.foldername = probe.getParameter("foldername");
            this.isroot = probe.getParameter("isroot");
            var pgpObj = new this.Pgp().cleanMidServer(this.foldername,this.isroot);
            return pgpObj;
    },

    type: FortegraUtil
};