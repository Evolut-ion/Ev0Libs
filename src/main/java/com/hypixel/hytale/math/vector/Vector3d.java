package com.hypixel.hytale.math.vector;

public class Vector3d extends org.joml.Vector3d {
    public static final Vector3d ZERO = new Vector3d();

    public Vector3d() { super(); }
    public Vector3d(double x, double y, double z) { super(x, y, z); }
    public Vector3d(org.joml.Vector3dc v) { super(v); }

    @Override
    public Vector3d clone() { return new Vector3d(this.x, this.y, this.z); }
}
