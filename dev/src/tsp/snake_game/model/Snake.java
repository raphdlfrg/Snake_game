package tsp.snake_game.model;

import tsp.snake_game.model.Coordinates;

import java.util.List;
import java.util.LinkedList;


public class Snake {
    private List<Coordinates> snakePosition;
    private Coordinates destination;

    public Snake(List snakePosition) {
        this.snakePosition = snakePosition;
    }

    public Snake() {
        this.snakePosition = new LinkedList<>();
        this.snakePosition.add(new Coordinates(0,0));
        for (float i=1; i<=Config.SNAKESIZE; i=i++) {
            this.snakePosition.add(new Coordinates(i,0));
        }
    }

    public List<Coordinates> getSnakePosition() {
        return snakePosition;
    }
}
