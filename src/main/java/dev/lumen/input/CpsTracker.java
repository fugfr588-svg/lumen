package dev.lumen.input;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/** Counts mouse clicks in the last second by polling GLFW every rendered frame (rising-edge detection). */
public final class CpsTracker {
    private CpsTracker() {}

    private static final Deque<Long> LEFT = new ArrayDeque<>(), RIGHT = new ArrayDeque<>();
    private static boolean lastL, lastR;

    public static void poll(MinecraftClient mc) {
        long h = mc.getWindow().getHandle();
        boolean inGame = mc.currentScreen == null;
        boolean l = inGame && GLFW.glfwGetMouseButton(h, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean r = inGame && GLFW.glfwGetMouseButton(h, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        long now = System.currentTimeMillis();
        if (l && !lastL) LEFT.addLast(now);
        if (r && !lastR) RIGHT.addLast(now);
        lastL = l; lastR = r;
    }

    private static int count(Deque<Long> q) {
        long cutoff = System.currentTimeMillis() - 1000;
        while (!q.isEmpty() && q.peekFirst() < cutoff) q.pollFirst();
        return q.size();
    }

    public static int left() { return count(LEFT); }
    public static int right() { return count(RIGHT); }
}
