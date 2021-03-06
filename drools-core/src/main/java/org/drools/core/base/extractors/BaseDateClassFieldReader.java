/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.base.extractors;

import org.drools.core.RuntimeDroolsException;
import org.drools.core.base.BaseClassFieldReader;
import org.drools.core.base.ValueType;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Date;

public abstract class BaseDateClassFieldReader extends BaseClassFieldReader {

    private static final long serialVersionUID = 510l;

    public BaseDateClassFieldReader() {
        
    }
    
    /**
     * This constructor is not supposed to be used from outside the class hirarchy
     * 
     * @param index
     * @param fieldType
     * @param valueType
     */
    protected BaseDateClassFieldReader(final int index,
                                           final Class fieldType,
                                           final ValueType valueType) {
        super( index,
               fieldType,
               valueType );
    }

    public Object getValue(InternalWorkingMemory workingMemory, final Object object) {
        return object;
    }

    public boolean getBooleanValue(InternalWorkingMemory workingMemory, final Object object) {
        throw new RuntimeDroolsException( "Conversion to boolean not supported from Date" );
    }

    public byte getByteValue(InternalWorkingMemory workingMemory, final Object object) {
        return (byte) getLongValue( workingMemory, object );

    }

    public char getCharValue(InternalWorkingMemory workingMemory, final Object object) {
        return (char) getLongValue( workingMemory, object );
    }

    public double getDoubleValue(InternalWorkingMemory workingMemory, final Object object) {
        return getLongValue( workingMemory, object );
    }

    public float getFloatValue(InternalWorkingMemory workingMemory, final Object object) {
        return getLongValue( workingMemory, object );
    }

    public int getIntValue(InternalWorkingMemory workingMemory, final Object object) {
        return (int) getLongValue( workingMemory, object );
    }

    public long getLongValue(InternalWorkingMemory workingMemory, Object object) {
        return ((Date)getValue(workingMemory, object)).getTime();
    }

    public short getShortValue(InternalWorkingMemory workingMemory, final Object object) {
        return (short) getLongValue( workingMemory, object );
    }

    public boolean isNullValue(InternalWorkingMemory workingMemory, final Object object) {
        if ( object == null ) {
            return true;
        } else {
            return getValue( workingMemory,
                             object ) == null;
        }
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod( "getValue",
                                                      new Class[]{InternalWorkingMemory.class, Object.class} );
        } catch ( final Exception e ) {
            throw new RuntimeDroolsException( "This is a bug. Please report to development team: " + e.getMessage(),
                                              e );
        }
    }

    public String getNativeReadMethodName() {
        return "getValue";
    }

    public int getHashCode(InternalWorkingMemory workingMemory, final Object object) {        
        final long temp = getLongValue( workingMemory, object );
        return (int) (temp ^ (temp >>> 32));
    }

}
