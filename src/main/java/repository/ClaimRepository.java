package repository;

import interfaces.ProcessCallback;
import org.json.JSONObject;
import util.CommonUtil;
import java.sql.*;

public class ClaimRepository {

    String imsConnectionString = "";

    /*
     Constructor
     */
    public ClaimRepository(String env){
        imsConnectionString = CommonUtil.appConfig.getOrDefault(env+"_imsconnectionstring","na");
    }

    /*
     upserts the new accident information
    */
    public String insertAccidentInformation(String input) throws SQLException {
        JSONObject inputObj = new JSONObject(input);
        JSONObject overAllResult = new JSONObject();
        Integer ClaimId = inputObj.get("ClaimId") == JSONObject.NULL ? null: (Integer)inputObj.get("ClaimId");
        String Address1 = inputObj.get("Address1") == JSONObject.NULL ? null: (String)inputObj.get("Address1");
        String City = inputObj.get("City") == JSONObject.NULL ? null: (String)inputObj.get("City");
        String State= inputObj.get("State") == JSONObject.NULL ? null: (String)inputObj.get("State");
        String ZipCode= inputObj.get("ZipCode") == JSONObject.NULL ? null: (String)inputObj.get("ZipCode");
        String ISOCountryCode = inputObj.get("ISOCountryCode") == JSONObject.NULL ? null: (String)inputObj.get("ISOCountryCode");
        String AccidentDescription = inputObj.get("AccidentDescription") == JSONObject.NULL ? null: (String)inputObj.get("AccidentDescription");
        String AccidentTime = inputObj.get("AccidentTime") == JSONObject.NULL ? null: (String)inputObj.get("AccidentTime");
        String County = inputObj.get("County") == JSONObject.NULL ? null: (String)inputObj.get("County");
        Integer  AccidentTypeId = inputObj.get("AccidentTypeId") == JSONObject.NULL ? 0: (Integer)inputObj.get("AccidentTypeId");
        String debugMessage = "";
        Connection connection = DriverManager.getConnection(imsConnectionString);
        try {
            connection.setAutoCommit(false);
            //Check if the accident/loss location already exist for the claim id
            PreparedStatement pstmt_Select =
                    connection.prepareStatement("SELECT * from tblClaims_ClaimAccidentInformation " +
                            "where claimid = "+ClaimId);
            ResultSet rs = pstmt_Select.executeQuery();
            if (rs.next()) {
                PreparedStatement pst =
                        connection.prepareStatement(
                                "{call spClaims_UpdateAccidentInformation(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"
                        );
                pst.setInt(1, rs.getInt("AccidentInformationId"));
                pst.setString(2, Address1);
                pst.setString(3, null);
                pst.setString(4, City);
                pst.setString(5, State);
                pst.setString(6, ZipCode);
                pst.setString(7, ISOCountryCode);
                pst.setString(8,null);
                pst.setString(9,AccidentDescription);
                pst.setString(10, AccidentTime);
                pst.setInt(11,AccidentTypeId);
                pst.setBigDecimal(12,null);
                pst.setBigDecimal(13,null);
                pst.setString(14,County);
                pst.execute();
                overAllResult.put("AccidentInformationId",rs.getInt("AccidentInformationId"));
                debugMessage = "spClaims_UpdateAccidentInformation executed successfully";
                pst.close();
            }
            else {
                CallableStatement pst =
                        connection.prepareCall(
                                "{? = call spClaims_InsertAccidentInformation(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"
                        );
                pst.registerOutParameter(1, java.sql.Types.INTEGER);
                pst.setInt(2, ClaimId);
                pst.setString(3, Address1);
                pst.setString(4, null);
                pst.setString(5, City);
                pst.setString(6, State);
                pst.setString(7, ZipCode);
                pst.setString(8, ISOCountryCode);
                pst.setString(9, null);
                pst.setString(10, AccidentDescription);
                pst.setString(11, AccidentTime);
                pst.setInt(12, AccidentTypeId);
                pst.setBigDecimal(13, null);
                pst.setBigDecimal(14, null);
                pst.setString(15, County);
                pst.execute();
                overAllResult.put("AccidentInformationId", pst.getInt(1));
                debugMessage = "spClaims_InsertAccidentInformation executed successfully";
                pst.close();
            }
            connection.commit();
            connection.close();
        }
        catch (Exception e) {
            debugMessage += "Error - "+ e.getMessage();
            debugMessage += "Error-StackTrace - "+e.getStackTrace();
            connection.close();
        }
        finally{
            overAllResult.put("DebugMessage", debugMessage);
        }
        return overAllResult.toString();
    }

    /*
      updates the existing accident information
     */
    public String updateAccidentInformation(String input) throws SQLException {
        JSONObject inputObj = new JSONObject(input);
        JSONObject overAllResult = new JSONObject();
        Integer AccidentInformationId = inputObj.get("AccidentInformationId") == JSONObject.NULL ? null: (Integer)inputObj.get("AccidentInformationId");
        String Address1 = inputObj.get("Address1") == JSONObject.NULL ? null: (String)inputObj.get("Address1");
        String City = inputObj.get("City") == JSONObject.NULL ? null: (String)inputObj.get("City");
        String State= inputObj.get("State") == JSONObject.NULL ? null: (String)inputObj.get("State");
        String ZipCode= inputObj.get("ZipCode") == JSONObject.NULL ? null: (String)inputObj.get("ZipCode");
        String ISOCountryCode = inputObj.get("ISOCountryCode") == JSONObject.NULL ? null: (String)inputObj.get("ISOCountryCode");
        String AccidentDescription = inputObj.get("AccidentDescription") == JSONObject.NULL ? null: (String)inputObj.get("AccidentDescription");
        String AccidentTime = inputObj.get("AccidentTime") == JSONObject.NULL ? null: (String)inputObj.get("AccidentTime");
        String County = inputObj.get("County") == JSONObject.NULL ? null: (String)inputObj.get("County");
        Integer  AccidentTypeId = inputObj.get("AccidentTypeId") == JSONObject.NULL ? null: (Integer)inputObj.get("AccidentTypeId");
        String debugMessage = "";
        Connection connection = DriverManager.getConnection(imsConnectionString);
        try {
            connection.setAutoCommit(false);
            PreparedStatement pst =
                    connection.prepareStatement(
                            "{call spClaims_UpdateAccidentInformation(?,?,?,?,?,?,?,?,?,?,?,?,?,?)}"
                    );
            pst.setInt(1, AccidentInformationId);
            pst.setString(2, Address1);
            pst.setString(3, null);
            pst.setString(4, City);
            pst.setString(5, State);
            pst.setString(6, ZipCode);
            pst.setString(7, ISOCountryCode);
            pst.setString(8,null);
            pst.setString(9,AccidentDescription);
            pst.setString(10, AccidentTime);
            pst.setInt(11,AccidentTypeId);
            pst.setBigDecimal(12,null);
            pst.setBigDecimal(13,null);
            pst.setString(14,County);
            pst.execute();
            overAllResult.put("AccidentInformationId",AccidentInformationId);
            debugMessage = "spClaims_UpdateAccidentInformation executed successfully";
            pst.close();
            connection.commit();
            connection.close();
        }
        catch (Exception e) {
            debugMessage += "Error - "+ e.getMessage();
            debugMessage += "Error-StackTrace - "+e.getStackTrace();
            connection.close();
        }
        finally{
            overAllResult.put("DebugMessage", debugMessage);
        }
        return overAllResult.toString();
    }

}
