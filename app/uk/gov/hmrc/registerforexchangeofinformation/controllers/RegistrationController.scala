/*
 * Copyright 2022 HM Revenue & Customs
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

package uk.gov.hmrc.registerforexchangeofinformation.controllers

import com.google.inject.Inject
import play.api.libs.json.{JsResult, JsValue}
import play.api.mvc.{Action, ControllerComponents, Request, Result}
import play.api.{Logger, Logging}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.registerforexchangeofinformation.auth.AuthAction
import uk.gov.hmrc.registerforexchangeofinformation.config.AppConfig
import uk.gov.hmrc.registerforexchangeofinformation.connectors.RegistrationConnector
import uk.gov.hmrc.registerforexchangeofinformation.models.{
  RegisterWithID,
  RegisterWithoutId
}

import scala.concurrent.{ExecutionContext, Future}

class RegistrationController @Inject() (
    val config: AppConfig,
    authenticate: AuthAction,
    registrationConnector: RegistrationConnector,
    override val controllerComponents: ControllerComponents
)(implicit executionContext: ExecutionContext)
    extends BackendController(controllerComponents)
    with Logging {

  def noIdRegistration: Action[JsValue] = authenticate.async(parse.json) {
    implicit request =>
      withoutIDRegistration(request)
  }

  private def withoutIDRegistration(
      request: Request[JsValue]
  )(implicit hc: HeaderCarrier): Future[Result] = {

    val noIdOrganisationRegistration: JsResult[RegisterWithoutId] =
      request.body.validate[RegisterWithoutId]

    noIdOrganisationRegistration.fold(
      invalid = _ => Future.successful(BadRequest("")),
      valid = sub =>
        for {
          response <- registrationConnector.sendWithoutIDInformation(sub)
        } yield response.convertToResult(implicitly[Logger](logger))
    )
  }

  def withoutID: Action[JsValue] = authenticate(parse.json).async {
    implicit request =>
      withoutIDRegistration(request)
  }

  def withoutOrgID: Action[JsValue] = authenticate(parse.json).async {
    implicit request =>
      withoutIDRegistration(request)
  }

  def withUTR: Action[JsValue] = authenticate(parse.json).async {
    implicit request =>
      withIdRegistration(request)
  }

  def withNino: Action[JsValue] = authenticate(parse.json).async {
    implicit request =>
      withIdRegistration(request)
  }

  def withOrgUTR: Action[JsValue] = authenticate(parse.json).async {
    implicit request =>
      withIdRegistration(request)
  }

  private def withIdRegistration(
      request: Request[JsValue]
  )(implicit hc: HeaderCarrier): Future[Result] = {

    val withIDRegistration: JsResult[RegisterWithID] =
      request.body.validate[RegisterWithID]

    withIDRegistration.fold(
      invalid = _ => Future.successful(BadRequest("")),
      valid = sub =>
        for {
          response <- registrationConnector.sendWithID(sub)
        } yield response.convertToResult(implicitly[Logger](logger))
    )
  }

}
