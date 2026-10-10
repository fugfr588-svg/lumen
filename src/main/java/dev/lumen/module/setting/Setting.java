package dev.lumen.module.setting;

import com.google.gson.JsonElement;

/** A single user-configurable value belonging to a module. Subclasses know how to (de)serialise themselves. */
public abstract class Setting<T> {
    private final String name;
    protected T value;

    protected Setting(String name, T value) { this.name = name; this.value = value; }

    public String name() { return name; }
    public T get() { return value; }
    public void set(T v) { this.value = v; }

    public abstract JsonElement toJson();
    public abstract void fromJson(JsonElement e);
}
