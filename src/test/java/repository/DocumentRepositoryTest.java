package repository;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import util.CommonUtil;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class DocumentRepositoryTest {

    @Test
    void shouldGenerateToken() throws IOException {
        CommonUtil cu = new CommonUtil();
        String token = cu.getAccessToken("client_credentials",
        "085249de-c6a7-4f50-95e8-5a5b3be09c64",
        "eKR8Q~Oo9wfEjZ8hUEUV4D2Z-zaCa1IFsRXZXcdj",
        "https://graph.microsoft.com/.default",
        "e49e9863-0f2e-4860-891b-48221b674dc2");
        assertNotEquals(token,"");
    }

    @Test
    void shouldTransferDocument_Test() throws IOException {
        CommonUtil cu = new CommonUtil();
        String token = cu.getAccessToken("client_credentials",
                "085249de-c6a7-4f50-95e8-5a5b3be09c64",
                "eKR8Q~Oo9wfEjZ8hUEUV4D2Z-zaCa1IFsRXZXcdj",
                "https://graph.microsoft.com/.default",
                "e49e9863-0f2e-4860-891b-48221b674dc2");
        String connectionUrl = cu.appConfig.get("test_imsconnectionstring");
        String resp = cu.transferdocument("be392212-c92d-4fd1-983b-f95224a9fa6d",
            connectionUrl,
                 token,
                 "abc/pdf",
                 "b!AXAKxzJkf02Shd-28ta_4kxl-h-3llJFvPvbCNsmniWUXHmML35hQZKP5y87-lge",
                "e519348c97fc2210bddf36c71153afc0");
        assertNotEquals(resp,"");
    }

    @Test
    void shouldTransferDocument_Prod() throws IOException {
        CommonUtil cu = new CommonUtil();
        String token = cu.getAccessToken("client_credentials",
                "085249de-c6a7-4f50-95e8-5a5b3be09c64",
                "eKR8Q~Oo9wfEjZ8hUEUV4D2Z-zaCa1IFsRXZXcdj",
                "https://graph.microsoft.com/.default",
                "e49e9863-0f2e-4860-891b-48221b674dc2");
        String connectionUrl = cu.appConfig.get("prod_imsconnectionstring");
        cu.transferdocument("83479256-4A5D-44C2-9B1A-A94D0E993B32",
                connectionUrl,
                token,
                "SPC0113310",
                "b!AXAKxzJkf02Shd-28ta_4kxl-h-3llJFvPvbCNsmniWLpr0OUNekQJ3GJWzOf4br","");

    }

}
