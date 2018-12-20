package DataStructure;

import java.util.ArrayList;

// Store the list of related word and value
public class WordLib {
    public ArrayList<Word> wordlist = new ArrayList<>();

    // Add new word to the list
    public void add(String name, String value) {
        for (Word w : wordlist) {
            if (w.name.equals(name)) {
                // The word is already in the list
                w.value = value;
                return;
            }
        }
        
        // The word is not in the list
        Word w = new Word(name, value);
        wordlist.add(w);
    }

    // Remove word from the list
    public boolean remove(String name) {
        boolean ret = false;

        for (Word w : wordlist) {
            if (w.name.equals(name)) {
                wordlist.remove(w);
                ret = true;
                break;
            }
        }

        return ret;
    }

    // Check whether a name is in the list or not
    public boolean isname(String name) {
        boolean ret = false;

        for (Word w : wordlist) {
            if (w.name.equals(name)) {
                ret = true;
                break;
            }
        }

        return ret;
    }
}
