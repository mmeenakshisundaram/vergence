package repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import util.CommonUtil;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
public class PaymentRepositoryTest {

    @Test
    void shouldInsertClaimPayment() throws IOException {
        PaymentRepository pr = new PaymentRepository("test");
        String result = pr.insertClaimPayment("{\n" +
                "  \"ClaimId\": 24093,\n" +
                "  \"ClaimantGuid\": \"45EA7CEE-8358-4D3C-9764-B0C289AC6B42\",\n" +
                "  \"CoverageTypeId\": 4,\n" +
                "  \"CoverageTypeDescriptionId\": null,\n" +
                "  \"ResPayTypeId\": 2,\n" +
                "  \"ResPaySubTypeId\": null,\n" +
                "  \"ResPayAmount\": \"3\",\n" +
                "  \"CreatedByGuid\": \"B19BE45A-B0EF-4182-892A-3D6C095640FF\",\n" +
                "  \"Comments\":\"Please include this on the memo.\",\n" +
                "  \"PayeeGuid\": \"201EFB80-0461-449E-A973-DA97A136F98A\",\n" +
                "  \"PayeeName\": \"Wingate Russotti Shapiro Moses & Halperin, LLP\",\n" +
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
                "  \"PaymentType\": 1\n" +
                "}");
        assertNotEquals(result,"");
    }

    @Test
    void shouldVoidPayment() throws IOException, SQLException {
        PaymentRepository pr = new PaymentRepository("test");
        String result = pr.void_Claim_Payment(
                303268,40945,
        "329858C3-DD40-4043-8174-98C28D2FC634",
        "15779D93-532B-424C-9399-04BF6CDF0739");
        assertNotEquals(result,"");
    }

    @Test
    void shouldtransferClaimPayment() throws IOException, SQLException {

        PaymentRepository pr = new PaymentRepository("test");
        String result = pr.transferClaimPayment(304702,
                "15779D93-532B-424C-9399-04BF6CDF0739",0);
        assertNotEquals(result,"");
    }

    @Test
    void shouldInsertPaymentReturn() throws IOException {
        PaymentRepository pr = new PaymentRepository("test");
//        String result = pr.insertPaymentReturn("{\n" +
//                "  \"ClaimId\": 1243,\n" +
//                "  \"ClaimantGuid\": \"A79F0332-168F-44EC-905D-266D6C38D041\",\n" +
//                "  \"CoverageTypeId\": null,\n" +
//                "  \"CoverageTypeDescriptionId\": null,\n" +
//                "  \"ResPayTypeId\": 2,\n" +
//                "  \"ResPaySubTypeId\": null,\n" +
//                "  \"ResPayAmount\": \"-7242.23\",\n" +
//                "  \"CreatedByGuid\": \"15779D93-532B-424C-9399-04BF6CDF0739\",\n" +
//                "  \"Comments\":\"\",\n" +
//                "  \"PayeeGuid\": \"A39F0332-168F-44EC-905D-266D6C38D041\",\n" +
//                "  \"PayeeName\": \"Fortegra Specialty Insurance Company\",\n" +
//                "  \"IsPayeeClaimant\": 1,\n" +
//                "  \"IsPayeeInsured\": 0,\n" +
//                "  \"AdditionalPayees\": null,\n" +
//                "  \"Override_Address1\": null,\n" +
//                "  \"Override_Address2\": null,\n" +
//                "  \"Override_City\": null,\n" +
//                "  \"Override_State\": null,\n" +
//                "  \"Override_ZipCode\": null,  \n" +
//                "  \"Override_ISOCountryCode\": null,  \n" +
//                "  \"dateCreated\": null,  \n" +
//                "  \"PaymentResPayId\": null,  \n" +
//                "  \"IsPayeeDefenseAttorney\": 0,  \n" +
//                "  \"IsPayeeClaimantAttorney\": 0, \n" +
//                "  \"PaymentReturnResPayId\": 4026, \n" +
//                "  \"RecoveryCheckNum\": \"4449\",\n" +
//                "  \"ChildLineGUID\": \"00000000-0000-0000-0000-000000000000\", \n" +
//                "  \"PaymentType\": 0\n" +
//                "}");


        String payload = "{\"ClaimId\":41991,\"ClaimantGuid\":\"557C0B12-3598-442A-B0C8-29A7D2F85A8F\",\"CoverageTypeId\":4,\"CoverageTypeDescriptionId\":null,\"ResPayTypeId\":5,\"ResPaySubTypeId\":null,\"ResPayAmount\":\"-40\",\"CreatedByGuid\":\"A3A0239A-58C9-41CA-B4CB-79F1EC0F09A9\",\"Comments\":null,\"PayeeGuid\":\"62174782-FD66-4187-83F8-1A78B2271236\",\"PayeeName\":\"Drummond Law Firm\",\"IsPayeeClaimant\":0,\"IsPayeeInsured\":0,\"AdditionalPayees\":null,\"Override_Address1\":null,\"Override_Address2\":null,\"Override_City\":null,\"Override_State\":null,\"Override_ZipCode\":null,\"Override_ISOCountryCode\":null,\"dateCreated\":null,\"PaymentResPayId\":304789,\"IsPayeeDefenseAttorney\":0,\"IsPayeeClaimantAttorney\":null,\"PaymentReturnResPayId\":304789,\"RecoveryCheckNum\":null,\"ChildLineGUID\":\"6369C391-428E-4C33-A421-33E5D16FD844\",\"PaymentType\":null}";
        String result = pr.insertPaymentReturn(payload);


        assertNotEquals(result,"");
    }
}
