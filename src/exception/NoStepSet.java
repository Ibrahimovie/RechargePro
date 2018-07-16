package exception;


public class NoStepSet extends Exception {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "该工步方案未设置工步";
    }
}


