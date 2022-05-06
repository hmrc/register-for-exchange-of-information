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

package uk.gov.hmrc.registerforexchangeofinformation.connectors

import com.google.inject.Inject
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse}
import uk.gov.hmrc.registerforexchangeofinformation.config.AppConfig
import uk.gov.hmrc.registerforexchangeofinformation.models.DisplaySubscriptionForMDRRequest
import uk.gov.hmrc.registerforexchangeofinformation.models.subscription.request.{
  CreateSubscriptionForCBCRequest,
  CreateSubscriptionForMDRRequest,
  CreateSubscriptionRequest
}

import scala.concurrent.{ExecutionContext, Future}

class SubscriptionConnector @Inject() (
    val config: AppConfig,
    val http: HttpClient
) {

  def sendSubscriptionInformation(
      subscription: CreateSubscriptionRequest
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    subscription match {
      case subs: CreateSubscriptionForMDRRequest =>
        val serviceName = "create-subscription"
        http.POST[CreateSubscriptionForMDRRequest, HttpResponse](
          config.baseUrl(serviceName),
          subs,
          headers = extraHeaders(config, serviceName)
        )(
          wts = CreateSubscriptionForMDRRequest.format,
          rds = httpReads,
          hc = hc,
          ec = ec
        )
      case subs: CreateSubscriptionForCBCRequest =>
        val serviceName = "create-subscription-cbc"
        http.POST[CreateSubscriptionForCBCRequest, HttpResponse](
          config.baseUrl(serviceName),
          subs,
          headers = extraHeaders(config, serviceName)
        )(
          wts = CreateSubscriptionForCBCRequest.format,
          rds = httpReads,
          hc = hc,
          ec = ec
        )
    }
  }

  def readSubscriptionInformation(
      subscription: DisplaySubscriptionForMDRRequest
  )(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val serviceName = "read-subscription"
    http.POST[DisplaySubscriptionForMDRRequest, HttpResponse](
      config.baseUrl(serviceName),
      subscription,
      headers = extraHeaders(config, serviceName)
    )(
      wts = DisplaySubscriptionForMDRRequest.format,
      rds = httpReads,
      hc = hc,
      ec = ec
    )
  }
}
