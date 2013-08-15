package com.ajjpj.adiagram.snippets

/**
 * @author arno
 */
object Traits extends App {
  println ("Hi Arno")

  val v = new X with Adder

  println (v.a(5))
}


trait TT {
  def a(i: Int): Int
}

trait Adder extends TT {
  abstract override def a(i: Int) = super.a(i)+1
}

class X extends TT {
  def a(i:Int) = i*2
}