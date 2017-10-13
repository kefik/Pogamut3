/*
 * Copyright (C) 2013 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.cuni.amis.pogamut.unreal.t3dgenerator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Interface to mark enums as representable in UT3.
 * Unless {@link EnumValue} is specified for enum fields, their name is converted from
 * Java enum constant form (OVERWRITE_ALL) to T3D format (OverwriteAll). This annotation's
 * value is prepended to the field name, enluess {@link EnumValue#raw() } is set.
 * @author Martin Cerny
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})    
public @interface UnrealEnum {
    /**
     * The prefix used for enum expression. Prepended to strings for all enum values
     * @return 
     */
    String value();
}
