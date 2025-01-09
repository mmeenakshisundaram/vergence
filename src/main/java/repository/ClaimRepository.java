package repository;

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
            overAllResult.put("DebugMessage","spClaims_UpdateAccidentInformation executed successfully");
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
