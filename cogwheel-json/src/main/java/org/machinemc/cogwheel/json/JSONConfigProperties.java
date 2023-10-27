package org.machinemc.cogwheel.json;

import org.machinemc.cogwheel.config.ConfigProperties;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONConfigProperties extends ConfigProperties {

    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .disableInnerClassSerialization()
            .disableJdkUnsafe()
            .serializeNulls()
            .create();

    public Gson gson() {
        return gson;
    }

}
