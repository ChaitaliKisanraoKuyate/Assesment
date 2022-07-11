function() {
 var config={REST_HOST: 'http://services.groupkt.com/country/get/iso2code/IN',
 SOAP_HOST: 'http://www.holidaywebservice.com/HolidayService_v2/HolidayService2.asmx',
 INITIALIZE: 'https://udr937n3hh.execute-api.us-east-1.amazonaws.com/stage/api/v1.0/initialization',
 IS_RECOGNIZED: 'https://udr937n3hh.execute-api.us-east-1.amazonaws.com/stage/api/v1.0/identities/recognise?srcClientId=a52f70e5-43f8-422d-9e3e-4203b574fff4&srcDpaId=ce06944b-0e82-4826-88d2-f5717a3d9695&serviceId=payload&srciTransactionId=ce06944b-0e82-4826-88d2-f5717a3d9695',
 PREPARE_SRC_PROFILE: 'https://udr937n3hh.execute-api.us-east-1.amazonaws.com/stage/api/v1.0/profiles/prepare',
 CHECKOUT: 'https://udr937n3hh.execute-api.us-east-1.amazonaws.com/stage/api/v1.0/transaction/credentials',
 ENROLL: 'https://udr937n3hh.execute-api.us-east-1.amazonaws.com/stage/api/v1.0/cards',
 findByBankIdAndReason: 'https://api.afinis.org/v1/banks/061103852/contact?reason=ACH_FRAUD&apikey=CNOzZUjTfm8EpXCI3AGG4CKK3oGUr9W4',
 APIKEY: "Q6vnRnDGMGT6pMkn0UXMHiO176pn7CH9",
 AFINIS_INITIATE_PAYMENT_CREDIT: 'https://api.afinis.org/v1/payments/ach/credit?apikey=',
 AFINIS_GET_TRANSACTION_STATUS_INSTRUCTION_ID: 'https://api.afinis.org/v1/payments/ach/status/instructionId/',
 USERNAME: 'DummyUN',
 PASSWORD: 'DummyTestPassword',
 url: 'Dummydriver',
 DB_USERNAME: 'dbUserName',
 DB_PASSWORD: 'dbPassword'
 };
 switch(karate.env){
    case 'UAT':
            config.USERNAME= 'uatUser';
            config.PASSWORD= 'uatPassword';
            //config.REST_HOST= 'https://restcountries.eu/rest/v2/capital';
            config.REST_HOST= 'http://services.groupkt.com/country/get/iso2code/';
            config.SOAP_HOST= 'http://www.holidaywebservice.com/HolidayService_v2/HolidayService2.asmx';
            break;

    case 'DEV':
             config.USERNAME= "devUser";
             config.PASSWORD= "devPassword";
             config.REST_HOST= 'http://services.groupkt.com/country/get/iso2code/IN';
             config.SOAP_HOST= 'http://www.holidaywebservice.com/HolidayService_v2/HolidayService2.asmx';
             break;
    case 'PROD':
             config.USERNAME= "devUser";
             config.PASSWORD= "devPassword";
             config.REST_HOST= 'http://services.groupkt.com/country/get/iso2code/IN';
             config.SOAP_HOST= 'http://www.holidaywebservice.com/HolidayService_v2/HolidayService2.asmx';
             break;

    case 'Discover':
            config.INITIALIZE= 'https://udr937n3hh.execute-api.us-east-1.amazonaws.com/stage/api/v1.0/initialization';
            config.IS_RECOGNIZED= 'https://udr937n3hh.execute-api.us-east-1.amazonaws.com/stage/api/v1.0/identities/recognise?srcClientId=a52f70e5-43f8-422d-9e3e-4203b574fff4&srcDpaId=ce06944b-0e82-4826-88d2-f5717a3d9695&serviceId=payload&srciTransactionId=ce06944b-0e82-4826-88d2-f5717a3d9695';
            config.PREPARE_SRC_PROFILE= 'https://udr937n3hh.execute-api.us-east-1.amazonaws.com/stage/api/v1.0/profiles/prepare';
            config.CHECKOUT= 'https://udr937n3hh.execute-api.us-east-1.amazonaws.com/stage/api/v1.0/transaction/credentials';
            config.ENROLL= 'https://udr937n3hh.execute-api.us-east-1.amazonaws.com/stage/api/v1.0/cards';
            break;
     case 'Afinis':

            config.APIKEY= "Q6vnRnDGMGT6pMkn0UXMHiO176pn7CH9";
            config.findByBankIdAndReason= 'https://api.afinis.org/v1/banks/#(bankId)/contact?reason=#(contactReaso)&apikey='+ config.APIKEY;
            config.AFINIS_INITIATE_PAYMENT_CREDIT= 'https://api.afinis.org/v1/payments/ach/credit?apikey=' + config.APIKEY;
            config.AFINIS_INITIATE_PAYMENT_DEBIT= 'https://api.afinis.org/v1/payments/ach/debit?apikey=' + config.APIKEY;
            config.AFINIS_GET_TRANSACTION_STATUS_INSTRUCTION_ID= 'https://api.afinis.org/v1/payments/ach/status/instructionId/'+ '5234561?apikey='+ config.APIKEY;
            config.AFINIS_POST_CREDIT_ACCOUNT_VALIDATION='https://api.afinis.org/v1/accounts/validate/achPaymentCreditAcceptance?apikey='+config.APIKEY;
            config.AFINIS_POST_DEBIT_ACCOUNT_VALIDATION='https://api.afinis.org/v1/accounts/validate/achPaymentDebitAcceptance?apikey='+config.APIKEY;
            config.AFINIS_POST_BILLING_ACCOUNT_VALIDATION='https://api.afinis.org/v1/accounts/payers?apikey='+config.APIKEY;
            break;
 }
var dirPath = karate.properties['user.dir'];
//config = karate.call(dirPath + '/src/test/resources/Utility.js',config);
//config = karate.call(dirPath + '\\src\\test\\resources\\header.js',config);
return config;
}

