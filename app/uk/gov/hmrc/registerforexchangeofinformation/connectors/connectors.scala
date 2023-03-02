/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.registerforexchangeofinformation

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

import uk.gov.hmrc.http.{Authorization, HeaderCarrier, HeaderNames, HttpReads, HttpResponse}
import uk.gov.hmrc.registerforexchangeofinformation.config.AppConfig

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

package object connectors {

  implicit val httpReads: HttpReads[HttpResponse] =
    (_: String, _: String, response: HttpResponse) => response

  private[connectors] def extraHeaders(
    config: AppConfig,
    serviceName: String
  )(implicit headerCarrier: HeaderCarrier): Seq[(String, String)] = {
    val newHeaders = headerCarrier
      .copy(authorization = Some(Authorization(s"Bearer ${config.bearerToken(serviceName)}")))

    newHeaders.headers(Seq(HeaderNames.authorisation)) ++ addHeaders(
      config.environment(serviceName)
    )
  }

  val stripSession: String => String = (input: String) => input.replace("session-", "")

  private def addHeaders(
    eisEnvironment: String
  )(implicit headerCarrier: HeaderCarrier): Seq[(String, String)] = {

    //HTTP-date format defined by RFC 7231 e.g. Fri, 01 Aug 2020 15:51:38 GMT+1
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O")

    Seq(
      "x-forwarded-host" -> "mdtp",
      "date"             -> ZonedDateTime.now().format(formatter),
      "x-correlation-id" -> UUID.randomUUID().toString,
      "x-conversation-id" -> {
        headerCarrier.sessionId
          .map(
            s => stripSession(s.value)
          )
          .getOrElse(UUID.randomUUID().toString)
      },
      "content-type" -> "application/json",
      "accept"       -> "application/json",
      "Environment"  -> eisEnvironment
    )
  }
}
