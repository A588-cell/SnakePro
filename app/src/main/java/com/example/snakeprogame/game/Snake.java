package com.example.snakeprogame.game;

import java.util.ArrayList;

public class Snake {
    private final ArrayList<SnakePart> body = new ArrayList<>();
    private boolean shouldGrow = false;

    public Snake(int startX, int startY) {
        body.add(new SnakePart(startX, startY));
        body.add(new SnakePart(startX - 1, startY));
        body.add(new SnakePart(startX - 2, startY));
    }

    public ArrayList<SnakePart> getBody() {
        return body;
    }

    public SnakePart getHead() {
        return body.get(0);
    }

    public void grow() {
        shouldGrow = true;
    }

    public void moveTo(int newX, int newY) {
        body.add(0, new SnakePart(newX, newY)); // head
        if (!shouldGrow) {
            body.remove(body.size() - 1); // remove tail
        } else {
            shouldGrow = false;
        }
    }

    public boolean hitsItself() {
        SnakePart head = getHead();
        for (int i = 1; i < body.size(); i++) {
            SnakePart p = body.get(i);
            if (p.getX() == head.getX() && p.getY() == head.getY()) {
                return true;
            }
        }
        return false;
    }
}