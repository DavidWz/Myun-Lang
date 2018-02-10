package myun;

/**
 * Represents the scope of a code block.
 */
public class Scope {
    private static int ID = 0;

    private int id;

    public Scope() {
        id = Scope.ID;
        Scope.ID++;
    }

    public int getID() {
        return id;
    }
}
