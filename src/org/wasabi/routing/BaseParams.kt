package org.wasabi.routing

import java.util.HashMap
import java.util.ArrayList
import java.util.Collections

public open class BaseParams {

    val _map = HashMap<String, String>()
    val _list = ArrayList<String>()

    fun get(name : String) : String? {
        return _map[name]
    }

    fun set(name : String, value : String) {
        if (!_map.containsKey(name)) // add it to the unnamed list as well, if it's not already there
            append(value)
        _map[name] = value
    }

    fun get(i : Int) : String? {
        return _list.get(i)
    }

    fun set(i : Int, value : String) {
        _list.set(i, value)
    }

    fun append(value : String) {
        _list.add(value)
    }

    fun size() : Int {
        return _list.size()
    }

    fun getHash(name : String) : HashMap<String,String> {
        val map = HashMap<String,String>()
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

    // TODO: Shit!
    fun copyFrom(params: BaseParams) {
        _map.clear()
        _map.putAll(params._map)
        _list.clear()
        for (i in params._list) {
            _list.add(i)
        }
    }

}