package DataStructure;

import java.util.ArrayList;

public class ListLib {
    public ArrayList<List> listlib = new ArrayList<>();

    // Add new list to the lib
    public void add(String name, String[] content) {
        for (List l : listlib) {
            if (l.name.equals(name)) {
                // The list is alreadly in the lib
                l.content = content;
                return;
            }
        }

        // The list is not in the lib
        List l = new List(name, content);
        listlib.add(l);
    }

    // Remove list from the lib
    public boolean remove(String name) {
        boolean ret = false;

        for (List l : listlib) {
            if (l.name.equals(name)) {
                listlib.remove(l);
                ret = true;
                break;
            }
        }

        return ret;
    }

    // Check whether a list is in the lib or not
    public boolean islist(String name) {
        boolean ret = false;

        for (List l : listlib) {
            if (l.name.equals(name)) {
                ret = true;
                break;
            }
        }

        return ret;
    }
}
