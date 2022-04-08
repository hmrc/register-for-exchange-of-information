# Register for exchange of information

This microservice is the backend data service for subscription and registration of exchange-of-information completion process.

### Overview:

This service interacts with [Register for exchange of information-frontend](https://github.com/hmrc/register-for-exchange-of-information-frontend) & ETMP.

### API:
| PATH | Context |  Supported Methods | Description |
|------|---------|--------------------|-------------|
|```/subscription/read-subscription``` |`/dac6/dct70d/v1`| POST | Reads subscription and returns subscriptionID -  |
|```/subscription/create-subscription``` |`/dac6/dct70c/v1`|POST | Creates subscription and returns subscriptionID |
|```/registration/organisation/utr``` |`/dac6/dct70b/v1`| POST | Sends registration for Organisation with ID |
|```/registration/individual/nino``` |`/dac6/dct70b/v1`| POST | Sends registration for Individual with ID |
|```/registration/organisation/noId``` |`/dac6/dct70a/v1`| POST | Sends registration for Organisation without ID |
|```/registration/individual/noId``` |`/dac6/dct70a/v1`| POST | Sends registration for Individual without ID |

***API Specs:***
- [Register without ID](https://confluence.tools.tax.service.gov.uk/display/DAC6/MDR+Specs?preview=/388662598/434373860/AEOI-DCT70a-1.10-EISAPISpecification-MDRCustomerRegistrationWithoutIdentifiertoETMP.pdf)
- [Register with ID](https://confluence.tools.tax.service.gov.uk/display/DAC6/MDR+Specs?preview=/388662598/434373864/AEOI-DCT70b-1.10-EISAPISpecification-MDRCustomerRegistrationWithIdentifiertoETMP.pdf)
- [Create subscription](https://confluence.tools.tax.service.gov.uk/display/DAC6/MDR+Specs?preview=/388662598/434373868/AEOI-DCT70c-1.10-EISAPISpecification-MDRCustomerSubscriptionCreate.pdf)
- [Read subscription](https://confluence.tools.tax.service.gov.uk/pages/viewpage.action?spaceKey=DAC6&title=MDR%20Specs&preview=/388662598/434373869/AEOI-DCT70d-1.2-EISAPISpecification-MDRSubscriptionDisplay.pdf)

## Run Locally

This service runs on port 10016 and is named REGISTER_FOR_EXCHANGE_OF_INFORMATION in service manager.

Run the following command to start services locally:

    sm --start MDR_ALL -r

#### *Acceptance test repo*:  
[register-for-exchange-of-information-ui-tests](https://github.com/hmrc/register-for-exchange-of-information-ui-tests)

## Requirements

This service is written in [Scala](http://www.scala-lang.org/) and [Play](http://playframework.com/), and requires a Java 8 [JRE] to run.

[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
