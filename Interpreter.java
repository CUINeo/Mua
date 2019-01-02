import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import DataStructure.*;

class Interpreter {
    private WordLib wl = new WordLib();
    private ListLib ll = new ListLib();
    private FuncLib fl = new FuncLib();
    private OpValid ov = new OpValid();

    // Static final variables
    private static final int _number = 1;
    private static final int _word = 2;
//    private static final int _bool = 3;
//    private static final int _list = 4;

    WordLib getWordLib() {
        return wl;
    }

    ListLib getListLib() {
        return ll;
    }

    void Run() throws InterruptedException {
        // Print the prompt
        System.out.println("Welcome to MUA!");
        Scanner in = new Scanner(System.in);

        while (true) {
            // Print the prompt
            System.out.print(">>> ");

            // Read in the operator
            String operator = in.next();

            // Execute
            Object retobj = Execute(in, operator);

            // Exit the interpreter
            if (retobj.getClass().getName().equals("DataStructure.Exit"))
                break;

            // Output has a value
            if (!(retobj.getClass().getName().equals("DataStructure.Null")))
                System.out.println(retobj.toString());
        }
    }

    Object Execute(Scanner in, String operator) throws InterruptedException {
        // Null return type
        Null n = new Null();

        // Branch according to the operator
        if (!(ov.operators.contains(operator) || operator.charAt(0) == ':' || ll.islist(operator))) {
            // Invalid input
            fl.PrintFalseInfo("Invalid input!", in);
            return n;
        }

        if (ll.islist(operator)) {
            // function
            if (!fl.isFunc(operator, ll)) {
                // Illegal function
                fl.PrintFalseInfo("Illegal function!", in);
                return n;
            }

            List func = new List();

            for (List l : ll.listlib) {
                if (l.name.equals(operator)) {
                    func = l;
                    break;
                }
            }

            // Construct a string
            String[] content = func.content;
            StringBuilder cont = new StringBuilder();
            for (int i = 0; i < content.length; i++) {
                cont.append(content[i]);
                if (i < content.length-1)
                    cont.append(" ");
            }

            int cnt = 0;
            int num = 0;
            StringBuilder arg_temp = new StringBuilder();
            StringBuilder instr_temp = new StringBuilder();

            for (int i = 0; i < cont.length(); i++) {
                if (cont.charAt(i) == '[') {
                    num++;
                    if (num == 1)
                        cnt++;
                }
                else if (cont.charAt(i) == ']')
                    num--;

                // Get the arg list and the instr list
                if (cnt == 1)
                    arg_temp.append(cont.charAt(i));
                if (cnt == 2)
                    instr_temp.append(cont.charAt(i));
            }

            String arg = arg_temp.toString();
            String instr = instr_temp.toString();

            // Dispose of spaces in both ends
            arg = arg.trim();
            instr = instr.trim();

            // Dispose of [ and ]
            arg = arg.substring(1, arg.length()-1);
            instr = instr.substring(1, instr.length()-1);

            // Dispose of spaces in both ends
            arg = arg.trim();
            instr = instr.trim();

//            System.out.println(arg);
//            System.out.println(instr);

            // Get the number of args
            String[] args = arg.split(" ");
            int arg_num = args.length;

            // No args
            if (args.length == 1 && args[0].equals(""))
                arg_num = 0;

//            System.out.println(arg_num);

            // Read in the arglist(from the user)
            ArrayList<String> arglist = new ArrayList<>();
            for (int i = 0; i < arg_num; i++) {
                String temp = in.next();
                if (fl.isOp(temp, ov.operators, ll))
                    temp = Execute(in, temp).toString();
                arglist.add(temp);
            }

            StringBuilder funcinstr = new StringBuilder();
            for (int i = 0; i < arg_num; i++)
                // Link args
                funcinstr.append("make \"").append(args[i]).append(" ").append(arglist.get(i)).append(" ");

            // Joint instructions
            funcinstr.append(instr);
//            System.out.println(funcinstr);

            // Function execution
            Scanner Strin = new Scanner(funcinstr.toString());
            Interpreter funcip = new Interpreter();
            String ret = null;

            while (Strin.hasNext()) {
                String str = Strin.next();
                if (str.equals("stop") || str.equals("exit")) {
                    // function stop / exit
                    if (ret != null)
                        return ret;
                }
                else if (operator.equals("thing")) {
                    // function thing
                    String name = Strin.next();

                    // Check if the name is an instruction
                    if (funcip.fl.isOp(name, funcip.ov.operators, funcip.ll))
                        name = Execute(Strin, name).toString();

                    if (name.charAt(0) != '"') {
                        // The word doesn't begin with "
                        funcip.fl.PrintFalseInfowithoutFlush("Missing \" before the name!");
                    }
                    else {
                        // Dispose of the beginning "
                        name = name.substring(1);

                        if (fl.isname(wl, name) || funcip.fl.isname(funcip.wl, name)) {
                            // Word
                            for (Word w : wl.wordlist) {
                                if (w.name.equals(name)) {
                                    // Return the value of required word
                                    return w.value;
                                }
                            }
                            for (Word w : funcip.wl.wordlist) {
                                if (w.name.equals(name)) {
                                    // Return the value of required word
                                    return w.value;
                                }
                            }
                        }
                        else if (fl.islist(ll, name) || funcip.fl.islist(funcip.ll, name)) {
                            // List
                            for (List l : ll.listlib) {
                                if (l.name.equals(name)) {
                                    // Return the value of required list
                                    StringBuilder retstr = new StringBuilder("[");
                                    for (int i = 0; i < l.content.length; i++) {
                                        retstr.append(l.content[i]);
                                        if (i != l.content.length-1)
                                            retstr.append(" ");
                                    }
                                    retstr.append("]");

                                    return retstr;
                                }
                            }
                            for (List l : funcip.ll.listlib) {
                                if (l.name.equals(name)) {
                                    // Return the value of required list
                                    StringBuilder retstr = new StringBuilder("[");
                                    for (int i = 0; i < l.content.length; i++) {
                                        retstr.append(l.content[i]);
                                        if (i != l.content.length-1)
                                            retstr.append(" ");
                                    }
                                    retstr.append("]");

                                    return retstr;
                                }
                            }
                        }
                        else
                            // Word name doesn't exist
                            fl.PrintFalseInfowithoutFlush("There is no name \"" + name + "\"!");
                    }
                }
                else if (operator.charAt(0) == ':') {
                    // function :
                    // Dispose of the beginning :
                    String name = operator.substring(1);
                    if (name.length() == 0) {
                        // : a
                        String temp = Strin.next();
                        name += temp;
                    }

                    // Check is the name is an instruction
                    if (funcip.fl.isOp(name, funcip.ov.operators, funcip.ll))
                        name = Execute(in, name).toString().substring(1);

                    if (fl.isname(wl, name) || funcip.fl.isname(funcip.wl, name)) {
                        // Word
                        for (Word w : wl.wordlist) {
                            if (w.name.equals(name)) {
                                // Return the value of required word
                                return w.value;
                            }
                        }
                        for (Word w : funcip.wl.wordlist) {
                            if (w.name.equals(name)) {
                                // Return the value of required word
                                return w.value;
                            }
                        }
                    }
                    else if (fl.islist(ll, name) || funcip.fl.islist(funcip.ll, name)) {
                        // List
                        for (List l : ll.listlib) {
                            if (l.name.equals(name)) {
                                // Return the value of required list
                                StringBuilder retstr = new StringBuilder("[");
                                for (int i = 0; i < l.content.length; i++) {
                                    retstr.append(l.content[i]);
                                    if (i != l.content.length-1)
                                        retstr.append(" ");
                                }
                                retstr.append("]");

                                return retstr;
                            }
                        }
                        for (List l : funcip.ll.listlib) {
                            if (l.name.equals(name)) {
                                // Return the value of required list
                                StringBuilder retstr = new StringBuilder("[");
                                for (int i = 0; i < l.content.length; i++) {
                                    retstr.append(l.content[i]);
                                    if (i != l.content.length-1)
                                        retstr.append(" ");
                                }
                                retstr.append("]");

                                return retstr;
                            }
                        }
                    }
                    else
                        // Word name doesn't exist
                        fl.PrintFalseInfowithoutFlush("There is no name \"" + name + "\"!");
                }
                else if (str.equals("erase")) {
                    // function erase
                    String name = Strin.next();

                    // Check if the name is an instruction
                    if (funcip.fl.isOp(name, funcip.ov.operators, funcip.ll))
                        name = Execute(Strin, name).toString();

                    if (name.charAt(0) != '"') {
                        // The word doesn't begin with "
                        fl.PrintFalseInfowithoutFlush("Missing \" before the name!");
                        continue;
                    }
                    name = name.substring(1);

                    boolean flag;

                    if (funcip.fl.isOp(name, funcip.ov.operators, funcip.ll)) {
                        // erase operators
                        funcip.ov.operators.remove(name);
                        continue;
                    }

                    flag = funcip.fl.erase(funcip.wl, funcip.ll, name);
                    if (flag)
                        continue;

                    // Not in this namespace
                    if (fl.isOp(name, ov.operators, ll)) {
                        // erase outer namespace's operators
                        ov.operators.remove(name);
                        continue;
                    }

                    flag = fl.erase(wl, ll, name);

                    if (!flag)
                        // Word(List) name doesn't exist
                        fl.PrintFalseInfowithoutFlush("There is no name \"" + name + "\"!");
                }
                else if (str.equals("export")) {
                    // function export
                    String value = Strin.next();
                    Object obj;

                    if (funcip.fl.isOp(value, funcip.ov.operators, funcip.ll)) {
                        obj = funcip.Execute(Strin, value);

                        if (obj.getClass().getName().equals("DataStructure.Null")) {
                            // Null return type
                            fl.PrintFalseInfowithoutFlush("Null return type!");
                            continue;
                        }
                        else
                            value = obj.toString();
                    }

                    if (value.charAt(0) != '"') {
                        fl.PrintFalseInfowithoutFlush("A name must start with \"");
                        continue;
                    }

                    // Dispose of the starting "
                    value = value.substring(1);

                    WordLib funcwl = funcip.wl;
                    ListLib funcll = funcip.ll;

                    for (Word w : funcwl.wordlist) {
                        if (w.name.equals(value)) {
                            wl.add(w.name, w.value);

                            // make operators
                            if (fl.isOp(w.name, ov.operators, ll))
                                ov.operators.remove(w.name);
                        }
                    }

                    for (List l : funcll.listlib) {
                        if (l.name.equals(value)) {
                            ll.add(l.name, l.content);

                            // make operators
                            if (fl.isOp(l.name, ov.operators, ll))
                                ov.operators.remove(l.name);
                        }
                    }
                }
                else if (str.equals("output")) {
                    // function output
                    String value = Strin.next();
                    Object obj;

                    if (funcip.fl.isOp(value, funcip.ov.operators, funcip.ll)) {
                        obj = funcip.Execute(Strin, value);

                        if (obj.getClass().getName().equals("DataStructure.Null")) {
                            // Null return type
                            fl.PrintFalseInfowithoutFlush("Null return type!");
                            continue;
                        }
                        else
                            value = obj.toString();
                    }
                    else {
                        String temp = in.nextLine();
                        value += temp;
                    }

                    // Set return value
                    ret = value;
                }
                else if (str.equals("save")) {
                    // function save
                    String string = in.next();
                    Object obj;

                    if (fl.isOp(string, ov.operators, ll)) {
                        obj = Execute(Strin, string);

                        if (obj.getClass().getName().equals("DataStructure.Null")) {
                            // Null return type
                            fl.PrintFalseInfowithoutFlush("Null return type!");
                            continue;
                        } else
                            string = obj.toString();
                    }

                    // Dispose of the starting "
                    if (string.charAt(0) == '"')
                        string = string.substring(1);

                    String path;
                    if (string.endsWith(".txt"))
                        path = string;
                    else
                        path = string + ".txt";

                    funcip.fl.save(funcip, path);
                }
                else if (str.equals("load")) {
                    // function load
                    String string = in.next();
                    Object obj;

                    if (fl.isOp(string, ov.operators, ll)) {
                        obj = Execute(Strin, string);

                        if (obj.getClass().getName().equals("DataStructure.Null")) {
                            // Null return type
                            fl.PrintFalseInfowithoutFlush("Null return type!");
                            continue;
                        } else
                            string = obj.toString();
                    }

                    // Dispose of the starting "
                    if (string.charAt(0) == '"')
                        string = string.substring(1);

                    String path;
                    if (string.endsWith(".txt"))
                        path = string;
                    else
                        path = string + ".txt";

                    funcip.fl.load(funcip, path);
                }
                else if (str.equals("erall")) {
                    // function erase all
                    funcip.fl.erall(funcip);
                }
                else if (str.equals("poall")) {
                    // function post all
                    funcip.fl.poall(funcip);
                }
                else
                    // function execute
                    funcip.Execute(Strin, str);
            }

            // Default return
            return Objects.requireNonNullElse(ret, n);
        }

        else if (operator.equals("make")) {
            // make
            String name = in.next();
            String value = in.next();

            if (name.charAt(0) != '"') {
                // The word doesn't begin with "
                fl.PrintFalseInfo("Missing \" before the name!", in);
                return n;
            }
            else {
                // Dispose of the beginning "
                name = name.substring(1);
                if (!fl.isLetter(name)) {
                    // Word name starts with non-letter
                    fl.PrintFalseInfo("The first character of the word name must be a letter!", in);
                    return n;
                }

                if (fl.isOp(value, ov.operators, ll)) {
                    // value is an operator
                    value = Execute(in, value).toString();

                    if (value.charAt(0) == '[') {
                        // Make list
                        if (value.charAt(value.length()-1) != ']') {
                            // Illegal list
                            fl.PrintFalseInfowithoutFlush("Illegal list input!");
                            return n;
                        }
                        else {
                            // Dispose of [ and ]
                            value = value.substring(1, value.length()-1);

                            String[] content = value.split(" ");
                            fl.makelist(ll, name, content);

                            value = "[" + value + "]";
                        }
                    }
                    else if (fl.isNum(value) || fl.isBool(value) || value.charAt(0) == '"')
                        // make word
                        fl.make(wl, name, value);
                    else {
                        // Illegal value
                        fl.PrintFalseInfo("Illegal value!", in);
                        return n;
                    }
                }
                else {
                    // value is not an operator
                    if (value.charAt(0) == '[') {
                        // Make list

                        // Get the whole list
                        int cnt = 0;
                        StringBuilder temp = new StringBuilder();
                        String s = value;
                        int left, right;
                        do {
                            left = 0;
                            right = 0;

                            for (int i = 0; i < s.length(); i++) {
                                if (s.charAt(i) == '[')
                                    left++;
                                if (s.charAt(i) == ']')
                                    right++;
                            }

                            cnt = cnt + left - right;

                            temp.append(s);

                            if (cnt != 0) {
                                temp.append(" ");
                                s = in.next();
                            }
                        } while (cnt > 0);

                        value = temp.toString();

                        if (value.charAt(value.length() - 1) != ']') {
                            // Illegal list
                            fl.PrintFalseInfowithoutFlush("Illegal list input!");
                            return n;
                        } else {
                            // Dispose of [ and ]
                            value = value.substring(1, value.length() - 1).trim();

                            String[] content = value.split(" ");
                            fl.makelist(ll, name, content);

                            value = "[" + value + "]";
                        }
                    } else if (fl.isNum(value) || fl.isBool(value) || value.charAt(0) == '"')
                        // make word
                        fl.make(wl, name, value);
                    else {
                        // Illegal value
                        fl.PrintFalseInfo("Illegal value!", in);
                        return n;
                    }
                }

                // erase operators
                ov.operators.remove(name);

                return name + " = " + value;
            }
        }

        else if (operator.equals("repeat")) {
            // repeat
            String str = in.next();
            Object obj;

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else
                    str = obj.toString();
            }

            // Dispose of the starting "
            if (str.charAt(0) == '"')
                str = str.substring(1);

            if (!fl.isInt(str)) {
                fl.PrintFalseInfo("Require an integer!", in);
                return n;
            }

            // Get the number of execution times
            int num = Integer.valueOf(str);

            str = in.next();

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else {
                    str = obj.toString();
                    if (str.charAt(0) != '[' || str.charAt(str.length()-1) != ']') {
                        // Not a list
                        fl.PrintFalseInfo("Require a list!", in);
                        return n;
                    }
                }
            }
            else {
                String temp = in.nextLine();
                str += temp;
                if (str.charAt(0) != '[' || str.charAt(str.length()-1) != ']') {
                    // Not a list
                    fl.PrintFalseInfo("Require a list!", in);
                    return n;
                }
            }

            // Dispose of [ and ]
            str = str.substring(1, str.length()-1);

            // Execute
            for (int i = 0; i < num; i++) {
                Scanner Strin = new Scanner(str + "\n");
                while (Strin.hasNext()) {
                    String temp = Strin.next();
                    Object retobj = Execute(Strin, temp);

                    // Exit the interpreter
                    if (retobj.getClass().getName().equals("DataStructure.Exit"))
                        return new Exit();

                    // Output has a value
                    if (!(retobj.getClass().getName().equals("DataStructure.Null")))
                        System.out.println(retobj.toString());
                }
            }
        }

        else if (operator.equals("isname")) {
            // isname
            String str = in.next();
            Object obj;

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else
                    str = obj.toString();
            }

            if (str.charAt(0) != '"') {
                // The word doesn't begin with "
                fl.PrintFalseInfo("A name should start with \"", in);
                return n;
            }

            if (fl.isname(wl, str.substring(1)) || fl.islist(ll, str.substring(1)))
                return "true";
            else
                return "false";
        }

        else if (operator.equals("isnumber") || operator.equals("isbool")) {
            // isnumber / isbool
            String str = in.next();
            Object obj;

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else
                    str = obj.toString();
            }

            // Dispose of the starting "
            if (str.charAt(0) == '"')
                str = str.substring(1);

            if (operator.equals("isnumber")) {
                // isnumber
                if (fl.isNum(str))
                    return "true";
                else
                    return "false";
            }
            else {
                // isbool
                if (fl.isBool(str))
                    return "true";
                else
                    return false;
            }
        }

        else if (operator.equals("isword")) {
            // isword
            String str = in.next();
            Object obj;

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else
                    str = obj.toString();
            }

            if (str.charAt(0) != '"') {
                // Start with no "
                if (fl.isNum(str) || fl.isBool(str))
                    return "true";
                else
                    return false;
            }
            else {
                // Start with "
                // Dispose of the starting "
                str = str.substring(1);

                if (fl.isNum(str) || fl.isBool(str) || fl.isLetter(str))
                    return "true";
                else
                    return "false";
            }
        }

        else if (operator.equals("islist")) {
            // islist
            String str = in.next();
            Object obj;

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else
                    str = obj.toString();
            }
            else {
                String temp = in.nextLine();
                str += temp;
            }

            if (str.charAt(0) == '[' && str.charAt(str.length()-1) == ']')
                return "true";
            else
                return "false";
        }

        else if (operator.equals("isempty")) {
            // isempty
            String str = in.next();
            Object obj;

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else
                    str = obj.toString();
            }
            else {
                String temp = in.nextLine();
                str += temp;
            }

            // Dispose of the space
            str = str.replace(" ", "");

            if (str.equals("\"") || str.equals("[]"))
                return "true";
            else
                return "false";
        }

        else if (operator.equals("thing")) {
            // thing
            String name = in.next();

            // Check if the name is an instruction
            if (fl.isOp(name, ov.operators, ll))
                name = Execute(in, name).toString();

            if (name.charAt(0) != '"') {
                // The word doesn't begin with "
                fl.PrintFalseInfowithoutFlush("Missing \" before the name!");
                return n;
            }
            else {
                // Dispose of the beginning "
                name = name.substring(1);

                if (fl.isname(wl, name)) {
                    // Word
                    for (Word w : wl.wordlist) {
                        if (w.name.equals(name)) {
                            // Return the value of required word
                            return w.value;
                        }
                    }
                }
                else if (fl.islist(ll, name)) {
                    // List
                    for (List l : ll.listlib) {
                        if (l.name.equals(name)) {
                            // Return the value of required list
                            StringBuilder ret = new StringBuilder("[");
                            for (int i = 0; i < l.content.length; i++) {
                                ret.append(l.content[i]);
                                if (i != l.content.length-1)
                                    ret.append(" ");
                            }
                            ret.append("]");

                            return ret;
                        }
                    }
                }
                else {
                    // Word name doesn't exist
                    fl.PrintFalseInfowithoutFlush("There is no name \"" + name + "\"!");
                    return n;
                }
            }
        }

        else if (operator.charAt(0) == ':') {
            // :
            // Dispose of the beginning :
            String name = operator.substring(1);
            if (name.length() == 0) {
                // : a
                String temp = in.next();
                name += temp;
            }

            // Check if the name is an instruction
            if (fl.isOp(name, ov.operators, ll))
                name = Execute(in, name).toString().substring(1);

            if (fl.isname(wl, name)) {
                // Word
                for (Word w : wl.wordlist) {
                    if (w.name.equals(name)) {
                        // Return the value of required word
                        return w.value;
                    }
                }
            }
            else if (fl.islist(ll, name)) {
                // List
                for (List l : ll.listlib) {
                    if (l.name.equals(name)) {
                        // Return the value of required list
                        StringBuilder ret = new StringBuilder("[");
                        for (int i = 0; i < l.content.length; i++) {
                            ret.append(l.content[i]);
                            if (i != l.content.length-1)
                                ret.append(" ");
                        }
                        ret.append("]");

                        return ret;
                    }
                }
            }
            else {
                // Word name doesn't exist
                fl.PrintFalseInfowithoutFlush("There is no name \"" + name + "\"!");
                return n;
            }
        }

        else if (operator.equals("erase")) {
            // erase
            String name = in.next();

            // Check if the name is an instruction
            if (fl.isOp(name, ov.operators, ll))
                name = Execute(in, name).toString().substring(1);

            if (name.charAt(0) != '"') {
                // The word doesn't begin with "
                fl.PrintFalseInfo("Missing \" before the name!", in);
                return n;
            }
            name = name.substring(1);

            if (fl.isOp(name, ov.operators, ll)) {
                // erase operators
                ov.operators.remove(name);
                return "\"" + name + "\" has been erased";
            }

            boolean flag = fl.erase(wl, ll, name);

            if (!flag) {
                // Word(List) name doesn't exist
                fl.PrintFalseInfowithoutFlush("There is no name \"" + name + "\"!");
                return n;
            }

            return "\"" + name + "\" has been erased";
        }

        else if (operator.equals("print")) {
            // print
            String operand = in.next();

            if (fl.isOp(operand, ov.operators, ll)) {
                // Execute the instruction
                Object obj = Execute(in, operand);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    fl.PrintFalseInfo(operand, in);
                    return n;
                }
                System.out.println(obj.toString());
            }
            else {
                String temp = in.nextLine();
                operand += temp;
                System.out.println(operand);
            }
        }

        else if (operator.equals("read")) {
            // read
            return in.next();
        }

        else if (operator.equals("readlist")) {
            // read list
            in.nextLine();
            String input = in.nextLine();
            return "[" + input + "]";
        }

        else if (operator.equals("add") || operator.equals("mul") ||
                operator.equals("div") || operator.equals("mod")) {
            // arithmetical operation
            // Get the first operand
            String firstoperand = in.next();
            if (fl.isOp(firstoperand, ov.operators, ll)) {
                if (Execute(in, firstoperand).getClass().getName().equals("DataStructure.Null")) {
                    fl.PrintFalseInfo("Invalid input!", in);
                    return n;
                }
                firstoperand = Execute(in, firstoperand).toString();
            }

            // Get the second operand
            String secondoperand = in.next();
            if (fl.isOp(secondoperand, ov.operators, ll)) {
                if (Execute(in, secondoperand).getClass().getName().equals("DataStructure.Null")) {
                    fl.PrintFalseInfo("Invalid input!", in);
                    return n;
                }
                secondoperand = Execute(in, secondoperand).toString();
            }

            // Dispose of the possible "
            if (firstoperand.charAt(0) == '"')
                firstoperand = firstoperand.substring(1);
            if (secondoperand.charAt(0) == '"')
                secondoperand = secondoperand.substring(1);

            // Get the result
            if (operator.equals("add"))
                return fl.add(firstoperand, secondoperand);
            if (operator.equals("mul"))
                return fl.mul(firstoperand, secondoperand);
            if (operator.equals("div"))
                return fl.div(firstoperand, secondoperand);
            return fl.mod(firstoperand, secondoperand, in);
        }

        else if (operator.equals("eq") || operator.equals("gt") || operator.equals("lt")) {
            // eq / gt / lt
            int type1, type2;

            // Get the first operand
            String firstoperand = in.next();
            if (fl.isOp(firstoperand, ov.operators, ll)) {
                if (Execute(in, firstoperand).getClass().getName().equals("DataStructure.Null")) {
                    fl.PrintFalseInfo("Invalid input!", in);
                    return n;
                }
                firstoperand = Execute(in, firstoperand).toString();
            }

            // Get the second operand
            String secondoperand = in.next();
            if (fl.isOp(secondoperand, ov.operators, ll)) {
                if (Execute(in, secondoperand).getClass().getName().equals("DataStructure.Null")) {
                    fl.PrintFalseInfo("Invalid input!", in);
                    return n;
                }
                secondoperand = Execute(in, secondoperand).toString();
            }

            // Dispose of the possible "
            if (firstoperand.charAt(0) == '"')
                firstoperand = firstoperand.substring(1);
            if (secondoperand.charAt(0) == '"')
                secondoperand = secondoperand.substring(1);

            // Get the type of the operand
            if (fl.isNum(firstoperand)) {
                type1 = _number;
            }
            else
                type1 = _word;

            // Get the type of the second operand
            if (fl.isNum(secondoperand)) {
                type2 = _number;
            }
            else
                type2 = _word;

            if (type1 == _number && type2 == _number) {
                // Two numbers
                double d1 = Double.valueOf(firstoperand);
                double d2 = Double.valueOf(secondoperand);

                if (operator.equals("eq")) {
                    // eq
                    if (d1 == d2)
                        return "true";
                    else
                        return "false";
                }
                else if (operator.equals("gt")) {
                    // gt
                    if (d1 > d2)
                        return "true";
                    else
                        return "false";
                }
                else {
                    // lt
                    if (d1 < d2)
                        return "true";
                    else
                        return "false";
                }
            }
            else {
                // Two words
                if (operator.equals("eq")) {
                    // eq
                    if (firstoperand.equals(secondoperand))
                        return "ture";
                    else
                        return "false";
                }
                else if (operator.equals("gt")) {
                    // gt
                    if (firstoperand.compareTo(secondoperand) > 0)
                        return "ture";
                    else
                        return "false";
                }
                else {
                    // lt
                    if (firstoperand.compareTo(secondoperand) < 0)
                        return "ture";
                    else
                        return "false";
                }
            }
        }

        else if (operator.equals("and") || operator.equals("or")) {
            // and / or
            boolean b1, b2;

            // Get the first operand
            String firstoperand = in.next();
            if (fl.isOp(firstoperand, ov.operators, ll))
                firstoperand = Execute(in, firstoperand).toString();

            // Dispose of the possible "
            if (firstoperand.charAt(0) == '"')
                firstoperand = firstoperand.substring(1);

            if (!firstoperand.equals("true") && !firstoperand.equals("false")) {
                // Type dismatch
                fl.PrintFalseInfo("Require a Bool variable!", in);
                return n;
            }
            b1 = firstoperand.equals("true");

            // Get the second operand
            String secondoperand = in.next();
            if (fl.isOp(secondoperand, ov.operators, ll))
                secondoperand = Execute(in, secondoperand).toString();

            // Dispose of the possible "
            if (secondoperand.charAt(0) == '"')
                secondoperand = secondoperand.substring(1);

            if (!secondoperand.equals("true") && !secondoperand.equals("false")) {
                // Type dismatch
                fl.PrintFalseInfo("Require a Bool variable!", in);
                return n;
            }
            b2 = secondoperand.equals("true");

            if (operator.equals("and")) {
                // and
                if (b1 && b2)
                    return "true";
                else
                    return "false";
            }
            else {
                // or
                if (b1 || b2)
                    return "true";
                else
                    return "false";
            }
        }

        else if (operator.equals("not")) {
            // not
            boolean b;

            // Get the operand
            String operand = in.next();

            // Dispose of the possible "
            if (operand.charAt(0) == '"')
                operand = operand.substring(1);

            if (fl.isOp(operand, ov.operators, ll))
                operand = Execute(in, operand).toString();
            if (!operand.equals("true") && !operand.equals("false")) {
                // Type dismatch
                fl.PrintFalseInfo("Require a Bool variable!", in);
                return n;
            }

            b = operand.equals("true");

            if (!b)
                return("true");
            else
                return("false");
        }

        else if (operator.equals("random")) {
            // random
            String str = in.next();
            if (!fl.isNum(str)) {
                // Input non-number
                fl.PrintFalseInfo("Input must be a number!", in);
                return n;
            }

            double num = Double.valueOf(str);
            double ret = Math.random() * num;

            return String.valueOf(ret);
        }

        else if (operator.equals("sqrt")) {
            // sqrt
            String str = in.next();
            if (!fl.isNum(str)) {
                // Input non-number
                fl.PrintFalseInfo("Input must be a number!", in);
                return n;
            }

            double num = Double.valueOf(str);
            double ret = Math.sqrt(num);

            return String.valueOf(ret);
        }

        else if (operator.equals("int")) {
            // int
            // sqrt
            String str = in.next();
            if (!fl.isNum(str)) {
                // Input non-number
                fl.PrintFalseInfo("Input must be a number!", in);
                return n;
            }

            double num = Double.valueOf(str);
            double ret = Math.floor(num);

            return String.valueOf((int)ret);
        }

        else if (operator.equals("erall")) {
            // erase all
            fl.erall(this);
        }

        else if (operator.equals("poall")) {
            // post all
            fl.poall(this);
        }

        else if (operator.equals("save")) {
            // save
            String str = in.next();
            Object obj;

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else
                    str = obj.toString();
            }

            // Dispose of the starting "
            if (str.charAt(0) == '"')
                str = str.substring(1);

            String path;
            if (str.endsWith(".txt"))
                path = str;
            else
                path = str + ".txt";

            fl.save(this, path);
        }

        else if (operator.equals("load")) {
            // load
            String str = in.next();
            Object obj;

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else
                    str = obj.toString();
            }

            // Dispose of the starting "
            if (str.charAt(0) == '"')
                str = str.substring(1);

            String path;
            if (str.endsWith(".txt"))
                path = str;
            else
                path = str + ".txt";

            fl.load(this, path);
        }

        else if (operator.equals("wait")) {
            // wait
            String str = in.next();
            Object obj;

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else
                    str = obj.toString();
            }

            // Dispose of the starting "
            if (str.charAt(0) == '"')
                str = str.substring(1);

            if (!fl.isInt(str)) {
                fl.PrintFalseInfo("Require an integer!", in);
                return n;
            }

            // Get sleep time
            int ms = Integer.valueOf(str);

            Thread.sleep(ms);
            return ms + " ms has passed.";
        }

        else if (operator.equals("pi")) {
            // pi
            return "3.14159";
        }

        else if (operator.equals("run")) {
            // run
            String str = in.next();
            Object obj;

            if (fl.isOp(str, ov.operators, ll)) {
                obj = Execute(in, str);

                if (obj.getClass().getName().equals("DataStructure.Null")) {
                    // Null return type
                    fl.PrintFalseInfo("Null return type!", in);
                    return n;
                } else {
                    str = obj.toString();
                    if (str.charAt(0) != '[' || str.charAt(str.length()-1) != ']') {
                        // Not a list
                        fl.PrintFalseInfo("Require a list!", in);
                        return n;
                    }
                }
            }
            else {
                String temp = in.nextLine();
                str += temp;
                if (str.charAt(0) != '[' || str.charAt(str.length()-1) != ']') {
                    // Not a list
                    fl.PrintFalseInfo("Require a list!", in);
                    return n;
                }
            }

            // Dispose of [ and ]
            str = str.substring(1, str.length()-1);

            // Execute
            Scanner Strin = new Scanner(str + "\n");
            while (Strin.hasNext()) {
                String temp = Strin.next();
                Object retobj = Execute(Strin, temp);

                // Exit the interpreter
                if (retobj.getClass().getName().equals("DataStructure.Exit"))
                    return new Exit();

                // Output has a value
                if (!(retobj.getClass().getName().equals("DataStructure.Null")))
                    System.out.println(retobj.toString());
            }
        }

        else if (operator.equals("//")) {
            // annotation
            in.nextLine();
        }

        else if (operator.equals("exit")) {
            // exit the interpreter
            return new Exit();
        }

        // If there is no return type, return Null
        return n;
    }
}
