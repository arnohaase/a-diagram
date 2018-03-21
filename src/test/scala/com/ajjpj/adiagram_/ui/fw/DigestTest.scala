package com.ajjpj.adiagram_.ui.fw

import javafx.beans.property.SimpleStringProperty

import org.scalatest.{FunSuite, Matchers}

/**
 * @author arno
 */
class DigestTest extends FunSuite with Matchers {
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

  test("watch") {
    val digest = new Digest()

    var a = 1
    var changeCounter = 0

    val onChange = (i: Int) => {changeCounter += 1}

    digest.watch(a, onChange)

    a=2
    changeCounter should equal (0)

    digest.execute {
      a=3
    }
    changeCounter should equal (1)

    digest.execute {
    }
    changeCounter should equal (1)
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
