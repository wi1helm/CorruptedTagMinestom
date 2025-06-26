package nub.wi1helm.map;

import it.unimi.dsi.fastutil.Pair;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public enum TagMap {
    LOBBY("resources/lobby", Pair.of(5,3), new Pos(0.5,0,0.5,0,0)),
    BUSY_CITY("resources/busy_city", Pair.of(5,5), new Pos(0.5,0,0.5,0,0));


    private final String path;
    private final Pair<Integer, Integer> chunksAllowed;
    private final Pos spawn;
    private final Instance instance;

    TagMap(String path, Pair<Integer, Integer> chunksAllowed, Pos spawn) {
        this.path = path; this.chunksAllowed = chunksAllowed; this.spawn = spawn;
        this.instance = new MapInstance(path, chunksAllowed.first(), chunksAllowed.value());
    }

    public Pos getSpawn() {
        return spawn;
    }

    public Instance getInstance() {
        return instance;
    }
}
