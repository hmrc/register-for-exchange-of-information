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

package uk.gov.hmrc.registerforexchangeofinformation.generators

import java.time.LocalDate
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.registerforexchangeofinformation.models._
import uk.gov.hmrc.registerforexchangeofinformation.models.subscription.common.{
  ContactInformationForIndividual,
  ContactInformationForOrganisation,
  IndividualDetails,
  OrganisationDetails,
  PrimaryContact,
  SecondaryContact
}
import uk.gov.hmrc.registerforexchangeofinformation.models.subscription.request.{
  CreateSubscriptionForCBCRequest,
  CreateSubscriptionForMDRRequest,
  CreateSubscriptionRequest,
  RequestCommonForSubscription,
  RequestDetail,
  SubscriptionRequest
}

trait ModelGenerators {
  self: Generators =>

  implicit val arbitraryName: Arbitrary[Name] = Arbitrary {
    for {
      firstName <- stringsWithMaxLength(50)
      secondName <- stringsWithMaxLength(50)
    } yield Name(firstName, secondName)
  }

  implicit val arbitraryNino: Arbitrary[Nino] = Arbitrary {
    for {
      prefix <- Gen.oneOf(Nino.validPrefixes)
      number <- Gen.choose(0, 999999)
      suffix <- Gen.oneOf(Nino.validSuffixes)
    } yield Nino(f"$prefix$number%06d$suffix")
  }

  implicit val arbitraryUtr: Arbitrary[Utr] = Arbitrary {
    for {
      value <- Gen.listOfN(10, Gen.chooseNum(0, 9)).map(_.mkString)
    } yield Utr(value)
  }

  implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
    datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
  }

  implicit val arbitraryRequestCommon: Arbitrary[RequestCommon] = Arbitrary {
    for {
      receiptDate <- arbitrary[String]
      acknowledgementRef <- stringsWithMaxLength(32)
      regime <- Gen.oneOf("MDR", "CBC")
    } yield RequestCommon(
      receiptDate = receiptDate,
      regime = regime,
      acknowledgementReference = acknowledgementRef,
      None
    )
  }

  implicit val arbitraryRegistration: Arbitrary[RegisterWithoutId] = Arbitrary {
    for {
      requestCommon <- arbitrary[RequestCommon]
      name <- arbitrary[String]
      address <- arbitrary[Address]
      contactDetails <- arbitrary[ContactDetails]
      identification <- Gen.option(arbitrary[Identification])
    } yield RegisterWithoutId(
      RegisterWithoutIDRequest(
        requestCommon,
        RequestDetails(
          Some(NoIdOrganisation(name)),
          None,
          address = address,
          contactDetails = contactDetails,
          identification = identification
        )
      )
    )
  }

  implicit val arbitraryAddress: Arbitrary[Address] = Arbitrary {
    for {
      addressLine1 <- arbitrary[String]
      addressLine2 <- Gen.option(arbitrary[String])
      addressLine3 <- arbitrary[String]
      addressLine4 <- Gen.option(arbitrary[String])
      postalCode <- Gen.option(arbitrary[String])
      countryCode <- arbitrary[String]
    } yield Address(
      addressLine1 = addressLine1,
      addressLine2 = addressLine2,
      addressLine3 = addressLine3,
      addressLine4 = addressLine4,
      postalCode = postalCode,
      countryCode = countryCode
    )
  }

  implicit val arbitraryContactDetails: Arbitrary[ContactDetails] = Arbitrary {
    for {
      phoneNumber <- Gen.option(arbitrary[String])
      mobileNumber <- Gen.option(arbitrary[String])
      faxNumber <- Gen.option(arbitrary[String])
      emailAddress <- Gen.option(arbitrary[String])
    } yield ContactDetails(
      phoneNumber = phoneNumber,
      mobileNumber = mobileNumber,
      faxNumber = faxNumber,
      emailAddress = emailAddress
    )
  }

  implicit val arbitraryIdentification: Arbitrary[Identification] = Arbitrary {
    for {
      idNumber <- arbitrary[String]
      issuingInstitution <- arbitrary[String]
      issuingCountryCode <- arbitrary[String]
    } yield Identification(
      idNumber = idNumber,
      issuingInstitution = issuingInstitution,
      issuingCountryCode = issuingCountryCode
    )
  }

  implicit val arbitraryPayloadRegisterWithID: Arbitrary[RegisterWithID] =
    Arbitrary {
      for {
        registerWithIDRequest <- arbitrary[RegisterWithIDRequest]
      } yield RegisterWithID(registerWithIDRequest)
    }

  implicit val arbitraryRegisterWithIDRequest
      : Arbitrary[RegisterWithIDRequest] = Arbitrary {
    for {
      requestCommon <- arbitrary[RequestCommon]
      requestDetail <- arbitrary[RequestWithIDDetails]
    } yield RegisterWithIDRequest(requestCommon, requestDetail)
  }

  implicit val arbitraryRequestWithIDDetails: Arbitrary[RequestWithIDDetails] =
    Arbitrary {
      for {
        idType <- arbitrary[String]
        idNumber <- arbitrary[String]
        requiresNameMatch <- arbitrary[Boolean]
        isAnAgent <- arbitrary[Boolean]
        partnerDetails <- Gen.oneOf(
          arbitrary[WithIDIndividual],
          arbitrary[WithIDOrganisation]
        )
      } yield RequestWithIDDetails(
        idType,
        idNumber,
        requiresNameMatch,
        isAnAgent,
        partnerDetails
      )
    }

  implicit val arbitraryWithIDIndividual: Arbitrary[WithIDIndividual] =
    Arbitrary {
      for {
        firstName <- arbitrary[String]
        middleName <- Gen.option(arbitrary[String])
        lastName <- arbitrary[String]
        dateOfBirth <- Gen.option(arbitrary[String])
      } yield WithIDIndividual(firstName, middleName, lastName, dateOfBirth)
    }

  implicit val arbitraryWithIDOrganisation: Arbitrary[WithIDOrganisation] =
    Arbitrary {
      for {
        organisationName <- arbitrary[String]
        organisationType <- Gen.oneOf(
          Seq("0000", "0001", "0002", "0003", "0004")
        )
      } yield WithIDOrganisation(organisationName, organisationType)
    }

  implicit val arbitraryRequestCommonForSubscription
      : Arbitrary[RequestCommonForSubscription] =
    Arbitrary {
      for {
        receiptDate <- arbitrary[String]
        acknowledgementRef <- stringsWithMaxLength(32)
      } yield RequestCommonForSubscription(
        regime = "MDR",
        conversationID = None,
        receiptDate = receiptDate,
        acknowledgementReference = acknowledgementRef,
        originatingSystem = "MDTP",
        None
      )
    }

  implicit val arbitraryIndividualDetails: Arbitrary[IndividualDetails] =
    Arbitrary {
      for {
        firstName <- arbitrary[String]
        middleName <- Gen.option(arbitrary[String])
        lastName <- arbitrary[String]
      } yield IndividualDetails(
        firstName = firstName,
        middleName = middleName,
        lastName = lastName
      )
    }

  implicit val arbitraryOrganisationDetails: Arbitrary[OrganisationDetails] =
    Arbitrary {
      for {
        name <- arbitrary[String]
      } yield OrganisationDetails(organisationName = name)
    }

  implicit val arbitraryContactInformationForIndividual
      : Arbitrary[ContactInformationForIndividual] = Arbitrary {
    for {
      individual <- arbitrary[IndividualDetails]
      email <- arbitrary[String]
      phone <- Gen.option(arbitrary[String])
      mobile <- Gen.option(arbitrary[String])
    } yield ContactInformationForIndividual(
      individual,
      email,
      phone,
      mobile
    )
  }

  implicit val arbitraryContactInformationForOrganisation
      : Arbitrary[ContactInformationForOrganisation] = Arbitrary {
    for {
      organisation <- arbitrary[OrganisationDetails]
      email <- arbitrary[String]
      phone <- Gen.option(arbitrary[String])
      mobile <- Gen.option(arbitrary[String])
    } yield ContactInformationForOrganisation(
      organisation,
      email,
      phone,
      mobile
    )
  }

  implicit val arbitraryPrimaryContact: Arbitrary[PrimaryContact] = Arbitrary {
    for {
      contactInformation <- Gen.oneOf(
        arbitrary[ContactInformationForIndividual],
        arbitrary[ContactInformationForIndividual]
      )
    } yield PrimaryContact(contactInformation)
  }

  implicit val arbitrarySecondaryContact: Arbitrary[SecondaryContact] =
    Arbitrary {
      for {
        contactInformation <- Gen.oneOf(
          arbitrary[ContactInformationForIndividual],
          arbitrary[ContactInformationForIndividual]
        )
      } yield SecondaryContact(contactInformation)
    }

  implicit val arbitraryRequestDetail: Arbitrary[RequestDetail] = Arbitrary {
    for {
      idType <- arbitrary[String]
      idNumber <- arbitrary[String]
      tradingName <- Gen.option(arbitrary[String])
      isGBUser <- arbitrary[Boolean]
      primaryContact <- arbitrary[PrimaryContact]
      secondaryContact <- Gen.option(arbitrary[SecondaryContact])
    } yield RequestDetail(
      IDType = idType,
      IDNumber = idNumber,
      tradingName = tradingName,
      isGBUser = isGBUser,
      primaryContact = primaryContact,
      secondaryContact = secondaryContact
    )
  }

  implicit val arbitraryCreateSubscriptionRequest
      : Arbitrary[CreateSubscriptionRequest] =
    Arbitrary {
      for {
        requestCommon <- arbitrary[RequestCommonForSubscription]
        requestDetail <- arbitrary[RequestDetail]
      } yield CreateSubscriptionForMDRRequest(
        SubscriptionRequest(requestCommon, requestDetail)
      )
    }

  implicit val arbitraryCreateSubscriptionForMDRRequest
      : Arbitrary[CreateSubscriptionForMDRRequest] =
    Arbitrary {
      for {
        requestCommon <- arbitrary[RequestCommonForSubscription]
        requestDetail <- arbitrary[RequestDetail]
      } yield CreateSubscriptionForMDRRequest(
        SubscriptionRequest(requestCommon, requestDetail)
      )
    }

  implicit val arbitraryCreateSubscriptionForCBCRequest
      : Arbitrary[CreateSubscriptionForCBCRequest] =
    Arbitrary {
      for {
        requestCommon <- arbitrary[RequestCommonForSubscription]
        requestDetail <- arbitrary[RequestDetail]
      } yield CreateSubscriptionForCBCRequest(
        SubscriptionRequest(requestCommon, requestDetail)
      )
    }

  implicit val arbitraryReadSubscriptionRequestDetail
      : Arbitrary[ReadSubscriptionRequestDetail] = Arbitrary {
    for {
      idType <- arbitrary[String]
      idNumber <- arbitrary[String]
    } yield ReadSubscriptionRequestDetail(
      IDType = idType,
      IDNumber = idNumber
    )
  }

  implicit val arbitraryReadSubscriptionForMDRRequest
      : Arbitrary[DisplaySubscriptionForMDRRequest] =
    Arbitrary {
      for {
        requestCommon <- arbitrary[RequestCommonForSubscription]
        requestDetail <- arbitrary[ReadSubscriptionRequestDetail]
      } yield DisplaySubscriptionForMDRRequest(
        DisplaySubscriptionDetails(requestCommon, requestDetail)
      )
    }

}
