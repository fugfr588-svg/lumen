package dev.lumen.module;

/** Tabs shown in the Click GUI sidebar. */
public enum Category {
    COMBAT("Combat"), HUD("HUD"), MOVEMENT("Movement"), VISUAL("Visual"), CLIENT("Client");

    private final String label;
    Category(String label) { this.label = label; }
    public String label() { return label; }
}
