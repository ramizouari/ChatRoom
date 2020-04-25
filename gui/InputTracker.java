package gui;
import java.util.*;

/*
    this class is used to track the user's input,it contains a list of what the user was typing
    this list will be used to facilate retyping messages via the UP and DOWN key
*/
public class InputTracker {
    private List<String> input;
    private int currIndex=0;
    public InputTracker()
    {
        input=new ArrayList<String>();
    }
    public void add(String s)
    {
        input.add(s);
        currIndex=0;
    }
    public void incrementIndex()
    {
            currIndex++;
    }
    public boolean isEmpty()
    {
        return input.isEmpty();
    }
    public boolean hasReachedLastElement()
    {
        return currIndex==input.size()-1;
    }

    public boolean hasReachedFirstElement()
    {
        return currIndex==0;
    }
    public void decrementIndex()
    {
            currIndex--;
    }
    public String getCurrentElement()
    {
        return input.get(input.size()-currIndex-1);
    }
    public String get(int index)
    {
        return input.get(input.size()-index-1);
    }
}