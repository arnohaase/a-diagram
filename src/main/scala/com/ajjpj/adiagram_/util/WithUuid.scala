package com.ajjpj.adiagram_.util

import java.util.UUID

/**
 * @author arno
 */
trait WithUuid {
  var uuid = UUID.randomUUID()
}
