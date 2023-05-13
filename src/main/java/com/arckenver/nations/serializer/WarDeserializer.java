package com.arckenver.nations.serializer;

import com.arckenver.nations.object.*;
import com.google.gson.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WarDeserializer implements JsonDeserializer<War> {
    @Override
    public War deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        UUID uuid = UUID.fromString(obj.get("uuid").getAsString());
        UUID attacker = UUID.fromString(obj.get("attacker").getAsString());
        UUID defender = UUID.fromString(obj.get("defender").getAsString());

        War war = new War(uuid, attacker, defender);

        return war;
    }
}

