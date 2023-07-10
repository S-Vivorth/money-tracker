package io.paraga.moneytrackerdev.networks

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import io.paraga.moneytrackerdev.models.Categories
import io.paraga.moneytrackerdev.models.Category
import io.paraga.moneytrackerdev.models.NestedTransaction
import io.paraga.moneytrackerdev.models.Transaction
import io.paraga.moneytrackerdev.Enums
import io.paraga.moneytrackerdev.R
import io.paraga.moneytrackerdev.constants.Constants
import io.paraga.moneytrackerdev.models.*
import io.paraga.moneytrackerdev.utils.helper.Extension
import io.paraga.moneytrackerdev.views.currentMonthYear
import io.paraga.moneytrackerdev.views.selectedWalletId
import io.paraga.moneytrackerdev.utils.helper.Preferences
import io.paraga.moneytrackerdev.views.isProUser
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.ConcurrentModificationException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


var user: User? = null
lateinit var currentUser: FirebaseUser
lateinit var transaction: Transaction
var nestedTransList: MutableLiveData<CopyOnWriteArrayList<NestedTransaction>> = MutableLiveData(CopyOnWriteArrayList())
var allNestedTransList: MutableLiveData<ArrayList<NestedTransaction>> = MutableLiveData(ArrayList())
var totalAmountByCategory: HashMap<String?, HashMap<String, Double>> = HashMap()
var incomeTransList: MutableLiveData<ArrayList<NestedTransaction>> = MutableLiveData()
var expenseTransList: MutableLiveData<ArrayList<NestedTransaction>> = MutableLiveData()
var nestedTransMapByMonth: HashMap<String, ArrayList<NestedTransaction>> = HashMap()
var nestedTransMapByWallet: HashMap<String, ArrayList<NestedTransaction>> = HashMap()
var walletList: MutableLiveData<ArrayList<WalletTrans>> = MutableLiveData(ArrayList())
var incomeModel: MutableLiveData<ArrayList<Category>> = MutableLiveData(ArrayList())
var expenseModel: MutableLiveData<ArrayList<Category>> = MutableLiveData(ArrayList())
var archivedCategoryModel: MutableLiveData<ArrayList<Category>> = MutableLiveData(ArrayList())
var mergeCategories: ArrayList<Category> = ArrayList()
var mostFrequentCategories: ArrayList<Category> = ArrayList()
var totalBalance: Double = 0.0
var transList = MutableLiveData<ArrayList<Transaction>>(ArrayList())
var transListCopy = MutableLiveData<ArrayList<Transaction>>()
var nestedTransListCopy: MutableLiveData<ArrayList<NestedTransaction>> = MutableLiveData()
var selectedWallet: Wallet = Wallet(name = "All Wallet", symbol = "globe")
var budgetList: MutableLiveData<ArrayList<Budget>> = MutableLiveData(ArrayList())
var transMapByMonthAndCategory: HashMap<String?, List<Transaction>> = HashMap()
var walletIdList: kotlin.collections.ArrayList<String> = ArrayList()
var notificationList: MutableLiveData<kotlin.collections.ArrayList<Notification>> = MutableLiveData(
    ArrayList()
)
var categories: Categories = Categories()
var categoriesList: ArrayList<Categories> = ArrayList()
var categoryUserIdList: ArrayList<String> = ArrayList()
@SuppressLint("SimpleDateFormat")
class FirebaseManager(val context: Context) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    lateinit var transCollectionRef: CollectionReference
    var getTransListThread: Thread? = null

    init {
        if (user == null) {
            initUser {

            }
        }
    }


    fun initUser(onSuccess: () -> Unit) {
        try {
            currentUser = auth.currentUser!!
        }
        catch (exc: Exception) {
            return
        }
        firestore.collection(Enums.DB.USERS_COLLECTION.value).document(auth.currentUser?.uid!!)
            .get().addOnCompleteListener {
                if (it.isSuccessful) {
                    user = it.result.toObject(User::class.java) as User?
                    onSuccess()
                }
            }
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun initTransaction(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        fun initTransList() {
            // set transList to empty

            firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
                .whereIn(Enums.DB.WALLET_ID_FIELD.value, walletIdList)
                .addSnapshotListener { value, error ->
//                    transList.postValue(ArrayList())
                    transList.value!!.clear()
                    fun getTranList(onCompleted: () -> Unit) {
                        var counter = 0
                        value?.documents?.forEach { snapshot ->
                            val trans = Transaction(
                                amount = (snapshot.get("amount") as Number).toDouble(),
                                date = snapshot.get("date") as Timestamp,
                                category = Category(
                                    color = (snapshot.get("category") as HashMap<*, *>)["color"] as String,
                                    image = (snapshot.get("category") as HashMap<*, *>)["image"] as String,
                                    selectedCount = ((snapshot.get("category") as HashMap<*, *>)["selectedCount"] as Long).toInt(),
                                    title = (snapshot.get("category") as HashMap<*, *>)["title"] as String,
                                    initialName = (snapshot.get("category") as HashMap<*, *>)["initialName"].toString() ?: "",
                                ),
                                currency = snapshot.get("currency") as String,
                                remark = snapshot.get("remark") as String,
                                type = (snapshot.get("type") as Long).toInt(),
                                walletID = snapshot.get("walletID") as String,
                                documentId = snapshot.id,
                                userid = snapshot.get("userid") as String
                            )
                            val archivedCategories = categoriesList.find { it.userid == trans.userid }?.categories?.get(Enums.DB.ARCHIVE.value) ?: ArrayList()

                            if (trans.type == Enums.TransTypes.INCOME.value) {
                                val incomeCategories = categoriesList.find { it.userid == trans.userid }?.categories?.get(Enums.DB.INCOME_FIELD.value)
                                if (incomeCategories?.find { it.initialName == trans.category.title || it.initialName == trans.category.initialName} != null) {
                                    trans.category = incomeCategories.find { it.initialName ==trans.category.title || it.initialName == trans.category.initialName}!!
                                }
                                else {
                                    if (archivedCategories.find { it.initialName == trans.category.title || it.initialName == trans.category.initialName} != null) {
                                        trans.category = archivedCategories.find { it.initialName ==trans.category.title || it.initialName == trans.category.initialName}!!
                                    }
                                }
                            }
                            else {
                                val expenseCategories = categoriesList.find { it.userid == trans.userid }?.categories?.get(Enums.DB.EXPENSE_FIELD.value)
                                if (expenseCategories?.find { it.initialName == trans.category.title || it.initialName == trans.category.initialName} != null) {
                                    trans.category = expenseCategories.find { it.initialName ==trans.category.title || it.initialName == trans.category.initialName}!!
                                }
                                else {
                                    if (archivedCategories.find { it.initialName == trans.category.title || it.initialName == trans.category.initialName} != null) {
                                        trans.category = archivedCategories.find { it.initialName ==trans.category.title || it.initialName == trans.category.initialName}!!
                                    }
                                }
                            }



                            transList.value!!.add(
                                trans
                            )
                            counter += 1
                        }
                        //check if loop is completed
                        if (counter == value?.documents?.size) {
                            onCompleted()
                        }
                    }

                    getTranList {
                        updateWalletList()
                        initNestedTransByMonth {
                            onSuccess()
                        }
                    }


//                    if (getTransListThread != null) {
//                        if (getTransListThread?.isAlive == true) {
//                            getTransListThread?.interrupt()
//                        }
//                        getTransListThread = Thread {
//                            kotlin.run {
//                                getTranList {
//                                    updateWalletList()
//                                    initNestedTransByMonth {
//                                        onSuccess()
//                                    }
//                                }
//                            }
//                        }
//                        getTransListThread?.start()
//                    }
//                    else {
//                        getTransListThread = Thread {
//                            kotlin.run {
//                                getTranList {
//                                    updateWalletList()
//                                    initNestedTransByMonth {
//                                        onSuccess()
//                                    }
//                                }
//                            }
//                        }
//                        getTransListThread?.start()
//                    }

                }
//            firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
//                .whereEqualTo(Enums.DB.USER_ID_FIELD.value, auth.currentUser?.uid)
//                .get()
//                .addOnCompleteListener {
//                    if (it.isSuccessful) {
//                        val result = it.result
//                        fun getTranList(onCompleted: () -> Unit) {
//                            var counter = 0
//                            result?.documents?.forEach { snapshot ->
//                                transList.value!!.add(
//                                    Transaction(
//                                        amount = (snapshot.get("amount") as Number).toDouble(),
//                                        date = snapshot.get("date") as Timestamp,
//                                        category = Category(
//                                            color = (snapshot.get("category") as HashMap<*, *>)["color"] as String,
//                                            image = (snapshot.get("category") as HashMap<*, *>)["image"] as String,
//                                            selectedCount = ((snapshot.get("category") as HashMap<*, *>)["selectedCount"] as Long).toInt(),
//                                            title = (snapshot.get("category") as HashMap<*, *>)["title"] as String
//                                        ),
//                                        currency = snapshot.get("currency") as String,
//                                        remark = snapshot.get("remark") as String,
//                                        type = (snapshot.get("type") as Long).toInt(),
//                                        walletID = snapshot.get("walletID") as String,
//                                        documentId = snapshot.id
//                                    )
//                                )
//                                counter += 1
//
//                                //check if loop is completed
//                                if (counter == result.documents.size) {
//                                    onCompleted()
//                                }
//                            }
//                        }
//                        getTranList {
//                            initNestedTransByMonth{
//                                onSuccess()
//                            }
//                        }
//                    }
//                }

            transListCopy.observeForever {
                initNestedTransByMonth {
                    getNestedTransList(
                        currentMonthYear.value.toString(), selectedWalletId,
                        onSuccess = {
                        })
                }
            }
        }
        try {
            if (user == null) {
                initUser {

                    initTransList()
                }
            } else {
                initTransList()
            }
        }
        catch (exc: Exception) {
            Log.d("exc", exc.toString())
            onFailure(Enums.Messages.SOMETHING_WENT_WRONG.value)
            Thread.currentThread().interrupt()
        }

    }


    fun updateWalletList() {
        if (walletList.value?.size != 0) {
            walletList.value?.distinct()?.forEach { walletSnapshot ->
                var balance = 0.0
                transList.value?.forEach { trans ->
                    if (trans.walletID.toString() == walletSnapshot.walletId) {
                        if (trans.type == Enums.TransTypes.EXPENSE.value) {
                            if (walletSnapshot.wallet.currency != trans.currency) {
                                balance -= Extension.convertCurrency(
                                    trans.currency.toString(),
                                    walletSnapshot.wallet.currency.toString(),
                                    trans.amount?.toDouble() ?: 0.0, context
                                )
                            } else {
                                balance -= trans.amount?.toDouble() ?: 0.0
                            }
                        } else {
                            if (walletSnapshot.wallet.currency != trans.currency) {
                                balance += Extension.convertCurrency(
                                    trans.currency.toString(),
                                    walletSnapshot.wallet.currency as String,
                                    trans.amount?.toDouble() ?: 0.0, context
                                )
                            } else {
                                balance += trans.amount?.toDouble() ?: 0.0
                            }
                        }
                    }
                }
                walletSnapshot.balance = balance
            }
            walletList.postValue(ArrayList(walletList.value!!.distinct()))
        }
    }

    private fun initNestedTransByMonth(onSuccess: () -> Unit) {
        totalAmountByCategory = HashMap()
        nestedTransMapByMonth = HashMap()
        allNestedTransList.value?.clear()
        val transMap = transList.value!!.distinct().groupBy {
            SimpleDateFormat("MMMM yyyy").format(it.date?.toDate() ?: Date())
        }.toSortedMap()
        for ((key, value) in transMap) {

            // map total amount by category
            val transMapByCategory = transList.value!!.distinct().groupBy {
                it.category.title
            }
            transMapByMonthAndCategory = HashMap(
                transList.value!!.distinct().groupBy {
                    it.category.title
                }
            )

            val nestedTransListTemp: ArrayList<NestedTransaction> = ArrayList()
            val totalAmountByCategoryMap: HashMap<String?, HashMap<String, Double>> = HashMap()
            for ((key, value) in transMapByCategory) {
                var totalIncome = 0.0
                var totalExpense = 0.0
                value.forEach {
                    val convertedAmount = Extension.convertCurrency(
                        it.currency.toString(),
                        selectedWallet.currency.toString(),
                        it.amount!!.toDouble(),
                        context
                    )
                    if (it.currency == selectedWallet.currency) {
                        if (it.type == Enums.TransTypes.INCOME.value) {
                            totalIncome += convertedAmount
                        } else {
                            totalExpense += convertedAmount
                        }
                    } else {
                        if (it.type == Enums.TransTypes.INCOME.value) {
                            totalIncome += convertedAmount
                        } else {
                            totalExpense += convertedAmount
                        }
                    }
//                    if (it.type == Enums.TransTypes.INCOME.value) {
//                        if (incomeModel.value?.find {category ->
//                                category.initialName == it.category.title} != null) {
//                            it.category = incomeModel.value?.find { category -> category.initialName ==it.category.title}!!
//                        }
//                    }
//                    else {
//                        if (expenseModel.value?.find {category ->
//                                category.initialName == it.category.title} != null) {
//                            it.category = expenseModel.value?.find {category ->
//                                category.initialName == it.category.title}!!
//                        }
//                    }

                }
                val categoryMap: HashMap<String, Double> = HashMap()
                categoryMap.put(Enums.General.INCOME.value, totalIncome)
                categoryMap.put(Enums.General.EXPENSE.value, totalExpense)
                calculateTotalAmountByCategory(key.toString(), totalIncome, totalExpense)
                totalAmountByCategoryMap.put(key, categoryMap)
            }

            val transMapByDay = value.groupBy {
                SimpleDateFormat("dd MMM yyyy").format(it.date?.toDate() ?: Date())
            }.toSortedMap(reverseOrder())
            for ((key, value) in transMapByDay) {
                var totalAmount: Double = 0.0
                var totalIncome = 0.0
                var totalExpense = 0.0

                value.forEach {
                    if (it.currency == selectedWallet.currency) {
                        if (it.type == Enums.TransTypes.INCOME.value) {
                            totalAmount += it.amount!!.toDouble()
                            totalIncome += it.amount!!.toDouble()
                        } else {
                            totalAmount -= it.amount!!.toDouble()
                            totalExpense += it.amount!!.toDouble()
                        }
                    } else {
                        val convertedAmount = Extension.convertCurrency(
                            it.currency.toString(),
                            selectedWallet.currency.toString(),
                            it.amount!!.toDouble(),
                            context
                        )
                        if (it.type == Enums.TransTypes.INCOME.value) {
                            totalAmount += convertedAmount
                            totalIncome += convertedAmount
                        } else {
                            totalAmount -= convertedAmount
                            totalExpense += convertedAmount
                        }
                    }
                }
                nestedTransListTemp.add(
                    NestedTransaction(
                        nestedTransList =
                            (value as ArrayList<Transaction>).sortedWith(
                                compareBy { (it.date).toString() }).reversed()
                        ,
                        date = key,
                        totalAmount = String.format("%.2f", totalAmount).replace(",", ".")
                            .toDouble(),
                        totalIncome = String.format("%.2f", totalIncome).replace(",", ".")
                            .toDouble(),
                        totalExpense = String.format("%.2f", totalExpense).replace(",", ".")
                            .toDouble(),
                        totalAmountByCategoryMap = totalAmountByCategoryMap
                    )
                )
                allNestedTransList.value?.add(
                    NestedTransaction(
                        nestedTransList =
                            (value as ArrayList<Transaction>).sortedWith(
                                compareBy { (it.date).toString() }).reversed()
                        ,
                        date = key,
                        totalAmount = String.format("%.2f", totalAmount).replace(",", ".")
                            .toDouble(),
                        totalIncome = String.format("%.2f", totalIncome).replace(",", ".")
                            .toDouble(),
                        totalExpense = String.format("%.2f", totalExpense).replace(",", ".")
                            .toDouble(),
                        totalAmountByCategoryMap = totalAmountByCategoryMap
                    )
                )
            }
            nestedTransMapByMonth.put(key, nestedTransListTemp)
        }
        onSuccess()
    }

    private fun calculateTotalAmountByCategoryMap(
        transMapByCategory: Map<String?, List<Transaction>>,
        walletId: String? = ""
    ): HashMap<String?, HashMap<String, Double>> {
        val totalAmountByCategoryMap: HashMap<String?, HashMap<String, Double>> = HashMap()
        for ((key, value) in transMapByCategory) {
            var totalIncome = 0.0
            var totalExpense = 0.0
            value.forEach {
                val convertedAmount = Extension.convertCurrency(
                    it.currency.toString(),
                    selectedWallet.currency.toString(),
                    it.amount!!.toDouble(),
                    context
                )
                Log.d("firstCurrency", it.currency.toString())
                Log.d("secondCurrency", selectedWallet.currency.toString())

                if (walletId == "") {
                    // all wallet
                    if (it.type == Enums.TransTypes.INCOME.value) {
                        totalIncome += convertedAmount
                    } else {
                        totalExpense += convertedAmount
                    }
                } else {
                    // other wallet
                    if (it.walletID == walletId) {
                        if (it.type == Enums.TransTypes.INCOME.value) {
                            totalIncome += convertedAmount
                        } else {
                            totalExpense += convertedAmount
                        }
                    }
                }

            }
            val categoryMap: HashMap<String, Double> = HashMap()
            categoryMap.put(Enums.General.INCOME.value, totalIncome)
            categoryMap.put(Enums.General.EXPENSE.value, totalExpense)
            calculateTotalAmountByCategory(key.toString(), totalIncome, totalExpense)
            totalAmountByCategoryMap.put(key, categoryMap)
        }
        return totalAmountByCategoryMap
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getNestedTransList(monthYear: String, walletId: String? = "", onSuccess: () -> Unit) {
        try {
            nestedTransList.value?.clear()
            totalAmountByCategory = HashMap()
            if (nestedTransMapByMonth.containsKey(monthYear)) {
                nestedTransList.value?.addAll(nestedTransMapByMonth[monthYear] ?: ArrayList())
                if (walletId != "") {
                    val filteredNestedTransList = ArrayList<NestedTransaction>()
                    nestedTransList.value!!.forEach {
                        // to copy only value, not reference
                        val copyNestedTransaction = NestedTransaction(
                            it.nestedTransList,
                            it.totalAmount,
                            it.totalIncome,
                            it.totalExpense,
                            it.date,
                            it.isExpanded,
                            it.totalAmountByCategoryMap
                        )
                        var totalAmount = 0.0
                        var totalIncome = 0.0
                        var totalExpense = 0.0
                        val filteredTransList = copyNestedTransaction.nestedTransList.filter {
                            it.walletID == walletId
                        }
                        filteredTransList.forEach {
                            val convertedAmount = Extension.convertCurrency(
                                it.currency.toString(),
                                selectedWallet.currency.toString(),
                                it.amount!!.toDouble(),
                                context
                            )
                            if (it.type == Enums.TransTypes.INCOME.value) {
                                totalIncome += convertedAmount
                            } else {
                                totalExpense += convertedAmount
                            }
                        }

                        // map total amount by category
                        val transMapByCategory = copyNestedTransaction.nestedTransList.groupBy {
                            it.category.title
                        }


                        totalAmount = totalIncome - totalExpense
                        copyNestedTransaction.totalAmount =
                            String.format("%.2f", totalAmount).replace(",", ".").toDouble()
                        copyNestedTransaction.totalIncome =
                            String.format("%.2f", totalIncome).replace(",", ".").toDouble()
                        copyNestedTransaction.totalExpense =
                            String.format("%.2f", totalExpense).replace(",", ".").toDouble()
                        copyNestedTransaction.nestedTransList = ArrayList(filteredTransList)
                        copyNestedTransaction.totalAmountByCategoryMap =
                            calculateTotalAmountByCategoryMap(transMapByCategory, walletId)
                        if (copyNestedTransaction.nestedTransList.size != 0) {
                            filteredNestedTransList.add(copyNestedTransaction)
                        }
                    }
                    nestedTransList.postValue(CopyOnWriteArrayList(filteredNestedTransList))
                } else {
                    // all wallet
                    var index = 0
                    val filteredNestedTransaction: ArrayList<NestedTransaction> = ArrayList()
                    nestedTransList.value?.forEach {
                        val copyNestedTrans = ArrayList(it.nestedTransList)
                        if (isProUser.value == false && walletList.value!!.size > 2) {
                            copyNestedTrans.removeIf { trans ->
                                Preferences().getInstance().getChosenTwoWalletIds(context)?.contains(trans.walletID) == false
                            }
                        }

                        val copyNestedTransaction = NestedTransaction(
                            copyNestedTrans.toList(),
                            it.totalAmount,
                            it.totalIncome,
                            it.totalExpense,
                            it.date,
                            it.isExpanded,
                            it.totalAmountByCategoryMap
                        )
                        val transMapByCategory = copyNestedTransaction.nestedTransList.groupBy {
                            it.category.title
                        }
                        copyNestedTransaction.totalAmountByCategoryMap = calculateTotalAmountByCategoryMap(transMapByCategory, walletId)
                        filteredNestedTransaction.add(copyNestedTransaction)
                        index = index + 1
                        if (index == nestedTransList.value?.size) {

                            // to make in execute in main thread
                            GlobalScope.launch(Dispatchers.Main) {
                                nestedTransList.value = ((CopyOnWriteArrayList(filteredNestedTransaction)))
                                onSuccess()
                            }
                        }
                    }
                }
            } else {
                nestedTransList.postValue(CopyOnWriteArrayList())
                onSuccess()
            }
        }
        catch (exc: ConcurrentModificationException) {
            Thread.currentThread().interrupt()
        }

    }

    fun initTransListByTransType(
        transType: Int,
        onCompleted: () -> Unit
    ) {
        totalAmountByCategory = HashMap()
        val filteredNestedTransList = ArrayList<NestedTransaction>()
        nestedTransList.value?.forEach {
            val copyNestedTransaction = NestedTransaction(
                it.nestedTransList,
                it.totalAmount,
                it.totalIncome,
                it.totalExpense,
                it.date,
                it.isExpanded,
                it.totalAmountByCategoryMap
            )
            var totalAmount = 0.0
            var totalIncome = 0.0
            var totalExpense = 0.0
            val filteredTransList: List<Transaction>
            if (transType == Enums.TransTypes.INCOME.value) {
                filteredTransList = copyNestedTransaction.nestedTransList.filter {
                    (it.type == Enums.TransTypes.INCOME.value)
                }
                filteredTransList.forEach {
                    val convertedAmount = Extension.convertCurrency(
                        it.currency.toString(),
                        selectedWallet.currency.toString(),
                        it.amount!!.toDouble(),
                        context
                    )
                    if (it.type == Enums.TransTypes.INCOME.value) {
                        totalIncome += convertedAmount
                    } else {
                        totalExpense += convertedAmount
                    }
                }

                // map total amount by category
                val transMapByCategory = copyNestedTransaction.nestedTransList.groupBy {
                    it.category.title
                }


                totalAmount = totalIncome - totalExpense
                copyNestedTransaction.totalAmount =
                    String.format("%.2f", totalAmount).replace(",", ".").toDouble()
                copyNestedTransaction.totalIncome =
                    String.format("%.2f", totalIncome).replace(",", ".").toDouble()
                copyNestedTransaction.totalExpense =
                    String.format("%.2f", totalExpense).replace(",", ".").toDouble()
                copyNestedTransaction.nestedTransList = ArrayList(filteredTransList)
                val totalAmountByCategoryMap: HashMap<String?, HashMap<String, Double>> = HashMap()
                for ((key, value) in transMapByCategory) {
                    var totalIncome = 0.0
                    var totalExpense = 0.0
                    value.forEach {
                        val convertedAmount = Extension.convertCurrency(
                            it.currency.toString(),
                            selectedWallet.currency.toString(),
                            it.amount!!.toDouble(),
                            context
                        )
                        if (it.type == Enums.TransTypes.INCOME.value) {
                            totalIncome += convertedAmount
                        } else {
                            totalExpense += convertedAmount
                        }
                    }
                    val categoryMap: HashMap<String, Double> = HashMap()
                    categoryMap.put(Enums.General.INCOME.value, totalIncome)
                    categoryMap.put(Enums.General.EXPENSE.value, totalExpense)
                    calculateTotalAmountByCategory(key.toString(), totalIncome, totalExpense)
                    totalAmountByCategoryMap.put(key, categoryMap)
                }
                copyNestedTransaction.totalAmountByCategoryMap = totalAmountByCategoryMap


                if (copyNestedTransaction.nestedTransList.size != 0) {
                    filteredNestedTransList.add(copyNestedTransaction)
                }
            } else if (transType == Enums.TransTypes.EXPENSE.value) {
                filteredTransList = copyNestedTransaction.nestedTransList.filter {
                    (it.type == Enums.TransTypes.EXPENSE.value)
                }
                filteredTransList.forEach {
                    val convertedAmount = Extension.convertCurrency(
                        it.currency.toString(),
                        selectedWallet.currency.toString(),
                        it.amount!!.toDouble(),
                        context
                    )
                    if (it.type == Enums.TransTypes.INCOME.value) {
                        totalIncome += convertedAmount
                    } else {
                        totalExpense += convertedAmount
                    }
                }

                // map total amount by category
                val transMapByCategory = copyNestedTransaction.nestedTransList.groupBy {
                    it.category.title
                }


                totalAmount = totalIncome - totalExpense
                copyNestedTransaction.totalAmount =
                    String.format("%.2f", totalAmount).replace(",", ".").toDouble()
                copyNestedTransaction.totalIncome =
                    String.format("%.2f", totalIncome).replace(",", ".").toDouble()
                copyNestedTransaction.totalExpense =
                    String.format("%.2f", totalExpense).replace(",", ".").toDouble()
                copyNestedTransaction.nestedTransList = ArrayList(filteredTransList)
                val totalAmountByCategoryMap: HashMap<String?, HashMap<String, Double>> = HashMap()
                for ((key, value) in transMapByCategory) {
                    var totalIncome = 0.0
                    var totalExpense = 0.0
                    value.forEach {
                        val convertedAmount = Extension.convertCurrency(
                            it.currency.toString(),
                            selectedWallet.currency.toString(),
                            it.amount!!.toDouble(),
                            context
                        )
                        if (it.type == Enums.TransTypes.INCOME.value) {
                            totalIncome += convertedAmount
                        } else {
                            totalExpense += convertedAmount
                        }
                    }
                    val categoryMap: HashMap<String, Double> = HashMap()
                    categoryMap.put(Enums.General.INCOME.value, totalIncome)
                    categoryMap.put(Enums.General.EXPENSE.value, totalExpense)
                    calculateTotalAmountByCategory(key.toString(), totalIncome, totalExpense)
                    totalAmountByCategoryMap.put(key, categoryMap)
                }
                copyNestedTransaction.totalAmountByCategoryMap = totalAmountByCategoryMap


                if (copyNestedTransaction.nestedTransList.size != 0) {
                    filteredNestedTransList.add(copyNestedTransaction)
                }
            } else {
                filteredTransList = copyNestedTransaction.nestedTransList
                filteredTransList.forEach {
                    var convertedAmount: Double
                    try {
                        convertedAmount = Extension.convertCurrency(
                            it.currency.toString(),
                            selectedWallet.currency.toString(),
                            it.amount!!.toDouble(),
                            context
                        )
                    } catch (ex: Exception) {
                        convertedAmount = 0.0
                    }

                    if (it.type == Enums.TransTypes.INCOME.value) {
                        totalIncome += convertedAmount
                    } else {
                        totalExpense += convertedAmount
                    }
                }

                // map total amount by category
                val transMapByCategory = copyNestedTransaction.nestedTransList.groupBy {
                    it.category.title
                }


                totalAmount = totalIncome - totalExpense
                copyNestedTransaction.totalAmount =
                    String.format("%.2f", totalAmount).replace(",", ".").toDouble()
                copyNestedTransaction.totalIncome =
                    String.format("%.2f", totalIncome).replace(",", ".").toDouble()
                copyNestedTransaction.totalExpense =
                    String.format("%.2f", totalExpense).replace(",", ".").toDouble()
                copyNestedTransaction.nestedTransList = ArrayList(filteredTransList)
                val totalAmountByCategoryMap: HashMap<String?, HashMap<String, Double>> = HashMap()
                for ((key, value) in transMapByCategory) {
                    var totalIncome = 0.0
                    var totalExpense = 0.0
                    value.forEach {
                        val convertedAmount = Extension.convertCurrency(
                            it.currency.toString(),
                            selectedWallet.currency.toString(),
                            it.amount!!.toDouble(),
                            context
                        )
                        if (it.type == Enums.TransTypes.INCOME.value) {
                            totalIncome += convertedAmount
                        } else {
                            totalExpense += convertedAmount
                        }
                    }
                    val categoryMap: HashMap<String, Double> = HashMap()
                    categoryMap.put(Enums.General.INCOME.value, totalIncome)
                    categoryMap.put(Enums.General.EXPENSE.value, totalExpense)
                    calculateTotalAmountByCategory(key.toString(), totalIncome, totalExpense)
                    totalAmountByCategoryMap.put(key, categoryMap)
                }
                copyNestedTransaction.totalAmountByCategoryMap = totalAmountByCategoryMap


                if (copyNestedTransaction.nestedTransList.size != 0) {
                    filteredNestedTransList.add(copyNestedTransaction)
                }
            }
        }
        if (transType == Enums.TransTypes.INCOME.value) {
            incomeTransList.postValue(filteredNestedTransList)
        } else if (transType == Enums.TransTypes.EXPENSE.value) {
            expenseTransList.postValue(filteredNestedTransList)
        } else if (transType == Enums.TransTypes.ALL.value) {
            nestedTransList.postValue(CopyOnWriteArrayList(filteredNestedTransList))
        }
        onCompleted()

    }


    fun getNestedTranListByWallet(walletId: String) {
        if (nestedTransMapByWallet.containsKey(walletId)) {
            nestedTransList.value = CopyOnWriteArrayList(nestedTransMapByWallet[walletId]!!)
        }
        // else display trans for all wallet
    }


    fun initNestedTransList() {
        totalAmountByCategory = HashMap()
        nestedTransList.value = CopyOnWriteArrayList()

        // map total amount by category
        val transMapByCategory = transList.value!!.distinct().groupBy {
            it.category.title
        }
        val totalAmountByCategoryMap: HashMap<String?, HashMap<String, Double>> = HashMap()
        for ((key, value) in transMapByCategory) {
            var totalIncome = 0.0
            var totalExpense = 0.0
            value.forEach {
                val convertedAmount = Extension.convertCurrency(
                    it.currency.toString(),
                    selectedWallet.currency.toString(),
                    it.amount!!.toDouble(),
                    context
                )
                if (it.type == Enums.TransTypes.INCOME.value) {
                    totalIncome += convertedAmount
                }
                else {
                    totalExpense += convertedAmount
                }
            }
            val categoryMap: HashMap<String, Double> = HashMap()
            categoryMap.put(Enums.General.INCOME.value, totalIncome)
            categoryMap.put(Enums.General.EXPENSE.value, totalExpense)
            calculateTotalAmountByCategory(key.toString(), totalIncome, totalExpense)
            totalAmountByCategoryMap.put(key, categoryMap)
        }

        val transMap = transList.value!!.distinct().groupBy {
            SimpleDateFormat("dd MMM yyyy").format(it.date?.toDate())
        }.toSortedMap(reverseOrder())

        for ((key, value) in transMap) {
            var totalAmount: Double = 0.0
            var totalIncome = 0.0
            var totalExpense = 0.0

            value.forEach {

                if (it.type == Enums.TransTypes.INCOME.value) {
                    totalAmount += it.amount!!.toDouble()
                    totalIncome += it.amount!!.toDouble()
                } else {
                    totalAmount -= it.amount!!.toDouble()
                    totalExpense += it.amount!!.toDouble()
                }
            }



            nestedTransList.value!!.add(
                NestedTransaction(
                    nestedTransList = ArrayList(
                        (value as ArrayList<Transaction>).sortedWith(
                            compareBy { (it.date).toString() }).reversed()
                    ),
                    date = key,
                    totalAmount = String.format("%.2f", totalAmount).replace(",", ".").toDouble(),
                    totalIncome = String.format("%.2f", totalIncome).replace(",", ".").toDouble(),
                    totalExpense = String.format("%.2f", totalExpense).replace(",", ".").toDouble(),
                    totalAmountByCategoryMap = totalAmountByCategoryMap

                )
            )
        }
//        nestedTransListCopy.value = nestedTransList
    }

    fun initCategory(onSuccess: () -> Unit, onFailure: () -> Unit) {
        firestore.collection(Enums.DB.CATEGORIES_COLLECTION.value)
            .whereIn(
                Enums.DB.USER_ID_FIELD.value,
                categoryUserIdList
            )
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onFailure()
                }
                else {
                    try {
                        var counter = 0
                        value?.documents?.forEach {
                            categoriesList.add(it.toObject(Categories::class.java) as Categories)
                            if ((it.toObject(Categories::class.java) as Categories).userid == currentUser.uid) {
                                categories = it.toObject(Categories::class.java) as Categories
                                categories.documentId = it.id
                                val localIncomeModel =  ((it.toObject(Categories::class.java) as Categories).categories?.get("Income") as ArrayList<io.paraga.moneytrackerdev.models.Category>)
                                val localExpenseModel = ((it.toObject(Categories::class.java) as Categories).categories?.get("Expense") as ArrayList<Category>)
                                val localArchivedCategoryModel = (it.toObject(Categories::class.java) as Categories).categories?.get("Archive") ?: ArrayList()
                                localIncomeModel.forEach {
                                    if (it.initialName == "") {
                                        it.initialName = it.title + "-" + it.color + "-" + "income"
                                    }
                                }
                                localExpenseModel.forEach {
                                    if (it.initialName == "") {
                                        it.initialName = it.title + "-" + it.color + "-" + "expense"
                                    }
                                }

                                incomeModel.postValue(localIncomeModel)
                                expenseModel.postValue(localExpenseModel)
                                archivedCategoryModel.postValue(localArchivedCategoryModel)
                            }
                            counter += 1
                            if (counter == value.documents.size) {
                                onSuccess()
                            }
                        }
                    }
                    catch (exc: java.lang.IndexOutOfBoundsException) {
                        categories.categories?.set(Enums.DB.INCOME_FIELD.value, ArrayList(Constants().defaultIncomeList))
                        categories.categories?.set(Enums.DB.EXPENSE_FIELD.value, ArrayList(Constants().defaultExpenseList))
                        onSuccess()
                    }

                }
            }
    }

    fun resetCategory(onSuccess: () -> Unit, onFailure: () -> Unit) {
        val categoryModel: HashMap<String, Any> = HashMap()
        val categoriesMap: HashMap<String, Any> = HashMap()

        incomeModel.value = ArrayList(Constants().defaultIncomeList)
        expenseModel.value = ArrayList(Constants().defaultExpenseList)

        val categoryDocRef = firestore.collection(
            Enums.DB.CATEGORIES_COLLECTION.value
        ).document(categories.documentId.toString())

        categoriesMap[Enums.DB.EXPENSE_FIELD.value] = expenseModel.value!!
        categoriesMap[Enums.DB.INCOME_FIELD.value] = incomeModel.value!!
        categoriesMap[Enums.DB.ARCHIVE.value] = archivedCategoryModel.value!!
        categoryModel[Enums.DB.CATEGORIES_FIELD.value] =
            categoriesMap


        categoryDocRef.update(categoryModel).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    private fun calculateTotalAmountByCategory(
        key: String,
        totalIncome: Double, totalExpense: Double
    ) {
        if (totalAmountByCategory.containsKey(key)) {
            var income = totalAmountByCategory[key]?.get(Enums.General.INCOME.value) ?: 0.0
            var expense = totalAmountByCategory[key]?.get(Enums.General.EXPENSE.value) ?: 0.0
            income = income.plus(totalIncome)
            expense = expense.plus(totalExpense)
            totalAmountByCategory[key]?.put(Enums.General.INCOME.value, income)
            totalAmountByCategory[key]?.put(Enums.General.EXPENSE.value, expense)
        } else {
            val totalMap = HashMap<String, Double>()
            totalMap.put(Enums.General.INCOME.value, totalIncome)
            totalMap.put(Enums.General.EXPENSE.value, totalExpense)
            totalAmountByCategory.put(key, totalMap)
        }
    }

    fun initWalletList(onSuccess: () -> Unit) {
        fun _initWalletList() {
            firestore.collection(Enums.DB.WALLETS_COLLECTION.value)
                .where(
                    Filter.or(
                        Filter.equalTo(Enums.DB.USER_ID_FIELD.value, auth.currentUser?.uid),
                        Filter.equalTo(
                            "sharedWith.${auth.currentUser?.uid}",
                            Enums.InvitationType.ACCEPTED.value
                        )
                    )
                )
                .addSnapshotListener { value, error ->
                    if (error == null) {
                        walletList.value?.clear()
                        walletIdList = ArrayList()
                        categoryUserIdList = ArrayList()
                        categoryUserIdList.add(user?.userid.toString())
                        val walletListDocs = value?.documents
                        walletListDocs?.forEach { walletSnapshot ->
                            var balance = 0.0
                            transList.value!!.distinct().forEach {
                                if (it.walletID == walletSnapshot.id) {

                                    if (it.type == Enums.TransTypes.EXPENSE.value) {
                                        if (walletSnapshot.get("currency") != it.currency) {
                                            balance -= Extension.convertCurrency(
                                                it.currency.toString(),
                                                walletSnapshot.get("currency") as String,
                                                it.amount?.toString()?.replace(",", ".")?.toDouble()
                                                    ?: 0.0, context
                                            )
                                        } else {
                                            balance -= it.amount?.toString()?.replace(",", ".")
                                                ?.toDouble() ?: 0.0
                                        }
                                    } else {
                                        if (walletSnapshot.get("currency") != it.currency) {
                                            balance += Extension.convertCurrency(
                                                it.currency.toString(),
                                                walletSnapshot.get("currency") as String,
                                                it.amount?.toString()?.replace(",", ".")?.toDouble()
                                                    ?: 0.0, context
                                            )
                                        } else {
                                            balance += it.amount?.toString()?.replace(",", ".")
                                                ?.toDouble() ?: 0.0
                                        }
                                    }
                                }
                            }
                            totalBalance += balance

                            val wallet = walletSnapshot.toObject(Wallet::class.java) as Wallet
                            walletList.value?.add(
                                WalletTrans(
                                    walletId = walletSnapshot.id,
                                    wallet = wallet,
                                    balance = String.format("%.2f", balance).replace(",", ".")
                                        .toDouble(),
                                    isDefault = user?.defaultWallet == walletSnapshot.id
                                )
                            )
                            wallet.sharedWith?.forEach {
                                if (it.value == 1 && !categoryUserIdList.contains(it.key)) {
                                    categoryUserIdList.add(it.key)
                                }
                            }
                            if (!categoryUserIdList.contains(wallet.userid)) {
                                categoryUserIdList.add(wallet.userid.toString())
                            }
                            walletIdList.add(walletSnapshot.id)
                        }
                        val lastSelectedWalletId =
                            Preferences().getInstance().getSelectedWalletId(context)
                        var counter = 0
                        if (lastSelectedWalletId == "") {
                            selectedWallet = Wallet(
                                name = "All Wallet",
                                symbol = "globe",
                                currency = user?.defaultCurrency
                            )
                            selectedWalletId = ""
                        } else {
                            walletList.value?.forEach {
                                if (lastSelectedWalletId == it.walletId) {
                                    selectedWallet = it.wallet
                                    selectedWalletId = it.walletId
                                    counter += 1
                                    return@forEach
                                }
                            }
                        }
                        walletList.postValue(ArrayList(
                            walletList.value?.sortedWith(compareBy { it.isDefault })
                                ?.reversed()?.distinct() ?: ArrayList()
                        ))
                        onSuccess()
                    }
                }
        }
        if (user == null) {
            initUser {
                _initWalletList()
            }
        } else {
            _initWalletList()
        }
    }


    fun getWallet(
        walletId: String,
        onSuccess: (Wallet) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection(Enums.DB.WALLETS_COLLECTION.value).document(walletId).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        it.result.toObject(Wallet::class.java) as Wallet
                        onSuccess(it.result.toObject(Wallet::class.java) as Wallet)
                    } catch (ex: Exception) {
                        onFailure(context.getString(R.string.failed_to_fetch_transaction))
                    }
                }
            }
    }

    fun updateCategory(
        categories: HashMap<String, ArrayList<Category>>,
        onSuccess: () -> Unit
    ) {
        val categoryRef = firestore.collection(
            Enums.DB.CATEGORIES_COLLECTION.value
        ).whereEqualTo(
            Enums.DB.USER_ID_FIELD.value,
            user?.userid
        ).get().addOnCompleteListener {
            if (it.isSuccessful) {
                val result = it.result
                result.documents.forEach {
                    firestore.collection(Enums.DB.CATEGORIES_COLLECTION.value)
                        .document(it.id)
                        .update(
                            Enums.DB.CATEGORIES_FIELD.value,
                            categories
                        ).addOnCompleteListener {
                            if (it.isSuccessful) {
                                transListCopy.postValue(transList.value)
                                onSuccess()
                            }
                        }
                }
            }
        }
    }

    fun updateMainCurrency(
        currency: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val userRef = firestore.collection(Enums.DB.USERS_COLLECTION.value)
            .document(user?.userid.toString())
        userRef.update(
            Enums.DB.DEFAULT_CUR_FIELD.value, currency
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun initBudget(onSuccess: () -> Unit, onFailure: () -> Unit) {

        firestore.collection(Enums.DB.BUDGETS_COLLECTION.value)
            .whereEqualTo(
                Enums.DB.USER_ID_FIELD.value,
                auth.currentUser?.uid
            )
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onFailure()
                } else {
                    budgetList.value = ArrayList()
                    value?.documents?.forEach { budgetSnapshot ->
                        val budget: Budget = try {
                            budgetSnapshot.toObject(Budget::class.java) as Budget
                        } catch (ex: java.lang.Exception) {
                            Budget()
                        }
                        budget.documentId = budgetSnapshot.id
                        budgetList.value?.add(
                            budget
                        )
                    }
                    onSuccess()
                }
            }
    }

    fun updateUserToken(token: String) {
        user?.token = token
        firestore.collection(Enums.DB.USERS_COLLECTION.value)
            .document(user?.userid.toString())
            .set(user as User)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("token", "Updated successfully!")
                }
            }
    }

    fun addNotification(
        notification: Notification, onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val notifDocsRef = firestore.collection(Enums.DB.NOTIFICATION_COLLECTION.value)
            .document()
        notification.documentId = notifDocsRef.id
        notifDocsRef.set(notification)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }
    }

    fun getSharedUserList(userIdList: ArrayList<String>, onSuccess: (ArrayList<User>) -> Unit) {
        val userList = kotlin.collections.ArrayList<User>()
        firestore.collection(Enums.DB.USERS_COLLECTION.value)
            .whereIn(Enums.DB.USER_ID_FIELD.value, userIdList)
            .addSnapshotListener { value, error ->
                if (error == null) {
                    value?.documents?.forEach { userSnapshot ->
                        try {
                            val user = userSnapshot.toObject(User::class.java) as User
                            userList.add(user)
                        } catch (exc: Exception) {
                            Log.d("exc", exc.toString())
                        }
                    }
                    onSuccess(userList)
                }
            }
    }

    fun searchUserByEmail(email: String, onSuccess: (User) -> Unit) {
        firestore.collection(Enums.DB.USERS_COLLECTION.value)
            .whereEqualTo(Enums.DB.EMAIL_FIELD.value, email)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        val user = it.result.documents[0].toObject(User::class.java) as User
                        onSuccess(user)
                    } catch (exc: Exception) {
                        onSuccess(User())
                    }
                }
            }
    }

    fun updateShareUser(wallet: WalletTrans?, onSuccess: () -> Unit) {
        firestore.collection(Enums.DB.WALLETS_COLLECTION.value)
            .document(wallet?.walletId.toString())
            .update(Enums.DB.SHARED_WITH.value, wallet?.wallet?.sharedWith)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    walletList.value?.distinct()?.forEach {
                        if (it.walletId == wallet?.walletId) {
                            it.wallet = wallet.wallet
                            walletList.postValue(ArrayList(walletList.value!!.distinct()))
                            return@forEach
                        }
                    }
                    onSuccess()
                }
            }
    }

    fun getNotificationList(onSuccess: () -> Unit) {
        firestore.collection(Enums.DB.NOTIFICATION_COLLECTION.value)
            .whereEqualTo(Enums.DB.USER_ID_FIELD.value, user?.userid.toString())
            .addSnapshotListener { value, error ->
                if (error == null) {
                    notificationList.value?.clear()
                    value?.documents?.forEach {
                        try {
                            val notification = it.toObject(Notification::class.java) as Notification
                            notification.documentId = it.id
                            notificationList.value?.add(notification)
                        } catch (exc: Exception) {
                            Log.d("exc", exc.toString())
                        }
                    }
                    notificationList.value?.sortWith(compareBy<Notification> { it.createdTime }.reversed())
                    onSuccess()
                }
            }
    }

    fun getUser(userId: String, onSuccess: (User) -> Unit) {
        firestore.collection(Enums.DB.USERS_COLLECTION.value)
            .document(userId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    try {
                        onSuccess(it.result.toObject(User::class.java) as User)
                    } catch (exc: Exception) {
                        onSuccess(User())
                    }
                }
            }
    }

    fun getTransByWallet(walletId: String, onSuccess: (ArrayList<Transaction>) -> Unit) {
        firestore.collection(Enums.DB.TRANSACTIONS_COLLECTION.value)
            .whereEqualTo(Enums.DB.WALLET_ID_FIELD.value, walletId)
            .addSnapshotListener { value, error ->
                val transList: ArrayList<Transaction> = ArrayList()
                if (error == null) {
                    value?.documents?.forEach { snapshot ->
                        val trans = Transaction(
                            amount = (snapshot.get("amount") as Number).toDouble(),
                            date = snapshot.get("date") as Timestamp,
                            category = Category(
                                color = (snapshot.get("category") as HashMap<*, *>)["color"] as String,
                                image = (snapshot.get("category") as HashMap<*, *>)["image"] as String,
                                selectedCount = ((snapshot.get("category") as HashMap<*, *>)["selectedCount"] as Long).toInt(),
                                title = (snapshot.get("category") as HashMap<*, *>)["title"] as String
                            ),
                            currency = snapshot.get("currency") as String,
                            remark = snapshot.get("remark") as String,
                            type = (snapshot.get("type") as Long).toInt(),
                            walletID = snapshot.get("walletID") as String,
                            documentId = snapshot.id
                        )
                        transList.add(trans)

                    }
                    onSuccess(transList)
                }
            }
    }

    fun getWallet(walletId: String, onSuccess: (WalletTrans) -> Unit) {
        firestore.collection(Enums.DB.WALLETS_COLLECTION.value)
            .document(walletId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val wallet = it.result.toObject(Wallet::class.java) as Wallet
                    val walletTrans = WalletTrans()
                    walletTrans.wallet = wallet
                    walletTrans.walletId = it.result.id
                    onSuccess(walletTrans)
                }
            }
    }

    fun updateNotificationData(notification: Notification, onSuccess: () -> Unit) {
        firestore.collection(Enums.DB.NOTIFICATION_COLLECTION.value)
            .document(notification.documentId.toString())
            .update(Enums.DB.DATA_FIELD.value, notification.data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                }
            }
    }

    fun updateReadNotifStatus(notification: Notification) {
        firestore.collection(Enums.DB.NOTIFICATION_COLLECTION.value)
            .document(notification.documentId.toString())
            .update(Enums.DB.IS_READ.value, notification.isRead)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                }
            }
    }

    fun clearAllNotification(onSuccess: () -> Unit) {
        val batch = firestore.batch()
        firestore.collection(Enums.DB.NOTIFICATION_COLLECTION.value)
            .whereEqualTo(Enums.DB.USER_ID_FIELD.value, user?.userid)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.documents.forEach {
                        batch.delete(it.reference)
                    }
                    batch.commit().addOnCompleteListener {
                        if (it.isSuccessful) {
                            notificationList.value?.clear()
                            onSuccess()
                        }
                    }
                }
            }
    }

    fun deleteNotification(documentId: String,
    onSuccess: () -> Unit) {
        firestore.collection(Enums.DB.NOTIFICATION_COLLECTION.value)
            .document(documentId)
            .delete()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                }
            }
    }


}

