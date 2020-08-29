package enigma;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Shelden Shi
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        notchesArray = notches.split("");
        _notches = notches;
    }

    @Override
    boolean atNotch() {
        for (String x : notchesArray) {
            int temp = _permutation.getAlphabet().toInt(x.charAt(0));
            if (temp == setting()) {
                return true;
            }
        }
        return false;
    }
    @Override
    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        if (!_notches.equals("")) {
            return true;
        }
        return false;
    }
    /** Updata notches.
     * @param newNotches new*/
    void updataNotches(String newNotches) {
        _notches = newNotches;
    }
    @Override
    void advance() {
        set(setting() + 1);
    }
    /** Array of notches. */
    private String[] notchesArray;
    /** notches. */
    private String _notches;
}
