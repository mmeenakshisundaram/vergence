package repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
public class ClaimRepositoryTest {

    @Test
    void shouldUpdateAccidentInformation() throws IOException, SQLException {
        String connectionUrl =
                "jdbc:sqlserver://MGADSTest01.ny.mgasystems.com:1433;"
                        + "database=Fortegra_Test;"
                        + "user=mperkins@fortegra.com_DBO;"
                        + "password=the2.sofa.chanted.a5.ragnet.relieves.a.sTump;";
        ClaimRepository pr = new ClaimRepository("test");
        String result = pr.updateAccidentInformation("{\n" +
                "  \"AccidentInformationId\": 1208,\n" +
                "  \"Address1\": \"test\",\n" +
                "  \"City\": \"test\",\n" +
                "  \"State\": \"test\",\n" +
                "  \"ZipCode\": \"test\",\n" +
                "  \"ISOCountryCode\": \"test\",\n" +
                "  \"AccidentDescription\": \"test\",\n" +
                "  \"AccidentTime\": \"test\",\n" +
                "  \"County\":\"Test\",\n" +
                "  \"AccidentTypeId\": null,\n" +
                "}");
        assertNotEquals(result,"");
    }

    @Test
    void shouldInsertAccidentInformation() throws IOException, SQLException {
        String connectionUrl =
                "jdbc:sqlserver://MGADSTest01.ny.mgasystems.com:1433;"
                        + "database=Fortegra_Test;"
                        + "user=mperkins@fortegra.com_DBO;"
                        + "password=the2.sofa.chanted.a5.ragnet.relieves.a.sTump;";
        ClaimRepository pr = new ClaimRepository("test");
        String result = pr.insertAccidentInformation("{\n" +
                "  \"ClaimId\": 1239,\n" +
                "  \"Address1\": \"test\",\n" +
                "  \"City\": \"test\",\n" +
                "  \"State\": \"test\",\n" +
                "  \"ZipCode\": \"test\",\n" +
                "  \"ISOCountryCode\": \"test\",\n" +
                "  \"AccidentDescription\": \"test\",\n" +
                "  \"AccidentTime\": \"test\",\n" +
                "  \"County\":\"Test\",\n" +
                "  \"AccidentTypeId\": null,\n" +
                "}");
        assertNotEquals(result,"");
    }
}
