package enigma;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/** Class that represents a complete enigma machine.
 *  @author Shelden Shi
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _myRotors = new Rotor[_numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        int count = 0;
        int numMovingRotor = 0;
        for (String x : rotors) {
            for (Rotor y : _allRotors) {
                if (count == _numRotors) {
                    break;
                }
                if (y.name().equals(x)) {
                    if (count == 0 && !y.reflecting()) {
                        throw EnigmaException.error(
                                "The first rotor has to "
                                        + "be a reflector");
                    } else if (count > 0 && !y.rotates()
                            && numMovingRotor > 0) {
                        throw EnigmaException.error(
                                "A FixedRotor can not be "
                                        + "placed on the right side of "
                                        + "a moving rotor");
                    } else if (y.rotates()) {
                        numMovingRotor += 1;
                        if (numMovingRotor > _pawls) {
                            throw EnigmaException.error(
                                    "too many moving rotors");
                        }
                    }
                    _myRotors[count] = y;
                    count++;
                }
            }
        }
        if (_myRotors.length != _numRotors) {
            throw EnigmaException.error(
                    "Names in rotors do not match "
                            + "everything in _allrotors");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        String[] settingList = setting.split("");
        if (settingList.length != _numRotors - 1) {
            throw EnigmaException.error(
                    "Wheel settings too short/long");
        }
        for (String s : settingList) {
            List<String> list = Arrays.asList(_alphabet.alphabet());
            if (!list.contains(s)) {
                throw EnigmaException.error(
                        "Bad character in wheel settings");
            }
        }
        int count = 0;
        for (Rotor x : _myRotors) {
            if (!x.reflecting() && (count < settingList.length)) {
                x.set(settingList[count].charAt(0));
                count++;
            }
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }
    /** Set the ring.
     * @param ring  ring*/
    void setRing(String ring) {
        String[] ringList = ring.split("");
        if (ringList.length != _numRotors - 1) {
            throw EnigmaException.error(
                    "Wheel settings too short/long");
        }
        int[] ringListInt = new int[ringList.length];
        for (int counter = 0; counter < ringList.length; counter++) {
            ringListInt[counter] = _alphabet.toInt(ringList[counter].charAt(0));
        }

        int count = 0;
        for (Rotor x : _myRotors) {
            if (!x.reflecting() && (count < ringList.length)) {
                x.setRing(ringListInt[count]);
                count++;
            }
        }

    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        int curr = _plugboard.permute(c);
        for (int indexForward = 1; indexForward <= _numRotors; indexForward++) {
            Rotor currRotor = _myRotors[_numRotors - indexForward];
            curr = currRotor.convertForward(curr);
        }
        for (int indexBackward = 1;
             indexBackward < _numRotors; indexBackward++) {
            Rotor currRotor = _myRotors[indexBackward];
            curr = currRotor.convertBackward(curr);
        }
        return _plugboard.permute(curr);
    }
    /** advancing the machine. */
    void advanceRotors() {
        int indexAdvance = 1;
        boolean rotate = true;
        boolean preAdvanced = true;
        while (!_myRotors[_numRotors - indexAdvance].reflecting()
                && (_numRotors - indexAdvance) >= 0) {
            Rotor currRotor = _myRotors[_numRotors - indexAdvance];
            if (rotate && currRotor.rotates()
                    && currRotor.atNotch()
                    && preAdvanced) {
                currRotor.advance();
            } else if (preAdvanced
                    && currRotor.rotates()
                    && currRotor.atNotch()
                    && (_myRotors[_numRotors - indexAdvance - 1].atNotch())) {
                currRotor.advance();
                rotate = true;
            } else if (preAdvanced
                    && currRotor.rotates()
                    && currRotor.atNotch()
                    && _myRotors[_numRotors - indexAdvance - 1].rotates()) {
                currRotor.advance();
                rotate = true;

            } else if (rotate && currRotor.rotates()
                    && indexAdvance == 1) {
                currRotor.advance();
                rotate = false;
            } else if (rotate && currRotor.rotates()) {
                currRotor.advance();
                rotate = false;
                preAdvanced = false;
            }
            indexAdvance++;
        }
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        String[] msgLines = msg.split("");
        int count = 0;
        for (String x : msgLines) {
            x = x.replaceAll("\\s+", "");
            String[] msgList = x.split("");
            for (String y : msgList) {
                if (y.equals("")) {
                    result += "";
                } else {
                    int temp = convert(_alphabet.toInt(y.charAt(0)));
                    result = result + _alphabet.toChar(temp);
                    count++;
                }
                if (count == 5) {
                    result += " ";
                    count = 0;
                }
            }
        }
        return result;
    }
    /** Returns _myRotors. */
    Rotor[] getMyRotors() {
        return _myRotors;
    }
    /** Returns _plugboard. */
    Permutation getPlugboard() {
        return _plugboard;
    }
    /** Returns _plugboard. */
    Alphabet getAlphabet() {
        return _alphabet;
    }


    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Number of rotors. */
    private final int _numRotors;
    /** Number of pawls. */
    private final int _pawls;
    /** allrotors. */
    private final Collection<Rotor> _allRotors;
    /** rotors that the machine uses. */
    private Rotor[] _myRotors;
    /** plugboard. */
    private Permutation _plugboard = new Permutation("()", new Alphabet());

}
