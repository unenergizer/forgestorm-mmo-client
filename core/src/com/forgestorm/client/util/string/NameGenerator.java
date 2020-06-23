package com.forgestorm.client.util.string;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * This class is released under GNU general public license
 * <p>
 * Description: This class generates random names from syllables, and provides programmer a
 * simple way to set a group of rules for generator to avoid unpronounceable and bizarre names.
 * <p>
 * SYLLABLE FILE REQUIREMENTS/FORMAT:
 * 1) all syllables are separated by line break.
 * 2) Syllable should not contain or start with whitespace, as this character is ignored and only first part of the syllable is read.
 * 3) + and - characters are used to set rules, and using them in other way, may result in unpredictable results.
 * 4) Empty lines are ignored.
 * <p>
 * SYLLABLE CLASSIFICATION:
 * Name is usually composed from 3 different class of syllables, which include prefix, middle part and suffix.
 * To declare syllable as a prefix in the file, insert "-" as a first character of the line.
 * To declare syllable as a suffix in the file, insert "+" as a first character of the line.
 * everything else is read as a middle part.
 * <p>
 * NUMBER OF SYLLABLES:
 * Names may have any positive number of syllables. In case of 2 syllables, name will be composed from prefix and suffix.
 * In case of 1 syllable, name will be chosen from amongst the prefixes.
 * In case of 3 and more syllables, name will begin with prefix, is filled with middle parts and ended with suffix.
 * <p>
 * ASSIGNING RULES:
 * I included a way to set 4 kind of rules for every syllable. To add rules to the syllables, write them right after the
 * syllable and SEPARATE WITH WHITESPACE. (example: "aad +v -c"). The order of rules is not important.
 * <p>
 * RULES:
 * 1) +v means that next syllable must definitely start with a vocal.
 * 2) +c means that next syllable must definitely start with a consonant.
 * 3) -v means that this syllable can only be added to another syllable, that ends with a vocal.
 * 4) -c means that this syllable can only be added to another syllable, that ends with a consonant.
 * So, our example: "aad +v -c" means that "aad" can only be after consonant and next syllable must start with vocal.
 * Beware of creating logical mistakes, like providing only syllables ending with consonants, but expecting only vocals, which will be detected
 * and RuntimeException will be thrown.
 * <p>
 * TO START:
 * Create a new NameGenerator object, provide the syllable file, and create names using compose() method.
 *
 * @author Joonas Vali, August 2009.
 */
@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class NameGenerator {
    final private static char[] vocals = {'a', 'e', 'i', 'o', 'u', 'y'};
    final private static char[] consonants = {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y'};
    private ArrayList<String> pre = new ArrayList<String>();
    private ArrayList<String> mid = new ArrayList<String>();
    private ArrayList<String> sur = new ArrayList<String>();
    private FileHandle fileHandle;

    /**
     * Create new random name generator object.
     */
    public NameGenerator() {
    }

    /**
     * Change the file. refresh() is automatically called during the process.
     *
     * @param nameGenTypes insert the file name, where syllables are located.
     */
    public void changeFile(NameGenTypes nameGenTypes) throws IOException {
        if (nameGenTypes == null) throw new IOException("File name cannot be null");
        fileHandle = Gdx.files.internal("data/language/namegen/" + nameGenTypes.toString() + ".txt");
        refresh();
    }

    /**
     * Refresh names from file. No need to call that method, if you are not changing the file during the operation of program, as this method
     * is called every time file name is changed or new NameGenerator object created.
     */
    private void refresh() throws IOException {

        BufferedReader bufRead;
        String line;

        bufRead = new BufferedReader(new InputStreamReader(fileHandle.read()));
        line = "";

        while (line != null) {
            line = bufRead.readLine();
            if (line != null && !line.equals("")) {
                if (line.charAt(0) == '-') {
                    pre.add(line.substring(1).toLowerCase());
                } else if (line.charAt(0) == '+') {
                    sur.add(line.substring(1).toLowerCase());
                } else {
                    mid.add(line.toLowerCase());
                }
            }
        }
        bufRead.close();
    }

    private String upper(String s) {
        return s.substring(0, 1).toUpperCase().concat(s.substring(1));
    }

    private boolean containsConsFirst(ArrayList<String> array) {
        for (String s : array) {
            if (consonantFirst(s)) return true;
        }
        return false;
    }

    private boolean containsVocalsFirst(ArrayList<String> array) {
        for (String s : array) {
            if (vocalFirst(s)) return true;
        }
        return false;
    }

    private boolean allowConstants(ArrayList<String> array) {
        for (String s : array) {
            if (hatesPreviousVocals(s) || !hatesPreviousConsonants(s)) return true;
        }
        return false;
    }

    private boolean allowVocals(ArrayList<String> array) {
        for (String s : array) {
            if (hatesPreviousConsonants(s) || !hatesPreviousVocals(s)) return true;
        }
        return false;
    }

    private boolean expectsVocal(String s) {
        return s.substring(1).contains("+v");
    }

    private boolean expectsConsonant(String s) {
        return s.substring(1).contains("+c");
    }

    private boolean hatesPreviousVocals(String s) {
        return s.substring(1).contains("-c");
    }

    private boolean hatesPreviousConsonants(String s) {
        return s.substring(1).contains("-v");
    }

    private String pureSyl(String s) {
        s = s.trim();
        if (s.charAt(0) == '+' || s.charAt(0) == '-') s = s.substring(1);
        return s.split(" ")[0];
    }

    private boolean vocalFirst(String s) {
        return (String.copyValueOf(vocals).contains(String.valueOf(s.charAt(0)).toLowerCase()));
    }

    private boolean consonantFirst(String s) {
        return (String.copyValueOf(consonants).contains(String.valueOf(s.charAt(0)).toLowerCase()));
    }

    private boolean vocalLast(String s) {
        return (String.copyValueOf(vocals).contains(String.valueOf(s.charAt(s.length() - 1)).toLowerCase()));
    }

    private boolean consonantLast(String s) {
        return (String.copyValueOf(consonants).contains(String.valueOf(s.charAt(s.length() - 1)).toLowerCase()));
    }


    /**
     * Compose a new name.
     *
     * @param syllables The number of syllables used in name.
     * @return Returns composed name as a String
     * @throws RuntimeException when logical mistakes are detected inside chosen file, and program is unable to complete the name.
     */
    public String compose(int syllables) {
        if (syllables > 2 && mid.size() == 0)
            throw new RuntimeException("You are trying to create a name with more than 3 parts, which requires middle parts, " +
                    "which you have none in the file " + fileHandle.name() + ". You should add some. Every word, which doesn't have + or - for a prefix is counted as a middle part.");
        if (pre.size() == 0)
            throw new RuntimeException("You have no prefixes to start creating a name. add some and use \"-\" prefix, to identify it as a prefix for a name. (example: -asd)");
        if (sur.size() == 0)
            throw new RuntimeException("You have no suffixes to end a name. add some and use \"+\" prefix, to identify it as a suffix for a name. (example: +asd)");
        if (syllables < 1)
            throw new RuntimeException("compose(int syls) can't have less than 1 syllable");
        int expecting = 0; // 1 for vocal, 2 for consonant
        int last; // 1 for vocal, 2 for consonant
        String name;
        int a = (int) (Math.random() * pre.size());

        if (vocalLast(pureSyl(pre.get(a)))) last = 1;
        else last = 2;

        if (syllables > 2) {
            if (expectsVocal(pre.get(a))) {
                expecting = 1;
                if (!containsVocalsFirst(mid))
                    throw new RuntimeException("Expecting \"middle\" part starting with vocal, " +
                            "but there is none. You should add one, or remove requirement for one.. ");
            }
            if (expectsConsonant(pre.get(a))) {
                expecting = 2;
                if (!containsConsFirst(mid))
                    throw new RuntimeException("Expecting \"middle\" part starting with consonant, " +
                            "but there is none. You should add one, or remove requirement for one.. ");
            }
        } else {
            if (expectsVocal(pre.get(a))) {
                expecting = 1;
                if (!containsVocalsFirst(sur))
                    throw new RuntimeException("Expecting \"suffix\" part starting with vocal, " +
                            "but there is none. You should add one, or remove requirement for one.. ");
            }
            if (expectsConsonant(pre.get(a))) {
                expecting = 2;
                if (!containsConsFirst(sur))
                    throw new RuntimeException("Expecting \"suffix\" part starting with consonant, " +
                            "but there is none. You should add one, or remove requirement for one.. ");
            }
        }
        if (vocalLast(pureSyl(pre.get(a))) && !allowVocals(mid))
            throw new RuntimeException("Expecting \"middle\" part that allows last character of prefix to be a vocal, " +
                    "but there is none. You should add one, or remove requirements that cannot be fulfilled.. the prefix used, was : \"" + pre.get(a) + "\", which" +
                    "means there should be a part available, that has \"-v\" requirement or no requirements for previous syllables at all.");

        if (consonantLast(pureSyl(pre.get(a))) && !allowConstants(mid))
            throw new RuntimeException("Expecting \"middle\" part that allows last character of prefix to be a consonant, " +
                    "but there is none. You should add one, or remove requirements that cannot be fulfilled.. the prefix used, was : \"" + pre.get(a) + "\", which" +
                    "means there should be a part available, that has \"-c\" requirement or no requirements for previous syllables at all.");

        int[] b = new int[syllables];
        for (int i = 0; i < b.length - 2; i++) {

            do {
                b[i] = (int) (Math.random() * mid.size());
                //System.out.println("exp " +expecting+" vocalF:"+vocalFirst(mid.get(b[i]))+" syl: "+mid.get(b[i]));
            }
            while (expecting == 1 && !vocalFirst(pureSyl(mid.get(b[i]))) || expecting == 2 && !consonantFirst(pureSyl(mid.get(b[i])))
                    || last == 1 && hatesPreviousVocals(mid.get(b[i])) || last == 2 && hatesPreviousConsonants(mid.get(b[i])));

            expecting = 0;
            if (expectsVocal(mid.get(b[i]))) {
                expecting = 1;
                if (i < b.length - 3 && !containsVocalsFirst(mid))
                    throw new RuntimeException("Expecting \"middle\" part starting with vocal, " +
                            "but there is none. You should add one, or remove requirement for one.. ");
                if (i == b.length - 3 && !containsVocalsFirst(sur))
                    throw new RuntimeException("Expecting \"suffix\" part starting with vocal, " +
                            "but there is none. You should add one, or remove requirement for one.. ");
            }
            if (expectsConsonant(mid.get(b[i]))) {
                expecting = 2;
                if (i < b.length - 3 && !containsConsFirst(mid))
                    throw new RuntimeException("Expecting \"middle\" part starting with consonant, " +
                            "but there is none. You should add one, or remove requirement for one.. ");
                if (i == b.length - 3 && !containsConsFirst(sur))
                    throw new RuntimeException("Expecting \"suffix\" part starting with consonant, " +
                            "but there is none. You should add one, or remove requirement for one.. ");
            }
            if (vocalLast(pureSyl(mid.get(b[i]))) && !allowVocals(mid) && syllables > 3)
                throw new RuntimeException("Expecting \"middle\" part that allows last character of last syllable to be a vocal, " +
                        "but there is none. You should add one, or remove requirements that cannot be fulfilled.. the part used, was : \"" + mid.get(b[i]) + "\", which " +
                        "means there should be a part available, that has \"-v\" requirement or no requirements for previous syllables at all.");

            if (consonantLast(pureSyl(mid.get(b[i]))) && !allowConstants(mid) && syllables > 3)
                throw new RuntimeException("Expecting \"middle\" part that allows last character of last syllable to be a consonant, " +
                        "but there is none. You should add one, or remove requirements that cannot be fulfilled.. the part used, was : \"" + mid.get(b[i]) + "\", which " +
                        "means there should be a part available, that has \"-c\" requirement or no requirements for previous syllables at all.");
            if (i == b.length - 3) {
                if (vocalLast(pureSyl(mid.get(b[i]))) && !allowVocals(sur))
                    throw new RuntimeException("Expecting \"suffix\" part that allows last character of last syllable to be a vocal, " +
                            "but there is none. You should add one, or remove requirements that cannot be fulfilled.. the part used, was : \"" + mid.get(b[i]) + "\", which " +
                            "means there should be a suffix available, that has \"-v\" requirement or no requirements for previous syllables at all.");

                if (consonantLast(pureSyl(mid.get(b[i]))) && !allowConstants(sur))
                    throw new RuntimeException("Expecting \"suffix\" part that allows last character of last syllable to be a consonant, " +
                            "but there is none. You should add one, or remove requirements that cannot be fulfilled.. the part used, was : \"" + mid.get(b[i]) + "\", which " +
                            "means there should be a suffix available, that has \"-c\" requirement or no requirements for previous syllables at all.");
            }
            if (vocalLast(pureSyl(mid.get(b[i])))) last = 1;
            else last = 2;
        }

        int c;
        do {
            c = (int) (Math.random() * sur.size());
        }
        while (expecting == 1 && !vocalFirst(pureSyl(sur.get(c))) || expecting == 2 && !consonantFirst(pureSyl(sur.get(c)))
                || last == 1 && hatesPreviousVocals(sur.get(c)) || last == 2 && hatesPreviousConsonants(sur.get(c)));

        name = upper(pureSyl(pre.get(a).toLowerCase()));
        for (int i = 0; i < b.length - 2; i++) {
            name = name.concat(pureSyl(mid.get(b[i]).toLowerCase()));
        }
        if (syllables > 1)
            name = name.concat(pureSyl(sur.get(c).toLowerCase()));
        return name;
    }

    public enum NameGenTypes {
        ELVEN,
        FANTASY,
        GOBLIN,
        ROMAN
    }
}