package io.zig.data.test

import org.scalatest.FunSuite
import io.zig.data.{ EdnToFressian, FressianToEdn }

class ConversionSuite extends FunSuite {
  
  test ("can convert integers") {
    pending
  }
  
  test ("can convert longs") {
    pending
  }
  
  test ("can convert BigIntegers") {
    pending
  }
  
  test ("can convert decimals") {
    pending
  }
  
  test ("can convert BigDecimals") {
    pending
  }
  
  test ("list written properly with ( and )") {
    pending
  }
  
  test ("can convert symbols") {
    pending
  }
  
  test ("can convert keywords") {
    pending
  }
  
  test ("can convert URIs") {
    pending
  }

  test ("conversion") {
    var bytes: Array[Byte] = EdnToFressian.convert("1")
    println(FressianToEdn.convert(bytes))
    bytes = EdnToFressian.convert("(1 2 3)")
    println(FressianToEdn.convert(bytes))
    bytes = EdnToFressian.convert("[1 2 3]")
    println(FressianToEdn.convert(bytes))
    bytes = EdnToFressian.convert("#{1 2 3}")
    println(FressianToEdn.convert(bytes))
    bytes = EdnToFressian.convert("{1 2 3 4}")
    println(FressianToEdn.convert(bytes))
  }

}

