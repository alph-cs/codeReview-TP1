package fr.isima.codereview.tp1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AwesomePasswordCheckerTest {

    private AwesomePasswordChecker passwordChecker;

	    @Test
    public void testComputeMD5() {
        String input = "test";
        String expectedHash = "098f6bcd4621d373cade4e832627b4f6";

        String actualHash = AwesomePasswordChecker.computeMD5(input);

        assertEquals(expectedHash, actualHash, "MD5 hash does not match expected value.");
    }
}
