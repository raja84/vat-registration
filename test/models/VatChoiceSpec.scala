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

package models

import java.time.LocalDate

import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.play.test.UnitSpec

class VatChoiceSpec extends UnitSpec with JsonFormatValidation {

  "Creating a VatChoice model from Json" should {

    val startDate = LocalDate.of(2017, 1, 1)

    "complete successfully from full Json" in {
      val json = Json.parse(
        s"""
           |{
           |  "start-date":"$startDate",
           |  "necessity":"obligatory"
           |}
        """.stripMargin)

      val tstVatChoice = VatChoice(
        startDate = startDate,
        necessity = "obligatory"
      )

      Json.fromJson[VatChoice](json) shouldBe JsSuccess(tstVatChoice)
    }
  }
}