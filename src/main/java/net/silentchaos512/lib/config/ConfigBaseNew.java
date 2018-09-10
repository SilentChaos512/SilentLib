/*
 * Silent Lib -- ConfigBaseNew
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.config;

import net.silentchaos512.lib.util.I18nHelper;
import net.silentchaos512.lib.util.LogHelper;

import javax.activation.UnsupportedDataTypeException;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Extension of ConfigBase that allows the {@link ConfigOption} annotation to be used.
 *
 * @since 3.0.4
 */
public abstract class ConfigBaseNew extends ConfigBase {
    public ConfigBaseNew(String modId) {
        super(modId);
    }

    public abstract I18nHelper i18n();

    public abstract LogHelper log();

    @Override
    public void load() {
        readAnnotatedFields();
    }

    private void readAnnotatedFields() {
        readAnnotatedFields(this.getClass());
    }

    private void readAnnotatedFields(Class<?> clazz) {
        for (Class<?> innerClass : clazz.getDeclaredClasses()) {
            readAnnotatedFields(innerClass);
        }

        for (Field field : clazz.getDeclaredFields()) {
            ConfigOption config = null;
            ConfigOption.Comment comment = null;
            ConfigOption.BooleanDefault booleanDefault = null;
            ConfigOption.RangeDouble rangeDouble = null;
            ConfigOption.RangeFloat rangeFloat = null;
            ConfigOption.RangeInt rangeInt = null;
            ConfigOption.RangeLong rangeLong = null;

            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof ConfigOption)
                    config = (ConfigOption) annotation;
                else if (annotation instanceof ConfigOption.Comment)
                    comment = (ConfigOption.Comment) annotation;
                else if (annotation instanceof ConfigOption.RangeDouble)
                    rangeDouble = (ConfigOption.RangeDouble) annotation;
                else if (annotation instanceof ConfigOption.RangeFloat)
                    rangeFloat = (ConfigOption.RangeFloat) annotation;
                else if (annotation instanceof ConfigOption.RangeInt)
                    rangeInt = (ConfigOption.RangeInt) annotation;
                else if (annotation instanceof ConfigOption.RangeLong)
                    rangeLong = (ConfigOption.RangeLong) annotation;
                else if (annotation instanceof ConfigOption.BooleanDefault)
                    booleanDefault = (ConfigOption.BooleanDefault) annotation;
            }

            if (config != null) {
                // Thought about localizing comments, but... maybe not the best idea
//                String commentTranslationKey = comment != null
//                        ? comment.value()
//                        : i18n().getKey("config", config.name().replaceAll("\\s", ""), "comment");
//                if (comment != null && !i18n().hasKey(commentTranslationKey)) {
//                    log().warn("Config option \"{}\" is missing its comment translation (key {})",
//                            config.name(), commentTranslationKey);
//                }
//                String commentTranslated = i18n().translate(commentTranslationKey);
                String commentTranslated = comment != null ? comment.value() : "";

                try {
                    if (field.getType() == int.class)
                        field.set(this, readInt(field, config, rangeInt, commentTranslated));
                    else if (field.getType() == float.class)
                        field.set(this, readFloat(field, config, rangeFloat, commentTranslated));
                    else if (field.getType() == double.class)
                        field.set(this, readDouble(field, config, rangeDouble, commentTranslated));
                    else if (field.getType() == long.class)
                        field.set(this, readLong(field, config, rangeLong, commentTranslated));
                    else if (field.getType() == boolean.class)
                        field.set(this, readBoolean(field, config, booleanDefault, commentTranslated));
                    else
                        throw new UnsupportedDataTypeException("Don't know how to load config for type " + field.getType() + "!");
                } catch (IllegalAccessException ex) {
                    log().error("Field \"{}\" is not accessible?", field.getName());
                    log().catching(ex);
                } catch (UnsupportedDataTypeException ex) {
                    log().error("Unknown field type");
                    log().catching(ex);
                }
            }
        }
    }

    private double readDouble(Field field, ConfigOption config, @Nullable ConfigOption.RangeDouble range, String comment) throws IllegalAccessException {
        double defaultValue = range != null ? range.value() : field.getDouble(this);
        if (range != null)
            return this.loadDouble(config.name(), config.category(), defaultValue, range.min(), range.max(), comment);
        else
            return this.loadDouble(config.name(), config.category(), defaultValue, comment);
    }

    private float readFloat(Field field, ConfigOption config, @Nullable ConfigOption.RangeFloat range, String comment) throws IllegalAccessException {
        float defaultValue = range != null ? range.value() : field.getFloat(this);
        if (range != null)
            return this.loadFloat(config.name(), config.category(), defaultValue, range.min(), range.max(), comment);
        else
            return this.loadFloat(config.name(), config.category(), defaultValue, comment);
    }

    private int readInt(Field field, ConfigOption config, @Nullable ConfigOption.RangeInt range, String comment) throws IllegalAccessException {
        int defaultValue = range != null ? range.value() : field.getInt(this);
        if (range != null)
            return this.loadInt(config.name(), config.category(), defaultValue, range.min(), range.max(), comment);
        else
            return this.loadInt(config.name(), config.category(), defaultValue, comment);
    }

    private long readLong(Field field, ConfigOption config, @Nullable ConfigOption.RangeLong range, String comment) throws IllegalAccessException {
        long defaultValue = range != null ? range.value() : field.getLong(this);
        if (range != null)
            return this.loadLong(config.name(), config.category(), defaultValue, range.min(), range.max(), comment);
        else
            return this.loadLong(config.name(), config.category(), defaultValue, comment);
    }

    private Object readBoolean(Field field, ConfigOption config, @Nullable ConfigOption.BooleanDefault booleanDefault, String comment) throws IllegalAccessException {
        boolean defaultValue = booleanDefault != null ? booleanDefault.value() : field.getBoolean(this);
        return loadBoolean(config.name(), config.category(), defaultValue, comment);
    }
}
