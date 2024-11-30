package processor;

import util.CommonUtil;
import java.io.IOException;


public class main {

    public static void main(String[] args) throws IOException {

    CommonUtil cu = new CommonUtil();
    String token = cu.getAccessToken("client_credentials",
            "085249de-c6a7-4f50-95e8-5a5b3be09c64",
            "eKR8Q~Oo9wfEjZ8hUEUV4D2Z-zaCa1IFsRXZXcdj",
            "https://graph.microsoft.com/.default",
            "e49e9863-0f2e-4860-891b-48221b674dc2");

    String connectionUrl =
                "jdbc:sqlserver://MGADS0002-NJ.NY.MGASYSTEMS.COM:1433;"
                        + "database=Fortegra;"
                        + "user=muthukumar.meenakshisundaram@augustahitech.com;"
                        + "password=the.tooN0.swore6.a.sign.hugs.the.noodle;";

    System.out.println(cu.transferdocument("00555848-0194-48F8-8E3A-8D5067878148",
            connectionUrl,token,"abc/pdf","b!AXAKxzJkf02Shd-28ta_4kxl-h-3llJFvPvbCNsmniWUXHmML35hQZKP5y87-lge"));
    }

}
