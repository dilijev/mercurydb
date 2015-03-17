package org.mercurydb.queryutils;

import java.util.Map;
import java.util.Set;

/**
 * This class defines two accessors for data
 * in a JoinStream. Note that JoinStream implements
 * JoinField. It provides a way for JoinStreams to
 * ask other JoinStreams how to extract a join key given
 * an instance of their class owner.
 *
 */
public interface FieldExtractable {
    /**
     * <p>
     * Returns the name of the class owner. Note
     * that the parameter to extractJoinKey should
     * always be of this type. Ex. For a JoinStream
     * from ATable, this should return A.class
     * </p>
     * // TODO extractJoinKey from which type?
     *
     * @return the Class of the class owner. // TODO what does class owner mean?
     */
    public Class<?> getContainerClass();

    /**
     * Extracts a join key value from an instance of the
     * type returned by getClassOwner()
     *
     * @param o The object to extract a value from.
     * @return The value of the field being extracted.
     */
    public Object extractField(Object o);

    /**
     * @return true if field is indexed, false otherwise
     */
    public boolean isIndexed();

    /**
     * @return map associated with index this field, or null if no index is present
     */
    public Map<Object, Set<Object>> getIndex();

    /**
     * Returns the id generated by the HgDB bootstrap process. This
     * is the ID in the tables. ID should be packaged into
     * produced FieldExtractable instances in this way.
     *
     * @return id generated by the HgDB bootstrap process.
     */
    public TableID<?> getTableId();
}
