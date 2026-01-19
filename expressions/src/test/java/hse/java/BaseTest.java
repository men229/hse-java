package hse.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BaseTest {

    @Test
    void test() {
        int actually = 1;
        Assertions.assertEquals(1, actually);
    }
}
