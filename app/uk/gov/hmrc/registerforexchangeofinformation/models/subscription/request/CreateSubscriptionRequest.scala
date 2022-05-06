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

package uk.gov.hmrc.registerforexchangeofinformation.models.subscription.request

import play.api.libs.json.{Json, OFormat}
import julienrf.json.derived

sealed trait CreateSubscriptionRequest

case class CreateSubscriptionForMDRRequest(
    createSubscriptionForMDRRequest: SubscriptionRequest
) extends CreateSubscriptionRequest

object CreateSubscriptionForMDRRequest {
  implicit val format: OFormat[CreateSubscriptionForMDRRequest] =
    Json.format[CreateSubscriptionForMDRRequest]
}

case class CreateSubscriptionForCBCRequest(
    createSubscriptionForCBCRequest: SubscriptionRequest
) extends CreateSubscriptionRequest

object CreateSubscriptionForCBCRequest {
  implicit val format: OFormat[CreateSubscriptionForCBCRequest] =
    Json.format[CreateSubscriptionForCBCRequest]
}

object CreateSubscriptionRequest {
  implicit val format = derived.oformat[CreateSubscriptionRequest]()
}
