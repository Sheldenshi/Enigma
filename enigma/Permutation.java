package enigma;


import java.util.Arrays;
import java.util.HashMap;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Shelden Shi
 */

class Permutation {
    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        cycles = cycles.replaceAll("\\s+", "");
        _cycle = cycles;
        mapPermuteChar = new HashMap<>();
        mapInvertChar = new HashMap<>();
        mapPermuteInt = new HashMap<>();
        mapInvertInt = new HashMap<>();
        cycleArray = cycles.split("[\\(||\\)]");
        cycleArray = Arrays.stream(cycleArray)
                .filter(x -> !x.isEmpty())
                .toArray(String[]::new);
        for (String x : cycleArray) {
            addCycle(x);
        }
        for (String x : _alphabet.alphabet()) {
            if (!mapPermuteChar.containsKey(x)) {
                mapToItself(x);
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    public void addCycle(String cycle) {
        String[] xCycle = cycle.replaceAll("\\s+", "").split("");
        int xSize = xCycle.length;
        if (xSize == 1) {
            mapToItself(xCycle[0]);
        } else {
            for (int i = 0; i < xSize; i++) {
                if (i == 0) {
                    mapPermuteChar.put(xCycle[0], xCycle[1]);
                    mapPermuteInt.put(_alphabet.toInt(xCycle[0].charAt(0)),
                            _alphabet.toInt(xCycle[1].charAt(0)));
                    mapInvertChar.put(xCycle[0], xCycle[xSize - 1]);
                    mapInvertInt.put(_alphabet.toInt(xCycle[0].charAt(0)),
                            _alphabet.toInt(xCycle[xSize - 1].charAt(0)));
                } else if (i == xSize - 1) {
                    mapPermuteChar.put(xCycle[i], xCycle[0]);
                    mapPermuteInt.put(_alphabet.toInt(xCycle[i].charAt(0)),
                            _alphabet.toInt(xCycle[0].charAt(0)));
                    mapInvertChar.put(xCycle[i], xCycle[i - 1]);
                    mapInvertInt.put(_alphabet.toInt(xCycle[i].charAt(0)),
                            _alphabet.toInt(xCycle[i - 1].charAt(0)));
                } else {
                    mapPermuteChar.put(xCycle[i], xCycle[i + 1]);
                    mapPermuteInt.put(_alphabet.toInt(xCycle[i].charAt(0)),
                            _alphabet.toInt(xCycle[i + 1].charAt(0)));
                    mapInvertChar.put(xCycle[i], xCycle[i - 1]);
                    mapInvertInt.put(_alphabet.toInt(xCycle[i].charAt(0)),
                            _alphabet.toInt(xCycle[i - 1].charAt(0)));
                }
            }
        }
    }
    /** Mapping to itself.
     * @param lonely lonely chars */
    private void mapToItself(String lonely) {
        mapPermuteChar.put(lonely, lonely);
        mapPermuteInt.put(_alphabet.toInt(lonely.charAt(0)),
                _alphabet.toInt(lonely.charAt(0)));
        mapInvertChar.put(lonely, lonely);
        mapInvertInt.put(_alphabet.toInt(lonely.charAt(0)),
                _alphabet.toInt(lonely.charAt(0)));
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int index = wrap(p);
        if (!mapPermuteInt.containsKey(index)) {
            throw EnigmaException.error("Not In Alphabet!");
        }
        if (derangement()) {
            return p;
        } else {
            return mapPermuteInt.get(index);
        }
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int index = wrap(c);
        if (!mapInvertInt.containsKey(index)) {
            throw EnigmaException.error("Not In Alphabet!");
        }
        if (derangement()) {
            return c;
        } else {
            return mapInvertInt.get(index);
        }
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        String s = String.valueOf(p);
        if (!mapPermuteChar.containsKey(s)) {
            throw EnigmaException.error("Not In Alphabet!");
        }
        if (derangement()) {
            return p;
        } else {
            return mapPermuteChar.get(s).charAt(0);
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        String s = String.valueOf(c);
        if (!mapInvertChar.containsKey(String.valueOf(s))) {
            throw EnigmaException.error("Not In Alphabet!");
        }
        if (derangement()) {
            return c;
        } else {
            return mapInvertChar.get(s).charAt(0);
        }
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return _cycle.equals("");
    }
    /** Alphabet of this permutation.
     * @return map*/
    HashMap getMapPermuteChar() {
        return mapPermuteChar;
    }
    /** Alphabet of this permutation.
     * @return map*/
    HashMap getMapPermuteInt() {
        return mapPermuteInt;
    }
    /** Alphabet of this permutation.
     * @return map*/
    HashMap getMapInvertChar() {
        return mapInvertChar;
    }
    /** Alphabet of this permutation.
     * @return map*/
    HashMap getMapInvertInt() {
        return mapInvertInt;
    }
    /** Alphabet of this permutation.
     * @return alphabet*/
    Alphabet getAlphabet() {
        return _alphabet;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** Alphabet of this permutation. */
    private String[] cycleArray;
    /** Alphabet of this permutation. */
    private String _cycle;
    /** Alphabet of this permutation. */
    private HashMap<String, String> mapPermuteChar;
    /** Alphabet of this permutation. */
    private HashMap<String, String> mapInvertChar;
    /** Alphabet of this permutation. */
    private HashMap<Integer, Integer> mapPermuteInt;
    /** Alphabet of this permutation. */
    private HashMap<Integer, Integer> mapInvertInt;
}
