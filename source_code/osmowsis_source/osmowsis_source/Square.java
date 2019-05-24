public class Square {
    private boolean hasPuppy;
    private boolean hasMower;
    private State state;

    public Square(State state) {
        this.state = state;
    }

    public Square(boolean hasPuppy, boolean hasMower, State state) {
        this.hasPuppy = hasPuppy;
        this.hasMower = hasMower;
        this.state = state;
    }

    public boolean isHasPuppy() {
        return hasPuppy;
    }

    public void setHasPuppy(boolean hasPuppy) {
        this.hasPuppy = hasPuppy;
    }

    public boolean isHasMower() {
        return hasMower;
    }

    public void setHasMower(boolean hasMower) {
        this.hasMower = hasMower;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String toString(){
        // produce square information as string
        String result = this.state.toString().toLowerCase();
        if(hasMower)
            result = "mower";
        if(hasPuppy)
            result = "puppy_" + result;
        return result;
    }
}
