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
        String connectionUrl =
                "jdbc:sqlserver://MGADSTest01.ny.mgasystems.com:1433;"
                        + "database=Fortegra_Test;"
                        + "user=mperkins@fortegra.com_DBO;"
                        + "password=the2.sofa.chanted.a5.ragnet.relieves.a.sTump;";
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
        String connectionUrl =
                "jdbc:sqlserver://MGADSTest01.ny.mgasystems.com:1433;"
                        + "database=Fortegra_Test;"
                        + "user=mperkins@fortegra.com_DBO;"
                        + "password=the2.sofa.chanted.a5.ragnet.relieves.a.sTump;";
        PaymentRepository pr = new PaymentRepository("test");
        String result = pr.void_Claim_Payment(connectionUrl,
        304427,19672,
        "15FD715F-7C52-45D9-8625-B1D3796B8A65",
        "15779D93-532B-424C-9399-04BF6CDF0739");
        assertNotEquals(result,"");
    }
}
