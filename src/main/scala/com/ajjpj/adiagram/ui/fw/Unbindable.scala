package com.ajjpj.adiagram.ui.fw

/**
 * @author arno
 */
trait Unbindable {
  def unbind ()    (implicit digest: Digest): Unit
}
