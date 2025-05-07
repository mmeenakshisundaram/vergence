package repository;

import org.apache.hc.client5.http.fluent.Content;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ContentType;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import util.CommonUtil;

import java.io.IOException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CCRepositoryTest {

    @Test
    void shouldGenerateToken() throws IOException {
        String auth = "eght_MmQ1YTA2NWEtNzUyZi00MzFlLTkzZmItNWMxYTA4MmI2MDRh" + ":" +
                "MzllYWNlODctNGY0MS00MzUyLWEwMzYtODE4M2Q2YTdlOTUw";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;
        Content response = Request.post("https://api.8x8.com/oauth/v2/token")
                .addHeader("Authorization", authHeader)
                .bodyForm(
                        Form.form()
                                .add("grant_type", "client_credentials")
                                .build()
                )
                .execute()
                .returnContent();
        JSONObject inputObj = new JSONObject(response.asString());
        assertNotEquals(response.asString(),"");
    }

    @Test
    void shoulddownload8x8File() throws IOException {
        CCRepository ccRepo = new CCRepository("test");
        String str = ccRepo.download8x8File("d7f910c0-a3db-4d61-b459-79d2d0f088c5",
                "eyJhbGciOiJSUzI1NiJ9.eyJzZXJ2aWNlcyI6WyJDRS1QQ1MtUHJvZHVjdCIsIkNFLVJDUy1Qcm9kdWN0IiwiQ2hhdCIsIlFNIC0gQVBJIiwiYW5hbHl0aWNzIHByb2R1Y3QiLCJhbmFseXRpY3MgcmVhbHRpbWUtYXBpIiwiYXVkaXQiLCJjaGF0LWdhdGV3YXkiLCJzdG9yYWdlIiwidmNjIiwidmNjLWVpdnIiLCJ2Y2MtbmF0aXZlLWNybSIsInZjYy1zY2hlZHVsZXMiXSwiaXNzIjoiY2xvdWQ4Ljh4OC5jb20iLCJpYXQiOjE3NDYzNDkzNjcsInN1YiI6ImQxMmY0NjY0NzRlNzI0MjEyZDRhNzU2ODE0YmQzZjhhYzcyNGExYjUiLCJleHAiOjE3NDYzNTExNjd9.d-tFZ6cnUKrL8KwGnsZcodbfkGif7OwQcfj8BB-19yo3GVzqJrPvhTAAUgemH-QDxfcfgY0AugTJGtv5JTIM-H1iKNQlljHKAh89AfpzDZ7iwFK29bVrnVZXM7oCcY1LUtQD5w26-z-fO_3zK6unskvUibdsLNxBCdHXh7nboJkJjVa2Jc05AmEwRPXT5vy2qIakIaZt5GQySSnGIWSxB7nCa1xSoaSmW-bowNMuUHSKnVHIc58QjmtyiZ8Qy8u1AjKPOO1JUfkFySxt_KniRXnl9ppBc5LLQ0vOcBX-QJNkXKyh8LQTP1XCOfPWuIsesEsu3ElG8Smd7HzlBMHX-A",

                "03_05_2025");
        assertNotEquals(str,"");
    }
}
