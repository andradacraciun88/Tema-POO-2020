package fileio;

import java.util.Comparator;

final public class Sort implements Comparator<ActorInputData> {

    @Override
    public int compare(final ActorInputData o1, final ActorInputData o2) {
        if (o1.getMed() > o2.getMed()) {
            return 1;
        }
        if (o1.getMed() < o2.getMed()) {
            return -1;
        }
        return o1.getName().compareTo(o2.getName());
    }

}
