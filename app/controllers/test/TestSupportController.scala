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

package controllers.test

import javax.inject.Inject

import auth.Authenticated
import connectors.test.BusinessRegistrationTestConnector
import connectors.{AuthConnector, BusinessRegistrationConnector}
import play.api.mvc.{Action, AnyContent}
import repositories.test.TestOnlyRepository
import uk.gov.hmrc.play.microservice.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Left, Right}

class TestSupportController @Inject()(
                                       val auth: AuthConnector,
                                       brConnector: BusinessRegistrationConnector,
                                       brTestConnector: BusinessRegistrationTestConnector,
                                       testOnlyRepository: TestOnlyRepository
                                     ) extends BaseController with Authenticated {
  // $COVERAGE-OFF$

  def currentProfileSetup(): Action[AnyContent] = Action.async { implicit request =>
    authenticated { user =>
      brConnector.retrieveCurrentProfile flatMap {
        case Left(common.exceptions.ResourceNotFound(msg)) => brTestConnector.createCurrentProfileEntry()
        case Right(b) => Future.successful(Ok)
        case Left(_) => Future.successful(ServiceUnavailable)
      }
    }
  }

  def dropCollection(): Action[AnyContent] = Action.async { implicit request =>
    authenticated { user =>
      testOnlyRepository.dropCollection
      Future.successful(Ok("Collection Dropped"))
    }
  }

  // $COVERAGE-ON$
}
