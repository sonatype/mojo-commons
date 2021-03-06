package org.sonatype.maven.mojo.settings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

/**
 * Utility for making deep copies of objects. Objects are first serialized and then deserialized. If an object is
 * encountered that cannot be serialized (or that references an object that cannot be serialized) an
 * {@link IllegalArgumentException} is thrown. This is not really a "clone" implementation, but is rather used to clone
 * classes generated by Modello for Maven settings, in case of Maven2. Since Maven3, the model classes are cloneable.
 */
class Clone
{
    /**
     * Returns a copy of the object, or null if the object cannot be serialized. The use case of this method is very
     * limited! Is used to copy "beans" that are used in Maven settings only!
     * 
     * @param orig the object to copy, must not be {@code null}.
     * @return the cloned/copied version of orig.
     * @throws IllegalArgumentException if the passed in object is not serializable or any other problem happens during
     *             copy.
     */
    public static <T> T copy( final T orig )
        throws IllegalArgumentException
    {
        T obj = tryClone( orig );
        if ( obj != null )
        {
            return obj;
        }
        try
        {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream( bos );
            out.writeObject( orig );
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream( new ByteArrayInputStream( bos.toByteArray() ) );
            obj = (T) in.readObject();
        }
        catch ( IOException e )
        {
            // will not happen, we do in-memory IO operations only
        }
        catch ( ClassNotFoundException cnfe )
        {
            // huh?
            throw new IllegalArgumentException( String.format( "Object of class %s is not copy-able!",
                orig.getClass().getName() ), cnfe );
        }
        return obj;
    }

    /**
     * Will try to use reflection and invoke {@code clone()} method (that exists in case if we are in Maven3). In case
     * of failure, it will remain silent and simply return {@code null}.
     * 
     * @param orig
     * @return
     * @throws IllegalArgumentException
     */
    public static <T> T tryClone( final T orig )
        throws IllegalArgumentException
    {
        try
        {
            final Method m = orig.getClass().getMethod( "clone" );
            return (T) m.invoke( orig );
        }
        catch ( Exception e )
        {
            return null;
        }
    }
}
