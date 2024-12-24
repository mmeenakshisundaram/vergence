package repository;

import org.json.JSONObject;
import util.CommonUtil;

import java.lang.ref.Reference;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Hashtable;

public class PaymentRepository {

    String imsConnectionString = "";

    public PaymentRepository(String env){
        imsConnectionString = CommonUtil.appConfig.getOrDefault(env+"_imsconnectionstring","na");
    }

    /*
     Inserts claim payment
     */
    public String insert_Claim_Payment(
            Integer ClaimId,
            String ClaimantGuid,
            Integer CoverageTypeId,
            Integer CoverageTypeDescriptionId,
            Integer ResPayTypeId,
            Integer ResPaySubTypeId,
            BigDecimal ResPayAmount,
            String CreatedByGuid,
            String Comments,
            String PayeeGuid,
            String PayeeName,
            Integer IsPayeeClaimant,
            Integer IsPayeeInsured,
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
            Integer IsPayeeClaimantAttorney,
            String ChildLineGUID, Integer PaymentType){

        JSONObject overAllResult = new JSONObject();
        String debugMessage = "";
        try  {
            Connection connection = DriverManager.getConnection(imsConnectionString);
            connection.setAutoCommit(false);
            //1. Begin Transaction
            PreparedStatement pst_tran =
                    connection.prepareStatement("BEGIN TRAN");
            pst_tran.execute();
            debugMessage += "Transaction Started...\n";

            //Step2: Call spClaims_InsertReservePayment
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
                    0,
             0,
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

                String result = insert_Fortegra_CustomChildLine(connection,
                        (Integer) paymentResult.getOrDefault("ResPayId", null),
                        ChildLineGUID,
                        PaymentType);
                debugMessage += result + "\n";

                //Step4: Call invoke_spClaims_TransferPayment
                String transferResult = invoke_spClaims_TransferPayment(connection,
                        (Integer) paymentResult.getOrDefault("ResPayId", null),
                        CreatedByGuid);

                debugMessage += transferResult + "\n";
            }

            PreparedStatement pstmt_commit =  connection.prepareStatement("COMMIT");
            pstmt_commit.execute();
            debugMessage += "Commited Transaction..."+ "\n";

            //Step5: Cleanup code
            pst_tran.close();
            pstmt_commit.close();
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
       This Method executes when the payment is voided.
    */
    public String invoke_spClaims_InsertVoidPayment(
            String imsConnectionString,
            int resPayId,
            int claimId,
            String claimantGUID,
            String userguid) throws SQLException {
        JSONObject overAllResult = new JSONObject();
        String debugMessage = "";
        try  {
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


            if(!existingPayment.isEmpty()) {

                //Step2: Call spClaims_InsertReservePayment
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

            }
            //Step4: Commit the transaction
            PreparedStatement pst_commit =
                    connection.prepareStatement("COMMIT");
            pst_commit.execute();
            debugMessage += "4.Commited Transaction...\n" ;
            connection.commit();

            //Step4: Cleanup code
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
                        "{call dbo.spClaims_InsertReservePayment(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"
                );
        try {
            pst.setInt(1, ClaimId);
            pst.setString(2, ClaimantGuid);
            pst.setInt(3, CoverageTypeId);
            pst.setObject(4, CoverageTypeDescriptionId);
            pst.setInt(5, ResPayTypeId);
            pst.setObject(6, ResPaySubTypeId);
            pst.setBigDecimal(7, ResPayAmount);
            pst.setString(8,CreatedByGuid);
            pst.setString(9, Comments);
            pst.setInt(10, IsPayment);
            pst.setString(11, PayeeGuid);
            pst.setString(12, PayeeName);
            pst.setInt(13, IsPayeeClaimant);
            pst.setInt(14, IsPayeeInsured);
            pst.setInt(15, IsPaymentReduction);
            pst.setInt(16, IsRecovery);
            pst.setString(17, RecoveryCheckNumber);
            pst.setInt(18, Void);
            pst.setInt(19, PaymentReturn);
            pst.setObject(20,PaymentReturn_ResPayId);
            pst.setObject(21, AdditionalPayees);
            pst.setObject(22, Override_Address1);
            pst.setObject(23, Override_Address2);
            pst.setObject(24, Override_City);
            pst.setObject(25, Override_State);
            pst.setObject(26, Override_ZipCode);
            pst.setObject(27, Override_ISOCountryCode);
            pst.setTimestamp(28, date);
            pst.setObject(29, PaymentResPayId);
            pst.setInt(30, IsPayeeDefenseAttorney);
            pst.setInt(31,IsPayeeClaimantAttorney);
            pst.registerOutParameter(32, java.sql.Types.INTEGER);
            pst.execute();
            result.put("RespayId",  pst.getInt(32));
            result.put("DebugMessage","spClaims_InsertReservePayment executed successfully");
        }
        catch(Exception ex){
            result.put("DebugMessage","spClaims_InsertReservePayment failed with an error - "+ex.getCause().toString());
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
            pst.setInt(3, PaymentTypeId);
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
            String userguid) throws SQLException {
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
                            "{call dbo.spClaims_TransferPayment(?,?,?)}"
                    );
            if(bankgl > 0) {
                pstmt.setInt(1, resPayId);
                pstmt.setString(2, userguid);
                pstmt.setInt(3, bankgl);
                pstmt.execute();
                result += "Called Stored Procedure...\n" ;
            }

            pstmt_bankgl.close();
            pstmt.close();
        }
        catch (Exception e){
            result += "Error-"+ e.getMessage() + "\n";
            result += "Error-StackTrace - "+e.getCause().toString() + "\n";
        }
        return result;
    }

}
