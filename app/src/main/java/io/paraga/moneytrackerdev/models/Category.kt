package io.paraga.moneytrackerdev.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName


data class Category(
    @PropertyName("color")
    var color: String? = "",
    @PropertyName("image")
    var image: String? = "ic-general",
    @PropertyName("selectedCount")
    var selectedCount: Int? = 0,
    @PropertyName("title")
    var title: String? = "",
    var initialName: String? = ""
): java.io.Serializable


data class Expense(
    val Expense: ArrayList<Category>
)

data class Income(
    val income: ArrayList<Category>
)


data class Categories(
    @PropertyName("categories")
    val categories: HashMap<String, ArrayList<Category>>? = HashMap(),
    @Exclude
    var documentId: String? = "",
    @get:PropertyName("Archive")
    @set:PropertyName("Archive")
    var archive: ArrayList<Category> = ArrayList(),
    var userid: String? = ""
)

data class CategoriesChild(
    val categoriesChild: HashMap<String, Array<Category>>,

    )