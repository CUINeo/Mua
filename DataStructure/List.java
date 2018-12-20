package DataStructure;

// The data structure of a list
public class List extends Value {
    public String name;
    public String[] content;

    public List () { }

    List(String name, String[] content) {
        this.name = name;
        this.content = content;
    }
}
