package io.paraga.moneytrackerdev

class Enums {


    enum class Languages(val value: String) {
       KH("kh"),
       EN("en")
    }

    enum class Themes(val value: String) {
        DARK("dark"),
        LIGHT("light")
    }

    enum class Errors(val value: String) {
        COULD_NOT_ACCESS_TO_API("Could not access to api"),
        COULD_NOT_DECODE_DATA("Could not decode data"),
        BAD_REQUEST("Bad request"),
        FAIL_LOGIN("Wrong email or password")

    }

    enum class ResponseCodes(val value: String) {
        SUCCESS_API("200"),
        SUCCESS("000")
    }

    enum class Extras(val value: String){
        EMAIL("email"),
        IS_FROM_PW_LOGIN("is from password screen?"),
        AMOUNT("amount"),
        CURRENCY("currency"),
        MILLISECOND("milliseconds"),
        REMARK("remark"),
        TYPE("type"),
        USER_ID("userid"),
        WALLET_ID("walletID"),
        DOC_ID("documentId"),
        CATEGORY_COLOR("categoryColor"),
        CATEGORY_IMG("categoryImage"),
        SELECTED_COUNT("selectedCount"),
        CATEGORY_TITLE("categoryTitle"),
        IS_EDIT_TRANS("isEditTrans"),
        IS_EDIT_BUDGET("isEditBudget"),
        IS_FROM_BUDGET("isFromBudget"),
        NOTIFICATION_ID("notificationId"),
        CURRENT_MONTH_YEAR("currentMonthYear")
    }


    enum class DB(val value: String) {
        USERS_COLLECTION("Users"),
        CATEGORIES_COLLECTION("Categories"),
        TRANSACTIONS_COLLECTION("Transactions"),
        WALLETS_COLLECTION("Wallets"),
        BUDGETS_COLLECTION("Budgets"),
        DEFAULT_CUR_FIELD("defaultCurrency"),
        DEFAULT_WALLET_ID_FIELD("defaultWallet"),
        DISPLAY_NAME_FIELD("displayName"),
        EMAIL_FIELD("email"),
        USER_ID_FIELD("userid"),
        WALLETS_FIELD("wallets"),
        ARCHIVE_FIELD("archive"),
        COLOR_FIELD("color"),
        CREATED_TIME_FIELD("createdTime"),
        CURRENCY_FIELD("currency"),
        EXCLUDE_FIELD("exclude"),
        WALLET_NAME_FIELD("name"),
        WALLET_SYMBOL_FIELD("symbol"),
        DEFAULT_WALLET_COLOR("#FFB028"),
        EXPENSE_FIELD("Expense"),
        INCOME_FIELD("Income"),
        IMAGE_FIELD("image"),
        SELECTED_COUNT_FIELD("selectedCount"),
        TITLE_FIELD("title"),
        CATEGORIES_FIELD("categories"),
        WALLET_ID_FIELD("walletID"),
        AMOUNT_FIELD("amount"),
        CATEGORY_FIELD("category"),
        REMARK_FIELD("remark"),
        TYPE_FIELD("type"),
        DATE_FIELD("date"),
        PERIOD_FIELD("period"),
        WALLET_FIELD("wallet"),
        SHARED_WITH("sharedWith"),
        NOTIFICATION_COLLECTION("Notifications"),
        TOKEN_FIELD("token"),
        IS_ACTION_TAKEN("isActionTaken"),
        DATA_FIELD("data"),
        IS_READ("isRead"),
        ARCHIVE("Archive")
    }

    enum class Default(val value: String) {
        DEFAULT_CURRENCY("USD"),
        DEFAULT_EXCLUDE("false"),
        DEFAULT_WALLET_NAME("Personal"),
        DEFAULT_SYMBOL("ic_all_wallet"),
    }


    enum class Messages(val value:String) {
        SIGN_OUT_SUCCESS_MESSAGE("Sign out successfully"),
        SENT_RESET_LINK("Reset link has been sent to your email."),
        ERROR_SEND_RESET_LINK("Error, reset link could not be sent."),
        INVALID_EMAIL("Invalid email address."),
        INVALID_PW("Invalid password."),
        INVALID_INPUT("Invalid inputs."),
        SOMETHING_WENT_WRONG("Something went wrong."),
        PASSWORD_IS_NOT_MATCH("Password is not matched."),
        PASSWORD_LENGTH("Password must be equal or greater than 8 characters."),
        WALLET_NOT_FOUND("Wallet not found"),
        FAILED_TO_FETCH_TRANS("Failed to fetch transaction."),
        DEFAULT_WALLET_CANNOT_BE_DELETED("Default wallet cannot be deleted!"),
        INVALID_TRANS_AMOUNT("Transaction amount must be greater than zero!"),
        NOTHING_CHANGE("Nothing has changed"),
        UPDATED_MAIN_CURRENCY("Main currency has been updated successfully!"),
        PASSWORD_UPDATED_SUCCESSFULLY("Password has been updated successfully!"),
        FAILED_TO_UPDATE_PW("Failed to update the password!"),
        EMAIL_CHANGED_SUCCESS("Email has been changed successfully."),
        FAILED_TO_CHANGE_EMAIL("Failed to change email!"),
        SUBSCRIPTION_RESTORE_SUCCESS("Subscription has been restore successfully!"),
        FAILED_TO_RESTORE_SUBSCRIPTION("Failed to restore the subscription!"),
        PREMIUM_UNLOCKED_SUCCESSULLY("Premium feature has been unlocked successfully!"),
        THREE_DAY_UNOPENED_ALERT_MESSAGE("You haven't added a transaction in a while. Start adding now to track your spending.")
    }

    enum class SharePref(val value: String) {
        LOGIN_TYPE("Login type"),
        GOOGLE("Google"),
        EMAIL_AND_PW("Email and password"),
        EXCHANGE_RATE("Exchange rate"),
        RECENT_CURRENCY("Recent currency"),
        IS_FIRST_OPEN("Is first open?"),
        POP_UP_DATE("pop up date"),
        SELECTED_WALLET_ID("Selected wallet id"),
        THEME("Theme"),
        AUTO_LANGUAGE("Auto Language"),
        LANGUAGE_CODE("Language Code"),
        WEEK_START("Week Start"),
        IMPORT_COUNT("import_count"),
        CURRENT_VERSION("current_version"),
        LATEST_NUMBER_OF_NOTIFICATION("Latest number of notification"),
        BUDGET_VIEW_TYPE("Budget view type"),
        PREVIOUS_SUBSCRIPTION_STATUS("Previous subscription status"),
        CHOSEN_TWO_WALLET_IDS("Chosen two wallet ids"),
        TWO_WALLET_IDS_CHOOSE_STATUS("Two wallet ids choose status"),
        CHOSEN_TWO_BUDGET_IDS("Chosen two budget ids"),
        TWO_BUDGET_IDS_CHOOSE_STATUS("Two budget ids choose status")
    }

    enum class DialogType(val value: Int) {
        TRANSACTION(1),
        WALLET(2),
        CATEGORY(3),
        LOG_OUT(4),
        ACCOUNT(5),
        PASSWORD(6),
        DELETE(7),
        RESET_CATEGORY(8),
        BUDGET(9),
        RETURN_PREVIOUS(10),
        Notification(11),
        REMOVE_USER(12),
        IMPORT(13)
    }

    enum class General(val value: String) {
        ADJUST_BALANCE("Adjust Balance"),
        IC_ALL_WALLET("ic_all_wallet"),
        INCOME("income"),
        EXPENSE("expense"),
        ANNUAL("ANNUAL"),
        MONTHLY("MONTHLY"),
        YEAR("Year"),
        MONTH("Month"),
        TODAY("Today"),
        YESTERDAY("Yesterday"),
        TOMORROW("Tomorrow"),
        DELETE("delete"),
        TOTAL_BUDGET_AMOUNT("Total Budget Amount"),
        BUDGET_TRANS_LIST("Budget Trans List"),
        ALL_CATEGORIES("All Categories"),
        MONDAY("Monday"),
        SUNDAY("Sunday")
    }

    enum class Currencies(val value: String) {
        USD("USD")
    }

    enum class TransTypes(val value: Int) {
        INCOME(0),
        EXPENSE(1),
        ALL(2)
    }

    enum class ResourceName(val value: String) {
        GLOBE("globe")
    }

    enum class FragmentType(val value: Int) {
        TRANS(0),
        CALENDAR(1),
        BUDGET(2),
        STATISTICS(3)
    }

    enum class Period(val value: Int) {
        MONTHLY(1)
    }

    enum class SpanCount(val value: Int) {
        ONE(1),
        TWO(2)
    }

    enum class NotificationType(val value: Int) {
        NORMAL(0),
        WALLET_INVITATION(1),
        NO_ACTION(2)
    }

    enum class InvitationType(val value: Int) {
        REJECTED(0),
        ACCEPTED(1),
        PENDING(2)
    }

    enum class ViewType(val value: Int) {
        TRANS_DROP_DOWN(0),
        TRANS_CARD(1)
    }
}