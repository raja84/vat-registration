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

package connectors

import java.util.UUID

import helpers.VatRegSpec
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse, _}

import scala.concurrent.Future

/**
  * Created by crispy on 03/08/16.
  */
class AuthConnectorSpec extends VatRegSpec with BeforeAndAfter {

  implicit val hc = HeaderCarrier()

  val mockHttp = mock[HttpGet with HttpPost]

  object TestAuthConnector extends AuthConnector {
    lazy val serviceUrl = "localhost"
    val authorityUri = "auth/authority"
    override val http: HttpGet with HttpPost = mockHttp
  }

  def authResponseJson(uri: String, userDetailsLink: String, gatewayId: String, idsLink: String): JsValue = Json.parse(
    s"""
       |{
       |  "uri":"$uri",
       |  "userDetailsLink":"$userDetailsLink",
       |  "credentials" : {
       |    "gatewayId":"$gatewayId"
       |  },
       |  "ids":"$idsLink"
       |}
     """.stripMargin
  )

  def idsResponseJson(internalId: String, externalId: String): JsValue = Json.parse(
    s"""{
           "internalId":"$internalId",
           "externalId":"$externalId"
        }""")

  before {
    reset(mockHttp)
  }

  "The auth connector" should {
    val ggid = "testGGID"
    val uri = s"""x/y/abc"""
    val userDetailsLink = "bar"
    val idsLink = "/auth/ids"

    "return auth info when an authority is found" in {
      val userIDs = UserIds("foo", "bar")
      val expected = Authority(uri, ggid, userDetailsLink, userIDs)

      when(mockHttp.GET[HttpResponse](Matchers.eq("localhost/auth/authority"))(Matchers.any(), Matchers.any())).
        thenReturn(Future.successful(HttpResponse(OK, Some(authResponseJson(uri, userDetailsLink, ggid, idsLink)))))

      when(mockHttp.GET[HttpResponse](Matchers.eq(s"localhost$idsLink"))(Matchers.any(), Matchers.any())).
        thenReturn(Future.successful(HttpResponse(OK, Some(idsResponseJson(userIDs.internalId, userIDs.externalId)))))


      implicit val hc = new HeaderCarrier(sessionId = Some(SessionId(s"session-${UUID.randomUUID}")))
      val result = TestAuthConnector.getCurrentAuthority()
      val authority = await(result)

      authority shouldBe Some(expected)
    }

    "return None when an authority isn't found" in {

      when(mockHttp.GET[HttpResponse](Matchers.eq("localhost/auth/authority"))(Matchers.any(), Matchers.any())).
        thenReturn(Future.successful(HttpResponse(NOT_FOUND, None)))

      implicit val hc = new HeaderCarrier(sessionId = Some(SessionId(s"session-${UUID.randomUUID}")))
      val result = TestAuthConnector.getCurrentAuthority()
      val authority = await(result)

      authority shouldBe None
    }
  }
}
