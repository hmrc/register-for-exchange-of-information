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

package uk.gov.hmrc.registerforexchangeofinformation.models

import org.scalatest.OptionValues
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import uk.gov.hmrc.registerforexchangeofinformation.base.SpecBase

class BusinessTypeSpec
    extends SpecBase
    with ScalaCheckPropertyChecks
    with OptionValues {

  "BusinessType" - {

    "must deserialise valid values" - {

      "partnerShip" in {
        Json.parse(""""Partnership"""").as[BusinessType] mustBe partnerShip
      }
      "limited liability" in {
        Json.parse(""""LLP"""").as[BusinessType] mustBe limitedLiability
      }
      "Corporate Body" in {
        Json.parse(""""Corporate Body"""").as[BusinessType] mustBe corporateBody
      }
      "Unincorporated Body" in {
        Json
          .parse(""""Unincorporated Body"""")
          .as[BusinessType] mustBe unIncorporatedBody
      }
      "Not Specified" in {
        Json.parse(""""Not Specified"""").as[BusinessType] mustBe other
      }
    }

    "must serialise" - {

      "partnerShip" in {
        Json
          .toJson(partnerShip: BusinessType)
          .toString() mustBe """"Partnership""""
      }
      "limited liability" in {
        Json
          .toJson(limitedLiability: BusinessType)
          .toString() mustBe """"LLP""""
      }
      "Corporate Body" in {
        Json
          .toJson(corporateBody: BusinessType)
          .toString() mustBe """"Corporate Body""""
      }
      "Unincorporated Body" in {
        Json
          .toJson(unIncorporatedBody: BusinessType)
          .toString() mustBe """"Unincorporated Body""""
      }
      "Not Specified" in {
        Json
          .toJson(other: BusinessType)
          .toString() mustBe """"Not Specified""""
      }
    }
  }
}
