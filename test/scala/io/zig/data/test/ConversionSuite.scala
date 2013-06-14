package io.zig.data.test

import org.scalatest.FunSuite
import io.zig.data.{ EdnToFressian, FressianToEdn }

class ConversionSuite extends FunSuite {

  def roundTrip(s: String) {
    val bytes = EdnToFressian.convert(s)
    expectResult (s) { FressianToEdn.convert(bytes) }
  }

  def roundTrip(s: String, alt: String) {
    val bytes = EdnToFressian.convert(s)
    expectResult (alt) { FressianToEdn.convert(bytes) }
  }

  test ("can convert integers") {
    roundTrip ("0")
    roundTrip ("12345678")
    roundTrip ("-12345678")
  }

  test ("can convert longs") {
    roundTrip ("300000000000")
    roundTrip ("-300000000000")
  }

  test ("can convert BigIntegers") {
    roundTrip ("0N")
    roundTrip ("3000000000000000000000000000N")
    roundTrip ("-3000000000000000000000000000N")
  }

  test ("can convert decimals") {
    roundTrip ("0.0")
    roundTrip ("6.2831853071")
    roundTrip ("-6.2831853071")
  }

  test ("can convert BigDecimals") {
    roundTrip ("1.00000000000000000000000000M")
    roundTrip ("-1.00000000000000000000000000M")
  }

  test ("can convert lists") {
    roundTrip ("()")
    roundTrip ("(1 2 3 4)")
    roundTrip ("(1(2 3)4)")
    roundTrip ("(1(2(3(4))))")
  }

  test ("can convert vectors") {
    roundTrip ("[]")
    roundTrip ("[1 2 3 4]")
    roundTrip ("[1[2 3]4]")
    roundTrip ("[1[2[3[4]]]]")
  }

  test ("can convert sets") {
    roundTrip ("#{}")
    roundTrip ("#{1 2 3 4}")
    roundTrip ("#{1#{2 3 4}}")
    roundTrip ("#{1#{2#{3#{4}}}}")
  }

  test ("can convert maps") {
    roundTrip ("{}")
    roundTrip ("{1 2}")
    roundTrip ("{1{2 3}}")
    roundTrip ("{1 2{2 3}4}")
  }

  /*
  test ("can convert symbols") {
    pending
  }

  test ("can convert keywords") {
    pending
  }

  test ("can convert URIs") {
    pending
  }

  test ("can convert Characters") {
    pending
  }

  test ("can convert Boolean") {
    pending
  }

  test ("can convert String") {
    pending
  }

  test ("can convert :nil") {
    pending
  }

  test ("can convert Instant") {
    pending
  }

  test ("can convert UUID") {
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
  }*/

}
