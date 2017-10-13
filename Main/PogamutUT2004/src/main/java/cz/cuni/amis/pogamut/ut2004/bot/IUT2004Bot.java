package cz.cuni.amis.pogamut.ut2004.bot;

import cz.cuni.amis.pogamut.unreal.bot.IUnrealBot;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cz.cuni.amis.pogamut.ut2004.communication.messages.gbinfomessages.ConfigChange;

/**
 * Unreal Tournament bot.
 * @author ik
 */
public interface IUT2004Bot extends IUnrealBot {

    public static enum BoolBotParam implements Serializable {
        // TODO comment

        AUTO_PICKUP_OFF,
        INVULNERABLE,
        SHOW_DEBUG,
        AUTO_TRACE,
        DRAW_TRACE_LINES,
        MANUAL_SPAWN,
        SHOW_FOCAL_POINT,
        SYNCHRONOUS_OFF;
        String propertyName = null;

        /**
         * Gets property name corresponding to enum constant.
         * @return
         */
        public String getPropName() {
            if (propertyName == null) {
                String name = name();
                boolean upperFlag = true;
                // converts ABC_DEF to AbcDef
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < name.length(); i++) {
                    char ch = name.charAt(i);
                    if (ch == '_') {
                        upperFlag = true;
                        continue;
                    }
                    if (!upperFlag) {
                        ch = Character.toLowerCase(ch);
                    } else {
                        upperFlag = false;
                    }

                    builder.append(ch);
                }
                propertyName = builder.toString();
            }
            return propertyName;
        }

        public void set(Object conf, boolean value) throws Exception {
            Method m = conf.getClass().getMethod("set" + getPropName(), Boolean.TYPE);
            m.invoke(conf, value);
        }

        public void setField(Object conf, boolean value) throws Exception {
            Field f = conf.getClass().getDeclaredField(getPropName());
            f.setAccessible(true);
            f.set(conf, value);
        }

        public boolean get(ConfigChange conf) throws Exception {
            Method m = conf.getClass().getMethod("is" + getPropName());
            return (Boolean) m.invoke(conf, new Object[0]);
        }
    }

    /**
     * Configures bot property.
     * @param param
     * @param value
     */
    public void setBoolConfigure(BoolBotParam param, boolean value);

    /**
     * Get configuration parameter value.
     * @param param
     * @return
     */
    public boolean getBoolConfigure(BoolBotParam param);
}
