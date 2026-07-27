package com.hypixel.hytale.math.vector;

import org.joml.Vector3ic;

/**
 * Stub that extends org.joml.Vector3i for 5.6 API compatibility.
 * The actual class at runtime is provided by the Hytale server.
 */
public class Vector3i extends org.joml.Vector3i {

    public Vector3i() {
        super();
    }

    public Vector3i(int x, int y, int z) {
        super(x, y, z);
    }

    public Vector3i(org.joml.Vector3i v) {
        super(v);
    }

    public Vector3i(Vector3ic v) {
        super(v);
    }
}