package communication.adapters;

import com.google.gson.*;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import eu.iv4xr.framework.spatial.Vec3;

import java.lang.reflect.Type;

/**
 * Custom serializer of WorldEntity with MAEV.
 */
public class MAEVEntityAdapter implements JsonDeserializer<WorldEntity>, JsonSerializer<WorldEntity> {

    /**
     * This deserializer should deserialize Entities based on their type
     */
    @Override
    public WorldEntity deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String id = obj.get("id").getAsString();
        String type = obj.get("tag").getAsString();
        WorldEntity entity = new WorldEntity(id, type, true);
        entity.position = context.deserialize(obj.get("position"), Vec3.class);
        entity.properties.put("property", obj.get("property").getAsString());
        entity.velocity = context.deserialize(obj.get("velocity"), Vec3.class);
        return entity;
    }

    /**
     * This serializer should serialize Entities normally, but entity types should be a number
     */
    @Override
    public JsonElement serialize(WorldEntity src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.add("id", context.serialize(src.id));
        obj.add("tag", context.serialize(src.type));
        obj.add("position", context.serialize(src.position));
        obj.add("properties", context.serialize(src.properties));
        obj.add("velocity", context.serialize(src.position));
        return obj;
    }
}
