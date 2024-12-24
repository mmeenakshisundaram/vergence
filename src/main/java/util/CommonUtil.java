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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CommonUtil {

    public static HashMap<String,String> appConfig;

    static {
        appConfig = new HashMap<String,String>();
        appConfig.put("test_imsconnectionstring","jdbc:sqlserver://MGADSTest01.ny.mgasystems.com:1433;database=Fortegra_Test;user=mperkins@fortegra.com_DBO;password=the2.sofa.chanted.a5.ragnet.relieves.a.sTump;");
        appConfig.put("prod_imsconnectionstring","jdbc:sqlserver://MGADS0002-NJ.NY.MGASYSTEMS.COM:1433;database=Fortegra;user=svc_acct_ims_prod;password=a.yin.snorTed.the2.devil3.popped.the.plaything;");
    }

    /*
     Default Constructor
     */
    public CommonUtil(){
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
    public String cleanMidServer(String foldername, boolean isroot) throws IOException
    {
        JSONObject overlAllResult = new JSONObject();
        try {
            overlAllResult.put("Status1",  "Cleanup started...");
            String prefix = "";
            if (isroot) {
                prefix = System.getProperty("java.io.tmpdir");
            }
            File dirPath = new File(prefix + foldername);
            if(dirPath.exists()) {
                File filesList[] = dirPath.listFiles();
                for(File file : filesList) {
                    try {
                        if (file.isFile()) {
                            file.delete();
                        } else {
                            cleanMidServer(file.getAbsolutePath(), false);
                            file.delete();
                        }
                    }
                    catch (Exception ex){
                        overlAllResult.put("Error_"+file.getName(),  ex.getCause().toString());
                    }
                }
                //FileUtils.cleanDirectory(dirPath);
                overlAllResult.put("Status2",  "Cleanup done...");
            }
            else{
                overlAllResult.put("Status2",  "Folder Not Exist...");
            }
            overlAllResult.put("Status2",  "Cleanup completed...");
            return overlAllResult.toString();
        }
        catch (Exception ex){
            overlAllResult.put("Error1",  ex.getMessage());
            overlAllResult.put("Error2",  ex.getStackTrace() +  ex.getCause().toString());
            return overlAllResult.toString();
        }
    }
}
