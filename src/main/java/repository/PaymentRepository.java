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
        try  {
            Connection connection = DriverManager.getConnection(imsConnectionString);
            connection.setAutoCommit(false);

            PreparedStatement pst_tran =
                    connection.prepareStatement("BEGIN TRAN");
            pst_tran.execute();
            overAllResult.put("Progress1", "Transaction Started...");

            //Step1: Select the existing payment record
            Hashtable<String, Object> existingPayment = new Hashtable<>();
            PreparedStatement pst_payment_existing =
                    connection.prepareStatement("SELECT * FROM tblclaims_reservepayments WHERE ResPayId="+resPayId+
                            " AND ClaimId="+claimId+" AND ClaimantGuid='"+claimantGUID+"'");
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
            overAllResult.put("Progress2", "Existing payment Retrieved...-");

            //Step2: Call spClaims_InsertReservePayment
            PreparedStatement pst =
                    connection.prepareStatement(
                            "{call dbo.spClaims_InsertReservePayment(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"
                    );
            if(!existingPayment.isEmpty()) {
                pst.setInt(1, (Integer) existingPayment.get("ClaimId"));
                pst.setString(2, (String) existingPayment.get("ClaimantGuid"));
                pst.setInt(3, (Integer) existingPayment.get("CoverageTypeId"));
                pst.setObject(4, ((Integer) existingPayment.get("CoverageTypeDescriptionId")) == 0? null: ((Integer)existingPayment.get("CoverageTypeDescriptionId")));
                pst.setInt(5, (Integer) existingPayment.get("ResPayTypeId"));
                pst.setObject(6, ((Integer) existingPayment.get("ResPaySubTypeId")) == 0? null: ((Integer)existingPayment.get("ResPaySubTypeId")));
                pst.setBigDecimal(7, ((BigDecimal)existingPayment.get("ResPayAmount") ).multiply(BigDecimal.valueOf(-1)));
                pst.setString(8, (String) existingPayment.get("CreatedByGuid"));
                pst.setString(9, (String) existingPayment.get("Comments"));
                pst.setInt(10, 1);
                pst.setString(11, (String) existingPayment.get("PayeeGuid"));
                pst.setString(12, (String) existingPayment.get("PayeeName"));
                pst.setInt(13, (Integer) existingPayment.get("IsPayeeClaimant"));
                pst.setInt(14, (Integer) existingPayment.get("IsPayeeInsured"));
                pst.setInt(15, (Integer) existingPayment.get("IsPaymentReduction"));
                pst.setInt(16, (Integer) existingPayment.get("IsRecovery"));
                pst.setString(17, (String) existingPayment.get("RecoveryCheckNumber"));
                pst.setInt(18, 1);
                pst.setInt(19, (Integer) existingPayment.get("PaymentReturn"));
                pst.setInt(20, (Integer) existingPayment.get("PaymentReturn_ResPayId"));
                pst.setString(21, existingPayment.get("AdditionalPayees") == null ? null : (String) existingPayment.get("AdditionalPayees"));
                pst.setObject(22, null);
                pst.setObject(23, null);
                pst.setObject(24, null);
                pst.setObject(25, null);
                pst.setObject(26, null);
                pst.setObject(27, null);
                pst.setTimestamp(28, (Timestamp) existingPayment.get("DateCreated"));
                pst.setInt(29, (Integer) existingPayment.get("PaymentResPayId"));
                pst.setInt(30, (Integer) existingPayment.get("IsPayeeDefenseAttorney"));
                pst.setInt(31, (Integer) existingPayment.get("IsPayeeClaimantAttorney"));
                pst.execute();
                overAllResult.put("Progress3", "Called Stored Procedure...");
            }

            //Step3: Commit the transaction
            PreparedStatement pst_commit =
                    connection.prepareStatement("COMMIT");
            pst_commit.execute();
            overAllResult.put("Progress4", "Commited Transaction...");
            connection.commit();

            //Step4: Cleanup code
            pst_tran.close();
            pst_payment_existing.close();
            pst.close();
            pst_commit.close();
            connection.close();
        }
        catch (Exception e){
            overAllResult.put("Error",e.getMessage());
            overAllResult.put("Error-StackTrace",e.getStackTrace());
        }

        return overAllResult.toString();
    }
}
