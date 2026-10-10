package dev.lumen.util;

/** ARGB colour helpers. All "rgb" parameters are 0xRRGGBB, all "argb" results are 0xAARRGGBB. */
public final class Colors {
    private Colors() {}

    public static float clamp01(float v) { return v < 0 ? 0 : Math.min(v, 1); }

    public static int argb(int rgb, float alpha) {
        return ((int) (clamp01(alpha) * 255f) << 24) | (rgb & 0xFFFFFF);
    }

    public static int opaque(int rgb) { return 0xFF000000 | (rgb & 0xFFFFFF); }

    public static int lerp(int a, int b, float t) {
        t = clamp01(t);
        int r = 0;
        for (int shift = 24; shift >= 0; shift -= 8) {
            int ca = (a >>> shift) & 0xFF, cb = (b >>> shift) & 0xFF;
            r |= ((int) (ca + (cb - ca) * t) & 0xFF) << shift;
        }
        return r;
    }

    /** HSV -> 0xRRGGBB. h, s, v in 0..1. */
    public static int hsb(float h, float s, float v) {
        h = h - (float) Math.floor(h);
        float c = v * s;
        float x = c * (1 - Math.abs((h * 6) % 2 - 1));
        float m = v - c;
        float r, g, b;
        switch ((int) (h * 6) % 6) {
            case 0 -> { r = c; g = x; b = 0; }
            case 1 -> { r = x; g = c; b = 0; }
            case 2 -> { r = 0; g = c; b = x; }
            case 3 -> { r = 0; g = x; b = c; }
            case 4 -> { r = x; g = 0; b = c; }
            default -> { r = c; g = 0; b = x; }
        }
        return ((int) ((r + m) * 255) << 16) | ((int) ((g + m) * 255) << 8) | (int) ((b + m) * 255);
    }
}
