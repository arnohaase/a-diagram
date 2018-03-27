package com.ajjpj.adiagram_

/**
 * @author arno
 */
package object util {
  def ignoringException (code: => Unit) = try {code} catch {case _: Exception => }
}
