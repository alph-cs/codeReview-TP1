package fr.isima.codereview.tp1;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AwesomePasswordCheckerTest {

    private AwesomePasswordChecker checker;
    private static final String zero24 = "0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;0.0;";
    private static final String TEST_DATA = "1.0;2.0;3.0;4.0;5.0;6.0;" + zero24 + zero24 + "0.0;";

    @BeforeEach
    void setUp() throws IOException {
        // Réinitialiser l'instance singleton entre les tests
        java.lang.reflect.Field instance;
        try {
            instance = AwesomePasswordChecker.class.getDeclaredField("instance");
            instance.setAccessible(true);
            instance.set(null, null);
        } catch (Exception e) {
            fail("Erreur lors de la réinitialisation du singleton");
        }

        // Créer une instance avec des données de test
        InputStream is = new ByteArrayInputStream(TEST_DATA.getBytes());
        checker = new AwesomePasswordChecker(is);
    }

    @Test
    void testGetInstance() throws IOException {
        // Test getInstance() sans paramètre
        AwesomePasswordChecker instance1 = AwesomePasswordChecker.getInstance();
        assertNotNull(instance1);

        // Test getInstance() avec le même appel
        AwesomePasswordChecker instance2 = AwesomePasswordChecker.getInstance();
        assertSame(instance1, instance2, "Les instances doivent être identiques (design patern Singleton)");
    }

    @Test
    void testMaskAff() throws IOException {
        // Test avec caractères fréquents minuscules
        int[] mask1 = checker.maskAff("esaitunrol");
        assertArrayEquals(new int[]{1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, mask1);

        // Test avec caractères fréquents majuscules
        int[] mask2 = checker.maskAff("ESAITUNROL");
        assertArrayEquals(new int[]{3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, mask2);

        // Test avec caractères spéciaux
        int[] mask3 = checker.maskAff("><-?.!@%&");
        assertArrayEquals(new int[]{6,6,6,6,6,6,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, mask3);

        // Test avec chiffres
        int[] mask4 = checker.maskAff("1234567890");
        assertArrayEquals(new int[]{5,5,5,5,5,5,5,5,5,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, mask4);

        // Test avec caractères mixtes
        int[] mask5 = checker.maskAff("aB1!xY2@");
        assertArrayEquals(new int[]{1,4,5,6,2,4,5,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}, mask5);

        // Test avec un mot de passe plus long que 28 caractères
        int[] mask6 = checker.maskAff("abcdefghijklmnopqrstuvwxyzABCD");
        assertEquals(28, mask6.length, "Le masque doit être limité à 28 caractères");
    }

    @Test
    void testGetDistance() throws IOException {
        // Test avec différents types de mots de passe
        double distance1 = checker.getDistance("password123");
        assertTrue(distance1 > 0, "La distance doit être positive");

        double distance2 = checker.getDistance("PASSWORD123");
        assertTrue(distance2 > 0, "La distance doit être positive");

        double distance3 = checker.getDistance("P@ssw0rd!");
        assertTrue(distance3 > 0, "La distance doit être positive");

        // Test avec un mot de passe vide
        double distance4 = checker.getDistance("");
        assertTrue(distance4 > 0, "La distance doit être positive même pour un mot de passe vide");
    }

    @Test
    void testComputeMD5() {
        // Test avec des chaînes connues et leurs hashes MD5 correspondants
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", AwesomePasswordChecker.computeMD5(""));
        assertEquals("900150983cd24fb0d6963f7d28e17f72", AwesomePasswordChecker.computeMD5("abc"));
        assertEquals("098f6bcd4621d373cade4e832627b4f6", AwesomePasswordChecker.computeMD5("test"));

        // Test avec un long mot de passe
        String longPassword = "ThisIsAVeryLongPasswordForTestingPurposes123!@#";
        assertNotNull(AwesomePasswordChecker.computeMD5(longPassword));
        assertEquals(32, AwesomePasswordChecker.computeMD5(longPassword).length());
    }

    @Test
    void testGetInstanceWithFile() throws IOException {
        // Créer un fichier temporaire pour le test
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();

        // Test getInstance avec fichier
        AwesomePasswordChecker instance1 = AwesomePasswordChecker.getInstance(tempFile);
        assertNotNull(instance1);

        // Vérifier que c'est toujours la même instance
        AwesomePasswordChecker instance2 = AwesomePasswordChecker.getInstance(tempFile);
        assertSame(instance1, instance2);
    }
}