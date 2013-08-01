package com.ajjpj.adiagram.ui.fw

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import javafx.beans.property.SimpleStringProperty
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author arno
 */
@RunWith(classOf[JUnitRunner])
class DigestTest extends FunSuite with ShouldMatchers {
  test("bind target function") {
    val digest = new Digest()

    var source = "a"
    var target = "b"

    digest.bind((x: String) => target=x, source)

    target should be ("a")

    digest.execute {
      source = "x"
    }

    target should be ("x")
  }

  test("unbind target function") {
    val digest = new Digest()

    var source = "a"
    var target = "b"

    val setter = (x: String) => target=x
    digest.bind(setter, source)

    target should be ("a")

    intercept[IllegalArgumentException] {
      digest.unbind((x: String) => target=x) // in order to unbind, you must pass in the *same* function as to 'bind'!
    }

    digest.unbind(setter)
    digest.execute {
      source = "x"
    }

    target should be ("a")
  }

  test("bind property") {
    val digest = new Digest()

    val prop = new SimpleStringProperty()
    var value = "a"

    digest.bind(prop, value)
    prop.getValue should equal ("a")

    digest.execute {
      value = "b"
    }

    prop.getValue should equal ("b")
  }

  test("unbind property") {
    val digest = new Digest()

    val prop = new SimpleStringProperty()
    var value = "a"

    digest.bind(prop, value)
    prop.getValue should equal ("a")

    digest.unbind(prop)

    digest.execute {
      value = "b"
    }

    prop.getValue should equal ("a")
  }

  test("check if property isBound") {
    val digest = new Digest()
    val prop = new SimpleStringProperty()

    digest.isBound(prop) should be (false)

    var value = "a"
    digest.bind(prop, value)

    digest.isBound(prop) should be (true)
  }
}
