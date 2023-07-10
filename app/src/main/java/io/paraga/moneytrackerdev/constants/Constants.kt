package io.paraga.moneytrackerdev.constants

import android.content.Context
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.models.Language
import io.paraga.moneytrackerdev.models.Slider
import org.json.JSONObject

class Constants() {
    companion object {
        const val RC_SIGN_IN = 200
        const val IMG_PICK_CONS = 300

        const val USD = "$"
        const val INCOME = 0
        const val EXPENSE = 1
        const val DATE_KEY = "date_key"
        const val ACTION_UPDATE_WIDGET = "action_update_widget"
        const val ACTION_INCOME = "action_income"
        const val ACTION_EXPENSE = "action_expense"
        const val ACTION_MAIN = "action_main"
        const val APP_NAME = "Money Tracker"
        const val THREE_DAY_UNOPENED_CHANNEL_ID = "3DAYS_ALERT"
        const val WALLET_INVITATION_REQUEST_CODE = 0
        const val THREE_DAY_UNOPENED_REQUEST_CODE = 1
        val currencyList = """
        {
	"USD": {
		"symbol": "${'$'}",
		"name": "US Dollar",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "USD",
		"name_plural": "US dollars"
	},
	"CAD": {
		"symbol": "CA$",
		"name": "Canadian Dollar",
		"symbol_native": "$",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "CAD",
		"name_plural": "Canadian dollars"
	},
	"EUR": {
		"symbol": "€",
		"name": "Euro",
		"symbol_native": "€",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "EUR",
		"name_plural": "euros"
	},
	"AED": {
		"symbol": "AED",
		"name": "United Arab Emirates Dirham",
		"symbol_native": "د.إ.‏",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "AED",
		"name_plural": "UAE dirhams"
	},
	"AFN": {
		"symbol": "Af",
		"name": "Afghan Afghani",
		"symbol_native": "؋",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "AFN",
		"name_plural": "Afghan Afghanis"
	},
	"ALL": {
		"symbol": "ALL",
		"name": "Albanian Lek",
		"symbol_native": "Lek",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "ALL",
		"name_plural": "Albanian lekë"
	},
	"AMD": {
		"symbol": "AMD",
		"name": "Armenian Dram",
		"symbol_native": "դր.",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "AMD",
		"name_plural": "Armenian drams"
	},
	"ARS": {
		"symbol": "AR${'$'}",
		"name": "Argentine Peso",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "ARS",
		"name_plural": "Argentine pesos"
	},
	"AUD": {
		"symbol": "AU${'$'}",
		"name": "Australian Dollar",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "AUD",
		"name_plural": "Australian dollars"
	},
	"AZN": {
		"symbol": "man.",
		"name": "Azerbaijani Manat",
		"symbol_native": "ман.",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "AZN",
		"name_plural": "Azerbaijani manats"
	},
	"BAM": {
		"symbol": "KM",
		"name": "Bosnia-Herzegovina Convertible Mark",
		"symbol_native": "KM",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "BAM",
		"name_plural": "Bosnia-Herzegovina convertible marks"
	},
	"BDT": {
		"symbol": "Tk",
		"name": "Bangladeshi Taka",
		"symbol_native": "৳",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "BDT",
		"name_plural": "Bangladeshi takas"
	},
	"BGN": {
		"symbol": "BGN",
		"name": "Bulgarian Lev",
		"symbol_native": "лв.",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "BGN",
		"name_plural": "Bulgarian leva"
	},
	"BHD": {
		"symbol": "BD",
		"name": "Bahraini Dinar",
		"symbol_native": "د.ب.‏",
		"decimal_digits": 3,
		"rounding": 0,
		"code": "BHD",
		"name_plural": "Bahraini dinars"
	},
	"BIF": {
		"symbol": "FBu",
		"name": "Burundian Franc",
		"symbol_native": "FBu",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "BIF",
		"name_plural": "Burundian francs"
	},
	"BND": {
		"symbol": "BN${'$'}",
		"name": "Brunei Dollar",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "BND",
		"name_plural": "Brunei dollars"
	},
	"BOB": {
		"symbol": "Bs",
		"name": "Bolivian Boliviano",
		"symbol_native": "Bs",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "BOB",
		"name_plural": "Bolivian bolivianos"
	},
	"BRL": {
		"symbol": "R${'$'}",
		"name": "Brazilian Real",
		"symbol_native": "R${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "BRL",
		"name_plural": "Brazilian reals"
	},
	"BWP": {
		"symbol": "BWP",
		"name": "Botswanan Pula",
		"symbol_native": "P",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "BWP",
		"name_plural": "Botswanan pulas"
	},
	"BYN": {
		"symbol": "Br",
		"name": "Belarusian Ruble",
		"symbol_native": "руб.",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "BYN",
		"name_plural": "Belarusian rubles"
	},
	"BZD": {
		"symbol": "BZ${'$'}",
		"name": "Belize Dollar",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "BZD",
		"name_plural": "Belize dollars"
	},
	"CDF": {
		"symbol": "CDF",
		"name": "Congolese Franc",
		"symbol_native": "FrCD",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "CDF",
		"name_plural": "Congolese francs"
	},
	"CHF": {
		"symbol": "CHF",
		"name": "Swiss Franc",
		"symbol_native": "CHF",
		"decimal_digits": 2,
		"rounding": 0.05,
		"code": "CHF",
		"name_plural": "Swiss francs"
	},
	"CLP": {
		"symbol": "CL${'$'}",
		"name": "Chilean Peso",
		"symbol_native": "${'$'}",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "CLP",
		"name_plural": "Chilean pesos"
	},
	"CNY": {
		"symbol": "CN¥",
		"name": "Chinese Yuan",
		"symbol_native": "CN¥",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "CNY",
		"name_plural": "Chinese yuan"
	},
	"COP": {
		"symbol": "CO${'$'}",
		"name": "Colombian Peso",
		"symbol_native": "${'$'}",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "COP",
		"name_plural": "Colombian pesos"
	},
	"CRC": {
		"symbol": "₡",
		"name": "Costa Rican Colón",
		"symbol_native": "₡",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "CRC",
		"name_plural": "Costa Rican colóns"
	},
	"CVE": {
		"symbol": "CV${'$'}",
		"name": "Cape Verdean Escudo",
		"symbol_native": "CV${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "CVE",
		"name_plural": "Cape Verdean escudos"
	},
	"CZK": {
		"symbol": "Kč",
		"name": "Czech Republic Koruna",
		"symbol_native": "Kč",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "CZK",
		"name_plural": "Czech Republic korunas"
	},
	"DJF": {
		"symbol": "Fdj",
		"name": "Djiboutian Franc",
		"symbol_native": "Fdj",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "DJF",
		"name_plural": "Djiboutian francs"
	},
	"DKK": {
		"symbol": "Dkr",
		"name": "Danish Krone",
		"symbol_native": "kr",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "DKK",
		"name_plural": "Danish kroner"
	},
	"DOP": {
		"symbol": "RD${'$'}",
		"name": "Dominican Peso",
		"symbol_native": "RD${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "DOP",
		"name_plural": "Dominican pesos"
	},
	"DZD": {
		"symbol": "DA",
		"name": "Algerian Dinar",
		"symbol_native": "د.ج.‏",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "DZD",
		"name_plural": "Algerian dinars"
	},
	"EEK": {
		"symbol": "Ekr",
		"name": "Estonian Kroon",
		"symbol_native": "kr",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "EEK",
		"name_plural": "Estonian kroons"
	},
	"EGP": {
		"symbol": "EGP",
		"name": "Egyptian Pound",
		"symbol_native": "ج.م.‏",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "EGP",
		"name_plural": "Egyptian pounds"
	},
	"ERN": {
		"symbol": "Nfk",
		"name": "Eritrean Nakfa",
		"symbol_native": "Nfk",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "ERN",
		"name_plural": "Eritrean nakfas"
	},
	"ETB": {
		"symbol": "Br",
		"name": "Ethiopian Birr",
		"symbol_native": "Br",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "ETB",
		"name_plural": "Ethiopian birrs"
	},
	"GBP": {
		"symbol": "£",
		"name": "British Pound Sterling",
		"symbol_native": "£",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "GBP",
		"name_plural": "British pounds sterling"
	},
	"GEL": {
		"symbol": "GEL",
		"name": "Georgian Lari",
		"symbol_native": "GEL",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "GEL",
		"name_plural": "Georgian laris"
	},
	"GHS": {
		"symbol": "GH₵",
		"name": "Ghanaian Cedi",
		"symbol_native": "GH₵",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "GHS",
		"name_plural": "Ghanaian cedis"
	},
	"GNF": {
		"symbol": "FG",
		"name": "Guinean Franc",
		"symbol_native": "FG",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "GNF",
		"name_plural": "Guinean francs"
	},
	"GTQ": {
		"symbol": "GTQ",
		"name": "Guatemalan Quetzal",
		"symbol_native": "Q",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "GTQ",
		"name_plural": "Guatemalan quetzals"
	},
	"HKD": {
		"symbol": "HK${'$'}",
		"name": "Hong Kong Dollar",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "HKD",
		"name_plural": "Hong Kong dollars"
	},
	"HNL": {
		"symbol": "HNL",
		"name": "Honduran Lempira",
		"symbol_native": "L",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "HNL",
		"name_plural": "Honduran lempiras"
	},
	"HRK": {
		"symbol": "kn",
		"name": "Croatian Kuna",
		"symbol_native": "kn",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "HRK",
		"name_plural": "Croatian kunas"
	},
	"HUF": {
		"symbol": "Ft",
		"name": "Hungarian Forint",
		"symbol_native": "Ft",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "HUF",
		"name_plural": "Hungarian forints"
	},
	"IDR": {
		"symbol": "Rp",
		"name": "Indonesian Rupiah",
		"symbol_native": "Rp",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "IDR",
		"name_plural": "Indonesian rupiahs"
	},
	"ILS": {
		"symbol": "₪",
		"name": "Israeli New Sheqel",
		"symbol_native": "₪",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "ILS",
		"name_plural": "Israeli new sheqels"
	},
	"INR": {
		"symbol": "Rs",
		"name": "Indian Rupee",
		"symbol_native": "টকা",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "INR",
		"name_plural": "Indian rupees"
	},
	"IQD": {
		"symbol": "IQD",
		"name": "Iraqi Dinar",
		"symbol_native": "د.ع.‏",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "IQD",
		"name_plural": "Iraqi dinars"
	},
	"IRR": {
		"symbol": "IRR",
		"name": "Iranian Rial",
		"symbol_native": "﷼",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "IRR",
		"name_plural": "Iranian rials"
	},
	"ISK": {
		"symbol": "Ikr",
		"name": "Icelandic Króna",
		"symbol_native": "kr",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "ISK",
		"name_plural": "Icelandic krónur"
	},
	"JMD": {
		"symbol": "J${'$'}",
		"name": "Jamaican Dollar",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "JMD",
		"name_plural": "Jamaican dollars"
	},
	"JOD": {
		"symbol": "JD",
		"name": "Jordanian Dinar",
		"symbol_native": "د.أ.‏",
		"decimal_digits": 3,
		"rounding": 0,
		"code": "JOD",
		"name_plural": "Jordanian dinars"
	},
	"JPY": {
		"symbol": "¥",
		"name": "Japanese Yen",
		"symbol_native": "￥",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "JPY",
		"name_plural": "Japanese yen"
	},
	"KES": {
		"symbol": "Ksh",
		"name": "Kenyan Shilling",
		"symbol_native": "Ksh",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "KES",
		"name_plural": "Kenyan shillings"
	},
	"KHR": {
		"symbol": "KHR",
		"name": "Cambodian Riel",
		"symbol_native": "៛",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "KHR",
		"name_plural": "Cambodian riels"
	},
	"KMF": {
		"symbol": "CF",
		"name": "Comorian Franc",
		"symbol_native": "FC",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "KMF",
		"name_plural": "Comorian francs"
	},
	"KRW": {
		"symbol": "₩",
		"name": "South Korean Won",
		"symbol_native": "₩",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "KRW",
		"name_plural": "South Korean won"
	},
	"KWD": {
		"symbol": "KD",
		"name": "Kuwaiti Dinar",
		"symbol_native": "د.ك.‏",
		"decimal_digits": 3,
		"rounding": 0,
		"code": "KWD",
		"name_plural": "Kuwaiti dinars"
	},
	"KZT": {
		"symbol": "KZT",
		"name": "Kazakhstani Tenge",
		"symbol_native": "тңг.",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "KZT",
		"name_plural": "Kazakhstani tenges"
	},
	"LBP": {
		"symbol": "L.L.",
		"name": "Lebanese Pound",
		"symbol_native": "ل.ل.‏",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "LBP",
		"name_plural": "Lebanese pounds"
	},
	"LKR": {
		"symbol": "SLRs",
		"name": "Sri Lankan Rupee",
		"symbol_native": "SL Re",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "LKR",
		"name_plural": "Sri Lankan rupees"
	},
	"LTL": {
		"symbol": "Lt",
		"name": "Lithuanian Litas",
		"symbol_native": "Lt",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "LTL",
		"name_plural": "Lithuanian litai"
	},
	"LVL": {
		"symbol": "Ls",
		"name": "Latvian Lats",
		"symbol_native": "Ls",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "LVL",
		"name_plural": "Latvian lati"
	},
	"LYD": {
		"symbol": "LD",
		"name": "Libyan Dinar",
		"symbol_native": "د.ل.‏",
		"decimal_digits": 3,
		"rounding": 0,
		"code": "LYD",
		"name_plural": "Libyan dinars"
	},
	"MAD": {
		"symbol": "MAD",
		"name": "Moroccan Dirham",
		"symbol_native": "د.م.‏",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "MAD",
		"name_plural": "Moroccan dirhams"
	},
	"MDL": {
		"symbol": "MDL",
		"name": "Moldovan Leu",
		"symbol_native": "MDL",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "MDL",
		"name_plural": "Moldovan lei"
	},
	"MGA": {
		"symbol": "MGA",
		"name": "Malagasy Ariary",
		"symbol_native": "MGA",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "MGA",
		"name_plural": "Malagasy Ariaries"
	},
	"MKD": {
		"symbol": "MKD",
		"name": "Macedonian Denar",
		"symbol_native": "MKD",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "MKD",
		"name_plural": "Macedonian denari"
	},
	"MMK": {
		"symbol": "MMK",
		"name": "Myanma Kyat",
		"symbol_native": "K",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "MMK",
		"name_plural": "Myanma kyats"
	},
	"MOP": {
		"symbol": "MOP${'$'}",
		"name": "Macanese Pataca",
		"symbol_native": "MOP${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "MOP",
		"name_plural": "Macanese patacas"
	},
	"MUR": {
		"symbol": "MURs",
		"name": "Mauritian Rupee",
		"symbol_native": "MURs",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "MUR",
		"name_plural": "Mauritian rupees"
	},
	"MXN": {
		"symbol": "MX${'$'}",
		"name": "Mexican Peso",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "MXN",
		"name_plural": "Mexican pesos"
	},
	"MYR": {
		"symbol": "RM",
		"name": "Malaysian Ringgit",
		"symbol_native": "RM",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "MYR",
		"name_plural": "Malaysian ringgits"
	},
	"MZN": {
		"symbol": "MTn",
		"name": "Mozambican Metical",
		"symbol_native": "MTn",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "MZN",
		"name_plural": "Mozambican meticals"
	},
	"NAD": {
		"symbol": "N${'$'}",
		"name": "Namibian Dollar",
		"symbol_native": "N${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "NAD",
		"name_plural": "Namibian dollars"
	},
	"NGN": {
		"symbol": "₦",
		"name": "Nigerian Naira",
		"symbol_native": "₦",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "NGN",
		"name_plural": "Nigerian nairas"
	},
	"NOK": {
		"symbol": "Nkr",
		"name": "Norwegian Krone",
		"symbol_native": "kr",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "NOK",
		"name_plural": "Norwegian kroner"
	},
	"NPR": {
		"symbol": "NPRs",
		"name": "Nepalese Rupee",
		"symbol_native": "नेरू",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "NPR",
		"name_plural": "Nepalese rupees"
	},
	"NZD": {
		"symbol": "NZ${'$'}",
		"name": "New Zealand Dollar",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "NZD",
		"name_plural": "New Zealand dollars"
	},
	"OMR": {
		"symbol": "OMR",
		"name": "Omani Rial",
		"symbol_native": "ر.ع.‏",
		"decimal_digits": 3,
		"rounding": 0,
		"code": "OMR",
		"name_plural": "Omani rials"
	},
	"PAB": {
		"symbol": "B/.",
		"name": "Panamanian Balboa",
		"symbol_native": "B/.",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "PAB",
		"name_plural": "Panamanian balboas"
	},
	"PEN": {
		"symbol": "S/.",
		"name": "Peruvian Nuevo Sol",
		"symbol_native": "S/.",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "PEN",
		"name_plural": "Peruvian nuevos soles"
	},
	"PHP": {
		"symbol": "₱",
		"name": "Philippine Peso",
		"symbol_native": "₱",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "PHP",
		"name_plural": "Philippine pesos"
	},
	"PKR": {
		"symbol": "PKRs",
		"name": "Pakistani Rupee",
		"symbol_native": "₨",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "PKR",
		"name_plural": "Pakistani rupees"
	},
	"PLN": {
		"symbol": "zł",
		"name": "Polish Zloty",
		"symbol_native": "zł",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "PLN",
		"name_plural": "Polish zlotys"
	},
	"PYG": {
		"symbol": "₲",
		"name": "Paraguayan Guarani",
		"symbol_native": "₲",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "PYG",
		"name_plural": "Paraguayan guaranis"
	},
	"QAR": {
		"symbol": "QR",
		"name": "Qatari Rial",
		"symbol_native": "ر.ق.‏",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "QAR",
		"name_plural": "Qatari rials"
	},
	"RON": {
		"symbol": "RON",
		"name": "Romanian Leu",
		"symbol_native": "RON",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "RON",
		"name_plural": "Romanian lei"
	},
	"RSD": {
		"symbol": "din.",
		"name": "Serbian Dinar",
		"symbol_native": "дин.",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "RSD",
		"name_plural": "Serbian dinars"
	},
	"RUB": {
		"symbol": "RUB",
		"name": "Russian Ruble",
		"symbol_native": "₽.",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "RUB",
		"name_plural": "Russian rubles"
	},
	"RWF": {
		"symbol": "RWF",
		"name": "Rwandan Franc",
		"symbol_native": "FR",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "RWF",
		"name_plural": "Rwandan francs"
	},
	"SAR": {
		"symbol": "SR",
		"name": "Saudi Riyal",
		"symbol_native": "ر.س.‏",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "SAR",
		"name_plural": "Saudi riyals"
	},
	"SDG": {
		"symbol": "SDG",
		"name": "Sudanese Pound",
		"symbol_native": "SDG",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "SDG",
		"name_plural": "Sudanese pounds"
	},
	"SEK": {
		"symbol": "Skr",
		"name": "Swedish Krona",
		"symbol_native": "kr",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "SEK",
		"name_plural": "Swedish kronor"
	},
	"SGD": {
		"symbol": "S${'$'}",
		"name": "Singapore Dollar",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "SGD",
		"name_plural": "Singapore dollars"
	},
	"SOS": {
		"symbol": "Ssh",
		"name": "Somali Shilling",
		"symbol_native": "Ssh",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "SOS",
		"name_plural": "Somali shillings"
	},
	"SYP": {
		"symbol": "SY£",
		"name": "Syrian Pound",
		"symbol_native": "ل.س.‏",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "SYP",
		"name_plural": "Syrian pounds"
	},
	"THB": {
		"symbol": "฿",
		"name": "Thai Baht",
		"symbol_native": "฿",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "THB",
		"name_plural": "Thai baht"
	},
	"TND": {
		"symbol": "DT",
		"name": "Tunisian Dinar",
		"symbol_native": "د.ت.‏",
		"decimal_digits": 3,
		"rounding": 0,
		"code": "TND",
		"name_plural": "Tunisian dinars"
	},
	"TOP": {
		"symbol": "T${'$'}",
		"name": "Tongan Paʻanga",
		"symbol_native": "T${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "TOP",
		"name_plural": "Tongan paʻanga"
	},
	"TRY": {
		"symbol": "TL",
		"name": "Turkish Lira",
		"symbol_native": "TL",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "TRY",
		"name_plural": "Turkish Lira"
	},
	"TTD": {
		"symbol": "TT${'$'}",
		"name": "Trinidad and Tobago Dollar",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "TTD",
		"name_plural": "Trinidad and Tobago dollars"
	},
	"TWD": {
		"symbol": "NT${'$'}",
		"name": "New Taiwan Dollar",
		"symbol_native": "NT${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "TWD",
		"name_plural": "New Taiwan dollars"
	},
	"TZS": {
		"symbol": "TSh",
		"name": "Tanzanian Shilling",
		"symbol_native": "TSh",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "TZS",
		"name_plural": "Tanzanian shillings"
	},
	"UAH": {
		"symbol": "₴",
		"name": "Ukrainian Hryvnia",
		"symbol_native": "₴",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "UAH",
		"name_plural": "Ukrainian hryvnias"
	},
	"UGX": {
		"symbol": "USh",
		"name": "Ugandan Shilling",
		"symbol_native": "USh",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "UGX",
		"name_plural": "Ugandan shillings"
	},
	"UYU": {
		"symbol": "$\U",
		"name": "Uruguayan Peso",
		"symbol_native": "${'$'}",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "UYU",
		"name_plural": "Uruguayan pesos"
	},
	"UZS": {
		"symbol": "UZS",
		"name": "Uzbekistan Som",
		"symbol_native": "UZS",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "UZS",
		"name_plural": "Uzbekistan som"
	},
	"VEF": {
		"symbol": "Bs.F.",
		"name": "Venezuelan Bolívar",
		"symbol_native": "Bs.F.",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "VEF",
		"name_plural": "Venezuelan bolívars"
	},
	"VND": {
		"symbol": "₫",
		"name": "Vietnamese Dong",
		"symbol_native": "₫",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "VND",
		"name_plural": "Vietnamese dong"
	},
	"XAF": {
		"symbol": "FCFA",
		"name": "CFA Franc BEAC",
		"symbol_native": "FCFA",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "XAF",
		"name_plural": "CFA francs BEAC"
	},
	"XOF": {
		"symbol": "CFA",
		"name": "CFA Franc BCEAO",
		"symbol_native": "CFA",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "XOF",
		"name_plural": "CFA francs BCEAO"
	},
	"YER": {
		"symbol": "YR",
		"name": "Yemeni Rial",
		"symbol_native": "ر.ي.‏",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "YER",
		"name_plural": "Yemeni rials"
	},
	"ZAR": {
		"symbol": "R",
		"name": "South African Rand",
		"symbol_native": "R",
		"decimal_digits": 2,
		"rounding": 0,
		"code": "ZAR",
		"name_plural": "South African rand"
	},
	"ZMK": {
		"symbol": "ZK",
		"name": "Zambian Kwacha",
		"symbol_native": "ZK",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "ZMK",
		"name_plural": "Zambian kwachas"
	},
	"ZWL": {
		"symbol": "ZWL${'$'}",
		"name": "Zimbabwean Dollar",
		"symbol_native": "ZWL${'$'}",
		"decimal_digits": 0,
		"rounding": 0,
		"code": "ZWL",
		"name_plural": "Zimbabwean Dollar"
	}
}
    
    """.trimIndent()

        val currencyJson = JSONObject(currencyList)
        val defaultCategoryName: HashMap<String, String> = hashMapOf<String, String>().apply {
            put("All Categories", "all_categories")
            put("Food & Beverage","food_and_beverage")
            put("Clothing","clothing")
            put("Shopping","shopping")
            put("Bill & Utilities", "bill_and_utils")
            put("Transportation","transportation")
            put("Education","education")
            put("Entertainment","entertainment")
            put("Housing","housing")
            put("Car / Automotive","car_and_automotive")
            put("Electronics","electronics")
            put("Wine","wine")
            put("Health","health")
            put("Gift","gift")
            put("Office","office")
            put("Furniture","furniture")
            put("Social","social")
            put("Fruit","fruit")
            put("Vegetables","vegetables")
            put("Travel","travel")
            put("Groceries","groceries")
            put("Beauty","beauty")
            put("Tax","tax")
            put("Rent","rent")
            put("Insurance","insurance")
            put("Telephone","telephone")
            put("Pet", "pet")
            put("Sport","sport")
            put("Snack / Fastfood","snack_and_fastfood")
            put("Others","others")
            put("Salary","salary")
            put("Freelancing","freelancing")
            put("Bonus","bonus")
            put("Awards","awards")
            put("Rental","rental")
            put("Grants","grants")
            put("Sale","sale")
            put("Refunds","refunds")
            put("Coupons","coupons")
            put("Lottery","lottery")
            put("Dividends","dividends")
        }
    }



    val categoryImgNameList: ArrayList<String> = arrayListOf(
        "ic_general",
        "ic_beauty",
        "ic_bill",
        "ic_clothing",
        "ic_education",
        "ic_electornics",
        "ic_entertainment",
        "ic_fastfood",
        "ic_food",
        "ic_fruit",
        "ic_furniture",
        "ic_gift",
        "ic_grocceries",
        "ic_health",
        "ic_housing",
        "ic_insurance",
        "ic_office",
        "ic_pet",
        "ic_rent",
        "ic_shopping",
        "ic_social",
        "ic_sport",
        "ic_tax",
        "ic_telephone",
        "ic_transportation",
        "ic_travel",
        "ic_vigetable",
        "ic_wine",
        "ic_animal_shelter",
        "ic_apple",
        "ic_babys_room",
        "ic_barbershop",
        "ic_bios",
        "ic_bitcoin",
        "ic_bra",
        "ic_briefcase",
        "ic_c_drive",
        "ic_cards",
        "ic_champagne",
        "ic_cherry_cheesecake",
        "ic_chipping",
        "ic_christmas_tree",
        "ic_citrus",
        "ic_cocktail_shot",
        "ic_confetti",
        "ic_department",
        "ic_diamond",
        "ic_diamond_ring",
        "ic_dispenser",
        "ic_doctors_folder",
        "ic_dog_park",
        "ic_doughnut_chart",
        "ic_euro_money",
        "ic_fairytale",
        "ic_fund_accounting",
        "ic_game_controller",
        "ic_geometric_flowers",
        "ic_halloween_candy",
        "ic_high_priority",
        "ic_ice_cream_sundae",
        "ic_israeli_parliament",
        "ic_knight_shield",
        "ic_london_cab",
        "ic_minecraft_sword",
        "ic_moleskine",
        "ic_money_box",
        "ic_movie_ticket",
        "ic_muscle",
        "ic_music",
        "ic_nurse",
        "ic_paint_palette",
        "ic_paint_roller",
        "ic_paycheque",
        "ic_people",
        "ic_playlist",
        "ic_plush",
        "ic_shoes",
        "ic_slice_of_watermelon",
        "ic_source_code",
        "ic_spa_flower",
        "ic_tie",
        "ic_usb_memory_stick",
        "ic_warranty"
    )

    val categoryList: ArrayList<Category> = arrayListOf(
        Category(
            color = "#F0831A",
            image = "ic-general"
        ),
        Category(
            color = "#BE7A25",
            image = "ic-beauty"
        ),
        Category(
            color = "#99C24D",
            image = "ic-bill"
        ),
        Category(
            color = "#468FD3",
            image = "ic-clothing"
        ),
        Category(
            color = "#B9BAA3",
            image = "ic-education"
        ),
        Category(
            color = "#D90368",
            image = "ic-electornics"
        ),
        Category(
            color = "#A22C29",
            image = "ic-entertainment"
        ),
        Category(
            color = "#3AFCCB",
            image = "ic-fastfood"
        ),
        Category(
            color = "#FFBA42",
            image = "ic-food"
        ),
        Category(
            color = "#E84855",
            image = "ic-fruit"
        ),
        Category(
            color = "#E53E3E",
            image = "ic-furniture"
        ),
        Category(
            color = "#B61ACE",
            image = "ic-gift"
        ),
        Category(
            color = "#5058CF",
            image = "ic-grocceries"
        ),
        Category(
            color = "#68D391",
            image = "ic-health"
        ),
        Category(
            color = "#79B4A9",
            image = "ic-housing"
        ),
        Category(
            color = "#0827E1",
            image = "ic-insurance"
        ),
        Category(
            color = "#1789FC",
            image = "ic-office"
        ),
        Category(
            color = "#C96667",
            image = "ic-pet"
        ),
        Category(
            color = "#2C3F4F",
            image = "ic-rent"
        ),
        Category(
            color = "#048BA8",
            image = "ic-shopping"
        ),
        Category(
            color = "#E6E338",
            image = "ic-social"
        ),
        Category(
            color = "#196C87",
            image = "ic-sport"
        ),
        Category(
            color = "#AE7E39",
            image = "ic-tax"
        ),
        Category(
            color = "#A74348",
            image = "ic-telephone"
        ),
        Category(
            color = "#2E4057",
            image = "ic-transportation"
        ),
        Category(
            color = "#BCAB79",
            image = "ic-travel"
        ),
        Category(
            color = "#315659",
            image = "ic-vigetable"
        ),
        Category(
            color = "#9CFC97",
            image = "ic-wine"
        ),
        Category(
            color = "#1B71F2",
            image = "ic-animal-shelter"
        ),
        Category(
            color = "#FFB028",
            image = "ic-apple"
        ),
        Category(
            color = "#00A8D6",
            image = "ic-babys-room"
        ),
        Category(
            color = "#2A9D69",
            image = "ic-barbershop"
        ),
        Category(
            color = "#20CAB6",
            image = "ic-bios"
        ),
        Category(
            color = "#9747FF",
            image = "ic-bitcoin"
        ),
        Category(
            color = "#CC3399",
            image = "ic-bra"
        ),
        Category(
            color = "#FF73D0",
            image = "ic-briefcase"
        ),
        Category(
            color = "#E595CB",
            image = "ic-c-drive"
        ),
        Category(
            color = "#CD1F1F",
            image = "ic-cards"
        ),
        Category(
            color = "#FF303F",
            image = "ic-champagne"
        ),
        Category(
            color = "#F2526D",
            image = "ic-cherry-cheesecake"
        ),
        Category(
            color = "#B85800",
            image = "ic-chipping"
        ),
        Category(
            color = "#AE8642",
            image = "ic-christmas-tree"
        ),
        Category(
            color = "#C2AB84",
            image = "ic-citrus"
        ),
        Category(
            color = "#FFAC1C",
            image = "ic-cocktail-shot"
        ),
        Category(
            color = "#E5B663",
            image = "ic-confetti"
        ),
        Category(
            color = "#83CC4B",
            image = "ic-department"
        ),
        Category(
            color = "#A2AD1E",
            image = "ic-diamond"
        ),
        Category(
            color = "#D8C625",
            image = "ic-diamond-ring"
        ),
        Category(
            color = "#1B71F2",
            image = "ic-dispenser"
        ),
        Category(
            color = "#E8F1FE",
            image = "ic-doctors-folder"
        ),
        Category(
            color = "#2A9D8F",
            image = "ic-dog-park"
        ),
        Category(
            color = "#B85800",
            image = "ic-doughnut-chart"
        ),
        Category(
            color = "#AE8642",
            image = "ic-euro-money"
        ),
        Category(
            color = "#391AFA",
            image = "ic-fairytale"
        ),
        Category(
            color = "#3E38F5",
            image = "ic-fund-accounting"
        ),
        Category(
            color = "#1B71F2",
            image = "ic-game-controller"
        ),
        Category(
            color = "#00A8D6",
            image = "ic-geometric-flowers"
        ),
        Category(
            color = "#2A9D69",
            image = "ic-halloween-candy"
        ),
        Category(
            color = "#20CAB6",
            image = "ic-high-priority"
        ),
        Category(
            color = "#9747FF",
            image = "ic-ice-cream-sundae"
        ),
        Category(
            color = "#CC3399",
            image = "ic-israeli-parliament"
        ),
        Category(
            color = "#FF73D0",
            image = "ic-knight-shield"
        ),
        Category(
            color = "#E595CB",
            image = "ic-london-cab"
        ),
        Category(
            color = "#CD1F1F",
            image = "ic-minecraft-sword"
        ),
        Category(
            color = "#FF303F",
            image = "ic-moleskine"
        ),
        Category(
            color = "#F2526D",
            image = "ic-money-box"
        ),
        Category(
            color = "#B85800",
            image = "ic-movie-ticket"
        ),
        Category(
            color = "#AE8642",
            image = "ic-muscle"
        ),
        Category(
            color = "#C2AB84",
            image = "ic-music"
        ),
        Category(
            color = "#FFAC1C",
            image = "ic-nurse"
        ),
        Category(
            color = "#E5B663",
            image = "ic-paint-palette"
        ),
        Category(
            color = "#83CC4B",
            image = "ic-paint-roller"
        ),
        Category(
            color = "#A2AD1E",
            image = "ic-paycheque"
        ),
        Category(
            color = "#D8C625",
            image = "ic-people"
        ),
        Category(
            color = "#1B71F2",
            image = "ic-playlist"
        ),
        Category(
            color = "#E8F1FE",
            image = "ic-plush"
        ),
        Category(
            color = "#2A9D8F",
            image = "ic-shoes"
        ),
        Category(
            color = "#B85800",
            image = "ic-slice-of-watermelon"
        ),
        Category(
            color = "#391AFA",
            image = "ic-source-code"
        ),
        Category(
            color = "#3E38F5",
            image = "ic-spa-flower"
        ),
        Category(
            color = "#1B71F2",
            image = "ic-tie"
        ),
        Category(
            color = "#00A8D6",
            image = "ic-usb-memory-stick"
        ),
        Category(
            color = "#2A9D69",
            image = "ic-warranty"
        )


    )
    val sliderList = listOf<Slider>(
        Slider(
            img = R.drawable.premium_slider_no_ads,
            title = "Remove all ADs",
            description = "No more interruption by advertisement"
        ),
        Slider(
            img = R.drawable.premium_slider_unlimited_wallet,
            title = "Unlimited Wallet",
            description = "You can have more than 2 wallets"
        ),
        Slider(
            img = R.drawable.premium_slider_import_trans,
            title = "Import Transaction",
            description = "Import transactions from CSV files and Excel spreadsheets."
        ),
        Slider(
            img = R.drawable.premium_slider_share_wallet,
            title = "Share Wallet",
            description = "Add your member to your wallet"
        ),
        Slider(
            img = R.drawable.premium_slider_upcoming_features,
            title = "Upcoming Feature",
            description = "Get all upcoming features for free"
        )
    )

    val defaultExpenseList = listOf<Category>(
        Category(
            color = "#FFBA42",
            image = "ic-food",
            selectedCount = 1,
            title = "Food & Beverage",
            initialName = "Food & Beverage-ic-food-expense"
        ),
        Category(
            color = "#468FD3",
            image = "ic-clothing",
            selectedCount = 1,
            title = "Clothing",
            initialName = "Clothing-ic-clothing-expense"
        ),
        Category(
            color = "#048BA8",
            image = "ic-shopping",
            selectedCount = 1,
            title = "Shopping",
            initialName = "Shopping-ic-shopping-expense"
        ),
        Category(
            color = "#99C24D",
            image = "ic-bill",
            selectedCount = 1,
            title = "Bill & Utilities",
            initialName = "Bill & Utilities-ic-bill-expense"
        ),
        Category(
            color = "#2E4057",
            image = "ic-transportation",
            selectedCount = 1,
            title = "Transportation",
            initialName = "Transportation-ic-transportation-expense"
        ),
        Category(
            color = "#B9BAA3",
            image = "ic-education",
            selectedCount = 0,
            title = "Education",
            initialName = "Education-ic-education-expense"
        ),
        Category(
            color = "#A22C29",
            image = "ic-entertainment",
            selectedCount = 0,
            title = "Entertainment",
            initialName = "Entertainment-ic-entertainment-expense"
        ),
        Category(
            color = "#79B4A9",
            image = "ic-housing",
            selectedCount = 0,
            title = "Housing",
            initialName = "Housing-ic-housing-expense"
        ),
        Category(
            color = "#676F54",
            image = "ic-automotive",
            selectedCount = 0,
            title = "Car / Automotive",
            initialName = "Car / Automotive-ic-automotive-expense"
        ),
        Category(
            color = "#D90368",
            image = "ic-electornics",
            selectedCount = 0,
            title = "Electronics",
            initialName = "Electronics-ic-electornics-expesne"
        ),
        Category(
            color = "#9CFC97",
            image = "ic-wine",
            selectedCount = 0,
            title = "Wine",
            initialName = "Wine-ic-wine-expense"
        ),
        Category(
            color = "#68D391",
            image = "ic-health",
            selectedCount = 0,
            title = "Health",
            initialName = "Health-ic-health-expense"
        ),
        Category(
            color = "#B61ACE",
            image = "ic-gift",
            selectedCount = 0,
            title = "Gift",
            initialName = "Gift-ic-gift-expense"
        ),
        Category(
            color = "#1789FC",
            image = "ic-office",
            selectedCount = 0,
            title = "Office",
            initialName = "Office-ic-office-expense"
        ),
        Category(
            color = "#E53E3E",
            image = "ic-furniture",
            selectedCount = 0,
            title = "Furniture",
            initialName = "Furniture-ic-furniture-expense"
        ),
        Category(
            color = "#E6E338",
            image = "ic-social",
            selectedCount = 0,
            title = "Social",
            initialName = "Social-ic-social-expense"
        ),
        Category(
            color = "#E84855",
            image = "ic-fruit",
            selectedCount = 0,
            title = "Fruit",
            initialName = "Fruit-ic-fruit-expense"
        ),
        Category(
            color = "#315659",
            image = "ic-vigetable",
            selectedCount = 0,
            title = "Vegetables",
            initialName = "Vegetables-ic-vigetable-expense"
        ),
        Category(
            color = "#BCAB79",
            image = "ic-travel",
            selectedCount = 0,
            title = "Travel",
            initialName = "Travel-ic-travel-expense"
        ),
        Category(
            color = "#5058CF",
            image = "ic-grocceries",
            selectedCount = 0,
            title = "Groceries",
            initialName = "Groceries-ic-grocceries-expense"
        ),
        Category(
            color = "#BE7A25",
            image = "ic-beauty",
            selectedCount = 0,
            title = "Beauty",
            initialName = "Beauty-ic-beauty-expense"
        ),
        Category(
            color = "#AE7E39",
            image = "ic-tax",
            selectedCount = 0,
            title = "Tax",
            initialName = "Tax-ic-tax-expense"
        ),
        Category(
            color = "#2C3F4F",
            image = "ic-rent",
            selectedCount = 0,
            title = "Rent",
            initialName = "Rent-ic-rent-expense"
        ),
        Category(
            color = "#0827E1",
            image = "ic-insurance",
            selectedCount = 0,
            title = "Insurance",
            initialName = "Insurance-ic-insurance-expense"
        ),
        Category(
            color = "#A74348",
            image = "ic-telephone",
            selectedCount = 0,
            title = "Telephone",
            initialName = "Telephone-ic-telephone-expense"
        ),
        Category(
            color = "#C96667",
            image = "ic-pet",
            selectedCount = 0,
            title = "Pet",
            initialName = "Pet-ic-pet-expense"
        ),
        Category(
            color = "#196C87",
            image = "ic-sport",
            selectedCount = 0,
            title = "Sport",
            initialName = "Sport-ic-sport-expense"
        ),
        Category(
            color = "#3AFCCB",
            image = "ic-fastfood",
            selectedCount = 0,
            title = "Snack / Fastfood",
            initialName = "Snack / Fastfood-ic-fastfood-expense"
        ),
        Category(
            color = "#F0831A",
            image = "ic-general",
            selectedCount = 0,
            title = "Others",
            initialName = "Others-ic-general-expense"
        )

    )

    val defaultIncomeList = listOf<Category>(
        Category(
            color = "#FFBA42",
            image = "ic-salary",
            selectedCount = 1,
            title = "Salary",
            initialName = "Salary-ic-salary-income"
        ),
        Category(
            color = "#468FD3",
            image = "ic-freelance",
            selectedCount = 1,
            title = "Freelancing",
            initialName = "Freelancing-ic-freelance-income"
        ),
        Category(
            color = "#048BA8",
            image = "ic-bonus",
            selectedCount = 1,
            title = "Bonus",
            initialName = "Bonus-ic-bonus-income"
        ),
        Category(
            color = "#99C24D",
            image = "ic-awards",
            selectedCount = 1,
            title = "Awards",
            initialName = "Awards-ic-awards-income"
        ),
        Category(
            color = "#2E4057",
            image = "ic-rental",
            selectedCount = 1,
            title = "Rental",
            initialName = "Rental-ic-rental-income"
        ),
        Category(
            color = "#B9BAA3",
            image = "ic-grants",
            selectedCount = 0,
            title = "Grants",
            initialName = "Grants-ic-grants-income"
        ),
        Category(
            color = "#A22C29",
            image = "ic-sale",
            selectedCount = 0,
            title = "Sale",
            initialName = "Sale-ic-sale-income"
        ),
        Category(
            color = "#79B4A9",
            image = "ic-refund",
            selectedCount = 0,
            title = "Refunds",
            initialName = "Refunds-ic-refund-income"
        ),
        Category(
            color = "#676F54",
            image = "ic-coupon",
            selectedCount = 0,
            title = "Coupons",
            initialName = "Coupons-ic-coupon-income"
        ),
        Category(
            color = "#D90368",
            image = "ic-lottery",
            selectedCount = 0,
            title = "Lottery",
            initialName = "Lottery-ic-lottery-income"
        ),
        Category(
            color = "#9CFC97",
            image = "ic-dividend",
            selectedCount = 0,
            title = "Dividends",
            initialName = "Dividends-ic-dividend-income"
        ),
        Category(
            color = "#F0831A",
            image = "ic-general",
            selectedCount = 0,
            title = "Others",
            initialName = "Others-ic-general-income"
        )
    )

    val allWallet: io.paraga.moneytrackerdev.models.Wallet = io.paraga.moneytrackerdev.models.Wallet(
        name = "All Wallet",
        symbol = "globe"
    )

    val languageList = listOf<Language>(
        Language(
            "en",
            "English",
            "English(US)"
        ),
        Language(
            "de",
            "Deutsch",
            "German"
        ),
        Language(
            "fr",
            "Français",
            "French"
        ),
        Language(
            "in",
            "Bahasa Indonesia",
            "Indonesian"
        ),
        Language(
            "it",
            "Italiano",
            "Italian"
        ),
        Language(
            "ja",
            "日本語",
            "Japanese"
        ),
        Language(
            "km",
            "ភាសាខ្មែរ",
            "Khmer"
        ),
        Language(
            "ko",
            "한국어",
            "Korean"
        ),
        Language(
            "nl",
            "Netherlands",
            "Dutch"
        ),
        Language(
            "no",
            "norsk språk",
            "Norwegian"
        ),
        Language(
            "pt-rBR",
            "idioma portugues",
            "Portuguese"
        ),
        Language(
            "sv",
            "Svenska",
            "Swedish"
        ),
        Language(
            "th",
            "ภาษาไทย",
            "Thai"
        ),
        Language(
            "tr",
            "Türk Dili",
            "Turkish"
        ),
        Language(
            "vi",
            "ngôn ngữ tiếng Việt",
            "Vietnamese"
        ),
        Language(
            "zh-rCN",
            "简体中文",
            "Chinese(Simplified)"
        ),
        Language(
            "zh-rTW",
            "繁體中文",
            "Chinese(Traditional)"
        ),
    )



}
