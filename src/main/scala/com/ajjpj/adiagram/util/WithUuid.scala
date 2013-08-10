package com.ajjpj.adiagram.util

import java.util.UUID

/**
 * @author arno
 */
trait WithUuid {
  var uuid = UUID.randomUUID()
}
