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

package uk.gov.hmrc.registerforexchangeofinformation.services

import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import play.api.{Application, Logger}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.http.connector.AuditResult.{
  Disabled,
  Failure,
  Success
}
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent
import uk.gov.hmrc.registerforexchangeofinformation.base.SpecBase
import uk.gov.hmrc.registerforexchangeofinformation.config.AppConfig

import scala.concurrent.{ExecutionContext, Future}

class AuditServiceSpec extends SpecBase with BeforeAndAfterEach {

  val mockAuditConnector: AuditConnector = mock[AuditConnector]
  val mockLogger: Logger = mock[Logger]
  val mockAppConfig: AppConfig = mock[AppConfig]
  val eventDetail: JsObject = Json.obj("test-param1" -> "test-value-1")

  override lazy val app: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[AuditConnector].toInstance(mockAuditConnector),
      bind[Logger].toInstance(mockLogger),
      bind[AppConfig].toInstance(mockAppConfig)
    )
    .build()

  override def beforeEach: Unit = {
    reset(mockAuditConnector)
  }

  val auditService: AuditService =
    new AuditService(mockAppConfig, mockAuditConnector) {
      override val logger: Logger = mockLogger
    }

  "AuditService must" - {
    "call the audit connector and sendExtendedEvent when it is Success" in {

      when(mockLogger.isDebugEnabled).thenReturn(true)

      when(
        mockAuditConnector.sendExtendedEvent(any[ExtendedDataEvent]())(
          any[HeaderCarrier](),
          any[ExecutionContext]()
        )
      )
        .thenReturn(Future.successful(Success))
      val result = auditService.sendAuditEvent(
        "dummy app",
        eventDetail
      )(hc)

      result.futureValue mustBe Success

      verify(mockAuditConnector, times(1))
        .sendExtendedEvent(any[ExtendedDataEvent]())(
          any[HeaderCarrier](),
          any[ExecutionContext]()
        )

      verify(mockLogger, times(1)).debug(
        s"Audit event dummy app issued successful."
      )
    }

    "call the audit connector and sendExtendedEvent when it is Failure" in {
      when(
        mockAuditConnector.sendExtendedEvent(any[ExtendedDataEvent]())(
          any[HeaderCarrier](),
          any[ExecutionContext]()
        )
      )
        .thenReturn(Future.successful(Failure("Some thing gone wrong")))

      val result = auditService.sendAuditEvent(
        "dummy app",
        eventDetail
      )(hc)

      result.futureValue mustBe Failure("Some thing gone wrong")

      verify(mockAuditConnector, times(1))
        .sendExtendedEvent(any[ExtendedDataEvent]())(
          any[HeaderCarrier](),
          any[ExecutionContext]()
        )
    }

    "call the audit connector and sendExtendedEvent when it is Disabled" in {
      when(
        mockAuditConnector.sendExtendedEvent(any[ExtendedDataEvent]())(
          any[HeaderCarrier](),
          any[ExecutionContext]()
        )
      )
        .thenReturn(Future.successful(Disabled))

      val result = auditService.sendAuditEvent(
        "dummy app",
        eventDetail
      )(hc)

      result.futureValue mustBe Disabled

      verify(mockAuditConnector, times(1))
        .sendExtendedEvent(any[ExtendedDataEvent]())(
          any[HeaderCarrier](),
          any[ExecutionContext]()
        )
    }

  }

}
