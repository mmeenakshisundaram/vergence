package repository;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.core5.http.ContentType;
import org.json.JSONObject;
import util.CommonUtil;

import java.io.*;
import java.nio.file.*;

public class CCRepository {

    String env = "";
    public CCRepository(String env){
        this.env = env;
    }

    /*

     */
    public String download8x8File(String objectId,
                                   String authToken,
                                   String oDate){
        JSONObject overAllResult = new JSONObject();
        String fileName = downloadFileName(objectId,authToken,overAllResult);
        downloadFile(fileName,authToken,oDate,overAllResult);
        return overAllResult.toString();
    }

    /*

     */
    public String downloadFileName(String objectId,
                               String authToken, JSONObject overAllResult) {
        String fileUrl = "https://api.8x8.com/storage/us-east/v3/bulk/download/start";
        String fileName = "";
        try {
            Content response = Request.post(fileUrl)
                    .addHeader("Authorization", authToken)
                    .addHeader("Accept", "application/json")
                    .bodyString("[\""+ objectId  +"\"]", ContentType.APPLICATION_JSON)
                    .execute()
                    .returnContent();
            JSONObject inputObj = new JSONObject(response.asString());
            fileName =  inputObj.get("zipName") == JSONObject.NULL ? null: (String)inputObj.get("zipName");
            overAllResult.put("FileDownloadMetadata", response.asString());

        } catch (Exception e) {
            overAllResult.put("Error","Error Occured in downloadFileName.Details -"+e.getMessage()+ e.getStackTrace());
        }
        return fileName;
    }


    /*

     */
    public void downloadFile(String fileName,
                               String authToken,
                              String oDate,
                               JSONObject overAllResult){

        String fileUrl = "https://api.8x8.com/storage/us-east/v3/bulk/download/"+fileName;
        String localFilePath = System.getProperty("java.io.tmpdir")+fileName;
        //Azure Blob Storage settings
        String connectionString = "";
        if(this.env.toUpperCase().equals("TEST")) {
             connectionString = "DefaultEndpointsProtocol=https;AccountName=adlsservicenowdataengdev;AccountKey=gzgUuY7CPiI267+scO5DmGXB8WthMlzasxhlcCsND+Lm+2eE0NSpdaXYaAOuWAXQ3FOqtVIoyXEt+ASt8EeX7A==;EndpointSuffix=core.windows.net";
        }
        else if(this.env.toUpperCase().equals("PROD")){
            connectionString = "DefaultEndpointsProtocol=https;AccountName=adlsservicenowdataengprd;AccountKey=/bqP+LN6cv0nQILkJKHHNt7P2zGk/Ib+1xUS4a3ZjUQd4MmCeAE6s2oiu/bxbgA/eqPP9Cu9+1HP+AStiePT5w==;EndpointSuffix=core.windows.net";
        }
        String containerName = "8x8";
        String blobName =  oDate +"/"+ fileName;

        try {
            // Step 1: Download the file from the URL
            System.out.println("Downloading file from URL...");
            byte[] fileData = Request.get(fileUrl)
                    .addHeader("Authorization", authToken)
                    .execute()
                    .returnContent()
                    .asBytes();
            Files.write(Paths.get(localFilePath), fileData);
            System.out.println("File downloaded: " + localFilePath);
            overAllResult.put("DownloadStatus-1", "Downloaded to Local File System"+localFilePath);

            // Step 2: Upload to Azure Blob Storage
            overAllResult.put("DownloadStatus-2", "Uploading to blob storage");
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            blobClient.uploadFromFile(localFilePath, true);
            overAllResult.put("DownloadStatus-3", "Uploaded to blob storage"+ blobClient.getBlobUrl());

            //Delete local file
            Files.delete(Paths.get(localFilePath));
            overAllResult.put("DownloadStatus-4", "Local file deleted.");

        } catch (Exception e) {
            overAllResult.put("Error","Error Occured in downloadFile.Details -"+e.getMessage()+ e.getStackTrace());
        }
    }
}
