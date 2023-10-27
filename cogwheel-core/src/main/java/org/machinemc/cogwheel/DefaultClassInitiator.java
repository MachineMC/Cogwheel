package org.machinemc.cogwheel;

import org.machinemc.cogwheel.util.JavaUtils;

class DefaultClassInitiator implements ClassInitiator {

    @Override
    public <T> T newInstance(Class<T> type) {
        return JavaUtils.newInstance(type);
    }

}
