package org.wasabi.http

public enum class CacheControl {

    NoCache {
        override fun toString(): String {
            return "no-cache"
        }
    },
    Private {
        override fun toString(): String {
            return "private"
        }
    },
    Public {
        override fun toString(): String {
            return "public"
        }
    },
    NoStore {
        override fun toString(): String {
            return "no-store"
        }
    }

}