package com.damonlei.vimdroid.utils;

import com.damonlei.vimdroid.keyBoard.KeyCode;
import com.damonlei.vimdroid.keyBoard.KeyRequest;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * @author damonlei
 * @time 2017/3/14
 * @email danxionglei@foxmail.com
 */
class KeyRequestProcessor implements JsonSerializer<KeyRequest>, JsonDeserializer<KeyRequest> {
    @Override
    public JsonElement serialize(KeyRequest src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }
        JsonObject object = new JsonObject();
        object.addProperty("name", src.name.getAsLowerStr());
        object.addProperty("ctrl", src.ctrl);
        object.addProperty("meta", src.meta);
        object.addProperty("shift", src.shift);
        return object;
    }

    @Override
    public KeyRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null) {
            return null;
        }
        JsonObject object = json.getAsJsonObject();
        KeyRequest request = new KeyRequest();
        request.name = KeyCode.parse(object.get("name").getAsString());
        request.ctrl = object.has("ctrl") && object.get("ctrl").getAsBoolean();
        request.shift = object.has("shift") && object.get("shift").getAsBoolean();
        request.meta = object.has("meta") && object.get("meta").getAsBoolean();
        return request;
    }
}
