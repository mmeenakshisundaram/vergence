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
                "  \"ClaimId\": 41970,\n" +
                "  \"ClaimantGuid\": \"810A04AC-82BD-45C0-A6E5-003F7E963754\",\n" +
                "  \"CoverageTypeId\": 5,\n" +
                "  \"CoverageTypeDescriptionId\": null,\n" +
                "  \"ResPayTypeId\": 3,\n" +
                "  \"ResPaySubTypeId\": null,\n" +
                "  \"ResPayAmount\": \"12.52\",\n" +
                "  \"CreatedByGuid\": \"810A04AC-82BD-45C0-A6E5-003F7E963754\",\n" +
                "  \"Comments\":\"Test\",\n" +
                "  \"PayeeGuid\": \"810A04AC-82BD-45C0-A6E5-003F7E963754\",\n" +
                "  \"PayeeName\": \"Test\",\n" +
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
                "15779D93-532B-424C-9399-04BF6CDF0739");
        assertNotEquals(result,"");
    }

    @Test
    void shouldInsertPaymentReturn() throws IOException {
        PaymentRepository pr = new PaymentRepository("test");
        String result = pr.insertPaymentReturn("{\n" +
                "  \"ClaimId\": 1243,\n" +
                "  \"ClaimantGuid\": \"A79F0332-168F-44EC-905D-266D6C38D041\",\n" +
                "  \"CoverageTypeId\": null,\n" +
                "  \"CoverageTypeDescriptionId\": null,\n" +
                "  \"ResPayTypeId\": 2,\n" +
                "  \"ResPaySubTypeId\": null,\n" +
                "  \"ResPayAmount\": \"-7242.23\",\n" +
                "  \"CreatedByGuid\": \"15779D93-532B-424C-9399-04BF6CDF0739\",\n" +
                "  \"Comments\":\"\",\n" +
                "  \"PayeeGuid\": \"A39F0332-168F-44EC-905D-266D6C38D041\",\n" +
                "  \"PayeeName\": \"Fortegra Specialty Insurance Company\",\n" +
                "  \"IsPayeeClaimant\": 1,\n" +
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
                "  \"PaymentReturnResPayId\": 4026, \n" +
                "  \"RecoveryCheckNum\": \"4449\",\n" +
                "  \"ChildLineGUID\": \"00000000-0000-0000-0000-000000000000\", \n" +
                "  \"PaymentType\": 0\n" +
                "}");
        assertNotEquals(result,"");
    }
}
