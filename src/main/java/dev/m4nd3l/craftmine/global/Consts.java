package dev.m4nd3l.craftmine.global;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Consts {
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
}
