/*
 * Copyright 2021 HM Revenue & Customs
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
import play.api.Logger
import play.api.libs.json.{JsResult, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, ControllerComponents, Result}
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.registerforexchangeofinformation.auth.AuthAction
import uk.gov.hmrc.registerforexchangeofinformation.config.AppConfig
import uk.gov.hmrc.registerforexchangeofinformation.connectors.SubscriptionConnector
import uk.gov.hmrc.registerforexchangeofinformation.models.{
  CreateSubscriptionForMDRRequest,
  ErrorDetails
}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

class SubscriptionController @Inject() (
    val config: AppConfig,
    authenticate: AuthAction,
    subscriptionConnector: SubscriptionConnector,
    override val controllerComponents: ControllerComponents
)(implicit executionContext: ExecutionContext)
    extends BackendController(controllerComponents) {

  private val logger: Logger = Logger(this.getClass)

  def createSubscription: Action[JsValue] = authenticate(parse.json).async {
    implicit request =>
      val subscriptionSubmissionResult
          : JsResult[CreateSubscriptionForMDRRequest] =
        request.body.validate[CreateSubscriptionForMDRRequest]

      subscriptionSubmissionResult.fold(
        invalid = _ =>
          Future.successful(
            BadRequest("CreateSubscriptionForMDRRequest is invalid")
          ),
        valid = sub =>
          for {
            response <- subscriptionConnector.sendSubscriptionInformation(sub)
          } yield convertToResult(response)
      )
  }

  private def convertToResult(httpResponse: HttpResponse): Result =
    httpResponse.status match {
      case OK        => Ok(httpResponse.body)
      case NOT_FOUND => NotFound(httpResponse.body)
      case BAD_REQUEST =>
        logDownStreamError(httpResponse.body)
        BadRequest(httpResponse.body)

      case FORBIDDEN =>
        logDownStreamError(httpResponse.body)
        Forbidden(httpResponse.body)

      case SERVICE_UNAVAILABLE =>
        logDownStreamError(httpResponse.body)
        ServiceUnavailable(httpResponse.body)

      case CONFLICT =>
        logDownStreamError(httpResponse.body)
        Conflict(httpResponse.body)

      case _ =>
        logDownStreamError(httpResponse.body)
        InternalServerError(httpResponse.body)

    }

  private def logDownStreamError(body: String): Unit = {
    val error = Try(Json.parse(body).validate[ErrorDetails])
    error match {
      case Success(JsSuccess(value, _)) =>
        logger.error(
          s"Error with submission: ${value.errorDetail.sourceFaultDetail.map(_.detail.mkString)}"
        )
      case _ =>
        logger.error("Error with submission but return is not a valid json")
    }
  }
}
