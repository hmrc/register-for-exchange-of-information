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

import org.joda.time.DateTime
import org.mockito.ArgumentMatchers.any
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.audit.http.connector.AuditResult
import uk.gov.hmrc.registerforexchangeofinformation.auth.{
  AuthAction,
  FakeAuthAction
}
import uk.gov.hmrc.registerforexchangeofinformation.base.SpecBase
import uk.gov.hmrc.registerforexchangeofinformation.connectors.SubscriptionConnector
import uk.gov.hmrc.registerforexchangeofinformation.generators.Generators
import uk.gov.hmrc.registerforexchangeofinformation.models._
import uk.gov.hmrc.registerforexchangeofinformation.services.AuditService

import scala.concurrent.{ExecutionContext, Future}

class SubscriptionControllerSpec
    extends SpecBase
    with Generators
    with ScalaCheckPropertyChecks
    with BeforeAndAfterEach {

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val mockAuditService: AuditService = mock[AuditService]

  val mockSubscriptionConnector: SubscriptionConnector =
    mock[SubscriptionConnector]

  val application: Application = applicationBuilder()
    .overrides(
      bind[SubscriptionConnector].toInstance(mockSubscriptionConnector),
      bind[AuthConnector].toInstance(mockAuthConnector),
      bind[AuthAction].to[FakeAuthAction],
      bind[AuditService].toInstance(mockAuditService)
    )
    .build()

  override def beforeEach: Unit = {
    reset(
      mockSubscriptionConnector,
      mockAuthConnector,
      mockSubscriptionConnector,
      mockAuditService
    )
  }

  "SubscriptionController" - {

    "should return OK when subscriptionForMDRRequest is valid" in {
      when(
        mockSubscriptionConnector
          .sendSubscriptionInformation(any[CreateSubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      ).thenReturn(
        Future.successful(
          HttpResponse(200, Json.obj(), Map.empty[String, Seq[String]])
        )
      )

      when(
        mockAuditService.sendAuditEvent(any[String](), any[JsValue]())(
          any[HeaderCarrier]()
        )
      )
        .thenReturn(Future.successful(AuditResult.Success))

      forAll(arbitrary[CreateSubscriptionForMDRRequest]) {
        subscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.createSubscription().url
            )
              .withJsonBody(Json.toJson(subscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual OK
      }
    }

    "should return OK when ReadSubscriptionForMDRRequest is valid" in {
      when(
        mockSubscriptionConnector
          .readSubscriptionInformation(any[DisplaySubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      ).thenReturn(
        Future.successful(
          HttpResponse(200, Json.obj(), Map.empty[String, Seq[String]])
        )
      )

      forAll(arbitrary[DisplaySubscriptionForMDRRequest]) {
        readSubscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.readSubscription().url
            )
              .withJsonBody(Json.toJson(readSubscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual OK
      }
    }

    "should return BAD_REQUEST when subscriptionForMDRRequest ia invalid" in {
      when(
        mockSubscriptionConnector
          .sendSubscriptionInformation(any[CreateSubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(400, Json.obj(), Map.empty[String, Seq[String]])
          )
        )

      when(
        mockAuditService.sendAuditEvent(any[String](), any[JsValue]())(
          any[HeaderCarrier]()
        )
      )
        .thenReturn(Future.successful(AuditResult.Success))

      val request =
        FakeRequest(
          POST,
          routes.SubscriptionController.createSubscription().url
        )
          .withJsonBody(Json.parse("""{"value": "field"}"""))

      val result = route(application, request).value
      status(result) mustEqual BAD_REQUEST
    }

    "should return BAD_REQUEST when DisplaySubscriptionForMDRRequest ia invalid" in {
      when(
        mockSubscriptionConnector
          .readSubscriptionInformation(any[DisplaySubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(400, Json.obj(), Map.empty[String, Seq[String]])
          )
        )

      val request =
        FakeRequest(
          POST,
          routes.SubscriptionController.readSubscription().url
        )
          .withJsonBody(Json.parse("""{"value": "field"}"""))

      val result = route(application, request).value
      status(result) mustEqual BAD_REQUEST
    }

    "should return BAD_REQUEST when one is encountered" in {
      when(
        mockSubscriptionConnector
          .sendSubscriptionInformation(any[CreateSubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(400, Json.obj(), Map.empty[String, Seq[String]])
          )
        )

      when(
        mockAuditService.sendAuditEvent(any[String](), any[JsValue]())(
          any[HeaderCarrier]()
        )
      )
        .thenReturn(Future.successful(AuditResult.Success))

      forAll(arbitrary[CreateSubscriptionForMDRRequest]) {
        subscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.createSubscription().url
            )
              .withJsonBody(Json.toJson(subscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual BAD_REQUEST
      }
    }

    "should return FORBIDDEN when authorisation is invalid" in {
      when(
        mockSubscriptionConnector
          .sendSubscriptionInformation(any[CreateSubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(403, Json.obj(), Map.empty[String, Seq[String]])
          )
        )

      when(
        mockAuditService.sendAuditEvent(any[String](), any[JsValue]())(
          any[HeaderCarrier]()
        )
      )
        .thenReturn(Future.successful(AuditResult.Success))

      forAll(arbitrary[CreateSubscriptionForMDRRequest]) {
        subscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.createSubscription().url
            )
              .withJsonBody(Json.toJson(subscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual FORBIDDEN
      }
    }

    "should return FORBIDDEN when authorisation is invalid for read Subscription" in {
      when(
        mockSubscriptionConnector
          .readSubscriptionInformation(any[DisplaySubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(403, Json.obj(), Map.empty[String, Seq[String]])
          )
        )

      when(
        mockAuditService.sendAuditEvent(any[String](), any[JsValue]())(
          any[HeaderCarrier]()
        )
      )
        .thenReturn(Future.successful(AuditResult.Success))

      forAll(arbitrary[DisplaySubscriptionForMDRRequest]) {
        readSubscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.readSubscription().url
            )
              .withJsonBody(Json.toJson(readSubscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual FORBIDDEN
      }
    }

    "should return SERVICE_UNAVAILABLE when EIS becomes unavailable for read Subscription" in {
      when(
        mockSubscriptionConnector
          .readSubscriptionInformation(any[DisplaySubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(503, Json.obj(), Map.empty[String, Seq[String]])
          )
        )

      forAll(arbitrary[DisplaySubscriptionForMDRRequest]) {
        readSubscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.readSubscription().url
            )
              .withJsonBody(Json.toJson(readSubscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual SERVICE_UNAVAILABLE
      }
    }

    "should return SERVICE_UNAVAILABLE when EIS becomes unavailable" in {
      when(
        mockSubscriptionConnector
          .sendSubscriptionInformation(any[CreateSubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(503, Json.obj(), Map.empty[String, Seq[String]])
          )
        )

      when(
        mockAuditService.sendAuditEvent(any[String](), any[JsValue]())(
          any[HeaderCarrier]()
        )
      )
        .thenReturn(Future.successful(AuditResult.Success))

      forAll(arbitrary[CreateSubscriptionForMDRRequest]) {
        subscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.createSubscription().url
            )
              .withJsonBody(Json.toJson(subscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual SERVICE_UNAVAILABLE
      }
    }

    "should return INTERNAL_SERVER_ERROR when EIS fails" in {
      when(
        mockSubscriptionConnector
          .sendSubscriptionInformation(any[CreateSubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(
              BAD_GATEWAY,
              Json.obj(),
              Map.empty[String, Seq[String]]
            )
          )
        )

      when(
        mockAuditService.sendAuditEvent(any[String](), any[JsValue]())(
          any[HeaderCarrier]()
        )
      )
        .thenReturn(Future.successful(AuditResult.Success))

      forAll(arbitrary[CreateSubscriptionForMDRRequest]) {
        subscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.createSubscription().url
            )
              .withJsonBody(Json.toJson(subscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }
    "should return INTERNAL_SERVER_ERROR when EIS fails for readSubscription" in {
      when(
        mockSubscriptionConnector
          .readSubscriptionInformation(any[DisplaySubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(
              BAD_GATEWAY,
              Json.obj(),
              Map.empty[String, Seq[String]]
            )
          )
        )

      forAll(arbitrary[DisplaySubscriptionForMDRRequest]) {
        readSubscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.readSubscription().url
            )
              .withJsonBody(Json.toJson(readSubscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual INTERNAL_SERVER_ERROR
      }
    }

    "should return CONFLICT when one occurs" in {
      val errorDetails = ErrorDetails(
        ErrorDetail(
          DateTime.now().toString,
          Some("xx"),
          "409",
          "CONFLICT",
          "",
          Some(SourceFaultDetail(Seq("a", "b")))
        )
      )
      when(
        mockSubscriptionConnector
          .sendSubscriptionInformation(any[CreateSubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(
              409,
              Json.toJson(errorDetails),
              Map.empty[String, Seq[String]]
            )
          )
        )

      when(
        mockAuditService.sendAuditEvent(any[String](), any[JsValue]())(
          any[HeaderCarrier]()
        )
      )
        .thenReturn(Future.successful(AuditResult.Success))

      forAll(arbitrary[CreateSubscriptionForMDRRequest]) {
        subscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.createSubscription().url
            )
              .withJsonBody(Json.toJson(subscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual CONFLICT
      }
    }

    "should return NOT_FOUND for unspecified errors" in {
      when(
        mockSubscriptionConnector
          .sendSubscriptionInformation(any[CreateSubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(404, Json.obj(), Map.empty[String, Seq[String]])
          )
        )

      when(
        mockAuditService.sendAuditEvent(any[String](), any[JsValue]())(
          any[HeaderCarrier]()
        )
      )
        .thenReturn(Future.successful(AuditResult.Success))

      forAll(arbitrary[CreateSubscriptionForMDRRequest]) {
        subscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.createSubscription().url
            )
              .withJsonBody(Json.toJson(subscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual NOT_FOUND
      }
    }

    "should return NOT_FOUND for unspecified errors for read Subscription" in {
      when(
        mockSubscriptionConnector
          .readSubscriptionInformation(any[DisplaySubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(404, Json.obj(), Map.empty[String, Seq[String]])
          )
        )

      forAll(arbitrary[DisplaySubscriptionForMDRRequest]) {
        readSubscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.readSubscription().url
            )
              .withJsonBody(Json.toJson(readSubscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual NOT_FOUND
      }
    }

    "downstream errors should be recoverable when not in json" in {
      when(
        mockSubscriptionConnector
          .sendSubscriptionInformation(any[CreateSubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(503, "Not Available", Map.empty[String, Seq[String]])
          )
        )

      when(
        mockAuditService.sendAuditEvent(any[String](), any[JsValue]())(
          any[HeaderCarrier]()
        )
      )
        .thenReturn(Future.successful(AuditResult.Success))

      forAll(arbitrary[CreateSubscriptionForMDRRequest]) {
        subscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.createSubscription().url
            )
              .withJsonBody(Json.toJson(subscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual SERVICE_UNAVAILABLE
      }
    }

    "downstream errors should be recoverable when not in json for read subscription" in {
      when(
        mockSubscriptionConnector
          .readSubscriptionInformation(any[DisplaySubscriptionForMDRRequest]())(
            any[HeaderCarrier](),
            any[ExecutionContext]()
          )
      )
        .thenReturn(
          Future.successful(
            HttpResponse(503, "Not Available", Map.empty[String, Seq[String]])
          )
        )

      forAll(arbitrary[DisplaySubscriptionForMDRRequest]) {
        readSubscriptionForMDRRequest =>
          val request =
            FakeRequest(
              POST,
              routes.SubscriptionController.readSubscription().url
            )
              .withJsonBody(Json.toJson(readSubscriptionForMDRRequest))

          val result = route(application, request).value
          status(result) mustEqual SERVICE_UNAVAILABLE
      }
    }

  }
}
