package org.wasabi.routing

import java.util.HashMap
import java.util.ArrayList
import java.util.Collections

public open class BaseParams<Value> {

    private val _map = HashMap<String, Value>()
    private val _list = ArrayList<Value>()

    fun get(name : String) : Value? {
        return _map[name]
    }

    fun set(name : String, value : Value) {
        if (!_map.containsKey(name)) // add it to the unnamed list as well, if it's not already there
            append(value)
        _map[name] = value
    }

    fun get(i : Int) : Value? {
        return _list.get(i)
    }

    fun set(i : Int, value : Value) {
        _list.set(i, value)
    }

    fun append(value : Value) {
        _list.add(value)
    }

    fun size() : Int {
        return _list.size()
    }

    fun getHash(name : String) : HashMap<String,Value> {
        val map = HashMap<String,Value>()
        val prefix = name + "["
        for (key in _map.keySet()) {
            if (key.startsWith(prefix)) {
                var subkey = key.replace(prefix, "")
                subkey = subkey.substring(0, subkey.length()-1)
                val value = _map[key]!!
                map[subkey] = value
            }
        }
        return map
    }



}