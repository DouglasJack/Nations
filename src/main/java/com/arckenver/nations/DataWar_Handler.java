package com.arckenver.nations;

import com.arckenver.nations.object.Nation;
import com.arckenver.nations.object.War;
import com.arckenver.nations.serializer.WarDeserializer;
import com.arckenver.nations.serializer.WarSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.UUID;

import static com.arckenver.nations.DataHandler.wars;


public class DataWar_Handler {

    private static File warDir;
    private static Gson gson;

    public static void init(File rootDir)
    {
        warDir = new File(rootDir, "wars");
        gson = (new GsonBuilder())
                .registerTypeAdapter(War.class, new WarSerializer())
                .registerTypeAdapter(War.class, new WarDeserializer())
                .setPrettyPrinting()
                .create();
    }

    public static void load()
    {
        warDir.mkdirs();
        wars = new Hashtable<>();
        for (File f : warDir.listFiles())
        {
            if (f.isFile() && f.getName().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.json"))
            {
                try
                {
                    String json = new String(Files.readAllBytes(f.toPath()));
                    War war = gson.fromJson(json, War.class);
                    if(!war.getDisabled()) {
                        wars.put(war.getUuid(), war);
                    }

                }
                catch (IOException e)
                {
                    NationsPlugin.getLogger().error("Error while loading file " + f.getName());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void save()
    {
        if(wars != null) {
            for (UUID uuid : wars.keySet()) {
                saveWar(uuid);
            }
        }
    }

    public static void saveWar(UUID uuid)
    {
        War war = wars.get(uuid);
        if (war == null)
        {
            NationsPlugin.getLogger().warn("Trying to save null war !");
            return;
        }
        File file = new File(warDir, uuid.toString() + ".json");
        try
        {
            if (!file.exists())
            {
                file.createNewFile();
            }
            String json = gson.toJson(war, War.class);
            NationsPlugin.getLogger().debug("Saving GSON"+json);
            Files.write(file.toPath(), json.getBytes());
        }
        catch (IOException e)
        {
            NationsPlugin.getLogger().error("Error while saving file " + file.getName() + " for war");
        }
    }
}
