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
                "  \"ClaimId\": 55859,\n" +
                "  \"ClaimantGuid\": \"B7D30CD9-1EE5-43C4-8321-6763EA0FDE5F\",\n" +
                "  \"CoverageTypeId\": null,\n" +
                "  \"CoverageTypeDescriptionId\": null,\n" +
                "  \"ResPayTypeId\": 2,\n" +
                "  \"ResPaySubTypeId\": null,\n" +
                "  \"ResPayAmount\": \"200\",\n" +
                "  \"CreatedByGuid\": \"B19BE45A-B0EF-4182-892A-3D6C095640FF\",\n" +
                "  \"Comments\":\"Please include this on the memo.\",\n" +
                "  \"PayeeGuid\": \"201EFB80-0461-449E-A973-DA97A136F98A\",\n" +
                "  \"PayeeName\": \"Wingate Russotti Shapiro Moses & Halperin, LLP\",\n" +
                "  \"IsPayeeClaimant\": 1,\n" +
                "  \"IsPayeeInsured\": 1,\n" +
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
                "  \"ChildLineGUID\": \"DF49CDBD-71E6-4AA7-B1D9-172631437980\", \n" +
                "  \"PaymentType\": 1\n" +
                "}");
        assertNotEquals(result,"");
    }

    @Test
    void shouldVoidPayment() throws IOException, SQLException {
        PaymentRepository pr = new PaymentRepository("test");
        String result = pr.void_Claim_Payment(
                423092,55859,
        "B7D30CD9-1EE5-43C4-8321-6763EA0FDE5F",
        "15779D93-532B-424C-9399-04BF6CDF0739",
                "DF49CDBD-71E6-4AA7-B1D9-172631437980");
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


        String payload = "{\"ClaimId\":56075,\"ClaimantGuid\":\"E9B8E9AA-72BC-4C9B-92B7-44AA2D3775D6\",\"CoverageTypeId\":4,\"CoverageTypeDescriptionId\":null,\"ResPayTypeId\":3,\"ResPaySubTypeId\":null,\"ResPayAmount\":\"-300\",\"CreatedByGuid\":\"15779D93-532B-424C-9399-04BF6CDF0739\",\"Comments\":null,\"PayeeGuid\":null,\"PayeeName\":null,\"IsPayeeClaimant\":0,\"IsPayeeInsured\":0,\"AdditionalPayees\":null,\"Override_Address1\":null,\"Override_Address2\":null,\"Override_City\":null,\"Override_State\":null,\"Override_ZipCode\":null,\"Override_ISOCountryCode\":null,\"dateCreated\":null,\"PaymentResPayId\":423756,\"IsPayeeDefenseAttorney\":0,\"IsPayeeClaimantAttorney\":null,\"PaymentReturnResPayId\":423756,\"RecoveryCheckNum\":null,\"ChildLineGUID\":\"DF49CDBD-71E6-4AA7-B1D9-172631437980\",\"PaymentType\":null}";
        String result = pr.insertPaymentReturn(payload);


        assertNotEquals(result,"");
    }

    @Test
    void shouldgetReserveSummary() throws IOException, SQLException {
        PaymentRepository pr = new PaymentRepository("test");
        String result = pr.getReserveSummary(54162);
        assertNotEquals(result,"");
    }
}
