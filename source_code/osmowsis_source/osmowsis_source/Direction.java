import java.util.*;

public enum Direction {
    N(0),
    NE(1),
    E(2),
    SE(3),
    S(4),
    SW(5),
    W(6),
    NW(7);

    private int code;
    private static Map map = new HashMap<>();

    Direction(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    static {
        for (Direction dir : Direction.values()) {
            map.put(dir.code, dir);
        }
    }

    public static Direction valueOf(int i) {
        return (Direction) map.get(i);
    }

    public static String stringOf(Direction dir){
        switch (dir){
            case E:
                return "east";
            case N:
                return "north";
            case S:
                return "south";
            case W:
                return "west";
            case NE:
                return "northeast";
            case SE:
                return "southeast";
            case SW:
                return "southwest";
            case NW:
                return "northwest";
        }
        return "";
    }

}
