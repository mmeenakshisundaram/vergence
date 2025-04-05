package repository;

import org.json.JSONArray;
import org.json.JSONObject;
import util.CommonUtil;

import java.io.IOException;
import java.lang.ref.Reference;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class PaymentRepository {

    String imsConnectionString = "";

    /*
     Constructor
     */
    public PaymentRepository(String env){
        imsConnectionString = CommonUtil.appConfig.getOrDefault(env+"_imsconnectionstring","na");
    }

    /*
     Inserts claim payment
     */
    public String insertClaimPayment(String input){
        JSONObject inputObj = new JSONObject(input);
        Integer ClaimId = inputObj.get("ClaimId") == JSONObject.NULL ? null: (Integer)inputObj.get("ClaimId");
        String ClaimantGuid = inputObj.get("ClaimantGuid") == JSONObject.NULL ? null: (String)inputObj.get("ClaimantGuid");
        Integer CoverageTypeId = inputObj.get("CoverageTypeId") == JSONObject.NULL ? null: (Integer)inputObj.get("CoverageTypeId");
        Integer CoverageTypeDescriptionId= inputObj.get("CoverageTypeDescriptionId") == JSONObject.NULL ? null: (Integer)inputObj.get("CoverageTypeDescriptionId");
        Integer ResPayTypeId= inputObj.get("ResPayTypeId") == JSONObject.NULL ? null:  (Integer)inputObj.get("ResPayTypeId");
        Integer ResPaySubTypeId = inputObj.get("ResPaySubTypeId") == JSONObject.NULL ? null: (Integer)inputObj.get("ResPaySubTypeId");
        BigDecimal ResPayAmount = BigDecimal.valueOf(Double.valueOf((String)inputObj.get("ResPayAmount")));
        String CreatedByGuid= inputObj.get("CreatedByGuid") == JSONObject.NULL ? null: (String)inputObj.get("CreatedByGuid");
        String Comments = inputObj.get("Comments") == JSONObject.NULL ? null: (String)inputObj.get("Comments");
        String PayeeGuid= inputObj.get("PayeeGuid") == JSONObject.NULL ? null: (String)inputObj.get("PayeeGuid");
        String PayeeName= inputObj.get("PayeeName") == JSONObject.NULL ? null: (String)inputObj.get("PayeeName");
        Integer IsPayeeClaimant=(Integer) inputObj.get("IsPayeeClaimant");
        Integer IsPayeeInsured=(Integer) inputObj.get("IsPayeeInsured");
        String AdditionalPayees = inputObj.get("AdditionalPayees") == JSONObject.NULL ? null: (String)inputObj.get("AdditionalPayees");
        String Override_Address1 = inputObj.get("Override_Address1") == JSONObject.NULL ? null: (String)inputObj.get("Override_Address1");
        String Override_Address2 = inputObj.get("Override_Address2") == JSONObject.NULL ? null: (String)inputObj.get("Override_Address2");
        String Override_City = inputObj.get("Override_City") == JSONObject.NULL ? null: (String)inputObj.get("Override_City");
        String Override_State = inputObj.get("Override_State") == JSONObject.NULL ? null: (String)inputObj.get("Override_State");
        String Override_ZipCode = inputObj.get("Override_ZipCode") == JSONObject.NULL ? null: (String)inputObj.get("Override_ZipCode");
        String Override_ISOCountryCode = inputObj.get("Override_ISOCountryCode") == JSONObject.NULL ? null: (String)inputObj.get("Override_ISOCountryCode");
        Timestamp date = inputObj.get("dateCreated") == JSONObject.NULL ? null: Timestamp.valueOf((String)inputObj.get("dateCreated"));
        Integer PaymentResPayId = inputObj.get("PaymentResPayId") == JSONObject.NULL ? null: (Integer)inputObj.get("PaymentResPayId");
        Integer IsPayeeDefenseAttorney=(Integer) inputObj.get("IsPayeeDefenseAttorney");
        Integer IsPayeeClaimantAttorney=(Integer) inputObj.get("IsPayeeClaimantAttorney");
        String ChildLineGUID= inputObj.get("ChildLineGUID") == JSONObject.NULL ? null: (String)inputObj.get("ChildLineGUID");
        Integer PaymentType= inputObj.get("PaymentType") == JSONObject.NULL ? null: (Integer)inputObj.get("PaymentType");
        String debugMessage = "";
        JSONObject overAllResult = new JSONObject();
        try {
            Connection connection = DriverManager.getConnection(imsConnectionString);
            connection.setAutoCommit(false);
            //1. Begin Transaction
            PreparedStatement pst_tran =
                    connection.prepareStatement("BEGIN TRAN");
            pst_tran.execute();
            debugMessage += "Transaction Started...\n";

            //Step2: Call Fortegra_InsertCustomReservePaymentData
            HashMap<String,Object> paymentResult = insertClaims_ReservePayment(
                    connection,
                    ClaimId,
                    ClaimantGuid,
                    CoverageTypeId,
                    CoverageTypeDescriptionId,
                    ResPayTypeId,
                    ResPaySubTypeId,
                    ResPayAmount,
                    CreatedByGuid,
                    Comments,
                    1,
                    PayeeGuid,
                    PayeeName,
                    IsPayeeClaimant,
                    IsPayeeInsured,
                    0,
                    0,
                    "",
                    0, 0,
                    null,
                    AdditionalPayees,
                    Override_Address1,
                    Override_Address2,
                    Override_City,
                    Override_State,
                    Override_ZipCode,
                    Override_ISOCountryCode,
                    date,
                    PaymentResPayId,
                    IsPayeeDefenseAttorney,
                    IsPayeeClaimantAttorney
            );
            debugMessage += "Call to spClaims_InsertReservePayment completed."+ paymentResult.getOrDefault("DebugMessage","") + "\n";

            //Step3: Call insert_Fortegra_CustomChildLine
            if(paymentResult.containsKey("ResPayId")) {

                overAllResult.put("ResPayId", (Integer) paymentResult.getOrDefault("ResPayId", null));

                //Inserting childline for payment record
                String result = insert_Fortegra_CustomChildLine(connection,
                        (Integer) paymentResult.getOrDefault("ResPayId", null),
                        ChildLineGUID,
                        PaymentType);
                debugMessage += "Inserting childline for payment record - "+ result + "\n";

                if(IsPayeeClaimant == 0 && IsPayeeInsured == 0 &&
                        (IsPayeeDefenseAttorney == 0 && IsPayeeClaimantAttorney == 0)){
                    JSONObject result_claimpayee = insertClaimsPayees(connection,
                            (Integer) paymentResult.getOrDefault("ResPayId", null),
                            IsPayeeInsured,
                            PayeeGuid);
                    overAllResult.put("InsertClaimPayee",result_claimpayee);
                }

                //Step4: Call invoke_spClaims_TransferPayment
//                String transferResult = invoke_spClaims_TransferPayment(connection,
//                        (Integer) paymentResult.getOrDefault("ResPayId", null),
//                        CreatedByGuid);
//
//                debugMessage += transferResult + "\n";

                //Query the offset payment for sending to servicenow
                JSONObject offset_Record =  select_OffSet_PaymentRecord(connection,
                        (Integer) paymentResult.getOrDefault("ResPayId", null));
                overAllResult.put("OffsetPayment",offset_Record);

                //Inserting childline for payment offset record
                String result_1 = insert_Fortegra_CustomChildLine(connection,
                        offset_Record.getInt("RespayId"),
                        ChildLineGUID,
                        PaymentType);
                debugMessage += "Inserting childline for payment offset record - "+ result_1 + "\n";
            }

            PreparedStatement pstmt_commit =  connection.prepareStatement("COMMIT");
            pstmt_commit.execute();
            debugMessage += "Commited Transaction..."+ "\n";

            //Step5: Cleanup code
            pst_tran.close();
            pstmt_commit.close();
            connection.commit();
            connection.close();
        }
        catch (Exception e) {
            debugMessage += "Error - "+ e.getMessage();
            debugMessage += "Error-StackTrace - "+e.getStackTrace();
        }
        finally{
            overAllResult.put("DebugMessage", debugMessage);
        }
        return overAllResult.toString();
    }

    /*
     Transfer claim payment
     */
    public String transferClaimPayment(int resPayId,
                                       String userguid, int createCheck) throws SQLException {
        String debugMessage = "";
        JSONObject overAllResult = new JSONObject();
        try {
            Connection connection = DriverManager.getConnection(imsConnectionString);
            connection.setAutoCommit(false);
            String transferResult = invoke_spClaims_TransferPayment(connection,
                    resPayId,userguid,createCheck);
            connection.commit();
            connection.close();
            debugMessage += transferResult + "\n";
        }
        catch (Exception e) {
            debugMessage += "Error - "+ e.getMessage();
            debugMessage += "Error-StackTrace - "+e.getStackTrace();
        }
        finally{
            overAllResult.put("DebugMessage", debugMessage);
        }
        return overAllResult.toString();
    }

    /*
       This Method executes when the payment is voided.
    */
    public String void_Claim_Payment(
            int resPayId,
            int claimId,
            String claimantGUID,
            String userguid, String childLine) throws SQLException, IOException {
        JSONObject overAllResult = new JSONObject();
        String debugMessage = "";
        CommonUtil cu = new CommonUtil();

        try  {
            cu.snLog("test_mid_server","Void claim payment started for Respayid - " +resPayId+ "Claimid - "+ claimId);
            Connection connection = DriverManager.getConnection(imsConnectionString);
            connection.setAutoCommit(false);

            PreparedStatement pst_tran =
                    connection.prepareStatement("BEGIN TRAN");
            pst_tran.execute();
            debugMessage += "Transaction Started...\n";

            //Step1: Select the existing payment record
            HashMap<String, Object> existingPayment = new HashMap<>();
            String Query = "SELECT * FROM tblclaims_reservepayments WHERE ResPayId="+resPayId+
                    " AND ClaimId="+claimId+" AND ClaimantGuid='"+claimantGUID+"'";
            PreparedStatement pst_payment_existing =
                    connection.prepareStatement(Query);
            ResultSet rs = pst_payment_existing.executeQuery();
            while (rs.next()) {
                existingPayment.put("ClaimId",rs.getInt("ClaimId"));
                existingPayment.put("ClaimantGuid",rs.getString("ClaimantGuid"));
                existingPayment.put("CoverageTypeId",rs.getObject("CoverageTypeId"));
                existingPayment.put("CoverageTypeDescriptionId",rs.getObject("CoverageTypeDescriptionId"));
                existingPayment.put("ResPayTypeId",rs.getInt("ResPayTypeId"));
                existingPayment.put("ResPaySubTypeId",rs.getObject("ResPaySubTypeId"));
                existingPayment.put("ResPayAmount",rs.getBigDecimal("ResPayAmount"));
                existingPayment.put("DateCreated",rs.getTimestamp("DateCreated"));
                existingPayment.put("CreatedByGuid",userguid);
                existingPayment.put("Comments",rs.getObject("Comments"));
                existingPayment.put("IsPayment",rs.getInt("IsPayment"));
                existingPayment.put("IsPaymentReduction",rs.getInt("IsPaymentReduction"));
                existingPayment.put("PayeeGuid",rs.getString("PayeeGuid"));
                existingPayment.put("PayeeName",rs.getString("PayeeName"));
                existingPayment.put("IsPayeeClaimant",rs.getInt("IsPayeeClaimant"));
                existingPayment.put("IsPayeeInsured",rs.getInt("IsPayeeInsured"));
                existingPayment.put("DatePaid",rs.getDate("DatePaid"));
                existingPayment.put("PaidByUserGuid",rs.getString("PaidByUserGuid"));
                existingPayment.put("Void",rs.getInt("Void"));
                existingPayment.put("IsRecovery",rs.getInt("IsRecovery"));
                existingPayment.put("RecoveryCheckNumber",rs.getObject("RecoveryCheckNumber"));
                existingPayment.put("PaymentReturn",rs.getInt("PaymentReturn"));
                existingPayment.put("PaymentReturn_ResPayId",rs.getObject("PaymentReturn_ResPayId"));
                existingPayment.put("AdditionalPayees",rs.getObject("AdditionalPayees"));
                existingPayment.put("Override_Address1",rs.getObject("Override_Address1"));
                existingPayment.put("Override_Address2",rs.getObject("Override_Address2"));
                existingPayment.put("Override_City",rs.getObject("Override_City"));
                existingPayment.put("Override_State",rs.getObject("Override_State"));
                existingPayment.put("Override_ZipCode",rs.getObject("Override_ZipCode"));
                existingPayment.put("Override_ISOCountryCode",rs.getObject("Override_ISOCountryCode"));
                existingPayment.put("DateAdded",rs.getTimestamp("DateAdded"));
                existingPayment.put("PaymentResPayId",rs.getObject("PaymentResPayId"));
                existingPayment.put("IsPayeeDefenseAttorney",rs.getInt("IsPayeeDefenseAttorney"));
                existingPayment.put("IsPayeeClaimantAttorney",rs.getInt("IsPayeeClaimantAttorney"));
                break;
            }
            debugMessage += "Existing payment Retrieved for the query -"+ Query  +"\n";
            cu.snLog("test_mid_server","Existing payment Retrieved for the query -"+ Query);

            if(!existingPayment.isEmpty()) {

                //Update void =1 for the existing payment
                HashMap<String,Object> result_update = updatePaymentForVoid(
                        connection,
                        resPayId,
                        existingPayment.get("CoverageTypeId") == null ? null: ((Integer)existingPayment.get("CoverageTypeId")),
                        existingPayment.get("CoverageTypeDescriptionId") == null ? null: ((Integer)existingPayment.get("CoverageTypeDescriptionId")),
                        (Integer) existingPayment.get("ResPayTypeId"),
                        existingPayment.get("ResPaySubTypeId") == null ? null: ((Integer)existingPayment.get("ResPaySubTypeId")),
                        ((BigDecimal)existingPayment.get("ResPayAmount")),
                        existingPayment.get("Comments") == null ? null: ((String)existingPayment.get("Comments")),
                        1
                );
                debugMessage +="Called updatePaymentForVoid." + result_update.getOrDefault("DebugMessage","") + "\n";
                cu.snLog("test_mid_server","Called updatePaymentForVoid for updating the existing payment with void =1." + result_update.getOrDefault("DebugMessage","") );

                //Call Fortegra_InsertReservePayment to insert void records
                HashMap<String,Object> result = insertClaims_ReservePayment(
                        connection,
                        (Integer) existingPayment.get("ClaimId"),
                        (String) existingPayment.get("ClaimantGuid"),
                        existingPayment.get("CoverageTypeId") == null ? null: ((Integer)existingPayment.get("CoverageTypeId")),
                        existingPayment.get("CoverageTypeDescriptionId") == null ? null: ((Integer)existingPayment.get("CoverageTypeDescriptionId")),
                        (Integer) existingPayment.get("ResPayTypeId"),
                        existingPayment.get("ResPaySubTypeId") == null ? null: ((Integer)existingPayment.get("ResPaySubTypeId")),
                        ((BigDecimal)existingPayment.get("ResPayAmount")).multiply(BigDecimal.valueOf(-1)),
                        (String) existingPayment.get("CreatedByGuid"),
                        existingPayment.get("Comments") == null ? null: ((String)existingPayment.get("Comments")),
                        1,
                        (String) existingPayment.get("PayeeGuid"),
                        (String) existingPayment.get("PayeeName"),
                        (Integer) existingPayment.get("IsPayeeClaimant"),
                        (Integer) existingPayment.get("IsPayeeInsured"),
                        (Integer) existingPayment.get("IsPaymentReduction"),
                        (Integer) existingPayment.get("IsRecovery"),
                        existingPayment.get("RecoveryCheckNumber") == null ? null : ((String) existingPayment.get("RecoveryCheckNumber")),
                        1,
                        (Integer) existingPayment.get("PaymentReturn"),
                        existingPayment.get("PaymentReturn_ResPayId") == null ? null : ((Integer) existingPayment.get("PaymentReturn_ResPayId")) ,
                        existingPayment.get("AdditionalPayees") == null ? null : (String) existingPayment.get("AdditionalPayees"),
                        existingPayment.get("Override_Address1") == null ? null : (String) existingPayment.get("Override_Address1"),
                        existingPayment.get("Override_Address2") == null ? null : (String) existingPayment.get("Override_Address2"),
                        existingPayment.get("Override_City") == null ? null : (String) existingPayment.get("Override_City"),
                        existingPayment.get("Override_State") == null ? null : (String) existingPayment.get("Override_State"),
                        existingPayment.get("Override_ZipCode") == null ? null : (String) existingPayment.get("Override_ZipCode"),
                        existingPayment.get("Override_ISOCountryCode") == null ? null : (String) existingPayment.get("Override_ISOCountryCode"),
                        null,
                        existingPayment.get("PaymentResPayId") == null? null:((Integer) existingPayment.get("PaymentResPayId")),
                       (Integer)existingPayment.get("IsPayeeDefenseAttorney") ,
                       (Integer)existingPayment.get("IsPayeeClaimantAttorney")
                );
                debugMessage +="Called insertClaims_ReservePayment." + result.getOrDefault("DebugMessage","") + "\n";
                overAllResult.put("ResPayId", (Integer) result.getOrDefault("ResPayId", null));
                cu.snLog("test_mid_server","Called insertClaims_ReservePayment for inserting void and void offset." + result.getOrDefault("DebugMessage",""));

                //Void Record
                JSONObject result_void_record = new JSONObject();
                result_void_record.put("RespayId",(Integer) result.getOrDefault("ResPayId", null));
                result_void_record.put("ClaimId", (Integer) existingPayment.get("ClaimId"));
                result_void_record.put("ClaimantGUID", (String) existingPayment.get("ClaimantGuid"));
                result_void_record.put("CoverageTypeId", (Integer)existingPayment.get("CoverageTypeId"));
                result_void_record.put("CoverageTypeDescriptionId", (Integer)existingPayment.get("CoverageTypeDescriptionId"));
                result_void_record.put("ResPayTypeId", (Integer) existingPayment.get("ResPayTypeId"));
                result_void_record.put("RespaySubTypeId", existingPayment.get("ResPaySubTypeId") );
                result_void_record.put("ResPayAmount", ((BigDecimal)existingPayment.get("ResPayAmount")).multiply(BigDecimal.valueOf(-1)));
                result_void_record.put("CreatedByGuid", (String) existingPayment.get("CreatedByGuid"));
                result_void_record.put("Comments",((String)existingPayment.get("Comments")));
                result_void_record.put("IsPayment", 1);
                result_void_record.put("IsPaymentReduction", (Integer) existingPayment.get("IsPaymentReduction"));
                result_void_record.put("PayeeGuid",(String) existingPayment.get("PayeeGuid") );
                result_void_record.put("PayeeName",  (String) existingPayment.get("PayeeName"));
                result_void_record.put("IsPayeeClaimant", (Integer) existingPayment.get("IsPayeeClaimant"));
                result_void_record.put("IsPayeeInsured", (Integer) existingPayment.get("IsPayeeInsured"));
                result_void_record.put("PaymentResPayId", ((Integer) existingPayment.get("PaymentResPayId")));
                result_void_record.put("IsPayeeDefenseAttorney", (Integer)existingPayment.get("IsPayeeDefenseAttorney"));
                result_void_record.put("IsPayeeClaimantAttorney", (Integer)existingPayment.get("IsPayeeClaimantAttorney"));
                result_void_record.put("PaymentReturn_ResPayId", ((Integer) existingPayment.get("PaymentReturn_ResPayId")));
                result_void_record.put("RecoveryCheckNumber", ((String) existingPayment.get("RecoveryCheckNumber")));
                result_void_record.put("Void", 1);
                result_void_record.put("IsRecovery", (Integer) existingPayment.get("IsRecovery"));
                result_void_record.put("PaymentReturn", (Integer) existingPayment.get("PaymentReturn"));
                overAllResult.put("Void",result_void_record);

                //Query the offset payment for sending to servicenow
                JSONObject offset_Record =  select_OffSet_PaymentRecord(connection,
                        (Integer) result.getOrDefault("ResPayId", null));
                overAllResult.put("OffsetVoid",offset_Record);

                //Inserting child line for void record
                String result_1 = insert_Fortegra_CustomChildLine(connection,
                        (Integer) result.getOrDefault("ResPayId", null),
                        childLine,
                        null);
                debugMessage += "Inserting child line for void record - " + result_1 + "\n";
                cu.snLog("test_mid_server","Inserting child line for void offset record - " +  result_1);
                //Inserting child line for void offset record
                String result_2 = insert_Fortegra_CustomChildLine(connection,
                        offset_Record.getInt("RespayId"),
                        childLine,
                        null);
                debugMessage += "Inserting child line for void offset record - " +  result_2 + "\n";
                cu.snLog("test_mid_server","Inserting child line for void offset record - " +  result_2);
            }
            //Commit the transaction
            PreparedStatement pst_commit =
                    connection.prepareStatement("COMMIT");
            pst_commit.execute();
            debugMessage += "4.Commited Transaction...\n" ;
            cu.snLog("test_mid_server","Committed the transaction for Respayid - " +resPayId+ "Claimid - "+ claimId);
            connection.commit();

            //Cleanup code
            pst_tran.close();
            pst_payment_existing.close();
            pst_commit.close();
            connection.close();
        }
        catch (Exception e) {
            debugMessage += "Error - "+ e.getMessage();
            debugMessage += "Error-StackTrace - "+e.getStackTrace();
        }
        finally{
            overAllResult.put("DebugMessage", debugMessage);
        }
        return overAllResult.toString();
    }

    /*
     Inserts Payment return
     */
    public String insertPaymentReturn(String input){
        JSONObject inputObj = new JSONObject(input);
        Integer ClaimId = inputObj.get("ClaimId") == JSONObject.NULL ? null: (Integer)inputObj.get("ClaimId");
        String ClaimantGuid = inputObj.get("ClaimantGuid") == JSONObject.NULL ? null: (String)inputObj.get("ClaimantGuid");
        Integer CoverageTypeId = inputObj.get("CoverageTypeId") == JSONObject.NULL ? null: (Integer)inputObj.get("CoverageTypeId");
        Integer CoverageTypeDescriptionId= inputObj.get("CoverageTypeDescriptionId") == JSONObject.NULL ? null: (Integer)inputObj.get("CoverageTypeDescriptionId");
        Integer ResPayTypeId= inputObj.get("ResPayTypeId") == JSONObject.NULL ? null:  (Integer)inputObj.get("ResPayTypeId");
        Integer ResPaySubTypeId = inputObj.get("ResPaySubTypeId") == JSONObject.NULL ? null: (Integer)inputObj.get("ResPaySubTypeId");
        BigDecimal ResPayAmount = BigDecimal.valueOf(Double.valueOf((String)inputObj.get("ResPayAmount")));
        String CreatedByGuid= inputObj.get("CreatedByGuid") == JSONObject.NULL ? null: (String)inputObj.get("CreatedByGuid");
        String Comments = inputObj.get("Comments") == JSONObject.NULL ? null: (String)inputObj.get("Comments");
        String PayeeGuid= inputObj.get("PayeeGuid") == JSONObject.NULL ? null: (String)inputObj.get("PayeeGuid");
        String PayeeName= inputObj.get("PayeeName") == JSONObject.NULL ? null: (String)inputObj.get("PayeeName");
        Integer IsPayeeClaimant=(Integer) inputObj.get("IsPayeeClaimant");
        Integer IsPayeeInsured=(Integer) inputObj.get("IsPayeeInsured");
        String AdditionalPayees = inputObj.get("AdditionalPayees") == JSONObject.NULL ? null: (String)inputObj.get("AdditionalPayees");
        String Override_Address1 = inputObj.get("Override_Address1") == JSONObject.NULL ? null: (String)inputObj.get("Override_Address1");
        String Override_Address2 = inputObj.get("Override_Address2") == JSONObject.NULL ? null: (String)inputObj.get("Override_Address2");
        String Override_City = inputObj.get("Override_City") == JSONObject.NULL ? null: (String)inputObj.get("Override_City");
        String Override_State = inputObj.get("Override_State") == JSONObject.NULL ? null: (String)inputObj.get("Override_State");
        String Override_ZipCode = inputObj.get("Override_ZipCode") == JSONObject.NULL ? null: (String)inputObj.get("Override_ZipCode");
        String Override_ISOCountryCode = inputObj.get("Override_ISOCountryCode") == JSONObject.NULL ? null: (String)inputObj.get("Override_ISOCountryCode");
        Timestamp date = inputObj.get("dateCreated") == JSONObject.NULL ? null: Timestamp.valueOf((String)inputObj.get("dateCreated"));
        Integer PaymentResPayId = inputObj.get("PaymentResPayId") == JSONObject.NULL ? null: (Integer)inputObj.get("PaymentResPayId");
        Integer PaymentReturnResPayId = inputObj.get("PaymentReturnResPayId") == JSONObject.NULL ? null: (Integer)inputObj.get("PaymentReturnResPayId");
        String RecoveryCheckNum = inputObj.get("RecoveryCheckNum") == JSONObject.NULL ? null: (String)inputObj.get("RecoveryCheckNum");
        Integer IsPayeeDefenseAttorney=(Integer) inputObj.get("IsPayeeDefenseAttorney");
        Integer IsPayeeClaimantAttorney=(Integer) inputObj.get("IsPayeeClaimantAttorney");
        String ChildLineGUID= inputObj.get("ChildLineGUID") == JSONObject.NULL ? null: (String)inputObj.get("ChildLineGUID");
        Integer PaymentType= inputObj.get("PaymentType") == JSONObject.NULL ? null: (Integer)inputObj.get("PaymentType");
        String debugMessage = "";
        JSONObject overAllResult = new JSONObject();
        try {
            Connection connection = DriverManager.getConnection(imsConnectionString);
            connection.setAutoCommit(false);
            //1. Begin Transaction
            PreparedStatement pst_tran =
                    connection.prepareStatement("BEGIN TRAN");
            pst_tran.execute();
            debugMessage += "Transaction Started...\n";

            //Step1: Call spClaims_InsertReservePayment
            HashMap<String,Object> paymentResult = insertClaims_ReservePayment(
                    connection,
                    ClaimId,
                    ClaimantGuid,
                    CoverageTypeId,
                    CoverageTypeDescriptionId,
                    ResPayTypeId,
                    ResPaySubTypeId,
                    ResPayAmount,
                    CreatedByGuid,
                    Comments,
                    1,
                    PayeeGuid,
                    PayeeName,
                    IsPayeeClaimant,
                    IsPayeeInsured,
                    0,
                    0,
                    RecoveryCheckNum,
                    0, 1,
                    PaymentReturnResPayId,
                    AdditionalPayees,
                    Override_Address1,
                    Override_Address2,
                    Override_City,
                    Override_State,
                    Override_ZipCode,
                    Override_ISOCountryCode,
                    date,
                    PaymentResPayId,
                    IsPayeeDefenseAttorney,
                    IsPayeeClaimantAttorney
            );
            debugMessage += "Call to Fortegra_InsertReservePayment completed."+ paymentResult.getOrDefault("DebugMessage","") + "\n";
            if(paymentResult.containsKey("ResPayId")) {
                overAllResult.put("ResPayId", (Integer) paymentResult.getOrDefault("ResPayId", null));

                String result = insert_Fortegra_CustomChildLine(connection,
                        (Integer) paymentResult.getOrDefault("ResPayId", null),
                        ChildLineGUID,
                        PaymentType);
                debugMessage += result + "\n";

                //Query the offset payment for sending to servicenow
                JSONObject offset_Record =  select_OffSet_PaymentRecord(connection,
                        (Integer) paymentResult.getOrDefault("ResPayId", null));
                overAllResult.put("OffsetPaymentReturn",offset_Record);
            }

            PreparedStatement pstmt_commit =  connection.prepareStatement("COMMIT");
            pstmt_commit.execute();
            debugMessage += "Commited Transaction..."+ "\n";

            pst_tran.close();
            pstmt_commit.close();
            connection.commit();
            connection.close();
        }
        catch (Exception e) {
            debugMessage += "Error - "+ e.getMessage();
            debugMessage += "Error-StackTrace - "+e.getStackTrace();
        }
        finally{
            overAllResult.put("DebugMessage", debugMessage);
        }
        return overAllResult.toString();
    }

    /*
     Calls stored procedure to insert
        1. Reserves
        2. Payments
     */
    private HashMap<String,Object> insertClaims_ReservePayment(
            Connection connection,
            Integer ClaimId,
            String ClaimantGuid,
            Integer CoverageTypeId,
            Integer CoverageTypeDescriptionId,
            Integer ResPayTypeId,
            Integer ResPaySubTypeId,
            BigDecimal ResPayAmount,
            String CreatedByGuid,
            String Comments,
            Integer IsPayment,
            String PayeeGuid,
            String PayeeName,
            Integer IsPayeeClaimant,
            Integer IsPayeeInsured,
            Integer IsPaymentReduction,
            Integer IsRecovery,
            String RecoveryCheckNumber,
            Integer Void,
            Integer PaymentReturn,
            Integer PaymentReturn_ResPayId,
            String AdditionalPayees,
            String Override_Address1,
            String Override_Address2,
            String Override_City,
            String Override_State,
            String Override_ZipCode,
            String Override_ISOCountryCode,
            Timestamp date,
            Integer PaymentResPayId,
            Integer IsPayeeDefenseAttorney,
            Integer IsPayeeClaimantAttorney
    ) throws SQLException {
        HashMap<String,Object> result = new HashMap<>();
        CallableStatement  pst =
                connection.prepareCall(
                        "{? = call dbo.Fortegra_InsertReservePayment(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"
                );
        try {
            pst.registerOutParameter(1, java.sql.Types.INTEGER);
            pst.setInt(2, ClaimId);
            pst.setString(3, ClaimantGuid);
            pst.setObject(4, CoverageTypeId);
            pst.setObject(5, CoverageTypeDescriptionId);
            pst.setInt(6, ResPayTypeId);
            pst.setObject(7, ResPaySubTypeId);
            pst.setBigDecimal(8, ResPayAmount);
            pst.setObject(9,CreatedByGuid);
            pst.setObject(10, Comments);
            pst.setInt(11, IsPayment);
            pst.setObject(12, PayeeGuid);
            pst.setObject(13, PayeeName);
            pst.setInt(14, IsPayeeClaimant);
            pst.setInt(15, IsPayeeInsured);
            pst.setInt(16, IsPaymentReduction);
            pst.setInt(17, IsRecovery);
            pst.setObject(18, RecoveryCheckNumber);
            pst.setInt(19, Void);
            pst.setInt(20, PaymentReturn);
            pst.setObject(21,PaymentReturn_ResPayId);
            pst.setObject(22, AdditionalPayees);
            pst.setObject(23, Override_Address1);
            pst.setObject(24, Override_Address2);
            pst.setObject(25, Override_City);
            pst.setObject(26, Override_State);
            pst.setObject(27, Override_ZipCode);
            pst.setObject(28, Override_ISOCountryCode);
            pst.setObject(29, date);
            pst.setObject(30, PaymentResPayId);
            pst.setObject(31, IsPayeeDefenseAttorney);
            pst.setObject(32,IsPayeeClaimantAttorney);
            pst.execute();
            result.put("ResPayId",  pst.getInt(1));
            result.put("DebugMessage","Fortegra_InsertReservePayment executed successfully");
        }
        catch(Exception ex){
            result.put("DebugMessage","Fortegra_InsertReservePayment failed with an error - "+ex.getCause().toString());
        }
        finally {
            pst.close();
        }
        return result;
    }

    /*
     Update payment record with void = 1
     */
    private HashMap<String,Object> updatePaymentForVoid(
            Connection connection,
            Integer ResPayId,
            Integer CoverageTypeId,
            Integer CoverageTypeDescriptionId,
            Integer ResPayTypeId,
            Integer ResPaySubTypeId,
            BigDecimal ResPayAmount,
            String Comments,
            Integer Void
    ) throws SQLException {
        HashMap<String,Object> result = new HashMap<>();
        PreparedStatement  pst =
                connection.prepareStatement(
                        "{call spClaims_UpdateReservePayment(?,?,?,?,?,?,?,?)}"
                );
        try {
            pst.setInt(1, ResPayId);
            pst.setObject(2, CoverageTypeId);
            pst.setObject(3, CoverageTypeDescriptionId);
            pst.setInt(4, ResPayTypeId);
            pst.setObject(5, ResPaySubTypeId);
            pst.setBigDecimal(6, ResPayAmount);
            pst.setObject(7, Comments);
            pst.setInt(8, Void);
            pst.execute();
            result.put("DebugMessage","spClaims_UpdateReservePayment executed successfully");
        }
        catch(Exception ex){
            result.put("DebugMessage","spClaims_UpdateReservePayment failed with an error - "+ex.getCause().toString());
        }
        finally {
            pst.close();
        }
        return result;
    }

    /*
     Inserts child line association
     */
    private String insert_Fortegra_CustomChildLine(
            Connection connection,
            Integer ResPayId,
            String ChildLineGuid,
            Integer PaymentTypeId

    ) throws SQLException {
        PreparedStatement pst =
                connection.prepareStatement(
                        "{call dbo.Fortegra_InsertCustomReservePaymentData(?,?,?)}"
                );

        try {
            pst.setInt(1, ResPayId);
            pst.setString(2, ChildLineGuid);
            pst.setObject(3, PaymentTypeId);
            pst.execute();
            return "insert_Fortegra_CustomChildLine executed successfully";
        }
        catch(Exception ex){
            return "insert_Fortegra_CustomChildLine failed with an error - "+ex.getCause().toString();
        }
        finally {
            pst.close();
        }
    }

    /*
      Calls Stored Procedure - spClaims_TransferPayment
    */
    private String invoke_spClaims_TransferPayment(
            Connection connection,
            int resPayId,
            String userguid, int createCheck) throws SQLException {
        String result = "";
        try  {
            int bankgl = 0;
            PreparedStatement pstmt_bankgl =
                    connection.prepareStatement("select top 1 BankAccountId from Fortegra_tblClaims_TransferSettings \n" +
                            "where GLCompanyId = (SELECT SettingNumericValue FROM dbo.tblClaims_Settings WHERE SettingAutomationCode = 'GLCO')");
            ResultSet rs = pstmt_bankgl.executeQuery();
            while (rs.next()) {
                bankgl = rs.getInt("BankAccountId");
                break;
            }
            result += "Bank GL Retrieved...-"+bankgl + "\n";

            PreparedStatement pstmt =
                    connection.prepareStatement(
                            "{call dbo.spClaims_TransferPayment(?,?,?,?)}"
                    );
            if(bankgl > 0) {
                pstmt.setInt(1, resPayId);
                pstmt.setString(2, userguid);
                pstmt.setInt(3, bankgl);
                pstmt.setInt(4, createCheck);
                pstmt.execute();
                result += "Called invoke_spClaims_TransferPayment Stored Procedure...\n" ;
            }

            pstmt_bankgl.close();
            pstmt.close();
        }
        catch (Exception e){
            result += "Error-"+ e.getMessage() + "\n";
            result += "invoke_spClaims_TransferPayment failed with an error. Error-StackTrace - "+e.getCause().toString() + "\n";
        }
        return result;
    }

    /*
     Selects the offset payment record
     */
    private JSONObject select_OffSet_PaymentRecord(
            Connection connection,
            Integer paymentResPayId) throws SQLException {
        JSONObject result_offset_record = new JSONObject();
        PreparedStatement pstmt_Select =
                connection.prepareStatement("SELECT * FROM tblclaims_reservepayments where paymentrespayid = "+paymentResPayId);
        ResultSet rs = pstmt_Select.executeQuery();
        while (rs.next()) {
            result_offset_record.put("RespayId", rs.getInt("RespayId"));
            result_offset_record.put("ClaimId", rs.getInt("ClaimId"));
            result_offset_record.put("ClaimantGUID", rs.getString("ClaimantGUID"));
            result_offset_record.put("CoverageTypeId", rs.getObject("CoverageTypeId") == null ? null: (Integer)rs.getObject("CoverageTypeId"));
            result_offset_record.put("CoverageTypeDescriptionId", rs.getObject("CoverageTypeDescriptionId") == null ? null: (Integer)rs.getObject("CoverageTypeDescriptionId"));
            result_offset_record.put("ResPayTypeId", rs.getObject("ResPayTypeId") == null ? null: (Integer)rs.getObject("ResPayTypeId"));
            result_offset_record.put("RespaySubTypeId", rs.getObject("RespaySubTypeId") == null ? null: (Integer)rs.getObject("RespaySubTypeId"));
            result_offset_record.put("ResPayAmount", rs.getBigDecimal("ResPayAmount"));
            result_offset_record.put("DateCreated", rs.getTimestamp("DateCreated"));
            result_offset_record.put("CreatedByGuid", rs.getString("CreatedByGuid"));
            result_offset_record.put("Comments", rs.getObject("Comments")== null ? null: (String)rs.getObject("Comments"));
            result_offset_record.put("IsPayment", rs.getInt("IsPayment"));
            result_offset_record.put("IsPaymentReduction", rs.getObject("IsPaymentReduction") == null ? null: rs.getInt("IsPaymentReduction"));
            result_offset_record.put("PayeeGuid", rs.getObject("PayeeGuid")== null ? null: (String)rs.getObject("PayeeGuid"));
            result_offset_record.put("PayeeName", rs.getObject("PayeeName")== null ? null: (String)rs.getObject("PayeeName"));
            result_offset_record.put("IsPayeeClaimant", rs.getObject("IsPayeeClaimant") == null ? null:  rs.getInt("IsPayeeClaimant"));
            result_offset_record.put("IsPayeeInsured", rs.getObject("IsPayeeInsured") == null ? null:  rs.getInt("IsPayeeInsured"));
            result_offset_record.put("PaymentResPayId", rs.getObject("PaymentResPayId") == null ? null: (Integer)rs.getObject("PaymentResPayId"));
            result_offset_record.put("IsPayeeDefenseAttorney", rs.getObject("IsPayeeDefenseAttorney") == null ? null:  rs.getInt("IsPayeeDefenseAttorney"));
            result_offset_record.put("IsPayeeClaimantAttorney", rs.getObject("IsPayeeClaimantAttorney") == null ? null:  rs.getInt("IsPayeeClaimantAttorney"));
            result_offset_record.put("PaymentReturn_ResPayId", rs.getObject("PaymentReturn_ResPayId") == null ? null:  rs.getInt("PaymentReturn_ResPayId"));
            result_offset_record.put("RecoveryCheckNumber", rs.getObject("RecoveryCheckNumber") == null ? null:  rs.getString("RecoveryCheckNumber"));
            result_offset_record.put("Void", rs.getInt("Void"));
            result_offset_record.put("IsRecovery", rs.getInt("IsRecovery"));
            result_offset_record.put("PaymentReturn", rs.getInt("PaymentReturn"));
            break;
        }
        return result_offset_record;
    }


    /*
     Calls stored procedure to insert
        Claim payee
     */
    private JSONObject insertClaimsPayees(
            Connection connection,
            Integer ResPayId,
            Integer IsInsured,
            String PayeeGuid) throws SQLException {
        JSONObject result = new JSONObject();
        try
        {
            PreparedStatement pstmt_Select =
                    connection.prepareStatement("SELECT temp.* FROM\n" +
                            "(SELECT AdjusterGuid AS ExternalID,CASE  WHEN EntityType = 'C' THEN Company  WHEN EntityType = 'I' THEN FirstName+' '+LastName     \n" +
                            "END AS Name,'' AS AttorneyName,'' AS AttorneyType,'' AS AttorneyEntityType,FEIN,tblClaims_Addresses.Address1,tblClaims_Addresses.City,\n" +
                            "tblClaims_Addresses.State,tblClaims_Addresses.ISOCountryCode,tblClaims_Addresses.ZipCode,'' AS PhoneNumber,'' AS FaxNumber,\n" +
                            "FirstName,LastName,SSN,EntityType,Company FROM lstClaims_OutsideAdjusters left join tblClaims_Addresses on lstClaims_OutsideAdjusters.Addressid = tblClaims_Addresses.Addressid) temp \n" +
                            "where temp.ExternalID = '"+PayeeGuid+"'");
            ResultSet rs = pstmt_Select.executeQuery();
            PreparedStatement  pst =
                    connection.prepareStatement(
                            "{call spClaims_InsertClaimPayee(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"
                    );
            while (rs.next()) {

                pst.setInt(1,ResPayId);
                pst.setObject(2,null);
                pst.setInt(3,IsInsured);
                pst.setString(4,rs.getString("Name"));
                pst.setString(5,rs.getString("Address1"));
                pst.setString(6,null);
                pst.setString(7,rs.getString("City"));
                pst.setString(8,rs.getString("State"));
                pst.setString(9,rs.getString("ZipCode"));
                pst.setString(10,null);
                pst.setInt(11,0);
                pst.setString(12,null);
                pst.setString(13,"ISOCountryCode");
                pst.setString(14,null);
                pst.setString(15,rs.getString("FEIN"));
                pst.setString(16,rs.getString("SSN"));
                pst.setInt(17,0);
                pst.setTimestamp(18,null);
                pst.setString(19,null);
                pst.execute();
                result.put("DebugMessage","spClaims_InsertClaimPayee executed successfully");
                break;
            }
            pst.close();
            pstmt_Select.close();
        }
        catch(Exception ex){
            result.put("DebugMessage","spClaims_InsertClaimPayee failed with an error - "+ex.getCause().toString());
        }
        return result;
    }

    /*
     Returns the reserve summary for a particular claimid
     */
    public String getReserveSummary(int claimId) throws SQLException {
        Connection connection = DriverManager.getConnection(imsConnectionString);
        JSONObject result = new JSONObject();
        try
        {
            PreparedStatement pstmt_Select =
                    connection.prepareStatement(
                            "SELECT ClaimantGuid,ResPayTypeId,ResPaySubTypeId,\n" +
                                    "    SUM(CASE WHEN ispayment=0 and ispaymentreduction=0 and void = 0 and isrecovery =0 THEN respayamount ELSE 0 END) AS TotalIncurred,\n" +
                                    "    SUM(CASE WHEN ispayment=1 THEN respayamount ELSE 0 END) AS TotalPaid,\n" +
                                    "    SUM(CASE WHEN (ispayment=0 and ispaymentreduction=0 and void = 0 and isrecovery =0 and PaymentReturn =0) \n" +
                                    "        or Ispaymentreduction=1 THEN respayamount ELSE 0 END) AS RemainingReserve\n" +
                                    "    FROM tblClaims_ReservePayments where claimid = "+claimId+"  group by ClaimantGuid, ResPayTypeId,ResPaySubTypeId");
            ResultSet rs = pstmt_Select.executeQuery();
            JSONArray objArr = new JSONArray();
            while (rs.next()) {
                HashMap<String,String> reservesummary = new HashMap<>();
                reservesummary.put("ClaimantGuid", rs.getString("ClaimantGuid"));
                reservesummary.put("ResPayTypeId", rs.getString("ResPayTypeId"));
                reservesummary.put("ResPaySubTypeId", rs.getString("ResPaySubTypeId") == null ? "NULL" : rs.getString("ResPaySubTypeId"));
                reservesummary.put("TotalIncurred", rs.getString("TotalIncurred"));
                reservesummary.put("TotalPaid", rs.getString("TotalPaid"));
                reservesummary.put("RemainingReserve", rs.getString("RemainingReserve"));
                objArr.put(reservesummary);
            }
            result.put("ClaimId",claimId);
            result.put("ReserveSummary",objArr);
            result.put("DebugMessage","");
            pstmt_Select.close();
            connection.close();
        }
        catch(Exception ex){
            result.put("DebugMessage","getReserveSummary failed with an error - "+ex.getCause().toString());
        }
        return result.toString();
    }

}
