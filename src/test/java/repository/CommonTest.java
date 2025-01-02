package repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import util.CommonUtil;

import java.io.IOException;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class CommonTest {

    @Test
    void shouldCleanMidServer() throws IOException {
        CommonUtil cu = new CommonUtil();
        String re = cu.cleanMidServer("Fortegra",true);
        System.out.println(re);
    }
}
