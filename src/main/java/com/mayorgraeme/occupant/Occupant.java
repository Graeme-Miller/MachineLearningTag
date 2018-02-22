package com.mayorgraeme.occupant;

import com.mayorgraeme.world.World;

public interface Occupant {

    boolean process(World world);
    char getChar();
}
