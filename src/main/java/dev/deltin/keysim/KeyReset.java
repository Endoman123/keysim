package dev.deltin.keysim;

public class KeyReset {

    private int tick = 0;
    private int keyCode;
    private String type;

    public KeyReset(int keyCode, String type)
    {
        this.keyCode = keyCode;
        this.type = type;
    }

    public void IncrementTick()
    {
        tick++;
    }
    public int GetCurrentTick()
    {
        return tick;
    }
    public int GetKeyCode()
    {
        return keyCode;
    }
    public String GetType() {
        return type;
    }
}
