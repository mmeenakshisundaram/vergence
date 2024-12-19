package util;

import com.microsoft.sqlserver.jdbc.SQLServerResultSet;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CommonUtil {

    /*
     Default Constructor
     */
    public CommonUtil(){
    }

    /*
     Test Method
     */
    public String Test(String param){
        return "Hello"+param;
    }

    /*
     Calls Stored Procedure - spClaims_TransferPayment
     */
    public String invoke_spClaims_TransferPayment(
            String imsConnectionString,
            int resPayId,
            String userguid) throws SQLException {
        JSONObject overlAllResult = new JSONObject();
        try  {
            Connection connection = DriverManager.getConnection(imsConnectionString);
            connection.setAutoCommit(false);

            PreparedStatement pstmt_tran =
                    connection.prepareStatement("BEGIN TRAN");
            pstmt_tran.execute();
            overlAllResult.put("Progress1", "Transaction Started...");

            int bankgl = 0;
            PreparedStatement pstmt_bankgl =
                    connection.prepareStatement("select top 1 BankAccountId from Fortegra_tblClaims_TransferSettings \n" +
                            "where GLCompanyId = (SELECT SettingNumericValue FROM dbo.tblClaims_Settings WHERE SettingAutomationCode = 'GLCO')");
            ResultSet rs = pstmt_bankgl.executeQuery();
            while (rs.next()) {
                bankgl = rs.getInt("BankAccountId");
                break;
            }
            overlAllResult.put("Progress2", "Bank GL Retrieved...-"+bankgl);

            PreparedStatement pstmt =
                    connection.prepareStatement(
                            "{call dbo.spClaims_TransferPayment(?,?,?)}"
                    );
            if(bankgl > 0) {
                pstmt.setInt(1, resPayId);
                pstmt.setString(2, userguid);
                pstmt.setInt(3, bankgl);
                pstmt.execute();
                overlAllResult.put("Progress3", "Called Stored Procedure...");
            }

            PreparedStatement pstmt_commit =
                    connection.prepareStatement("COMMIT");
            pstmt_commit.execute();
            overlAllResult.put("Progress4", "Commited Transaction...");

            connection.commit();
            pstmt_tran.close();
            pstmt_bankgl.close();
            pstmt.close();
            pstmt_commit.close();
            connection.close();
        }
        catch (Exception e){
            overlAllResult.put("Error",e.getMessage());
            overlAllResult.put("Error-StackTrace",e.getStackTrace());
        }

        return overlAllResult.toString();
    }

    /*
     Transfer the document from IMS to Sharepoint.
     */
    public String transferdocument(
            String documentStoreGUID,
            String imsConnectionString,
            String sharepointToken,
            String folderName,
            String driveId) {

        String tempPath = System.getProperty("java.io.tmpdir")+"Fortegra\\";
        JSONObject overlAllResult = new JSONObject();
        ResultSet resultSet = null;
        try {

            Connection connection = DriverManager.getConnection(imsConnectionString);
            Statement statement = connection.createStatement();
            String selectSql = "SELECT CAST(N'' AS XML)" +
                    ".value('xs:base64Binary(xs:hexBinary(sql:column(\"bin\")))','VARCHAR(MAX)')  Base64Encoding, Compressed, FileName " +
                    " FROM (select Document AS bin, Compressed, FileName from tblDocumentStore where " +
                    "DocumentStoreGUID = '"+documentStoreGUID+"') AS bin_sql_server_temp;";
            resultSet = statement.executeQuery(selectSql);
            // Print results from select statement
            while (resultSet.next()) {
                System.out.println(resultSet.getString("Base64Encoding"));
                //Storing into temp location
                byte[] data = DatatypeConverter.parseBase64Binary(resultSet.getString("Base64Encoding"));
                String path = "";
                if(resultSet.getInt("Compressed") == 1){
                     path = tempPath +documentStoreGUID+".zip";
                     new File(tempPath).mkdirs();
                }
                else{
                     path = tempPath+documentStoreGUID+"\\"+resultSet.getString("FileName");
                     new File(tempPath+documentStoreGUID).mkdirs();
                }
                File file = new File(path);
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
                outputStream.write(data);
                outputStream.close();
                overlAllResult.put("IMS Status",  "File read from IMS...");
                //UnZip the file
                if(resultSet.getInt("Compressed") == 1){
                    extractFolder(path, tempPath+documentStoreGUID);
                }
                overlAllResult.put("Compression Status",  "File unzipped...");

                //Send to sharepoint
                File dir = new File(tempPath+documentStoreGUID);
                File[] directoryListing = dir.listFiles();
                overlAllResult.put("Progress1",  "listing files from "+tempPath+documentStoreGUID);
                if (directoryListing != null) {
                    for (File child : directoryListing) {
                        String filename = child.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                        HttpPut putRequest_data =
                                new HttpPut("https://graph.microsoft.com/v1.0/drives/" +
                                        driveId + "/root:/"+folderName+"/"+filename+":/content");
                        putRequest_data.setHeader("Authorization", "Bearer " + sharepointToken);
                        putRequest_data.setEntity(new FileEntity(child));
                        HttpClient client_data = HttpClientBuilder.create().build();
                        overlAllResult.put("Progress2",  "sending file to sharepoint. File name:"+filename);
                        HttpResponse response_data = client_data.execute(putRequest_data);
                        overlAllResult.put("Progress3",  "sent to sharepoint. File name:"+filename);
                        String json_data = EntityUtils.toString(response_data.getEntity());
                        JSONObject jsonObject = new JSONObject(json_data);
                        overlAllResult.put("fileid",  jsonObject.get("id"));
                        overlAllResult.put("sharepointurl",  jsonObject.get("webUrl"));

                        //Get listid
                        HttpGet getRequest_data =
                                new HttpGet("https://graph.microsoft.com" +
                                        "/v1.0/drives/"+driveId+"/items/"+jsonObject.get("id")+"?$expand=listItem");
                        getRequest_data.setHeader("Authorization", "Bearer " + sharepointToken);
                        HttpClient client_listid = HttpClientBuilder.create().build();
                        HttpResponse response_listid = client_listid.execute(getRequest_data);
                        String json_listid = EntityUtils.toString(response_listid.getEntity());
                        JSONObject jsonObject_listid = new JSONObject(json_listid);
                        overlAllResult.put("listid", ((JSONObject)jsonObject_listid.get("listItem")).get("id"));

                        child.delete();
                    }
                }
                //Delete existing files if anything exists
                try
                {
                    Files.deleteIfExists(Paths.get(path));
                    overlAllResult.put("Cleanup Status",  "Cleanup status is done...");
                }
                catch(Exception ex){
                    overlAllResult.put("Cleanup Status", ex.getMessage());
                }
                overlAllResult.put("Sharepoint Status",  "File Stored in sharepoint...");
            }
            connection.close();

        }
        catch (SQLException e) {
            overlAllResult.put("Exception",e.getMessage());
        }
        catch (FileNotFoundException e) {
            overlAllResult.put("Exception",e.getMessage());
        }
        catch (IOException e) {
            overlAllResult.put("Exception",e.getMessage());
        }
        catch (Exception e){
            overlAllResult.put("Exception",e.getMessage());
        }
        return overlAllResult.toString();
    }

    /*
      Generate new access token
     */
    public String getAccessToken(
            String granttype,
            String clientid,
            String clientSecret,
            String scope,
            String tenant
    ) throws IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("grant_type", granttype));
        params.add(new BasicNameValuePair("client_id", clientid));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        params.add(new BasicNameValuePair("scope", scope));
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params);
        HttpPost postRequest_token = new HttpPost("https://login.microsoft.com/" +
               tenant+"/oauth2/v2.0/token");
        postRequest_token.setEntity(urlEncodedFormEntity);
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(postRequest_token);
        String json_string = EntityUtils.toString(response.getEntity());
        JSONObject tokenjson = new JSONObject(json_string);
        return tokenjson.get("access_token").toString();
    }

    /*
     UnZip the zip file
     */
    private void extractFolder(String zipFile,
                               String extractFolder) throws IOException {
        try
        {
            int BUFFER = 2048;
            File file = new File(zipFile);
            ZipFile zip = new ZipFile(file);
            String newPath = extractFolder;
            new File(newPath).mkdir();
            Enumeration zipFileEntries = zip.entries();
            // Process each entry
            while (zipFileEntries.hasMoreElements())            {
                // grab a zip file entry
                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
                File destFile = new File(newPath, currentEntry);
                //destFile = new File(newPath, destFile.getName());
                File destinationParent = destFile.getParentFile();
                // create the parent directory structure if needed
                destinationParent.mkdirs();

                if (!entry.isDirectory())
                {
                    BufferedInputStream is = new BufferedInputStream(zip
                            .getInputStream(entry));
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[BUFFER];
                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    BufferedOutputStream dest = new BufferedOutputStream(fos,
                            BUFFER);
                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            }
        }
        catch (Exception e)
        {
            throw e;
        }
    }


   /*
    Clean up mid server temp table
   */
    public boolean cleanMidServer() throws IOException {

        try {
            File dirPath = new File(System.getProperty("java.io.tmpdir") + "Fortegra");
            FileUtils.cleanDirectory(dirPath);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
}
