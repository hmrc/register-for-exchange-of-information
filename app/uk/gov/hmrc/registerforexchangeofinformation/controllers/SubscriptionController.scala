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
import play.api.libs.json.{JsResult, JsValue, Json}
import play.api.mvc.{Action, ControllerComponents}
import play.api.{Logger, Logging}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.registerforexchangeofinformation.auth.AuthAction
import uk.gov.hmrc.registerforexchangeofinformation.config.AppConfig
import uk.gov.hmrc.registerforexchangeofinformation.connectors.SubscriptionConnector
import uk.gov.hmrc.registerforexchangeofinformation.models.audit.SubscriptionAudit
import uk.gov.hmrc.registerforexchangeofinformation.models.{
  CreateSubscriptionForMDRRequest,
  DisplaySubscriptionForMDRRequest
}
import uk.gov.hmrc.registerforexchangeofinformation.services.AuditService

import scala.concurrent.{ExecutionContext, Future}

class SubscriptionController @Inject() (
    val config: AppConfig,
    authenticate: AuthAction,
    subscriptionConnector: SubscriptionConnector,
    auditService: AuditService,
    override val controllerComponents: ControllerComponents
)(implicit executionContext: ExecutionContext)
    extends BackendController(controllerComponents)
    with Logging {

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
            _ <- auditService.sendAuditEvent(
              "MDRSubscription",
              Json.toJson(
                SubscriptionAudit.fromRequestDetail(
                  sub.createSubscriptionForMDRRequest.requestDetail
                )
              )
            )
          } yield response.convertToResult(implicitly[Logger](logger))
      )
  }

  def readSubscription: Action[JsValue] = authenticate(parse.json).async {
    implicit request =>
      val subscriptionReadResult: JsResult[DisplaySubscriptionForMDRRequest] =
        request.body.validate[DisplaySubscriptionForMDRRequest]

      subscriptionReadResult.fold(
        invalid = _ =>
          Future.successful(
            BadRequest("DisplaySubscriptionForMDRRequest is invalid")
          ),
        valid = sub =>
          for {
            response <- subscriptionConnector.readSubscriptionInformation(sub)
          } yield response.convertToResult(implicitly[Logger](logger))
      )
  }
}
