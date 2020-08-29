package enigma;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests of the Alphabet class.
 *  @author Shelden Shi
 */

public class AlphabetTest {
    Alphabet a = new Alphabet("ABC");
    @Test
    public void sizeTest() {
        assertEquals(3, a.size());
    }
    @Test
    public void containsTest() {
        assertTrue(a.contains('A'));
        assertFalse(a.contains('D'));
    }
    @Test
    public void toCharTest() {
        assertEquals('A', a.toChar(0));
        assertEquals('B', a.toChar(1));
    }
    @Test
    public void toIntTest() {
        assertEquals(0, a.toInt('A'));
        assertEquals(-1, a.toInt('D'));
    }
    @Test(expected = EnigmaException.class)
    public void toIntErrorTest() {
        a.toInt('E');
    }
}
