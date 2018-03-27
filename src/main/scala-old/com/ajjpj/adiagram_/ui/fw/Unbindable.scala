package com.ajjpj.adiagram_.ui.fw

/**
 * @author arno
 */
trait Unbindable {
  def unbind ()    (implicit digest: Digest): Unit
}
