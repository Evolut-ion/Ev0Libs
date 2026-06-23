package com.hypixel.hytale.math.vector;

public class Vector3f extends org.joml.Vector3f {
    public static final Vector3f ZERO = new Vector3f(0, 0, 0);

    public Vector3f() { super(); }
    public Vector3f(float x, float y, float z) { super(x, y, z); }
    public Vector3f(org.joml.Vector3fc v) { super(v); }

    public float getPitch() { return this.x; }
    public float getYaw() { return this.y; }

    @Override
    public Vector3f clone() { return new Vector3f(this.x, this.y, this.z); }
}
