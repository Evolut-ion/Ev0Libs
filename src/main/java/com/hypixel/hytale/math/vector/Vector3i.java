/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.vector;

public class Vector3i {
    public int x;
    public int y;
    public int z;

    public Vector3i() {
    }

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i(org.joml.Vector3i v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vector3i)) {
            return false;
        }
        Vector3i other = (Vector3i)obj;
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    public int hashCode() {
        int result = Integer.hashCode(this.x);
        result = 31 * result + Integer.hashCode(this.y);
        result = 31 * result + Integer.hashCode(this.z);
        return result;
    }

    public String toString() {
        return "Vector3i(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}

