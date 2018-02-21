package com.mayorgraeme.occupant;

import com.mayorgraeme.world.World;

public interface Occupant {

    void process(World world);
    char getChar();
}
