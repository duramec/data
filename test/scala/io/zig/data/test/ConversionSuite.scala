package io.zig.data.test

import org.scalatest.FunSuite
import io.zig.data.Convert

class ConversionSuite extends FunSuite {

  def roundTrip(s: String) {
    val bytes = Convert.toBytes(s)
    expectResult (s) { Convert.toEDN(bytes) }
  }

  def roundTrip(s: String, alt: String) {
    val bytes = Convert.toBytes(s)
    expectResult (alt) { Convert.toEDN(bytes) }
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

  test ("can convert symbols") {
    roundTrip ("abc")
    roundTrip ("abc/def")
    roundTrip ("abc-def/ghi-zyx")
  }

  test ("can convert keywords") {
    roundTrip (":keyword")
    roundTrip (":extended-longer-keyword-test")
  }

  test ("can convert URIs") {
    roundTrip ("#uri\"http://www.example.com\"")
  }

  test ("can convert characters") {
    roundTrip ("""\a""")
    roundTrip ("""\A""")
    // \n, \r, \t are character literals and are not escaped.
    roundTrip ("""\newline""")
    roundTrip ("""\return""")
    roundTrip ("""\tab""")
    roundTrip ("""\space""")
  }

  test ("can convert boolean") {
    roundTrip ("true")
    roundTrip ("false")
  }

  test ("can convert string") {
    roundTrip ("\"\"")
    roundTrip (""""this-is-a-string"""")
  }

  test ("can convert nil") {
    roundTrip ("nil")
  }

  test ("can convert Instant") {
    roundTrip ("#inst\"2002-10-02T15:00:00.000-00:00\"")
  }

  test ("can convert UUID") {
    roundTrip ("#uuid\"f4e9c4c4-d5d3-11e2-bdc4-00264a106cf2\"")
  }

  test ("can convert Regex") {
    //roundTrip ("""#"[a-z0-9]+"""")
    pending
  }

}

