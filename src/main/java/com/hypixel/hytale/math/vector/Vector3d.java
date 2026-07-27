package com.hypixel.hytale.math.vector;

import org.joml.Vector3dc;

/**
 * Stub that extends org.joml.Vector3d for 5.6 API compatibility.
 * The actual class at runtime is provided by the Hytale server.
 */
public class Vector3d extends org.joml.Vector3d {

    public Vector3d() {
        super();
    }

    public Vector3d(double x, double y, double z) {
        super(x, y, z);
    }

    public Vector3d(org.joml.Vector3d v) {
        super(v);
    }

    public Vector3d(Vector3dc v) {
        super(v);
    }

    public Vector3d(com.hypixel.hytale.math.vector.Vector3i v) {
        super(v.x, v.y, v.z);
    }

    public static final Vector3d ZERO = new Vector3d(0.0, 0.0, 0.0);
}