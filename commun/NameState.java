package commun;

//For verifying the validity of username
abstract public class NameState {
    public static final int VALID=0;
    public static final int INVALID=1;
    public static final int EXISTS=2;
    public static final int RESERVED=3;
}