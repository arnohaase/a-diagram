package com.ajjpj.adiagram

/**
 * @author arno
 */
package object util {
  def ignoringException (code: => Unit) = try {code} catch {case _: Exception => }
}
