package com.example.reactive.configs.cache

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitSingle
import lombok.AllArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.*
import org.springframework.stereotype.Component

@Component
@AllArgsConstructor
class RedisUtils @Autowired constructor(
    private val redisTemplate: RedisTemplate<String, String>,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
) {
    companion object {
        val mapper by lazy { jacksonObjectMapper() }
    }

    suspend fun delete(key: String): Long {
        return reactiveRedisTemplate.deleteAndAwait(key)
    }

    suspend fun get(key: String): String?  {
        return reactiveRedisTemplate.opsForValue().getAndAwait(key)
    }

    suspend fun set(key: String, value: String): Boolean{
        return reactiveRedisTemplate.opsForValue().setAndAwait(key, value)
    }

    suspend fun opsForZSet(): ReactiveZSetOperations<String, String> {
        return reactiveRedisTemplate.opsForZSet()
    }

    suspend fun zCard(str: String): Long {
        return opsForZSet().sizeAndAwait(str)
    }

    suspend fun zRange(key: String, start: Long, end: Long): Any {
        return opsForZSet().range(key, Range.closed(start, end)).awaitSingle()
    }

    suspend fun getZRank(key: String, value: String): Long? {
        return opsForZSet().rankAndAwait(key, value)
    }

    suspend fun opsForHash() = reactiveRedisTemplate.opsForHash<String, String>()

    suspend fun hset(key: String, hkey: String, value: String): Boolean {
        return opsForHash().putAndAwait(key, hkey, value)
    }

    suspend fun hset(key: String, vararg keyValuePair: Pair<String, String>): Boolean {
        return hset(key, keyValuePair.toMap())
    }

    suspend fun hset(key: String, map: Map<String, String>): Boolean{
        return opsForHash().putAllAndAwait(key, map)
    }
    
    suspend fun hget(key: String, hkey: String): String? {
        return opsForHash().getAndAwait(key, hkey)
    }

    suspend fun hgetMulti(key: String, vararg hkey: String): List<String> {
        return opsForHash().multiGetAndAwait(key, *hkey).filterNotNull()
    }

    suspend fun hgetAll(key: String): List<String> {
        return opsForHash().valuesAsFlow(key).toList()
    }

    suspend fun hdeleteAll(key: String): Boolean {
        return opsForHash().deleteAndAwait(key)
    }
    suspend fun hdelete(key: String, vararg hkey: String): Long {
        return opsForHash().removeAndAwait(key, *hkey)
    }
}

inline fun <reified T> RedisUtils.jsonAsObject(json: String): T {
    return RedisUtils.mapper.readValue(json, T::class.java)
}

inline fun <reified T> RedisUtils.objectAsJson(obj: T): String {
    return RedisUtils.mapper.writeValueAsString(obj)
}

inline fun <reified T> RedisUtils.objectPairAsJsonPair(pair: Pair<String, T>): Pair<String, String>{
    return Pair(pair.first, objectAsJson(pair.second))
}

inline fun <reified T> RedisUtils.mapValueObjectAsJson(map: Map<String, T>): Map<String, String> {
    return map.map { objectPairAsJsonPair(it.toPair()) }.toMap()
}