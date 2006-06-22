/*
 * Copyright 2004 The Apache Software Foundation.
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
package javax.faces.model;

import javax.faces.FacesException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * see Javadoc of <a href="http://java.sun.com/j2ee/javaserverfaces/1.1_01/docs/api/index.html">JSF Specification</a>
 *
 * @author Thomas Spiegl (latest modification by $Author$)
 * @author Martin Marinschek
 * @version $Revision$ $Date$
 */
public class ResultSetDataModel extends DataModel
{
    // FIELDS

    private int _currentIndex = -1;

    /**
     * The ResultSet being wrapped by this DataModel.
     */
    private ResultSet _resultSet = null;

    /**
     * The MetaData of the ResultSet being wrapped by this DataModel.
     */
    private ResultSetMetaData _resultSetMetadata = null;


    /**
     *  Indicator for an updated row at the current position.
     */
    private boolean _currentRowUpdated = false;

    // CONSTRUCTORS
    public ResultSetDataModel()
    {
        this(null);
    }

    public ResultSetDataModel(ResultSet resultSet)
    {

        super();
        setWrappedData(resultSet);

    }

    /** We don't know how many rows the result set has without scrolling
     * through the whole thing.
     */
    public int getRowCount()
    {
        return -1;
    }

    /** Get the actual data of this row
     *  wrapped into a map.
     *  The specification is very strict about what has to be
     *  returned from here, so check the spec before
     *  modifying anything here.
     */
    public Object getRowData()
    {
        if (_resultSet == null)
        {
	        return null;
        }
        else if (!isRowAvailable())
        {
            throw new IllegalArgumentException(
                    "the requested row is not available in the ResultSet - you have scrolled beyond the end.");
        }

        try
        {
            return new WrapResultSetMap(String.CASE_INSENSITIVE_ORDER);
        }
        catch (SQLException e)
        {
            throw new FacesException(e);
        }
    }

    public int getRowIndex()
    {
        return _currentIndex;
    }

    public Object getWrappedData()
    {
        return _resultSet;
    }

    public boolean isRowAvailable()
    {
        if (_resultSet == null)
        {
	        return false;
        }
        else if (_currentIndex < 0)
        {
            return false;
        }

        try
        {
            return _resultSet.absolute(_currentIndex + 1);
        }
        catch (SQLException e)
        {
            throw new FacesException(e);
        }
    }

    public void setRowIndex(int rowIndex)
    {
        if (rowIndex < -1)
        {
            throw new IllegalArgumentException(
                    "you cannot set the rowIndex to anything less than 0");
        }

        // Handle the case of an updated row
        if (_currentRowUpdated && _resultSet != null)
        {
            try
            {
                if (!_resultSet.rowDeleted())
                    _resultSet.updateRow();

                setCurrentRowUpdated(false);
            }
            catch (SQLException e)
            {
                throw new FacesException(e);
            }
        }

        int old = _currentIndex;
        _currentIndex = rowIndex;

        //if no underlying data has been set, the listeners
        //need not be notified
        if (_resultSet == null)
            return;

        //Notify all listeners of the upated row
        DataModelListener [] listeners = getDataModelListeners();

        if ((old != _currentIndex) && (listeners != null))
        {
            Object rowData = null;

            if (isRowAvailable())
            {
                rowData = getRowData();
            }

            DataModelEvent event =
                new DataModelEvent(this, _currentIndex, rowData);

            int n = listeners.length;

            for (int i = 0; i < n; i++)
            {
                if (listeners[i]!=null)
                {
                    listeners[i].rowSelected(event);
                }
            }
        }
    }

    public void setWrappedData(Object data)
    {
        if (data == null)
        {
            _resultSetMetadata = null;
            _resultSet = null;
            setRowIndex(-1);
        }
        else
        {
            _resultSetMetadata = null;
            _resultSet = (ResultSet) data;
            _currentIndex = -1;
            setRowIndex(0);
        }
    }

    private ResultSetMetaData getResultSetMetadata()
    {
        if (_resultSetMetadata == null)
        {
            try
            {
                _resultSetMetadata = _resultSet.getMetaData();
            }
            catch (SQLException e)
            {
                throw new FacesException(e);
            }
        }

        return _resultSetMetadata;
    }

    private void setCurrentRowUpdated(boolean currentRowUpdated)
    {
        _currentRowUpdated = currentRowUpdated;
    }

    /* A map wrapping the result set and calling
    * the corresponding operations on the result set,
    * first setting the correct row index.
    */
    private class WrapResultSetMap extends TreeMap
    {
        private static final long serialVersionUID = -4321143404567038922L;
        private int _currentIndex;

        public WrapResultSetMap(Comparator comparator) throws SQLException
        {
            super(comparator);

            _currentIndex = ResultSetDataModel.this._currentIndex;

            _resultSet.absolute(_currentIndex + 1);

            int columnCount = getResultSetMetadata().getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                super.put(getResultSetMetadata().getColumnName(i),
                          getResultSetMetadata().getColumnName(i));
            }
        }

        public void clear()
        {
            throw new UnsupportedOperationException(
                    "It is not allowed to remove from this map");
        }

        public boolean containsValue(Object value)
        {
            Set keys = keySet();
            for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
                Object object = get(iterator.next());
                if (object == null) {
                    return value == null;
                }
                if (object.equals(value)) {
                    return true;
                }

            }
            return false;
        }

        public Set entrySet()
        {
            return new WrapResultSetEntries(this);
        }

        public Object get(Object key)
        {
            if (!containsKey(key))
                return null;

            return basicGet(key);
        }


        private Object basicGet(Object key)
        {  //#################################################### remove
            try
            {
                _resultSet.absolute(_currentIndex + 1);

                return _resultSet.getObject((String) getUnderlyingKey(key));

            }
            catch (SQLException e)
            {
                throw new FacesException(e);
            }
        }


        public Set keySet()
        {
            return new WrapResultSetKeys(this);
        }

        public Object put(Object key, Object value)
        {
            if (!containsKey(key))
                throw new IllegalArgumentException(
                        "underlying result set does not provide this key");

            if (!(key instanceof String))
                throw new IllegalArgumentException(
                        "key must be of type 'String', is of type : "+(key==null?"null":key.getClass().getName()));

            try
            {
                _resultSet.absolute(_currentIndex + 1);

                Object oldValue = _resultSet.getObject((String) getUnderlyingKey(key));

                if(oldValue==null?value==null:oldValue.equals(value))
                    return oldValue;

                _resultSet.updateObject((String) getUnderlyingKey(key), value);

                setCurrentRowUpdated(true);

                return oldValue;
            }
            catch (SQLException e)
            {
                throw new FacesException(e);
            }
        }

        public void putAll(Map map)
        {
            for (Iterator i = map.entrySet().iterator(); i.hasNext(); )
            {
                Map.Entry entry = (Map.Entry) i.next();
                put(entry.getKey(), entry.getValue());
            }
        }

        public Object remove(Object key)
        {
            throw new UnsupportedOperationException(
                    "It is not allowed to remove entries from this set.");
        }

        public Collection values()
        {
            return new WrapResultSetValues(this);
        }

        Object getUnderlyingKey(Object key)
        {
            return super.get(key);
        }

        Iterator getUnderlyingKeys()
        {
            return super.keySet().iterator();
        }

    }

    private static class WrapResultSetEntries extends AbstractSet
    {
        private WrapResultSetMap _wrapMap;

        public WrapResultSetEntries(WrapResultSetMap wrapMap)
        {
            _wrapMap = wrapMap;
        }

        public boolean add(Object o)
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to add to this set");
        }

        public boolean addAll(Collection c)
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to add to this set");
        }

        public void clear()
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to remove from this set"
            );
        }

        public boolean contains(Object o)
        {
            if (o == null)
                throw new NullPointerException();
            if (!(o instanceof Map.Entry))
                return false;

            Map.Entry e = (Map.Entry) o;
            Object key = e.getKey();

            if (!_wrapMap.containsKey(key))
                return false;

            Object value = e.getValue();
            Object cmpValue = _wrapMap.get(key);

            return value==null?cmpValue==null:value.equals(cmpValue);
        }

        public boolean isEmpty()
        {
            return _wrapMap.isEmpty();
        }

        public Iterator iterator()
        {
            return new WrapResultSetEntriesIterator(_wrapMap);
        }

        public boolean remove(Object o)
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to remove from this set");
        }

        public boolean removeAll(Collection c)
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to remove from this set");
        }

        public boolean retainAll(Collection c)
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to remove from this set");
        }

        public int size()
        {
            return _wrapMap.size();
        }
    }


    private static class WrapResultSetEntriesIterator implements Iterator
    {

        private WrapResultSetMap _wrapMap = null;
        private Iterator _keyIterator = null;

        public WrapResultSetEntriesIterator(WrapResultSetMap wrapMap)
        {
            _wrapMap = wrapMap;
            _keyIterator = _wrapMap.keySet().iterator();
        }

        public boolean hasNext()
        {
            return _keyIterator.hasNext();
        }

        public Object next()
        {
            return new WrapResultSetEntry(_wrapMap, _keyIterator.next());
        }

        public void remove()
        {
            throw new UnsupportedOperationException(
                    "It is not allowed to remove from this iterator"
            );
        }

    }

    private static class WrapResultSetEntry implements Map.Entry {

        private WrapResultSetMap _wrapMap;
        private Object _entryKey;

        public WrapResultSetEntry(WrapResultSetMap wrapMap, Object entryKey)
        {
            _wrapMap = wrapMap;
            _entryKey = entryKey;
        }


        public boolean equals(Object o)
        {
            if (o == null)
                return false;

            if (!(o instanceof Map.Entry))
                return false;

            Map.Entry cmpEntry = (Map.Entry) o;

            if(_entryKey ==null?cmpEntry.getKey()!=null:
                    !_entryKey.equals(cmpEntry.getKey()))
                return false;

            Object value = _wrapMap.get(_entryKey);
            Object cmpValue = cmpEntry.getValue();

            return value==null?cmpValue!=null:value.equals(cmpValue);
        }

        public Object getKey()
        {
            return _entryKey;
        }

        public Object getValue()
        {
            return _wrapMap.get(_entryKey);
        }

        public int hashCode()
        {
            int result;
            result = (_entryKey != null ? _entryKey.hashCode() : 0);
            result = 29 * result + (_wrapMap.get(_entryKey) != null ?
                    _wrapMap.get(_entryKey).hashCode() : 0);
            return result;
        }

        public Object setValue(Object value)
        {
            Object oldValue = _wrapMap.get(_entryKey);
            _wrapMap.put(_entryKey, value);
            return oldValue;
        }
    }

    private static class WrapResultSetKeys extends AbstractSet
    {
        private WrapResultSetMap _wrapMap;

        public WrapResultSetKeys(WrapResultSetMap wrapMap) {
            _wrapMap = wrapMap;
        }

        public boolean add(Object o)
        {
            throw new UnsupportedOperationException(
                    "It is not allowed to add to this set");
        }

        public boolean addAll(Collection c)
        {
            throw new UnsupportedOperationException(
                    "It is not allowed to add to this set");
        }

        public void clear()
        {
            throw new UnsupportedOperationException(
                    "It is not allowed to remove from this set"
            );
        }

        public boolean contains(Object obj)
        {
            return _wrapMap.containsKey(obj);
        }

        public boolean isEmpty()
        {
            return _wrapMap.isEmpty();
        }

        public Iterator iterator()
        {
            return new WrapResultSetKeysIterator(_wrapMap);
        }

        public boolean remove(Object o)
        {
            throw new UnsupportedOperationException(
                    "It is not allowed to remove from this set");
        }

        public boolean removeAll(Collection c)
        {
            throw new UnsupportedOperationException(
                    "It is not allowed to remove from this set");
        }

        public boolean retainAll(Collection c)
        {
            throw new UnsupportedOperationException(
                    "It is not allowed to remove from this set");
        }

        public int size()
        {
            return _wrapMap.size();
        }
    }

    private static class WrapResultSetKeysIterator implements Iterator
    {
        private Iterator _keyIterator = null;

        public WrapResultSetKeysIterator(WrapResultSetMap map)
        {
            _keyIterator = map.getUnderlyingKeys();
        }

        public boolean hasNext()
        {
            return _keyIterator.hasNext();
        }

        public Object next()
        {
            return _keyIterator.next();
        }

        public void remove()
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to remove from this iterator");
        }

    }

    private static class WrapResultSetValues extends AbstractCollection
    {
        private WrapResultSetMap _wrapMap;

        public WrapResultSetValues(WrapResultSetMap wrapMap)
        {
            _wrapMap = wrapMap;
        }

        public boolean add(Object o)
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to add to this collection"
            );
        }

        public boolean addAll(Collection c)
        {
            throw new UnsupportedOperationException(
                "it is not allowed to add to this collection"
            );
        }

        public void clear()
        {
            throw new UnsupportedOperationException(
                "it is not allowed to remove from this collection"
            );
        }

        public boolean contains(Object value)
        {
            return _wrapMap.containsValue(value);
        }

        public Iterator iterator()
        {
            return new WrapResultSetValuesIterator(_wrapMap);
        }

        public boolean remove(Object o)
        {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection c)
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to remove from this collection");
        }

        public boolean retainAll(Collection c)
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to remove from this collection");
        }

        public int size()
        {
            return _wrapMap.size();
        }

    }


    private static class WrapResultSetValuesIterator implements Iterator
    {

        private WrapResultSetMap _wrapMap;
        private Iterator _keyIterator;

        public WrapResultSetValuesIterator(WrapResultSetMap wrapMap)
        {
            _wrapMap = wrapMap;
            _keyIterator = _wrapMap.keySet().iterator();
        }

        public boolean hasNext()
        {
            return _keyIterator.hasNext();
        }

        public Object next()
        {
            return _wrapMap.get(_keyIterator.next());
        }

        public void remove()
        {
            throw new UnsupportedOperationException(
                    "it is not allowed to remove from this map"
            );
        }

    }

}
