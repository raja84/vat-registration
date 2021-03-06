/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.api

import auth.Crypto
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class VatBankAccount(
                           accountName: String,
                           accountSortCode: String,
                           accountNumber: String
                         )
  extends VatBankAccountValidator

object VatBankAccount extends VatBankAccountValidator {

  implicit val format: OFormat[VatBankAccount] = (
    (__ \ "accountName").format[String] and
      (__ \ "accountSortCode").format[String](accountSortCodeValidator) and
      (__ \ "accountNumber").format[String](accountNumberValidator)
    ) (VatBankAccount.apply, unlift(VatBankAccount.unapply))

}

object VatBankAccountMongoFormat {

  implicit val encryptedFormat: OFormat[VatBankAccount] = (
    (__ \ "accountName").format[String] and
      (__ \ "accountSortCode").format[String] and
      (__ \ "accountNumber").format[String](Crypto.rds)(Crypto.wts)
    ) (VatBankAccount.apply, unlift(VatBankAccount.unapply))
}
