package fi.tamk.tiko.jsonparser;

/**
 * @author Ville-Veikko Nieminen
 * @version 1.8
 * @since 2018-11-20
 */
interface MyList<T> {

    /** 
     * Appends the specified element to the end of this list.
     *
     * @param e Object to add to the JsonArray.
     */
    void add(T e);

    /** 
     * Removes all of the elements from this list.
     */
    void clear();

    /** 
     * Returns the element at the specified position in this list. 
     *
     * @param index Index for the data to return.
     * @return An Object from given index.
     */
    Object get(int index);

    /**
     * Checks if JsonArray is empty or not.
     *
     * @return True or false depending if JsonArray is empty or not.
     */
    boolean isEmpty();

    /**
     * Removes data from given index.
     *
     * @param index Index where data is ereased.
     * @return An Integer representing old value from given index.
     */
    Object remove(int index);

    /**
     * Removes Object from given index if found.
     *
     * @param o given Object to check if present
     * @return True or false depending if there is given object in the JsonArray.
     */
    boolean remove(T o);

    /**
     * Returns size of the JsonArray.
     *
     * @return An Integer representing size of the JsonArray.
     */
    int size();
}

/**
 * represents an JSON array.
 *
 * @author Ville-Veikko Nieminen
 * @version 1.8
 * @since 2018-11-20
 */
public class JsonArray<T> implements MyList<T> {
    T[] data;
    int threshold;
    public int size;
    String name;

    /**
     * Sets variables for JsonArray.
     *
     * @param name Name for the JsonArray.
     */
    public JsonArray(String name) {
        threshold = 5;
        data = (T[]) new Object[threshold];
        this.name = name;
    }

    /**
     * @see fi.tamk.tiko.jsonparser.MyList#add(Object)
     */
    public void add(T e) {
        if(size < data.length) {
            data[size] = e;
        } else {
            T[] arr = (T[]) new Object[data.length +threshold];
            System.arraycopy(data,0,arr,0,data.length);
            arr[size] = e;
            data = arr;
        }
        
        size++;

    }

    /**
     * @see fi.tamk.tiko.jsonparser.MyList#clear()
     */
    public void clear() {
        data = (T[]) new Object[threshold];
        size = 0;
    }

    /**
     * @see fi.tamk.tiko.jsonparser.MyList#get(int)
     */
    public Object get(int index) {
        return data[index];
    }

    /**
     * @see fi.tamk.tiko.jsonparser.MyList#isEmpty()
     */
    public boolean isEmpty() {
        if(size == 0) {
            return true;
        } else {
            return false;
        }
        
    }

    /**
     * @see fi.tamk.tiko.jsonparser.MyList#remove(int)
     */
    public Object remove(int index) {
        if(size<data.length-5) {
            T[] arr =(T[]) new Object[size];
            System.arraycopy(data,0,arr,0,size);
            data = arr;
        }
        Object oldValue = data[index];
        for(int i=index; i<size-1; i++) {
            data[i] = data[i+1];
        }
        size -= 1;
        return oldValue;
    }

    /**
     * @see fi.tamk.tiko.jsonparser.MyList#remove(Object)
     */
    public boolean remove(T o) {
        for(int i=0; i<size; i++) {
            if(data[i] == o) {
                remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * @see fi.tamk.tiko.jsonparser.MyList#size()
     */
    public int size() {
        return size;
    }

    /**
     * Returns data[] length.
     *
     * @return An Integer representing length of data[].
     */
    public int length() {
        return data.length;
    }

    /**
     * Returns contents from JsonArray in correct form for Json.
     *
     * @return String representing contents of JsonArray.
     */
    public String toString() {
        String s="    \""+this.name+"\""+":"+"[\n";
        for(int i=0; i<size; i++) {
            s+= "        "+data[i];
            if(i!=size-1) {
                s+=",\n";
            }
        }
        s+= "\n    ]" + "\n";
        return s;
    }

    /**
     * Returns name of JsonArray.
     *
     * @return Name of JsonArray.
     */
    public String getName() {
        return this.name;
    }
    
}
