package com.codesnippler.Model;


import javax.json.JsonObject;
import java.util.Collection;


public abstract class JsonModel {
    public abstract JsonObject toJson();
    public abstract JsonObject toJson(Collection<String> hidden);
}
