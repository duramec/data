package io.zig.data.test

import org.scalatest.FunSuite
import io.zig.data.edn.EDN
import io.zig.data.fressian.Fressian;

class ConversionSuite extends FunSuite {

  def backAndForth(s: String): String = {
    val obj = EDN.toObject(s)
    val bytes = Fressian.toBytes(obj)
    val newObj = Fressian.toObject(bytes)
    EDN.toFormat(newObj)
  }

  def roundTrip(s: String) {
    expectResult (s) {
      backAndForth(s)
    }
  }

  def roundTripAlternate(s: String, alt: String) {
    expectResult (alt) {
      backAndForth(s)
    }
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
    roundTrip ("""#regex"[a-z0-9]+"""")
    val s = """#regex"[a-z0-9\\\\]+""""
    val sStripped = s.replace("""\\""", """\""");
    roundTripAlternate (s, sStripped)
  }

  test ("can convert a sequence of un-enclosed values") {
    roundTrip("1 2")
    roundTrip("1 2 3")
    roundTrip("1 2 3 nil")
    roundTrip("{:a 1}[b 2](c 3)#{d}1 2 3")
  }

}

