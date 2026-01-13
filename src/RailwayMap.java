import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

import TSim.CommandException;
import TSim.TSimInterface;

class RailwayMap {
    private static HashMap<Position, Section> sensorMap = null;

    public static void init() {
        sensorMap = new HashMap<>();

        int N_SEMS = 9;
        Semaphore[] sems = new Semaphore[N_SEMS];
        for (int i = 0; i < sems.length; i++) {
            sems[i] = new Semaphore(1);
        }

        Position[] switches = {
            new Position(3, 11),
            new Position(4, 9),
            new Position(15, 9),
            new Position(17, 7)
        };

        Object[][] sharedDefs = {
            { Direction.TO_B, 0, new Position(6, 7) },
            { Direction.TO_B, 0, new Position(8, 5) },
            { Direction.TO_A, 0, new Position(10, 7) },
            { Direction.TO_A, 0, new Position(10, 8) }
        };

        Section[] sharedSections = new Section[sharedDefs.length];
        for (int i = 0; i < sharedDefs.length; i++) {
            sharedSections[i] = new Section(
                (Direction) sharedDefs[i][0],
                sems[(int) sharedDefs[i][1]],
                (Position) sharedDefs[i][2]
            );
        }

        Object[][] trackDefs = {
            { Direction.TO_B, 1, 5, 11 },
            { Direction.TO_B, 2, 3, 13 },
            { Direction.TO_B, 3, 2, 9 },
            { Direction.TO_B, 4, 13, 9 },
            { Direction.TO_B, 5, 13, 10 },
            { Direction.TO_B, 6, 19, 7 },
            { Direction.TO_B, 7, 16, 3 },
            { Direction.TO_B, 8, 16, 5 },

            { Direction.TO_A, 1, 16, 11 },
            { Direction.TO_A, 2, 16, 13 },
            { Direction.TO_A, 3, 1, 11 },
            { Direction.TO_A, 4, 6, 9 },
            { Direction.TO_A, 5, 6, 10 },
            { Direction.TO_A, 6, 17, 9 },
            { Direction.TO_A, 7, 15, 7 },
            { Direction.TO_A, 8, 15, 8 }
        };
    
        Track[] tracks = new Track[trackDefs.length];
        for (int i = 0; i < trackDefs.length; i++) {
            tracks[i] = new Track(
                (Direction) trackDefs[i][0],
                sems[(int) trackDefs[i][1]],
                new Position((int) trackDefs[i][2], (int) trackDefs[i][3])
            );
        }
    
        int[][] connDefs = {
            {0, 2, 0, TSimInterface.SWITCH_LEFT},
            {1, 2, 0, TSimInterface.SWITCH_RIGHT},
            {2, 3, 1, TSimInterface.SWITCH_LEFT},
            {2, 4, 1, TSimInterface.SWITCH_RIGHT},
            {3, 5, 2, TSimInterface.SWITCH_RIGHT},
            {4, 5, 2, TSimInterface.SWITCH_LEFT},
            {5, 6, 3, TSimInterface.SWITCH_RIGHT},
            {5, 7, 3, TSimInterface.SWITCH_LEFT},
    
            {10, 8, 0, TSimInterface.SWITCH_LEFT},
            {10, 9, 0, TSimInterface.SWITCH_RIGHT},
            {11, 10, 1, TSimInterface.SWITCH_LEFT},
            {12, 10, 1, TSimInterface.SWITCH_RIGHT},
            {13, 11, 2, TSimInterface.SWITCH_RIGHT},
            {13, 12, 2, TSimInterface.SWITCH_LEFT},
            {14, 13, 3, TSimInterface.SWITCH_RIGHT},
            {15, 13, 3, TSimInterface.SWITCH_LEFT}
        };
    
        for (int[] def : connDefs) {
            int from = def[0], to = def[1], sw = def[2], dir = def[3];
            tracks[from].connect(tracks[to], switches[sw], dir);
        }
    }

    public static Section getSection(Position pos) {
        if (sensorMap == null) throw new IllegalStateException("Map not initialized!");
        return sensorMap.get(pos);
    }

    public enum Direction {
        TO_A,
        TO_B
    }

    public static class Position {
        public final int x, y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            return x * 100 + y;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Position && this.hashCode() == o.hashCode();
        }
    }

    public static class Section {
        private final Semaphore sem;
        private final Direction dir;

        public Section(Direction dir, Semaphore sem, Position sensor) {
            this.dir = dir;
            this.sem = sem;
            sensorMap.put(sensor, this);
        }

        public Direction getDirection() {
            return dir;
        }

        public boolean tryAcquire() {
            return sem.tryAcquire();
        }

        public void acquire() {
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void release() {
            sem.release();
        }
    }

    public static class Track extends Section {
        private final List<Connection> connections = new ArrayList<>();

        public Track(Direction dir, Semaphore sem, Position sensor) {
            super(dir, sem, sensor);
        }

        public void connect(Track next, Position switchPos, int switchDir) {
            connections.add(new Connection(next, switchPos, switchDir));
        }

        public boolean tryAcquireNext() throws CommandException {
            for (int i = 0; i < connections.size(); i++) {
                Connection c = connections.get(i);
                Track next = c.getNext();

                if (i == connections.size() - 1) {
                    next.acquire();
                    c.switchRail();
                    return true;
                }
                else if (next.tryAcquire()) {
                    c.switchRail();
                    return true;
                }
            }
            return false;
        }
    }

    public static class Connection {
        private final Track next;
        private final Position switchPos;
        private final int switchDir;

        public Connection(Track next, Position pos, int dir) {
            this.next = next;
            this.switchPos = pos;
            this.switchDir = dir;
        }

        public Track getNext() {
            return next;
        }

        public void switchRail() throws CommandException {
            TSimInterface.getInstance().setSwitch(switchPos.x, switchPos.y, switchDir);
        }
    }
}