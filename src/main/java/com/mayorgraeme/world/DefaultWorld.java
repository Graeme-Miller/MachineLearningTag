package com.mayorgraeme.world;

import com.mayorgraeme.XY;
import com.mayorgraeme.occupant.Occupant;

import java.util.HashMap;
import java.util.Map;

public class DefaultWorld implements World {

    private final Occupant[][] occupants;
    private final Map<Occupant, XY> occupantLocation;

    private final int DEFAULT_X = 25;
    private final int DEFAULT_Y = 25;

    public DefaultWorld() {
        this.occupants = new Occupant[DEFAULT_X][DEFAULT_Y];
        occupantLocation = new HashMap<>();
    }

    @Override
    public Occupant[][] getOccupantMap() {
        return occupants;
    }

    @Override
    public void moveOccupant(Occupant occupant, XY oldXY, XY newXY) {
        removeOccupant(occupant, oldXY);
        addOccupant(occupant, newXY);
    }

    @Override
    public void addOccupant(Occupant occupant, XY xy) {
        Occupant oldOccupant = occupants[xy.getX()][xy.getY()];
        if(oldOccupant != null) {
            throw new IllegalArgumentException("Tried to add "+occupant+" to "+xy +" but encountered "+oldOccupant);
        }

        occupantLocation.put(occupant, xy);
        occupants[xy.getX()][xy.getY()] = occupant;

    }

    @Override
    public void removeOccupant(Occupant occupant, XY xy) {
        Occupant oldOccupant = occupants[xy.getX()][xy.getY()];
        if(oldOccupant == null || !oldOccupant.equals(occupant)) {
            throw new IllegalArgumentException("Tried to remove "+occupant+" from "+xy +" but encountered "+oldOccupant);
        }

        occupantLocation.put(occupant, null);
        occupants[xy.getX()][xy.getY()] = null;

    }

    @Override
    public World clone() {
        World newWorld = new DefaultWorld();
        occupantLocation.forEach(newWorld::addOccupant);
        return newWorld;
    }

    @Override
    public boolean tick() {
        for (Occupant occupant : occupantLocation.keySet()) {
            if(occupant.process(this)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkCanMove(XY xy) {
        if(xy.getX() < 0 || xy.getY() < 0) {
            return false;
        } else if (xy.getX() >= occupants[0].length) {
            return false;
        } else if (xy.getY() >= occupants.length) {
            return false;
        }

        return occupants[xy.getX()][xy.getY()] == null;
    }

    @Override
    public XY getOccupantLocation(Occupant occupant) {
        return occupantLocation.get(occupant);
    }
}
