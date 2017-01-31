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

package repositories

import javax.inject.{Inject, Named}

import cats.data.EitherT
import common.exceptions.{InsertFailed, RepositoryException, RetrieveFailed}
import helpers.DateTimeHelpers._
import models._
import play.api.Logger
import play.modules.reactivemongo.MongoDbConnection
import reactivemongo.api.DB
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID, _}
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RegistrationRepository {

  def createNewRegistration(registrationId: String): RepositoryResult[VatScheme]

  def retrieveRegistration(registrationId: String): RepositoryResult[Option[VatScheme]]

}

class MongoDBProvider extends Function0[DB] with MongoDbConnection {
  def apply: DB = db()
}

class RegistrationMongoRepository @Inject()(mongoProvider: Function0[DB], @Named("collectionName") collectionName: String)
  extends ReactiveRepository[VatScheme, BSONObjectID](
    collectionName = collectionName,
    mongo = mongoProvider,
    domainFormat = VatScheme.format
  ) with RegistrationRepository {

  override def indexes: Seq[Index] = Seq(
    Index(
      name = Some("RegId"),
      key = Seq("registrationId" -> IndexType.Ascending),
      unique = true
    )
  )

  private[repositories] def registrationIdSelector(registrationID: String) = BSONDocument("ID" -> BSONString(registrationID))

  override def createNewRegistration(registrationId: String): RepositoryResult[VatScheme] = {
    val newReg = VatScheme.blank(registrationId)
    EitherT[Future, RepositoryException, VatScheme](
      collection.insert(newReg) map {
        _ => Right(newReg)
      } recover {
        case e =>
          Logger.warn(s"Unable to insert new VAT Registration for registration ID $registrationId, Error: ${e.getMessage}")
          Left(InsertFailed(registrationId, "VatScheme"))
      })
  }

  override def retrieveRegistration(registrationId: String): RepositoryResult[Option[VatScheme]] = {
    val selector = registrationIdSelector(registrationId)
    EitherT[Future, RepositoryException, Option[VatScheme]](
      collection.find(selector).one[VatScheme] map (Right(_)) recover {
        case e: Exception =>
          Logger.error(s"Unable to retrieve VAT Registration for registration ID $registrationId, Error: ${e.getMessage}")
          Left(RetrieveFailed(registrationId))
      }
    )
  }

}
