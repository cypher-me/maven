package org.apache.maven.toolchain;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.toolchain.model.ToolchainModel;
import org.codehaus.plexus.logging.Logger;

/**
 *
 * @author mkleint
 * @since 2.0.9
 */
public abstract class DefaultToolchain
    implements Toolchain, ToolchainPrivate
{

    private String type;

    private Map<String, RequirementMatcher> provides = new HashMap<String, RequirementMatcher>();

    public static final String KEY_TYPE = "type"; //NOI18N

    private ToolchainModel model;

    private Logger logger;

    protected DefaultToolchain( ToolchainModel model, Logger logger )
    {
        this.model = model;

        this.logger = logger;
    }

    protected DefaultToolchain( ToolchainModel model, String type, Logger logger )
    {
        this( model, logger );
        this.type = type;
    }

    @Override
    public final String getType()
    {
        return type != null ? type : model.getType();
    }

    @Override
    public final ToolchainModel getModel()
    {
        return model;
    }

    public final void addProvideToken( String type, RequirementMatcher matcher )
    {
        provides.put( type, matcher );
    }

    @Override
    public boolean matchesRequirements( Map<String, String> requirements )
    {
        for ( Map.Entry<String, String> requirement : requirements.entrySet() )
        {
            String key = requirement.getKey();

            RequirementMatcher matcher = provides.get( key );

            if ( matcher == null )
            {
                getLog().debug( "Toolchain " + this + " is missing required property: " + key );
                return false;
            }
            if ( !matcher.matches( requirement.getValue() ) )
            {
                getLog().debug( "Toolchain " + this + " doesn't match required property: " + key );
                return false;
            }
        }
        return true;
    }

    protected Logger getLog()
    {
        return logger;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
        {
            return false;
        }
        
        if ( this == obj )
        {
            return true;
        }
    
        if ( !( obj instanceof DefaultToolchain ) )
        {
            return false;
        }
    
        DefaultToolchain other = (DefaultToolchain) obj;

        if ( type == null ? other.type != null : !type.equals( other.type ) )
        {
            return false;
        }

        Properties thisProvides = this.getModel().getProvides();
        Properties otherProvides = other.getModel().getProvides();
    
        if ( thisProvides == null ? otherProvides != null : !thisProvides.equals( otherProvides ) )
        {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode()
    {
        int hashCode = ( type == null ) ? 0 : type.hashCode();
        
        if ( this.getModel().getProvides() != null )
        {
            hashCode = 31 * hashCode + this.getModel().getProvides().hashCode();
        }
        return hashCode;
    }
}
