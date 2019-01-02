import DataStructure.*;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.ArrayList;

// Class of functions
class FuncLib {
    // Print out false information
    void PrintFalseInfo(String Info, Scanner in) {
        System.out.println("Error: " + Info);
        in.nextLine();
    }

    // Print out false information without flush
    void PrintFalseInfowithoutFlush(String Info) {
        System.out.println("Error: " + Info);
    }

    // Check whether list is a function
    boolean isFunc(String str, ListLib ll) {
        for (List l : ll.listlib) {
            if (l.name.equals(str)) {
                // Construct a string
                String[] content = l.content;
                StringBuilder cont = new StringBuilder();
                for (int i = 0; i < content.length; i++) {
                    cont.append(content[i]);
                    if (i < content.length-1)
                        cont.append(" ");
                }

                String s = cont.toString();
                s = s.trim();
                cont = new StringBuilder(s);

                // 1 [1] [2] 1
                if (cont.charAt(0) != '[' || cont.charAt(cont.length()-1) != ']')
                    return false;

                int cnt = 0;
                int num = 0;

                for (int i = 0; i < cont.length(); i++) {
                    if (cont.charAt(i) == '[') {
                        num++;
                        if (num == 1)
                            cnt++;
                    }
                    else if (cont.charAt(i) == ']')
                        num--;

                    // [1 2] 1 [1 2]
                    if (num == 0 && cnt == 1 && cont.charAt(i) != ']' && cont.charAt(i) != ' ')
                        return false;
                }

                return (cnt == 2) && (num == 0);
            }
        }

        return false;
    }

    // Check whether str is an operator
    boolean isOp(String str, HashSet<String> valid, ListLib ll) {
        return str.charAt(0) == ':' || valid.contains(str) || (isFunc(str, ll));
    }

    // Check whether the starting character is a letter
    boolean isLetter(String str) {
        char c = str.charAt(0);
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    // Check whether the string is a bool
    boolean isBool(String str) {
        // Dispose of the starting "
        if (str.charAt(0) == '"')
            str = str.substring(1);

        return str.equals("true") || str.equals("false");
    }

    // Check whether a string is a number
    boolean isNum(String str) {
        if ((str.charAt(0) > '9' || str.charAt(0) < '0')
                && str.charAt(0) != '-' && str.charAt(0) != '+')
            return false;

        for (int i = 1; i < str.length(); i++)
            if ((str.charAt(i) > '9' || str.charAt(i) < '0') && str.charAt(i) != '.')
                return false;
        return true;
    }

    // Check whether a string is a integer
    boolean isInt(String str) {
        if (!isNum(str))
            return false;

        for (int i = 0; i < str.length(); i++)
            if (str.charAt(i) == '.')
                return false;
        return true;
    }

    void make(WordLib wl, String name, String value) {
        wl.add(name, value);
    }

    void makelist(ListLib ll, String name, String[] content) {
        ll.add(name, content);
    }

    boolean erase(WordLib wl, ListLib ll, String name) {
        return (wl.remove(name) || ll.remove(name));
    }

    boolean isname(WordLib wl, String name) {
        return wl.isname(name);
    }

    boolean islist(ListLib ll, String name) {
        return ll.islist(name);
    }

    String add(String s1, String s2) {
        if (!(isNum(s1) && isNum(s2))) {
            System.err.println("Operand not number!");
        }

        double a = Double.valueOf(s1);
        double b = Double.valueOf(s2);

        double ret = a + b;
        return String.valueOf(ret);
    }

    String mul(String s1, String s2) {
        if (!(isNum(s1) && isNum(s2))) {
            System.err.println("Operand not number!");
        }

        double a = Double.valueOf(s1);
        double b = Double.valueOf(s2);

        double ret = a * b;
        return String.valueOf(ret);
    }

    String mod(String s1, String s2, Scanner in) {
        if (!(isInt(s1) && isInt(s2))) {
            System.err.print("Operand not integer!");
            in.nextLine();
            return "";
        }

        int a = Integer.valueOf(s1);
        int b = Integer.valueOf(s2);

        int ret = a % b;
        return String.valueOf(ret);
    }

    String div(String s1, String s2) {
        if (!(isNum(s1) && isNum(s2))) {
            System.err.println("Operand not number!");
        }

        double a = Double.valueOf(s1);
        double b = Double.valueOf(s2);

        if (b == 0) {
            System.err.println("Divide by zero!");
            return null;
        }

        double ret = a / b;
        return String.valueOf(ret);
    }

    void save(Interpreter ip, String path) {
        try {
            File file = new File(path);

            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            WordLib wl = ip.getWordLib();
            ListLib ll = ip.getListLib();
            String string;

            for (Word w : wl.wordlist) {
                string = w.name + " " + w.value;
                bufferedWriter.write(string);
                bufferedWriter.write("\r\n");
            }
            for (List l : ll.listlib) {
                StringBuilder temp = new StringBuilder("[ ");
                for (String s : l.content) {
                    temp.append(s);
                    temp.append(" ");
                }
                temp.append("]");

                string = l.name + " " + temp;
                bufferedWriter.write(string);
                bufferedWriter.write("\r\n");
            }

            bufferedWriter.flush();
            bufferedWriter.close();
            writer.close();
            System.out.println("Save to \"" + path + "\" successfully.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    void load(Interpreter ip, String path) {
        try (FileReader reader = new FileReader(path);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String string;
            StringBuilder temp = new StringBuilder();

            while ((string = bufferedReader.readLine()) != null) {
                temp.append("make \"");
                temp.append(string);
                temp.append(" ");
            }
            System.out.println(temp);

            Scanner Loadin = new Scanner(temp.toString());
            while (Loadin.hasNext()) {
                String str = Loadin.next();
                ip.Execute(Loadin, str);
            }

            bufferedReader.close();
            reader.close();
            System.out.println("Load from \"" + path + "\" successfully.");
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    void erall(Interpreter ip) {
        WordLib wl = ip.getWordLib();
        ListLib ll = ip.getListLib();

        ArrayList<String> names = new ArrayList<>();

        for (Word w : wl.wordlist)
            names.add(w.name);
        for (List l : ll.listlib)
            names.add(l.name);

        for (String name : names)
            this.erase(wl, ll, name);

        System.out.println("Erase all succeed.");
    }

    void poall(Interpreter ip) {
        WordLib wl = ip.getWordLib();
        ListLib ll = ip.getListLib();

        if (wl.wordlist.isEmpty()) {
            // No word
            System.out.println("No defined word.");
        }
        else {
            System.out.println("Here are all the words:");
            System.out.printf("%-8s\t%s\n", "name", "content");
            for (Word w : wl.wordlist)
                System.out.printf("%-8s\t%s\n", w.name, w.value);
        }

        System.out.println();

        if (ll.listlib.isEmpty()) {
            System.out.println("No defined list.");
        }
        else {
            System.out.println("Here are all the lists:");
            System.out.printf("%-8s\t%s\n", "name", "content");
            for (List l : ll.listlib) {
                StringBuilder out = new StringBuilder("[ ");
                for (String string : l.content) {
                    out.append(string);
                    out.append(" ");
                }
                out.append(" ]");

                System.out.printf("%-8s\t%s\n", l.name, out.toString());
            }
        }
    }
}
