package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.NoSuchElementException;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Shelden Shi
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine smd = readConfig();
        boolean hasSet = false;
        while (_input.hasNextLine()) {
            String settingMsg = _input.nextLine();
            String[] settingList = settingMsg.split("\\s+");
            if (settingList[0].equals("*")) {
                setUp(smd, settingMsg);
                hasSet = true;
            } else if (!hasSet && !settingMsg.isEmpty()) {
                throw error("has not set");
            } else {
                printMessageLine(smd.convert(settingMsg));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            if (_config.hasNextLine()) {
                String temp = _config.nextLine();
                temp = temp.replaceAll("\\s+", "");
                boolean numeric = temp.matches("\\d+");
                if (numeric) {
                    throw error("configuration file truncated");
                }
                alphabetString = temp;
            }
            if (_config.hasNextLine()) {
                String temp = _config.nextLine();
                temp = temp.replaceAll("\\s+", "");
                boolean numeric = temp.matches("\\d+");
                if (!numeric) {
                    throw error("configuration file truncated");
                }
                String[] numRotorPawlsList = temp.split("");
                _numRotors = Integer.parseInt(numRotorPawlsList[0]);
                _rawls = Integer.parseInt(numRotorPawlsList[1]);
            }
            while (_config.hasNextLine()) {
                String nextLine = _config.nextLine();
                String[] nextLineList = nextLine.
                        replaceAll("\\s+", "").split("");
                nextLineList = Arrays.stream(nextLineList)
                        .filter(x -> !x.isEmpty())
                        .toArray(String[]::new);
                if (nextLineList.length > 0 && nextLineList[0].equals("(")) {
                    nextLineList = nextLine.replaceAll
                            ("\\s+", "").split("[\\(||\\)]");
                    nextLineList = Arrays.stream(nextLineList)
                            .filter(x -> !x.isEmpty())
                            .toArray(String[]::new);
                    for (String x : nextLineList) {
                        _allRotor.get(_allRotor.size() - 1).
                                permutation().addCycle(x);
                    }

                } else if (nextLineList.length > 0) {
                    _allRotor.add(readRotor(nextLine));
                }

            }
            return new Machine(new Alphabet(alphabetString),
                    _numRotors, _rawls, _allRotor);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }
    /** Return a rotor.
     * @param name name
     * @param type type
     * @param  notch notch(es)
     * @param  cycle cycle */
    private Rotor roterCreater(String name, String type,
                               String notch, String cycle) {
        if (type.equals("M")) {
            _allRotorNames.add(name);
            return new MovingRotor(name,
                    new Permutation(cycle,
                            new Alphabet(alphabetString)), notch);
        } else if (type.equals("N")) {
            _allRotorNames.add(name);
            return new FixedRotor(name, new Permutation(cycle,
                    new Alphabet(alphabetString)));
        } else if (type.equals("R")) {
            _allRotorNames.add(name);
            return new Reflector(name, new Permutation(cycle,
                    new Alphabet(alphabetString)));
        }
        throw error("type does not match");
    }

    /** Return a rotor, reading its description from _config.
     * @param nextLine next line*/
    private Rotor readRotor(String nextLine) {
        try {
            String[] nextLineList = nextLine.split("\\s+");
            nextLineList = Arrays.stream(nextLineList)
                    .filter(x -> !x.isEmpty())
                    .toArray(String[]::new);
            String name = nextLineList[0];
            String typeNotch = nextLineList[1];
            String type = typeNotch.substring(0, 1);
            String notch;
            if (typeNotch.length() > 1) {
                notch = typeNotch.substring(1);
            } else {
                notch = "";
            }
            String cycle = "";
            for (int i = 2; i < nextLineList.length; i++) {
                cycle += nextLineList[i];
            }
            return roterCreater(name, type, notch, cycle);
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] settingList = settings.split("\\s+");
        String[] order = new String[M.numRotors()];
        String posSetting = "";
        String plugboard = "";
        String ring = "";
        for (int i = 1; i < settingList.length; i++) {
            if (i < (M.numRotors() + 1)) {
                if (!_allRotorNames.contains(settingList[i])) {
                    throw error("Name not in all rotors");
                } else {
                    order[i - 1] = settingList[i];
                }
            } else if (i == (M.numRotors() + 1)) {
                posSetting = settingList[i];
            } else {
                if (!settingList[i].split("")[0].equals("(")) {
                    ring = settingList[i];
                } else {
                    plugboard += settingList[i];
                }

            }
        }
        if (checkDuplicateUsingAdd(order)) {
            throw error("Duplicate rotor name");
        }
        M.insertRotors(order);
        if (!ring.equals("")) {
            M.setRing(ring);
        }
        M.setRotors(posSetting);
        M.setPlugboard(new Permutation(plugboard,
                new Alphabet(alphabetString)));
    }
    /** Find duplicate of a set.
     * @return if it has duplicates
     * @param input an array*/
    private boolean checkDuplicateUsingAdd(String[] input) {
        Set<String> tempSet = new HashSet<String>();
        for (String str : input) {
            if (!tempSet.add(str)) {
                return true;
            }
        }
        return false;
    }


    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        _output.println(msg);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
    /** Number of rotors. */
    private int _numRotors;
    /** Number of rawls. */
    private int _rawls;
    /** All rotors. */
    private ArrayList<Rotor> _allRotor = new ArrayList<Rotor>();
    /** All rotor names. */
    private ArrayList<String> _allRotorNames = new ArrayList<String>();
    /** StringAlpha. */
    private String alphabetString;
}
