package com.mayorgraeme.world;

import com.mayorgraeme.XY;
import com.mayorgraeme.occupant.Occupant;

public interface World {

    Occupant[][] getOccupantMap();
    void moveOccupant(Occupant occupant, XY oldXY, XY newXY);
    void addOccupant(Occupant occupant, XY xy);
    void removeOccupant(Occupant occupant, XY xy);
    boolean checkCanMove(XY xy);
    public XY getOccupantLocation(Occupant occupant);
    public World clone();
}
