package com.github.mercurydb.queryutils;

public class TableID<T> {
    /**
     * This value may wrap around, but we have built in machinery to prevent
     * this from becoming a problem. We allow the creation of Long.MAX_VALUE
     * names. We preserve all of the negative numbers for aliases so that the
     * usual machinery of creating aliases can proceed as usual even if all
     * of the names have been created.
     * <p/>
     * Note: This implies that the maximum number of classes which can be used
     * under MercuryDB is Long.MAX_VALUE classes. We believe this is an
     * acceptable limit as the amount of classes a Java program can have in
     * total is probably smaller than this number, based on expected
     * limitations of the compiler and physical memory of a modern machine
     * [CITATION NEEDED].
     */
    private static long counter = 0;

    /**
     * For efficiency reasons in accounting for unique TableIDs throughout a
     * client codebase, we have elected to have two regions for IDs (those
     * which are permanent [names], and those which are temporary [aliases]).
     * For this reason, names cannot be created after any aliases have been created.
     * <p/>
     * This distinction allows us to deal with wrap around of the alias IDs.
     * On wrap around, we will set the value back to firstAliasID.
     * <p/>
     * This flag will be used to prevent the creation of names after the first
     * alias has been created. When the first alias is created, set this to false,
     * and never allow any names to be created anymore (instead throw an exception).
     */
    private static boolean allowNames = true;

    /**
     * Initially 0, this value will be updated when the first alias is created
     * and never changed. Whenever the counter wraps around, it will be reset
     * to this value.
     */
    private static long firstAliasID = 0;

    /**
     * This is the ID value for this instance of a TableID. If this TableID is a
     * name, then it will be globally unique. If this TableID is an alias, it is
     * intended to be unique within an HgTuple. This assumption is safe at least
     * until you have Long.MAX_VALUE entries the HgTuple, and this assumes that
     * you have used all Long.MAX_VALUE names, so it may be even less restrictive.
     */
    public final long id;

    /**
     * Create a new TableID with the current value of the static counter, and
     * increment the counter for the next instance.
     */
    private TableID() {
        id = counter++;
    }

    /**
     * Compare a this TableID with another generic TableID, determine whether they are
     * equal by trying to match their values.
     *
     * @param other A generic TableID against which to compare.
     * @return Whether the id values are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof TableID) {
            return this.id == ((TableID)other).id;
        }
        return false;
    }

    /**
     * Utility method to cast the objects retrieved from an HgTuple back to
     * the appropriate type which is captured by the generic type, T of this
     * instance of TableID.
     *
     * @param o The input object to be cast.
     * @return The object casted to its original type.
     */
    @SuppressWarnings("unchecked")
    public T castObject(Object o) {
        // TODO verify is this actually used (or verify it is not needed, and then remove it)
        return (T) o;
    }

    /**
     * Create a name.
     *
     * This is intended mainly for use by the Table classes generated by MercuryDB.
     * Users can create names as well, but only if they do so before creating any
     * aliases. If they violate this contract, this method will throw an
     * OutOfNamesException.
     *
     * This method will throw OutOfNamesException if Long.MAX_VALUE names have
     * already been created. Users can't do anything about this Exception so it is
     * implemented as a RuntimeException and not added to the throws clause here.
     *
     * This method will throw NameCreationDisallowedException if called after
     * createAlias has been called.
     *
     * Having both of these Exceptions as RuntimeExceptions makes this method
     * more practical to use because it can be used to initialize static class fields.
     *
     * @param <T> The type to represent.
     * @return a new TableID name representing the type T.
     */
    public static <T> TableID<T> createName() {
        if (!allowNames) {
            throw new NameCreationDisallowedException();
        }

        if (counter == Long.MIN_VALUE) {
            throw new OutOfNamesException();
        }

        return new TableID<>();
    }

    /**
     * Create an alias.
     *
     * @param <T> The type to represent.
     * @return a new TableID alias representing the type T.
     */
    public static <T> TableID<T> createAlias() {
        if (allowNames) {
            sealNames();
        }

        // when we wrap all the way back around to 0, we need to skip over the permanent names.
        if (counter == 0) {
            counter = firstAliasID;
        }

        return new TableID<>();
    }

    /**
     * Thrown when a total of Long.MAX_VALUE names have been created, and
     * we have thus exhausted all available names.
     *
     * This is a RuntimeException because a user catching this exception would have
     * very little practical meaning. The only fallback would be not to create as
     * many names, which is exactly what needs to be done in the first place, and
     * needs to be fixed statically, not dynamically after the exception occurs.
     */
    public static class OutOfNamesException extends RuntimeException {
    }

    /**
     * This is thrown when a user attempts to create a name after already
     * creating one or more aliases.
     *
     * This is a RuntimeException because a user catching this exception would have
     * very little practical meaning. The only fallback would be use create an alias
     * instead of a name, and if that is acceptable, it should be done in any case.
     * Instead of using a try/catch block, this can be fixed by correcting the code.
     */
    public static class NameCreationDisallowedException extends RuntimeException {
    }

    /**
     * Tells us not to allow any more names to be created from this point forward.
     * Set the firstAliasID to the current value of counter, as this value
     * will be the value of the first alias, and is the smallest value
     * guaranteed to never be reserved by a permanent name.
     */
    private static void sealNames() {
        allowNames = false;
        firstAliasID = counter;
    }

    @Override
    public String toString() {
        return String.format("id: %d", id);
    }
}

// TODO remove the test code, below

class TableIdTestX {
}

@SuppressWarnings("unused")
class TableIdTestCase {
    void foo() {
        try {
            TableID<TableIdTestX> xName = TableID.createName();
            TableIdTestX x = new TableIdTestX();
            Object o = x;
            TableIdTestX xx = xName.castObject(o); // castObject MIGHT not be necessary but lets keep it around just in case.
            TableIdTestX xx1 = (TableIdTestX) o;
        } catch (TableID.OutOfNamesException e) {
            e.printStackTrace();
        }
    }
}