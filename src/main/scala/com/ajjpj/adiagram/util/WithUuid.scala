package com.ajjpj.adiagram.util

import java.util.UUID

/**
 * @author arno
 */
trait WithUuid {
  protected var _uuid = UUID.randomUUID()
  def uuid = _uuid
}
