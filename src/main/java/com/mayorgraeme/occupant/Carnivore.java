package com.mayorgraeme.occupant;

import com.mayorgraeme.XY;
import com.mayorgraeme.world.World;

import java.util.TreeMap;

import static com.mayorgraeme.world.WorldServices.distanceBetweenXY;

public class Carnivore implements Occupant {

    private final Herbivore toChase;
    private boolean carnivorePause = false;

    public Carnivore(Herbivore toChase) {
        this.toChase = toChase;
    }

    public boolean process(World world) {

        if (carnivorePause) {
            carnivorePause = false;
            return false;
        } else {
            carnivorePause = true;
        }

        TreeMap<Double, XY> doubleList= new TreeMap<>();
        XY carnivoreLoc = world.getOccupantLocation(this);
        XY herbivoreLoc = world.getOccupantLocation(toChase);

        double currentDistance = distanceBetweenXY( herbivoreLoc, carnivoreLoc);
        if(currentDistance <= 1) {
            return true;
        }

        //up
        XY upLocation = new XY(carnivoreLoc.getX(),carnivoreLoc.getY()+1);
        double upDistance = distanceBetweenXY( herbivoreLoc, upLocation);
        if(world.checkCanMove(upLocation)) {
            doubleList.put(upDistance, upLocation);
        }

        //down
        XY downLocation = new XY(carnivoreLoc.getX(),carnivoreLoc.getY()-1);
        double downDistance = distanceBetweenXY( herbivoreLoc, downLocation);
        if(world.checkCanMove(downLocation)) {
            doubleList.put(downDistance, downLocation);
        }

        //right
        XY rightLocation = new XY(carnivoreLoc.getX() + 1,carnivoreLoc.getY());
        double rightDistance = distanceBetweenXY( herbivoreLoc, rightLocation);
        if(world.checkCanMove(rightLocation)) {
            doubleList.put(rightDistance, rightLocation);
        }

        //left
        XY leftLocation = new XY(carnivoreLoc.getX() - 1,carnivoreLoc.getY());
        double leftDistance = distanceBetweenXY( herbivoreLoc, leftLocation);
        if(world.checkCanMove(leftLocation)) {
            doubleList.put(leftDistance, leftLocation);
        }


        if(!doubleList.isEmpty()) {
            XY xy = doubleList.firstEntry().getValue();
            world.moveOccupant(this, carnivoreLoc, xy);
        }

        return false;
    }



    public char getChar() {
        return 'c';
    }
}
