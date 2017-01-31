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

package common.exceptions

sealed trait RepositoryException

final case class PreExistingRegDocument(regId: String) extends RepositoryException

final case class MissingRegDocument(regId: String) extends RepositoryException

final case class UpdateFailed(regId: String, attemptedModel: String) extends RepositoryException

final case class InsertFailed(regId: String, attemptedModel: String) extends RepositoryException

final case class DeleteFailed(regId: String, msg: String) extends RepositoryException

final case class RetrieveFailed(regId: String) extends RepositoryException


case object NotFound extends RepositoryException

case object Forbidden extends RepositoryException

final case class GenericRepositoryException(oe: Option[Exception]) extends RepositoryException