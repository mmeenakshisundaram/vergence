package processor;


import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;
import org.json.*;
import util.CommonUtil;

public class main {

    private static String ACCESS_TOKEN = "YOUR_ACCESS_TOKEN"; // OAuth Token
    private static final String SITE_ID = "YOUR_SITE_ID"; // SharePoint Site ID
    private static final String DRIVE_ID = "b!AXAKxzJkf02Shd-28ta_4kxl-h-3llJFvPvbCNsmniWLpr0OUNekQJ3GJWzOf4br"; // Document Library ID
    private static final Pattern FILE_PATTERN = Pattern.compile("^.*\\([a-fA-F0-9]{32}\\)\\..+$");

    private static Timestamp tstart = Timestamp.valueOf(LocalDateTime.now());
    static CommonUtil cu = new CommonUtil();

    public static void main(String[] args) throws Exception {

        ACCESS_TOKEN = cu.getAccessToken("client_credentials",
                "085249de-c6a7-4f50-95e8-5a5b3be09c64",
                "eKR8Q~Oo9wfEjZ8hUEUV4D2Z-zaCa1IFsRXZXcdj",
                "https://graph.microsoft.com/.default",
                "e49e9863-0f2e-4860-891b-48221b674dc2");


        scanFolder("root"); // Start from the root folder
    }

    private static void scanFolder(String folderId) throws Exception {
        String url = "https://graph.microsoft.com/v1.0/drives/" + DRIVE_ID + "/items/" + folderId + "/children";
        fetchAndProcessFiles(url);
    }

    private static void fetchAndProcessFiles(String url) throws Exception {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            while (url != null) {

                long millisecondsDiff = Timestamp.valueOf(LocalDateTime.now()).getTime() - tstart.getTime();
                long minutesDiff = TimeUnit.MILLISECONDS.toMinutes(millisecondsDiff);
                if(minutesDiff > 30){
                    System.out.println("Token Refreshed-"+ LocalDateTime.now());
                    tstart = Timestamp.valueOf(LocalDateTime.now());
                    ACCESS_TOKEN = cu.getAccessToken("client_credentials",
                            "085249de-c6a7-4f50-95e8-5a5b3be09c64",
                            "eKR8Q~Oo9wfEjZ8hUEUV4D2Z-zaCa1IFsRXZXcdj",
                            "https://graph.microsoft.com/.default",
                            "e49e9863-0f2e-4860-891b-48221b674dc2");
                }

                HttpGet request = new HttpGet(url);
                request.setHeader("Authorization", "Bearer " + ACCESS_TOKEN);

                try (CloseableHttpResponse response = client.execute(request)) {
                    String responseBody = EntityUtils.toString(response.getEntity());

                    if (response.getCode() != 200) {
                        System.out.println("Error: " + responseBody);
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray items = jsonObject.getJSONArray("value");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        String name = item.getString("name");
                        String webUrl = item.getString("webUrl");

                        if (item.has("folder")) {
                            // It's a folder, scan recursively
                            scanFolder(item.getString("id"));
                        } else if (FILE_PATTERN.matcher(name).matches()) {
                            // It's a matching file, print the URL
                            System.out.println("Match Found: " + name + " -> " + webUrl);
                        }
                    }

                    // Handle pagination (if there are more files, `@odata.nextLink` will be present)
                    url = jsonObject.optString("@odata.nextLink", null);
                }
            }
        }
        catch(Exception ex){
            System.out.println("Exception - " + ex.getCause());
            System.out.println("Exception - " + ex.getMessage());
        }
    }
}
