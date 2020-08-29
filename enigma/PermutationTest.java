package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;


import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Shelden Shi
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
    Permutation a = new Permutation("(QWER)(T)", new Alphabet("QWERTY"));
    Permutation b = new Permutation("(RE) (JK)", new Alphabet("JERK"));
    @Test
    public void testInvertChar() {

        assertEquals('B', p.invert('A'));
        assertEquals('A', p.invert('C'));
        assertEquals('C', p.invert('D'));
        assertEquals('D', p.invert('B'));


        assertEquals('Q', a.invert('W'));
        assertEquals('W', a.invert('E'));
        assertEquals('E', a.invert('R'));
        assertEquals('R', a.invert('Q'));
        assertEquals('T', a.invert('T'));
        assertEquals('Y', a.invert('Y'));


        assertEquals('K', b.invert('J'));
        assertEquals('J', b.invert('K'));
        assertEquals('E', b.invert('R'));
        assertEquals('R', b.invert('E'));
    }
    @Test
    public void testPermuteChar() {
        assertEquals('A', p.permute('B'));
        assertEquals('C', p.permute('A'));
        assertEquals('D', p.permute('C'));
        assertEquals('B', p.permute('D'));

        assertEquals('W', a.permute('Q'));
        assertEquals('Q', a.permute('R'));
        assertEquals('E', a.permute('W'));
        assertEquals('R', a.permute('E'));
        assertEquals('T', a.permute('T'));
        assertEquals('Y', a.permute('Y'));

        assertEquals('K', b.permute('J'));
        assertEquals('J', b.permute('K'));
        assertEquals('E', b.permute('R'));
        assertEquals('R', b.permute('E'));
    }
    @Test
    public void testInvertInt() {
        assertEquals(1, p.invert(0));
        assertEquals(0, p.invert(2));
        assertEquals(2, p.invert(3));
        assertEquals(3, p.invert(1));

        assertEquals(0, a.invert(1));
        assertEquals(1, a.invert(2));
        assertEquals(2, a.invert(3));
        assertEquals(3, a.invert(0));
        assertEquals(4, a.invert(4));
        assertEquals(0, a.invert(7));
        assertEquals(5, a.invert(5));

        assertEquals(3, b.invert(0));
        assertEquals(0, b.invert(3));
        assertEquals(1, b.invert(2));
        assertEquals(2, b.invert(1));
    }

    @Test
    public void testPermuteInt() {
        assertEquals(0, p.permute(1));
        assertEquals(2, p.permute(0));
        assertEquals(3, p.permute(2));
        assertEquals(1, p.permute(3));

        assertEquals(1, a.permute(0));
        assertEquals(2, a.permute(1));
        assertEquals(3, a.permute(2));
        assertEquals(0, a.permute(3));
        assertEquals(4, a.permute(4));
        assertEquals(2, a.permute(7));
        assertEquals(5, a.permute(5));

        assertEquals(3, b.invert(0));
        assertEquals(0, b.invert(3));
        assertEquals(1, b.invert(2));
        assertEquals(2, b.invert(1));
    }
    @Test
    public void testSize() {
        Alphabet d = new Alphabet("ABCD");
        assertEquals(4, d.size());

        Alphabet e = new Alphabet("");
        assertEquals(0, e.size());

    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {

        p.invert('F');
        p.invert('W');

        a.invert('F');

        b.invert('A');
    }

    @Test
    public void testCheckPerm() {
        alpha = "ABCD";
        perm = new Permutation("(AB) (CD)", new Alphabet(alpha));
        checkPerm("a", "ABCD", "BADC");
    }
}
