package repository;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Hashtable;

public class PaymentRepository {

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
            Hashtable<String, Object> existingPayment = new Hashtable<>();
            String Query = "SELECT * FROM tblclaims_reservepayments WHERE ResPayId="+resPayId+
                    " AND ClaimId="+claimId+" AND ClaimantGuid='"+claimantGUID+"'";
            PreparedStatement pst_payment_existing =
                    connection.prepareStatement(Query);
            ResultSet rs = pst_payment_existing.executeQuery();
            while (rs.next()) {
                existingPayment.put("ClaimId",rs.getInt("ClaimId"));
                existingPayment.put("ClaimantGuid",rs.getString("ClaimantGuid"));
                existingPayment.put("CoverageTypeId",rs.getInt("CoverageTypeId"));
                existingPayment.put("CoverageTypeDescriptionId",rs.getInt("CoverageTypeDescriptionId"));
                existingPayment.put("ResPayTypeId",rs.getInt("ResPayTypeId"));
                existingPayment.put("ResPaySubTypeId",rs.getInt("ResPaySubTypeId"));
                existingPayment.put("ResPayAmount",rs.getBigDecimal("ResPayAmount"));
                existingPayment.put("DateCreated",rs.getTimestamp("DateCreated"));
                existingPayment.put("CreatedByGuid",userguid);
                existingPayment.put("Comments",rs.getString("Comments"));
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
                existingPayment.put("RecoveryCheckNumber",rs.getString("RecoveryCheckNumber"));
                existingPayment.put("PaymentReturn",rs.getInt("PaymentReturn"));
                existingPayment.put("PaymentReturn_ResPayId",rs.getInt("PaymentReturn_ResPayId"));
                existingPayment.put("AdditionalPayees",rs.getString("AdditionalPayees") == null ? "": rs.getString("AdditionalPayees"));
                existingPayment.put("DateAdded",rs.getTimestamp("DateAdded"));
                existingPayment.put("PaymentResPayId",rs.getInt("PaymentResPayId"));
                existingPayment.put("IsPayeeDefenseAttorney",rs.getInt("IsPayeeDefenseAttorney"));
                existingPayment.put("IsPayeeClaimantAttorney",rs.getInt("IsPayeeClaimantAttorney"));
                break;
            }
            debugMessage += "Existing payment Retrieved for the query -"+ Query  +"\n";


            if(!existingPayment.isEmpty()) {

                //Step2: Call spClaims_InsertReservePayment
               String result = insertClaims_ReservePayment(
                        connection,
                        (Integer) existingPayment.get("ClaimId"),
                        (String) existingPayment.get("ClaimantGuid"),
                        (Integer) existingPayment.get("CoverageTypeId"),
                        ((Integer) existingPayment.get("CoverageTypeDescriptionId")) == 0? null: ((Integer)existingPayment.get("CoverageTypeDescriptionId")),
                        (Integer) existingPayment.get("ResPayTypeId"),
                        ((Integer) existingPayment.get("ResPaySubTypeId")) == 0? null: ((Integer)existingPayment.get("ResPaySubTypeId")),
                        ((BigDecimal)existingPayment.get("ResPayAmount") ).multiply(BigDecimal.valueOf(-1)),
                        (String) existingPayment.get("CreatedByGuid"),
                        (String) existingPayment.get("Comments"),
                        1,
                        (String) existingPayment.get("PayeeGuid"),
                        (String) existingPayment.get("PayeeName"),
                        (Integer) existingPayment.get("IsPayeeClaimant"),
                        (Integer) existingPayment.get("IsPayeeInsured"),
                        (Integer) existingPayment.get("IsPaymentReduction"),
                        (Integer) existingPayment.get("IsRecovery"),
                        (String) existingPayment.get("RecoveryCheckNumber"),
                        1,
                        (Integer) existingPayment.get("PaymentReturn"),
                        (Integer) existingPayment.get("PaymentReturn_ResPayId"),
                        existingPayment.get("AdditionalPayees") == null ? null : (String) existingPayment.get("AdditionalPayees"),
                        null,null,null,null,null,null,
                        (Timestamp) existingPayment.get("DateCreated"),
                        (Integer) existingPayment.get("PaymentResPayId"),
                        (Integer) existingPayment.get("IsPayeeDefenseAttorney"),
                        (Integer) existingPayment.get("IsPayeeClaimantAttorney")
                );
                debugMessage +="Called insertClaims_ReservePayment." + result + "\n";

                //Step3: Insert into childline

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
        catch (Exception e){
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
    private String insertClaims_ReservePayment(
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

        PreparedStatement pst =
                connection.prepareStatement(
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
            pst.setInt(20,PaymentReturn_ResPayId);
            pst.setString(21, AdditionalPayees);
            pst.setObject(22, Override_Address1);
            pst.setObject(23, Override_Address2);
            pst.setObject(24, Override_City);
            pst.setObject(25, Override_State);
            pst.setObject(26, Override_ZipCode);
            pst.setObject(27, Override_ISOCountryCode);
            pst.setTimestamp(28, date);
            pst.setInt(29, PaymentResPayId);
            pst.setInt(30, IsPayeeDefenseAttorney);
            pst.setInt(31,IsPayeeClaimantAttorney);
            pst.execute();
            return "spClaims_InsertReservePayment executed successfully";
        }
        catch(Exception ex){
            return "spClaims_InsertReservePayment failed with an error - "+ex.getCause().toString();
        }
        finally {
            pst.close();
        }
    }

    /*
     Inserts child line association
     */
    private String insertFortegra_CustomChildLine(
            //Connection connection,

    ){
        return "";
    }

}
