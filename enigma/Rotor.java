package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Shelden Shi
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        set(0);
        setRing(0);
    }


    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return position;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        position = mod(posn);
    }

    /** Set ring.
     * @param ringInput ring*/
    void setRing(int ringInput) {
        _ring = ringInput;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        position = _permutation.getAlphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        return mod(_permutation.permute(mod
                (p + position - _ring)) - position + _ring);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        return mod(_permutation.invert(mod
                (e + position - _ring)) - position + _ring);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int mod(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }
    /** Advance me one position, if possible. By default, does nothing.
     * @return ring */
    int getRing() {
        return _ring;
    }
    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    protected final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    protected Permutation _permutation;
    /** The permutation implemented by this rotor in its 0 position. */
    private int position;
    /** The permutation implemented by this rotor in its 0 position. */
    private int _ring;

}
