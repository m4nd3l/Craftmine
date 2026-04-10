package dev.m4nd3l.craftmine.renderer.input;

import static org.lwjgl.glfw.GLFW.*;

public enum KeyboardKeys {

    A(GLFW_KEY_A, "aA", true),
    B(GLFW_KEY_B, "bB", true),
    C(GLFW_KEY_C, "cC", true),
    D(GLFW_KEY_D, "dD", true),
    E(GLFW_KEY_E, "eE", true),
    F(GLFW_KEY_F, "fF", true),
    G(GLFW_KEY_G, "gG", true),
    H(GLFW_KEY_H, "hH", true),
    I(GLFW_KEY_I, "iI", true),
    J(GLFW_KEY_J, "jJ", true),
    K(GLFW_KEY_K, "kK", true),
    L(GLFW_KEY_L, "lL", true),
    M(GLFW_KEY_M, "mM", true),
    N(GLFW_KEY_N, "nN", true),
    O(GLFW_KEY_O, "oO", true),
    P(GLFW_KEY_P, "pP", true),
    Q(GLFW_KEY_Q, "qQ", true),
    R(GLFW_KEY_R, "rR", true),
    S(GLFW_KEY_S, "sS", true),
    T(GLFW_KEY_T, "tT", true),
    U(GLFW_KEY_U, "uU", true),
    V(GLFW_KEY_V, "vV", true),
    W(GLFW_KEY_W, "wW", true),
    X(GLFW_KEY_X, "xX", true),
    Y(GLFW_KEY_Y, "yY", true),
    Z(GLFW_KEY_Z, "zZ", true),


    NUM_0(GLFW_KEY_0, "0", false),
    NUM_1(GLFW_KEY_1, "1", false),
    NUM_2(GLFW_KEY_2, "2", false),
    NUM_3(GLFW_KEY_3, "3", false),
    NUM_4(GLFW_KEY_4, "4", false),
    NUM_5(GLFW_KEY_5, "5", false),
    NUM_6(GLFW_KEY_6, "6", false),
    NUM_7(GLFW_KEY_7, "7", false),
    NUM_8(GLFW_KEY_8, "8", false),
    NUM_9(GLFW_KEY_9, "9", false),


    SPACE(        GLFW_KEY_SPACE,         "space", false),
    APOSTROPHE(   GLFW_KEY_APOSTROPHE,    "'",     false),
    COMMA(        GLFW_KEY_COMMA,         ",",     false),
    MINUS(        GLFW_KEY_MINUS,         "-",     false),
    PERIOD(       GLFW_KEY_PERIOD,        ".",     false),
    SLASH(        GLFW_KEY_SLASH,         "/",     false),
    SEMICOLON(    GLFW_KEY_SEMICOLON,     ";",     false),
    EQUAL(        GLFW_KEY_EQUAL,         "=",     false),
    LEFT_BRACKET( GLFW_KEY_LEFT_BRACKET,  "[",     false),
    BACKSLASH(    GLFW_KEY_BACKSLASH,     "\\",    false),
    RIGHT_BRACKET(GLFW_KEY_RIGHT_BRACKET, "]",     false),
    GRAVE_ACCENT( GLFW_KEY_GRAVE_ACCENT,  "`",     false),


    ESC(         GLFW_KEY_ESCAPE,       "esc",         false),
    ENTER(       GLFW_KEY_ENTER,        "enter",       false),
    TAB(         GLFW_KEY_TAB,          "tab",         false),
    BACKSPACE(   GLFW_KEY_BACKSPACE,    "backspace",   false),
    INSERT(      GLFW_KEY_INSERT,       "insert",      false),
    DELETE(      GLFW_KEY_DELETE,       "delete",      false),
    RIGHT(       GLFW_KEY_RIGHT,        "right",       false),
    LEFT(        GLFW_KEY_LEFT,         "left",        false),
    DOWN(        GLFW_KEY_DOWN,         "down",        false),
    UP(          GLFW_KEY_UP,           "up",          false),
    PAGE_UP(     GLFW_KEY_PAGE_UP,      "page up",     false),
    PAGE_DOWN(   GLFW_KEY_PAGE_DOWN,    "page down",   false),
    HOME(        GLFW_KEY_HOME,         "home",        false),
    END(         GLFW_KEY_END,          "end",         false),
    CAPS_LOCK(   GLFW_KEY_CAPS_LOCK,    "caps lock",   false),
    SCROLL_LOCK( GLFW_KEY_SCROLL_LOCK,  "scroll lock", false),
    NUM_LOCK(    GLFW_KEY_NUM_LOCK,     "num lock",    false),
    PRINT_SCREEN(GLFW_KEY_PRINT_SCREEN, "print screen",false),
    PAUSE(       GLFW_KEY_PAUSE,        "pause",       false),


    F1(GLFW_KEY_F1,   "f1",   false),
    F2(GLFW_KEY_F2,   "f2",   false),
    F3(GLFW_KEY_F3,   "f3",   false),
    F4(GLFW_KEY_F4,   "f4",   false),
    F5(GLFW_KEY_F5,   "f5",   false),
    F6(GLFW_KEY_F6,   "f6",   false),
    F7(GLFW_KEY_F7,   "f7",   false),
    F8(GLFW_KEY_F8,   "f8",   false),
    F9(GLFW_KEY_F9,   "f9",   false),
    F10(GLFW_KEY_F10, "f10",  false),
    F11(GLFW_KEY_F11, "f11",  false),
    F12(GLFW_KEY_F12, "f12",  false),
    F13(GLFW_KEY_F13, "f13",  false),
    F14(GLFW_KEY_F14, "f14",  false),
    F15(GLFW_KEY_F15, "f15",  false),
    F16(GLFW_KEY_F16, "f16",  false),
    F17(GLFW_KEY_F17, "f17",  false),
    F18(GLFW_KEY_F18, "f18",  false),
    F19(GLFW_KEY_F19, "f19",  false),
    F20(GLFW_KEY_F20, "f20",  false),
    F21(GLFW_KEY_F21, "f21",  false),
    F22(GLFW_KEY_F22, "f22",  false),
    F23(GLFW_KEY_F23, "f23",  false),
    F24(GLFW_KEY_F24, "f24",  false),
    F25(GLFW_KEY_F25, "f25",  false),


    LEFT_SHIFT(   GLFW_KEY_LEFT_SHIFT,    "left shift",   false),
    LEFT_CONTROL( GLFW_KEY_LEFT_CONTROL,  "left ctrl",    false),
    LEFT_ALT(     GLFW_KEY_LEFT_ALT,      "left alt",     false),
    LEFT_SUPER(   GLFW_KEY_LEFT_SUPER,    "left super",   false),
    RIGHT_SHIFT(  GLFW_KEY_RIGHT_SHIFT,   "right shift",  false),
    RIGHT_CONTROL(GLFW_KEY_RIGHT_CONTROL, "right ctrl",   false),
    RIGHT_ALT(    GLFW_KEY_RIGHT_ALT,     "right alt",    false),
    RIGHT_SUPER(  GLFW_KEY_RIGHT_SUPER,   "right super",  false),
    MENU(         GLFW_KEY_MENU,          "menu",         false);

    private int keyCode;
    private String name;
    private boolean isLetter;

    KeyboardKeys(int keyCode, String name, boolean isLetter) {
        this.keyCode = keyCode;
        this.name = name;
        this.isLetter = isLetter;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public String getName() {
        return this.name;
    }

    private boolean isLetter() {
        return isLetter;
    }

    public String getLowercaseLetter() {
        if (isLetter) return getName().replaceAll("[A-Z]", "");
        return "";
    }

    public String getUppercaseLetter() {
        if (isLetter) return getName().replaceAll("[a-z]", "");
        return "";
    }

    @Override
    public String toString() {
        return String.format("Key %s: %s", getKeyCode(), getName());
    }
}