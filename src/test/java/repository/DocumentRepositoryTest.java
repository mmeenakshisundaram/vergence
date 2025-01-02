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
        String connectionUrl = "jdbc:sqlserver://MGADSTest01.ny.mgasystems.com:1433;"
                + "database=Fortegra_Test;"
                + "user=mperkins@fortegra.com_DBO;"
                + "password=the2.sofa.chanted.a5.ragnet.relieves.a.sTump;";
         cu.transferdocument("00AE87B5-FF65-44AA-A1CA-4404D621FA07",
            connectionUrl,token,"abc/pdf","b!AXAKxzJkf02Shd-28ta_4kxl-h-3llJFvPvbCNsmniWUXHmML35hQZKP5y87-lge");


    }

    @Test
    void shouldTransferDocument_Prod() throws IOException {
        CommonUtil cu = new CommonUtil();
        String token = cu.getAccessToken("client_credentials",
                "085249de-c6a7-4f50-95e8-5a5b3be09c64",
                "eKR8Q~Oo9wfEjZ8hUEUV4D2Z-zaCa1IFsRXZXcdj",
                "https://graph.microsoft.com/.default",
                "e49e9863-0f2e-4860-891b-48221b674dc2");
        String connectionUrl = "jdbc:sqlserver://MGADSTest01.ny.mgasystems.com:1433;"
                + "database=Fortegra_Test;"
                + "user=mperkins@fortegra.com_DBO;"
                + "password=the2.sofa.chanted.a5.ragnet.relieves.a.sTump;";
        cu.transferdocument("83479256-4A5D-44C2-9B1A-A94D0E993B32",
                connectionUrl,token, "SPC0113310","b!AXAKxzJkf02Shd-28ta_4kxl-h-3llJFvPvbCNsmniWLpr0OUNekQJ3GJWzOf4br");

    }

}
