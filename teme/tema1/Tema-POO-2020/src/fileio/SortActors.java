package fileio;

import java.util.Comparator;

final public class SortActors implements Comparator<ActorInputData> {
    @Override
    public int compare(final ActorInputData o1, final ActorInputData o2) {
        if (o1.getActorAwards() > o2.getActorAwards()) {
            return 1;
        }
        if (o1.getActorAwards() < o2.getActorAwards()) {
            return -1;
        }
        return o1.getName().compareTo(o2.getName());
    }
}
