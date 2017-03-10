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

package mocks

import cats.data.EitherT
import common.exceptions._
import connectors.{AuthConnector, Authority}
import models._
import org.mockito.Matchers
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.mockito.MockitoSugar
import services.{RegistrationService, ServiceResult}
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

trait VatMocks extends WSHTTPMock {

  this: MockitoSugar =>

  lazy val mockAuthConnector = mock[AuthConnector]
  lazy val mockRegistrationService = mock[RegistrationService]

  object AuthorisationMocks {

    def mockSuccessfulAuthorisation(authority: Authority): OngoingStubbing[Future[Option[Authority]]] = {
      when(mockAuthConnector.getCurrentAuthority()(Matchers.any()))
        .thenReturn(Future.successful(Some(authority)))
    }

    def mockNotLoggedInOrAuthorised: OngoingStubbing[Future[Option[Authority]]] = {
      when(mockAuthConnector.getCurrentAuthority()(Matchers.any[HeaderCarrier]()))
        .thenReturn(Future.successful(None))
    }

    def mockNotAuthorised(authority: Authority): OngoingStubbing[Future[Option[Authority]]] = {
      when(mockAuthConnector.getCurrentAuthority()(Matchers.any()))
        .thenReturn(Future.successful(Some(authority)))
    }

  }

  object ServiceMocks {

    def serviceResult[B](b: B): ServiceResult[B] = {
      EitherT[Future, LeftState, B](Future.successful(Right(b)))
    }

    def serviceError[B](a: LeftState): ServiceResult[B] = {
      EitherT[Future, LeftState, B](Future.successful(Left(a)))
    }

    def mockRetrieveVatSchemeThrowsException(testId: String): Unit = {
      val exception = new Exception("Exception")
      when(mockRegistrationService.retrieveVatScheme(Matchers.any()))
        .thenReturn(serviceError[VatScheme](GenericDatabaseError(exception, Some("regId"))))
    }

    def mockRetrieveVatScheme(testId: String, vatScheme: VatScheme): Unit = {
      when(mockRegistrationService.retrieveVatScheme(Matchers.contains(testId)))
        .thenReturn(serviceResult(vatScheme))
    }

    def mockDeleteVatScheme(testId: String): Unit = {
      when(mockRegistrationService.deleteVatScheme(Matchers.contains(testId)))
        .thenReturn(serviceResult(true))
    }

    def mockDeleteVatSchemeThrowsException(testId: String): Unit = {
      val exception = new Exception("Exception")
      when(mockRegistrationService.deleteVatScheme(Matchers.any()))
        .thenReturn(serviceError[Boolean](GenericDatabaseError(exception, Some("regId"))))
    }

    def mockDeleteBankAccountDetails(testId: String): Unit = {
      when(mockRegistrationService.deleteBankAccountDetails(Matchers.contains(testId)))
        .thenReturn(serviceResult(true))
    }

    def mockDeleteBankAccountDetailsThrowsException(testId: String): Unit = {
      val exception = new Exception("Exception")
      when(mockRegistrationService.deleteBankAccountDetails(Matchers.any()))
        .thenReturn(serviceError[Boolean](GenericDatabaseError(exception, Some("regId"))))
    }

    def mockDeleteAccountingPeriodStart(testId: String): Unit = {
      when(mockRegistrationService.deleteAccountingPeriodStart(Matchers.contains(testId)))
        .thenReturn(serviceResult(true))
    }

    def mockDeleteAccountingPeriodStartThrowsException(testId: String): Unit = {
      val exception = new Exception("Exception")
      when(mockRegistrationService.deleteAccountingPeriodStart(Matchers.any()))
        .thenReturn(serviceError[Boolean](GenericDatabaseError(exception, Some("regId"))))
    }

    def mockDeleteZeroRatedTurnover(testId: String): Unit = {
      when(mockRegistrationService.deleteZeroRatedTurnover(Matchers.contains(testId)))
        .thenReturn(serviceResult(true))
    }

    def mockDeleteZeroRatedTurnoverThrowsException(testId: String): Unit = {
      val exception = new Exception("Exception")
      when(mockRegistrationService.deleteZeroRatedTurnover(Matchers.any()))
        .thenReturn(serviceError[Boolean](GenericDatabaseError(exception, Some("regId"))))
    }

    def mockSuccessfulCreateNewRegistration(registrationId: String): Unit = {
      when(mockRegistrationService.createNewRegistration()(Matchers.any[HeaderCarrier]()))
        .thenReturn(serviceResult(VatScheme(registrationId, None, None, None)))
    }

    def mockFailedCreateNewRegistration(registrationId: String): Unit = {
      when(mockRegistrationService.createNewRegistration()(Matchers.any[HeaderCarrier]()))
        .thenReturn(serviceError[VatScheme](GenericError(new RuntimeException("something went wrong"))))
    }

    def mockFailedCreateNewRegistrationWithDbError(registrationId: String): Unit = {
      val exception = new Exception("Exception")
      when(mockRegistrationService.createNewRegistration()(Matchers.any[HeaderCarrier]()))
        .thenReturn(serviceError[VatScheme](GenericDatabaseError(exception, Some("regId"))))
    }

    def mockSuccessfulUpdateVatChoice(registrationId: String, vatChoice: VatChoice): Unit = {
      when(mockRegistrationService.updateVatChoice(Matchers.any(), Matchers.any()))
        .thenReturn(serviceResult(vatChoice))
    }

    def mockServiceUnavailableUpdateVatChoice(registrationId: String, vatChoice: VatChoice, exception: Exception): Unit = {
      when(mockRegistrationService.updateVatChoice(Matchers.any(), Matchers.any()))
        .thenReturn(serviceError[VatChoice](GenericError(exception)))
    }

    def mockSuccessfulUpdateTradingDetails(registrationId: String, tradingDetails: VatTradingDetails): Unit = {
      when(mockRegistrationService.updateTradingDetails(Matchers.any(), Matchers.any()))
        .thenReturn(serviceResult(tradingDetails))
    }

    def mockServiceUnavailableUpdateTradingDetails(registrationId: String, exception: Exception): Unit = {
      when(mockRegistrationService.updateTradingDetails(Matchers.any(), Matchers.any()))
        .thenReturn(serviceError[VatTradingDetails](GenericError(exception)))
    }

    def mockSuccessfulUpdateSicAndCompliance(registrationId: String, sicAndCompliance: VatSicAndCompliance): Unit = {
      when(mockRegistrationService.updateSicAndCompliance(Matchers.any(), Matchers.any()))
        .thenReturn(serviceResult(sicAndCompliance))
    }

    def mockServiceUnavailableUpdateSicAndCompliance(registrationId: String, exception: Exception): Unit = {
      when(mockRegistrationService.updateSicAndCompliance(Matchers.any(), Matchers.any()))
        .thenReturn(serviceError[VatSicAndCompliance](GenericError(exception)))
    }

    def mockSuccessfulUpdateVatFinancials(registrationId: String, vatFinancials: VatFinancials): Unit = {
      when(mockRegistrationService.updateVatFinancials(Matchers.any(), Matchers.any()))
        .thenReturn(serviceResult(vatFinancials))
    }

    def mockServiceUnavailableUpdateVatFinancials(registrationId: String, vatFinancials: VatFinancials, exception: Exception): Unit = {
      when(mockRegistrationService.updateVatFinancials(Matchers.any(), Matchers.any()))
        .thenReturn(serviceError[VatFinancials](GenericError(exception)))
    }

  }

}
