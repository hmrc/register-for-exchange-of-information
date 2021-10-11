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

package uk.gov.hmrc.registerforexchangeofinformation.models

import play.api.libs.json._

sealed trait ContactInformation

case class OrganisationDetails(organisationName: String)

object OrganisationDetails {
  implicit val format: OFormat[OrganisationDetails] =
    Json.format[OrganisationDetails]
}

case class IndividualDetails(
    firstName: String,
    middleName: Option[String],
    lastName: String
)

object IndividualDetails {
  implicit val format: OFormat[IndividualDetails] =
    Json.format[IndividualDetails]
}

case class ContactInformationForIndividual(
    individual: IndividualDetails,
    email: String,
    phone: Option[String],
    mobile: Option[String]
) extends ContactInformation

object ContactInformationForIndividual {
  implicit val format: OFormat[ContactInformationForIndividual] =
    Json.format[ContactInformationForIndividual]
}

case class ContactInformationForOrganisation(
    organisation: OrganisationDetails,
    email: String,
    phone: Option[String],
    mobile: Option[String]
) extends ContactInformation

object ContactInformationForOrganisation {
  implicit val format: OFormat[ContactInformationForOrganisation] =
    Json.format[ContactInformationForOrganisation]
}

case class PrimaryContact(contactInformation: ContactInformation)

object PrimaryContact {

  implicit lazy val reads: Reads[PrimaryContact] = {
    import play.api.libs.functional.syntax._
    (
      (__ \ "organisation").readNullable[OrganisationDetails] and
        (__ \ "individual").readNullable[IndividualDetails] and
        (__ \ "email").read[String] and
        (__ \ "phone").readNullable[String] and
        (__ \ "mobile").readNullable[String]
    )((organisation, individual, email, phone, mobile) =>
      (organisation.isDefined, individual.isDefined) match {
        case (true, false) =>
          PrimaryContact(
            ContactInformationForOrganisation(
              organisation.get,
              email,
              phone,
              mobile
            )
          )
        case (false, true) =>
          PrimaryContact(
            ContactInformationForIndividual(
              individual.get,
              email,
              phone,
              mobile
            )
          )
        case _ =>
          throw new Exception(
            "Primary Contact must have either an organisation or individual element"
          )
      }
    )
  }

  implicit lazy val writes: OWrites[PrimaryContact] = {
    case PrimaryContact(
          contactInformationForInd @ ContactInformationForIndividual(_, _, _, _)
        ) =>
      Json.toJsObject(contactInformationForInd)
    case PrimaryContact(
          contactInformationForOrg @ ContactInformationForOrganisation(
            _,
            _,
            _,
            _
          )
        ) =>
      Json.toJsObject(contactInformationForOrg)
  }
}

case class SecondaryContact(contactInformation: ContactInformation)

object SecondaryContact {

  implicit lazy val reads: Reads[SecondaryContact] = {
    import play.api.libs.functional.syntax._
    (
      (__ \ "organisation").readNullable[OrganisationDetails] and
        (__ \ "individual").readNullable[IndividualDetails] and
        (__ \ "email").read[String] and
        (__ \ "phone").readNullable[String] and
        (__ \ "mobile").readNullable[String]
    )((organisation, individual, email, phone, mobile) =>
      (organisation.isDefined, individual.isDefined) match {
        case (true, false) =>
          SecondaryContact(
            ContactInformationForOrganisation(
              organisation.get,
              email,
              phone,
              mobile
            )
          )
        case (false, true) =>
          SecondaryContact(
            ContactInformationForIndividual(
              individual.get,
              email,
              phone,
              mobile
            )
          )
        case _ =>
          throw new Exception(
            "Secondary Contact must have either an organisation or individual element"
          )
      }
    )
  }

  implicit lazy val writes: OWrites[SecondaryContact] = {
    case SecondaryContact(
          contactInformationForInd @ ContactInformationForIndividual(_, _, _, _)
        ) =>
      Json.toJsObject(contactInformationForInd)
    case SecondaryContact(
          contactInformationForOrg @ ContactInformationForOrganisation(
            _,
            _,
            _,
            _
          )
        ) =>
      Json.toJsObject(contactInformationForOrg)
  }
}

case class RequestDetail(
    IDType: String,
    IDNumber: String,
    tradingName: Option[String],
    isGBUser: Boolean,
    primaryContact: PrimaryContact,
    secondaryContact: Option[SecondaryContact]
)

object RequestDetail {
  implicit val requestDetailFormats: OFormat[RequestDetail] =
    Json.format[RequestDetail]
}

case class RequestParameters(paramName: String, paramValue: String)

object RequestParameters {
  implicit val indentifierFormats: OFormat[RequestParameters] =
    Json.format[RequestParameters]
}

case class RequestCommonForSubscription(
    regime: String,
    receiptDate: String,
    acknowledgementReference: String,
    originatingSystem: String,
    requestParameters: Option[Seq[RequestParameters]]
)

object RequestCommonForSubscription {
  implicit val requestCommonForSubscriptionFormats
      : OFormat[RequestCommonForSubscription] =
    Json.format[RequestCommonForSubscription]
}

case class SubscriptionForMDRRequest(
    requestCommon: RequestCommonForSubscription,
    requestDetail: RequestDetail
)

object SubscriptionForMDRRequest {
  implicit val format: OFormat[SubscriptionForMDRRequest] =
    Json.format[SubscriptionForMDRRequest]
}

case class CreateSubscriptionForMDRRequest(
    createSubscriptionForMDRRequest: SubscriptionForMDRRequest
)

object CreateSubscriptionForMDRRequest {
  implicit val format: OFormat[CreateSubscriptionForMDRRequest] =
    Json.format[CreateSubscriptionForMDRRequest]
}

case class ReturnParameters(paramName: String, paramValue: String)

object ReturnParameters {
  implicit val format: Format[ReturnParameters] = Json.format[ReturnParameters]
}

case class ResponseCommon(
    status: String,
    statusText: Option[String],
    processingDate: String,
    returnParameters: Option[Seq[ReturnParameters]]
)

object ResponseCommon {
  implicit val format: Format[ResponseCommon] = Json.format[ResponseCommon]
}

case class ResponseDetailForMDRSubscription(subscriptionID: String)

object ResponseDetailForMDRSubscription {
  implicit val format: OFormat[ResponseDetailForMDRSubscription] =
    Json.format[ResponseDetailForMDRSubscription]
}

case class SubscriptionForMDRResponse(
    responseCommon: ResponseCommon,
    responseDetail: ResponseDetailForMDRSubscription
)

object SubscriptionForMDRResponse {

  implicit val reads: Reads[SubscriptionForMDRResponse] = {
    import play.api.libs.functional.syntax._
    (
      (__ \ "responseCommon").read[ResponseCommon] and
        (__ \ "responseDetail").read[ResponseDetailForMDRSubscription]
    )((responseCommon, responseDetail) =>
      SubscriptionForMDRResponse(responseCommon, responseDetail)
    )
  }

  implicit val writes: OWrites[SubscriptionForMDRResponse] =
    Json.writes[SubscriptionForMDRResponse]
}

case class CreateSubscriptionForMDRResponse(
    createSubscriptionForMDRResponse: SubscriptionForMDRResponse
)

object CreateSubscriptionForMDRResponse {
  implicit val format: OFormat[CreateSubscriptionForMDRResponse] =
    Json.format[CreateSubscriptionForMDRResponse]
}
