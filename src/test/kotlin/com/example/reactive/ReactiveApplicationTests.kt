package com.example.reactive

import com.example.reactive.configs.cache.RedisUtils
import com.example.reactive.configs.cache.jsonAsObject
import com.example.reactive.configs.cache.mapValueObjectAsJson
import com.example.reactive.configs.cache.objectAsJson
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import kotlin.math.abs
import kotlin.random.Random


@SpringBootTest
class ReactiveApplicationTests {
    @Autowired
    lateinit var redisUtils: RedisUtils
    data class TestData1(val num: Int, val title: String, val date: Date)
    data class TestData2(val id: Int, val name: String, val child: List<TestChild>)
    data class TestChild(val value: String, val date: Date)
    val key = "RedisTestCase"
    val rand = Random(100000)

    fun getData1(): List<TestData1>{
        val datas = mutableListOf<TestData1>()
        repeat((0 until 10).count()) {
            val id = abs(rand.nextInt())
            datas.add(TestData1(rand.nextInt(), "TestTitle$id", Date()))
        }
        return datas
    }
    fun getData2(): List<TestData2>{
        val datas = mutableListOf<TestData2>()
        repeat((0 until 10).count()) {
            val id = abs(rand.nextInt())
            datas.add(
                TestData2(
                    rand.nextInt(), "TestName$id", listOf(
                        TestChild("TestValue${rand.nextInt()}", Date()),
                        TestChild("TestValue${rand.nextInt()}", Date()),
                        TestChild("TestValue${rand.nextInt()}", Date()),
                    )
                )
            )
        }
        return datas
    }
    @Test
    fun case1(){
        runBlocking {
            val datas = getData1()
            val case = key+1
            val data = datas[0]
            redisUtils.hset(case, data.title, redisUtils.objectAsJson(data))
            val readData = redisUtils.hget(case, data.title)?.let { redisUtils.jsonAsObject<TestData1>(it) }
            println(case)
            println("\tInputData   : $data")
            println("\tOutputData : $readData")
            redisUtils.hdeleteAll(case)
            Assertions.assertEquals(data, readData)
        }
    }

    @Test
    fun case2(){
        runBlocking {
            val datas = getData1()
            val case = key+2
            val items = datas.take(3)
            redisUtils.hset(case, redisUtils.mapValueObjectAsJson(items.associateBy { it.title }))

            val readData = redisUtils.hgetMulti(case, *items.map { it.title }.toTypedArray()).map { redisUtils.jsonAsObject<TestData1>(it) }
            println(case)
            println("\tInputData: $items")
            println("\tOutputData: $readData")
            redisUtils.hdeleteAll(case)
            Assertions.assertEquals(items, readData)
        }
    }

    @Test
    fun case3(){
        runBlocking {
            val datas: List<TestData2> = getData2()
            val case = key+3
            val items = datas.take(2)
            redisUtils.hset(case, redisUtils.mapValueObjectAsJson(items.associateBy { it.name }))

            val readData = redisUtils.hgetMulti(case, *items.map { it.name }.toTypedArray()).map { redisUtils.jsonAsObject<TestData2>(it) }
            println(case)
            println("\tInputData: $items")
            println("\tOutputData: $readData")
//            redisUtils.hdelete(case, *items.map { it.name }.toTypedArray())
            Assertions.assertEquals(items, readData)
        }
    }



}
