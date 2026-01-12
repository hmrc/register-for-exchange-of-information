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

package uk.gov.hmrc.registerforexchangeofinformation.connectors

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.registerforexchangeofinformation.config.AppConfig
import uk.gov.hmrc.registerforexchangeofinformation.models.{RegisterWithID, RegisterWithoutId}

import java.net.URI
import scala.concurrent.{ExecutionContext, Future}

class RegistrationConnector @Inject() (val config: AppConfig, val http: HttpClientV2) {

  def sendWithoutIDInformation(registration: RegisterWithoutId)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val serviceName = "register-without-id"
    http
      .post(new URI(config.baseUrl(serviceName)).toURL)
      .setHeader(extraHeaders(config, serviceName): _*)
      .withBody(Json.toJson(registration))
      .execute[HttpResponse]
  }

  def sendWithID(registration: RegisterWithID)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val serviceName = "register-with-id"
    http
      .post(new URI(config.baseUrl(serviceName)).toURL)
      .setHeader(extraHeaders(config, serviceName): _*)
      .withBody(Json.toJson(registration))
      .execute[HttpResponse]
  }

}
