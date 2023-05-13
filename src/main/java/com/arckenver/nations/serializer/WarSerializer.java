package com.arckenver.nations.serializer;

import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.War;
import com.google.gson.*;

import java.lang.reflect.Type;


public class WarSerializer implements JsonSerializer<War> {

    @Override
    public JsonElement serialize(War war, Type type, JsonSerializationContext ctx) {
        JsonObject json = new JsonObject();

        json.add("uuid", new JsonPrimitive(war.getUuid().toString()));
        json.add("attacker", new JsonPrimitive(war.attacker.getUUID().toString()));
        json.add("defender", new JsonPrimitive(war.defender.getUUID().toString()));

        return json;
    }
}
