package processor;

import org.json.JSONObject;
import repository.PaymentRepository;
import util.CommonUtil;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;


public class main {

    public static void main(String[] args) throws IOException, SQLException {

    /*
        Test Case for transferdocument
     */
//    CommonUtil cu = new CommonUtil();
//    String token = cu.getAccessToken("client_credentials",
//            "085249de-c6a7-4f50-95e8-5a5b3be09c64",
//            "eKR8Q~Oo9wfEjZ8hUEUV4D2Z-zaCa1IFsRXZXcdj",
//            "https://graph.microsoft.com/.default",
//            "e49e9863-0f2e-4860-891b-48221b674dc2");

//    String connectionUrl =
//                "jdbc:sqlserver://MGADS0002-NJ.NY.MGASYSTEMS.COM:1433;"
//                        + "database=Fortegra;"
//                        + "user=muthukumar.meenakshisundaram@augustahitech.com;"
//                        + "password=the.tooN0.swore6.a.sign.hugs.the.noodle;";

//    //Test
//    System.out.println(cu.transferdocument("00AE87B5-FF65-44AA-A1CA-4404D621FA07",
//            connectionUrl,token,"abc/pdf","b!AXAKxzJkf02Shd-28ta_4kxl-h-3llJFvPvbCNsmniWUXHmML35hQZKP5y87-lge"));
        //Prod
//    System.out.println(cu.transferdocument("83479256-4A5D-44C2-9B1A-A94D0E993B32",
//                connectionUrl,token, "SPC0113310","b!AXAKxzJkf02Shd-28ta_4kxl-h-3llJFvPvbCNsmniWLpr0OUNekQJ3GJWzOf4br"));

        /*
        Test Case for invoke_spClaims_TransferPayment
        */
//        String connectionUrl =
//                "jdbc:sqlserver://MGADSTest01.ny.mgasystems.com:1433;"
//                        + "database=Fortegra_Test;"
//                        + "user=mperkins@fortegra.com_DBO;"
//                        + "password=the2.sofa.chanted.a5.ragnet.relieves.a.sTump;";
//        CommonUtil cu = new CommonUtil();
//        cu.invoke_spClaims_TransferPayment(connectionUrl,
//                4026,
//                "15779D93-532B-424C-9399-04BF6CDF0739"
//                );

        /*
        Test Case for Void payment
         */
        String connectionUrl =
                "jdbc:sqlserver://MGADSTest01.ny.mgasystems.com:1433;"
                        + "database=Fortegra_Test;"
                        + "user=mperkins@fortegra.com_DBO;"
                        + "password=the2.sofa.chanted.a5.ragnet.relieves.a.sTump;";
        PaymentRepository pr = new PaymentRepository("test");
        String result = pr.insertClaimPayment("{\n" +
                "  \"ClaimId\": 41970,\n" +
                "  \"ClaimantGuid\": \"810A04AC-82BD-45C0-A6E5-003F7E963754\",\n" +
                "  \"CoverageTypeId\": null,\n" +
                "  \"CoverageTypeDescriptionId\": null,\n" +
                "  \"ResPayTypeId\": 3,\n" +
                "  \"ResPaySubTypeId\": null,\n" +
                "  \"ResPayAmount\": \"12.52\",\n" +
                "  \"CreatedByGuid\": \"810A04AC-82BD-45C0-A6E5-003F7E963754\",\n" +
                "  \"Comments\":null,\n" +
                "  \"PayeeGuid\": null,\n" +
                "  \"PayeeName\": null,\n" +
                "  \"IsPayeeClaimant\": 0,\n" +
                "  \"IsPayeeInsured\": 0,\n" +
                "  \"AdditionalPayees\": null,\n" +
                "  \"Override_Address1\": null,\n" +
                "  \"Override_Address2\": null,\n" +
                "  \"Override_City\": null,\n" +
                "  \"Override_State\": null,\n" +
                "  \"Override_ZipCode\": null,  \n" +
                "  \"Override_ISOCountryCode\": null,  \n" +
                "  \"dateCreated\": null,  \n" +
                "  \"PaymentResPayId\": null,  \n" +
                "  \"IsPayeeDefenseAttorney\": 0,  \n" +
                "  \"IsPayeeClaimantAttorney\": 0, \n" +
                "  \"ChildLineGUID\": \"00000000-0000-0000-0000-000000000000\", \n" +
                "  \"PaymentType\": 0\n" +
                "}");

//        String result = pr.insert_Claim_Payment(
//                41970,
//                "810A04AC-82BD-45C0-A6E5-003F7E963754",
//            null,
//                null,
//                3,
//                null,
//                BigDecimal.valueOf(12.52),
//                "810A04AC-82BD-45C0-A6E5-003F7E963754",
//                null,
//                null,
//                null,
//                0,
//                0,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                null,
//                "00000000-0000-0000-0000-000000000000",
//                0);
        System.out.println(result);

//        String result = pr.void_Claim_Payment(connectionUrl,
//                304427,19672,
//                "15FD715F-7C52-45D9-8625-B1D3796B8A65",
//                "15779D93-532B-424C-9399-04BF6CDF0739");

        /*
           Test Case for cleanup
         */
//        CommonUtil cu = new CommonUtil();
//        String re = cu.cleanMidServer("Fortegra",true);
//        System.out.println(re);

    }
}
