package fileio;

import java.util.Comparator;

final public class SortActorsDescription implements Comparator<ActorInputData> {
    @Override
    public int compare(final ActorInputData o1, final ActorInputData o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
