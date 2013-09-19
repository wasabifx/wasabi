package org.wasabi.http

public enum class CacheControl {

    NoCache {
        fun toString(): String {
            return "no-cache"
        }
    }
    Private {
        fun toString(): String {
            return "private"
        }
    }
    Public {
        fun toString(): String {
            return "public"
        }
    }
    NoStore {
        fun toString(): String {
            return "no-store"
        }
    }

}