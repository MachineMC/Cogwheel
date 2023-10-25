package org.machinemc.cogwheel;

public interface ClassInitiator {

    ClassInitiator DEFAULT = new DefaultClassInitiator();

    <T> T newInstance(Class<T> type);

}
