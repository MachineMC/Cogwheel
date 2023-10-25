package org.machinemc.cogwheel.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.machinemc.cogwheel.config.ConfigProperties;

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
