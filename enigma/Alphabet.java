package enigma;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Shelden Shi
 */
class Alphabet {
    /** SMD. */
    private String[] alphabets;
    /** SMD. */
    private String _chars;
    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        alphabets = chars.replaceAll("\\s+", "").split("");
        alphabets = Arrays.stream(alphabets).filter
                (x -> !x.isEmpty()).
                toArray(String[]::new);
        _chars = chars;
        Set<String> set = new HashSet<String>();
        for (String x : alphabets) {
            if (!set.add(x)) {
                throw EnigmaException.error(
                        "There are duplicate "
                                + "alphabets in the arguement");
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return alphabet().length;
    }
    /** Changes alphabet.
     * @param i a number*/
    void updateAlphabet(int i) {
        String[] newAlpha = new String[alphabet().length];
        for (int x = 0; x < alphabet().length; x++) {
            newAlpha[x] = alphabets[mod(x + i)];
        }
        alphabets = newAlpha;
    }
    /** Return the value of P modulo the size of this permutation. */
    final int mod(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }
    /** Returns alphabet. */
    String[] alphabet() {
        String[] alphabetCopy = alphabets;
        return alphabetCopy;
    }
    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        String s = String.valueOf(ch);
        return _chars.contains(s);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        char c = alphabets[index].charAt(0);
        return c;
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (this.contains(ch)) {
            String s = String.valueOf(ch);
            for (int i = 0; i < alphabets.length; i++) {
                if (alphabets[i].equals(s)) {
                    return i;
                }
            }
        }
        throw EnigmaException.error(
                "Arguement is not in the alphabet");
    }

}
