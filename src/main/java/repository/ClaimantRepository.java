package repository;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.*;

public class ClaimantRepository {

    public String updateClaimant(String input){
        JSONObject inputObj = new JSONObject(input);
        JSONObject overAllResult = new JSONObject();
//        String ClaimantGuid = inputObj.get("ClaimantGuid") == JSONObject.NULL ? null: (String)inputObj.get("ClaimantGuid");
//        String UserGuid = inputObj.get("UserGuid") == JSONObject.NULL ? null: (String)inputObj.get("UserGuid");
//        Timestamp DateReported= inputObj.get("CoverageTypeDescriptionId") == JSONObject.NULL ? null: (Integer)inputObj.get("CoverageTypeDescriptionId");
//        Timestamp DateSuitServed= inputObj.get("ResPayTypeId") == JSONObject.NULL ? null:  (Integer)inputObj.get("ResPayTypeId");
//        Timestamp DateSuitAnswered = inputObj.get("ResPaySubTypeId") == JSONObject.NULL ? null: (Integer)inputObj.get("ResPaySubTypeId");
//        Timestamp DateDenied = BigDecimal.valueOf(Double.valueOf((String)inputObj.get("ResPayAmount")));
//        Integer IsInsured= inputObj.get("CreatedByGuid") == JSONObject.NULL ? null: (String)inputObj.get("CreatedByGuid");
//        String CorporationName = inputObj.get("Comments") == JSONObject.NULL ? null: (String)inputObj.get("Comments");
//        String FirstName= inputObj.get("PayeeGuid") == JSONObject.NULL ? null: (String)inputObj.get("PayeeGuid");
//        String MiddleName= inputObj.get("PayeeName") == JSONObject.NULL ? null: (String)inputObj.get("PayeeName");
//        String LastName=(Integer) inputObj.get("IsPayeeClaimant");
//        Integer Gender=(Integer) inputObj.get("IsPayeeInsured");
//        String SSN = inputObj.get("AdditionalPayees") == JSONObject.NULL ? null: (String)inputObj.get("AdditionalPayees");
//        Timestamp DOB = inputObj.get("Override_Address1") == JSONObject.NULL ? null: (String)inputObj.get("Override_Address1");
//        String FEIN = inputObj.get("Override_Address2") == JSONObject.NULL ? null: (String)inputObj.get("Override_Address2");
//        String EmailAddress = inputObj.get("Override_City") == JSONObject.NULL ? null: (String)inputObj.get("Override_City");
//        Integer ManagedCareId = inputObj.get("Override_State") == JSONObject.NULL ? null: (String)inputObj.get("Override_State");
//        Integer LossTypeId = inputObj.get("Override_ZipCode") == JSONObject.NULL ? null: (String)inputObj.get("Override_ZipCode");
//        Integer AccidentTypeId = inputObj.get("Override_ISOCountryCode") == JSONObject.NULL ? null: (String)inputObj.get("Override_ISOCountryCode");
//        Integer StatusId = inputObj.get("PaymentResPayId") == JSONObject.NULL ? null: (Integer)inputObj.get("PaymentResPayId");
//        String OutsideInvestigator=(Integer) inputObj.get("IsPayeeDefenseAttorney");
//        Integer IsSettled=(Integer) inputObj.get("IsPayeeInsured");
//        Integer SettlementTypeId=(Integer) inputObj.get("IsPayeeInsured");
//        String ClaimantComments=(Integer) inputObj.get("IsPayeeInsured");
//        String OutsideAdjusterGuid=(Integer) inputObj.get("IsPayeeInsured");
//        Integer MedicareEligible =(Integer) inputObj.get("IsPayeeInsured");
//        Integer MailingAddressId =(Integer) inputObj.get("IsPayeeInsured");
//        String debugMessage = "";
//        try {
//            Connection connection = DriverManager.getConnection(imsConnectionString);
//            connection.setAutoCommit(false);
//            //1. Begin Transaction
//            PreparedStatement pst_tran =
//                    connection.prepareStatement("BEGIN TRAN");
//            pst_tran.execute();
//            debugMessage += "Transaction Started...\n";
//
//            CallableStatement pst =
//                    connection.prepareCall(
//                            "{call spClaims_UpdateClaimant(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"
//                    );
//
//            pst.registerOutParameter(1, java.sql.Types.INTEGER);
//            pst.setInt(2, ClaimId);
//            pst.setString(3, ClaimantGuid);
//            pst.setObject(4, CoverageTypeId);
//            pst.setObject(5, CoverageTypeDescriptionId);
//            pst.setInt(6, ResPayTypeId);
//            pst.setObject(7, ResPaySubTypeId);
//            pst.setBigDecimal(8, ResPayAmount);
//            pst.setObject(9,CreatedByGuid);
//            pst.setObject(10, Comments);
//            pst.setInt(11, IsPayment);
//            pst.setObject(12, PayeeGuid);
//            pst.setObject(13, PayeeName);
//            pst.setInt(14, IsPayeeClaimant);
//            pst.setInt(15, IsPayeeInsured);
//            pst.setInt(16, IsPaymentReduction);
//            pst.setInt(17, IsRecovery);
//            pst.setObject(18, RecoveryCheckNumber);
//            pst.setInt(19, Void);
//            pst.setInt(20, PaymentReturn);
//            pst.setObject(21,PaymentReturn_ResPayId);
//            pst.setObject(22, AdditionalPayees);
//            pst.setObject(23, Override_Address1);
//            pst.setObject(24, Override_Address2);
//            pst.setObject(25, Override_City);
//            pst.setObject(26, Override_State);
//            pst.setObject(27, Override_ZipCode);
//            pst.setObject(28, Override_ISOCountryCode);
//            pst.setObject(29, date);
//            pst.setObject(30, PaymentResPayId);
//            pst.setObject(31, IsPayeeDefenseAttorney);
//            pst.setObject(32,IsPayeeClaimantAttorney);
//            pst.execute();
//            result.put("ResPayId",  pst.getInt(1));
//            result.put("DebugMessage","spClaims_InsertReservePayment executed successfully");
//
//            PreparedStatement pstmt_commit =  connection.prepareStatement("COMMIT");
//            pstmt_commit.execute();
//            debugMessage += "Commited Transaction..."+ "\n";
//
//            //Step5: Cleanup code
//            pst_tran.close();
//            pst.close();
//            pstmt_commit.close();
//            connection.commit();
//            connection.close();
//        }
//        catch (Exception e) {
//            debugMessage += "Error - "+ e.getMessage();
//            debugMessage += "Error-StackTrace - "+e.getStackTrace();
//        }
//        finally{
//            overAllResult.put("DebugMessage", debugMessage);
//        }
        return overAllResult.toString();
    }
}
