package com.ajjpj.adiagram.ui.fw

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import javafx.beans.property.SimpleStringProperty

/**
 * @author arno
 */
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
